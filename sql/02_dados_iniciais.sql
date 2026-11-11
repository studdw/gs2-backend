-- =====================================================================
-- Rota para Todos - dados iniciais de DEMONSTRACAO (opcional)
-- Execute depois do 01_criar_tabelas.sql, em um banco ainda vazio.
--
-- Contas de demonstracao (todas ficticias, com a mesma senha):
--   gestor@rotaparatodos.app   -> Gestor publico
--   cida@rotaparatodos.app     -> Cidada (usa andador)
--   bruno@rotaparatodos.app    -> Cidadao (usa cadeira de rodas)
--   marina@rotaparatodos.app   -> Cidada (carrinho de bebe)
--   paulo@rotaparatodos.app    -> Cidadao (deficiencia visual)
--   Senha de todas: RotaParaTodos@2026
--
-- A coluna ds_senha guarda apenas o hash BCrypt dessa senha.
-- Enderecos e coordenadas sao aproximados e servem so para a demonstracao.
-- =====================================================================

-- Usuarios -------------------------------------------------------------
INSERT INTO tb_usuario (cd_usuario, nm_usuario, ds_email, ds_senha, tp_papel, tp_perfil_mobilidade, dt_cadastro)
VALUES (1, 'Rafael Gestor', 'gestor@rotaparatodos.app', '$2a$10$4NScRn4XMJiKOClYva49.eEaor/m0d7FUIR7HqcCm8cRCG4Qu3yuG', 'GESTOR', NULL, SYSDATE - 60);
INSERT INTO tb_usuario (cd_usuario, nm_usuario, ds_email, ds_senha, tp_papel, tp_perfil_mobilidade, dt_cadastro)
VALUES (2, 'Cida Oliveira', 'cida@rotaparatodos.app', '$2a$10$4NScRn4XMJiKOClYva49.eEaor/m0d7FUIR7HqcCm8cRCG4Qu3yuG', 'CIDADAO', 'MULETA_ANDADOR', SYSDATE - 50);
INSERT INTO tb_usuario (cd_usuario, nm_usuario, ds_email, ds_senha, tp_papel, tp_perfil_mobilidade, dt_cadastro)
VALUES (3, 'Bruno Santos', 'bruno@rotaparatodos.app', '$2a$10$4NScRn4XMJiKOClYva49.eEaor/m0d7FUIR7HqcCm8cRCG4Qu3yuG', 'CIDADAO', 'CADEIRANTE', SYSDATE - 48);
INSERT INTO tb_usuario (cd_usuario, nm_usuario, ds_email, ds_senha, tp_papel, tp_perfil_mobilidade, dt_cadastro)
VALUES (4, 'Marina Costa', 'marina@rotaparatodos.app', '$2a$10$4NScRn4XMJiKOClYva49.eEaor/m0d7FUIR7HqcCm8cRCG4Qu3yuG', 'CIDADAO', 'CARRINHO_BEBE', SYSDATE - 47);
INSERT INTO tb_usuario (cd_usuario, nm_usuario, ds_email, ds_senha, tp_papel, tp_perfil_mobilidade, dt_cadastro)
VALUES (5, 'Paulo Lima', 'paulo@rotaparatodos.app', '$2a$10$4NScRn4XMJiKOClYva49.eEaor/m0d7FUIR7HqcCm8cRCG4Qu3yuG', 'CIDADAO', 'DEFICIENCIA_VISUAL', SYSDATE - 46);

-- Relatos --------------------------------------------------------------
INSERT INTO tb_relato (cd_relato, cd_usuario, tp_obstaculo, tp_gravidade, ds_relato, ds_endereco, nm_bairro, vl_latitude, vl_longitude, vl_confianca_ia, st_relato, dt_criacao, dt_previsao_obra, dt_resolucao)
VALUES (1, 2, 'BURACO', 'ALTA', 'Buraco grande no meio da calçada, perto do ponto de ônibus.', 'Rua Domingos de Morais, 2100', 'Vila Mariana', -23.588000, -46.634200, 92, 'CONFIRMADO', SYSDATE - 12, NULL, NULL);
INSERT INTO tb_relato (cd_relato, cd_usuario, tp_obstaculo, tp_gravidade, ds_relato, ds_endereco, nm_bairro, vl_latitude, vl_longitude, vl_confianca_ia, st_relato, dt_criacao, dt_previsao_obra, dt_resolucao)
VALUES (2, 3, 'SEM_RAMPA', 'CRITICA', 'Esquina sem rampa e com guia alta: cadeira de rodas não passa.', 'Rua Sena Madureira, 300', 'Vila Mariana', -23.592500, -46.639500, 92, 'CONFIRMADO', SYSDATE - 20, NULL, NULL);
INSERT INTO tb_relato (cd_relato, cd_usuario, tp_obstaculo, tp_gravidade, ds_relato, ds_endereco, nm_bairro, vl_latitude, vl_longitude, vl_confianca_ia, st_relato, dt_criacao, dt_previsao_obra, dt_resolucao)
VALUES (3, 4, 'ILUMINACAO_RUIM', 'MEDIA', 'Trecho escuro à noite, poste apagado há semanas.', 'Rua Pedro de Toledo, 500', 'Vila Clementino', -23.596800, -46.644000, 80, 'PUBLICADO', SYSDATE - 8, NULL, NULL);
INSERT INTO tb_relato (cd_relato, cd_usuario, tp_obstaculo, tp_gravidade, ds_relato, ds_endereco, nm_bairro, vl_latitude, vl_longitude, vl_confianca_ia, st_relato, dt_criacao, dt_previsao_obra, dt_resolucao)
VALUES (4, 2, 'CALCADA_IRREGULAR', 'MEDIA', 'Piso quebrado por raiz de árvore.', 'Rua Borges Lagoa, 700', 'Vila Clementino', -23.598500, -46.647000, 80, 'PUBLICADO', SYSDATE - 5, NULL, NULL);
INSERT INTO tb_relato (cd_relato, cd_usuario, tp_obstaculo, tp_gravidade, ds_relato, ds_endereco, nm_bairro, vl_latitude, vl_longitude, vl_confianca_ia, st_relato, dt_criacao, dt_previsao_obra, dt_resolucao)
VALUES (5, 5, 'DEGRAU', 'ALTA', 'Degrau alto na entrada da passagem, sem alternativa.', 'Avenida Paulista, 1100', 'Bela Vista', -23.564800, -46.652000, 92, 'EM_OBRA', SYSDATE - 25, TRUNC(SYSDATE) + 20, NULL);
INSERT INTO tb_relato (cd_relato, cd_usuario, tp_obstaculo, tp_gravidade, ds_relato, ds_endereco, nm_bairro, vl_latitude, vl_longitude, vl_confianca_ia, st_relato, dt_criacao, dt_previsao_obra, dt_resolucao)
VALUES (6, 3, 'OBSTRUCAO', 'MEDIA', 'Entulho de obra ocupando a calçada inteira.', 'Rua Vergueiro, 1500', 'Paraíso', -23.574000, -46.640000, 80, 'PUBLICADO', SYSDATE - 6, NULL, NULL);
INSERT INTO tb_relato (cd_relato, cd_usuario, tp_obstaculo, tp_gravidade, ds_relato, ds_endereco, nm_bairro, vl_latitude, vl_longitude, vl_confianca_ia, st_relato, dt_criacao, dt_previsao_obra, dt_resolucao)
VALUES (7, 4, 'TRAVESSIA_INSEGURA', 'ALTA', 'Travessia muito movimentada e sem semáforo sonoro.', 'Praça da Sé, 100', 'Sé', -23.550300, -46.633900, 92, 'CONFIRMADO', SYSDATE - 15, NULL, NULL);
INSERT INTO tb_relato (cd_relato, cd_usuario, tp_obstaculo, tp_gravidade, ds_relato, ds_endereco, nm_bairro, vl_latitude, vl_longitude, vl_confianca_ia, st_relato, dt_criacao, dt_previsao_obra, dt_resolucao)
VALUES (8, 5, 'BURACO', 'MEDIA', 'Buraco na calçada em frente à loja.', 'Rua Galvão Bueno, 200', 'Liberdade', -23.556000, -46.635200, 80, 'RESOLVIDO', SYSDATE - 30, NULL, SYSDATE - 3);
INSERT INTO tb_relato (cd_relato, cd_usuario, tp_obstaculo, tp_gravidade, ds_relato, ds_endereco, nm_bairro, vl_latitude, vl_longitude, vl_confianca_ia, st_relato, dt_criacao, dt_previsao_obra, dt_resolucao)
VALUES (9, 2, 'LOCAL_ISOLADO', 'MEDIA', 'Passagem deserta depois que o comércio fecha.', 'Rua Muniz de Souza, 400', 'Aclimação', -23.571200, -46.629000, 80, 'PUBLICADO', SYSDATE - 4, NULL, NULL);
INSERT INTO tb_relato (cd_relato, cd_usuario, tp_obstaculo, tp_gravidade, ds_relato, ds_endereco, nm_bairro, vl_latitude, vl_longitude, vl_confianca_ia, st_relato, dt_criacao, dt_previsao_obra, dt_resolucao)
VALUES (10, 3, 'CALCADA_IRREGULAR', 'BAIXA', NULL, 'Rua Tutóia, 100', 'Paraíso', -23.576000, -46.647000, 40, 'EM_MODERACAO', SYSDATE - 1, NULL, NULL);
INSERT INTO tb_relato (cd_relato, cd_usuario, tp_obstaculo, tp_gravidade, ds_relato, ds_endereco, nm_bairro, vl_latitude, vl_longitude, vl_confianca_ia, st_relato, dt_criacao, dt_previsao_obra, dt_resolucao)
VALUES (11, 4, 'SEM_RAMPA', 'ALTA', 'Falta de rampa na esquina.', 'Rua Augusta, 900', 'Consolação', -23.553000, -46.656000, 80, 'RESOLVIDO', SYSDATE - 45, NULL, SYSDATE - 10);
INSERT INTO tb_relato (cd_relato, cd_usuario, tp_obstaculo, tp_gravidade, ds_relato, ds_endereco, nm_bairro, vl_latitude, vl_longitude, vl_confianca_ia, st_relato, dt_criacao, dt_previsao_obra, dt_resolucao)
VALUES (12, 5, 'BURACO', 'BAIXA', 'Buraco pequeno perto da árvore.', 'Rua Cubatão, 300', 'Vila Mariana', -23.579000, -46.642000, 92, 'PUBLICADO', SYSDATE - 120, NULL, NULL);

-- Validacoes (o autor nunca vota no proprio relato; um voto por pessoa) ----
INSERT INTO tb_validacao (cd_validacao, cd_relato, cd_usuario, tp_voto, ds_comentario, dt_validacao) VALUES (1, 1, 3, 'AINDA_EXISTE', 'Continua lá.', SYSDATE - 11);
INSERT INTO tb_validacao (cd_validacao, cd_relato, cd_usuario, tp_voto, ds_comentario, dt_validacao) VALUES (2, 1, 4, 'AINDA_EXISTE', NULL, SYSDATE - 10);
INSERT INTO tb_validacao (cd_validacao, cd_relato, cd_usuario, tp_voto, ds_comentario, dt_validacao) VALUES (3, 1, 5, 'AINDA_EXISTE', NULL, SYSDATE - 9);
INSERT INTO tb_validacao (cd_validacao, cd_relato, cd_usuario, tp_voto, ds_comentario, dt_validacao) VALUES (4, 2, 2, 'AINDA_EXISTE', 'Não dá para descer com o andador.', SYSDATE - 18);
INSERT INTO tb_validacao (cd_validacao, cd_relato, cd_usuario, tp_voto, ds_comentario, dt_validacao) VALUES (5, 2, 4, 'AINDA_EXISTE', NULL, SYSDATE - 17);
INSERT INTO tb_validacao (cd_validacao, cd_relato, cd_usuario, tp_voto, ds_comentario, dt_validacao) VALUES (6, 2, 5, 'AINDA_EXISTE', NULL, SYSDATE - 15);
INSERT INTO tb_validacao (cd_validacao, cd_relato, cd_usuario, tp_voto, ds_comentario, dt_validacao) VALUES (7, 3, 2, 'AINDA_EXISTE', NULL, SYSDATE - 7);
INSERT INTO tb_validacao (cd_validacao, cd_relato, cd_usuario, tp_voto, ds_comentario, dt_validacao) VALUES (8, 5, 2, 'AINDA_EXISTE', NULL, SYSDATE - 24);
INSERT INTO tb_validacao (cd_validacao, cd_relato, cd_usuario, tp_voto, ds_comentario, dt_validacao) VALUES (9, 5, 3, 'AINDA_EXISTE', 'Impossível passar de cadeira.', SYSDATE - 23);
INSERT INTO tb_validacao (cd_validacao, cd_relato, cd_usuario, tp_voto, ds_comentario, dt_validacao) VALUES (10, 5, 4, 'AINDA_EXISTE', NULL, SYSDATE - 22);
INSERT INTO tb_validacao (cd_validacao, cd_relato, cd_usuario, tp_voto, ds_comentario, dt_validacao) VALUES (11, 6, 2, 'AINDA_EXISTE', NULL, SYSDATE - 5);
INSERT INTO tb_validacao (cd_validacao, cd_relato, cd_usuario, tp_voto, ds_comentario, dt_validacao) VALUES (12, 6, 5, 'AINDA_EXISTE', NULL, SYSDATE - 4);
INSERT INTO tb_validacao (cd_validacao, cd_relato, cd_usuario, tp_voto, ds_comentario, dt_validacao) VALUES (13, 7, 2, 'AINDA_EXISTE', NULL, SYSDATE - 14);
INSERT INTO tb_validacao (cd_validacao, cd_relato, cd_usuario, tp_voto, ds_comentario, dt_validacao) VALUES (14, 7, 3, 'AINDA_EXISTE', NULL, SYSDATE - 13);
INSERT INTO tb_validacao (cd_validacao, cd_relato, cd_usuario, tp_voto, ds_comentario, dt_validacao) VALUES (15, 7, 5, 'AINDA_EXISTE', 'Muito perigoso para quem não enxerga.', SYSDATE - 12);
INSERT INTO tb_validacao (cd_validacao, cd_relato, cd_usuario, tp_voto, ds_comentario, dt_validacao) VALUES (16, 8, 2, 'FOI_RESOLVIDO', 'Consertaram na semana passada.', SYSDATE - 5);
INSERT INTO tb_validacao (cd_validacao, cd_relato, cd_usuario, tp_voto, ds_comentario, dt_validacao) VALUES (17, 8, 3, 'FOI_RESOLVIDO', NULL, SYSDATE - 4);
INSERT INTO tb_validacao (cd_validacao, cd_relato, cd_usuario, tp_voto, ds_comentario, dt_validacao) VALUES (18, 8, 4, 'FOI_RESOLVIDO', NULL, SYSDATE - 3);

COMMIT;
