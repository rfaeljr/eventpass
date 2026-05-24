-- =============================================================================
--  EventPass SaaS — Script DDL Completo
--  Banco de Dados: MySQL 8.0+
--  Charset: utf8mb4 | Collation: utf8mb4_unicode_ci
--  Gerado em: 2025
-- =============================================================================

CREATE DATABASE IF NOT EXISTS eventpass
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE eventpass;

-- =============================================================================
-- 1. PLANOS
--    Precisa existir antes de clientes/assinaturas
-- =============================================================================
CREATE TABLE planos (
    id                        BIGINT          NOT NULL AUTO_INCREMENT,
    slug                      VARCHAR(40)     NOT NULL,
    nome                      VARCHAR(60)     NOT NULL,
    tipo_cobranca             ENUM(
                                  'MENSAL',
                                  'ANUAL',
                                  'POR_EVENTO'
                              )               NOT NULL,
    preco_centavos            INT             NOT NULL DEFAULT 0 COMMENT 'Valor em centavos. Ex: R$59,90 = 5990',
    max_eventos               INT             NULL     COMMENT 'NULL = ilimitado',
    max_convidados_por_evento INT             NULL     COMMENT 'NULL = ilimitado',
    max_dispositivos          INT             NOT NULL DEFAULT 1,
    tem_relatorios            BOOLEAN         NOT NULL DEFAULT FALSE,
    ativo                     BOOLEAN         NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_planos PRIMARY KEY (id),
    CONSTRAINT uq_planos_slug UNIQUE (slug)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Planos disponíveis no SaaS (starter, mensal, anual, por_evento)';


-- =============================================================================
-- 2. CLIENTES  (tenants — multi-tenant)
-- =============================================================================
CREATE TABLE clientes (
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    slug                 VARCHAR(80)  NOT NULL COMMENT 'Identificador único amigável da conta',
    nome                 VARCHAR(120) NOT NULL,
    cpf                  VARCHAR(14)  NOT NULL COMMENT 'Formato: 000.000.000-00',
    email                VARCHAR(150) NOT NULL,
    telefone_whatsapp    VARCHAR(20)  NOT NULL COMMENT 'Formato: +5511999999999',
    senha_hash           VARCHAR(255) NOT NULL,
    telefone_verificado  BOOLEAN      NOT NULL DEFAULT FALSE,
    status               ENUM(
                             'ATIVO',
                             'SUSPENSO',
                             'CANCELADO'
                         )            NOT NULL DEFAULT 'ATIVO',
    trial_expira_em      DATETIME     NULL,
    criado_em            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em        DATETIME     NULL     ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_clientes    PRIMARY KEY (id),
    CONSTRAINT uq_clientes_slug  UNIQUE (slug),
    CONSTRAINT uq_clientes_cpf   UNIQUE (cpf),
    CONSTRAINT uq_clientes_email UNIQUE (email),

    INDEX idx_clientes_status (status),
    INDEX idx_clientes_trial_expira_em (trial_expira_em)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Contas SaaS dos organizadores de eventos (tenants)';


-- =============================================================================
-- 3. ASSINATURAS
-- =============================================================================
CREATE TABLE assinaturas (
    id                BIGINT   NOT NULL AUTO_INCREMENT,
    cliente_id        BIGINT   NOT NULL,
    plano_id          BIGINT   NOT NULL,
    status            ENUM(
                          'TRIAL',
                          'ATIVO',
                          'ATRASADO',
                          'CANCELADO'
                      )        NOT NULL DEFAULT 'TRIAL',
    inicia_em         DATETIME NOT NULL,
    expira_em         DATETIME NULL,
    renova_em         DATETIME NULL,
    mp_assinatura_id  VARCHAR(100) NULL COMMENT 'ID da assinatura no Mercado Pago',
    criado_em         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_assinaturas PRIMARY KEY (id),

    CONSTRAINT fk_assinaturas_cliente
        FOREIGN KEY (cliente_id) REFERENCES clientes (id)
        ON DELETE CASCADE ON UPDATE CASCADE,

    CONSTRAINT fk_assinaturas_plano
        FOREIGN KEY (plano_id) REFERENCES planos (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    INDEX idx_assinaturas_cliente_id (cliente_id),
    INDEX idx_assinaturas_status     (status),
    INDEX idx_assinaturas_renova_em  (renova_em)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Assinaturas ativas e históricas dos clientes';


-- =============================================================================
-- 4. PAGAMENTOS
-- =============================================================================
CREATE TABLE pagamentos (
    id               BIGINT      NOT NULL AUTO_INCREMENT,
    cliente_id       BIGINT      NOT NULL,
    assinatura_id    BIGINT      NULL COMMENT 'NULL para cobranças avulsas (por_evento)',
    valor_centavos   INT         NOT NULL COMMENT 'Valor em centavos',
    metodo           ENUM(
                         'CARTAO_CREDITO',
                         'PIX',
                         'BOLETO'
                     )           NOT NULL,
    status           ENUM(
                         'PENDENTE',
                         'APROVADO',
                         'RECUSADO',
                         'ESTORNADO'
                     )           NOT NULL DEFAULT 'PENDENTE',
    mp_pagamento_id  VARCHAR(100) NULL COMMENT 'ID do pagamento no Mercado Pago',
    pago_em          DATETIME    NULL,
    criado_em        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_pagamentos PRIMARY KEY (id),
    CONSTRAINT uq_pagamentos_mp_id UNIQUE (mp_pagamento_id),

    CONSTRAINT fk_pagamentos_cliente
        FOREIGN KEY (cliente_id) REFERENCES clientes (id)
        ON DELETE CASCADE ON UPDATE CASCADE,

    CONSTRAINT fk_pagamentos_assinatura
        FOREIGN KEY (assinatura_id) REFERENCES assinaturas (id)
        ON DELETE SET NULL ON UPDATE CASCADE,

    INDEX idx_pagamentos_cliente_id (cliente_id),
    INDEX idx_pagamentos_status     (status),
    INDEX idx_pagamentos_pago_em    (pago_em)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Histórico de pagamentos e cobranças';


-- =============================================================================
-- 5. EVENTOS
-- =============================================================================
CREATE TABLE eventos (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    cliente_id          BIGINT       NOT NULL,
    uuid                CHAR(36)     NOT NULL COMMENT 'UUID v4 — usado na URL pública do convite',
    nome                VARCHAR(150) NOT NULL,
    descricao           TEXT         NULL,
    local               VARCHAR(255) NULL,
    url_banner          VARCHAR(500) NULL COMMENT 'URL do banner armazenado no S3/MinIO',
    inicia_em           DATETIME     NOT NULL,
    termina_em          DATETIME     NOT NULL,
    capacidade_maxima   INT          NULL COMMENT 'NULL = sem limite de capacidade',
    max_acompanhantes   INT          NOT NULL DEFAULT 0 COMMENT 'Máx. de acompanhantes por convidado',
    permite_reentrada   BOOLEAN      NOT NULL DEFAULT FALSE,
    status              ENUM(
                            'RASCUNHO',
                            'PUBLICADO',
                            'EM_ANDAMENTO',
                            'ENCERRADO',
                            'CANCELADO'
                        )            NOT NULL DEFAULT 'RASCUNHO',
    criado_em           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em       DATETIME     NULL     ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_eventos   PRIMARY KEY (id),
    CONSTRAINT uq_eventos_uuid UNIQUE (uuid),

    CONSTRAINT fk_eventos_cliente
        FOREIGN KEY (cliente_id) REFERENCES clientes (id)
        ON DELETE CASCADE ON UPDATE CASCADE,

    INDEX idx_eventos_cliente_id (cliente_id),
    INDEX idx_eventos_status     (status),
    INDEX idx_eventos_inicia_em  (inicia_em)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Eventos criados pelos clientes (organizadores)';


-- =============================================================================
-- 6. CONVIDADOS
-- =============================================================================
CREATE TABLE convidados (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    evento_id           BIGINT       NOT NULL,
    uuid                CHAR(36)     NOT NULL COMMENT 'UUID v4 — é a chave gravada no QR Code',
    nome                VARCHAR(150) NOT NULL,
    documento           VARCHAR(20)  NULL COMMENT 'CPF ou RG',
    telefone            VARCHAR(20)  NULL,
    email               VARCHAR(150) NULL,
    grupo_tag           VARCHAR(60)  NULL COMMENT 'Mesa, setor, turma, etc.',
    max_acompanhantes   INT          NOT NULL DEFAULT 0,
    url_qrcode          VARCHAR(500) NULL COMMENT 'URL da imagem do QR Code no S3/MinIO',
    status_convite      ENUM(
                            'PENDENTE',
                            'ENVIADO',
                            'CONFIRMADO',
                            'RECUSADO'
                        )            NOT NULL DEFAULT 'PENDENTE',
    convite_enviado_em  DATETIME     NULL,
    criado_em           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_convidados     PRIMARY KEY (id),
    CONSTRAINT uq_convidados_uuid UNIQUE (uuid),

    CONSTRAINT fk_convidados_evento
        FOREIGN KEY (evento_id) REFERENCES eventos (id)
        ON DELETE CASCADE ON UPDATE CASCADE,

    INDEX idx_convidados_evento_id      (evento_id),
    INDEX idx_convidados_status_convite (status_convite),
    INDEX idx_convidados_grupo_tag      (grupo_tag)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Convidados de cada evento com o UUID que gera o QR Code';


-- =============================================================================
-- 7. DISPOSITIVOS  (app Android da portaria)
-- =============================================================================
CREATE TABLE dispositivos (
    id                    BIGINT       NOT NULL AUTO_INCREMENT,
    cliente_id            BIGINT       NOT NULL,
    token_dispositivo     VARCHAR(256) NULL     COMMENT 'Token permanente após pareamento concluído',
    token_pareamento      VARCHAR(256) NULL     COMMENT 'JWT de uso único gerado no painel — expira em 10min',
    pareamento_expira_em  DATETIME     NULL,
    descricao             VARCHAR(80)  NOT NULL COMMENT 'Ex: "Portaria Principal", "Entrada VIP"',
    modelo_android        VARCHAR(100) NULL,
    versao_app            VARCHAR(20)  NULL,
    status                ENUM(
                              'AGUARDANDO',
                              'ATIVO',
                              'REVOGADO'
                          )            NOT NULL DEFAULT 'AGUARDANDO',
    ultimo_acesso_em      DATETIME     NULL,
    criado_em             DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_dispositivos PRIMARY KEY (id),
    CONSTRAINT uq_dispositivos_token_dispositivo UNIQUE (token_dispositivo),
    CONSTRAINT uq_dispositivos_token_pareamento  UNIQUE (token_pareamento),

    CONSTRAINT fk_dispositivos_cliente
        FOREIGN KEY (cliente_id) REFERENCES clientes (id)
        ON DELETE CASCADE ON UPDATE CASCADE,

    INDEX idx_dispositivos_cliente_id (cliente_id),
    INDEX idx_dispositivos_status     (status)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Dispositivos Android da portaria vinculados à conta do cliente';


-- =============================================================================
-- 8. DISPOSITIVO_EVENTOS  (N:M — qual device lê qual evento)
-- =============================================================================
CREATE TABLE dispositivo_eventos (
    id              BIGINT   NOT NULL AUTO_INCREMENT,
    dispositivo_id  BIGINT   NOT NULL,
    evento_id       BIGINT   NOT NULL,
    ativo           BOOLEAN  NOT NULL DEFAULT TRUE,
    associado_em    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_dispositivo_eventos PRIMARY KEY (id),
    CONSTRAINT uq_dispositivo_eventos UNIQUE (dispositivo_id, evento_id),

    CONSTRAINT fk_disp_eventos_dispositivo
        FOREIGN KEY (dispositivo_id) REFERENCES dispositivos (id)
        ON DELETE CASCADE ON UPDATE CASCADE,

    CONSTRAINT fk_disp_eventos_evento
        FOREIGN KEY (evento_id) REFERENCES eventos (id)
        ON DELETE CASCADE ON UPDATE CASCADE,

    INDEX idx_disp_eventos_evento_id (evento_id),
    INDEX idx_disp_eventos_ativo     (ativo)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Associação N:M entre dispositivos e eventos. ativo=true = evento em leitura';


-- =============================================================================
-- 9. ENTRADAS  (log imutável de cada leitura na portaria)
-- =============================================================================
CREATE TABLE entradas (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    convidado_id        BIGINT       NOT NULL,
    evento_id           BIGINT       NOT NULL,
    dispositivo_id      BIGINT       NOT NULL,
    qtd_acompanhantes   INT          NOT NULL DEFAULT 0,
    status              ENUM(
                            'LIBERADO',
                            'NEGADO',
                            'REENTRADA'
                        )            NOT NULL,
    motivo_negacao      VARCHAR(100) NULL COMMENT 'Ex: qr_invalido, ja_entrou, evento_encerrado',
    registrado_em       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_entradas PRIMARY KEY (id),

    CONSTRAINT fk_entradas_convidado
        FOREIGN KEY (convidado_id) REFERENCES convidados (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT fk_entradas_evento
        FOREIGN KEY (evento_id) REFERENCES eventos (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT fk_entradas_dispositivo
        FOREIGN KEY (dispositivo_id) REFERENCES dispositivos (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    INDEX idx_entradas_evento_id      (evento_id),
    INDEX idx_entradas_convidado_id   (convidado_id),
    INDEX idx_entradas_registrado_em  (registrado_em),
    INDEX idx_entradas_status         (status)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Registro imutável de cada leitura de QR Code na portaria';


-- =============================================================================
-- 10. CODIGOS_OTP  (verificação via WhatsApp)
-- =============================================================================
CREATE TABLE codigos_otp (
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    cliente_id  BIGINT      NULL COMMENT 'NULL enquanto o cliente ainda não foi criado (pré-cadastro)',
    telefone    VARCHAR(20) NOT NULL,
    codigo      VARCHAR(255) NOT NULL COMMENT 'Hash do código de 6 dígitos (bcrypt)',
    finalidade  ENUM(
                    'CADASTRO',
                    'LOGIN',
                    'RECUPERACAO'
                )           NOT NULL DEFAULT 'CADASTRO',
    expira_em   DATETIME    NOT NULL,
    usado_em    DATETIME    NULL,
    tentativas  INT         NOT NULL DEFAULT 0 COMMENT 'Máx. 5 tentativas antes de invalidar',
    criado_em   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_codigos_otp PRIMARY KEY (id),

    CONSTRAINT fk_codigos_otp_cliente
        FOREIGN KEY (cliente_id) REFERENCES clientes (id)
        ON DELETE SET NULL ON UPDATE CASCADE,

    INDEX idx_codigos_otp_telefone  (telefone),
    INDEX idx_codigos_otp_expira_em (expira_em)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Códigos OTP de 6 dígitos enviados via WhatsApp para verificação';


-- =============================================================================
-- 11. LOGS_NOTIFICACOES  (rastreio de cada envio)
-- =============================================================================
CREATE TABLE logs_notificacoes (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    cliente_id     BIGINT       NOT NULL,
    convidado_id   BIGINT       NULL COMMENT 'NULL para notificações gerais (sem convidado específico)',
    evento_id      BIGINT       NULL,
    canal          ENUM(
                       'WHATSAPP',
                       'EMAIL',
                       'PUSH'
                   )            NOT NULL,
    destinatario   VARCHAR(150) NOT NULL COMMENT 'Telefone, e-mail ou device token',
    template       VARCHAR(60)  NOT NULL COMMENT 'Ex: convite_evento, lembrete_entrada, otp_cadastro',
    status         ENUM(
                       'NA_FILA',
                       'ENVIADO',
                       'FALHOU'
                   )            NOT NULL DEFAULT 'NA_FILA',
    ref_provedor   VARCHAR(100) NULL COMMENT 'ID de retorno do provedor (Z-API, SendGrid, FCM)',
    msg_erro       TEXT         NULL,
    enviado_em     DATETIME     NULL,
    criado_em      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_logs_notificacoes PRIMARY KEY (id),

    CONSTRAINT fk_logs_notif_cliente
        FOREIGN KEY (cliente_id) REFERENCES clientes (id)
        ON DELETE CASCADE ON UPDATE CASCADE,

    CONSTRAINT fk_logs_notif_convidado
        FOREIGN KEY (convidado_id) REFERENCES convidados (id)
        ON DELETE SET NULL ON UPDATE CASCADE,

    CONSTRAINT fk_logs_notif_evento
        FOREIGN KEY (evento_id) REFERENCES eventos (id)
        ON DELETE SET NULL ON UPDATE CASCADE,

    INDEX idx_logs_notif_cliente_id   (cliente_id),
    INDEX idx_logs_notif_convidado_id (convidado_id),
    INDEX idx_logs_notif_status       (status),
    INDEX idx_logs_notif_criado_em    (criado_em)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Log de todos os envios de notificação (WhatsApp, e-mail, push)';


-- =============================================================================
-- DADOS INICIAIS — Planos padrão
-- =============================================================================
INSERT INTO planos
    (slug, nome, tipo_cobranca, preco_centavos, max_eventos, max_convidados_por_evento, max_dispositivos, tem_relatorios, ativo)
VALUES
    ('STARTER',   'Starter Gratuito', 'POR_EVENTO', 0,     1,    50,   1,  FALSE, TRUE),
    ('POR_EVENTO','Por Evento',        'POR_EVENTO', 2900,  NULL, 300,  1,  FALSE, TRUE),
    ('MENSAL',    'Mensal',            'MENSAL',     5900,  NULL, 500,  3,  TRUE,  TRUE),
    ('ANUAL',     'Anual',             'ANUAL',      49900, NULL, 2000, 10, TRUE,  TRUE);


-- =============================================================================
-- VIEW: resumo de entradas por evento (útil para o dashboard ao vivo)
-- =============================================================================
CREATE OR REPLACE VIEW vw_resumo_entradas AS
SELECT
    e.id                                                    AS evento_id,
    e.nome                                                  AS evento_nome,
    e.cliente_id,
    e.capacidade_maxima,
    COUNT(en.id)                                            AS total_leituras,
    SUM(CASE WHEN en.status = 'liberado'  THEN 1 ELSE 0 END) AS total_entradas,
    SUM(CASE WHEN en.status = 'negado'    THEN 1 ELSE 0 END) AS total_negadas,
    SUM(CASE WHEN en.status = 'reentrada' THEN 1 ELSE 0 END) AS total_reentradas,
    SUM(COALESCE(en.qtd_acompanhantes, 0))                  AS total_acompanhantes
FROM eventos e
LEFT JOIN entradas en ON en.evento_id = e.id
GROUP BY e.id, e.nome, e.cliente_id, e.capacidade_maxima;


-- =============================================================================
-- VIEW: convidados com status de entrada para o painel ao vivo
-- =============================================================================
CREATE OR REPLACE VIEW vw_convidados_evento AS
SELECT
    c.id                AS convidado_id,
    c.evento_id,
    c.uuid              AS qrcode_uuid,
    c.nome,
    c.documento,
    c.grupo_tag,
    c.max_acompanhantes,
    c.status_convite,
    MAX(en.registrado_em)                                         AS ultima_entrada_em,
    SUM(CASE WHEN en.status = 'liberado' THEN 1 ELSE 0 END)      AS total_entradas,
    CASE WHEN MAX(en.status) = 'liberado' THEN TRUE ELSE FALSE END AS entrou
FROM convidados c
LEFT JOIN entradas en ON en.convidado_id = c.id AND en.status IN ('liberado','reentrada')
GROUP BY c.id, c.evento_id, c.uuid, c.nome, c.documento,
         c.grupo_tag, c.max_acompanhantes, c.status_convite;


-- =============================================================================
-- FIM DO SCRIPT
-- Tabelas criadas: 11
-- Views criadas:    2
-- Registros seed:   4 planos padrão
-- =============================================================================
