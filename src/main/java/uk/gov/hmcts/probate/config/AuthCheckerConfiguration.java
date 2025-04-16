package uk.gov.hmcts.probate.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Configuration
public class AuthCheckerConfiguration {

    @Bean
    public Function<HttpServletRequest, Collection<String>> authorizedRolesExtractor() {
        log.info("authorizedRolesExtractor - return empty list");
        return any -> Collections.emptyList();
    }

    @Bean
    @Qualifier("authorizedRolesExtractor")
    public Function<HttpServletRequest, Optional<String>> userIdExtractor() {
        Pattern pattern = Pattern.compile("^/users/([^/]+)/.+$");

        return request -> {
            Matcher matcher = pattern.matcher(request.getRequestURI());
            boolean matched = matcher.find();
            log.info("userIdExtractor with uri: [{}], matched: [{}], returning: [{}]", request.getRequestURI(), matched, matched ? matcher.group(1) : null);
            return Optional.ofNullable(matched ? matcher.group(1) : null);
        };
    }
}
