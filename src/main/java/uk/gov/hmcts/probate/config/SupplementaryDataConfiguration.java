package uk.gov.hmcts.probate.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class SupplementaryDataConfiguration {
    private final String hmctsId;

    public SupplementaryDataConfiguration(@Value("${supplementary-data.hmctsid}") String hmctsId) {
        this.hmctsId = hmctsId;
    }
}
