package uk.gov.hmcts.probate.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "bulkscan.default-value")
public class BulkScanConfig {

    private String postcode;
}