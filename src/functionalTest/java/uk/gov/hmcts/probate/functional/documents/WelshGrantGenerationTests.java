package uk.gov.hmcts.probate.functional.documents;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;



@ExtendWith(SerenityJUnit5Extension.class)
public class WelshGrantGenerationTests extends DocumentGenerationTestBase {

    public void verifyPersonalGenerateWelshReissueGOP() throws IOException {
        verifyPersonalWelshReissueText("personalPayloadReissueGOPDuplicateWelsh.json",
            "expectedDocumentReissueGOPWelsh.txt");
    }

    @Test
    void verifyPersonalGenerateWelshReissueAdmonWill() throws IOException {
        verifyPersonalWelshReissueText("personalPayloadReissueAdmonWillRegistrarsOrderWelsh.json",
            "expectedDocumentReissueAdmonWillWelsh.txt");
    }

    @Test
    void verifyPersonalGenerateWelshReissueIntestacy() throws IOException {
        verifyPersonalWelshReissueText("personalPayloadReissueIntestacyRegistrarsDirectionWelsh.json",
            "expectedDocumentReissueIntestacyWelsh.txt");
    }

    @Test
    void verifyPersonalGenerateWelshGrantGOP() throws IOException {
        verifyPersonalWelshGrantText("personalPayloadGrantGOPWelsh.json", "expectedDocumentGrantGOPWelsh.txt");
    }

    @Test
    void verifyPersonalExecutorNameGenerateWelshGrantGOP() throws IOException {
        verifyPersonalWelshGrantText("personalPayloadGrantExecutorGOPWelsh.json",
                "expectedDocumentGrantWithExecutorGOPWelsh.txt");
    }

    @Test
    void verifyPersonalGenerateWelshGrantAdmonWill() throws IOException {
        verifyPersonalWelshGrantText("personalPayloadGrantAdmonWillWelsh.json",
            "expectedDocumentGrantAdmonWillWelsh.txt");
    }

    @Test
    void verifyPersonalGenerateWelshGrantIntestacy() throws IOException {
        verifyPersonalWelshGrantText("personalPayloadGrantIntestacyWelsh.json",
            "expectedDocumentGrantIntestacyWelsh.txt");
    }

    @Test
    void verifyPersonalGenerateWelshGrantAdColligenda() throws IOException {
        verifyPersonalWelshGrantText("personalPayloadGrantAdColligendaWelsh.json",
                "expectedDocumentGrantAdColligendaWelsh.txt");
    }

    @Test
    void verifyPersonalGenerateWelshGrantAdColligendaWithLimitationText() throws IOException {
        verifyPersonalWelshGrantText("personalPayloadLimitationTextGrantAdColligendaWelsh.json",
                "expectedDocumentWithLimitationTextAdColligendaWelsh.txt");
    }

    private void verifyPersonalWelshGrantText(String payload, String expectedFile) throws IOException {
        final String response = getFirstProbateDocumentsText(payload, GENERATE_GRANT);

        String expectedText = removeCrLfs(utils.getJsonFromFile(expectedFile));
        expectedText = expectedText.replaceAll("1st August 2021", utils.formatDate(LocalDate.now()));
        expectedText = expectedText.replaceAll("1 Awst 2021", utils.convertToWelsh(LocalDate.now()));

        assertEquals(expectedText.trim(), response.trim());
    }

    private void verifyPersonalWelshReissueText(String payload, String expectedFile) throws IOException {
        final String response = getProbateDocumentsGeneratedTextAtIndex(payload, GENERATE_GRANT_REISSUE, "2");

        String expectedText = removeCrLfs(utils.getJsonFromFile(expectedFile));

        assertEquals(expectedText.trim(), response.trim());
    }

}
