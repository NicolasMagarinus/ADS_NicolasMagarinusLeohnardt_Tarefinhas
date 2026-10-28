CREATE TABLE categoria (
    id          BIGSERIAL PRIMARY KEY,
    usuario_id  BIGINT NOT NULL REFERENCES usuario (id) ON DELETE CASCADE,
    nome        VARCHAR(50) NOT NULL,
    cor         CHAR(7) NOT NULL,
    CONSTRAINT uq_categoria_usuario_nome UNIQUE (usuario_id, nome),
    CONSTRAINT ck_categoria_cor CHECK (cor ~ '^#[0-9A-Fa-f]{6}$')
);
CREATE INDEX ix_categoria_usuario ON categoria (usuario_id);

CREATE TABLE tarefa (
    id               BIGSERIAL PRIMARY KEY,
    usuario_id       BIGINT NOT NULL REFERENCES usuario (id) ON DELETE CASCADE,
    categoria_id     BIGINT REFERENCES categoria (id) ON DELETE SET NULL,
    titulo           VARCHAR(150) NOT NULL,
    descricao        TEXT,
    status           VARCHAR(20) NOT NULL DEFAULT 'A_FAZER',
    prioridade       VARCHAR(10) NOT NULL DEFAULT 'MEDIA',
    data_vencimento  DATE,
    posicao          INTEGER NOT NULL DEFAULT 0,
    criado_em        TIMESTAMPTZ NOT NULL DEFAULT now(),
    concluido_em     TIMESTAMPTZ,
    CONSTRAINT ck_tarefa_status CHECK (status IN ('A_FAZER', 'FAZENDO', 'CONCLUIDA')),
    CONSTRAINT ck_tarefa_prioridade CHECK (prioridade IN ('BAIXA', 'MEDIA', 'ALTA'))
);
CREATE INDEX ix_tarefa_usuario_status ON tarefa (usuario_id, status);
CREATE INDEX ix_tarefa_usuario_vencimento ON tarefa (usuario_id, data_vencimento);
