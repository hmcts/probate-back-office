package uk.gov.hmcts.probate.controller;

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
import uk.gov.hmcts.probate.service.pdf.PDFTemplateService;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class PDFGeneratorControllerTest {

    public static final String SOME_DATA = "{\"legalStatementData\": \"someData\"}";

    @InjectMocks
    PDFGeneratorController pdfGeneratorController;

    @Mock
    PDFTemplateService pdfTemplateService;

    @Mock
    ResponseEntity responseEntityMock;

    @Mock
    PDFMissingPayloadException pdfMissingPayloadExceptionMock;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGenerateLegalStatementWithNoErrors() throws IOException {
        when(pdfTemplateService.validateAndGeneratePDF(SOME_DATA, PDFServiceTemplate.LEGAL_STATEMENT))
            .thenReturn(responseEntityMock);
        ResponseEntity response = pdfGeneratorController.generateLegalStatement(SOME_DATA);

        MatcherAssert.assertThat(response, Matchers.is(responseEntityMock));
    }

    @Test
    public void shouldGenerateLegalStatementWithErrorsForMissingFields() throws IOException {
        when(pdfTemplateService.validateAndGeneratePDF(any(String.class), any(PDFServiceTemplate.class)))
            .thenThrow(pdfMissingPayloadExceptionMock);
        try {
            ResponseEntity response = pdfGeneratorController.generateLegalStatement(SOME_DATA);
        } catch (PDFMissingPayloadException e) {
            MatcherAssert.assertThat(e, Matchers.is(pdfMissingPayloadExceptionMock));
        }
    }
}
