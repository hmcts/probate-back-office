package uk.gov.hmcts.probate.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import uk.gov.hmcts.auth.checker.spring.serviceonly.AuthCheckerServiceOnlyFilter;
import uk.gov.hmcts.probate.exception.handler.AuthenticationExceptionHandler;

@Data
@EqualsAndHashCode(callSuper = true)
@Configuration
@ConditionalOnProperty(name = "s2s.enabled", havingValue = "true", matchIfMissing = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final AuthCheckerServiceOnlyFilter filter;
    private final AuthenticationExceptionHandler authenticationExceptionHandler;

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
            .anyRequest().authenticated();
    }

}
