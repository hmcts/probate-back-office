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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT400421_VALUE;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.NOT_APPLICABLE_VALUE;


class NaValidationRuleTest {

    @InjectMocks
    private NaValidationRule naValidationRule;

    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;
    private static final String uniqueCode = "CTS 0405231104 3tpp s8e9";
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;
    private CaseData dataMock;
    @Mock
    private CaseDetails detailsMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        dataMock = CaseData.builder()
                .ihtFormEstateValuesCompleted(YES)
                .ihtFormEstate(IHT400421_VALUE).build();
        detailsMock = new CaseDetails(dataMock, LAST_MODIFIED, CASE_ID);
    }

    @Test
    void shouldReturnNoErrorForCorrectUniqueCode() {
        detailsMock = new CaseDetails(dataMock, LAST_MODIFIED, CASE_ID);
        assertDoesNotThrow(() -> {
            naValidationRule.validate(detailsMock);
        });
    }

    @Test
    void shouldReturnErrorForNaSelection() {
        dataMock = CaseData.builder()
                .ihtFormEstateValuesCompleted(YES)
                .ihtFormEstate(NOT_APPLICABLE_VALUE).build();
        detailsMock = new CaseDetails(dataMock, LAST_MODIFIED, CASE_ID);

        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            naValidationRule.validate(detailsMock);
        });
        assertEquals("NA selection is invalid: 12345678987654321", exception.getMessage());
    }

    @Test
    void shouldReturnNoerrorForEstateCompletedNo() {
        dataMock = CaseData.builder()
                .ihtFormEstateValuesCompleted(NO).build();
        detailsMock = new CaseDetails(dataMock, LAST_MODIFIED, CASE_ID);

        naValidationRule.validate(detailsMock);
    }
}
