package com.kadiraksoy.todoapp.utils;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

public class FlywayUtil {
    public static void initTenantTables(DataSource dataSource, String schema) {
        Flyway.configure()
                .dataSource(dataSource)
                .locations("db/migration/tenants")
                .schemas(schema)
                .load()
                .migrate();
    }
}