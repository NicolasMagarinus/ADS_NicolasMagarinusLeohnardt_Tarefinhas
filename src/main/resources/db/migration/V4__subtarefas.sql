CREATE TABLE subtarefa (
    id BIGSERIAL PRIMARY KEY,
    tarefa_id BIGINT NOT NULL REFERENCES tarefa (id) ON DELETE CASCADE,
    descricao VARCHAR(150) NOT NULL,
    concluida BOOLEAN NOT NULL DEFAULT FALSE,
    ordem INTEGER NOT NULL DEFAULT 0
);
CREATE INDEX ix_subtarefa_tarefa ON subtarefa (tarefa_id);
