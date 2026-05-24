🎉 EventPass SaaS — Fluxo Funcional Completo

👥 Atores
AtorDescriçãoCliente/AdminSe cadastra no site, cria eventos, gerencia tudoDispositivo PortariaApp Android vinculado ao painel via QR CodeConvidadoRecebe QR Code e apresenta na entradaSistemaAutomações, cobrança, notificações

📦 MÓDULO 1 — Landing Page e Cadastro Público
[Visitante acessa eventpass.com.br]
        ↓
[Landing Page pública]
    ├── Apresentação do produto
    ├── Planos e preços
    ├── Botão: "Começar grátis"
    └── Login para quem já tem conta
        ↓
[Clica em "Começar grátis" → Formulário de cadastro]
    ├── Nome completo
    ├── CPF (validado — dígitos verificadores)
    ├── WhatsApp (com DDD)
    ├── E-mail
    ├── Senha (mín. 8 chars, 1 número, 1 especial)
    └── Aceite dos Termos de Uso
        ↓
[Sistema envia código OTP de 6 dígitos via WhatsApp]
        ↓
[Usuário digita o código → conta verificada]
        ↓
[Trial de 14 dias ativado automaticamente]
    └── Acesso completo ao plano Pro durante o trial
        ↓
[Redireciona para o Dashboard principal]

📦 MÓDULO 2 — Planos e Monetização
[Trial expira em 14 dias]
        ↓
[Sistema notifica por WhatsApp + e-mail nos dias]
    ├── D-3 dias para expirar
    ├── D-1 dia para expirar
    └── D-0 conta suspensa (eventos bloqueados)
        ↓
[Usuário acessa tela de planos]

┌─────────────────────────────────────────────────────┐
│                    PLANOS DISPONÍVEIS                │
├────────────┬────────────┬────────────┬──────────────┤
│  Starter   │   Mensal   │   Anual    │  Por Evento  │
│  Gratuito  │  R$ 59/mês │ R$ 499/ano │  R$ 29/evt   │
│  1 evento  │ Ilimitado  │ Ilimitado  │ Pague ao usar │
│  até 50    │ até 500    │ até 2.000  │ até 300      │
│  convidados│ convidados │ convidados │ convidados   │
│  1 device  │ 3 devices  │ 10 devices │ 1 device     │
└────────────┴────────────┴────────────┴──────────────┘
        ↓
[Usuário seleciona plano]
        ↓
[Pagamento via Mercado Pago]
    ├── Cartão de crédito (recorrente)
    ├── Pix (manual — renova ao pagar)
    └── Boleto
        ↓
[Plano ativado → limites atualizados no sistema]
        ↓
[Renovação automática mensal/anual]
    └── Falha → 3 dias de carência → suspend → 7 dias → dados deletados

📦 MÓDULO 3 — Dashboard do Admin
[Admin logado acessa o Dashboard]
        ↓
[Visão geral]
    ├── Plano atual + dias restantes / validade
    ├── Eventos criados / limite do plano
    ├── Dispositivos vinculados / limite do plano
    ├── Próximo evento (data + status)
    └── Atalhos rápidos:
        ├── [+ Novo Evento]
        ├── [+ Convidados]
        └── [Gerenciar Dispositivos]

📦 MÓDULO 4 — Gestão de Eventos
[Admin clica em "+ Novo Evento"]
        ↓
[Preenche dados do evento]
    ├── Nome do evento
    ├── Data, horário de início e fim
    ├── Local / endereço
    ├── Banner/foto (upload)
    ├── Capacidade máxima de convidados
    ├── Permite acompanhante? (sim → quantos por convidado)
    ├── Permite reentrada? (sim/não)
    └── Observações (dress code, estacionamento, etc.)
        ↓
[Evento criado com status: Rascunho]
        ↓
[Status do evento]
    ├── 📝 Rascunho     → editável, nenhum convite enviado
    ├── ✅ Publicado    → aceitando convidados
    ├── 🟢 Em andamento → portaria liberada (dia do evento)
    ├── 🔴 Encerrado    → leitura bloqueada, relatório disponível
    └── ❌ Cancelado    → notifica convidados automaticamente

📦 MÓDULO 5 — Cadastro de Convidados
[Admin acessa o evento → aba "Convidados"]
        ↓
OPÇÃO A — Cadastro manual
    [+ Adicionar convidado]
    ├── Nome completo
    ├── CPF ou RG
    ├── Telefone (WhatsApp)
    ├── E-mail (opcional)
    ├── Setor / Mesa / Grupo (opcional)
    └── Qtd de acompanhantes permitidos

OPÇÃO B — Importação por planilha
    [Upload Excel/CSV]
    ├── Sistema valida colunas e dados
    ├── Exibe prévia com erros destacados
    ├── Admin corrige ou ignora erros
    └── Confirma importação em lote

EM AMBOS OS CASOS
    ↓
[Para cada convidado o sistema gera]
    ├── UUID único (chave do QR Code)
    ├── QR Code em imagem PNG
    └── Status: Pendente / Convite enviado / Confirmado / Entrou

📦 MÓDULO 6 — Envio dos Convites
[Admin seleciona convidados → "Enviar convites"]
        ↓
[Escolhe canal]
    ├── WhatsApp — mensagem com QR Code + detalhes do evento
    ├── E-mail   — layout com banner + QR Code em PDF
    └── Link     — admin copia e envia manualmente
        ↓
[Sistema enfileira envios (Redis Queue)]
        ↓
[Disparo assíncrono — não trava o painel]
        ↓
[Painel exibe progresso em tempo real]
    ├── ✅ Enviados: 87
    ├── ❌ Falhas:    3  (telefone inválido → alerta)
    └── ⏳ Na fila:  10
        ↓
[Convidado recebe no WhatsApp]
    "Olá Maria! Você está convidada para [Nome do Evento]
     📅 Data: 15/06 às 20h
     📍 Local: Espaço Vila Nova
     Apresente o QR Code abaixo na entrada 👇
     [imagem do QR Code]"

📦 MÓDULO 7 — Gerenciamento de Dispositivos (Android)
[Admin acessa "Meus Dispositivos" no painel]
        ↓
[Clica em "+ Adicionar Dispositivo"]
        ↓
[Preenche]
    ├── Nome/descrição (ex: "Portaria Principal", "Entrada VIP")
    └── [Gerar QR Code de Pareamento]
        ↓
[Sistema gera QR Code de pareamento único com]
    ├── Token seguro de vinculação (JWT de uso único)
    ├── URL da API do painel
    └── ID do dispositivo
        ↓
[Admin exibe QR Code na tela do computador]
        ↓
[No celular/tablet Android]
    ├── Abre o app EventPass Portaria
    ├── Clica em "Vincular ao painel"
    └── Aponta câmera para o QR Code do computador
        ↓
[App lê o QR Code e envia token para a API]
        ↓
[API valida o token → vincula device ao admin]
    ├── Token expira após 1 uso
    ├── Dispositivo registrado com ID único
    └── App mostra: "✅ Vinculado ao painel de [Nome do Admin]"
        ↓
[No painel: dispositivo aparece como "Online 🟢"]
    ├── Nome do dispositivo
    ├── Último acesso
    ├── Versão do app
    └── Evento associado no momento
        ↓
[Admin pode]
    ├── Renomear o dispositivo
    ├── Desvincular (revogar acesso)
    └── Associar a um evento ativo ← próximo módulo

📦 MÓDULO 8 — Associação Dispositivo ↔ Evento
[Admin acessa o evento → aba "Dispositivos"]
        ↓
[Lista de dispositivos vinculados à conta]
    ├── 📱 Portaria Principal  [Associar]
    ├── 📱 Entrada VIP         [Associar]
    └── 📱 Tablet Recepção     [Associar]
        ↓
[Admin clica em "Associar" no dispositivo desejado]
        ↓
[Dispositivo fica autorizado a ler QR Codes deste evento]
        ↓
[No app Android — atualização automática]
    └── Tela exibe: "Evento ativo: [Nome do Evento] 🟢"
        ↓
[Admin pode]
    ├── Associar múltiplos devices ao mesmo evento
    ├── Trocar o evento associado a qualquer momento
    └── Desassociar (device fica ocioso, não lê nada)

REGRAS DE NEGÓCIO
    ├── Device só lê QR Codes do evento associado
    ├── Sem associação → app mostra "Nenhum evento ativo"
    └── Plano limita quantidade de devices simultâneos

📦 MÓDULO 9 — Operação da Portaria (App Android)
[Porteiro abre o app → já logado via device token]
        ↓
[Tela principal exibe]
    ├── Nome do evento associado
    ├── Total de entradas até agora
    ├── Capacidade restante
    └── Botão: [Iniciar Leitura]
        ↓
[Câmera ativada para leitura contínua]
        ↓
[Convidado apresenta QR Code]
        ↓
[App envia UUID para API → validação em tempo real]

    ✅ ENTRADA LIBERADA — tela VERDE
    ─────────────────────────────────
    ├── Foto do convidado (se cadastrada)
    ├── Nome completo
    ├── Setor / Mesa
    ├── Acompanhantes: [0] [1] [2] [3] (porteiro toca)
    └── Som de confirmação + vibração

    ⚠️ JÁ ENTROU — tela AMARELA
    ─────────────────────────────────
    ├── Horário da entrada anterior
    └── Botão: [Permitir reentrada] / [Negar]

    ❌ QR INVÁLIDO — tela VERMELHA
    ─────────────────────────────────
    ├── Motivo (não encontrado / evento errado / expirado)
    └── Botão: [Buscar por nome]

    🔍 BUSCA MANUAL por nome
    ─────────────────────────────────
    └── Porteiro digita nome → lista filtrada → confirma entrada
        ↓
[Cada entrada registrada com]
    ├── Horário exato
    ├── ID do dispositivo que registrou
    └── Qtd de acompanhantes

📦 MÓDULO 10 — Painel ao Vivo e Relatórios
[Admin acompanha o evento em tempo real]
    ├── Entradas por minuto (gráfico ao vivo)
    ├── % de ocupação
    ├── Lista de entradas com horário
    ├── Convidados pendentes (ainda não chegaram)
    └── Alerta de capacidade máxima

PÓS-EVENTO
    ├── Total presentes / ausentes / acompanhantes
    ├── Horário de pico
    ├── Relatório por grupo/setor
    └── Exportar PDF ou Excel

Agora o diagrama da arquitetura atualizada:Criou um arquivo, leu um arquivoCriou um arquivo, leu um arquivoArquitetura completa! Os pontos novos em destaque:
🔗 Pareamento de Dispositivo — o fluxo dos 7 passos está detalhado dentro da camada de interfaces, mostrando exatamente como o QR Code do painel vincula o app Android com segurança (JWT de uso único, expira em 10 min, trocado por Device Token permanente).
🔑 Multi-tenant — cada Admin tem seus dados isolados. O Device Token é separado do JWT do usuário, o app da portaria funciona de forma independente de sessão.

Próximo passo — por onde quer começar a implementar?
OpçãoEntrega🗃️ ERD do bancoTodas as tabelas: users, tenants, plans, events, guests, devices, entries⚙️ Spring BootEstrutura de pacotes, dependências Maven, configuração multi-tenant📱 App AndroidEstrutura Kotlin + CameraX + fluxo de pareamento🖥️ Painel WebEstrutura React + telas de cadastro e gerenciamento