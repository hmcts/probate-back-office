package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


class CaseTypeValidationRuleTest {

    @InjectMocks
    private CaseTypeValidationRule caseTypeValidationRule;

    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;
    private CaseData dataMock;
    @Mock
    private CaseDetails detailsMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        dataMock = CaseData.builder().caseType("gop").build();
        detailsMock = new CaseDetails(dataMock, LAST_MODIFIED, CASE_ID);
    }

    @Test
    void shouldReturnErrorForNaSelection() {
        dataMock = CaseData.builder()
                .caseType("adColligendaBona").build();
        detailsMock = new CaseDetails(dataMock, LAST_MODIFIED, CASE_ID);

        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            caseTypeValidationRule.validate(detailsMock);
        });
        assertEquals("Ad Colligenda Bona selection is invalid: 12345678987654321", exception.getMessage());
    }

    @Test
    void shouldReturnNoerrorForEstateCompletedNo() {
        assertDoesNotThrow(() -> {
            caseTypeValidationRule.validate(detailsMock);
        });
    }
}
