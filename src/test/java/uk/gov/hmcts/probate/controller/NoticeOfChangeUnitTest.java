package uk.gov.hmcts.probate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.ChangeOfRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.RemovedRepresentative;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.PrepareNocCaveatService;
import uk.gov.hmcts.probate.service.PrepareNocService;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.NocEmailAddressNotifyValidationRule;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.ID;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.LAST_MODIFIED;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;

class NoticeOfChangeUnitTest {

    @Mock
    private CallbackRequest callbackRequestMock;
    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private BindingResult bindingResultMock;
    @Mock
    private PrepareNocService prepareNocServiceMock;
    @Mock
    private PrepareNocCaveatService prepareNocCaveatServiceMock;
    @Mock
    private EventValidationService eventValidationService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private CaveatCallbackResponseTransformer caveatCallbackResponseTransformer;
    @Mock
    private NocEmailAddressNotifyValidationRule nocEmailAddressNotifyValidationRule;
    @Mock
    private CaveatCallbackRequest caveatCallbackRequest;
    @Mock
    private CaveatCallbackResponse caveatCallbackResponse;
    private Document document;
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private NoticeOfChangeController underTest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new NoticeOfChangeController(prepareNocServiceMock, prepareNocCaveatServiceMock,
                eventValidationService, notificationService,
                caveatCallbackResponseTransformer, nocEmailAddressNotifyValidationRule);
    }

    @Test
    void shouldApplyDecision() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(bindingResultMock.hasErrors()).thenReturn(false);
        underTest.applyDecision("auth", callbackRequestMock);
        verify(prepareNocServiceMock, times(1))
                .applyDecision(Mockito.any(CallbackRequest.class), Mockito.anyString());
    }

    @Test
    void shouldApplyCaveatDecision() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(bindingResultMock.hasErrors()).thenReturn(false);
        underTest.applyDecisionCaveat("auth", callbackRequestMock);
        verify(prepareNocCaveatServiceMock, times(1))
                .applyDecision(Mockito.any(CallbackRequest.class), Mockito.anyString());
    }

    @Test
    void shouldSendNocEmail() throws NotificationClientException {
        CaveatDetails caveatDetails = new CaveatDetails(CaveatData.builder()
                .applicationType(SOLICITOR)
                .registryLocation("Manchester")
                .solsSolicitorAppReference("1234-5678-9012")
                .languagePreferenceWelsh("No")
                .removedRepresentative(RemovedRepresentative.builder()
                        .solicitorEmail("solicitor@gmail.com")
                        .solicitorFirstName("FirstName")
                        .solicitorLastName("LastName").build())
                .build(), LAST_MODIFIED, ID);
        caveatCallbackRequest  = new CaveatCallbackRequest(caveatDetails);
        document = Document.builder()
                .documentDateAdded(LocalDate.now())
                .documentFileName("fileName")
                .documentGeneratedBy("generatedBy")
                .documentLink(
                        DocumentLink.builder().documentUrl("url").documentFilename("file")
                                .documentBinaryUrl("binary").build())
                .documentType(DocumentType.SENT_EMAIL)
                .build();
        caveatCallbackResponse = CaveatCallbackResponse.builder().errors(Collections.EMPTY_LIST).build();
        when(eventValidationService.validateCaveatNocEmail(any(), any())).thenReturn(caveatCallbackResponse);
        when(notificationService.sendCaveatNocEmail(any(), any())).thenReturn(document);

        ResponseEntity<CaveatCallbackResponse> response =
                underTest.sendNOCEmailNotification(caveatCallbackRequest);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldNotSendNocEmailForPaperCaseFirstNoc() throws NotificationClientException {
        List<CollectionMember<ChangeOfRepresentative>> representatives = new ArrayList();
        CollectionMember<ChangeOfRepresentative> changeRepresentative1 =
                new CollectionMember<>(null, ChangeOfRepresentative
                        .builder()
                        .addedDateTime(LocalDateTime.parse("2022-12-01T12:39:54.001Z", dateTimeFormatter))
                        .build());
        representatives.add(changeRepresentative1);
        CaveatDetails caveatDetails = new CaveatDetails(CaveatData.builder()
                .applicationType(SOLICITOR)
                .changeOfRepresentatives(representatives)
                .paperForm("Yes")
                .registryLocation("Manchester")
                .solsSolicitorAppReference("1234-5678-9012")
                .languagePreferenceWelsh("No")
                .removedRepresentative(RemovedRepresentative.builder()
                        .solicitorEmail("solicitor@gmail.com")
                        .solicitorFirstName("FirstName")
                        .solicitorLastName("LastName").build())
                .build(), LAST_MODIFIED, ID);
        caveatCallbackRequest  = new CaveatCallbackRequest(caveatDetails);
        caveatCallbackResponse = CaveatCallbackResponse.builder().errors(Collections.EMPTY_LIST).build();
        when(eventValidationService.validateCaveatNocEmail(any(), any())).thenReturn(caveatCallbackResponse);
        ResponseEntity<CaveatCallbackResponse> response =
                underTest.sendNOCEmailNotification(caveatCallbackRequest);
        verify(notificationService, times(0)).sendCaveatNocEmail(any(), any());
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void shouldSendNocEmailForPaperCaseSecondNoc() throws NotificationClientException {
        List<CollectionMember<ChangeOfRepresentative>> representatives = new ArrayList();
        CollectionMember<ChangeOfRepresentative> changeRepresentative1 =
                new CollectionMember<>(null, ChangeOfRepresentative
                        .builder()
                        .addedDateTime(LocalDateTime.parse("2022-12-01T12:39:54.001Z", dateTimeFormatter))
                        .build());
        CollectionMember<ChangeOfRepresentative> changeRepresentative2 =
                new CollectionMember<>(null, ChangeOfRepresentative
                        .builder()
                        .addedDateTime(LocalDateTime.parse("2023-01-01T18:00:00.001Z", dateTimeFormatter))
                        .build());
        representatives.add(changeRepresentative1);
        representatives.add(changeRepresentative2);
        CaveatDetails caveatDetails = new CaveatDetails(CaveatData.builder()
                .applicationType(SOLICITOR)
                .changeOfRepresentatives(representatives)
                .paperForm("Yes")
                .registryLocation("Manchester")
                .solsSolicitorAppReference("1234-5678-9012")
                .languagePreferenceWelsh("No")
                .removedRepresentative(RemovedRepresentative.builder()
                        .solicitorEmail("solicitor@gmail.com")
                        .solicitorFirstName("FirstName")
                        .solicitorLastName("LastName").build())
                .build(), LAST_MODIFIED, ID);
        caveatCallbackRequest  = new CaveatCallbackRequest(caveatDetails);
        caveatCallbackResponse = CaveatCallbackResponse.builder().errors(Collections.EMPTY_LIST).build();
        when(eventValidationService.validateCaveatNocEmail(any(), any())).thenReturn(caveatCallbackResponse);
        ResponseEntity<CaveatCallbackResponse> response =
                underTest.sendNOCEmailNotification(caveatCallbackRequest);
        verify(notificationService, times(1)).sendCaveatNocEmail(any(), any());
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }
}
