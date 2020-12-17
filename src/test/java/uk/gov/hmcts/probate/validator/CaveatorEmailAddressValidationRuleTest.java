package uk.gov.hmcts.probate.validator;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

public class CaveatorEmailAddressValidationRuleTest {

    @InjectMocks
    private CaveatorEmailAddressValidationRule caveatorEmailAddressValidationRule;

    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;

    private CaveatData CaveatDataEmptyEmail;
    private CaveatData CaveatDataEmptyEmailNull;
    private CaveatData CaveatDataEmailInvalid1;
    private CaveatData CaveatDataEmailInvalid2;
    private CaveatData CaveatDataEmailInvalid3;
    private CaveatData CaveatDataEmailInvalid4;
    private CaveatData CaveatDataEmailValid;

    private static final String[] LAST_MODIFIED = {"2020", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        CaveatDataEmptyEmail = CaveatData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .caveatorEmailAddress("")
                .registryLocation("Bristol")
                .build();

        CaveatDataEmptyEmailNull = CaveatData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .caveatorEmailAddress(null)
                .registryLocation("Bristol")
                .build();

        CaveatDataEmailInvalid1 = CaveatData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .caveatorEmailAddress("example.@test.com")
                .registryLocation("Bristol")
                .build();

        CaveatDataEmailInvalid2 = CaveatData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .caveatorEmailAddress("example@.test.com")
                .registryLocation("Bristol")
                .build();

        CaveatDataEmailInvalid3 = CaveatData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .caveatorEmailAddress("example@test.com.")
                .registryLocation("Bristol")
                .build();

        CaveatDataEmailInvalid4 = CaveatData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .caveatorEmailAddress(".example@test.com")
                .registryLocation("Bristol")
                .build();

        CaveatDataEmailValid = CaveatData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .caveatorEmailAddress("example@test.com")
                .registryLocation("Bristol")
                .build();
    }

    @Test
    public void shouldThrowApplyingExecEmailIsEmpty() {
        CaveatDetails CaveatDetailsEmptyEmail =
                new CaveatDetails(CaveatDataEmptyEmail, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            caveatorEmailAddressValidationRule.validate(CaveatDetailsEmptyEmail);
        })
                .isInstanceOf(BusinessValidationException.class);
    }


    @Test
    public void shouldThrowApplyingExecEmailIsInvalid1() {
        CaveatDetails CaveatDetailsEmptyEmail =
                new CaveatDetails(CaveatDataEmailInvalid1, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            caveatorEmailAddressValidationRule.validate(CaveatDetailsEmptyEmail);
        })
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("Caveator email does not meet the criteria for case id 12345678987654321");
    }

    @Test
    public void shouldThrowApplyingExecEmailIsInvalid2() {
        CaveatDetails CaveatDetailsEmptyEmail =
                new CaveatDetails(CaveatDataEmailInvalid2, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            caveatorEmailAddressValidationRule.validate(CaveatDetailsEmptyEmail);
        })
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("Caveator email does not meet the criteria for case id 12345678987654321");
    }

    @Test
    public void shouldThrowApplyingExecEmailIsInvalid3() {
        CaveatDetails CaveatDetailsEmptyEmail =
                new CaveatDetails(CaveatDataEmailInvalid3, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            caveatorEmailAddressValidationRule.validate(CaveatDetailsEmptyEmail);
        })
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("Caveator email does not meet the criteria for case id 12345678987654321");
    }

    @Test
    public void shouldThrowApplyingExecEmailIsInvalid4() {
        CaveatDetails CaveatDetailsEmptyEmail =
                new CaveatDetails(CaveatDataEmailInvalid4, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            caveatorEmailAddressValidationRule.validate(CaveatDetailsEmptyEmail);
        })
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("Caveator email does not meet the criteria for case id 12345678987654321");
    }

    @Test
    public void ApplyingExecEmailIsValid() {
        CaveatDetails CaveatDetailsEmptyEmail =
                new CaveatDetails(CaveatDataEmailValid, LAST_MODIFIED, CASE_ID);
        caveatorEmailAddressValidationRule.validate(CaveatDetailsEmptyEmail);
    }

    @Test
    public void ApplyingExecEmailIsNull() {
        CaveatDetails CaveatDetailsEmptyEmail =
                new CaveatDetails(CaveatDataEmptyEmailNull, LAST_MODIFIED, CASE_ID);
        caveatorEmailAddressValidationRule.validate(CaveatDetailsEmptyEmail);
    }
}
