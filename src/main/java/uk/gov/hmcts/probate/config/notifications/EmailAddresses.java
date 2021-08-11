package uk.gov.hmcts.probate.config.notifications;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties
public class EmailAddresses {

    private String excelaEmail;
    
    private String smeeAndFordEmail;
}
