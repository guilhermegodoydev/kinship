IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'kinship')
BEGIN
    CREATE DATABASE kinship;
END
GO

USE kinship;
GO

IF NOT EXISTS (SELECT * FROM sys.server_principals WHERE name = 'kinship_app')
BEGIN
    CREATE LOGIN kinship_app WITH PASSWORD = 'Kinship@APS2026';
END
GO

IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'kinship_app')
BEGIN
    CREATE USER kinship_app FOR LOGIN kinship_app;
END
GO

ALTER ROLE db_owner ADD MEMBER kinship_app;
GO
