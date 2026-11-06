package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        CallbackRequest request = mock(CallbackRequest.class);
        CaseDetails details = mock(CaseDetails.class);
        when(request.getCaseDetails()).thenReturn(details);
        when(details.getData()).thenReturn(caseWithUploaded(List.of(uploaded("1", "No"), uploaded("2", "No"))));
        assertThrows(BusinessValidationException.class, () -> service.validateFinalWillIndicator(request));
    }

    @Test
    void addingAnUploadedDocumentIsRejected() {
        var before = caseWithUploaded(List.of(uploaded("1", "No")));
        var after = caseWithUploaded(List.of(uploaded("1", "No"), uploaded("2", "No")));
        assertThrows(BusinessValidationException.class,
                () -> service.validateIssueGrantDocuments(request(before, after)));
    }

    @Test
    void removingAnUploadedDocumentIsRejected() {
        var before = caseWithUploaded(List.of(uploaded("1", "No"), uploaded("2", "No")));
        var after = caseWithUploaded(List.of(uploaded("1", "No")));
        assertThrows(BusinessValidationException.class,
                () -> service.validateIssueGrantDocuments(request(before, after)));
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
