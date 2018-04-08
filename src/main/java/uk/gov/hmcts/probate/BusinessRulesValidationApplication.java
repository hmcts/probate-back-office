package uk.gov.hmcts.probate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import uk.gov.hmcts.auth.checker.RequestAuthorizer;
import uk.gov.hmcts.auth.checker.service.Service;
import uk.gov.hmcts.auth.checker.spring.serviceonly.AuthCheckerServiceOnlyFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

@SpringBootApplication
@EnableCircuitBreaker
@EnableHystrixDashboard
public class BusinessRulesValidationApplication {

    @Value("#{'${authorised.services}'.split(',\\s*')}")
    private List<String> authorisedServices;

    public static void main(String[] args) {
        SpringApplication.run(BusinessRulesValidationApplication.class, args);
    }

    @Bean
    public AuthCheckerServiceOnlyFilter authCheckerServiceOnlyFilter(
        RequestAuthorizer<Service> serviceRequestAuthorizer,
        AuthenticationManager authenticationManager) {

        AuthCheckerServiceOnlyFilter filter = new AuthCheckerServiceOnlyFilter(serviceRequestAuthorizer);
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }


    @Bean
    public Function<HttpServletRequest, Collection<String>> authorizedServicesExtractor() {
        return request -> authorisedServices;
    }
}
