package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.EmailAddressNotifyApplicantValidationRule;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class InformationRequestServiceTest {

    private static final String[] LAST_MODIFIED = {"2018", "1", "2", "0", "0", "0", "0"};
    private static final Long ID = 123456789L;
    private static final Document SENT_EMAIL_DOCUMENT =
        Document.builder().documentType(DocumentType.SENT_EMAIL).build();
    private static final List<CollectionMember<BulkPrint>> BULK_PRINT_IDS =
        Arrays.asList(new CollectionMember<>(BulkPrint.builder().sendLetterId("123").build()),
            new CollectionMember<>(BulkPrint.builder().sendLetterId("321").build()));
    private static final Optional<UserInfo> CASEWORKER_USERINFO = Optional.ofNullable(UserInfo.builder()
            .familyName("familyName")
            .givenName("givenname")
            .roles(Arrays.asList("caseworker-probate"))
            .build());

    @Mock
    private InformationRequestCorrespondenceService informationRequestCorrespondenceService;
    @Mock
    private CallbackResponseTransformer callbackResponseTransformer;
    @Mock
    private EmailAddressNotifyApplicantValidationRule emailAddressNotifyApplicantValidationRule;

    @InjectMocks
    private InformationRequestService informationRequestService;
    @Mock
    private NotificationService notificationService;
    private CaseData caseData;
    private CaseDetails caseDetails;
    private CallbackRequest callbackRequest;
    private List<CollectionMember<Document>> documentList;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        caseData = CaseData.builder().build();
        caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);
        callbackRequest = new CallbackRequest(caseDetails);

        documentList.add(new CollectionMember<>(SOT_DOCUMENT));
        documentList.add(new CollectionMember<>(SOT_DOCUMENT_2));

        letterIdDocs.add(SOT_DOCUMENT);
        letterIdDocs.add(SOT_DOCUMENT_2);

        ResponseCaseData letterResponseCaseData =
            ResponseCaseData.builder().probateNotificationsGenerated(documentList).bulkPrintId(BULK_PRINT_IDS).build();
        CallbackResponse callbackResponse = CallbackResponse.builder().data(letterResponseCaseData).build();

        when(informationRequestCorrespondenceService.generateLetterWithCoversheet(any(), eq(execApplying.getValue())))
            .thenReturn(Arrays.asList(SOT_DOCUMENT));
        when(informationRequestCorrespondenceService.generateLetterWithCoversheet(any(), eq(execApplying2.getValue())))
            .thenReturn(Arrays.asList(SOT_DOCUMENT_2));

        when(informationRequestCorrespondenceService.getLetterId(Arrays.asList(SOT_DOCUMENT), callbackRequest))
            .thenReturn(Arrays.asList("123"));
        when(informationRequestCorrespondenceService.getLetterId(Arrays.asList(SOT_DOCUMENT_2), callbackRequest))
            .thenReturn(Arrays.asList("321"));

        when(callbackResponseTransformer.addInformationRequestDocuments(any(),
            eq(letterIdDocs), eq(Arrays.asList("123", "321")), any())).thenReturn(callbackResponse);
    }

    @Test
    void testEmailRequestReturnsSentEmailDocumentSuccessfully() {
        CollectionMember<Document> documentCollectionMember =
            new CollectionMember<>(Document.builder().documentType(DocumentType.SENT_EMAIL).build());
        documentList = new ArrayList<>();
        documentList.add(documentCollectionMember);

        ResponseCaseData emailResponseCaseData =
            ResponseCaseData.builder().probateNotificationsGenerated(documentList).build();
        CallbackResponse callbackResponse = CallbackResponse.builder().data(emailResponseCaseData).build();

        when(callbackResponseTransformer.addInformationRequestDocuments(any(),
            eq(Arrays.asList(SENT_EMAIL_DOCUMENT)), eq(new ArrayList<>()), any())).thenReturn(callbackResponse);

        caseData = CaseData.builder()
            .applicationType(ApplicationType.PERSONAL)
            .primaryApplicantEmailAddress("primary@probate-test.com").build();
        caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);
        callbackRequest = new CallbackRequest(caseDetails);
        when(informationRequestCorrespondenceService.emailInformationRequest(caseDetails))
            .thenReturn(Arrays.asList(SENT_EMAIL_DOCUMENT));
        when(callbackResponseTransformer.addInformationRequestDocuments(any(),
            eq(Arrays.asList(SENT_EMAIL_DOCUMENT)), eq(new ArrayList<>()), any())).thenReturn(callbackResponse);

        assertEquals(SENT_EMAIL_DOCUMENT,
            informationRequestService.handleInformationRequest(callbackRequest, CASEWORKER_USERINFO)
                .getData().getProbateNotificationsGenerated().get(0).getValue());
    }

    private CollectionMember<ExecutorsApplyingNotification> buildExec(String item, String name, String email,
                                                                      String applying) {
        return new CollectionMember<>(item, ExecutorsApplyingNotification.builder()
            .name(name)
            .address(ADDRESS)
            .email(email)
            .notification(applying)
            .build());

    @Test
    void testEmailRequestReturnsErrorWhenNoEmailProvided() {
        caseData = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .build();
        caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);
        callbackRequest = new CallbackRequest(caseDetails);

        CCDData ccdData = CCDData.builder()
                .applicationType("PERSONAL")
                .build();
        FieldErrorResponse fieldErrorResponse = FieldErrorResponse.builder()
                .message("NoEmailProvidedErrorMessage")
                .build();
        when(emailAddressNotifyApplicantValidationRule.validate(ccdData))
                .thenReturn(Collections.singletonList(fieldErrorResponse));

        assertEquals("NoEmailProvidedErrorMessage",
                informationRequestService.handleInformationRequest(callbackRequest).getErrors().get(0));
    }

    @Test
    void testEmailPreviewDocumentSuccessfully() throws NotificationClientException {
        Document document = Document.builder()
                .documentDateAdded(LocalDate.now())
                .documentFileName("fileName")
                .documentGeneratedBy("generatedBy")
                .documentLink(
                        DocumentLink.builder().documentUrl("url").documentFilename("file").documentBinaryUrl("binary")
                                .build())
                .documentType(DocumentType.DIGITAL_GRANT)
                .build();

        when(notificationService.emailPreview(any())).thenReturn(document);

        assertEquals(document,
                informationRequestService.emailPreview(callbackRequest));
    }

    @Test
    void shouldReturnNullWhenNotificationClientException() throws NotificationClientException {
        when(notificationService.emailPreview(any())).thenThrow(new NotificationClientException("error message"));

        assertNull(informationRequestService.emailPreview(callbackRequest));
    }
}
