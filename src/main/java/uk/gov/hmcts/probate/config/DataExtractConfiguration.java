package uk.gov.hmcts.probate.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("cron.key")
public class DataExtractConfiguration {

    private String exela;
    private String iron;
    private String hmrc;
}
