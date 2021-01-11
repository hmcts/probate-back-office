package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ExecutorsApplyingNotification;
import uk.gov.hmcts.probate.model.ccd.raw.BulkPrint;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.EmailAddressExecutorsApplyingValidationRule;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.DocumentType.SENT_EMAIL;
import static uk.gov.hmcts.probate.model.DocumentType.SOT_INFORMATION_REQUEST;

public class RedeclarationNotificationServiceTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private NotificationClientException notificationClientException;

    @Mock
    private EmailAddressExecutorsApplyingValidationRule emailAddressExecutorsApplyingValidationRule;

    @Mock
    private CallbackResponseTransformer callbackResponseTransformer;

    @InjectMocks
    private RedeclarationNotificationService redeclarationNotificationService;

    private CollectionMember<ExecutorsApplyingNotification> execApplying;
    private CollectionMember<ExecutorsApplyingNotification> execApplying2;
    private CaseData caseData;
    private CaseDetails caseDetails;
    private CallbackRequest callbackRequest;
    private List<CollectionMember<ExecutorsApplyingNotification>> executorsApplying;
    private List<CollectionMember<Document>> documentListSOT;
    private List<CollectionMember<Document>> documentListEmail;
    private List<CollectionMember<BulkPrint>> bulkPrintIds;

    private static final SolsAddress ADDRESS =
            SolsAddress.builder().addressLine1("Address line 1").postCode("AB1 2CD").build();
    private static final String[] LAST_MODIFIED = {"2018", "1", "2", "0", "0", "0", "0"};
    private static final Long ID = 123456789L;
    private static final Document SOT_DOCUMENT =
            Document.builder().documentType(SOT_INFORMATION_REQUEST).documentFileName("file1").build();
    private static final Document SOT_DOCUMENT_2 =
            Document.builder().documentType(SENT_EMAIL).documentFileName("file2").build();
    private static final Document SENT_EMAIL_DOCUMENT =
            Document.builder().documentType(DocumentType.SENT_EMAIL).build();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        documentListSOT = new ArrayList<>();
        documentListEmail = new ArrayList<>();
        executorsApplying = new ArrayList<>();
        bulkPrintIds = new ArrayList<>();

        execApplying = buildExec("1", "Bob Smith", "executor1@probate-test.com", "Yes");
        execApplying2 = buildExec("2", "John Smith", "executor2@probate-test.com", "Yes");

        executorsApplying.add(execApplying);
        executorsApplying.add(execApplying2);

        caseData = CaseData.builder()
                .executorsApplyingNotifications(executorsApplying)
                .boRequestInfoSendToBulkPrintRequested("Yes")
                .paperForm("No")
                .build();
        caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);
        callbackRequest = new CallbackRequest(caseDetails);

        documentListEmail.add(new CollectionMember<>(SENT_EMAIL_DOCUMENT));
        documentListSOT.add(new CollectionMember<>(SOT_DOCUMENT));

        ResponseCaseData letterResponseCaseData =
                ResponseCaseData.builder()
                        .probateNotificationsGenerated(documentListEmail)
                        .probateSotDocumentsGenerated(documentListSOT)
                        .build();
        CallbackResponse callbackResponse = CallbackResponse.builder().data(letterResponseCaseData).build();


        when(callbackResponseTransformer.addDocuments(any(), any(), any(), any())).thenReturn(callbackResponse);
    }

    @Test
    public void handleRedeclarationNotificationShouldBeSuccessful() {
        CallbackResponse response = redeclarationNotificationService.handleRedeclarationNotification(callbackRequest);

        assertEquals(1, response.getData().getProbateSotDocumentsGenerated().size());
        assertEquals(SOT_INFORMATION_REQUEST,
                response.getData().getProbateSotDocumentsGenerated().get(0).getValue().getDocumentType());
        assertEquals(1, response.getData().getProbateNotificationsGenerated().size());
        assertEquals(SENT_EMAIL,
                response.getData().getProbateNotificationsGenerated().get(0).getValue().getDocumentType());
    }

    private CollectionMember<ExecutorsApplyingNotification> buildExec(String item, String name, String email,
                                                                      String applying) {
        return new CollectionMember<>(item, ExecutorsApplyingNotification.builder()
                .name(name)
                .address(ADDRESS)
                .email(email)
                .notification(applying)
                .build());
    }

}
