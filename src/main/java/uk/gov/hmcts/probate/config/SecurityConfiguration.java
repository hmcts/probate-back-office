package uk.gov.hmcts.probate.config;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import uk.gov.hmcts.reform.authorisation.filters.ServiceAuthFilter;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@ConfigurationProperties(prefix = "spring.security")
public class SecurityConfiguration {

    private final ServiceAuthFilter serviceAuthFilter;

    private final List<String> anonymousPaths = new ArrayList<>();

    @Autowired
    public SecurityConfiguration(ServiceAuthFilter serviceAuthFilter) {
        super();
        this.serviceAuthFilter = serviceAuthFilter;
    }

    @SneakyThrows
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
                .addFilterBefore(serviceAuthFilter, BearerTokenAuthenticationFilter.class)
                .sessionManagement()
                .sessionCreationPolicy(STATELESS)
                .and()
                .httpBasic()
                .disable()
                .formLogin()
                .disable()
                .logout()
                .disable()
                .csrf()
                .disable();
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(anonymousPaths.toArray(String[]::new));
    }

    public List<String> getAnonymousPaths() {
        return anonymousPaths;
    }
}
