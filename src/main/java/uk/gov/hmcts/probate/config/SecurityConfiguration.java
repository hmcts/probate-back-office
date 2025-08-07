package uk.gov.hmcts.probate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import uk.gov.hmcts.reform.auth.checker.core.RequestAuthorizer;
import uk.gov.hmcts.reform.auth.checker.core.service.Service;
import uk.gov.hmcts.reform.auth.checker.core.service.ServiceRequestAuthorizer;
import uk.gov.hmcts.reform.auth.checker.core.user.User;
import uk.gov.hmcts.reform.auth.checker.core.user.UserRequestAuthorizer;
import uk.gov.hmcts.reform.auth.checker.spring.serviceanduser.AuthCheckerServiceAndUserFilter;
import uk.gov.hmcts.reform.auth.checker.spring.serviceonly.AuthCheckerServiceOnlyFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    @Order(1)
    public SecurityFilterChain serviceAndUserFilterChain(
            HttpSecurity http,
            UserRequestAuthorizer<User> userRequestAuthorizer,
            ServiceRequestAuthorizer serviceRequestAuthorizer,
            AuthenticationManager authenticationManager
    ) throws Exception {
        AuthCheckerServiceAndUserFilter authCheckerServiceAndUserFilter =
                new AuthCheckerServiceAndUserFilter(serviceRequestAuthorizer, userRequestAuthorizer);
        authCheckerServiceAndUserFilter.setAuthenticationManager(authenticationManager);

        http
                .securityMatcher("/notify/grant-delayed-scheduled",
                        "/notify/grant-awaiting-documents-scheduled")
                .addFilter(authCheckerServiceAndUserFilter)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
            );

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain serviceOnlyFilterChain(HttpSecurity http,
                                                      RequestAuthorizer<Service> serviceRequestAuthorizer,
                                                      AuthenticationManager authenticationManager) throws Exception {
        AuthCheckerServiceOnlyFilter authCheckerServiceOnlyFilter
                = new AuthCheckerServiceOnlyFilter(serviceRequestAuthorizer);
        authCheckerServiceOnlyFilter.setAuthenticationManager(authenticationManager);

        http
                .securityMatcher(
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/v2/api-docs",
                        "/health",
                        "/health/liveness",
                        "/info",
                        "/case/**",
                        "/case-matching/**",
                        "/caveat/**",
                        "/data-extract/**",
                        "/document/**",
                        "/transform-scanned-data",
                        "/transform-exception-record",
                        "/update-case",
                        "/grant/**",
                        "/nextsteps/**",
                        "/notify/**",
                        "/forms/**",
                        "/template/**",
                        "/probateManTypes/**",
                        "/legacy/**",
                        "/standing-search/**",
                        "/payment/**"
                )
                .addFilter(authCheckerServiceOnlyFilter)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
            );

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
                "/swagger-ui.html",
                "/swagger-resources/**",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/v2/api-docs",
                "/health",
                "/health/liveness",
                "/info",
                "/data-extract/**",
                "/"
        );
    }
}