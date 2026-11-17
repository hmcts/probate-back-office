package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvidenceUploadServiceFinalWillTest {

    @InjectMocks
    private EvidenceUploadService service;

    private CollectionMember<UploadDocument> uploaded(String id, String finalWill) {
        return new CollectionMember<>(id, UploadDocument.builder()
                .documentType(DocumentType.WILL).finalWill(finalWill).build());
    }

    private CaseData caseWithUploaded(List<CollectionMember<UploadDocument>> documents) {
        return CaseData.builder().boDocumentsUploaded(documents).build();
    }

    @Test
    void reviewIsSkippedWhenExactlyOneOfMultipleWillsIsAlreadyFinal() {
        CaseData caseData = caseWithUploaded(List.of(uploaded("1", "Yes"), uploaded("2", "No")));
        assertEquals(2, service.countWillDocuments(caseData));
        assertEquals(1, service.countFinalWills(caseData));
        assertFalse(service.showReviewFinalWillPage(caseData));
    }

    @Test
    void reviewIsRequiredWhenNoneOrMultipleAreFinal() {
        assertTrue(service.showReviewFinalWillPage(caseWithUploaded(List.of(
                uploaded("1", "No"), uploaded("2", null)))));
        assertTrue(service.showReviewFinalWillPage(caseWithUploaded(List.of(
                uploaded("1", "Yes"), uploaded("2", "Yes")))));
    }

    @Test
    void singleWillDefaultsToYesEvenIfPreviouslyNo() {
        var documents = service.defaultUploadedFinalWillIndicator(List.of(uploaded("1", "No")), true);
        assertEquals("Yes", documents.getFirst().getValue().getFinalWill());
        assertEquals("1", documents.getFirst().getId());
    }

    @Test
    void missingValuesDefaultToNoWithoutOverwritingYes() {
        var documents = service.defaultUploadedFinalWillIndicator(List.of(
                uploaded("1", "Yes"), uploaded("2", null)), false);
        assertEquals("Yes", documents.getFirst().getValue().getFinalWill());
        assertEquals("No", documents.get(1).getValue().getFinalWill());
    }

    @Test
    void finalWillSelectionRequiresExactlyOneForMultipleWills() {
        CallbackRequest callbackRequest = requestFor(caseWithUploaded(List.of(
                uploaded("1", "No"), uploaded("2", "No"))));
        assertThrows(BusinessValidationException.class,
                () -> service.validateFinalWillIndicator(callbackRequest));
    }

    @Test
    void multipleFinalWillsAreRejected() {
        CallbackRequest callbackRequest = requestFor(caseWithUploaded(List.of(
                uploaded("1", "Yes"), uploaded("2", "Yes"))));
        assertThrows(BusinessValidationException.class,
                () -> service.validateFinalWillIndicator(callbackRequest));
    }

    @Test
    void oneFinalWillAcrossMultipleDocumentsIsAccepted() {
        CallbackRequest callbackRequest = requestFor(caseWithUploaded(List.of(
                uploaded("1", "Yes"), uploaded("2", null))));
        assertDoesNotThrow(() -> service.validateFinalWillIndicator(callbackRequest));
    }

    @Test
    void noAndSingleWillsDoNotRequireFinalSelection() {
        CallbackRequest empty = requestFor(CaseData.builder().build());
        CallbackRequest single = requestFor(caseWithUploaded(List.of(uploaded("1", null))));
        assertDoesNotThrow(() -> service.validateFinalWillIndicator(empty));
        assertDoesNotThrow(() -> service.validateFinalWillIndicator(single));
        assertEquals(0, service.countWillDocuments(CaseData.builder().build()));
    }

    @Test
    void countsUploadedAndScannedWillDocumentsAndFinalSelections() {
        CaseData caseData = CaseData.builder()
                .boDocumentsUploaded(List.of(uploaded("1", "Yes")))
                .scannedDocuments(List.of(
                        scanned("2", "will", null, "No"),
                        scanned("3", "other", "will", "Yes"),
                        scanned("4", "other", "not a will", "No")))
                .build();
        assertEquals(3, service.countWillDocuments(caseData));
        assertEquals(2, service.countFinalWills(caseData));
        assertTrue(service.showReviewFinalWillPage(caseData));
    }

    @Test
    void scannedWillDefaultsToYesWhenItIsTheOnlyWill() {
        var result = service.defaultScannedFinalWillIndicator(
                List.of(scanned("1", "will", null, null)), true);
        assertEquals("Yes", result.getFirst().getValue().getFinalWill());
        assertEquals("1", result.getFirst().getId());
    }

    @Test
    void scannedMissingValueDefaultsToNoWithoutOverwritingExistingYes() {
        var result = service.defaultScannedFinalWillIndicator(List.of(
                scanned("1", "will", null, "Yes"),
                scanned("2", "other", "will", " "),
                scanned("3", "other", "not a will", null)), false);
        assertEquals("Yes", result.get(0).getValue().getFinalWill());
        assertEquals("No", result.get(1).getValue().getFinalWill());
        assertNull(result.get(2).getValue().getFinalWill());
    }

    @Test
    void nullInputCollectionsReturnEmptyCollections() {
        assertTrue(service.defaultScannedFinalWillIndicator(null, true).isEmpty());
        assertTrue(service.defaultUploadedFinalWillIndicator(null, true).isEmpty());
    }

    @Test
    void uploadedNonWillDocumentsAreNotModified() {
        var other = new CollectionMember<>("other", UploadDocument.builder()
                .documentType(DocumentType.GRANT_COVER).build());
        var result = service.defaultUploadedFinalWillIndicator(List.of(other), true);
        assertSame(other, result.getFirst());
    }

    @Test
    void addingAnUploadedDocumentIsRejected() {
        var before = caseWithUploaded(List.of(uploaded("1", "No")));
        var after = caseWithUploaded(List.of(uploaded("1", "No"), uploaded("2", "No")));
        CallbackRequest callbackRequest = request(before, after);
        assertThrows(BusinessValidationException.class,
                () -> service.validateIssueGrantDocuments(callbackRequest));
    }

    @Test
    void removingAnUploadedDocumentIsRejected() {
        var before = caseWithUploaded(List.of(uploaded("1", "No"), uploaded("2", "No")));
        var after = caseWithUploaded(List.of(uploaded("1", "No")));
        CallbackRequest callbackRequest = request(before, after);
        assertThrows(BusinessValidationException.class,
                () -> service.validateIssueGrantDocuments(callbackRequest));
    }

    @Test
    void addingScannedDocumentIsRejected() {
        var before = CaseData.builder().scannedDocuments(List.of(
                scanned("1", "will", null, "No"))).build();
        var after = CaseData.builder().scannedDocuments(List.of(
                scanned("1", "will", null, "No"), scanned("2", "will", null, "No"))).build();
        CallbackRequest callbackRequest = request(before, after);
        assertThrows(BusinessValidationException.class,
                () -> service.validateIssueGrantDocuments(callbackRequest));
    }

    @Test
    void removingScannedDocumentIsRejected() {
        var before = CaseData.builder().scannedDocuments(List.of(
                scanned("1", "will", null, "No"), scanned("2", "will", null, "No"))).build();
        var after = CaseData.builder().scannedDocuments(List.of(
                scanned("1", "will", null, "No"))).build();
        CallbackRequest callbackRequest = request(before, after);
        assertThrows(BusinessValidationException.class,
                () -> service.validateIssueGrantDocuments(callbackRequest));
    }

    @Test
    void changingScannedFinalIndicatorIsAllowed() {
        var before = CaseData.builder().scannedDocuments(List.of(
                scanned("1", "will", null, "No"))).build();
        var after = CaseData.builder().scannedDocuments(List.of(
                scanned("1", "will", null, "Yes"))).build();
        CallbackRequest callbackRequest = request(before, after);
        assertDoesNotThrow(() -> service.validateIssueGrantDocuments(callbackRequest));
    }

    private CollectionMember<ScannedDocument> scanned(String id, String type, String subtype, String finalWill) {
        return new CollectionMember<>(id, ScannedDocument.builder()
                .type(type)
                .subtype(subtype)
                .finalWill(finalWill)
                .url(DocumentLink.builder().documentUrl("http://documents.example/" + id).build())
                .build());
    }

    private CallbackRequest requestFor(CaseData caseData) {
        CallbackRequest request = mock(CallbackRequest.class);
        CaseDetails details = mock(CaseDetails.class);
        when(request.getCaseDetails()).thenReturn(details);
        when(details.getData()).thenReturn(caseData);
        return request;
    }

    private CallbackRequest request(CaseData before, CaseData after) {
        CallbackRequest request = mock(CallbackRequest.class);
        CaseDetails beforeDetails = mock(CaseDetails.class);
        CaseDetails afterDetails = mock(CaseDetails.class);
        when(beforeDetails.getData()).thenReturn(before);
        when(afterDetails.getData()).thenReturn(after);
        when(request.getCaseDetailsBefore()).thenReturn(beforeDetails);
        when(request.getCaseDetails()).thenReturn(afterDetails);
        return request;
    }
}
