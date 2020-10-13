package uk.gov.hmcts.probate.functional;

import io.restassured.RestAssured;
import net.serenitybdd.junit.spring.integration.SpringIntegrationMethodRule;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.hmcts.probate.functional.util.FunctionalTestUtils;

@RunWith(SpringIntegrationSerenityRunner.class)
@ContextConfiguration(classes = TestContextConfiguration.class)
public abstract class IntegrationTestBase {

    @Autowired
    protected SolCCDServiceAuthTokenGenerator serviceAuthTokenGenerator;

    @Rule
    public SpringIntegrationMethodRule springIntegration;

    private String solCcdServiceUrl;
    public static String evidenceManagementUrl;

    @Autowired
    public void solCcdServiceUrl(@Value("${sol.ccd.service.base.url}") String solCcdServiceUrl) {
        this.solCcdServiceUrl = solCcdServiceUrl;
        RestAssured.baseURI = solCcdServiceUrl;
    }

    @Autowired
    public void evidenceManagementUrl(@Value("${evidence.management.url}") String evidenceManagementUrl) {
        this.evidenceManagementUrl = evidenceManagementUrl;

    }

    public static void setEvidenceManagementUrlAsBaseUri() {
        RestAssured.baseURI = evidenceManagementUrl;
    }
    @Autowired
    protected FunctionalTestUtils utils;

    public IntegrationTestBase() {
        this.springIntegration = new SpringIntegrationMethodRule();

    }
}
