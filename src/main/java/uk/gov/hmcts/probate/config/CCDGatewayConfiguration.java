package uk.gov.hmcts.probate.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("ccd.gateway")
public class CCDGatewayConfiguration {

    private String host;
    private String caseMatchingPath;
}
