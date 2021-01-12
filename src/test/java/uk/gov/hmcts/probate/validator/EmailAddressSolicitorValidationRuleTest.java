package uk.gov.hmcts.probate.validator;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

public class EmailAddressSolicitorValidationRuleTest {

    @InjectMocks
    private EmailAddressSolicitorValidationRule emailAddressSolicitorValidationRule;

    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;

    private CaseData caseDataEmptyEmail;
    private CaseData caseDataEmptyEmailNull;
    private CaseData caseDataEmailInvalid1;
    private CaseData caseDataEmailInvalid2;
    private CaseData caseDataEmailInvalid3;
    private CaseData caseDataEmailInvalid4;
    private CaseData caseDataEmailValid;

    private static final String[] LAST_MODIFIED = {"2020", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        caseDataEmptyEmail = CaseData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .solsSolicitorEmail("")
                .registryLocation("Bristol")
                .build();

        caseDataEmptyEmailNull = CaseData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .solsSolicitorEmail(null)
                .registryLocation("Bristol")
                .build();

        caseDataEmailInvalid1 = CaseData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .solsSolicitorEmail("example.@test.com")
                .registryLocation("Bristol")
                .build();

        caseDataEmailInvalid2 = CaseData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .solsSolicitorEmail("example@.test.com")
                .registryLocation("Bristol")
                .build();

        caseDataEmailInvalid3 = CaseData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .solsSolicitorEmail("example@test.com.")
                .registryLocation("Bristol")
                .build();

        caseDataEmailInvalid4 = CaseData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .solsSolicitorEmail(".example@test.com")
                .registryLocation("Bristol")
                .build();

        caseDataEmailValid = CaseData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .solsSolicitorEmail("example@test.com")
                .registryLocation("Bristol")
                .build();
    }

    @Test
    public void shouldThrowApplyingExecEmailIsEmpty() {
        CaseDetails caseDetailsEmptyEmail =
                new CaseDetails(caseDataEmptyEmail, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            emailAddressSolicitorValidationRule.validate(caseDetailsEmptyEmail);
        })
                .isInstanceOf(BusinessValidationException.class);
    }


    @Test
    public void shouldThrowApplyingExecEmailIsInvalid1() {
        CaseDetails caseDetailsEmailInvalid =
                new CaseDetails(caseDataEmailInvalid1, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            emailAddressSolicitorValidationRule.validate(caseDetailsEmailInvalid);
        })
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("Solicitor's email does not meet the criteria for case id 12345678987654321");
    }

    @Test
    public void shouldThrowApplyingExecEmailIsInvalid2() {
        CaseDetails caseDetailsEmailInvalid =
                new CaseDetails(caseDataEmailInvalid2, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            emailAddressSolicitorValidationRule.validate(caseDetailsEmailInvalid);
        })
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("Solicitor's email does not meet the criteria for case id 12345678987654321");
    }

    @Test
    public void shouldThrowApplyingExecEmailIsInvalid3() {
        CaseDetails caseDetailsEmailInvalid =
                new CaseDetails(caseDataEmailInvalid3, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            emailAddressSolicitorValidationRule.validate(caseDetailsEmailInvalid);
        })
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("Solicitor's email does not meet the criteria for case id 12345678987654321");
    }

    @Test
    public void shouldThrowApplyingExecEmailIsInvalid4() {
        CaseDetails caseDetailsEmailInvalid =
                new CaseDetails(caseDataEmailInvalid4, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            emailAddressSolicitorValidationRule.validate(caseDetailsEmailInvalid);
        })
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("Solicitor's email does not meet the criteria for case id 12345678987654321");
    }

    @Test
    public void ApplyingExecEmailIsValid() {
        CaseDetails caseDetailsEmailValid =
                new CaseDetails(caseDataEmailValid, LAST_MODIFIED, CASE_ID);
        emailAddressSolicitorValidationRule.validate(caseDetailsEmailValid);
    }

    @Test
    public void ApplyingExecEmailIsNull() {
        CaseDetails caseDetailsEmailNull =
                new CaseDetails(caseDataEmptyEmailNull, LAST_MODIFIED, CASE_ID);
        emailAddressSolicitorValidationRule.validate(caseDetailsEmailNull);
    }
}