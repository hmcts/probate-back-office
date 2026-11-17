package uk.gov.hmcts.probate.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.EvidenceUploadService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReviewFinalWillSwitchDefaulterTest {

    private EvidenceUploadService evidenceUploadService;
    private ReviewFinalWillSwitchDefaulter defaulter;

    @BeforeEach
    void setUp() {
        evidenceUploadService = mock(EvidenceUploadService.class);
        defaulter = new ReviewFinalWillSwitchDefaulter(evidenceUploadService);
    }

    @Test
    void intestacySkipsReviewWithoutCountingWills() {
        CaseData caseData = CaseData.builder().caseType("intestacy").build();
        assertEquals("No", defaulter.defaultReviewFinalWillSwitch(caseData));
        verify(evidenceUploadService, never()).showReviewFinalWillPage(caseData);
    }

    @ParameterizedTest
    @CsvSource({"true,Yes", "false,No"})
    void probateUsesEvidenceServiceDecision(boolean reviewRequired, String expected) {
        CaseData caseData = CaseData.builder().caseType("gop").build();
        when(evidenceUploadService.showReviewFinalWillPage(caseData)).thenReturn(reviewRequired);
        assertEquals(expected, defaulter.defaultReviewFinalWillSwitch(caseData));
        verify(evidenceUploadService).showReviewFinalWillPage(caseData);
    }
}
