# Exception

Note: Eğer flyway hatası oluşuyorsa, databaseden flyway schemasını siliniz.

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
    
       private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();
------------------------------------------------------
- Mevcut iş parçacığına (thread) ait tenant bilgisini döndürür
- Hibernate gibi ORM araçlarında hangi tenant'ın (örneğin, veritabanı şeması)
- kullanılması gerektiğini belirlemek için çağrılır.
- API işlemleri sırasında, iş parçacığına atanmış tenant bilgisine ihtiyaç duyulursa bu yöntem kullanılabilir.
    
       public static String getCurrentTenant() {
        return currentTenant.get();}
  
------------------------------------------------------
- Mevcut iş parçacığına bir tenant bilgisi atar.
- tenant: Tenant'ın adı veya ID'si (örneğin, bir veritabanı şeması adı).
- API isteği geldiğinde, istek başlatılırken Filter veya Interceptor gibi bir mekanizma üzerinden tenant bilgisi alınır ve burada iş parçacığına atanır.
- Bu bilgi daha sonra veritabanı bağlantılarında veya iş mantığında kullanılabilir.
    
         public static void setTenant(String tenant) {
            currentTenant.set(tenant);}
  
------------------------------------------------------
- İş parçacığına atanmış tenant bilgisini temizler.
- API isteği tamamlandığında, iş parçacığındaki tenant bilgisini temizlemek için çağrılır.
- Bu, aynı iş parçacığının farklı bir tenant için yeniden kullanıldığında eski bilgilerin karışmasını engeller.
    
         public static void clearTenant() {
            currentTenant.remove();}


# Class TenantIdentifierResolver

- Amaç: Tenant bilgisi bulunamazsa veya belirtilmezse kullanılacak varsayılan tenant'ı
- (örneğin, şema adı) belirtir. Bu Koddaki Ayar: Varsayılan tenant public şemasıdır.
  
   public static final String DEFAULT_TENANT = "public";
------------------------------------------------------

-Hibernate'in işlem sırasında hangi tenant'ı (örneğin şema) kullanacağını belirler.

- Nasıl Çalışır?
- TenantContext.getCurrentTenant() çağrılarak iş parçacığına atanmış tenant bilgisi alınır.
- Eğer tenantId boş değilse, bu ID döndürülür (kullanıcıya özel tenant şeması).
- Eğer tenantId boş veya null ise, varsayılan tenant (örneğin public) döndürülür.
- Önemi: İşlemin hangi tenant üzerinde gerçekleşeceğini dinamik olarak belirler.

         @Override
         public String resolveCurrentTenantIdentifier() {
            String tenantId = TenantContext.getCurrentTenant();
            if (tenantId != null && !tenantId.isEmpty()) {
                return tenantId;
            }
            return DEFAULT_TENANT;}

 ------------------------------------------------------

- Hibernate, mevcut bir oturumu (session) kullanmadan önce tenant bilgisini doğrulayıp doğrulamayacağını belirler.
- false olarak ayarlandığı için mevcut oturumlarda tenant bilgisi doğrulanmaz.
- Bu, performansı artırabilir ancak daha az güvenli bir yapı olabilir.

         @Override
         public boolean validateExistingCurrentSessions() {
            return false;}

------------------------------------------------------

# Class TenantConnectionProvider

          private final DataSource dataSource;
 
 - tenant için bağlantı sağlar
   
         @Override
         public Connection getAnyConnection() throws SQLException {
            return dataSource.getConnection();}

------------------------------------------------------

- Verilen bağlantıyı serbest bırakır ve kapatır.
   
         @Override
         public void releaseAnyConnection(Connection connection) throws SQLException {
            connection.close();}

------------------------------------------------------

- (tenant) kimliği ile bir bağlantı alır.
- Bu bağlantının şeması, tenantIdentifier ile belirtilen şemaya ayarlanır.
  
          @Override
          public Connection getConnection(String tenantIdentifier) throws SQLException {
            final Connection connection = getAnyConnection();
    
            connection.setSchema(tenantIdentifier.toString());
    
            return connection;}

------------------------------------------------------

        @Override
        public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
            connection.setSchema(TenantIdentifierResolver.DEFAULT_TENANT);
    
            releaseAnyConnection(connection);}

------------------------------------------------------
    
        @Override
        public boolean supportsAggressiveRelease() {
            return false;}

------------------------------------------------------

        @Override
        public boolean isUnwrappableAs(Class unwrapType) {
            return false;}

------------------------------------------------------

        @Override
        public <T> T unwrap(Class<T> unwrapType) {
            return null;}  

------------------------------------------------------

# Class TenantFilter

 - X-Tenant-ID header'ı üzerinden tenant bilgisi alınır ve TenantContext'e atanır. İstek tamamlanınca bilgi temizlenir.
 
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

   ------------------------------------------------------

   # FilterConfig
        
           @Bean
            public FilterRegistrationBean<TenantFilter> tenantFilter() {
                FilterRegistrationBean<TenantFilter> registrationBean = new FilterRegistrationBean<>();
                registrationBean.setFilter(new TenantFilter());
                registrationBean.addUrlPatterns("/*");
                return registrationBean;
            }



------------------------------------------------------ 

# Class HibernateConfig 

        @Bean
        JpaVendorAdapter jpaVendorAdapter() {
            return new HibernateJpaVendorAdapter();
        }

------------------------------------------------------ 

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

- Çok kiracılı (multi-tenant) yapıyı Hibernate üzerinden etkinleştirir.
- Tenant başına ayrı bir şema kullanılmasını sağlar.
- Hangi şemanın kullanılacağını belirlemek için
- CurrentTenantIdentifierResolver ve MultiTenantConnectionProvider yapılarını entegre eder.

------------------------------------------------------ 

# Class FlywayConfig

- Varsayılan Flyway yapılandırması
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

- Kullanıcılar için her biri ayrı şemada migration işlemi yapan CommandLineRunner
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
            });
          }
