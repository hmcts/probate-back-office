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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Pre1900DOBValidationRuleTest {

    @InjectMocks
    private Pre1900DOBValidationRule pre1900DOBValidationRule;

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
    }

    @Test
    void shouldReturnErrorForInvalidDOB() {
        dataMock = CaseData.builder()
                .deceasedDob("1889-13-31")
                .build();
        detailsMock = new CaseDetails(dataMock, LAST_MODIFIED, CASE_ID);

        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            pre1900DOBValidationRule.validate(detailsMock);
        });
        assertEquals("Date of birth is invalid format for case: 12345678987654321", exception.getMessage());
    }

    @Test
    void shouldReturnErrorForFutureDOB() {
        dataMock = CaseData.builder()
                .deceasedDob("2021-12-31")
                .deceasedDateOfDeath(LocalDate.of(2020, 1, 2))
                .build();
        detailsMock = new CaseDetails(dataMock, LAST_MODIFIED, CASE_ID);

        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            pre1900DOBValidationRule.validate(detailsMock);
        });
        assertEquals("Date of birth cannot be after date of death for case: 12345678987654321",
                exception.getMessage());
    }

    @Test
    void shouldReturnNoErrorForValidDOB() {
        dataMock = CaseData.builder()
                .deceasedDob("1889-03-31")
                .deceasedDateOfDeath(LocalDate.of(2020, 1, 2))
                .build();
        detailsMock = new CaseDetails(dataMock, LAST_MODIFIED, CASE_ID);

        pre1900DOBValidationRule.validate(detailsMock);
    }
}
