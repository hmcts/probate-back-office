package uk.gov.hmcts.probate.service.pdf;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.probate.controller.exception.PDFMissingPayloadException;
import uk.gov.hmcts.probate.model.pdf.PDFServiceTemplate;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.pdf.PDFServiceTemplate.LEGAL_STATEMENT;

public class PDFTemplateServiceTest {


    public static final String SOME_DATA = "{\"legalStatementData\": \"someData\"}";

    @InjectMocks
    PDFTemplateService pdfTemplateService;

    @Mock
    PDFGeneratorService pdfGeneratorServiceMock;

    @Mock
    ResponseEntity<byte[]> responseEntityMock;

    @Mock
    PDFPayloadValidator pdfPayloadValidatorMock;

    @Mock
    PDFMissingPayloadException pdfMissingPayloadExceptionMock;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGenerateLegalStatementWithNoErrors() throws IOException {
        when(pdfPayloadValidatorMock.validatePayload(any(String.class), any(PDFServiceTemplate.class))).thenReturn(true);
        when(pdfGeneratorServiceMock.generatePdf(LEGAL_STATEMENT, SOME_DATA))
            .thenReturn(responseEntityMock);
        ResponseEntity response = pdfTemplateService.validateAndGeneratePDF(SOME_DATA, LEGAL_STATEMENT);

        MatcherAssert.assertThat(response, Matchers.is(responseEntityMock));
    }

    @Test
    public void shouldGenerateLegalStatementWithErrorsForMissingFields() throws IOException {
        when(pdfPayloadValidatorMock.validatePayload(any(String.class), any(PDFServiceTemplate.class)))
            .thenThrow(pdfMissingPayloadExceptionMock);
        try {
            ResponseEntity response = pdfTemplateService.validateAndGeneratePDF(SOME_DATA, LEGAL_STATEMENT);
        } catch (PDFMissingPayloadException e) {
            MatcherAssert.assertThat(e, Matchers.is(pdfMissingPayloadExceptionMock));
        }
    }
}
