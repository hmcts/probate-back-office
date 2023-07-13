package uk.gov.hmcts.probate.functional.documents;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringIntegrationSerenityRunner.class)
public class CoversheetGenerationTests extends DocumentGenerationTestBase {

    public static final String NEXTSTEPS_VALIDATE_URL = "/nextsteps/validate";

    @Test
    public void verifyGenerateSolsCoverSheetGopRenouncingExecutors() throws IOException {
        String payload = "/coversheet/caseprogress/04a-caseCreated.json";
        assertNotNull(validatePostSuccess(payload, NEXTSTEPS_VALIDATE_URL));

    }

    @Test
    public void verifyGenerateSolsCoverSheetGopWillHasCodicils() throws IOException {
        String payload = "/coversheet/caseprogress/04b-caseCreated.json";
        assertNotNull(validatePostSuccess(payload, NEXTSTEPS_VALIDATE_URL));
    }

    @Test
    public void verifyGenerateSolsCoverSheetGopIht217() throws IOException {
        String payload = "/coversheet/caseprogress/04c-caseCreated.json";
        assertNotNull(validatePostSuccess(payload, NEXTSTEPS_VALIDATE_URL));
    }

    @Test
    public void verifyGenerateSolsCoverSheetGopPA17Form() throws IOException {
        String payload = "/coversheet/caseprogress/04d-caseCreated.json";
        assertNotNull(validatePostSuccess(payload, NEXTSTEPS_VALIDATE_URL));
    }

    @Test
    public void verifyGenerateSolsCoverSheetIntestacy() throws IOException {
        String payload = "/coversheet/caseprogressintestacy/04-caseCreated.json";
        assertNotNull(validatePostSuccess(payload, NEXTSTEPS_VALIDATE_URL));
    }

    @Test
    public void verifyGenerateSolsCoverSheetIntestacyPA16Form() throws IOException {
        String payload = "/coversheet/caseprogressintestacy/04b-caseCreated.json";
        assertNotNull(validatePostSuccess(payload, NEXTSTEPS_VALIDATE_URL));
    }

    @Test
    public void verifyGenerateSolsCoverSheetAdmonWill() throws IOException {
        String payload = "/coversheet/caseprogressadmonwill/04-caseCreated.json";
        assertNotNull(validatePostSuccess(payload, NEXTSTEPS_VALIDATE_URL));
    }

    @Test
    public void verifyCoverSheetGenerateSolsGopExpectedEstatesBeforeSwitchDate() throws IOException {
        //confirmation page for these tests at SolCcdServiceNextStepsTests
        String dir = "/coversheet/exceptedEstates/ihtEstateBeforeSwitchDate/";
        String payload = dir + "caseCreate.json";
        assertNotNull(validatePostSuccess(payload, NEXTSTEPS_VALIDATE_URL));
    }

    @Test
    public void verifyCoverSheetGenerateSolsGopExpectedEstatesNo() throws IOException {
        String dir = "/coversheet/exceptedEstates/ihtEstateCompletedNo/";
        String payload = dir + "caseCreate.json";
        assertNotNull(validatePostSuccess(payload, NEXTSTEPS_VALIDATE_URL));
    }

    @Test
    public void verifyCoverSheetGenerateSolsGopExpectedEstatesCompletedYes207() throws IOException {
        String dir = "/coversheet/exceptedEstates/ihtEstateCompletedYes207/";
        String payload = dir + "caseCreate.json";
        assertNotNull(validatePostSuccess(payload, NEXTSTEPS_VALIDATE_URL));
    }

    @Test
    public void verifyCoverSheetGenerateSolsGopExpectedEstatesCompletedYes400421() throws IOException {
        String dir = "/coversheet/exceptedEstates/ihtEstateCompletedYes400421/";
        String payload = dir + "caseCreate.json";
        assertNotNull(validatePostSuccess(payload, NEXTSTEPS_VALIDATE_URL));
    }

    @Test
    public void verifyGenerateSolsCoverSheetGopTcResolutionLodgedWithinApplication() throws IOException {
        String payload = "/coversheet/caseprogress/04e-caseCreated.json";
        assertNotNull(validatePostSuccess(payload, NEXTSTEPS_VALIDATE_URL));
    }

    @Test
    public void verifyGenerateSolsCoverSheetGopNotarialWill() throws IOException {
        String payload = "/coversheet/caseprogress/04-caseCreated-notarial.json";
        assertNotNull(validatePostSuccess(payload, NEXTSTEPS_VALIDATE_URL));
    }
}
