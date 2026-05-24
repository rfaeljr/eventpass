-- =============================================================================
--  EventPass SaaS — Script de Inserts Fictícios (MySQL)
--  Finalidade: Testes e Desenvolvimento
-- =============================================================================

-- 1. CLIENTES (Tenants)
--$2a$12$dwTOQ.E1OO4YR5lIgfrqhOKiNJE1wBNiMdW/vXw66VLE9U9POg5PC --> minhasenha
INSERT INTO clientes (slug, nome, cpf, email, telefone_whatsapp, senha_hash, status, criado_em)
VALUES ('tech-events-ltda', 'Tech Events Consultoria', '111.111.111-11', 'contato@techevents.com', '+5511911111111',
        '$2a$12$dwTOQ.E1OO4YR5lIgfrqhOKiNJE1wBNiMdW/vXw66VLE9U9POg5PC', 'ATIVO', CURRENT_TIMESTAMP),
       ('festas-vip-premium', 'Festas VIP Organização', '222.222.222-22', 'financeiro@festasvip.com', '+5511922222222',
        '$2a$12$dwTOQ.E1OO4YR5lIgfrqhOKiNJE1wBNiMdW/vXw66VLE9U9POg5PC', 'ATIVO', CURRENT_TIMESTAMP),
       ('show-time-producoes', 'Show Time Produções', '333.333.333-33', 'producao@showtime.com', '+5511933333333',
        '$2a$12$dwTOQ.E1OO4YR5lIgfrqhOKiNJE1wBNiMdW/vXw66VLE9U9POg5PC', 'ATIVO', CURRENT_TIMESTAMP);

-- 2. ASSINATURAS
INSERT INTO assinaturas (cliente_id, plano_id, status, inicia_em, expira_em)
VALUES ((SELECT id FROM clientes WHERE slug = 'tech-events-ltda'), (SELECT id FROM planos WHERE slug = 'anual'),
        'ATIVO', CURRENT_TIMESTAMP, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 1 YEAR)),
       ((SELECT id FROM clientes WHERE slug = 'festas-vip-premium'), (SELECT id FROM planos WHERE slug = 'mensal'),
        'ATIVO', CURRENT_TIMESTAMP, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 1 MONTH)),
       ((SELECT id FROM clientes WHERE slug = 'show-time-producoes'), (SELECT id FROM planos WHERE slug = 'starter'),
        'TRIAL', CURRENT_TIMESTAMP, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 7 DAY));

-- 3. PAGAMENTOS
INSERT INTO pagamentos (cliente_id, assinatura_id, valor_centavos, metodo, status, pago_em)
VALUES ((SELECT id FROM clientes WHERE slug = 'tech-events-ltda'),
        (SELECT id
         FROM assinaturas
         WHERE cliente_id = (SELECT id FROM clientes WHERE slug = 'tech-events-ltda') LIMIT 1), 49900, 'CARTAO_CREDITO', 'APROVADO', CURRENT_TIMESTAMP),
       ((SELECT id FROM clientes WHERE slug = 'festas-vip-premium'),
        (SELECT id FROM assinaturas WHERE cliente_id = (SELECT id FROM clientes WHERE slug = 'festas-vip-premium') LIMIT 1), 5900, 'PIX', 'APROVADO', CURRENT_TIMESTAMP),
       ((SELECT id FROM clientes WHERE slug = 'show-time-producoes'), NULL, 2900, 'BOLETO', 'PENDENTE', NULL);

-- 4. EVENTOS (Usando UUID() nativo do MySQL)
INSERT INTO eventos (cliente_id, uuid, nome, descricao, local, inicia_em, termina_em, status)
VALUES ((SELECT id FROM clientes WHERE slug = 'tech-events-ltda'), UUID(), 'Workshop Java Backend',
        'Treinamento intensivo de Spring Boot', 'Auditório Central', CURRENT_TIMESTAMP,
        DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 4 HOUR), 'PUBLICADO'),
       ((SELECT id FROM clientes WHERE slug = 'festas-vip-premium'), UUID(), 'Casamento de Aline & Roberto',
        'Cerimônia e Recepção', 'Espaço Solar', DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 15 DAY),
        DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 16 DAY), 'PUBLICADO'),
       ((SELECT id FROM clientes WHERE slug = 'show-time-producoes'), UUID(), 'Festival de Rock Local',
        'Bandas da região', 'Praça da Liberdade', DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 1 MONTH),
        DATE_ADD(DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 1 MONTH), INTERVAL 12 HOUR), 'RASCUNHO');

-- 5. CONVIDADOS
INSERT INTO convidados (evento_id, uuid, nome, email, grupo_tag, status_convite)
VALUES ((SELECT id FROM eventos WHERE nome = 'Workshop Java Backend'), UUID(), 'João Silva', 'joao@email.com',
        'Desenvolvedores', 'CONFIRMADO'),
       ((SELECT id FROM eventos WHERE nome = 'Casamento de Aline & Roberto'), UUID(), 'Maria Oliveira',
        'maria@email.com', 'Família Noiva', 'PENDENTE'),
       ((SELECT id FROM eventos WHERE nome = 'Festival de Rock Local'), UUID(), 'Carlos Souza',
        'carlos@email.com', 'VIP', 'PENDENTE');

-- 6. DISPOSITIVOS
INSERT INTO dispositivos (cliente_id, descricao, status)
VALUES ((SELECT id FROM clientes WHERE slug = 'tech-events-ltda'), 'Tablet Portaria Principal', 'ATIVO'),
       ((SELECT id FROM clientes WHERE slug = 'festas-vip-premium'), 'Celular Segurança 01', 'ATIVO'),
       ((SELECT id FROM clientes WHERE slug = 'show-time-producoes'), 'Coletor Entrada VIP', 'AGUARDANDO');

-- 7. DISPOSITIVO_EVENTOS
INSERT INTO dispositivo_eventos (dispositivo_id, evento_id, ativo)
VALUES ((SELECT id FROM dispositivos WHERE descricao = 'Tablet Portaria Principal'),
        (SELECT id FROM eventos WHERE nome = 'Workshop Java Backend'), TRUE),
       ((SELECT id FROM dispositivos WHERE descricao = 'Celular Segurança 01'),
        (SELECT id FROM eventos WHERE nome = 'Casamento de Aline & Roberto'), TRUE),
       ((SELECT id FROM dispositivos WHERE descricao = 'Coletor Entrada VIP'),
        (SELECT id FROM eventos WHERE nome = 'Festival de Rock Local'), FALSE);

-- 8. ENTRADAS
INSERT INTO entradas (convidado_id, evento_id, dispositivo_id, status, registrado_em)
VALUES ((SELECT id FROM convidados WHERE nome = 'João Silva'),
        (SELECT id FROM eventos WHERE nome = 'Workshop Java Backend'),
        (SELECT id FROM dispositivos WHERE descricao = 'Tablet Portaria Principal'), 'LIBERADO', CURRENT_TIMESTAMP),
       ((SELECT id FROM convidados WHERE nome = 'Maria Oliveira'),
        (SELECT id FROM eventos WHERE nome = 'Casamento de Aline & Roberto'),
        (SELECT id FROM dispositivos WHERE descricao = 'Celular Segurança 01'), 'NEGADO', CURRENT_TIMESTAMP),
       ((SELECT id FROM convidados WHERE nome = 'João Silva'),
        (SELECT id FROM eventos WHERE nome = 'Workshop Java Backend'),
        (SELECT id FROM dispositivos WHERE descricao = 'Tablet Portaria Principal'), 'REENTRADA',
        DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 1 HOUR));

-- 9. CODIGOS_OTP
INSERT INTO codigos_otp (cliente_id, telefone, codigo, finalidade, expira_em)
VALUES ((SELECT id FROM clientes WHERE slug = 'tech-events-ltda'), '+5511911111111', 'hash_otp_1', 'LOGIN',
        DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 10 MINUTE)),
       ((SELECT id FROM clientes WHERE slug = 'festas-vip-premium'), '+5511922222222', 'hash_otp_2', 'CADASTRO',
        DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 10 MINUTE)),
       (NULL, '+5511999999999', 'hash_otp_3', 'CADASTRO', DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 10 MINUTE));

-- 10. LOGS_NOTIFICACOES
INSERT INTO logs_notificacoes (cliente_id, convidado_id, evento_id, canal, destinatario, template, status)
VALUES ((SELECT id FROM clientes WHERE slug = 'tech-events-ltda'),
        (SELECT id FROM convidados WHERE nome = 'João Silva'),
        (SELECT id FROM eventos WHERE nome = 'Workshop Java Backend'), 'EMAIL', 'joao@email.com', 'convite_evento',
        'ENVIADO'),
       ((SELECT id FROM clientes WHERE slug = 'festas-vip-premium'),
        (SELECT id FROM convidados WHERE nome = 'Maria Oliveira'),
        (SELECT id FROM eventos WHERE nome = 'Casamento de Aline & Roberto'), 'WHATSAPP', '+5511988888888',
        'lembrete_entrada', 'FALHOU'),
       ((SELECT id FROM clientes WHERE slug = 'show-time-producoes'), NULL, NULL, 'PUSH', 'token_firebase_123',
        'boas_vindas', 'NA_FILA');