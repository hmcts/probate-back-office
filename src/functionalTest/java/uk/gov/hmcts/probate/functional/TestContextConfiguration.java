package uk.gov.hmcts.probate.functional;

import com.launchdarkly.sdk.server.LDClient;
import feign.Feign;
import feign.jackson.JacksonEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import uk.gov.hmcts.probate.service.FeatureToggleService;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;

import javax.sql.DataSource;

@Slf4j
@Configuration
@ComponentScan("uk.gov.hmcts.probate.functional")
@PropertySource("application.properties")
public class TestContextConfiguration {

    @Value("${datasource.url}")
    private String dataSourceUrl;

    @Value("${datasource.username}")
    private String datasourceUsername;

    @Value("${datasource.password}")
    private String datasourcePassword;

    @Value("${ld.sdk.key}")
    private String ldSdkKey;

    @Value("${ld.user.key}")
    private String ldUserKey;

    @Value("${ld.user.firstName")
    private String ldUserFirstName;

    @Value("${ld.user.lastName")
    private String ldUserLastName;

    @Bean
    public ServiceAuthTokenGenerator serviceAuthTokenGenerator(
                                                              @Value("${service.auth.provider.base.url}") String s2sUrl,
                                                              @Value("${s2s-auth.totp_secret}") String secret,
                                                              @Value("${service.name}") String microservice) {
        final ServiceAuthorisationApi serviceAuthorisationApi = Feign.builder()
                .encoder(new JacksonEncoder())
                .contract(new SpringMvcContract())
                .target(ServiceAuthorisationApi.class, s2sUrl);
        return new ServiceAuthTokenGenerator(secret, microservice, serviceAuthorisationApi);
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(dataSourceUrl);
        dataSource.setUsername(datasourceUsername);
        dataSource.setPassword(datasourcePassword);
        return dataSource;
    }

    public FeatureToggleService featureToggleService() {
        log.info("create client");
        LDClient ldClient = new LDClient(ldSdkKey);
        log.info("created: {}", ldClient);
        final FeatureToggleService featureToggleService = new FeatureToggleService(
                ldClient,
                ldUserKey,
                ldUserFirstName,
                ldUserLastName);
        log.info("featureToggleService: {}", featureToggleService);
        return featureToggleService;
    }
}
