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

    private CaveatData caveatDataEmptyEmail;
    private CaveatData caveatDataEmptyEmailNull;
    private CaveatData caveatDataEmailInvalid1;
    private CaveatData caveatDataEmailInvalid2;
    private CaveatData caveatDataEmailInvalid3;
    private CaveatData caveatDataEmailInvalid4;
    private CaveatData caveatDataEmailValid1;

    private static final String[] LAST_MODIFIED = {"2020", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        caveatDataEmptyEmail = CaveatData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .caveatorEmailAddress("")
                .registryLocation("Bristol")
                .build();

        caveatDataEmptyEmailNull = CaveatData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .caveatorEmailAddress(null)
                .registryLocation("Bristol")
                .build();

        caveatDataEmailInvalid1 = CaveatData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .caveatorEmailAddress("example.@probate-test.com")
                .registryLocation("Bristol")
                .build();

        caveatDataEmailInvalid2 = CaveatData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .caveatorEmailAddress("example@.probate-test.com")
                .registryLocation("Bristol")
                .build();

        caveatDataEmailInvalid3 = CaveatData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .caveatorEmailAddress("example@probate-test.com.")
                .registryLocation("Bristol")
                .build();

        caveatDataEmailInvalid4 = CaveatData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .caveatorEmailAddress(".example@probate-test.com")
                .registryLocation("Bristol")
                .build();

        caveatDataEmailValid1 = CaveatData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .caveatorEmailAddress("example@probate-test.com")
                .registryLocation("Bristol")
                .build();

    }

    @Test
    public void shouldThrowApplyingExecEmailIsEmpty() {
        CaveatDetails caveatDetailsEmptyEmail =
                new CaveatDetails(caveatDataEmptyEmail, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            caveatorEmailAddressValidationRule.validate(caveatDetailsEmptyEmail);
        })
                .isInstanceOf(BusinessValidationException.class);
    }


    @Test
    public void shouldThrowApplyingExecEmailIsInvalid1() {
        CaveatDetails caveatDetailsInvalidEmail =
                new CaveatDetails(caveatDataEmailInvalid1, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            caveatorEmailAddressValidationRule.validate(caveatDetailsInvalidEmail);
        })
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("Caveator email does not meet the criteria for case id 12345678987654321");
    }

    @Test
    public void shouldThrowApplyingExecEmailIsInvalid2() {
        CaveatDetails caveatDetailsInvalidEmail =
                new CaveatDetails(caveatDataEmailInvalid2, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            caveatorEmailAddressValidationRule.validate(caveatDetailsInvalidEmail);
        })
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("Caveator email does not meet the criteria for case id 12345678987654321");
    }

    @Test
    public void shouldThrowApplyingExecEmailIsInvalid3() {
        CaveatDetails caveatDetailsInvalidEmail =
                new CaveatDetails(caveatDataEmailInvalid3, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            caveatorEmailAddressValidationRule.validate(caveatDetailsInvalidEmail);
        })
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("Caveator email does not meet the criteria for case id 12345678987654321");
    }

    @Test
    public void shouldThrowApplyingExecEmailIsInvalid4() {
        CaveatDetails caveatDetailsInvalidEmail =
                new CaveatDetails(caveatDataEmailInvalid4, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            caveatorEmailAddressValidationRule.validate(caveatDetailsInvalidEmail);
        })
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("Caveator email does not meet the criteria for case id 12345678987654321");
    }

    @Test
    public void applyingExecEmailIsValid() {
        CaveatDetails caveatDetailsValidEmail =
                new CaveatDetails(caveatDataEmailValid1, LAST_MODIFIED, CASE_ID);
        caveatorEmailAddressValidationRule.validate(caveatDetailsValidEmail);
    }

    @Test
    public void applyingExecEmailIsNull() {
        CaveatDetails caveatDetailsNullEmail =
                new CaveatDetails(caveatDataEmptyEmailNull, LAST_MODIFIED, CASE_ID);
        caveatorEmailAddressValidationRule.validate(caveatDetailsNullEmail);
    }

}