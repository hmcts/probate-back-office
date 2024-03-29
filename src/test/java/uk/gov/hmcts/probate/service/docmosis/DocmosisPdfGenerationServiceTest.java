package uk.gov.hmcts.probate.service.docmosis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.config.properties.docmosis.TemplateProperties;
import uk.gov.hmcts.probate.exception.PDFGenerationException;
import uk.gov.hmcts.probate.model.docmosis.PdfDocumentRequest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.probate.model.ProbateDocumentType.CAVEAT_RAISED;

class DocmosisPdfGenerationServiceTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    TemplateProperties templateProperties;
    @Mock
    RestTemplate restTemplate;
    @Mock
    ResponseEntity<byte[]> responseEntity;
    @InjectMocks
    private DocmosisPdfGenerationService docmosisPdfGenerationService = new DocmosisPdfGenerationService();

    @BeforeEach
    public void setup() throws IllegalAccessException {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBasicDocmosisPdfGenerationServiceCall() {

        ResponseEntity<byte[]> responseEntityBytes = ResponseEntity.ok("A String".getBytes());
        when(restTemplate
            .postForEntity((String) isNull(), any(PdfDocumentRequest.class), eq(byte[].class)))
            .thenReturn(responseEntityBytes);

        Map<String, Object> registry = new HashMap<>();
        registry.put("name", "Bristol District Probate Registry");
        registry.put("phone", "02920 474373");
        registry.put("emailReplyToId", "6d98cad6-adb4-4446-b37e-5c3f0441a0c8");
        registry.put("addressLine1", "3rd Floor, Cardiff Magistrates’ Court");
        registry.put("addressLine2", "Fitzalan Place");
        registry.put("addressLine3", "Cardiff");
        registry.put("addressLine4", "");
        registry.put("town", "South Wales");
        registry.put("postcode", "CF24 0RZ");

        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put("caseReference", "1111-2222-3333-4444");
        placeholders.put("generatedDate", "13052019");
        placeholders.put("registry", registry);
        placeholders.put("PA8AURL", "www.citizensadvice.org.uk|https://www.citizensadvice.org.uk/");
        placeholders.put("hmctsfamily", "image:base64:" + null);

        byte[] result = docmosisPdfGenerationService.generateDocFrom(CAVEAT_RAISED.getTemplateName(),
            placeholders);

        assertEquals(8, result.length);
    }

    @Test
    void shouldThrowPDFGeneratedException() {
        assertThrows(PDFGenerationException.class, () -> {
            Map<String, Object> registry = new HashMap<>();
            Map<String, Object> placeholders = new HashMap<>();

            docmosisPdfGenerationService.generateDocFrom(CAVEAT_RAISED.getTemplateName(), placeholders);
        });
    }
}
