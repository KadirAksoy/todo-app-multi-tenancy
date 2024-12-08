package com.kadiraksoy.todoapp.config;

import com.kadiraksoy.todoapp.repository.IUserRepository;
import com.kadiraksoy.todoapp.tenant.TenantIdentifierResolver;
import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayConfig {

    @Bean
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .locations("db/migration/default")
                .dataSource(dataSource)
                .schemas(TenantIdentifierResolver.DEFAULT_TENANT)
                .load();

        flyway.migrate();
        return flyway;
    }

    @Bean
    public CommandLineRunner commandLineRunner(IUserRepository userRepository, DataSource dataSource) {
        return args -> {
            userRepository.findAll().forEach(user -> {
                String tenant = user.getUsername().toLowerCase();
                Flyway flyway = Flyway.configure()
                        .locations("db/migration/tenants")
                        .dataSource(dataSource)
                        .schemas(tenant)
                        .load();

                flyway.migrate();
            });
        };
    }
}
