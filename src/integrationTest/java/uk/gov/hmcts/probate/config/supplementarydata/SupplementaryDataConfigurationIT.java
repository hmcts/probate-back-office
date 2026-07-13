package uk.gov.hmcts.probate.config.supplementarydata;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.probate.config.SupplementaryDataConfiguration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = SupplementaryDataConfiguration.class)
public class SupplementaryDataConfigurationIT {
    @Autowired
    private SupplementaryDataConfiguration configuration;

    @Test
    void shouldLoadHmctsIdFromApplicationProperties() {
        assertThat(configuration.getHmctsId()).isEqualTo("ABA6");
    }
}
