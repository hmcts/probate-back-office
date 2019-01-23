package uk.gov.hmcts.probate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class AuthCheckerConfiguration {

    @Bean
    public Function<HttpServletRequest, Collection<String>> authorizedRolesExtractor() {
        return any -> Collections.emptyList();
    }

    @Bean
    public Function<HttpServletRequest, Optional<String>> userIdExtractor() {
        Pattern pattern = Pattern.compile("^/users/([^/]+)/.+$");

        return request -> {
            Matcher matcher = pattern.matcher(request.getRequestURI());
            boolean matched = matcher.find();
            return Optional.ofNullable(matched ? matcher.group(1) : null);
        };
    }
}
