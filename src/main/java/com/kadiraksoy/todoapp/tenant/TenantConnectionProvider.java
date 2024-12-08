package com.kadiraksoy.todoapp.tenant;

import lombok.RequiredArgsConstructor;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class TenantConnectionProvider implements MultiTenantConnectionProvider<String> {

    private final DataSource dataSource;

    // tenant için bağlantı sağlar
    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // Verilen bağlantıyı serbest bırakır ve kapatır.
    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    // (tenant) kimliği ile bir bağlantı alır.
    // Bu bağlantının şeması, tenantIdentifier ile belirtilen şemaya ayarlanır.
    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        final Connection connection = getAnyConnection();

        connection.setSchema(tenantIdentifier.toString());

        return connection;
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        connection.setSchema(TenantIdentifierResolver.DEFAULT_TENANT);

        releaseAnyConnection(connection);
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }

}
