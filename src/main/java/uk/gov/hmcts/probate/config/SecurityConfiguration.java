package uk.gov.hmcts.probate.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import uk.gov.hmcts.reform.auth.checker.core.RequestAuthorizer;
import uk.gov.hmcts.reform.auth.checker.core.service.Service;
import uk.gov.hmcts.reform.auth.checker.core.service.ServiceRequestAuthorizer;
import uk.gov.hmcts.reform.auth.checker.core.user.User;
import uk.gov.hmcts.reform.auth.checker.core.user.UserRequestAuthorizer;
import uk.gov.hmcts.reform.auth.checker.spring.serviceanduser.AuthCheckerServiceAndUserFilter;
import uk.gov.hmcts.reform.auth.checker.spring.serviceonly.AuthCheckerServiceOnlyFilter;

@EnableWebSecurity
public class SecurityConfiguration {

    @Configuration
    @Order(1)
    public static class AuthCheckerServiceAndUSerFilterConfigurerAdapter extends WebSecurityConfigurerAdapter {

        private final AuthCheckerServiceAndUserFilter authCheckerServiceAndUserFilter;

        public AuthCheckerServiceAndUSerFilterConfigurerAdapter(UserRequestAuthorizer<User> userRequestAuthorizer,
                                                                ServiceRequestAuthorizer serviceRequestAuthorizer,
                                                                AuthenticationManager authenticationManager) {
            authCheckerServiceAndUserFilter =
                new AuthCheckerServiceAndUserFilter(serviceRequestAuthorizer, userRequestAuthorizer);
            authCheckerServiceAndUserFilter.setAuthenticationManager(authenticationManager);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http
                .requestMatchers()
                .antMatchers("/notify/grant-delayed-scheduled")
                .antMatchers("/notify/grant-awaiting-documents-scheduled")
                .and()
                .addFilter(authCheckerServiceAndUserFilter)
                .csrf().disable()
                .formLogin().disable()
                .logout().disable()
                .authorizeRequests()
                .anyRequest().authenticated();
        }
    }

    @Configuration
    public static class AuthCheckerServiceOnlyFilterConfigurerAdapter extends WebSecurityConfigurerAdapter {

        private AuthCheckerServiceOnlyFilter authCheckerServiceOnlyFilter;

        public AuthCheckerServiceOnlyFilterConfigurerAdapter(RequestAuthorizer<Service> serviceRequestAuthorizer,
                                                             AuthenticationManager authenticationManager) {
            authCheckerServiceOnlyFilter = new AuthCheckerServiceOnlyFilter(serviceRequestAuthorizer);
            authCheckerServiceOnlyFilter.setAuthenticationManager(authenticationManager);
        }

        @Override
        @Order(2)
        protected void configure(HttpSecurity http) throws Exception {

            http
                .requestMatchers()
                .antMatchers("/swagger-ui.html")
                .antMatchers("/swagger-resources/**")
                .antMatchers("/v2/api-docs")
                .antMatchers("/health", "/health/liveness")
                .antMatchers("/info")
                .antMatchers("/case/**")
                .antMatchers("/case-matching/**")
                .antMatchers("/caveat/**")
                .antMatchers("/data-extract/**")
                .antMatchers("/document/**")
                .antMatchers("/transform-scanned-data")
                .antMatchers("/transform-exception-record")
                .antMatchers("/update-case")
                .antMatchers("/grant/**")
                .antMatchers("/nextsteps/**")
                .antMatchers("/notify/**")
                .antMatchers("/forms/**")
                .antMatchers("/template/**")
                .antMatchers("/probateManTypes/**")
                .antMatchers("/legacy/**")
                .antMatchers("/standing-search/**")
                .antMatchers("/payment/**")
                .and()
                .addFilter(authCheckerServiceOnlyFilter)
                .csrf().disable()
                .formLogin().disable()
                .logout().disable()
                .authorizeRequests()
                .anyRequest().authenticated();

        }

        public void configure(WebSecurity web) {
            web.ignoring().antMatchers("/swagger-ui.html",
                "/swagger-resources/**",
                "/v2/api-docs",
                "/health",
                "/health/liveness",
                "/info",
                "/data-extract/**",
                "/");
        }
    }


}
