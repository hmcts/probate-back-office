package uk.gov.hmcts.probate.config;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = SupplementaryDataConfiguration.class)
@TestPropertySource(properties = {
    "supplementary-data.hmctsid=ABA6"
})
public class SupplementaryDataConfigurationUnitTest {
    @Autowired
    private SupplementaryDataConfiguration configuration;

    @Test
    void shouldLoadHmctsIdFromProperties() {
        assertThat(configuration.getHmctsId()).isEqualTo("ABA6");
    }

}
