-- Desativa a verificação de chaves estrangeiras para permitir a limpeza total
SET FOREIGN_KEY_CHECKS = 0;

-- 1. Tabelas de Log e Operações (Geralmente as últimas da hierarquia)
TRUNCATE TABLE logs_notificacoes;
TRUNCATE TABLE entradas;
TRUNCATE TABLE codigos_otp;

-- 2. Tabelas de Relacionamento e Dispositivos
TRUNCATE TABLE dispositivo_eventos;
TRUNCATE TABLE dispositivos;

-- 3. Tabelas de Público e Eventos
TRUNCATE TABLE convidados;
TRUNCATE TABLE eventos;

-- 4. Tabelas Financeiras e Contratuais
TRUNCATE TABLE pagamentos;
TRUNCATE TABLE assinaturas;

-- 5. Tabelas Base (Mães de todas)
TRUNCATE TABLE clientes;

-- Reativa a verificação de chaves estrangeiras
SET FOREIGN_KEY_CHECKS = 1;