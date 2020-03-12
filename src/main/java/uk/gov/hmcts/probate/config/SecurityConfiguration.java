package uk.gov.hmcts.probate.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import uk.gov.hmcts.probate.exception.handler.AuthenticationExceptionHandler;
import uk.gov.hmcts.reform.auth.checker.core.RequestAuthorizer;
import uk.gov.hmcts.reform.auth.checker.core.service.Service;
import uk.gov.hmcts.reform.auth.checker.core.user.User;
import uk.gov.hmcts.reform.auth.checker.spring.serviceanduser.AuthCheckerServiceAndUserFilter;
import uk.gov.hmcts.reform.auth.checker.spring.serviceonly.AuthCheckerServiceOnlyFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private AuthCheckerServiceAndUserFilter authCheckerServiceAndUserFilter;
    private AuthCheckerServiceOnlyFilter authCheckerServiceOnlyFilter;

    public SecurityConfiguration(RequestAuthorizer<Service> serviceRequestAuthorizer,
                                 AuthenticationManager authenticationManager,
                                 RequestAuthorizer<User> userRequestAuthorizer) {
        authCheckerServiceAndUserFilter = new AuthCheckerServiceAndUserFilter(serviceRequestAuthorizer, userRequestAuthorizer);
        authCheckerServiceAndUserFilter.setAuthenticationManager(authenticationManager);
        authCheckerServiceOnlyFilter = new AuthCheckerServiceOnlyFilter(serviceRequestAuthorizer);
        authCheckerServiceOnlyFilter.setAuthenticationManager(authenticationManager);

    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/springfox-swagger-ui/**",
            "/v2/api-docs",
            "/health",
            "/health/liveness",
            "/info",
            "/data-extract/**",
            "/");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
            .requestMatchers()
            .antMatchers("/notify/grant-delayed-scheduled")
            .and()
            .addFilter(authCheckerServiceAndUserFilter)
            .requestMatchers()
            .antMatchers("/swagger-ui.html")
            .antMatchers("/swagger-resources/**")
            .antMatchers("/webjars/springfox-swagger-ui/**")
            .antMatchers("/v2/api-docs")
            .antMatchers("/health", "/health/liveness")
            .antMatchers("/info")
            .antMatchers("/case/**")
            .antMatchers("/case-matching/**")
            .antMatchers("/caveat/**")
            .antMatchers("/data-extract/**")
            .antMatchers("/document/**")
            .antMatchers("/transform-exception-record")
            .antMatchers("/grant/**")
            .antMatchers("/nextsteps/**")
            .antMatchers("/notify/**")
            .antMatchers("/forms/**")
            .antMatchers("/template/**")
            .antMatchers("/probateManTypes/**")
            .antMatchers("/legacy/**")
            .antMatchers("/standing-search/**")
            .and()
            .addFilter(authCheckerServiceOnlyFilter)
            .csrf().disable()
            .formLogin().disable()
            .logout().disable()
            .authorizeRequests()
            .anyRequest().authenticated();

    }
}
