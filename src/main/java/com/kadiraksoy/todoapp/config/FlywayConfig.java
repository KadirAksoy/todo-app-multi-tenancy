package com.kadiraksoy.todoapp.config;

import com.kadiraksoy.todoapp.repository.IUserRepository;
import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayConfig {

    // Varsayılan Flyway yapılandırması
    @Bean
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .locations("db/migration/default") // Varsayılan migration dosyaları
                .dataSource(dataSource)
                .schemas("public") // Varsayılan şema
                .ignoreMigrationPatterns("ıgnored")
                .load();

        flyway.migrate(); // Varsayılan migration işlemi
        return flyway;
    }

    // Kullanıcılar için her biri ayrı şemada migration işlemi yapan CommandLineRunner
    @Bean
    public CommandLineRunner commandLineRunner(IUserRepository userRepository, DataSource dataSource) {
        return args -> {
            // Kullanıcıları al
            userRepository.findAll().forEach(user -> {
                String tenant = user.getUsername().toLowerCase(); // Tenant şeması olarak kullanıcının name'ini kullan
                // Tenant'a özel Flyway yapılandırması
                Flyway flyway = Flyway.configure()
                        .locations("db/migration/tenants") // Tenant'a özel migration dosyaları
                        .dataSource(dataSource)
                        .schemas(tenant) // Tenant'a özel şema adı
                        .load();

                // Tenant'a özel migration işlemi uygula
                flyway.migrate();
            });
        };
    }
}
