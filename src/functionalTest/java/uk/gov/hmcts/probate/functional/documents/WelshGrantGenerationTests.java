package uk.gov.hmcts.probate.functional.documents;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringIntegrationSerenityRunner.class)
public class WelshGrantGenerationTests extends DocumentGenerationTestBase {

    public void verifyPersonalGenerateWelshReissueGOP() throws IOException {
        verifyPersonalWelshReissueText("personalPayloadReissueGOPDuplicateWelsh.json",
            "expectedDocumentReissueGOPWelsh.txt");
    }

    @Test
    public void verifyPersonalGenerateWelshReissueAdmonWill() throws IOException {
        verifyPersonalWelshReissueText("personalPayloadReissueAdmonWillRegistrarsOrderWelsh.json",
            "expectedDocumentReissueAdmonWillWelsh.txt");
    }

    @Test
    public void verifyPersonalGenerateWelshReissueIntestacy() throws IOException {
        verifyPersonalWelshReissueText("personalPayloadReissueIntestacyRegistrarsDirectionWelsh.json",
            "expectedDocumentReissueIntestacyWelsh.txt");
    }

    @Test
    public void verifyPersonalGenerateWelshGrantGOP() throws IOException {
        verifyPersonalWelshGrantText("personalPayloadGrantGOPWelsh.json", "expectedDocumentGrantGOPWelsh.txt");
    }

    @Test
    public void verifyPersonalExecutorNameGenerateWelshGrantGOP() throws IOException {
        verifyPersonalWelshGrantText("personalPayloadGrantExecutorGOPWelsh.json",
                "expectedDocumentGrantWithExecutorGOPWelsh.txt");
    }

    @Test
    public void verifyPersonalGenerateWelshGrantAdmonWill() throws IOException {
        verifyPersonalWelshGrantText("personalPayloadGrantAdmonWillWelsh.json",
            "expectedDocumentGrantAdmonWillWelsh.txt");
    }

    @Test
    public void verifyPersonalGenerateWelshGrantIntestacy() throws IOException {
        verifyPersonalWelshGrantText("personalPayloadGrantIntestacyWelsh.json",
            "expectedDocumentGrantIntestacyWelsh.txt");
    }

    private void verifyPersonalWelshGrantText(String payload, String expectedFile) throws IOException {
        final String response = getFirstProbateDocumentsText(payload, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile(expectedFile));
        expectedText = expectedText.replaceAll("1st August 2021", utils.formatDate(LocalDate.now()));
        expectedText = expectedText.replaceAll("1 Awst 2021", utils.convertToWelsh(LocalDate.now()));
        log.info("expectedFile *****>>:" + expectedText.trim());
        log.info("response.trim() *****>>:" + response.trim());
        assertEquals(expectedText.trim(), response.trim());
    }

    private void verifyPersonalWelshReissueText(String payload, String expectedFile) throws IOException {
        final String response = getProbateDocumentsGeneratedTextAtIndex(payload, GENERATE_GRANT_REISSUE, "2");

        String expectedText = removeCrLfs(utils.getJsonFromFile(expectedFile));

        assertEquals(expectedText.trim(), response.trim());
    }

}
