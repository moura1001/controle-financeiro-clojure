CREATE TABLE IF NOT EXISTS transacoes(
    id BIGSERIAL PRIMARY KEY,
    valor NUMERIC NOT NULL,
    tipo VARCHAR(8) NOT NULL,
    rotulos VARCHAR[] DEFAULT '{}'
);

INSERT INTO transacoes(valor, tipo, rotulos)
VALUES (3000, 'receita', '{"salário"}'),
        (400, 'despesa', '{"curso", "educação"}'),
        (88, 'despesa', '{"livro", "educação"}');