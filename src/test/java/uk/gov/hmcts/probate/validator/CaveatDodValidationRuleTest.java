package uk.gov.hmcts.probate.validator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.time.LocalDate;


class CaveatDodValidationRuleTest {

    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;
    @InjectMocks
    private CaveatDodValidationRule caveatDodValidationRule;
    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;
    private CaveatData caveatDataNoException;
    private CaveatData caveatDataWithDodException;
    private static final LocalDate DATE_01_JAN_1971 = LocalDate.of(1971, 1, 1);

    private static final LocalDate DATE_02_JAN_2099 = LocalDate.of(2099, 1, 2);

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        caveatDataNoException = CaveatData.builder()
            .applicationType(ApplicationType.PERSONAL)
            .deceasedDateOfDeath(DATE_01_JAN_1971)
            .registryLocation("Bristol").build();

        caveatDataWithDodException = CaveatData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .deceasedDateOfDeath(DATE_02_JAN_2099)
                .registryLocation("Bristol").build();
    }

    @Test
    void shouldNotThrowDodExceptionRaiseCaveatValidate() {
        CaveatDetails caveatDetails =
                new CaveatDetails(caveatDataNoException, LAST_MODIFIED, CASE_ID);
        caveatDodValidationRule.validate(caveatDetails);
    }

    @Test
    void shouldThrowDodExceptionRaiseCaveatValidate() {
        CaveatDetails caveatDetails =
                new CaveatDetails(caveatDataWithDodException, LAST_MODIFIED, CASE_ID);
        Assertions.assertThatThrownBy(() -> {
            caveatDodValidationRule.validate(caveatDetails);
        })
            .isInstanceOf(BusinessValidationException.class)
            .hasMessage("Date of death cannot be in the future");
    }


}

