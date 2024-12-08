package com.kadiraksoy.todoapp.tenant;

import lombok.RequiredArgsConstructor;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class TenantConnectionProvider implements MultiTenantConnectionProvider<Object> {

    private final DataSource dataSource;


    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }


    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(Object tenantIdentifier) throws SQLException {
        final Connection connection = getAnyConnection();

        connection.setSchema(tenantIdentifier.toString());

        return connection;
    }

    @Override
    public void releaseConnection(Object tenantIdentifier, Connection connection) throws SQLException {
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
