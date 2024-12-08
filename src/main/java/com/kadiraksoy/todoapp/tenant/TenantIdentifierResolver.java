package com.kadiraksoy.todoapp.tenant;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<String> {
    // Amaç: Tenant bilgisi bulunamazsa veya belirtilmezse kullanılacak varsayılan tenant'ı
    // (örneğin, şema adı) belirtir.
    // Bu Koddaki Ayar: Varsayılan tenant public şemasıdır.
    public static final String DEFAULT_TENANT = "public";


    // Hibernate'in işlem sırasında hangi tenant'ı (örneğin şema) kullanacağını belirler.

    // Nasıl Çalışır?
    // TenantContext.getCurrentTenant() çağrılarak iş parçacığına atanmış tenant bilgisi alınır.
    // Eğer tenantId boş değilse, bu ID döndürülür (kullanıcıya özel tenant şeması).
    // Eğer tenantId boş veya null ise, varsayılan tenant (örneğin public) döndürülür.
    // Önemi: İşlemin hangi tenant üzerinde gerçekleşeceğini dinamik olarak belirler.
    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId != null && !tenantId.isEmpty()) {
            return tenantId;
        }
        return DEFAULT_TENANT;
    }

    // Hibernate, mevcut bir oturumu (session) kullanmadan önce tenant bilgisini doğrulayıp doğrulamayacağını belirler.
    // false olarak ayarlandığı için mevcut oturumlarda tenant bilgisi doğrulanmaz.
    // Bu, performansı artırabilir ancak daha az güvenli bir yapı olabilir.
    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}