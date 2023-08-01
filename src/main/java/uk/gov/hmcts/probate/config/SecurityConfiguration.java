package uk.gov.hmcts.probate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import uk.gov.hmcts.reform.authorisation.filters.ServiceAuthFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final ServiceAuthFilter serviceAuthFilter;

    public SecurityConfiguration(ServiceAuthFilter serviceAuthFilter) {
        this.serviceAuthFilter = serviceAuthFilter;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/health",
            "/health/liveness",
            "/health/readiness",
            "/v2/api-docs/**",
            "/info",
            "/favicon.ico",
            "/data-extract/**",
            "/");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)
            .addFilterBefore(serviceAuthFilter, BearerTokenAuthenticationFilter.class)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                    "/notify/grant-delayed-scheduled",
                    "/notify/grant-awaiting-documents-scheduled",
                    "/case/**",
                    "/case-matching/**",
                    "/caveat/**",
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
                    "/standing-search/**")
                .permitAll())
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
