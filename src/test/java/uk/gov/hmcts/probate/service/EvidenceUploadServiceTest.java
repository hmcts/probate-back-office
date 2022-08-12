package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.time.LocalDate;

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
    void shouldGatherValidationErrors() {

        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        evidenceUploadService.updateLastEvidenceAddedDate(caseDetailsMock);

        verify(caseDataMock).setLastEvidenceAddedDate(any(LocalDate.class));

    }


}
