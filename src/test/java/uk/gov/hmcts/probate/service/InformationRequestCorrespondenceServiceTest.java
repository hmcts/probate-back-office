package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.State;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.service.notify.NotificationClientException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class InformationRequestCorrespondenceServiceTest {

    private static final String[] LAST_MODIFIED = {"2018", "1", "2", "0", "0", "0", "0"};
    private static final Long ID = 123456789L;
    private static final Document GENERIC_DOCUMENT =
        Document.builder().documentType(DocumentType.SENT_EMAIL).build();
    @InjectMocks
    private InformationRequestCorrespondenceService informationRequestCorrespondenceService;
    @Mock
    private NotificationService notificationService;
    private CaseDetails caseDetails;
    private CaseData caseData;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        caseData = CaseData.builder().build();
        caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);
    }

    @Test
    void testEmailInformationRequestSuccessful() throws NotificationClientException {
        when(notificationService.sendEmail(State.CASE_STOPPED_REQUEST_INFORMATION, caseDetails))
            .thenReturn(GENERIC_DOCUMENT);
        assertEquals(GENERIC_DOCUMENT,
            informationRequestCorrespondenceService.emailInformationRequest(caseDetails).get(0));
    }
}
