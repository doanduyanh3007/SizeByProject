-- Migration: UserAddresses table for multi-address management
-- Run this script on ShopGiay database if table does not exist yet.

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'UserAddresses')
BEGIN
    CREATE TABLE [dbo].[UserAddresses](
        [id] [int] IDENTITY(1,1) NOT NULL,
        [account_id] [int] NOT NULL,
        [label] [nvarchar](100) NULL,
        [fullname] [nvarchar](255) NULL,
        [phone] [nvarchar](50) NULL,
        [street_address] [nvarchar](500) NULL,
        [ward] [nvarchar](255) NULL,
        [district] [nvarchar](255) NULL,
        [province] [nvarchar](255) NULL,
        [full_address] [nvarchar](max) NOT NULL,
        [is_default] [bit] NOT NULL DEFAULT 0,
        [created_at] [datetimeoffset](7) NULL DEFAULT SYSDATETIMEOFFSET(),
        PRIMARY KEY CLUSTERED ([id] ASC),
        CONSTRAINT [FK_UserAddresses_Accounts] FOREIGN KEY([account_id])
            REFERENCES [dbo].[Accounts] ([id]) ON DELETE CASCADE
    );

    CREATE INDEX [IX_UserAddresses_AccountId] ON [dbo].[UserAddresses]([account_id]);
END
GO

-- Migrate existing Accounts.address into UserAddresses (one default per account)
INSERT INTO [dbo].[UserAddresses] (
    [account_id], [label], [fullname], [phone],
    [street_address], [ward], [district], [province],
    [full_address], [is_default], [created_at]
)
SELECT
    a.[id],
    N'Địa chỉ mặc định',
    a.[username],
    a.[phone],
    NULL, NULL, NULL, NULL,
    a.[address],
    1,
    SYSDATETIMEOFFSET()
FROM [dbo].[Accounts] a
WHERE a.[address] IS NOT NULL
  AND LTRIM(RTRIM(a.[address])) <> ''
  AND NOT EXISTS (
      SELECT 1 FROM [dbo].[UserAddresses] ua WHERE ua.[account_id] = a.[id]
  );
GO
