package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EvidenceUploadServiceTest {

    @InjectMocks
    private EvidenceUploadService evidenceUploadService;

    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private CaseData caseDataMock;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldVerifyUpdateOfLastEvidenceAddedDate() {

        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        evidenceUploadService.updateLastEvidenceAddedDate(caseDetailsMock);

        verify(caseDataMock).setLastEvidenceAddedDate(any(LocalDate.class));

    }

    @Test
    void shouldThrowWhenExistingUploadedDocumentWasModified() {
        UploadDocument beforeDoc = UploadDocument.builder().comment("before").build();
        UploadDocument afterDoc = UploadDocument.builder().comment("after").build();

        CaseData beforeData = CaseData.builder()
                .boDocumentsUploaded(List.of(new CollectionMember<>("doc-id", beforeDoc)))
                .build();
        CaseData afterData = CaseData.builder()
                .boDocumentsUploaded(List.of(new CollectionMember<>("doc-id", afterDoc)))
                .build();

        CaseDetails beforeDetails = new CaseDetails(beforeData, null, 123L);
        CaseDetails afterDetails = new CaseDetails(afterData, null, 123L);
        CallbackRequest callbackRequest = new CallbackRequest(afterDetails);
        callbackRequest.setCaseDetailsBefore(beforeDetails);

        assertThrows(BusinessValidationException.class,
                () -> evidenceUploadService.validateExistingUploadedDocuments(callbackRequest));
    }


}
