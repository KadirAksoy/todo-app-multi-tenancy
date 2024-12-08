package com.kadiraksoy.todoapp.config;


import com.kadiraksoy.todoapp.TodoAppMultiTenancyApplication;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class HibernateConfig {

    @Bean
    JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory(
            // Veritabanı bağlantısı için kullanılan temel yapı.
            // Burada, Hibernate multi-tenancy mekanizması bu bağlantıyı alır
            // ve her tenant için farklı bir şemada işlem yapar.
            DataSource dataSource,
            // Spring Boot'un otomatik olarak yapılandırdığı JPA özelliklerini taşır.
            // Örneğin: hibernate.dialect, hibernate.hbm2ddl.auto gibi Hibernate'e özel ayarları içerebilir.
            JpaProperties jpaProperties,
            // // Hibernate'in çok kiracılı bağlantı yönetimini sağlar.
            // Hangi şema için hangi bağlantının kullanılacağını belirler.
            MultiTenantConnectionProvider<String> multiTenantConnectionProviderImpl,
            // Şu anda aktif olan tenant'ı (örneğin, hangi şemanın kullanılacağını) belirler.
            CurrentTenantIdentifierResolver<String> currentTenantIdentifierResolverImpl
    ) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan(TodoAppMultiTenancyApplication.class.getPackage().getName());
        em.setJpaVendorAdapter(this.jpaVendorAdapter());

        // hibernate.multiTenancy: Çok kiracılı yapıyı etkinleştirir. Burada SCHEMA seçeneği, şema bazlı bir yaklaşımı ifade eder.
        // Environment.MULTI_TENANT_CONNECTION_PROVIDER: Şema geçişlerini yöneten MultiTenantConnectionProvider'ı tanımlar.
        // Environment.MULTI_TENANT_IDENTIFIER_RESOLVER: Aktif tenant bilgisini belirlemek için kullanılan sınıfı tanımlar.
        Map<String, Object> jpaPropertiesMap = new HashMap<>(jpaProperties.getProperties());
        jpaPropertiesMap.put("hibernate.multiTenancy", "SCHEMA");
        jpaPropertiesMap.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProviderImpl);
        jpaPropertiesMap.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolverImpl);

        // Hibernate'in çalışması için gerekli olan ayarları geçirir.
        em.setJpaPropertyMap(jpaPropertiesMap);
        return em;
    }

    // Çok kiracılı (multi-tenant) yapıyı Hibernate üzerinden etkinleştirir.
    // Tenant başına ayrı bir şema kullanılmasını sağlar.
    // Hangi şemanın kullanılacağını belirlemek için
    // CurrentTenantIdentifierResolver ve MultiTenantConnectionProvider yapılarını entegre eder.
}