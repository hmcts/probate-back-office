package uk.gov.hmcts.probate;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.util.TestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PDFGeneratorComponentTest extends ComponentTestBase {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
        MediaType.APPLICATION_JSON.getSubtype(),
        Charset.forName("utf8"));

    protected static final String PDF_GENERATOR_LEGAL_STATEMENT_URL = "/pdf-generator/legal-statement";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    protected TestUtils utils;

    private MockMvc mockMvc;

    @Value("${pdf.service.url}")
    private String pdfServiceUrl;

    @Value("${pdf.service.pdfApi}")
    private String pdfServicePdfApi;

    @Before
    public void setup() throws Exception {

        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void shouldReturnPDFForLegalStatement() throws Exception {
        if (isPDFServiceAvailable()) {
            MvcResult result = validatePostSuccess("success.legalStatementPayload.json");

            String textContent = textContentOf(result.getResponse().getContentAsByteArray());
            MatcherAssert.assertThat(textContent, StringContains.containsString("12345678"));
        }
    }

    @Test
    public void shouldReturnErrorForInvalidLegalStatement() throws Exception {
        if (isPDFServiceAvailable()) {
            mockMvc.perform(post(PDF_GENERATOR_LEGAL_STATEMENT_URL)
                .content(utils.getJsonFromFile("failure.invalidLegalStatementPayload.json"))
                .contentType(contentType))
                .andExpect(status().isUnprocessableEntity())
                .andReturn();
        }
    }

    @Test
    @Ignore
    public void shouldReturn503ForInvalidPayload() throws Exception {
        //TODO cannot run this test to use an invalid html template without changing the template itself
        if (isPDFServiceAvailable()) {
            MvcResult result = mockMvc.perform(post(PDF_GENERATOR_LEGAL_STATEMENT_URL)
                .content(utils.getJsonFromFile("failure.invalidLegalStatementPayload.json"))
                .contentType(contentType))
                .andExpect(status().is5xxServerError())
                .andReturn();
        }
    }

    private MvcResult validatePostSuccess(String jsonFile) throws Exception {
        return mockMvc.perform(post(PDF_GENERATOR_LEGAL_STATEMENT_URL)
            .content(utils.getJsonFromFile(jsonFile))
            .contentType(contentType))
            .andExpect(status().isOk())
            .andReturn();
    }

    private static String textContentOf(byte[] pdfData) throws IOException {
        PDDocument pdfDocument = PDDocument.load(new ByteArrayInputStream(pdfData));
        try {
            return new PDFTextStripper().getText(pdfDocument);
        } finally {
            pdfDocument.close();
        }
    }

    private boolean isPDFServiceAvailable() {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = URI.create(String.format("%s%s", pdfServiceUrl, pdfServicePdfApi, "/info"));

        try {
            String response = restTemplate.getForObject(uri, String.class);
            return response.equals("{}");
        } catch (Exception e) {
            return false;
        }
    }
}
