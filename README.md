# Class TenantContext

TenantContext sınıfı, her API isteği sırasında, o isteğe özel bir tenant bilgisi sağlar.
ThreadLocal, her iş parçacığının kendi değerine sahip olmasını sağlar ve bu değer diğer iş parçacıkları tarafından paylaşılamaz.
Bu, çoklu kullanıcıların aynı anda uygulamayı kullanırken tenant bilgilerinin karışmasını önler.


- Neden ThreadLocal Kullanılıyor?
- İzole Veri Saklama:
- Her iş parçacığının kendi tenant bilgisine sahip olmasını sağlar.
- Diğer iş parçacıkları aynı anda farklı tenant bilgileri ile çalışabilir.

- Çoklu Kullanıcı Desteği:
- Uygulama aynı anda birden fazla kullanıcıya hizmet verdiğinde, kullanıcıların tenant bilgileri birbirine karışmaz.

- Performans:
- Tenant bilgisi her istekte global bir değişkende saklanıp sorgulanmak yerine iş parçacığına özel tutulduğu için daha hızlıdır.
    
    - private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();
------------------------------------------------------
- Mevcut iş parçacığına (thread) ait tenant bilgisini döndürür
- Hibernate gibi ORM araçlarında hangi tenant'ın (örneğin, veritabanı şeması)
- kullanılması gerektiğini belirlemek için çağrılır.
- API işlemleri sırasında, iş parçacığına atanmış tenant bilgisine ihtiyaç duyulursa bu yöntem kullanılabilir.
    
    - public static String getCurrentTenant() {
        return currentTenant.get();
    }
------------------------------------------------------
- Mevcut iş parçacığına bir tenant bilgisi atar.
- tenant: Tenant'ın adı veya ID'si (örneğin, bir veritabanı şeması adı).
- API isteği geldiğinde, istek başlatılırken Filter veya Interceptor gibi bir mekanizma üzerinden tenant bilgisi alınır ve burada iş parçacığına atanır.
- Bu bilgi daha sonra veritabanı bağlantılarında veya iş mantığında kullanılabilir.
    
    - public static void setTenant(String tenant) {
        currentTenant.set(tenant);
    }
------------------------------------------------------
- İş parçacığına atanmış tenant bilgisini temizler.
- API isteği tamamlandığında, iş parçacığındaki tenant bilgisini temizlemek için çağrılır.
- Bu, aynı iş parçacığının farklı bir tenant için yeniden kullanıldığında eski bilgilerin karışmasını engeller.
    
    - public static void clearTenant() {
        currentTenant.remove();
    }


# Class TenantIdentifierResolver

- Amaç: Tenant bilgisi bulunamazsa veya belirtilmezse kullanılacak varsayılan tenant'ı
- (örneğin, şema adı) belirtir. Bu Koddaki Ayar: Varsayılan tenant public şemasıdır.
  
  - public static final String DEFAULT_TENANT = "public";
------------------------------------------------------

-Hibernate'in işlem sırasında hangi tenant'ı (örneğin şema) kullanacağını belirler.

- Nasıl Çalışır?
- TenantContext.getCurrentTenant() çağrılarak iş parçacığına atanmış tenant bilgisi alınır.
- Eğer tenantId boş değilse, bu ID döndürülür (kullanıcıya özel tenant şeması).
- Eğer tenantId boş veya null ise, varsayılan tenant (örneğin public) döndürülür.
- Önemi: İşlemin hangi tenant üzerinde gerçekleşeceğini dinamik olarak belirler.

    @Override
    - public String resolveCurrentTenantIdentifier() {
        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId != null && !tenantId.isEmpty()) {
            return tenantId;
        }
        return DEFAULT_TENANT;

 ------------------------------------------------------

- Hibernate, mevcut bir oturumu (session) kullanmadan önce tenant bilgisini doğrulayıp doğrulamayacağını belirler.
- false olarak ayarlandığı için mevcut oturumlarda tenant bilgisi doğrulanmaz.
- Bu, performansı artırabilir ancak daha az güvenli bir yapı olabilir.

    @Override
    - public boolean validateExistingCurrentSessions() {
        return false;

------------------------------------------------------

# Class TenantConnectionProvider

    - private final DataSource dataSource;
 
 - tenant için bağlantı sağlar
   
    @Override
    - public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

------------------------------------------------------

- Verilen bağlantıyı serbest bırakır ve kapatır.
   
    @Override
    - public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

------------------------------------------------------

- (tenant) kimliği ile bir bağlantı alır.
- Bu bağlantının şeması, tenantIdentifier ile belirtilen şemaya ayarlanır.
  
    @Override
    - public Connection getConnection(String tenantIdentifier) throws SQLException {
        final Connection connection = getAnyConnection();

        connection.setSchema(tenantIdentifier.toString());

        return connection;
    }

------------------------------------------------------

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        connection.setSchema(TenantIdentifierResolver.DEFAULT_TENANT);

        releaseAnyConnection(connection);
    }

------------------------------------------------------

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

------------------------------------------------------

    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return false;
    }

------------------------------------------------------

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }  

------------------------------------------------------

# Class TenantFilter

 - X-Tenant-ID header'ı üzerinden tenant bilgisi alınır ve TenantContext'e atanır. İstek tamamlanınca bilgi temizlenir.
 
        @Override
        - public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
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

   ------------------------------------------------------

   
