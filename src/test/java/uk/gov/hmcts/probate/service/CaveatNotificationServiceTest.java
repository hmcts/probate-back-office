package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.State;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.caveat.response.ResponseCaveatData;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.service.docmosis.CaveatDocmosisService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.BulkPrintValidationRule;
import uk.gov.hmcts.probate.validator.ValidationRuleCaveats;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.DocumentType.CAVEAT_COVERSHEET;
import static uk.gov.hmcts.probate.model.DocumentType.CAVEAT_RAISED;


public class CaveatNotificationServiceTest {

    @InjectMocks
    private CaveatNotificationService caveatNotificationService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private EventValidationService eventValidationService;

    @Mock
    private List<ValidationRuleCaveats> emailAddressNotificationValidationRules;

    @Mock
    private List<BulkPrintValidationRule> bulkPrintValidationRules;

    @Mock
    private CaveatDocmosisService caveatDocmosisService;

    @Mock
    private CaveatCallbackResponseTransformer caveatCallbackResponseTransformer;

    @Mock
    Map<String, Object> placeholders;

    @Mock
    private PDFManagementService pdfManagementService;

    @Mock
    BulkPrintService bulkPrintService;

    @Mock
    private CaveatCallbackResponse caveatCallbackResponse;

    private Document coversheet;
    private Document caveatRaised;
    private Document sentEmail;
    private CaveatData caveatData;
    private CaveatDetails caveatDetails;
    private CaveatCallbackRequest caveatCallbackRequest;
    private ResponseCaveatData responseCaveatData;
    private List<Document> documents = new ArrayList<>();

    private static final long ID = 1234567891234567L;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final String SENT_EMAIL_FILE_NAME = "sentEmail.pdf";
    private static final String COVERSHEET_FILE_NAME = "sentEmail.pdf";
    private static final String CAVEAT_RAISED_FILE_NAME = "sentEmail.pdf";
    private static final List<CollectionMember<Document>> DOCUMENTS_LIST = Arrays.asList(
            new CollectionMember("id",
                    Document.builder()
                            .documentFileName(SENT_EMAIL_FILE_NAME)
                            .build()));

    private static final List<CollectionMember<Document>> DOCUMENTS_LIST_CAVEAT_RAISED = Arrays.asList(
            new CollectionMember("id",
                    Document.builder()
                            .documentFileName(COVERSHEET_FILE_NAME)
                            .build()),
            new CollectionMember("id",
                    Document.builder()
                            .documentFileName(CAVEAT_RAISED_FILE_NAME)
                            .build()));

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        caveatData = CaveatData.builder()
                .registryLocation("leeds")
                .caveatorEmailAddress("test@test.com")
                .deceasedForenames("name")
                .deceasedSurname("name")
                .build();

        responseCaveatData = ResponseCaveatData.builder()
                .registryLocation("leeds")
                .caveatorEmailAddress("test@test.com")
                .deceasedForenames("name")
                .deceasedSurname("name")
                .caveatRaisedEmailNotificationRequested("Yes")
                .build();

        sentEmail = Document.builder().documentFileName(SENT_EMAIL_FILE_NAME).build();
        coversheet = Document.builder().documentFileName(COVERSHEET_FILE_NAME).build();
        caveatRaised = Document.builder().documentFileName(CAVEAT_RAISED_FILE_NAME).build();

    }

    @Test
    public void testCaveatRaiseWithEmail() throws NotificationClientException {
        caveatData = CaveatData.builder()
                .caveatRaisedEmailNotificationRequested("Yes")
                .build();

        documents.add(sentEmail);

        responseCaveatData = ResponseCaveatData.builder()
                .notificationsGenerated(DOCUMENTS_LIST)
                .build();

        caveatDetails = new CaveatDetails(caveatData, LAST_MODIFIED, ID);
        caveatCallbackRequest = new CaveatCallbackRequest(caveatDetails);

        when(eventValidationService.validateCaveatRequest(any(CaveatCallbackRequest.class), any(List.class)))
                .thenReturn(caveatCallbackResponse.builder().errors(new ArrayList<>()).build());
        when(notificationService.sendCaveatEmail(State.CAVEAT_RAISED, caveatDetails)).thenReturn(Document.builder().documentFileName(SENT_EMAIL_FILE_NAME).build());

        caveatCallbackResponse = CaveatCallbackResponse.builder().caveatData(responseCaveatData).build();
        when(caveatCallbackResponseTransformer.caveatRaised(caveatCallbackRequest, documents, null)).thenReturn(caveatCallbackResponse);

        caveatNotificationService.caveatRaise(caveatCallbackRequest);

        assertEquals(1, caveatCallbackResponse.getCaveatData().getNotificationsGenerated().size());
    }

    @Test
    public void testCaveatRaiseWithNoEmailNoBulkPrint() throws NotificationClientException {
        caveatData = CaveatData.builder()
                .caveatRaisedEmailNotificationRequested("No")
                .sendToBulkPrintRequested("No")
                .build();

        responseCaveatData = ResponseCaveatData.builder()
                .notificationsGenerated(DOCUMENTS_LIST_CAVEAT_RAISED)
                .build();

        documents.add(coversheet);
        documents.add(caveatRaised);

        caveatDetails = new CaveatDetails(caveatData, LAST_MODIFIED, ID);
        caveatCallbackRequest = new CaveatCallbackRequest(caveatDetails);

        when(caveatDocmosisService.caseDataAsPlaceholders(caveatDetails)).thenReturn(placeholders);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, CAVEAT_COVERSHEET)).thenReturn(coversheet);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, CAVEAT_RAISED)).thenReturn(caveatRaised);
        caveatCallbackResponse = CaveatCallbackResponse.builder().caveatData(responseCaveatData).build();
        when(caveatCallbackResponseTransformer.caveatRaised(caveatCallbackRequest, documents, null)).thenReturn(caveatCallbackResponse);

        caveatNotificationService.caveatRaise(caveatCallbackRequest);

        assertEquals(2, caveatCallbackResponse.getCaveatData().getNotificationsGenerated().size());
    }

    @Test
    public void testCaveatRaiseWithNoEmailBulkPrintValidSendLetter() throws NotificationClientException {
        caveatData = CaveatData.builder()
                .caveatRaisedEmailNotificationRequested("No")
                .sendToBulkPrintRequested("Yes")
                .build();

        responseCaveatData = ResponseCaveatData.builder()
                .registryLocation("leeds")
                .caveatorEmailAddress("test@test.com")
                .deceasedForenames("name")
                .deceasedSurname("name")
                .caveatRaisedEmailNotificationRequested("Yes")
                .notificationsGenerated(DOCUMENTS_LIST_CAVEAT_RAISED)
                .build();


        documents.add(coversheet);
        documents.add(caveatRaised);

        caveatCallbackResponse = CaveatCallbackResponse.builder().caveatData(responseCaveatData).build();
        caveatDetails = new CaveatDetails(caveatData, LAST_MODIFIED, ID);
        caveatCallbackRequest = new CaveatCallbackRequest(caveatDetails);
        SendLetterResponse sendLetterResponse = new SendLetterResponse(UUID.randomUUID());
        when(caveatDocmosisService.caseDataAsPlaceholders(caveatDetails)).thenReturn(placeholders);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, CAVEAT_COVERSHEET)).thenReturn(coversheet);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, CAVEAT_RAISED)).thenReturn(caveatRaised);
        when(bulkPrintService.sendToBulkPrint(caveatCallbackRequest, caveatRaised, coversheet)).thenReturn(sendLetterResponse);
        when(eventValidationService.validateCaveatBulkPrintResponse(eq(sendLetterResponse.letterId.toString()), any(List.class)))
                .thenReturn(caveatCallbackResponse.builder().errors(new ArrayList<>()).build());

        when(caveatCallbackResponseTransformer.caveatRaised(caveatCallbackRequest, documents, null)).thenReturn(caveatCallbackResponse);

        caveatNotificationService.caveatRaise(caveatCallbackRequest);

        assertEquals(2, caveatCallbackResponse.getCaveatData().getNotificationsGenerated().size());
    }

}
