package br.com.eventpass.application.service;

import br.com.eventpass.application.dto.request.CadastroRequest;
import br.com.eventpass.application.dto.request.LoginRequest;
import br.com.eventpass.application.dto.request.PlanoRequest;
import br.com.eventpass.application.dto.request.VerificarOtpRequest;
import br.com.eventpass.application.dto.response.AuthResponse;
import br.com.eventpass.domain.entity.Assinatura;
import br.com.eventpass.domain.entity.Cliente;
import br.com.eventpass.domain.entity.CodigoOtp;
import br.com.eventpass.domain.entity.Plano;
import br.com.eventpass.domain.enums.FinalidadeOtp;
import br.com.eventpass.domain.enums.PlanoSlug;
import br.com.eventpass.domain.enums.StatusAssinatura;
import br.com.eventpass.domain.enums.StatusCliente;
import br.com.eventpass.domain.repository.AssinaturaRepository;
import br.com.eventpass.domain.repository.ClienteRepository;
import br.com.eventpass.domain.repository.CodigoOtpRepository;
import br.com.eventpass.domain.repository.PlanoRepository;
import br.com.eventpass.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.RedisTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClienteRepository clienteRepository;
    private final CodigoOtpRepository codigoOtpRepository;
    private final AssinaturaRepository assinaturaRepository;
    private final PlanoRepository planoRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final NotificacaoService notificacaoService;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String PREFIXO_CADASTRO = "cadastro_pendente:";

    @Value("${otp.expiracao-minutos:5}")
    private int otpExpiracaoMinutos;

    @Value("${otp.max-tentativas:5}")
    private int maxTentativas;

    @Value("${trial.dias:14}")
    private int trialDias;
    

    // ── Passo 1: Cadastro inicial ─────────────────────────────────────────────
    @Transactional
    public void iniciarCadastro(CadastroRequest req) {
        if (clienteRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }
        if (clienteRepository.existsByCpf(req.cpf())) {
            throw new IllegalArgumentException("CPF já cadastrado.");
        }

        // Persiste dados do formulário no Redis — expiram junto com o OTP
        try {
            String json = objectMapper.writeValueAsString(req);
            redisTemplate.opsForValue().set(
                    PREFIXO_CADASTRO + req.telefoneWhatsapp(),
                    json,
                    Duration.ofMinutes(otpExpiracaoMinutos)
            );
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Erro interno ao processar cadastro.", e);
        }

        String codigo = gerarCodigo();
        salvarOtp(req.telefoneWhatsapp(), codigo, FinalidadeOtp.CADASTRO, null);
        notificacaoService.enviarOtpWhatsApp(req.telefoneWhatsapp(), codigo);
        log.info("OTP de cadastro enviado para {}", req.telefoneWhatsapp());
    }

    // 3. verificarOtp — método novo com 1 parâmetro (chamado pelo controller)
    @Transactional
    public AuthResponse verificarOtp(VerificarOtpRequest req) {
        // Recupera dados do formulário salvos no passo 1
        String chave = PREFIXO_CADASTRO + req.telefone();
        String json  = redisTemplate.opsForValue().get(chave);
        CadastroRequest dadosCadastro;
        Assinatura assinatura = null;
        Plano plano = null;
        Cliente clienteCadastrado = null;
        LocalDateTime dataHoraAtual = null;

        if (json == null) {
            throw new IllegalStateException("Sessão expirada. Inicie o cadastro novamente.");
        }


        try {
            dadosCadastro = objectMapper.readValue(json, CadastroRequest.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Erro interno ao recuperar dados de cadastro.", e);
        }

        // Delega para o método existente que já tem toda a lógica
        AuthResponse response = verificarOtpECriarConta(req, dadosCadastro);
        dataHoraAtual = LocalDateTime.now();
        clienteCadastrado = clienteRepository.findByCpf( dadosCadastro.cpf() ).orElseThrow(()->new IllegalArgumentException("Cliente recém criado não encontrado para CPF"+dadosCadastro.cpf()));
        plano = planoRepository.findBySlug( PlanoSlug.STARTER.getSlug() ).orElseThrow(()->new IllegalArgumentException("Plano não encontrado para pesquisa por slug"+PlanoSlug.POR_EVENTO));

        //Cadastrar a assinatura
        assinatura = Assinatura.builder()
                .cliente(clienteCadastrado)
                .plano(plano)
                .status(StatusAssinatura.TRIAL)
                .iniciaEm(dataHoraAtual)
                .expiraEm(dataHoraAtual.plusDays(14))
                .renovaEm(dataHoraAtual.plusDays(13))
                .mpAssinaturaId("trial mercado pago")
                .criadoEm(dataHoraAtual)
                .build();

        assinaturaRepository.save( assinatura );

        // Limpa Redis — dados de cadastro não são mais necessários
        redisTemplate.delete(chave);

        return response;
    }

    // ── Passo 2: Verificar OTP e criar conta ─────────────────────────────────
    @Transactional
    public AuthResponse verificarOtpECriarConta(VerificarOtpRequest req, CadastroRequest dadosCadastro) {
        CodigoOtp otp = buscarOtpValido(dadosCadastro.telefoneWhatsapp(), FinalidadeOtp.CADASTRO);
        validarCodigo(otp, req.codigo());

        Cliente cliente = Cliente.builder()
                .slug(gerarSlug(dadosCadastro.nome()))
                .nome(dadosCadastro.nome())
                .cpf(dadosCadastro.cpf())
                .email(dadosCadastro.email())
                .telefoneWhatsapp(dadosCadastro.telefoneWhatsapp())
                .senhaHash(passwordEncoder.encode(dadosCadastro.senha()))
                .telefoneVerificado(true)
                .status(StatusCliente.ATIVO)
                .trialExpiraEm(LocalDateTime.now().plusDays(trialDias))
                .build();

        clienteRepository.save(cliente);
        otp.marcarComoUsado();
        codigoOtpRepository.save(otp);

        String token = jwtService.gerarTokenUsuario(cliente.getId(), cliente.getEmail());
        return new AuthResponse(token, cliente.getNome(), cliente.getEmail(), true);
    }

    // ── Login ─────────────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        Cliente cliente = clienteRepository.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas."));

        if (!passwordEncoder.matches(req.senha(), cliente.getSenhaHash())) {
            throw new IllegalArgumentException("Credenciais inválidas.");
        }
        if (cliente.getStatus() == StatusCliente.SUSPENSO) {
            throw new IllegalStateException("Conta suspensa. Verifique seu plano.");
        }
        if (cliente.getStatus() == StatusCliente.CANCELADO) {
            throw new IllegalStateException("Conta cancelada.");
        }

        String token = jwtService.gerarTokenUsuario(cliente.getId(), cliente.getEmail());
        boolean emTrial = cliente.estaEmTrial();
        return new AuthResponse(token, cliente.getNome(), cliente.getEmail(), emTrial);
    }

    // ── Helpers privados ──────────────────────────────────────────────────────
    private CodigoOtp buscarOtpValido(String telefone, FinalidadeOtp finalidade) {
        return codigoOtpRepository
            .findTopByTelefoneAndFinalidadeAndUsadoEmIsNullOrderByCriadoEmDesc(telefone, finalidade)
            .filter(CodigoOtp::estaValido)
            .orElseThrow(() -> new IllegalArgumentException("Código expirado ou não encontrado."));
    }

    private void validarCodigo(CodigoOtp otp, String codigoInformado) {
        if (otp.atingiuMaxTentativas(maxTentativas)) {
            throw new IllegalStateException("Número máximo de tentativas atingido.");
        }
        if (!passwordEncoder.matches(codigoInformado, otp.getCodigo())) {
            otp.incrementarTentativas();
            codigoOtpRepository.save(otp);
            throw new IllegalArgumentException("Código inválido.");
        }
    }

    private void salvarOtp(String telefone, String codigo, FinalidadeOtp finalidade, Cliente cliente) {
        int qtd = 0;
        if( cliente != null){
            CodigoOtp codigoOtp = codigoOtpRepository.findByCliente(cliente).orElse(null);

            if( codigoOtp != null ){
                qtd = codigoOtp.getTentativas().intValue();
                qtd++;
            }
        }
        
        CodigoOtp otp = CodigoOtp.builder()
                .telefone(telefone)
                .tentativas( qtd )
                .codigo(passwordEncoder.encode(codigo))
                .finalidade(finalidade)
                .cliente(cliente)
                .expiraEm(LocalDateTime.now().plusMinutes(otpExpiracaoMinutos))
                .build();
        codigoOtpRepository.save(otp);
    }

    private String gerarCodigo() {
        Random random = new SecureRandom();
        return String.format("%06d", random.nextInt(1_000_000));
    }

    private String gerarSlug(String nome) {
        String base = nome.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-")
                .substring(0, Math.min(nome.length(), 50));
        String sufixo = String.valueOf(System.currentTimeMillis()).substring(8);
        return base + "-" + sufixo;
    }
}
