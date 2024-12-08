package com.kadiraksoy.todoapp.tenant;


//TenantContext sınıfı, her API isteği sırasında, o isteğe özel bir tenant bilgisi sağlar.
//Bu tenant bilgisi genellikle bir şema adı, müşteri ID'si veya organizasyon adı olabilir.
//Kullanıcıya veya isteğe özel tenant bilgilerini güvenli ve eşzamanlı bir şekilde saklamak için ThreadLocal kullanılır.
//ThreadLocal, her iş parçacığının kendi değerine sahip olmasını sağlar
// ve bu değer diğer iş parçacıkları tarafından paylaşılamaz.
// Bu, çoklu kullanıcıların aynı anda uygulamayı kullanırken tenant bilgilerinin karışmasını önler.
public class TenantContext {

    // Neden ThreadLocal Kullanılıyor?
    // İzole Veri Saklama:
    // Her iş parçacığının kendi tenant bilgisine sahip olmasını sağlar.
    // Diğer iş parçacıkları aynı anda farklı tenant bilgileri ile çalışabilir.

    //Çoklu Kullanıcı Desteği:
    //Uygulama aynı anda birden fazla kullanıcıya hizmet verdiğinde,
    // kullanıcıların tenant bilgileri birbirine karışmaz.

    //Performans:
    //Tenant bilgisi her istekte global bir değişkende saklanıp sorgulanmak yerine
    // iş parçacığına özel tutulduğu için daha hızlıdır.
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

    // Mevcut iş parçacığına (thread) ait tenant bilgisini döndürür
    // Hibernate gibi ORM araçlarında hangi tenant'ın (örneğin, veritabanı şeması)
    // kullanılması gerektiğini belirlemek için çağrılır.
    // API işlemleri sırasında, iş parçacığına atanmış tenant bilgisine ihtiyaç duyulursa bu yöntem kullanılabilir.
    public static String getCurrentTenant() {
        return currentTenant.get();
    }

    // Mevcut iş parçacığına bir tenant bilgisi atar.
    // tenant: Tenant'ın adı veya ID'si (örneğin, bir veritabanı şeması adı).
    // API isteği geldiğinde, istek başlatılırken Filter veya
    // Interceptor gibi bir mekanizma üzerinden tenant bilgisi alınır ve burada iş parçacığına atanır.
    //Bu bilgi daha sonra veritabanı bağlantılarında veya iş mantığında kullanılabilir.
    public static void setTenant(String tenant) {
        currentTenant.set(tenant);
    }

    // İş parçacığına atanmış tenant bilgisini temizler.
    // API isteği tamamlandığında, iş parçacığındaki tenant bilgisini temizlemek için çağrılır.
    // Bu, aynı iş parçacığının farklı bir tenant için yeniden kullanıldığında eski bilgilerin karışmasını engeller.
    public static void clearTenant() {
        currentTenant.remove();
    }
}
