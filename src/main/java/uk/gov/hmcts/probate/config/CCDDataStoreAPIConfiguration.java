package uk.gov.hmcts.probate.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("ccd.data.store.api")
public class CCDDataStoreAPIConfiguration {

    private String host;
    private String caseMatchingPath;
}
