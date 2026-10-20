-- =====================================================================
-- Administrador inicial
--   e-mail: admin@todolist.local
--   senha : Admin@12345   (troque no primeiro acesso, em "Meu perfil")
-- O hash abaixo foi gerado pela implementação Pbkdf2PasswordHash do GlassFish
-- (formato algoritmo:iterações:salt:hash, em Base64).
-- =====================================================================
INSERT INTO usuario (nome, email, senha_hash, papel, ativo, criado_em)
VALUES ('Administrador',
        'admin@todolist.local',
        'PBKDF2WithHmacSHA256:2048:5Y5k2Ds5TvnV8lxsJuaP6YpYFVexMZuraeUNWEEPz7k=:h/2nvfYDt6N+02E3p9RwHdtDaHUKuLkAR0Yq0jPYc6o=',
        'ADMIN',
        TRUE,
        now());
