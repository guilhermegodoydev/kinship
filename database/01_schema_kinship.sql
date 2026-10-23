
USE kinship;
GO

IF OBJECT_ID('dbo.curtida_comentario', 'U') IS NOT NULL DROP TABLE dbo.curtida_comentario;
IF OBJECT_ID('dbo.curtida_post',       'U') IS NOT NULL DROP TABLE dbo.curtida_post;
IF OBJECT_ID('dbo.comentario',         'U') IS NOT NULL DROP TABLE dbo.comentario;
IF OBJECT_ID('dbo.post',               'U') IS NOT NULL DROP TABLE dbo.post;
IF OBJECT_ID('dbo.usuario',            'U') IS NOT NULL DROP TABLE dbo.usuario;
GO


CREATE TABLE dbo.usuario (
    codigo         INT IDENTITY(1,1) NOT NULL,
    nome           NVARCHAR(100)     NOT NULL,
    nickname       NVARCHAR(30)      NOT NULL,
    email          NVARCHAR(150)     NOT NULL,
    senha          VARCHAR(255)      NOT NULL,   
    biografia      NVARCHAR(300)     NULL,       
    foto_perfil    NVARCHAR(255)     NULL,       
    data_cadastro  DATETIME2         NOT NULL CONSTRAINT df_usuario_data_cadastro DEFAULT SYSDATETIME(),
    CONSTRAINT pk_usuario          PRIMARY KEY (codigo),
    CONSTRAINT uq_usuario_nickname UNIQUE (nickname),
    CONSTRAINT uq_usuario_email    UNIQUE (email)
);
GO

CREATE TABLE dbo.post (
    codigo           INT IDENTITY(1,1) NOT NULL,
    conteudo         NVARCHAR(MAX)     NOT NULL,
    data_publicacao  DATETIME2         NOT NULL CONSTRAINT df_post_data_publicacao DEFAULT SYSDATETIME(),
    codigo_usuario   INT               NOT NULL,
    CONSTRAINT pk_post PRIMARY KEY (codigo),
    
    CONSTRAINT fk_post_usuario FOREIGN KEY (codigo_usuario)
        REFERENCES dbo.usuario (codigo) ON DELETE CASCADE
);
GO

CREATE TABLE dbo.comentario (
    codigo          INT IDENTITY(1,1) NOT NULL,
    texto           NVARCHAR(500)     NOT NULL,
    data_hora       DATETIME2         NOT NULL CONSTRAINT df_comentario_data_hora DEFAULT SYSDATETIME(),
    codigo_post     INT               NOT NULL,
    codigo_usuario  INT               NOT NULL,
    CONSTRAINT pk_comentario PRIMARY KEY (codigo),
    
    CONSTRAINT fk_comentario_post FOREIGN KEY (codigo_post)
        REFERENCES dbo.post (codigo) ON DELETE CASCADE,
    
    CONSTRAINT fk_comentario_usuario FOREIGN KEY (codigo_usuario)
        REFERENCES dbo.usuario (codigo) ON DELETE NO ACTION
);
GO

CREATE TABLE dbo.curtida_post (
    codigo_usuario  INT NOT NULL,
    codigo_post     INT NOT NULL,
    
    CONSTRAINT pk_curtida_post PRIMARY KEY (codigo_usuario, codigo_post),
    CONSTRAINT fk_curtida_post_usuario FOREIGN KEY (codigo_usuario)
        REFERENCES dbo.usuario (codigo) ON DELETE NO ACTION,   
    CONSTRAINT fk_curtida_post_post FOREIGN KEY (codigo_post)
        REFERENCES dbo.post (codigo) ON DELETE CASCADE
);
GO

CREATE TABLE dbo.curtida_comentario (
    codigo_usuario    INT NOT NULL,
    codigo_comentario INT NOT NULL,
    CONSTRAINT pk_curtida_comentario PRIMARY KEY (codigo_usuario, codigo_comentario),
    CONSTRAINT fk_curtida_comentario_usuario FOREIGN KEY (codigo_usuario)
        REFERENCES dbo.usuario (codigo) ON DELETE NO ACTION,   
    CONSTRAINT fk_curtida_comentario_comentario FOREIGN KEY (codigo_comentario)
        REFERENCES dbo.comentario (codigo) ON DELETE CASCADE
);
GO


CREATE INDEX ix_post_codigo_usuario                  ON dbo.post (codigo_usuario);
CREATE INDEX ix_comentario_codigo_post               ON dbo.comentario (codigo_post);
CREATE INDEX ix_comentario_codigo_usuario            ON dbo.comentario (codigo_usuario);
CREATE INDEX ix_curtida_post_codigo_post             ON dbo.curtida_post (codigo_post);
CREATE INDEX ix_curtida_comentario_codigo_comentario ON dbo.curtida_comentario (codigo_comentario);
GO