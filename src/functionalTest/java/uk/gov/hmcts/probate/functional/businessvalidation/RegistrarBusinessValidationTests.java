package uk.gov.hmcts.probate.functional.businessvalidation;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;
import uk.gov.hmcts.probate.functional.util.FunctionalTestUtils;

import java.io.IOException;

@RunWith(SpringIntegrationSerenityRunner.class)
public class RegistrarBusinessValidationTests extends IntegrationTestBase {

    private static final String DEFAULT_REGISTRARS_DECISION = "/case/default-registrars-decision";
    private static final String REGISTRARS_DECISION = "/case/registrars-decision";
    private static final String PAYLOAD_DEFAULT = "registrardecision/success.defaultRegistrarDecision.json";
    private static final String PAYLOAD_ADD = "registrardecision/success.addRegistrarDecision.json";


    @Autowired
    protected FunctionalTestUtils utils;

    @Before
    public void setUp() {
        initialiseConfig();
    }

    @Test
    public void shouldPostForRegistrarEvents() throws IOException {
        validatePostSuccess(PAYLOAD_DEFAULT, DEFAULT_REGISTRARS_DECISION);
        validatePostSuccess(PAYLOAD_ADD, REGISTRARS_DECISION);
    }
}
