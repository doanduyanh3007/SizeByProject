-- Migration 003: Convert VARCHAR columns to NVARCHAR for Vietnamese character support
-- Run this script on ShopGiay database

IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[Brands]') AND name = 'name')
BEGIN
    ALTER TABLE [dbo].[Brands] ALTER COLUMN [name] NVARCHAR(255) NULL;
END
GO

IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[Categories]') AND name = 'name')
BEGIN
    ALTER TABLE [dbo].[Categories] ALTER COLUMN [name] NVARCHAR(255) NULL;
END
GO

IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[Colors]') AND name = 'name')
BEGIN
    ALTER TABLE [dbo].[Colors] ALTER COLUMN [name] NVARCHAR(255) NULL;
END
GO

IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[Sizes]') AND name = 'name')
BEGIN
    ALTER TABLE [dbo].[Sizes] ALTER COLUMN [name] NVARCHAR(255) NULL;
END
GO

IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[Orders]') AND name = 'fullname')
BEGIN
    ALTER TABLE [dbo].[Orders] ALTER COLUMN [fullname] NVARCHAR(255) NULL;
END
GO
