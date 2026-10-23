USE kinship;
GO

IF OBJECT_ID('dbo.trg_usuario_excluir', 'TR') IS NOT NULL
    DROP TRIGGER dbo.trg_usuario_excluir;
GO


CREATE TRIGGER dbo.trg_usuario_excluir
ON dbo.usuario
INSTEAD OF DELETE
AS
BEGIN
    SET NOCOUNT ON;

    DELETE FROM dbo.curtida_post
    WHERE codigo_usuario IN (SELECT codigo FROM deleted);

    DELETE FROM dbo.curtida_comentario
    WHERE codigo_usuario IN (SELECT codigo FROM deleted);

    DELETE FROM dbo.comentario
    WHERE codigo_usuario IN (SELECT codigo FROM deleted);

    DELETE FROM dbo.usuario
    WHERE codigo IN (SELECT codigo FROM deleted);
END
GO