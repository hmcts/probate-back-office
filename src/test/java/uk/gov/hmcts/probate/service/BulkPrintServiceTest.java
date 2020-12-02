package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
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
import uk.gov.hmcts.probate.service.client.DocumentStoreClient;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BulkPrintServiceTest {

    @InjectMocks
    private BulkPrintService bulkPrintService;

    @Mock
    private SendLetterApi sendLetterApiMock;

    @Mock
    private EventValidationService eventValidationService;

    @Mock
    private ServiceAuthTokenGenerator authTokenGeneratorMock;

    @Mock
    private DocumentStoreClient documentStoreClientMock;

    @Mock
    private BusinessValidationMessageService businessValidationMessageService;

    private ResponseCaseData responseCaseData;
    @Mock
    private DocumentTransformer documentTransformer;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(authTokenGeneratorMock.generate()).thenReturn("authToken");
        when(documentStoreClientMock.retrieveDocument(any(Document.class), anyString())).thenReturn(new byte[256]);
    }

    @Test
    public void testSuccessfulSendToBulkPrintWithNoExtraCopies() {

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
        when(sendLetterApiMock.sendLetter(anyString(), any(LetterV3.class))).thenReturn(sendLetterResponse);
        SendLetterResponse response = bulkPrintService.sendToBulkPrintForGrant(callbackRequest, grant, coverSheet);

        verify(sendLetterApiMock).sendLetter(anyString(), any(LetterV3.class));

        assertNotNull(response);
        assertThat(response.letterId, is(uuid));
    }


    @Test
    public void testSuccessfulSendToBulkPrintWithSixExtraCopies() {

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
        when(documentTransformer.hasDocumentWithType(Collections.singletonList(document), DocumentType.DIGITAL_GRANT)).thenReturn(true);

        SendLetterResponse response = bulkPrintService.sendToBulkPrintForGrant(callbackRequest, document, coverSheet);

        verify(sendLetterApiMock).sendLetter(anyString(), any(LetterV3.class));

        assertNotNull(response);
        assertThat(response.letterId, is(uuid));
    }

    @Test
    public void testHttpClientException() {
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

        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "StatusText", "Body".getBytes(), Charset.defaultCharset()))
            .when(sendLetterApiMock).sendLetter(anyString(), any(LetterV3.class));
        SendLetterResponse response = bulkPrintService.sendToBulkPrintForGrant(callbackRequest, document, coverSheet);

        assertNull(response);
    }

    @Test
    public void shouldThrowException() {
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
    public void shouldThrowIOException() throws IOException {
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
                .when(documentStoreClientMock).retrieveDocument(any(Document.class), anyString());

        bulkPrintService.sendToBulkPrintForGrant(callbackRequest, document, coverSheet);

        verify(documentStoreClientMock).retrieveDocument(any(Document.class), anyString());
    }

    @Test
    public void shouldThrowCaveatsIOException() throws IOException {

        ProbateAddress address = ProbateAddress.builder().proAddressLine1("Address 1")
            .proAddressLine2("Address 2")
            .proPostCode("EC2")
            .proCountry("UK")
            .build();
        CaveatData caseData = CaveatData.builder()
            .caveatorEmailAddress("email@email.com")
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
            .when(documentStoreClientMock).retrieveDocument(any(Document.class), anyString());

        bulkPrintService.sendToBulkPrintForCaveat(callbackRequest, document, coverSheet);

        verify(documentStoreClientMock).retrieveDocument(any(Document.class), anyString());
    }

    @Test
    public void shouldThrowCaveatsException() throws IOException {

        ProbateAddress address = ProbateAddress.builder().proAddressLine1("Address 1")
            .proAddressLine2("Address 2")
            .proPostCode("EC2")
            .proCountry("UK")
            .build();
        CaveatData caseData = CaveatData.builder()
            .caveatorEmailAddress("email@email.com")
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
    public void testHttpClientExceptionCaveats() {
        ProbateAddress address = ProbateAddress.builder().proAddressLine1("Address 1")
                .proAddressLine2("Address 2")
                .proPostCode("EC2")
                .proCountry("UK")
                .build();
        CaveatData caseData = CaveatData.builder()
                .caveatorEmailAddress("email@email.com")
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

        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "StatusText", "Body".getBytes(), Charset.defaultCharset()))
            .when(sendLetterApiMock).sendLetter(anyString(), any(LetterV3.class));
        SendLetterResponse response = bulkPrintService.sendToBulkPrintForCaveat(callbackRequest, document, coverSheet);

        assertNull(response);
    }

    @Test
    public void testSuccessfulSendToBulkPrintForCaveats() {

        ProbateAddress address = ProbateAddress.builder().proAddressLine1("Address 1")
                .proAddressLine2("Address 2")
                .proPostCode("EC2")
                .proCountry("UK")
                .build();
        CaveatData caseData = CaveatData.builder()
                .caveatorEmailAddress("email@email.com")
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
        when(sendLetterApiMock.sendLetter(anyString(), any(LetterV3.class))).thenReturn(sendLetterResponse);
        SendLetterResponse response = bulkPrintService.sendToBulkPrintForCaveat(callbackRequest, document, coverSheet);

        verify(sendLetterApiMock).sendLetter(anyString(), any(LetterV3.class));

        assertNotNull(response);
        assertThat(response.letterId, is(uuid));
    }

    @Test
    public void testSuccessfulSendToBulkPrintGrant() {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.DIGITAL_GRANT);
    }

    @Test
    public void testSuccessfulSendToBulkPrintIntestacyGrant() {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.INTESTACY_GRANT);
    }

    @Test
    public void testSuccessfulSendToBulkPrintAdmonWillGrant() {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.ADMON_WILL_GRANT);
    }
    
    @Test
    public void testSuccessfulSendToBulkPrintGrantReissue() {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.DIGITAL_GRANT_REISSUE);
    }

    @Test
    public void testSuccessfulSendToBulkPrintIntestacyGrantReissue() {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.INTESTACY_GRANT_REISSUE);
    }

    @Test
    public void testSuccessfulSendToBulkPrintAdmonWillGrantReissue() {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.ADMON_WILL_GRANT_REISSUE);
    }

    @Test
    public void testSuccessfulSendToBulkPrintGrantReissueWelsh() {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.WELSH_DIGITAL_GRANT_REISSUE);
    }

    @Test
    public void testSuccessfulSendToBulkPrintIntestacyGrantReissueWelsh() {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.WELSH_INTESTACY_GRANT_REISSUE);
    }

    @Test
    public void testSuccessfulSendToBulkPrintAdmonWillGrantReissueWelsh() {
        testSuccessfulSendToBulkPrintForDocumentType(DocumentType.WELSH_ADMON_WILL_GRANT_REISSUE);
    }

    @Test
    public void testUnSuccessfulValidateEmailThrowsError() throws BulkPrintException {

        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
                .addressLine2("Address 2")
                .postCode("EC2")
                .country("UK")
                .build();
        CaseData caseData = CaseData.builder()
                .primaryApplicantEmailAddress("email@email.com")
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
        when(businessValidationMessageService.generateError(any(), any())).thenReturn(FieldErrorResponse.builder().build());

        assertThatThrownBy(() -> bulkPrintService.optionallySendToBulkPrint(callbackRequest, coverSheet, document, true))
                .isInstanceOf(BulkPrintException.class).hasMessage("Bulk print send letter response is null for: 0");
    }

    @Test
    public void testNoSendToBulkPrintReturnsNull() {
        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
                .addressLine2("Address 2")
                .postCode("EC2")
                .country("UK")
                .build();
        CaseData caseData = CaseData.builder()
                .primaryApplicantEmailAddress("email@email.com")
                .primaryApplicantForenames("firstname")
                .primaryApplicantSurname("surname")
                .primaryApplicantAddress(address)
                .build();
        final CallbackRequest callbackRequest = new CallbackRequest(new CaseDetails(caseData, null, 0L));

        assertNull(bulkPrintService.optionallySendToBulkPrint(callbackRequest, Document.builder().build(),
                Document.builder().build(), false));
    }

    @Test
    public void sendToBulkPrintWith50ExtraCopiesWDG() {

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
        when(documentTransformer.hasDocumentWithType(Collections.singletonList(document), DocumentType.WELSH_DIGITAL_GRANT)).thenReturn(true);

        SendLetterResponse response = bulkPrintService.sendToBulkPrintForGrant(callbackRequest, document, coverSheet);

        verify(sendLetterApiMock).sendLetter(anyString(), any(LetterV3.class));

        assertNotNull(response);
        assertThat(response.letterId, is(uuid));
    }

    @Test
    public void sendToBulkPrintWith50ExtraCopiesWIG() {

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
        when(documentTransformer.hasDocumentWithType(Collections.singletonList(document), DocumentType.WELSH_INTESTACY_GRANT)).thenReturn(true);

        SendLetterResponse response = bulkPrintService.sendToBulkPrintForGrant(callbackRequest, document, coverSheet);

        verify(sendLetterApiMock).sendLetter(anyString(), any(LetterV3.class));

        assertNotNull(response);
        assertThat(response.letterId, is(uuid));
    }

    @Test
    public void sendToBulkPrintWith50ExtraCopiesAWDG() {

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
        when(documentTransformer.hasDocumentWithType(Collections.singletonList(document), DocumentType.WELSH_ADMON_WILL_GRANT)).thenReturn(true);

        SendLetterResponse response = bulkPrintService.sendToBulkPrintForGrant(callbackRequest, document, coverSheet);

        verify(sendLetterApiMock).sendLetter(anyString(), any(LetterV3.class));

        assertNotNull(response);
        assertThat(response.letterId, is(uuid));
    }
    
    @Captor 
    private ArgumentCaptor<LetterV3> letterV3ArgumentCaptor;
    
    @Test
    public void shouldSendToBulkPrintForReprint() {

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

        when(sendLetterApiMock.sendLetter(anyString(), letterV3ArgumentCaptor.capture())).thenReturn(sendLetterResponse);
        when(eventValidationService.validateBulkPrintResponse(eq(uuid.toString()), any())).thenReturn(callbackResponse);

        SendLetterResponse response = bulkPrintService.sendDocumentsForReprint(callbackRequest, grant, coverSheet);

        assertEquals(sendLetterResponse, response);
        assertEquals(1, letterV3ArgumentCaptor.getValue().documents.get(0).copies);
        assertEquals(10, letterV3ArgumentCaptor.getValue().documents.get(1).copies);

        verify(sendLetterApiMock).sendLetter(anyString(), any(LetterV3.class));
    }

    @Test
    public void shouldSendToBulkPrintForReprintWillNullLetterId() {

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
    public void shouldErrorOnSendToBulkPrintForReprint() {

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
        errors.add("test error");
        CallbackResponse callbackResponse = CallbackResponse.builder()
            .data(responseCaseData)
            .errors(errors)
            .build();
        
        when(sendLetterApiMock.sendLetter(anyString(), any(LetterV3.class))).thenReturn(sendLetterResponse);
        when(eventValidationService.validateBulkPrintResponse(eq(uuid.toString()), any())).thenReturn(callbackResponse);

        when(businessValidationMessageService.generateError(any(), any())).thenReturn(FieldErrorResponse.builder().build());

        assertThatThrownBy(() -> bulkPrintService.sendDocumentsForReprint(callbackRequest, grant, coverSheet))
            .isInstanceOf(BulkPrintException.class).hasMessage("Bulk print send letter for reprint response is null for: 0");
    }
    
    private void testSuccessfulSendToBulkPrintForDocumentType(DocumentType documentType) {
        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
            .addressLine2("Address 2")
            .postCode("EC2")
            .country("UK")
            .build();
        CaseData caseData = CaseData.builder()
            .primaryApplicantEmailAddress("email@email.com")
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
        when(documentTransformer.hasDocumentWithType(Collections.singletonList(document), documentType)).thenReturn(true);

        String letterId = bulkPrintService.optionallySendToBulkPrint(callbackRequest, coverSheet, document, true);

        verify(sendLetterApiMock).sendLetter(anyString(), letterV3ArgumentCaptor.capture());

        assertEquals(1, letterV3ArgumentCaptor.getValue().documents.get(0).copies);
        assertEquals(2, letterV3ArgumentCaptor.getValue().documents.get(1).copies);

        assertNotNull(letterId);
        assertThat(letterId, is(uuid.toString()));
    }


}