package uk.gov.hmcts.probate.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import uk.gov.hmcts.probate.security.JwtGrantedAuthoritiesConverter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private static final String[] AUTHORITIES = {
        "caseworker-probate",
        "caseworker-probate-solicitor",
        "caseworker"
    };

    private static final String[] AUTH_WHITELIST = {
        // -- swagger ui
        "/v3/api-docs",
        "/v3/api-docs/**",
        "/swagger-ui.html",
        "/swagger-resources/**",
        "/swagger-ui/**",
        // other public endpoints of API
        "/health",
        "/health/liveness",
        "/health/readiness",
        "/status/health",
        "/",
        "/loggers/**",
        "/data-extract/**",
        "/info"
    };

    @Value("${spring.security.oauth2.client.provider.oidc.issuer-uri}")
    private String issuerUri;

    @Value("${oidc.issuer}")
    private String issuerOverride;

    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    @Autowired
    public SecurityConfiguration(final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter) {
        jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
    }

    @Bean
    public WebSecurityCustomizer configure() {
        return web -> web.ignoring().requestMatchers(AUTH_WHITELIST);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement().sessionCreationPolicy(STATELESS).and()
            .formLogin().disable()
            .logout().disable()
            .authorizeHttpRequests().requestMatchers(AUTH_WHITELIST).permitAll()
            .and()
            .authorizeHttpRequests()
            .requestMatchers("/cases/callbacks/**",
                    "/case/**",
                    "/case-matching/**",
                    "/caveat/**",
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
                    "/notify/grant-delayed-scheduled",
                    "/notify/grant-awaiting-documents-scheduled")
            .hasAnyAuthority(AUTHORITIES)
            .anyRequest()
            .authenticated()
            .and()
            .oauth2ResourceServer()
            .jwt()
            .jwtAuthenticationConverter(jwtAuthenticationConverter)
            .and()
            .and()
            .oauth2Client();
        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromOidcIssuerLocation(issuerUri);

        // We are using issuerOverride instead of issuerUri as SIDAM has the wrong issuer at the moment
        OAuth2TokenValidator<Jwt> withTimestamp = new JwtTimestampValidator();
        OAuth2TokenValidator<Jwt> withIssuer = new JwtIssuerValidator(issuerOverride);
        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(withTimestamp, withIssuer);

        jwtDecoder.setJwtValidator(validator);
        return jwtDecoder;
    }
}
