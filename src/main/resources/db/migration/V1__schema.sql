-- =====================================================================
-- TodoList - schema do login (somente a tabela de usuários)
-- Executado automaticamente pelo Flyway no deploy (FlywayMigrator).
-- As demais tabelas (categoria, tarefa, etc.) entram com as outras funcionalidades.
-- =====================================================================

CREATE TABLE usuario (
    id          BIGSERIAL    PRIMARY KEY,
    nome        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    senha_hash  VARCHAR(255) NOT NULL,
    papel       VARCHAR(20)  NOT NULL DEFAULT 'USER',
    ativo       BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT ck_usuario_papel CHECK (papel IN ('USER', 'ADMIN'))
);
