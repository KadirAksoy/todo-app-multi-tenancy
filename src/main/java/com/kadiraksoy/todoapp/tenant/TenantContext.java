package com.kadiraksoy.todoapp.tenant;


public class TenantContext {

    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();


    public static String getCurrentTenant() {
        return currentTenant.get();
    }


    public static void setTenant(String tenant) {
        currentTenant.set(tenant);
    }

    public static void clearTenant() {
        currentTenant.remove();
    }
}
