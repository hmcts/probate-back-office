package uk.gov.hmcts.probate.validator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
class DobOverrideValidationRuleTest {
    @InjectMocks
    private DobOverrideValidationRule dobOverrideValidationRule;

    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;

    private CaseData caseData;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldTPassForValidDobOverrideDate() {
        caseData = CaseData.builder()
                .deceasedDateOfDeath(LocalDate.of(1900, 1, 1))
                .dobOverride("1800-12-31")
                .build();
        CaseDetails caseDetails =
                new CaseDetails(caseData, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatNoException().isThrownBy(() -> {
            dobOverrideValidationRule.validate(caseDetails);
        });
    }

    @Test
    void shouldThrowAExceptionForInvalidDobOverrideDate() {
        caseData = CaseData.builder()
                .deceasedDateOfDeath(LocalDate.of(1900, 1, 1))
                .dobOverride("1800-31-12")
                .build();
        CaseDetails caseDetails =
                new CaseDetails(caseData, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            dobOverrideValidationRule.validate(caseDetails);
        })
            .isInstanceOf(BusinessValidationException.class)
            .hasMessage("dob override date is not valid for case: 12345678987654321");
    }

}
