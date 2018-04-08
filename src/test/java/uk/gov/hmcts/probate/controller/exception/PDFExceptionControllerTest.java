package uk.gov.hmcts.probate.controller.exception;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.hmcts.probate.model.BusinessValidationError;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static uk.gov.hmcts.probate.model.pdf.PDFServiceTemplate.LEGAL_STATEMENT;

public class PDFExceptionControllerTest {

    @InjectMocks
    PDFExceptionController pdfExceptionController;

    @Mock
    HttpClientErrorException httpClientErrorExceptionMock;

    @Mock
    PDFClientException pdfClientExceptionMock;

    @Mock
    PDFMissingPayloadException pdfMissingPayloadExceptionMock;

    @Mock
    BusinessValidationMessageService businessValidationMessageServiceMock;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldHandleBadRequestAsStatusServiceUnavaiable() {
        when(pdfClientExceptionMock.getHttpClientErrorException()).thenReturn(httpClientErrorExceptionMock);
        when(httpClientErrorExceptionMock.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        ResponseEntity<byte[]> response = pdfExceptionController.handle(pdfClientExceptionMock);

        assertThat(response.getStatusCode(), is(SERVICE_UNAVAILABLE));
    }

    @Test
    public void shouldHandleBadGatewaytAsStatusOK() {
        when(pdfClientExceptionMock.getHttpClientErrorException()).thenReturn(httpClientErrorExceptionMock);
        when(httpClientErrorExceptionMock.getStatusCode()).thenReturn(HttpStatus.BAD_GATEWAY);
        ResponseEntity<byte[]> response = pdfExceptionController.handle(pdfClientExceptionMock);

        assertThat(response.getStatusCode(), is(OK));
    }

    @Test
    public void shouldHandleMissingPDFDatatAsStatusUN() {
        List<String> keys = new ArrayList<>();
        keys.add("key1");
        keys.add("key2");
        String[] args1 = {LEGAL_STATEMENT.getHtmlFileName(), "key1"};
        String[] args2 = {LEGAL_STATEMENT.getHtmlFileName(), "key2"};
        BusinessValidationError bve1Mock = Mockito.mock(BusinessValidationError.class);
        BusinessValidationError bve2Mock = Mockito.mock(BusinessValidationError.class);
        when(pdfMissingPayloadExceptionMock.getMissingPayloadKeys()).thenReturn(keys);
        when(pdfMissingPayloadExceptionMock.getPdfServiceTemplate()).thenReturn(LEGAL_STATEMENT);
        when(businessValidationMessageServiceMock.generateError(null, "missingPDFPayload", args1)).thenReturn(bve1Mock);
        when(businessValidationMessageServiceMock.generateError(null, "missingPDFPayload", args2)).thenReturn(bve2Mock);

        ResponseEntity<List<BusinessValidationError>> response = pdfExceptionController.handle(pdfMissingPayloadExceptionMock);

        assertThat(response.getStatusCode(), is(UNPROCESSABLE_ENTITY));
        assertThat(response.getBody().get(0), is(bve1Mock));
        assertThat(response.getBody().get(1), is(bve2Mock));
    }

}
