package uk.gov.hmcts.probate.config;

import com.launchdarkly.sdk.server.LDClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class LDClientConfiguration {
    @Bean
    public LDClient ldClient(@Value("${ld.sdk_key}") String ldSdkKey) {
        log.info("ld.sdk_key: {}",ldSdkKey);
        return new LDClient(ldSdkKey);
    }
}
