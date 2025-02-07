package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.StopReason;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class StopReasonValidationRuleTest {

    @InjectMocks
    private StopReasonValidationRule stopReasonValidationRule;

    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;
    private static final Long CASE_ID = 12345678987654321L;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private CaseData dataMock;
    @Mock
    private CaseDetails detailsMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(businessValidationMessageRetriever.getMessage(any(), any(), any()))
                .thenReturn("You cannot use stop reason **NOT TO BE USED (Other)**. "
                        + "You must select a specific stop reason from the case stop reason list");
    }

    @Test
    void shouldReturnNoErrorForNonOther() {
        List<CollectionMember<StopReason>> stopReasonList = Arrays.asList(
                new CollectionMember<>(null,
                        StopReason.builder()
                                .caseStopReason("Item")
                                .build()));
        dataMock = CaseData.builder()
                .boCaseStopReasonList(stopReasonList).build();
        detailsMock = new CaseDetails(dataMock,LAST_MODIFIED,CASE_ID);
        assertDoesNotThrow(() -> {
            stopReasonValidationRule.validate(detailsMock);
        });
    }

    @Test
    void shouldThrowExceptionForOtherReason() {
        List<CollectionMember<StopReason>> stopReasonList = Arrays.asList(
                new CollectionMember<>(null,
                        StopReason.builder()
                                .caseStopReason("Other")
                                .build()));
        dataMock = CaseData.builder()
                .boCaseStopReasonList(stopReasonList).build();
        detailsMock = new CaseDetails(dataMock,LAST_MODIFIED,CASE_ID);
        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            stopReasonValidationRule.validate(detailsMock);
        });
        assertEquals("You cannot use stop reason **NOT TO BE USED (Other)**. "
                + "You must select a specific stop reason from the case stop reason list", exception.getMessage());
    }
}
