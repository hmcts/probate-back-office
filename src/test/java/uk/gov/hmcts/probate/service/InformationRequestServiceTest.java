package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ExecutorsApplyingNotification;
import uk.gov.hmcts.probate.model.State;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.validator.EmailAddressExecutorsApplyingValidationRule;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class InformationRequestServiceTest {

    @InjectMocks
    private InformationRequestService informationRequestService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private DocumentGeneratorService documentGeneratorService;

    @Mock
    private EmailAddressExecutorsApplyingValidationRule emailAddressExecutorsApplyingValidationRule;

    @Mock
    private BulkPrintService bulkPrintService;

    private CaseDetails caseDetails;
    private CaseData caseData;
    private CallbackRequest callbackRequest;
    private List<Document> documents;

    private static final String[] LAST_MODIFIED = {"2018", "1", "2", "0", "0", "0", "0"};
    private static final Long ID = 123456789L;
    private static final SolsAddress ADDRESS =
            SolsAddress.builder().addressLine1("Address line 1").postCode("AB1 2CD").build();
    private static final Document GENERIC_DOCUMENT =
            Document.builder().documentType(DocumentType.SOT_INFORMATION_REQUEST).build();
    private static final Document COVERSHEET = Document.builder().documentType(DocumentType.GRANT_COVER).build();

    @Before
    public void setup() throws NotificationClientException {
        documents = new ArrayList<>();
        MockitoAnnotations.initMocks(this);
        CollectionMember<ExecutorsApplyingNotification> execApplying = new CollectionMember<>("1",
                ExecutorsApplyingNotification.builder()
                        .email("test@test.com")
                        .address(ADDRESS)
                        .name("Fred Smith")
                        .notification("Yes").build());

        List<CollectionMember<ExecutorsApplyingNotification>> executorsApplying = new ArrayList<>();
        executorsApplying.add(execApplying);
        caseData = CaseData.builder()
                .executorsApplyingNotifications(executorsApplying)
                .boRequestInfoSendToBulkPrintRequested("Yes").build();
        caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);
        callbackRequest = new CallbackRequest(caseDetails);
        documents.add(COVERSHEET);
        documents.add(GENERIC_DOCUMENT);

        when(notificationService.sendEmail(eq(State.CASE_STOPPED_REQUEST_INFORMATION), eq(caseDetails), any()))
                .thenReturn(GENERIC_DOCUMENT);
        when(documentGeneratorService.generateCoversheet(callbackRequest)).thenReturn(COVERSHEET);
        when(documentGeneratorService.generateRequestForInformation(caseDetails)).thenReturn(GENERIC_DOCUMENT);
        when(bulkPrintService.sendToBulkPrint(callbackRequest, COVERSHEET, GENERIC_DOCUMENT, true))
                .thenReturn("123");
    }

    @Test
    public void testEmailInformationRequestSuccessful() {
        assertEquals(GENERIC_DOCUMENT,
                informationRequestService.emailInformationRequest(caseDetails).get(0));
    }

    @Test
    public void testGenerateLetterWithCoversheetReturnsSuccessful() {
        assertEquals(documents, informationRequestService.generateLetterWithCoversheet(callbackRequest));
    }

    @Test
    public void testGetLetterIdReturnsSuccessful() {
        assertEquals("123", informationRequestService.getLetterId(documents, callbackRequest).get(0));
    }

}