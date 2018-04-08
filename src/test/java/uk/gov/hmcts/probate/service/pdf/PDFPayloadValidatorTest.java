package uk.gov.hmcts.probate.service.pdf;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.model.pdf.PDFServiceTemplate;
import uk.gov.hmcts.probate.util.TestUtils;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PDFPayloadValidatorTest {

    @Autowired
    PDFPayloadValidator pdfPayloadValidator;

    @Test
    public void shouldValidatePayload() throws IOException {

        String jsonPayload = new TestUtils().getJsonFromFile("success.legalStatementPayload.json");
        boolean result = pdfPayloadValidator.validatePayload(jsonPayload, PDFServiceTemplate.LEGAL_STATEMENT);

        MatcherAssert.assertThat(result, Matchers.is(true));
    }

}
