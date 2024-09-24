package uk.gov.hmcts.probate.functional.documents;



import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

@ExtendWith(SerenityJUnit5Extension.class)
public class CoversheetGenerationTests extends DocumentGenerationTestBase {

    public static final String NEXTSTEPS_VALIDATE_URL = "/nextsteps/validate";

    @Test
    void verifyGenerateSolsCoverSheetGopRenouncingExecutors() throws IOException {
        String payload = "/coversheet/caseprogress/04a-caseCreated.json";
        assertNotNull(validatePostSuccess(payload, NEXTSTEPS_VALIDATE_URL));

    }

    @Test
    void verifyGenerateSolsCoverSheetGopWillHasCodicils() throws IOException {
        String payload = "/coversheet/caseprogress/04b-caseCreated.json";
        assertNotNull(validatePostSuccess(payload, NEXTSTEPS_VALIDATE_URL));
    }

    @Test
    void verifyGenerateSolsCoverSheetGopIht217() throws IOException {
        String payload = "/coversheet/caseprogress/04c-caseCreated.json";
        assertNotNull(validatePostSuccess(payload, NEXTSTEPS_VALIDATE_URL));
    }

    @Test
    void verifyGenerateSolsCoverSheetGopPA17Form() throws IOException {
        String payload = "/coversheet/caseprogress/04d-caseCreated.json";
        assertNotNull(validatePostSuccess(payload, NEXTSTEPS_VALIDATE_URL));
    }

    @Test
    void verifyGenerateSolsCoverSheetIntestacy() throws IOException {
        String payload = "/coversheet/caseprogressintestacy/04-caseCreated.json";
        assertNotNull(validatePostSuccess(payload, NEXTSTEPS_VALIDATE_URL));
    }

    @Test
    void verifyGenerateSolsCoverSheetIntestacyPA16Form() throws IOException {
        String payload = "/coversheet/caseprogressintestacy/04b-caseCreated.json";
        assertNotNull(validatePostSuccess(payload, NEXTSTEPS_VALIDATE_URL));
    }

    @Test
    void verifyGenerateSolsCoverSheetAdmonWill() throws IOException {
        String payload = "/coversheet/caseprogressadmonwill/04-caseCreated.json";
        assertNotNull(validatePostSuccess(payload, NEXTSTEPS_VALIDATE_URL));
    }

    @Test
    void verifyCoverSheetGenerateSolsGopExpectedEstatesBeforeSwitchDate() throws IOException {
        //confirmation page for these tests at SolCcdServiceNextStepsTests
        String dir = "/coversheet/exceptedEstates/ihtEstateBeforeSwitchDate/";
        String payload = dir + "caseCreate.json";
        assertNotNull(validatePostSuccess(payload, NEXTSTEPS_VALIDATE_URL));
    }

    @Test
    void verifyCoverSheetGenerateSolsGopExpectedEstatesNo() throws IOException {
        String dir = "/coversheet/exceptedEstates/ihtEstateCompletedNo/";
        String payload = dir + "caseCreate.json";
        assertNotNull(validatePostSuccess(payload, NEXTSTEPS_VALIDATE_URL));
    }

    @Test
    void verifyCoverSheetGenerateSolsGopExpectedEstatesCompletedYes207() throws IOException {
        String dir = "/coversheet/exceptedEstates/ihtEstateCompletedYes207/";
        String payload = dir + "caseCreate.json";
        assertNotNull(validatePostSuccess(payload, NEXTSTEPS_VALIDATE_URL));
    }

    @Test
    void verifyCoverSheetGenerateSolsGopExpectedEstatesCompletedYes400421() throws IOException {
        String dir = "/coversheet/exceptedEstates/ihtEstateCompletedYes400421/";
        String payload = dir + "caseCreate.json";
        assertNotNull(validatePostSuccess(payload, NEXTSTEPS_VALIDATE_URL));
    }

    @Test
    void verifyCoverSheetGenerateSolsGopExpectedEstatesCompletedYes400() throws IOException {
        String dir = "/coversheet/exceptedEstates/ihtEstateCompletedYes400/";
        String payload = dir + "caseCreate.json";
        assertNotNull(validatePostSuccess(payload, NEXTSTEPS_VALIDATE_URL));
    }

    @Test
    void verifyCoverSheetGenerateSolsGopFormNA() throws IOException {
        String dir = "/coversheet/exceptedEstates/ihtFormNA/";
        String payload = dir + "caseCreate.json";
        assertNotNull(validatePostSuccess(payload, NEXTSTEPS_VALIDATE_URL));
    }

    @Test
    void verifyGenerateSolsCoverSheetGopTcResolutionLodgedWithinApplication() throws IOException {
        String payload = "/coversheet/caseprogress/04e-caseCreated.json";
        assertNotNull(validatePostSuccess(payload, NEXTSTEPS_VALIDATE_URL));
    }

    @Test
    void verifyGenerateSolsCoverSheetGopNotarialWill() throws IOException {
        String payload = "/coversheet/caseprogress/04-caseCreated-notarial.json";
        assertNotNull(validatePostSuccess(payload, NEXTSTEPS_VALIDATE_URL));
    }
}
