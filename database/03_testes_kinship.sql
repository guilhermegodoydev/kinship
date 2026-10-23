USE kinship;
GO

SET NOCOUNT ON;

CREATE TABLE #res (
    id       INT IDENTITY(1,1),
    teste    NVARCHAR(200),
    esperado VARCHAR(80),
    obtido   VARCHAR(80),
    status   VARCHAR(10)
);

DECLARE @a INT, @b INT, @c INT;
DECLARE @pA INT, @pB INT, @pC INT;
DECLARE @c1 INT, @c2 INT, @c3 INT, @c4 INT;
DECLARE @n INT, @obt VARCHAR(80);


DELETE FROM dbo.usuario WHERE email LIKE '%@teste.kinship';


INSERT INTO dbo.usuario (nome, nickname, email, senha) VALUES (N'Teste A', N'teste_a', N'a@teste.kinship', 'hash_teste'); SET @a = SCOPE_IDENTITY();
INSERT INTO dbo.usuario (nome, nickname, email, senha) VALUES (N'Teste B', N'teste_b', N'b@teste.kinship', 'hash_teste'); SET @b = SCOPE_IDENTITY();
INSERT INTO dbo.usuario (nome, nickname, email, senha) VALUES (N'Teste C', N'teste_c', N'c@teste.kinship', 'hash_teste'); SET @c = SCOPE_IDENTITY();


INSERT INTO dbo.post (conteudo, codigo_usuario) VALUES (N'Post de A', @a); SET @pA = SCOPE_IDENTITY();
INSERT INTO dbo.post (conteudo, codigo_usuario) VALUES (N'Post de B', @b); SET @pB = SCOPE_IDENTITY();
INSERT INTO dbo.post (conteudo, codigo_usuario) VALUES (N'Post de C', @c); SET @pC = SCOPE_IDENTITY();


INSERT INTO dbo.comentario (texto, codigo_post, codigo_usuario) VALUES (N'A comenta no post de B', @pB, @a); SET @c1 = SCOPE_IDENTITY();
INSERT INTO dbo.comentario (texto, codigo_post, codigo_usuario) VALUES (N'B comenta no post de A', @pA, @b); SET @c2 = SCOPE_IDENTITY();
INSERT INTO dbo.comentario (texto, codigo_post, codigo_usuario) VALUES (N'C comenta no post de B', @pB, @c); SET @c3 = SCOPE_IDENTITY();
INSERT INTO dbo.comentario (texto, codigo_post, codigo_usuario) VALUES (N'A comenta no post de C', @pC, @a); SET @c4 = SCOPE_IDENTITY();


INSERT INTO dbo.curtida_post (codigo_usuario, codigo_post) VALUES (@b, @pA), (@c, @pA), (@a, @pB), (@a, @pC), (@b, @pC);


INSERT INTO dbo.curtida_comentario (codigo_usuario, codigo_comentario) VALUES (@b, @c1), (@a, @c2), (@c, @c3), (@b, @c4), (@c, @c4);


SELECT @n = COUNT(*) FROM dbo.usuario WHERE codigo = @a AND biografia IS NULL AND foto_perfil IS NULL;
INSERT INTO #res VALUES (N'Usuario sem biografia e sem foto e aceito', '1', CAST(@n AS VARCHAR(10)), CASE WHEN @n = 1 THEN 'OK' ELSE 'FALHOU' END);

SELECT @n = COUNT(*) FROM dbo.usuario WHERE codigo = @a AND data_cadastro IS NOT NULL;
INSERT INTO #res VALUES (N'data_cadastro preenchida automaticamente', '1', CAST(@n AS VARCHAR(10)), CASE WHEN @n = 1 THEN 'OK' ELSE 'FALHOU' END);

SELECT @n = COUNT(*) FROM dbo.post WHERE codigo = @pA AND data_publicacao IS NOT NULL;
INSERT INTO #res VALUES (N'data_publicacao preenchida automaticamente', '1', CAST(@n AS VARCHAR(10)), CASE WHEN @n = 1 THEN 'OK' ELSE 'FALHOU' END);

SELECT @n = COUNT(*) FROM dbo.comentario WHERE codigo = @c1 AND data_hora IS NOT NULL;
INSERT INTO #res VALUES (N'data_hora do comentario preenchida automaticamente', '1', CAST(@n AS VARCHAR(10)), CASE WHEN @n = 1 THEN 'OK' ELSE 'FALHOU' END);


BEGIN TRY
    INSERT INTO dbo.usuario (nome, nickname, email, senha) VALUES (N'Dup', N'teste_a', N'dup1@teste.kinship', 'x');
    INSERT INTO #res VALUES (N'Nickname repetido e recusado', 'erro 2627', 'NAO deu erro', 'FALHOU');
END TRY
BEGIN CATCH
    INSERT INTO #res VALUES (N'Nickname repetido e recusado', 'erro 2627', CONCAT('erro ', ERROR_NUMBER()), CASE WHEN ERROR_NUMBER() IN (2627, 2601) THEN 'OK' ELSE 'FALHOU' END);
END CATCH


BEGIN TRY
    INSERT INTO dbo.usuario (nome, nickname, email, senha) VALUES (N'Dup', N'teste_dup2', N'a@teste.kinship', 'x');
    INSERT INTO #res VALUES (N'E-mail repetido e recusado', 'erro 2627', 'NAO deu erro', 'FALHOU');
END TRY
BEGIN CATCH
    INSERT INTO #res VALUES (N'E-mail repetido e recusado', 'erro 2627', CONCAT('erro ', ERROR_NUMBER()), CASE WHEN ERROR_NUMBER() IN (2627, 2601) THEN 'OK' ELSE 'FALHOU' END);
END CATCH


BEGIN TRY
    INSERT INTO dbo.usuario (nome, nickname, email, senha) VALUES (NULL, N'teste_nulo', N'nulo@teste.kinship', 'x');
    INSERT INTO #res VALUES (N'Usuario sem nome e recusado', 'erro 515', 'NAO deu erro', 'FALHOU');
END TRY
BEGIN CATCH
    INSERT INTO #res VALUES (N'Usuario sem nome e recusado', 'erro 515', CONCAT('erro ', ERROR_NUMBER()), CASE WHEN ERROR_NUMBER() = 515 THEN 'OK' ELSE 'FALHOU' END);
END CATCH


BEGIN TRY
    INSERT INTO dbo.curtida_post (codigo_usuario, codigo_post) VALUES (@b, @pA);
    INSERT INTO #res VALUES (N'Curtir o mesmo post 2 vezes e recusado', 'erro 2627', 'NAO deu erro', 'FALHOU');
END TRY
BEGIN CATCH
    INSERT INTO #res VALUES (N'Curtir o mesmo post 2 vezes e recusado', 'erro 2627', CONCAT('erro ', ERROR_NUMBER()), CASE WHEN ERROR_NUMBER() = 2627 THEN 'OK' ELSE 'FALHOU' END);
END CATCH


BEGIN TRY
    INSERT INTO dbo.curtida_comentario (codigo_usuario, codigo_comentario) VALUES (@b, @c1);
    INSERT INTO #res VALUES (N'Curtir o mesmo comentario 2 vezes e recusado', 'erro 2627', 'NAO deu erro', 'FALHOU');
END TRY
BEGIN CATCH
    INSERT INTO #res VALUES (N'Curtir o mesmo comentario 2 vezes e recusado', 'erro 2627', CONCAT('erro ', ERROR_NUMBER()), CASE WHEN ERROR_NUMBER() = 2627 THEN 'OK' ELSE 'FALHOU' END);
END CATCH


BEGIN TRY
    INSERT INTO dbo.post (conteudo, codigo_usuario) VALUES (N'Post fantasma', -1);
    INSERT INTO #res VALUES (N'Post de usuario inexistente e recusado', 'erro 547', 'NAO deu erro', 'FALHOU');
END TRY
BEGIN CATCH
    INSERT INTO #res VALUES (N'Post de usuario inexistente e recusado', 'erro 547', CONCAT('erro ', ERROR_NUMBER()), CASE WHEN ERROR_NUMBER() = 547 THEN 'OK' ELSE 'FALHOU' END);
END CATCH


SET @obt = CONCAT(
    'A=', (SELECT COUNT(*) FROM dbo.curtida_post WHERE codigo_post = @pA), '/', (SELECT COUNT(*) FROM dbo.comentario WHERE codigo_post = @pA),
    ' B=', (SELECT COUNT(*) FROM dbo.curtida_post WHERE codigo_post = @pB), '/', (SELECT COUNT(*) FROM dbo.comentario WHERE codigo_post = @pB),
    ' C=', (SELECT COUNT(*) FROM dbo.curtida_post WHERE codigo_post = @pC), '/', (SELECT COUNT(*) FROM dbo.comentario WHERE codigo_post = @pC));
INSERT INTO #res VALUES (N'COUNT de curtidas/comentarios por post', 'A=2/1 B=1/2 C=2/1', @obt, CASE WHEN @obt = 'A=2/1 B=1/2 C=2/1' THEN 'OK' ELSE 'FALHOU' END);


BEGIN TRY
    DELETE FROM dbo.post WHERE codigo = @pB;
END TRY
BEGIN CATCH
    INSERT INTO #res VALUES (N'Excluir post', 'sem erro', CONCAT('erro ', ERROR_NUMBER(), ': ', LEFT(ERROR_MESSAGE(), 50)), 'FALHOU');
END CATCH

SELECT @n = COUNT(*) FROM dbo.comentario WHERE codigo IN (@c1, @c2, @c3, @c4);
INSERT INTO #res VALUES (N'Apagar post: comentarios dele somem (sobram c2 e c4)', '2', CAST(@n AS VARCHAR(10)), CASE WHEN @n = 2 THEN 'OK' ELSE 'FALHOU' END);

SELECT @n = COUNT(*) FROM dbo.curtida_post WHERE codigo_post IN (@pA, @pB, @pC);
INSERT INTO #res VALUES (N'Apagar post: curtidas dele somem (sobram 4)', '4', CAST(@n AS VARCHAR(10)), CASE WHEN @n = 4 THEN 'OK' ELSE 'FALHOU' END);

SELECT @n = COUNT(*) FROM dbo.curtida_comentario WHERE codigo_comentario IN (@c1, @c2, @c3, @c4);
INSERT INTO #res VALUES (N'Apagar post: curtidas dos comentarios dele somem (sobram 3)', '3', CAST(@n AS VARCHAR(10)), CASE WHEN @n = 3 THEN 'OK' ELSE 'FALHOU' END);


BEGIN TRY
    DELETE FROM dbo.usuario WHERE codigo = @a;
END TRY
BEGIN CATCH
    INSERT INTO #res VALUES (N'Excluir usuario (trigger)', 'sem erro', CONCAT('erro ', ERROR_NUMBER(), ': ', LEFT(ERROR_MESSAGE(), 50)), 'FALHOU');
END CATCH

SELECT @n = COUNT(*) FROM dbo.usuario WHERE codigo = @a;
INSERT INTO #res VALUES (N'Apagar usuario: ele some', '0', CAST(@n AS VARCHAR(10)), CASE WHEN @n = 0 THEN 'OK' ELSE 'FALHOU' END);

SELECT @n = COUNT(*) FROM dbo.usuario WHERE codigo IN (@b, @c);
INSERT INTO #res VALUES (N'Apagar usuario: os outros usuarios continuam', '2', CAST(@n AS VARCHAR(10)), CASE WHEN @n = 2 THEN 'OK' ELSE 'FALHOU' END);

SELECT @n = COUNT(*) FROM dbo.post WHERE codigo IN (@pA, @pB, @pC);
INSERT INTO #res VALUES (N'Apagar usuario: posts dele somem (sobra so pC)', '1', CAST(@n AS VARCHAR(10)), CASE WHEN @n = 1 THEN 'OK' ELSE 'FALHOU' END);

SELECT @n = COUNT(*) FROM dbo.comentario WHERE codigo IN (@c1, @c2, @c3, @c4);
INSERT INTO #res VALUES (N'Apagar usuario: comentarios dele (inclusive em posts de outros) somem', '0', CAST(@n AS VARCHAR(10)), CASE WHEN @n = 0 THEN 'OK' ELSE 'FALHOU' END);

SELECT @n = COUNT(*) FROM dbo.curtida_post WHERE codigo_post IN (@pA, @pB, @pC);
INSERT INTO #res VALUES (N'Apagar usuario: curtidas feitas e recebidas por ele somem (sobra B->pC)', '1', CAST(@n AS VARCHAR(10)), CASE WHEN @n = 1 THEN 'OK' ELSE 'FALHOU' END);

SELECT @n = COUNT(*) FROM dbo.curtida_comentario WHERE codigo_comentario IN (@c1, @c2, @c3, @c4);
INSERT INTO #res VALUES (N'Apagar usuario: curtidas em comentarios somem', '0', CAST(@n AS VARCHAR(10)), CASE WHEN @n = 0 THEN 'OK' ELSE 'FALHOU' END);


DELETE FROM dbo.usuario WHERE email LIKE '%@teste.kinship';

SELECT @n = COUNT(*) FROM dbo.usuario WHERE email LIKE '%@teste.kinship';
INSERT INTO #res VALUES (N'Limpeza final: nenhum dado de teste sobra', '0', CAST(@n AS VARCHAR(10)), CASE WHEN @n = 0 THEN 'OK' ELSE 'FALHOU' END);


SELECT id, teste, esperado, obtido, status FROM #res ORDER BY id;
SELECT status, COUNT(*) AS quantidade FROM #res GROUP BY status;

DROP TABLE #res;
GO