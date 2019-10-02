package uk.gov.hmcts.probate.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import uk.gov.hmcts.probate.exception.handler.AuthenticationExceptionHandler;
import uk.gov.hmcts.reform.auth.checker.core.RequestAuthorizer;
import uk.gov.hmcts.reform.auth.checker.core.service.Service;
import uk.gov.hmcts.reform.auth.checker.spring.serviceonly.AuthCheckerServiceOnlyFilter;

@EnableWebSecurity
public class SecurityConfiguration {

    @Configuration
    @ConditionalOnProperty(name = "s2s.enabled", havingValue = "true")
    public class ServiceOnlySecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        private final AuthCheckerServiceOnlyFilter filter;
        private final AuthenticationExceptionHandler authenticationExceptionHandler;

        public ServiceOnlySecurityConfigurationAdapter(RequestAuthorizer<Service> serviceRequestAuthorizer,
                                                       AuthenticationManager authenticationManager,
                                                       AuthenticationExceptionHandler authenticationExceptionHandler) {
            filter = new AuthCheckerServiceOnlyFilter(serviceRequestAuthorizer);
            filter.setAuthenticationManager(authenticationManager);
            this.authenticationExceptionHandler = authenticationExceptionHandler;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            final ProviderManager authenticationManager = (ProviderManager) authenticationManager();
            authenticationManager.setEraseCredentialsAfterAuthentication(false);
            filter.setAuthenticationManager(authenticationManager());

            http.exceptionHandling()
                    .authenticationEntryPoint(authenticationExceptionHandler);

            http.addFilter(filter)
                    .csrf().disable()
                    .formLogin().disable()
                    .logout().disable()
                    .authorizeRequests()
                    .antMatchers("/swagger-ui.html").permitAll()
                    .antMatchers("/swagger-resources/**").permitAll()
                    .antMatchers("/webjars/springfox-swagger-ui/**").permitAll()
                    .antMatchers("/v2/api-docs").permitAll()
                    .antMatchers("/health", "/health/liveness").permitAll()
                    .antMatchers("/info").permitAll()
                    .anyRequest().authenticated();
        }
    }
}
