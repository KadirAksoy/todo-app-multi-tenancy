    package com.kadiraksoy.todoapp.filter;

    import com.kadiraksoy.todoapp.tenant.TenantContext;
    import com.kadiraksoy.todoapp.tenant.TenantIdentifierResolver;
    import jakarta.servlet.*;
    import jakarta.servlet.http.HttpServletRequest;
    import java.io.IOException;

    public class TenantFilter implements Filter {

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String tenantId = httpRequest.getHeader("X-Tenant-ID");

            try {
                if (tenantId != null && !tenantId.isEmpty()) {
                    TenantContext.setTenant(tenantId);
                } else {
                    TenantContext.setTenant(TenantIdentifierResolver.DEFAULT_TENANT);
                }
                chain.doFilter(request, response);
            } finally {
                TenantContext.clearTenant();
            }
        }
    }
