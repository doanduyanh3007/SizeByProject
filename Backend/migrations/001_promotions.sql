-- Migration: Promotions tables
-- Run this script on ShopGiay database if tables do not exist yet.

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Promotions')
BEGIN
    CREATE TABLE [dbo].[Promotions](
        [id] [bigint] IDENTITY(1,1) NOT NULL,
        [name] [nvarchar](255) NOT NULL,
        [discount_percent] [decimal](5, 2) NOT NULL,
        [start_date] [datetimeoffset](7) NOT NULL,
        [end_date] [datetimeoffset](7) NOT NULL,
        [is_active] [bit] NULL DEFAULT 1,
        [created_at] [datetimeoffset](7) NULL,
        [updated_at] [datetimeoffset](7) NULL,
        PRIMARY KEY CLUSTERED ([id] ASC)
    );
END
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'PromotionProducts')
BEGIN
    CREATE TABLE [dbo].[PromotionProducts](
        [promotion_id] [bigint] NOT NULL,
        [product_id] [bigint] NOT NULL,
        PRIMARY KEY CLUSTERED ([promotion_id] ASC, [product_id] ASC),
        CONSTRAINT [FK_PromotionProducts_Promotions] FOREIGN KEY([promotion_id])
            REFERENCES [dbo].[Promotions] ([id]) ON DELETE CASCADE,
        CONSTRAINT [FK_PromotionProducts_Products] FOREIGN KEY([product_id])
            REFERENCES [dbo].[Products] ([id]) ON DELETE CASCADE
    );
END
GO
