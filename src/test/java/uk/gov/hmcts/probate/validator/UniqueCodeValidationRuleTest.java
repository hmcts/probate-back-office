package uk.gov.hmcts.probate.validator;

import org.assertj.core.api.Assertions;
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


class UniqueCodeValidationRuleTest {

    @InjectMocks
    private UniqueCodeValidationRule uniqueCodeValidationRule;

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
                .uniqueProbateCodeId(uniqueCode).build();
        detailsMock = new CaseDetails(dataMock, LAST_MODIFIED, CASE_ID);
    }

    @Test
    void shouldReturnNoErrorForCorrectUniqueCode() {

        Assertions.assertThatThrownBy(() -> {
            uniqueCodeValidationRule.validate(detailsMock);
        }).isInstanceOf(BusinessValidationException.class).hasMessage(
                        "Unique Probate code is invalid: 12345678987654321");
    }

    @Test
    void shouldReturnErrorForSpecialCharacter() {
        dataMock = CaseData.builder()
                .uniqueProbateCodeId(uniqueCode + "@%").build();
        detailsMock = new CaseDetails(dataMock, LAST_MODIFIED, CASE_ID);

        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            uniqueCodeValidationRule.validate(detailsMock);
        });
        assertEquals("Unique Probate code is invalid: 12345678987654321", exception.getMessage());
    }

    @Test
    void shouldReturnErrorForExtraCharacter() {
        dataMock = CaseData.builder()
                .uniqueProbateCodeId(uniqueCode + "123").build();
        detailsMock = new CaseDetails(dataMock, LAST_MODIFIED, CASE_ID);

        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            uniqueCodeValidationRule.validate(detailsMock);
        });
        assertEquals("Unique Probate code is invalid: 12345678987654321", exception.getMessage());
    }

    @Test
    void shouldReturnErrorForIncorrectStartingCharacters() {
        dataMock = CaseData.builder()
                .uniqueProbateCodeId("abc" + uniqueCode.substring(4)).build();
        detailsMock = new CaseDetails(dataMock, LAST_MODIFIED, CASE_ID);

        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            uniqueCodeValidationRule.validate(detailsMock);
        });
        assertEquals("Unique Probate code is invalid: 12345678987654321", exception.getMessage());
    }

    @Test
    void shouldReturnErrorForLessCharacters() {
        dataMock = CaseData.builder()
                .uniqueProbateCodeId(uniqueCode.substring(0, uniqueCode.length()-3)).build();
        detailsMock = new CaseDetails(dataMock, LAST_MODIFIED, CASE_ID);

        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            uniqueCodeValidationRule.validate(detailsMock);
        });
        assertEquals("Unique Probate code is invalid: 12345678987654321", exception.getMessage());
    }
}
