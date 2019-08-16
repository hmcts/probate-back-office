package uk.gov.hmcts.probate.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ExecutorsApplyingNotification;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.validator.EmailAddressExecutorsApplyingValidationRule;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
public class InformationRequestServiceTest {

    @InjectMocks
    private InformationRequestService informationRequestService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private CallbackRequest callbackRequest;

    @Mock
    private CaseData caseData;

    @Mock
    private CaseDetails caseDetails;

    @Mock
    private Document document;

    @Mock
    private EmailAddressExecutorsApplyingValidationRule emailAddressExecutorsApplyingValidationRule;

    private Document sentEmail;
    private List<Document> documents = new ArrayList<>();

    private static final long ID = 1234567891234567L;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final String SENT_EMAIL_FILE_NAME = "sentEmail.pdf";

    private static final List<CollectionMember<ExecutorsApplyingNotification>> EXECUTORS_APPLYING = Arrays.asList(
            new CollectionMember("id",
                    ExecutorsApplyingNotification.builder()
                            .name("fred")
                            .email("test@test.com")
                            .notification("yes")
                            .build()));

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        caseData = CaseData.builder()
                .registryLocation("leeds")
                .deceasedForenames("name")
                .deceasedSurname("name")
                .executorsApplyingNotifications(EXECUTORS_APPLYING)
                .build();

        sentEmail = Document.builder().documentFileName(SENT_EMAIL_FILE_NAME).build();


        when(caseDetails.getData()).thenReturn(caseData);
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);

    }

    @Test
    public void shouldEmailInformationRequestSuccessful() throws NotificationClientException {

        caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);

        when(notificationService.sendEmail(any(), any(), any())).thenReturn(sentEmail);

        informationRequestService.emailInformationRequest(caseDetails);

    }

    @Test
    public void shouldThrowNotificationClientExceptionEmailInformationRequest() throws NotificationClientException {

        caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);

        when(notificationService.sendEmail(any(), any(), any())).thenReturn(sentEmail).thenThrow(NotificationClientException.class);

        informationRequestService.emailInformationRequest(caseDetails);

    }

}