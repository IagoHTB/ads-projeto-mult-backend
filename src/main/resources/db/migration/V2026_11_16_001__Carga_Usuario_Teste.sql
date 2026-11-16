-- Segundo usuário de desenvolvimento, usado para testar que um usuário
-- não acessa lançamentos de outro pela rota (mesma senha hash do admin; só para laboratório).
INSERT INTO usuarios
(
    nome,
    email,
    senha_hash,
    perfil,
    ativo
)
VALUES
(
    'Operador Teste',
    'operador@example.com',
    '$2y$12$LHxoZ.5h0naCNnzigswKDuNHld41WB9teA.CVyQrZvJFiyCCivjDu',
    'OPERADOR',
    TRUE
);
