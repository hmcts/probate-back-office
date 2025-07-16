package uk.gov.hmcts.probate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.template.DocumentResponse;
import uk.gov.hmcts.probate.service.template.printservice.PrintService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class PrintServiceTemplateControllerTest {

    @InjectMocks
    private PrintServiceTemplateController underTest;

    @Mock
    private PrintService printServiceMock;

    @Mock
    private CaseDetails caseDetailsMock;

    @Mock
    private BindingResult bindingResultMock;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldErrorForLegalStatement() {
        assertThrows(BadRequestException.class, () -> {
            when(bindingResultMock.hasErrors()).thenReturn(true);

            underTest.getAllDocuments(caseDetailsMock, bindingResultMock);
        });
    }

    @Test
    void shouldReturnAllDocumentsWithNoErrors() {
        List<DocumentResponse> docs = new ArrayList<>();
        DocumentResponse doc = new DocumentResponse("name", "type", "url");
        docs.add(doc);
        when(printServiceMock.getAllDocuments(caseDetailsMock)).thenReturn(docs);
        when(bindingResultMock.hasErrors()).thenReturn(false);

        ResponseEntity<List> response = underTest.getAllDocuments(caseDetailsMock, bindingResultMock);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(docs, response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(doc, response.getBody().get(0));
    }

    @Test
    void shouldReturnSolicitorTemplateWithNoErrors() {
        when(printServiceMock.getSolicitorCaseDetailsTemplateForPrintService()).thenReturn("some template");

        ResponseEntity<String> response = underTest.getSolicitorCaseDetailsTemplate();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("some template", response.getBody());
    }

    @Test
    void shouldReturnPATemplateWithNoErrors() {
        when(printServiceMock.getPACaseDetailsTemplateForPrintService()).thenReturn("some pa template");

        ResponseEntity<String> response = underTest.getPACaseDetailsTemplate();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("some pa template", response.getBody());
    }
}
