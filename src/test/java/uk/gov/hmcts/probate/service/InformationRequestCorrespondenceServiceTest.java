package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.State;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.service.notify.NotificationClientException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class InformationRequestCorrespondenceServiceTest {

    private static final String[] LAST_MODIFIED = {"2018", "1", "2", "0", "0", "0", "0"};
    private static final Long ID = 123456789L;
    private static final Document GENERIC_DOCUMENT =
        Document.builder().documentType(DocumentType.SENT_EMAIL).build();
    private static final Document COVERSHEET = Document.builder().documentType(DocumentType.GRANT_COVER).build();
    @InjectMocks
    private InformationRequestCorrespondenceService informationRequestCorrespondenceService;
    @Mock
    private NotificationService notificationService;
    private CaseDetails caseDetails;
    private CaseData caseData;
    private CallbackRequest callbackRequest;
    private CaseData caseDataMultiple;
    private CaseDetails caseDetailsMultiple;

    @BeforeEach
    public void setup() throws NotificationClientException {
        MockitoAnnotations.openMocks(this);

        caseData = CaseData.builder().build();
        caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);
        callbackRequest = new CallbackRequest(caseDetails);

        when(notificationService
            .sendEmail(eq(State.CASE_STOPPED_REQUEST_INFORMATION), eq(caseDetails), any(Optional.class)))
            .thenReturn(GENERIC_DOCUMENT);
    }

    @Test
    void testEmailInformationRequestMultipleExecSuccessful() throws NotificationClientException {
        caseDataMultiple = CaseData.builder()
            .build();
        caseDetailsMultiple = new CaseDetails(caseDataMultiple, LAST_MODIFIED, ID);

        when(notificationService.sendEmail(eq(State.CASE_STOPPED_REQUEST_INFORMATION), eq(caseDetails)))
            .thenReturn(GENERIC_DOCUMENT);
        List<Document> response = informationRequestCorrespondenceService.emailInformationRequest(caseDetails);
        assertEquals(GENERIC_DOCUMENT, response.get(0));

    }

    @Test
    void testEmailInformationRequestSuccessful() throws NotificationClientException {
        when(notificationService.sendEmail(eq(State.CASE_STOPPED_REQUEST_INFORMATION), eq(caseDetails)))
            .thenReturn(GENERIC_DOCUMENT);
        assertEquals(GENERIC_DOCUMENT,
            informationRequestCorrespondenceService.emailInformationRequest(caseDetails).get(0));
    }
}
