package com.finalproject2.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseUnicodeMigration implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseUnicodeMigration.class);
    private final JdbcTemplate jdbcTemplate;

    public DatabaseUnicodeMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        String[] alterStatements = new String[]{
                "IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[Brands]') AND name = 'name') " +
                        "ALTER TABLE [dbo].[Brands] ALTER COLUMN [name] NVARCHAR(255) NULL",
                "IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[Categories]') AND name = 'name') " +
                        "ALTER TABLE [dbo].[Categories] ALTER COLUMN [name] NVARCHAR(255) NULL",
                "IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[Colors]') AND name = 'name') " +
                        "ALTER TABLE [dbo].[Colors] ALTER COLUMN [name] NVARCHAR(255) NULL",
                "IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[Sizes]') AND name = 'name') " +
                        "ALTER TABLE [dbo].[Sizes] ALTER COLUMN [name] NVARCHAR(255) NULL",
                "IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[Orders]') AND name = 'fullname') " +
                        "ALTER TABLE [dbo].[Orders] ALTER COLUMN [fullname] NVARCHAR(255) NULL"
        };

        for (String sql : alterStatements) {
            try {
                jdbcTemplate.execute(sql);
            } catch (Exception e) {
                log.warn("Unicode schema migration check skipped or failed for statement: {}", sql, e);
            }
        }
    }
}
