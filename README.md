# Software Utilizado
* Jdk 26
* docker compose
* Intelij(backend Spring Boot, Mysql, Redis, RabbitMQ etc e web react vite, tailcss, shadcn etc 
* Android Studio(kotlin etc)
---------
# Projetos
* Projeto Android: pasta eventpass-android
* Projeto Backend e Web: pasta eventpass-solucao

# Execução
* Com o docker compose instalado e executando abrir o bash / cmd entrar na pasta( cd eventpass-solucao) e executar:
* | docker-compose up --build

* Informações sobre configuração, user e senha de banco de dados e dos demais componentes da aplicação estão no arquivo: **docker-compose.yml** na raíz eventpass-solucao


# 🎉 EventPass SaaS — Fluxo Funcional Completo

## 👥 Atores do Sistema

| Ator | Descrição |
| :--- | :--- |
| **Cliente / Admin** | Se cadastra no site, cria eventos, gerencia convidados, dispositivos e relatórios. |
| **Dispositivo Portaria** | Aplicativo Android vinculado ao painel web via QR Code para validação de ingressos. |
| **Convidado** | Recebe o QR Code exclusivo e o apresenta na entrada do evento. |
| **Sistema** | Executa automações, processamento de cobranças, filas de disparos e notificações. |

---

## 📦 MÓDULO 1 — Landing Page e Cadastro Público

1. **Acesso Inicial**
   * Visitante acessa `eventpass.com.br`
2. **Landing Page Pública**
   * Apresentação do produto e propostas de valor
   * Tabela de planos e preços
   * Botão de conversão: *"Começar grátis"*
   * Link de login para usuários já cadastrados
3. **Formulário de Cadastro (Apenas novos usuários)**
   * Nome completo
   * CPF (com validação em tempo real dos dígitos verificadores)
   * WhatsApp (com DDD)
   * E-mail único
   * Senha segura (mínimo de 8 caracteres, 1 número e 1 caractere especial)
   * Checkbox de aceite dos Termos de Uso e Políticas de Privacidade
4. **Verificação de Segurança (Anti-fraude)**
   * Sistema envia automaticamente um código OTP de 6 dígitos via WhatsApp.
   * Usuário digita o código no painel $\rightarrow$ Conta verificada.
5. **Ativação e Redirecionamento**
   * Período de teste (*Trial*) de 14 dias ativado de forma automática.
   * Concessão de acesso completo ao plano *Pro* durante o período de testes.
   * Redirecionamento imediato para o Dashboard principal.

---

## 📦 MÓDULO 2 — Planos e Monetização

### 📅 Régua de Cobrança (Fim do Trial / Renovação)
Quando o trial de 14 dias expira, o sistema inicia os disparos automáticos via WhatsApp e E-mail:
* **D-3 dias:** Alerta de proximidade do vencimento.
* **D-1 dia:** Último aviso antes do bloqueio.
* **D-0 dia:** Conta suspensa e eventos bloqueados até a regularização do pagamento.

### 💳 Planos Disponíveis

| Benefícios / Regras | Starter | Mensal | Anual | Por Evento |
| :--- | :--- | :--- | :--- | :--- |
| **Preço** | Gratuito | R$ 59 / mês | R$ 499 / ano | R$ 29 / evento |
| **Eventos** | 1 evento ativo | Ilimitado | Ilimitado | Pague ao usar |
| **Convidados** | Até 50 | Até 500 / evento | Até 2.000 / evento | Até 300 / evento |
| **Dispositivos** | 1 dispositivo | 3 dispositivos | 10 dispositivos | 1 dispositivo |

### 🔄 Fluxo de Pagamento e Retenção
1. Usuário seleciona o plano desejado na tela de checkout.
2. Processamento do pagamento via gateway **Mercado Pago**:
   * **Cartão de Crédito:** Cobrança recorrente automática.
   * **Pix:** Checkout manual (renovação do plano após a confirmação do pagamento).
   * **Boleto:** Liberação após compensação bancária.
3. Plano ativado $\rightarrow$ Limites e permissões atualizados instantaneamente no banco de dados.
4. **Política de Inadimplência:** Falha na cobrança $\rightarrow$ 3 dias de carência $\rightarrow$ Suspensão da conta $\rightarrow$ Após 7 dias sem pagamento, exclusão definitiva dos dados (*GDPR/LGPD compliant*).

---

## 📦 MÓDULO 3 — Dashboard do Admin

Ao autenticar-se na plataforma, o Admin tem acesso a uma visão consolidada do ecossistema:

* **Métricas de Infraestrutura e Conta:**
  * Plano ativo, status financeiro e dias restantes para renovação/expiração.
  * Contador de eventos criados vs. limite total do plano contratado.
  * Contador de dispositivos vinculados vs. limite disponível no plano.
* **Visão Operacional:**
  * Dados do próximo evento agendado (Data, horário e status atual).
* **Atalhos de Ação Rápida:**
  * `[+ Novo Evento]`
  * `[+ Adicionar Convidados]`
  * `[Gerenciar Dispositivos]`

---

## 📦 MÓDULO 4 — Gestão de Eventos

```
[Admin] ──> Clica em "+ Novo Evento" ──> Preenche Formulário ──> Status: Rascunho
```

### Dados Obrigatórios e Opcionais do Evento
* Nome do Evento
* Data, horário de início e término
* Endereço completo / Localização
* Upload de imagem para Banner/Identidade Visual
* Capacidade nominal máxima de convidados
* Regra de Acompanhantes (Permite acompanhante? Se sim, limite numérico por convidado)
* Regra de Reentrada (Permite reentrada no local? Sim/Não)
* Notas informativas adicionais (Dress code, orientações sobre estacionamento, etc.)

### 🔄 Ciclo de Vida do Evento (Status)

> 📝 **Rascunho**
> Totalmente editável. Nenhum convite foi gerado ou disparado para os convidados.

> ✅ **Publicado**
> Evento configurado com sucesso e pronto para receber a lista de convidados.

> 🟢 **Em andamento**
> Dia do evento. Liberação automática da comunicação com as portarias para validação.

> 🔴 **Encerrado**
> Leitura de QR Codes bloqueada no app. Relatórios finais e consolidação de dados disponíveis para exportação.

> ❌ **Cancelado**
> Interrompe o fluxo e dispara notificações de cancelamento automáticas para toda a lista de convidados.

---

## 📦 MÓDULO 5 — Cadastro de Convidados

Dentro da aba "Convidados" de um evento específico, o Admin pode popular a lista através de dois caminhos:

### 📥 Métodos de Entrada de Dados

#### OPÇÃO A — Cadastro Individual e Manual
* Nome completo do convidado
* Documento de identificação (CPF ou RG)
* Telefone celular (WhatsApp com DDD)
* E-mail (Opcional)
* Setorização (Mesa, área, camarote ou grupo — Opcional)
* Carga de acompanhantes permitida para este registro

#### OPÇÃO B — Importação Massiva via Planilha
* Upload de arquivo nos formatos `.xlsx` (Excel) ou `.csv`
* Validação automática das colunas e tipos de dados pelo sistema
* Exibição de tela de *preview* apontando linhas com erros (ex: CPFs inválidos ou telefones sem DDD)
* Opção de correção manual dos erros ou descarte das linhas inválidas
* Confirmação da importação em lote

### ⚙️ Processamento Pós-Cadastro
Independentemente da opção de cadastro utilizada, para cada convidado inserido, o sistema executa:
1. Geração de um **UUID v4 único** (utilizado como dado encriptado do QR Code).
2. Renderização do **QR Code** em formato de imagem PNG.
3. Atribuição do Status Inicial: `Pendente` $\rightarrow$ `Convite Enviado` $\rightarrow$ `Confirmado` $\rightarrow$ `Entrou`.

---

## 📦 MÓDULO 6 — Envio dos Convites

1. Admin seleciona os convidados desejados e clica em **"Enviar convites"**.
2. O Admin escolhe o canal de distribuição do ingresso:
   * **WhatsApp:** Mensagem direta com texto dinâmico + QR Code anexado como imagem.
   * **E-mail:** Mensagem formatada em HTML com a identidade do evento + arquivo PDF com o QR Code.
   * **Link Direto:** Cópia manual da URL individual para envio por meios externos.
3. O sistema envia as requisições de disparo para uma fila de processamento assíncrono (**Redis Queue**).
4. Microserviços processam os envios em background, garantindo estabilidade e impedindo o travamento do painel administrativo.
5. O painel web exibe o progresso do envio em tempo real através de contadores:
   * **✅ Enviados:** Contagem de sucessos.
   * **❌ Falhas:** Registros com falha de entrega (Gera alertas para números inválidos).
   * **⏳ Na fila:** Quantidade de mensagens aguardando processamento.

### ✉️ Modelo de Mensagem Recebida (WhatsApp)
> "Olá **[Nome do Convidado]**! Você está convidado para o evento **[Nome do Evento]**.
> 📅 **Data:** 15/06 às 20h
> 📍 **Local:** Espaço Vila Nova
> Presenteie sua entrada apresentando o QR Code abaixo na portaria do evento 👇
> *[Imagem do QR Code anexada]*"

---

## 📦 MÓDULO 7 — Gerenciamento de Dispositivos (Android)

Para garantir a segurança, os dispositivos móveis utilizados na portaria passam por um processo rigoroso de pareamento:

```
[Painel Web: Gera QR Code (JWT)] <─── (Scan da Câmera) ─── [App Android: Valida Token]
```

1. No painel web, o administrador acessa a tela de configurações e clica em **"+ Adicionar Dispositivo"**.
2. Preenche um identificador interno (Ex: *"Portaria Principal - Entrada 01"*) e clica em **[Gerar QR Code de Pareamento]**.
3. O sistema gera um QR Code temporário contendo:
   * Um token **JWT de uso único** com tempo de expiração curto (10 minutos).
   * A URL base da API do ambiente SaaS.
   * O ID único gerado para o dispositivo.
4. O administrador exibe esse QR Code na tela do monitor.
5. O operador, com o aplicativo **EventPass Portaria** instalado em um aparelho Android, clica na opção **"Vincular ao painel"**.
6. O aplicativo utiliza a câmera para fazer a leitura do QR Code do monitor.
7. O aplicativo envia o token criptografado para validação na API.
8. A API valida a autenticidade do JWT $\rightarrow$ Vincula de forma permanente o ID do dispositivo à conta do Admin, invalidando o token de uso único.
9. O aplicativo passa a exibir a mensagem: *"✅ Vinculado ao painel de [Nome do Admin]"*.
10. No painel Web, o dispositivo muda o status para **"Online 🟢"**, mostrando telemetrias como: nome do aparelho, horário do último acesso, versão do software instalado e o evento associado atualmente.

---

## 📦 MÓDULO 8 — Associação Dispositivo ↔ Evento

A vinculação lógica define quais dispositivos estão autorizados a liberar acessos para quais eventos.

* O Admin acessa a área interna do evento ativo e abre a aba **"Dispositivos"**.
* O sistema renderiza a lista de todos os aparelhos cadastrados na conta:
  * 📱 Portaria Principal `[Botão: Associar]`
  * 📱 Entrada VIP `[Botão: Associar]`
  * 📱 Tablet Recepção `[Botão: Associar]`
* Ao clicar em **"Associar"**, aquele dispositivo específico recebe autorização de leitura para o banco de dados de convidados deste respectivo evento.
* O aplicativo Android conectado recebe uma notificação via protocolo de comunicação contínua (WebSockets/SSE) e atualiza sua interface em tempo real para: **"Evento ativo: [Nome do Evento] 🟢"**.

### 🛠️ Regras de Negócio Importantes
* Um dispositivo móvel só consegue decodificar e validar QR Codes pertencentes ao evento com o qual está expressamente associado.
* Dispositivos sem associação activa exibem a mensagem lock: *"Nenhum evento ativo"*, bloqueando a abertura da câmera de leitura.
* O limite de dispositivos pareados operando simultaneamente varia de acordo com as travas do plano contratado pelo Admin.

---

## 📦 MÓDULO 9 — Operação da Portaria (App Android)

Com o dispositivo associado, o porteiro inicia o controle de acesso físico:

1. O app exibe o nome do evento, o balanço de entradas processadas até o momento e a capacidade de lotação atual do espaço.
2. O porteiro aciona o comando **[Iniciar Leitura]**, ativando o sensor de câmera do dispositivo (via biblioteca nativa de alto desempenho).
3. O convidado apresenta o QR Code (digital ou impresso).
4. O aplicativo realiza o scan, extrai o UUID e faz uma requisição HTTP do tipo POST para a API, que processa a validação retornando um dos cenários abaixo:

### 🟢 ENTRADA LIBERADA (Tela Verde)
* Emite sinal sonoro de aprovação + vibração no aparelho.
* Exibe na tela o nome completo do convidado, foto de identificação (se houver) e o setor/mesa designado.
* Disponibiliza um seletor numérico de acompanhantes: `[0] [1] [2] [3]`. O porteiro clica no número de pessoas que estão cruzando a catraca com o convidado principal para dar baixa.

### 🟡 JÁ ENTROU (Tela Amarela / Alerta de Duplicidade)
* Indica que aquele QR Code específico já foi validado anteriormente pelo sistema.
* Exibe o horário exato em que a primeira entrada foi registrada e o ID do dispositivo que realizou o scan.
* Exibe botões de ação manual para o porteiro: `[Permitir Reentrada]` ou `[Negar Acesso]`.

### 🔴 QR INVÁLIDO (Tela Vermelha)
* Exibe mensagem detalhada com a causa da recusa: código inexistente, ingresso pertencente a outro evento, ingresso cancelado ou fora do horário permitido.
* Fornece o botão de contingência: `[Buscar por nome]`.

### 🔍 BUSCA MANUAL (Recurso de Contingência)
Utilizado caso o convidado esteja sem bateria no celular ou com o papel do ingresso danificado. O porteiro digita parte do nome ou documento, o app filtra a lista de convidados autorizados diretamente na API e permite realizar o *check-in* manual com um clique.

> 📝 **Metadados do Registro de Entrada**
> Toda entrada validada (seja por leitura ou busca manual) grava obrigatoriamente no banco de dados: o timestamp exato da entrada, o ID do dispositivo coletor e o número de acompanhantes confirmados.

---

## 📦 MÓDULO 10 — Painel ao Vivo e Relatórios

### 📈 Monitoramento em Tempo Real (Live Dashboard)
Durante a execução do evento, o painel do Admin exibe em tempo real:
* Gráfico de linhas atualizado dinamicamente mostrando o fluxo de entradas por minuto (Curva de pico de chegada).
* Indicador percentual de ocupação física com base na capacidade máxima cadastrada.
* Feed de logs das últimas entradas processadas com fotos e nomes.
* Contador de convidados ausentes (*No-show*).
* Alertas visuais preventivos quando o local atinge 90% e 100% da capacidade total permitida.

### 📊 Inteligência Pós-Evento (Analytics)
Após o encerramento das atividades na portaria, o sistema consolida os dados para análise estratégica:
* Balanço numérico final entre convidados esperados, presentes, ausentes e soma total de acompanhantes que compareceram.
* Relatório analítico identificando os horários de maior fluxo na portaria (Gargalos de entrada).
* Segmentação e métricas de comparecimento divididas por grupos, setores ou mesas.
* Botão para exportação completa de relatórios gerenciais nos formatos **PDF formatado** ou planilhas **Excel**.

---

## 🔑 Detalhes Técnicos de Arquitetura em Destaque

* **🔗 Pareamento de Dispositivo:** Processo seguro baseado em um fluxo de handshake de duas etapas. O JWT temporário gerado no monitor é lido e trocado de forma segura por um **Device Token permanente** e exclusivo do hardware do dispositivo portaria, garantindo que credenciais de acesso administrativas nunca fiquem expostas no app mobile.
* **🔑 Arquitetura Multi-tenant:** Isolamento lógico completo a nível de banco de dados para cada Admin cadastrado. Os dados de convidados e eventos de uma empresa são completamente invisíveis para outras. O sistema valida os tokens de portaria de forma independente, permitindo que o app funcione sem depender de sessões de navegadores comuns.


# 🎉 EventPass SaaS — Arquitetura Macro v2

Este documento descreve a organização técnica e a infraestrutura do ecossistema **EventPass SaaS**, mapeando a interação entre as interfaces de usuário, o gateway de serviços em Spring Boot, as camadas de dados e as integrações de terceiros.

---

## 🗺️ Visão Geral da Arquitetura (Camadas)

A arquitetura do EventPass é dividida em 5 camadas lógicas estruturadas para suportar alta concorrência em tempo real, multi-tenancy e pareamento seguro de hardware.

```
[Camada 1: Interfaces (Web / Mobile)]
                 │  (HTTPS / REST / WebSockets)
                 ▼
[Camada 2: API Gateway (Spring Boot)]
                 │  (Domínios de Negócio)
                 ▼
[Camada 3: Serviços de Domínio (Core)]
                 │  (Leitura / Escrita / Filas)
                 ▼
[Camada 4: Persistência e Armazenamento]
                 │  (Webhooks / Chamadas de API)
                 ▼
[Camada 5: Serviços Externos (Integrações)]
```

---

## 📦 Detalhamento dos Componentes por Camada / Arquitetura

### 1. Interfaces — Clientes e Usuários
Responsável pela interação direta com administradores, porteiros e convidados.
* **🌐 Landing Page Pública:** Canal de conversão e aquisição de leads. Gerencia a exibição de planos, tabela de preços, fluxos de login e o formulário de cadastro público.
* **🖥️ Painel Web Admin (React + Vite):** Interface SPA de gerenciamento do organizador. Permite o CRUD de eventos, importação e controle de listas de convidados, pareamento de novos dispositivos e faturamento.
* **📊 Dashboard Ao Vivo:** Painel analítico de alta performance para o dia do evento. Utiliza protocolos de comunicação bidirecional (**WebSockets / SSE**) para plotar gráficos de entrada de fluxo em tempo real.
* **📱 App Portaria Android (Kotlin + CameraX):** Aplicativo móvel nativo focado em performance e leitura contínua. Utiliza a biblioteca CameraX para decodificação instantânea de QR Codes e opera de forma independente de sessões de navegador.
* **🔗 Página do Convite:** PWA estático e leve acessado pelo convidado final para visualização do QR Code de entrada, endereço mapeado e metadados do evento.

### 2. API Gateway — Backend Principal
O ponto central de entrada de dados da plataforma, centralizando políticas de segurança e roteamento.
* **⚙️ Spring Boot (Java 21):** Engine principal da aplicação desenvolvida com o ecossistema Spring.
* **Segurança e Infraestrutura Integrada:**
    * `JWT Auth:` Autenticação stateless para usuários administrativos.
    * `Device Token:` Validação baseada em hardware criptográfico para os dispositivos Android das portarias.
    * `Rate Limit:` Proteção contra ataques de negação de serviço (DoS) e tentativas de brute-force nas validações de convites.
    * `CORS:` Proteção de escopo de requisições web para o Painel React.
    * `Multi-tenant Engine:` Mecanismo em nível de infraestrutura para isolamento lógico de dados por cliente organizador.

### 3. Camada de Serviços — Domínios (Core)
Os microsserviços e componentes modulares que processam as regras de negócio do sistema:
* **👤 Auth & Usuários:** Controle de contas, geração de senhas seguras, verificação de contas via OTP e controle de dias restantes de Trial.
* **💳 Assinaturas:** Processamento do ciclo de vida dos planos, faturamento automático e escuta de webhooks de pagamento.
* **🗓️ Eventos:** Regras de negócio e transições de status de eventos (*Rascunho $
ightarrow$ Publicado $
ightarrow$ Em Andamento $
ightarrow$ Encerrado*).
* **👥 Convidados:** Motores de processamento para cadastros unitários ou processamento assíncrono de planilhas massivas em lote.
* **🔲 QR Code:** Geração de imagens PNG utilizando a biblioteca ZXing baseada em UUIDs v4 de alta entropia.
* **📱 Devices:** Handshake de pareamento e vinculação de dispositivos a eventos específicos.
* **🚪 Portaria:** Validação instantânea de chaves de acesso, checagem de reentradas e registro histórico de auditoria de entrada.
* **📤 Notificações:** Sistema de gerenciamento de mensageria assíncrona para WhatsApp e e-mails de ingressos.
* **📈 Relatórios:** Consolidação estatística pós-evento com exportação de dados para relatórios em PDF corporativo e planilhas Excel estruturadas.

### 4. Camada de Dados (Persistência e Armazenamento)
* **🗄️ MySQL:** Banco de dados relacional principal. Armazena os dados transacionais de usuários, eventos e convidados isolados logicamente por meio de estratégias multi-tenant baseadas em *Schema* dedicados ou identificadores de tabelas (`tenant_id`).
* **⚡ Redis:** Camada de memória ram ultrarrápida utilizada para:
    * Cache de dados quentes da portaria (garante validações abaixo de 50ms).
    * Armazenamento de tokens OTP voláteis com tempo de expiração (TTL).
    * Gerenciamento de filas assíncronas de mensagens de convites (**Redis Queue**).
* **🗂️ AWS S3 / MinIO:** Object Storage para armazenamento de arquivos estáticos como banners de eventos, imagens de QR Codes persistidas, relatórios gerados em PDF e planilhas de importação enviadas pelos clientes.

### 5. Serviços Externos (Integrações)
* **💬 WhatsApp API (Z-API / Evolution):** Gateway externo para envio em massa de convites com imagem, alertas de cobrança e disparos de segurança de tokens de verificação (OTP).
* **📧 E-mail (SendGrid / AWS SES):** Serviço SMTP de alta reputação para entrega de confirmações de compras, faturas e envio de ingressos em anexo PDF.
* **🔔 Push Notification (Firebase FCM):** Envio de sinais e comandos remotos em background para atualizar o status dos aplicativos Android de portaria (Ex: avisar o app que o evento foi alterado ou encerrado).
* **💳 Pagamentos (Mercado Pago):** Gateway financeiro integrado para split de notas e recebimento via Pix imediato, Cartão de Crédito recorrente e Boleto Bancário.
* **☁️ Hospedagem (Railway / VPS):** Infraestrutura cloud escalável rodando instâncias isoladas através de containers **Docker** gerenciados via pipelines automatizados de integração e entrega contínua (**CI/CD**).

---

## 🔗 Fluxo de Pareamento de Dispositivo (Device Pairing)

Para garantir que nenhum dispositivo não autorizado tenha acesso aos dados dos convidados, o pareamento de novos aparelhos portaria segue o fluxo criptográfico em 7 passos detalhado abaixo:

```
[Painel Web Admin]                                       [App Android]
        │                                                      │
 1. Clica em "+ Dispositivo"                                    │
        │                                                      │
 2. Solicita à API um JWT                                      │
        ▼                                                      │
 3. Exibe QR Code na Tela ─── 4. Câmera faz Scan do QR Code ───┤
 (JWT de uso único / 10min)                                    │
                                                               ▼
                                                  5. Troca JWT na API por um
                                                     Device Token Permanente
                                                               │
 6. Painel atualiza para 🟢 Online <───────────────────────────┤
        │                                                      │
 7. Associa Device ao Evento Ativo                             ▼
                                                  App atualiza para "Pronto para Ler"
```

1. **Ação do Usuário:** O Admin acessa a tela de gerenciamento de hardware no painel web e clica na opção `"+ Adicionar Dispositivo"`.
2. **Geração do Token Efêmero:** A API do Spring Boot gera um token **JWT (JSON Web Token) de uso único** com tempo de vida restrito a 10 minutos.
3. **Exibição Visual:** O painel web renderiza esse token em formato de QR Code na tela do computador.
4. **Captura Móvel:** O operador abre o aplicativo EventPass Portaria no aparelho Android e faz o escaneamento do QR Code exibido no monitor.
5. **Handshake de Segurança:** O aplicativo extrai o JWT e submete à API. A API valida a assinatura do token, invalida-o para novos usos e o substitui por um **Device Token permanente** atrelado ao identificador exclusivo do hardware do aparelho.
6. **Confirmação Visual:** O dispositivo estabelece conexão contínua e passa a constar como **Online 🟢** no painel administrativo web do organizador.
7. **Liberação Operacional:** O Admin vincula logicamente o dispositivo recém-cadastrado ao evento ativo do dia. O app Android é notificado e está pronto para iniciar as leituras.

---

## 🛠️ Regras de Negócio e Diretrizes de Arquitetura

> 🔒 **Isolamento de Dados Multi-tenant**
> Cada cliente Admin opera dentro de uma sandbox lógica estrita. Filtros nativos barram qualquer requisição entre dados de tenants distintos no banco de dados MySQL, prevenindo vazamentos de informações (*data leaks*).

> 🔑 **Segregação de Escopo de Credenciais**
> O `Device Token` gravado localmente no app Android possui escopo limitado exclusivamente a rotas de consulta de convidados e inserção de logs de entrada. Ele não possui privilégios de alteração de dados de faturamento, criação de eventos ou deleção de usuários administradores.

> ⏳ **Segurança Visual Antifraude**
> O QR Code gerado para pareamento expira rigidamente após 10 minutos. Se o operador não concluir o processo de leitura neste intervalo de tempo, o token é descartado pela API e um novo ciclo visual precisa ser iniciado.
