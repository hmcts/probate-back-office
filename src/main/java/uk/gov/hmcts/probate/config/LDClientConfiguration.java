package uk.gov.hmcts.probate.config;

import com.launchdarkly.sdk.server.LDClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LDClientConfiguration {
    @Bean
    public LDClient ldClient(@Value("${ld.sdk_key}") String ldClientKey) {
        System.out.println("============= SDKY LAUNCH DARKLY CONFIGURRATION =======>");
        System.out.println(ldClientKey);
        return new LDClient("sdk-4d50eb6e-8400-4aa7-b4c5-8bdfc8b1d844");
    }
}
