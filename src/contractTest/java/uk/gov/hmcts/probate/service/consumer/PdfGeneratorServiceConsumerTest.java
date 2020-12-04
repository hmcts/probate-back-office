package uk.gov.hmcts.probate.service.consumer;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactHttpsProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.service.FileSystemResourceService;
import uk.gov.hmcts.probate.service.template.pdf.PDFGeneratorService;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.pdf.service.client.GeneratePdfRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PdfGeneratorServiceConsumerTest {

    private static final String HTML = ".html";

    @Autowired
    PDFGeneratorService pdfGenerationService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private PDFServiceConfiguration pdfServiceConfiguration;

    @Autowired
    private FileSystemResourceService fileSystemResourceService;

    @MockBean
    private AuthTokenGenerator serviceTokenGenerator;

    private final String someServiceAuthToken = "someServiceAuthToken";
    private static final String SERVICE_AUTHORIZATION_HEADER = "ServiceAuthorization";

    @Rule
    public PactHttpsProviderRuleMk2 mockProvider = new PactHttpsProviderRuleMk2("rpePdfService_PDFGenerationEndpointV2", "localhost", 4411, this);

    // TBD consumer 'Name'
    @Pact(provider = "rpePdfService_PDFGenerationEndpointV2", consumer = "probate_backOffice")
    public RequestResponsePact generatePdfFromTemplate(PactDslWithProvider builder) throws JSONException, IOException {
        // @formatter:off
        return builder
            .given("A request to generate a Probate PDF document")
            .uponReceiving("A request to generate a Probate PDF document")
            .method("POST")
            .headers(SERVICE_AUTHORIZATION_HEADER, someServiceAuthToken)
            .body(createJsonObject(buildGenerateDocumentRequest(DocumentType.WILL_LODGEMENT_DEPOSIT_RECEIPT.getTemplateName(), createJsonObjectAsString("willLodgementPayload.json"))),
                "application/vnd.uk.gov.hmcts.pdf-service.v2+json;charset=UTF-8")
            .path("/pdfs")
            .willRespondWith()
            .matchHeader(org.springframework.http.HttpHeaders.CONTENT_TYPE, "application/pdf")
            .status(HttpStatus.SC_OK)
            .toPact();
    }

    @Test
    @PactVerification(fragment = "generatePdfFromTemplate")
    public void verifyGeneratePdfFromTemplatePact() throws IOException, JSONException {

        when(serviceTokenGenerator.generate()).thenReturn(someServiceAuthToken);

        EvidenceManagementFileUpload response = pdfGenerationService
            .generatePdf(DocumentType.WILL_LODGEMENT_DEPOSIT_RECEIPT,
                createJsonObjectAsString("willLodgementPayload.json"));

    }


    private GeneratePdfRequest buildGenerateDocumentRequest(String templateName, String callBackRequestJson)
        throws IOException {
        String templatePath = pdfServiceConfiguration.getTemplatesDirectory() + templateName + HTML;
        String templateAsString = fileSystemResourceService.getFileFromResourceAsString(templatePath);

        Map<String, Object> paramMap = this.asMap(callBackRequestJson);
        return new GeneratePdfRequest(templateAsString, paramMap);

    }

    protected String createJsonObjectAsString(String fileName) throws JSONException, IOException {
        File file = getFile(fileName);
        return new String(Files.readAllBytes(file.toPath()));
    }


    protected String createJsonObject(Object obj) throws JSONException, IOException {
        return objectMapper.writeValueAsString(obj);
    }

    private Map<String, Object> asMap(String placeholderValues) throws IOException {
        return objectMapper.readValue(placeholderValues, new TypeReference<HashMap<String, Object>>() {
        });
    }


    private File getFile(String fileName) throws FileNotFoundException {
        return ResourceUtils.getFile(this.getClass().getResource("/json/" + fileName));
    }
}
