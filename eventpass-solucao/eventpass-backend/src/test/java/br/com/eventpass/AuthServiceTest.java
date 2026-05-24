package br.com.eventpass.application.service;

import br.com.eventpass.application.dto.request.LoginRequest;
import br.com.eventpass.application.dto.response.AuthResponse;
import br.com.eventpass.domain.entity.Cliente;
import br.com.eventpass.domain.enums.StatusCliente;
import br.com.eventpass.domain.repository.ClienteRepository;
import br.com.eventpass.domain.repository.CodigoOtpRepository;
import br.com.eventpass.infrastructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService — testes unitários")
class AuthServiceTest {

    @Mock private ClienteRepository clienteRepository;
    @Mock private CodigoOtpRepository codigoOtpRepository;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private NotificacaoService notificacaoService;

    @InjectMocks
    private AuthService authService;

    private Cliente clienteAtivo;

    @BeforeEach
    void setUp() {
        clienteAtivo = Cliente.builder()
                .id(1L)
                .nome("João Silva")
                .email("joao@email.com")
                .senhaHash("$2a$12$hash")
                .status(StatusCliente.ATIVO)
                .trialExpiraEm(LocalDateTime.now().plusDays(10))
                .build();
    }

    @Test
    @DisplayName("login com credenciais válidas retorna token")
    void login_credenciaisValidas_retornaToken() {
        when(clienteRepository.findByEmail("joao@email.com"))
                .thenReturn(Optional.of(clienteAtivo));
        when(passwordEncoder.matches("senha123", "$2a$12$hash"))
                .thenReturn(true);
        when(jwtService.gerarTokenUsuario(1L, "joao@email.com"))
                .thenReturn("jwt-token-mock");

        AuthResponse resp = authService.login(new LoginRequest("joao@email.com", "senha123"));

        assertThat(resp.token()).isEqualTo("jwt-token-mock");
        assertThat(resp.emTrial()).isTrue();
        assertThat(resp.nome()).isEqualTo("João Silva");
    }

    @Test
    @DisplayName("login com senha errada lança exceção")
    void login_senhaErrada_lancaExcecao() {
        when(clienteRepository.findByEmail("joao@email.com"))
                .thenReturn(Optional.of(clienteAtivo));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(
                new LoginRequest("joao@email.com", "senhaErrada")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Credenciais inválidas");
    }

    @Test
    @DisplayName("login com conta suspensa lança exceção")
    void login_contaSuspensa_lancaExcecao() {
        clienteAtivo.setStatus(StatusCliente.SUSPENSO);
        when(clienteRepository.findByEmail("joao@email.com"))
                .thenReturn(Optional.of(clienteAtivo));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        assertThatThrownBy(() -> authService.login(
                new LoginRequest("joao@email.com", "senha123")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("suspensa");
    }

    @Test
    @DisplayName("login com e-mail inexistente lança exceção")
    void login_emailNaoCadastrado_lancaExcecao() {
        when(clienteRepository.findByEmail("naoexiste@email.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(
                new LoginRequest("naoexiste@email.com", "qualquer")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
