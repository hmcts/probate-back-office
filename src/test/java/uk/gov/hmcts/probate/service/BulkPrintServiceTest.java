package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.hmcts.probate.exception.BulkPrintException;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.ProbateAddress;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.documentmanagement.DocumentManagementService;
import uk.gov.hmcts.probate.transformer.DocumentTransformer;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;
import uk.gov.hmcts.reform.sendletter.api.SendLetterApi;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;
import uk.gov.hmcts.reform.sendletter.api.model.v3.LetterV3;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BulkPrintServiceTest {

    @InjectMocks
    private BulkPrintService bulkPrintService;

    @Mock
    private SendLetterApi sendLetterApiMock;

    @Mock
    private EventValidationService eventValidationService;

    @Mock
    private ServiceAuthTokenGenerator authTokenGeneratorMock;

    @Mock
    private DocumentManagementService documentManagementServiceMock;

    @Mock
    private BusinessValidationMessageService businessValidationMessageService;

    private ResponseCaseData responseCaseData;
    @Mock
    private DocumentTransformer documentTransformer;

    private static final String ADDITIONAL_DATA_CASE_REFERENCE = "caseReference";
    private static final String RECIPIENTS = "recipients";

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(authTokenGeneratorMock.generate()).thenReturn("authToken");
        when(documentManagementServiceMock.getDocument(any(Document.class))).thenReturn(new byte[256]);
    }

    @Test
    void testSuccessfulSendToBulkPrintWithNoExtraCopies() {

        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
                .addressLine2("Address 2")
                .postCode("EC2")
                .country("UK")
                .build();
        CaseData caseData = CaseData.builder()
                .primaryApplicantForenames("first")
                .primaryApplicantSurname("last")
                .primaryApplicantAddress(address)
                .build();
        CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));
        DocumentLink documentLink = DocumentLink.builder()
                .documentUrl("http://localhost")
                .build();
        Document grant = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentType(DocumentType.DIGITAL_GRANT)
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        Document coverSheet = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        UUID uuid = UUID.randomUUID();
        SendLetterResponse sendLetterResponse = new SendLetterResponse(uuid);
        when(sendLetterApiMock.sendLetter(anyString(), letterV3ArgumentCaptor.capture()))
                .thenReturn(sendLetterResponse);
        SendLetterResponse response = bulkPrintService.sendToBulkPrintForGrant(callbackRequest, grant, coverSheet);

        verify(sendLetterApiMock).sendLetter(anyString(), any(LetterV3.class));

        assertNotNull(response);
        assertThat(response.letterId, is(uuid));
        assertThat(letterV3ArgumentCaptor.getValue().additionalData).contains(
                entry(ADDITIONAL_DATA_CASE_REFERENCE, 0L),
                entry(RECIPIENTS, new String[] {"0"})
        );
    }


    @Test
    void testSuccessfulSendToBulkPrintWithSixExtraCopies() {

        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
                .addressLine2("Address 2")
                .postCode("EC2")
                .country("UK")
                .build();
        CaseData caseData = CaseData.builder()
                .primaryApplicantForenames("first")
                .primaryApplicantSurname("last")
                .primaryApplicantAddress(address)
                .extraCopiesOfGrant(6L)
                .build();
        CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));
        DocumentLink documentLink = DocumentLink.builder()
                .documentUrl("http://localhost")
                .build();
        Document document = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentType(DocumentType.DIGITAL_GRANT)
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        Document coverSheet = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        UUID uuid = UUID.randomUUID();
        SendLetterResponse sendLetterResponse = new SendLetterResponse(uuid);
        when(sendLetterApiMock.sendLetter(anyString(), any(LetterV3.class))).thenReturn(sendLetterResponse);
        when(documentTransformer.hasDocumentWithType(Collections.singletonList(document), DocumentType.DIGITAL_GRANT))
            .thenReturn(true);

        SendLetterResponse response = bulkPrintService.sendToBulkPrintForGrant(callbackRequest, document, coverSheet);

        verify(sendLetterApiMock).sendLetter(anyString(), any(LetterV3.class));

        assertNotNull(response);
        assertThat(response.letterId, is(uuid));
    }

    @Test
    void testHttpClientException() {
        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
                .addressLine2("Address 2")
                .postCode("EC2")
                .country("UK")
                .build();
        CaseData caseData = CaseData.builder()
                .primaryApplicantForenames("first")
                .primaryApplicantSurname("last")
                .primaryApplicantAddress(address)
                .extraCopiesOfGrant(6L)
                .build();
        CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));

        DocumentLink documentLink = DocumentLink.builder()
                .documentUrl("http://localhost")
                .build();
        Document document = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentType(DocumentType.DIGITAL_GRANT)
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        Document coverSheet = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();

        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "StatusText", "Body".getBytes(),
             Charset.defaultCharset()))
            .when(sendLetterApiMock).sendLetter(anyString(), any(LetterV3.class));
        SendLetterResponse response = bulkPrintService.sendToBulkPrintForGrant(callbackRequest, document, coverSheet);

        assertNull(response);
    }

    @Test
    void shouldThrowException() {
        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
            .addressLine2("Address 2")
            .postCode("EC2")
            .country("UK")
            .build();
        CaseData caseData = CaseData.builder()
            .primaryApplicantForenames("first")
            .primaryApplicantSurname("last")
            .primaryApplicantAddress(address)
            .extraCopiesOfGrant(6L)
            .build();
        CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));

        DocumentLink documentLink = DocumentLink.builder()
            .documentUrl("http://localhost")
            .build();
        Document document = Document.builder()
            .documentFileName("test.pdf")
            .documentGeneratedBy("test")
            .documentType(DocumentType.DIGITAL_GRANT)
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .build();
        Document coverSheet = Document.builder()
            .documentFileName("test.pdf")
            .documentGeneratedBy("test")
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .build();

        doThrow(new RuntimeException("Some exception"))
            .when(sendLetterApiMock).sendLetter(anyString(), any(LetterV3.class));
        SendLetterResponse response = bulkPrintService.sendToBulkPrintForGrant(callbackRequest, document, coverSheet);

        assertNull(response);
    }

    @Test
    void shouldThrowIOException() throws IOException {
        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
                .addressLine2("Address 2")
                .postCode("EC2")
                .country("UK")
                .build();
        CaseData caseData = CaseData.builder()
                .primaryApplicantForenames("first")
                .primaryApplicantSurname("last")
                .primaryApplicantAddress(address)
                .extraCopiesOfGrant(6L)
                .build();
        CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));

        DocumentLink documentLink = DocumentLink.builder()
                .documentUrl("http://localhost")
                .build();
        Document document = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentType(DocumentType.ASSEMBLED_LETTER)
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        Document coverSheet = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();

        doThrow(new IOException("Error retrieving document from store with url"))
                .when(documentManagementServiceMock).getDocument(any(Document.class));

        bulkPrintService.sendToBulkPrintForGrant(callbackRequest, document, coverSheet);

        verify(documentManagementServiceMock).getDocument(any(Document.class));
    }

    @Test
    void shouldThrowCaveatsIOException() throws IOException {

        ProbateAddress address = ProbateAddress.builder().proAddressLine1("Address 1")
            .proAddressLine2("Address 2")
            .proPostCode("EC2")
            .proCountry("UK")
            .build();
        CaveatData caseData = CaveatData.builder()
            .caveatorEmailAddress("caveator@probate-test.com")
            .caveatorForenames("firstname")
            .caveatorSurname("surname")
            .caveatorAddress(address)
            .build();
        CaveatCallbackRequest callbackRequest = new CaveatCallbackRequest(new CaveatDetails(caseData, null, 0L));

        DocumentLink documentLink = DocumentLink.builder()
            .documentUrl("http://localhost")
            .build();
        Document document = Document.builder()
            .documentFileName("test.pdf")
            .documentGeneratedBy("test")
            .documentType(DocumentType.DIGITAL_GRANT_REISSUE)
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .build();
        Document coverSheet = Document.builder()
            .documentFileName("test.pdf")
            .documentGeneratedBy("test")
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .build();

        doThrow(new IOException("Error retrieving document from store with url"))
            .when(documentManagementServiceMock).getDocument(any(Document.class));

        bulkPrintService.sendToBulkPrintForCaveat(callbackRequest, document, coverSheet);

        verify(documentManagementServiceMock).getDocument(any(Document.class));
    }

    @Test
    void shouldThrowCaveatsException() throws IOException {

        ProbateAddress address = ProbateAddress.builder().proAddressLine1("Address 1")
            .proAddressLine2("Address 2")
            .proPostCode("EC2")
            .proCountry("UK")
            .build();
        CaveatData caseData = CaveatData.builder()
            .caveatorEmailAddress("caveator@probate-test.com")
            .caveatorForenames("firstname")
            .caveatorSurname("surname")
            .caveatorAddress(address)
            .build();
        CaveatCallbackRequest callbackRequest = new CaveatCallbackRequest(new CaveatDetails(caseData, null, 0L));

        DocumentLink documentLink = DocumentLink.builder()
            .documentUrl("http://localhost")
            .build();
        Document document = Document.builder()
            .documentFileName("test.pdf")
            .documentGeneratedBy("test")
            .documentType(DocumentType.DIGITAL_GRANT_REISSUE)
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .build();
        Document coverSheet = Document.builder()
            .documentFileName("test.pdf")
            .documentGeneratedBy("test")
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .build();

        doThrow(new RuntimeException("SomeException"))
            .when(sendLetterApiMock).sendLetter(anyString(), any(LetterV3.class));

        SendLetterResponse response = bulkPrintService.sendToBulkPrintForCaveat(callbackRequest, document, coverSheet);

        assertNull(response);
    }

    @Test
    void testHttpClientExceptionCaveats() {
        ProbateAddress address = ProbateAddress.builder().proAddressLine1("Address 1")
                .proAddressLine2("Address 2")
                .proPostCode("EC2")
                .proCountry("UK")
                .build();
        CaveatData caseData = CaveatData.builder()
                .caveatorEmailAddress("caveator@probate-test.com")
                .caveatorForenames("firstname")
                .caveatorSurname("surname")
                .caveatorAddress(address)
                .build();
        CaveatCallbackRequest callbackRequest = new CaveatCallbackRequest(new CaveatDetails(caseData, null, 0L));

        DocumentLink documentLink = DocumentLink.builder()
                .documentUrl("http://localhost")
                .build();
        Document document = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentType(DocumentType.CAVEAT_RAISED)
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        Document coverSheet = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();

        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "StatusText", "Body".getBytes(),
            Charset.defaultCharset()))
            .when(sendLetterApiMock).sendLetter(anyString(), any(LetterV3.class));
        SendLetterResponse response = bulkPrintService.sendToBulkPrintForCaveat(callbackRequest, document, coverSheet);

        assertNull(response);
    }

    @Test
    void testSuccessfulSendToBulkPrintForCaveats() {

        ProbateAddress address = ProbateAddress.builder().proAddressLine1("Address 1")
                .proAddressLine2("Address 2")
                .proPostCode("EC2")
                .proCountry("UK")
                .build();
        CaveatData caseData = CaveatData.builder()
                .caveatorEmailAddress("caveator@probate-test.com")
                .caveatorForenames("firstname")
                .caveatorSurname("surname")
                .caveatorAddress(address)
                .build();
        CaveatCallbackRequest callbackRequest = new CaveatCallbackRequest(new CaveatDetails(caseData, null, 0L));

        DocumentLink documentLink = DocumentLink.builder()
                .documentUrl("http://localhost")
                .build();
        Document document = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentType(DocumentType.CAVEAT_RAISED)
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        Document coverSheet = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        UUID uuid = UUID.randomUUID();
        SendLetterResponse sendLetterResponse = new SendLetterResponse(uuid);
        when(sendLetterApiMock.sendLetter(anyString(), letterV3ArgumentCaptor.capture()))
                .thenReturn(sendLetterResponse);
        SendLetterResponse response = bulkPrintService.sendToBulkPrintForCaveat(callbackRequest, document, coverSheet);

        verify(sendLetterApiMock).sendLetter(anyString(), any(LetterV3.class));

        assertNotNull(response);
        assertThat(response.letterId, is(uuid));
        assertThat(letterV3ArgumentCaptor.getValue().additionalData).contains(
                entry(ADDITIONAL_DATA_CASE_REFERENCE, 0L),
                entry(RECIPIENTS, new String[] {"0"})
        );
    }

    @Test
    void testSuccessfulSendToBulkPrintGrant() {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.DIGITAL_GRANT);
    }

    @Test
    void testSuccessfulSendToBulkPrintIntestacyGrant() {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.INTESTACY_GRANT);
    }

    @Test
    void testSuccessfulSendToBulkPrintAdmonWillGrant() {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.ADMON_WILL_GRANT);
    }

    @Test
    void testSuccessfulSendToBulkPrintAdColligendaBonaGrant() {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.AD_COLLIGENDA_BONA_GRANT);
    }

    @Test
    void testSuccessfulSendToBulkPrintGrantReissue() {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.DIGITAL_GRANT_REISSUE);
    }

    @Test
    void testSuccessfulSendToBulkPrintIntestacyGrantReissue() {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.INTESTACY_GRANT_REISSUE);
    }

    @Test
    void testSuccessfulSendToBulkPrintAdmonWillGrantReissue() {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.ADMON_WILL_GRANT_REISSUE);
    }

    @Test
    void testSuccessfulSendToBulkPrintAdColligendaBonaGrantReissue() {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.AD_COLLIGENDA_BONA_GRANT_REISSUE);
    }

    @Test
    void testSuccessfulSendToBulkPrintGrantReissueWelsh() {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.WELSH_DIGITAL_GRANT_REISSUE);
    }

    @Test
    void testSuccessfulSendToBulkPrintIntestacyGrantReissueWelsh() {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.WELSH_INTESTACY_GRANT_REISSUE);
    }

    @Test
    void testSuccessfulSendToBulkPrintAdmonWillGrantReissueWelsh() {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.WELSH_ADMON_WILL_GRANT_REISSUE);
    }

    @Test
    void testSuccessfulSendToBulkPrintAdColligendaBonaGrantReissueWelsh() {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.WELSH_AD_COLLIGENDA_BONA_GRANT_REISSUE);
    }

    @Test
    void testUnSuccessfulValidateEmailThrowsError() throws BulkPrintException {

        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
                .addressLine2("Address 2")
                .postCode("EC2")
                .country("UK")
                .build();
        CaseData caseData = CaseData.builder()
                .primaryApplicantEmailAddress("primary@probate-test.com")
                .primaryApplicantForenames("firstname")
                .primaryApplicantSurname("surname")
                .primaryApplicantAddress(address)
                .build();
        final CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));

        DocumentLink documentLink = DocumentLink.builder()
                .documentUrl("http://localhost")
                .build();
        final Document document = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        final Document coverSheet = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();

        List<String> errors = new ArrayList<>();
        errors.add("test error");

        CallbackResponse callbackResponse = CallbackResponse.builder()
                .data(responseCaseData)
                .errors(errors)
                .build();

        when(sendLetterApiMock.sendLetter(anyString(), any(LetterV3.class))).thenReturn(null);
        when(eventValidationService.validateBulkPrintResponse(any(), any())).thenReturn(callbackResponse);
        when(businessValidationMessageService.generateError(any(), any()))
            .thenReturn(FieldErrorResponse.builder().build());

        assertThatThrownBy(
            () -> bulkPrintService.optionallySendToBulkPrint(callbackRequest, coverSheet, document, true))
            .isInstanceOf(BulkPrintException.class).hasMessage("Bulk print send letter response is null for: 0");
    }

    @Test
    void testNoSendToBulkPrintReturnsNull() {
        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
                .addressLine2("Address 2")
                .postCode("EC2")
                .country("UK")
                .build();
        CaseData caseData = CaseData.builder()
                .primaryApplicantEmailAddress("primary@probate-test.com")
                .primaryApplicantForenames("firstname")
                .primaryApplicantSurname("surname")
                .primaryApplicantAddress(address)
                .build();
        final CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));

        assertNull(bulkPrintService.optionallySendToBulkPrint(callbackRequest, Document.builder().build(),
                Document.builder().build(), false));
    }

    @Test
    void sendToBulkPrintWith50ExtraCopiesWDG() {

        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
                .addressLine2("Address 2")
                .postCode("EC2")
                .country("UK")
                .build();
        CaseData caseData = CaseData.builder()
                .primaryApplicantForenames("first")
                .primaryApplicantSurname("last")
                .primaryApplicantAddress(address)
                .extraCopiesOfGrant(50L)
                .build();
        CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));
        DocumentLink documentLink = DocumentLink.builder()
                .documentUrl("http://localhost")
                .build();
        Document document = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentType(DocumentType.WELSH_DIGITAL_GRANT)
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        Document coverSheet = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        UUID uuid = UUID.randomUUID();
        SendLetterResponse sendLetterResponse = new SendLetterResponse(uuid);
        when(sendLetterApiMock.sendLetter(anyString(), any(LetterV3.class))).thenReturn(sendLetterResponse);
        when(documentTransformer
            .hasDocumentWithType(Collections.singletonList(document), DocumentType.WELSH_DIGITAL_GRANT))
            .thenReturn(true);

        SendLetterResponse response = bulkPrintService.sendToBulkPrintForGrant(callbackRequest, document, coverSheet);

        verify(sendLetterApiMock).sendLetter(anyString(), any(LetterV3.class));

        assertNotNull(response);
        assertThat(response.letterId, is(uuid));
    }

    @Test
    void sendToBulkPrintWith50ExtraCopiesWIG() {

        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
                .addressLine2("Address 2")
                .postCode("EC2")
                .country("UK")
                .build();
        CaseData caseData = CaseData.builder()
                .primaryApplicantForenames("first")
                .primaryApplicantSurname("last")
                .primaryApplicantAddress(address)
                .extraCopiesOfGrant(50L)
                .build();
        CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));
        DocumentLink documentLink = DocumentLink.builder()
                .documentUrl("http://localhost")
                .build();
        Document document = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentType(DocumentType.WELSH_INTESTACY_GRANT)
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        Document coverSheet = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        UUID uuid = UUID.randomUUID();
        SendLetterResponse sendLetterResponse = new SendLetterResponse(uuid);
        when(sendLetterApiMock.sendLetter(anyString(), any(LetterV3.class))).thenReturn(sendLetterResponse);
        when(documentTransformer
            .hasDocumentWithType(Collections.singletonList(document), DocumentType.WELSH_INTESTACY_GRANT))
            .thenReturn(true);

        SendLetterResponse response = bulkPrintService.sendToBulkPrintForGrant(callbackRequest, document, coverSheet);

        verify(sendLetterApiMock).sendLetter(anyString(), any(LetterV3.class));

        assertNotNull(response);
        assertThat(response.letterId, is(uuid));
    }

    @Test
    void sendToBulkPrintWith50ExtraCopiesAWDG() {

        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
                .addressLine2("Address 2")
                .postCode("EC2")
                .country("UK")
                .build();
        CaseData caseData = CaseData.builder()
                .primaryApplicantForenames("first")
                .primaryApplicantSurname("last")
                .primaryApplicantAddress(address)
                .extraCopiesOfGrant(50L)
                .build();
        CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));
        DocumentLink documentLink = DocumentLink.builder()
                .documentUrl("http://localhost")
                .build();
        Document document = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentType(DocumentType.WELSH_ADMON_WILL_GRANT)
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        Document coverSheet = Document.builder()
                .documentFileName("test.pdf")
                .documentGeneratedBy("test")
                .documentDateAdded(LocalDate.now())
                .documentLink(documentLink)
                .build();
        UUID uuid = UUID.randomUUID();
        SendLetterResponse sendLetterResponse = new SendLetterResponse(uuid);
        when(sendLetterApiMock.sendLetter(anyString(), any(LetterV3.class))).thenReturn(sendLetterResponse);
        when(documentTransformer
            .hasDocumentWithType(Collections.singletonList(document), DocumentType.WELSH_ADMON_WILL_GRANT))
            .thenReturn(true);

        SendLetterResponse response = bulkPrintService.sendToBulkPrintForGrant(callbackRequest, document, coverSheet);

        verify(sendLetterApiMock).sendLetter(anyString(), any(LetterV3.class));

        assertNotNull(response);
        assertThat(response.letterId, is(uuid));
    }

    @Captor
    private ArgumentCaptor<LetterV3> letterV3ArgumentCaptor;

    @Test
    void shouldSendToBulkPrintForReprint() {

        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
            .addressLine2("Address 2")
            .postCode("EC2")
            .country("UK")
            .build();
        CaseData caseData = CaseData.builder()
            .primaryApplicantForenames("first")
            .primaryApplicantSurname("last")
            .primaryApplicantAddress(address)
            .reprintNumberOfCopies("10")
            .build();
        CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));
        DocumentLink documentLink = DocumentLink.builder()
            .documentUrl("http://localhost")
            .build();
        Document grant = Document.builder()
            .documentFileName("test.pdf")
            .documentGeneratedBy("test")
            .documentType(DocumentType.DIGITAL_GRANT)
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .build();
        Document coverSheet = Document.builder()
            .documentFileName("test.pdf")
            .documentGeneratedBy("test")
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .build();
        UUID uuid = UUID.randomUUID();
        SendLetterResponse sendLetterResponse = new SendLetterResponse(uuid);

        List<String> errors = new ArrayList<>();
        CallbackResponse callbackResponse = CallbackResponse.builder()
            .data(responseCaseData)
            .errors(errors)
            .build();

        when(sendLetterApiMock.sendLetter(anyString(), letterV3ArgumentCaptor.capture()))
            .thenReturn(sendLetterResponse);
        when(eventValidationService.validateBulkPrintResponse(eq(uuid.toString()), any())).thenReturn(callbackResponse);

        SendLetterResponse response = bulkPrintService.sendDocumentsForReprint(callbackRequest, grant, coverSheet);

        assertEquals(sendLetterResponse, response);
        assertEquals(1, letterV3ArgumentCaptor.getValue().documents.get(0).copies);
        assertEquals(10, letterV3ArgumentCaptor.getValue().documents.get(1).copies);
        assertThat(letterV3ArgumentCaptor.getValue().additionalData).contains(
                entry(ADDITIONAL_DATA_CASE_REFERENCE, 0L),
                entry(RECIPIENTS, new String[] {"0"})
        );
        verify(sendLetterApiMock).sendLetter(anyString(), any(LetterV3.class));
    }

    @Test
    void shouldSendToBulkPrintForReprintWillNullLetterId() {

        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
            .addressLine2("Address 2")
            .postCode("EC2")
            .country("UK")
            .build();
        CaseData caseData = CaseData.builder()
            .primaryApplicantForenames("first")
            .primaryApplicantSurname("last")
            .primaryApplicantAddress(address)
            .reprintNumberOfCopies("10")
            .build();
        CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));
        DocumentLink documentLink = DocumentLink.builder()
            .documentUrl("http://localhost")
            .build();
        Document grant = Document.builder()
            .documentFileName("test.pdf")
            .documentGeneratedBy("test")
            .documentType(DocumentType.DIGITAL_GRANT)
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .build();
        Document coverSheet = Document.builder()
            .documentFileName("test.pdf")
            .documentGeneratedBy("test")
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .build();
        UUID uuid = UUID.randomUUID();
        SendLetterResponse sendLetterResponse = new SendLetterResponse(uuid);

        List<String> errors = new ArrayList<>();
        CallbackResponse callbackResponse = CallbackResponse.builder()
            .data(responseCaseData)
            .errors(errors)
            .build();

        when(eventValidationService.validateBulkPrintResponse(any(), any())).thenReturn(callbackResponse);
        when(sendLetterApiMock.sendLetter(anyString(), any(LetterV3.class))).thenReturn(sendLetterResponse);

        SendLetterResponse response = bulkPrintService.sendDocumentsForReprint(callbackRequest, grant, coverSheet);

        assertEquals(sendLetterResponse, response);
        assertEquals(sendLetterResponse.letterId, response.letterId);
        verify(sendLetterApiMock).sendLetter(anyString(), any(LetterV3.class));
    }

    @Test
    void shouldErrorOnSendToBulkPrintForReprint() {

        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
            .addressLine2("Address 2")
            .postCode("EC2")
            .country("UK")
            .build();
        CaseData caseData = CaseData.builder()
            .primaryApplicantForenames("first")
            .primaryApplicantSurname("last")
            .primaryApplicantAddress(address)
            .reprintNumberOfCopies("10")
            .build();
        final CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));
        DocumentLink documentLink = DocumentLink.builder()
            .documentUrl("http://localhost")
            .build();
        final Document grant = Document.builder()
            .documentFileName("test.pdf")
            .documentGeneratedBy("test")
            .documentType(DocumentType.DIGITAL_GRANT)
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .build();
        final Document coverSheet = Document.builder()
            .documentFileName("test.pdf")
            .documentGeneratedBy("test")
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .build();
        UUID uuid = UUID.randomUUID();
        SendLetterResponse sendLetterResponse = new SendLetterResponse(uuid);

        List<String> errors = new ArrayList<>();
        errors.add("test error");
        CallbackResponse callbackResponse = CallbackResponse.builder()
            .data(responseCaseData)
            .errors(errors)
            .build();

        when(sendLetterApiMock.sendLetter(anyString(), any(LetterV3.class))).thenReturn(sendLetterResponse);
        when(eventValidationService.validateBulkPrintResponse(eq(uuid.toString()), any())).thenReturn(callbackResponse);

        when(businessValidationMessageService.generateError(any(), any()))
            .thenReturn(FieldErrorResponse.builder().build());

        assertThatThrownBy(() -> bulkPrintService.sendDocumentsForReprint(callbackRequest, grant, coverSheet))
            .isInstanceOf(BulkPrintException.class)
            .hasMessage("Bulk print send letter for reprint response is null for: 0");
    }

    private void testSuccessfulSendToBulkPrintForDocumentType(DocumentType documentType) {
        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
            .addressLine2("Address 2")
            .postCode("EC2")
            .country("UK")
            .build();
        CaseData caseData = CaseData.builder()
            .primaryApplicantEmailAddress("primary@probate-test.com")
            .primaryApplicantForenames("firstname")
            .primaryApplicantSurname("surname")
            .primaryApplicantAddress(address)
            .extraCopiesOfGrant(1L)
            .build();
        final CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));

        DocumentLink documentLink = DocumentLink.builder()
            .documentUrl("http://localhost")
            .build();
        final Document document = Document.builder()
            .documentFileName("test.pdf")
            .documentGeneratedBy("test")
            .documentType(documentType)
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .build();
        final Document coverSheet = Document.builder()
            .documentFileName("test.pdf")
            .documentGeneratedBy("test")
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .build();

        responseCaseData = ResponseCaseData.builder()
            .registryLocation("leeds")
            .deceasedForenames("name")
            .deceasedSurname("name")
            .build();

        final CallbackResponse callbackResponse = CallbackResponse.builder()
            .errors(new ArrayList<>())
            .data(responseCaseData)
            .build();

        UUID uuid = UUID.randomUUID();
        SendLetterResponse sendLetterResponse = new SendLetterResponse(uuid);
        when(sendLetterApiMock.sendLetter(anyString(), any(LetterV3.class))).thenReturn(sendLetterResponse);
        when(eventValidationService.validateBulkPrintResponse(eq(uuid.toString()), any())).thenReturn(callbackResponse);
        when(documentTransformer.hasDocumentWithType(Collections.singletonList(document), documentType))
            .thenReturn(true);

        final String letterId = bulkPrintService.optionallySendToBulkPrint(callbackRequest, coverSheet, document, true);

        verify(sendLetterApiMock).sendLetter(anyString(), letterV3ArgumentCaptor.capture());

        assertEquals(1, letterV3ArgumentCaptor.getValue().documents.get(0).copies);
        assertEquals(2, letterV3ArgumentCaptor.getValue().documents.get(1).copies);
        assertThat(letterV3ArgumentCaptor.getValue().additionalData).contains(
                entry(ADDITIONAL_DATA_CASE_REFERENCE, 0L),
                entry(RECIPIENTS, new String[] {"0"})
        );

        assertNotNull(letterId);
        assertThat(letterId, is(uuid.toString()));
    }
}
