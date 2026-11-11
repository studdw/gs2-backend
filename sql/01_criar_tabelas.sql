-- =====================================================================
-- Rota para Todos - Global Solution 2026/2 (CitizenTech)
-- Script de criacao das tabelas e sequences no Oracle da FIAP.
-- Padrao da disciplina: tb_nome, colunas cd_, nm_, ds_, dt_, tp_, st_, vl_, nr_
-- e sequence sq_tb_nome.
--
-- Como usar: abra no Oracle SQL Developer, conecte com o seu usuario
-- e execute o script inteiro (F5). Depois rode o 02_dados_iniciais.sql.
-- =====================================================================

-- Para recriar tudo do zero, descomente e execute o bloco abaixo antes:
-- DROP TABLE tb_validacao CASCADE CONSTRAINTS;
-- DROP TABLE tb_relato CASCADE CONSTRAINTS;
-- DROP TABLE tb_usuario CASCADE CONSTRAINTS;
-- DROP SEQUENCE sq_tb_validacao;
-- DROP SEQUENCE sq_tb_relato;
-- DROP SEQUENCE sq_tb_usuario;

-- ---------------------------------------------------------------------
-- tb_usuario: cidadaos e gestores publicos
-- ---------------------------------------------------------------------
CREATE TABLE tb_usuario (
    cd_usuario            NUMBER(10)     NOT NULL,
    nm_usuario            VARCHAR2(100)  NOT NULL,
    ds_email              VARCHAR2(150)  NOT NULL,
    ds_senha              VARCHAR2(100)  NOT NULL,
    tp_papel              VARCHAR2(10)   DEFAULT 'CIDADAO' NOT NULL,
    tp_perfil_mobilidade  VARCHAR2(20),
    dt_cadastro           TIMESTAMP      NOT NULL,
    CONSTRAINT pk_tb_usuario PRIMARY KEY (cd_usuario),
    CONSTRAINT uk_tb_usuario_email UNIQUE (ds_email),
    CONSTRAINT ck_tb_usuario_papel CHECK (tp_papel IN ('CIDADAO', 'GESTOR')),
    CONSTRAINT ck_tb_usuario_perfil CHECK (tp_perfil_mobilidade IN
        ('CADEIRANTE', 'IDOSO', 'GESTANTE', 'CARRINHO_BEBE', 'DEFICIENCIA_VISUAL', 'MULETA_ANDADOR'))
);

COMMENT ON TABLE tb_usuario IS 'Pessoas que usam a plataforma: cidadaos e gestores publicos';
COMMENT ON COLUMN tb_usuario.ds_senha IS 'Hash BCrypt da senha (nunca a senha em texto puro)';
COMMENT ON COLUMN tb_usuario.tp_perfil_mobilidade IS 'Perfil de mobilidade usado no calculo das rotas';

CREATE SEQUENCE sq_tb_usuario START WITH 100 INCREMENT BY 1 NOCACHE;

-- ---------------------------------------------------------------------
-- tb_relato: obstaculos reportados nas calcadas (1 usuario : N relatos)
-- ---------------------------------------------------------------------
CREATE TABLE tb_relato (
    cd_relato         NUMBER(10)     NOT NULL,
    cd_usuario        NUMBER(10)     NOT NULL,
    tp_obstaculo      VARCHAR2(30)   NOT NULL,
    tp_gravidade      VARCHAR2(10)   NOT NULL,
    ds_relato         VARCHAR2(500),
    ds_endereco       VARCHAR2(200)  NOT NULL,
    nm_bairro         VARCHAR2(80)   NOT NULL,
    vl_latitude       NUMBER(9,6)    NOT NULL,
    vl_longitude      NUMBER(9,6)    NOT NULL,
    vl_confianca_ia   NUMBER(3)      NOT NULL,
    st_relato         VARCHAR2(15)   NOT NULL,
    ds_foto           CLOB,
    dt_criacao        TIMESTAMP      NOT NULL,
    dt_previsao_obra  DATE,
    dt_resolucao      TIMESTAMP,
    CONSTRAINT pk_tb_relato PRIMARY KEY (cd_relato),
    CONSTRAINT fk_tb_relato_usuario FOREIGN KEY (cd_usuario) REFERENCES tb_usuario (cd_usuario),
    CONSTRAINT ck_tb_relato_tipo CHECK (tp_obstaculo IN
        ('BURACO', 'DEGRAU', 'SEM_RAMPA', 'CALCADA_IRREGULAR', 'OBSTRUCAO',
         'ILUMINACAO_RUIM', 'LOCAL_ISOLADO', 'TRAVESSIA_INSEGURA')),
    CONSTRAINT ck_tb_relato_gravidade CHECK (tp_gravidade IN ('BAIXA', 'MEDIA', 'ALTA', 'CRITICA')),
    CONSTRAINT ck_tb_relato_status CHECK (st_relato IN
        ('EM_MODERACAO', 'PUBLICADO', 'CONFIRMADO', 'EM_OBRA', 'RESOLVIDO', 'REJEITADO')),
    CONSTRAINT ck_tb_relato_confianca CHECK (vl_confianca_ia BETWEEN 0 AND 100)
);

COMMENT ON TABLE tb_relato IS 'Relatos de obstaculos nas calcadas, feitos pelos cidadaos';
-- O protocolo mostrado ao cidadao (ex.: RT-2026-00042) e montado pela API a partir do ano e do cd_relato.
COMMENT ON COLUMN tb_relato.vl_confianca_ia IS 'Confianca (0 a 100) da classificacao automatica; abaixo de 70 vai para moderacao';
COMMENT ON COLUMN tb_relato.ds_foto IS 'Foto do obstaculo em data URL (base64), ja comprimida pelo front';

CREATE SEQUENCE sq_tb_relato START WITH 100 INCREMENT BY 1 NOCACHE;

-- ---------------------------------------------------------------------
-- tb_validacao: votos da comunidade sobre um relato (1 relato : N validacoes)
-- ---------------------------------------------------------------------
CREATE TABLE tb_validacao (
    cd_validacao   NUMBER(10)     NOT NULL,
    cd_relato      NUMBER(10)     NOT NULL,
    cd_usuario     NUMBER(10)     NOT NULL,
    tp_voto        VARCHAR2(15)   NOT NULL,
    ds_comentario  VARCHAR2(200),
    dt_validacao   TIMESTAMP      NOT NULL,
    CONSTRAINT pk_tb_validacao PRIMARY KEY (cd_validacao),
    CONSTRAINT fk_tb_validacao_relato FOREIGN KEY (cd_relato) REFERENCES tb_relato (cd_relato),
    CONSTRAINT fk_tb_validacao_usuario FOREIGN KEY (cd_usuario) REFERENCES tb_usuario (cd_usuario),
    CONSTRAINT uk_tb_validacao_voto UNIQUE (cd_relato, cd_usuario),
    CONSTRAINT ck_tb_validacao_voto CHECK (tp_voto IN ('AINDA_EXISTE', 'FOI_RESOLVIDO', 'NAO_ENCONTREI'))
);

COMMENT ON TABLE tb_validacao IS 'Votos da comunidade confirmando se o obstaculo ainda existe ou foi resolvido';

CREATE SEQUENCE sq_tb_validacao START WITH 100 INCREMENT BY 1 NOCACHE;

COMMIT;
