package uk.gov.hmcts.probate.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import uk.gov.hmcts.auth.checker.spring.serviceonly.AuthCheckerServiceOnlyFilter;

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = "s2s.enabled", havingValue = "true", matchIfMissing = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final AuthCheckerServiceOnlyFilter filter;

    @Autowired
    public SecurityConfiguration(final AuthCheckerServiceOnlyFilter filter) {
        this.filter = filter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        final ProviderManager authenticationManager = (ProviderManager) authenticationManager();
        authenticationManager.setEraseCredentialsAfterAuthentication(false);
        filter.setAuthenticationManager(authenticationManager());

        http.addFilter(filter)
            .csrf().disable()
            .formLogin().disable()
            .logout().disable()
            .authorizeRequests()
            .anyRequest().authenticated();
    }

}
