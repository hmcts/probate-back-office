package uk.gov.hmcts.probate.validator;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.Arrays;
import java.util.List;

public class EmailAddressExecutorsValidationRuleTest {

    @InjectMocks
    private EmailAddressExecutorsValidationRule emailAddressExecutorsValidationRule;

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

    private static final List<CollectionMember<AdditionalExecutorApplying>> EXECEUTORS_APPLYING_EMAIL_EMPTY = Arrays.asList(
            new CollectionMember<>("id",
                    AdditionalExecutorApplying.builder()
                            .applyingExecutorName("Name surname")
                            .applyingExecutorFirstName("Name")
                            .applyingExecutorLastName("surname")
                            .applyingExecutorPhoneNumber("")
                            .applyingExecutorEmail("")
                            .applyingExecutorAddress(null)
                            .applyingExecutorOtherNames("")
                            .applyingExecutorOtherNamesReason("")
                            .applyingExecutorOtherReason("")
                            .build()));

    private static final List<CollectionMember<AdditionalExecutorApplying>> EXECEUTORS_APPLYING_EMAIL_NULL = Arrays.asList(
            new CollectionMember<>("id",
                    AdditionalExecutorApplying.builder()
                            .applyingExecutorName("Name surname")
                            .applyingExecutorFirstName("Name")
                            .applyingExecutorLastName("surname")
                            .applyingExecutorPhoneNumber("")
                            .applyingExecutorEmail(null)
                            .applyingExecutorAddress(null)
                            .applyingExecutorOtherNames("")
                            .applyingExecutorOtherNamesReason("")
                            .applyingExecutorOtherReason("")
                            .build()));

    private static final List<CollectionMember<AdditionalExecutorApplying>> EXECEUTORS_APPLYING_EMAIL_INVALID1 = Arrays.asList(
            new CollectionMember<>("id",
                    AdditionalExecutorApplying.builder()
                            .applyingExecutorName("Name surname")
                            .applyingExecutorFirstName("Name")
                            .applyingExecutorLastName("surname")
                            .applyingExecutorPhoneNumber("")
                            .applyingExecutorEmail("example.@test.com")
                            .applyingExecutorAddress(null)
                            .applyingExecutorOtherNames("")
                            .applyingExecutorOtherNamesReason("")
                            .applyingExecutorOtherReason("")
                            .build()));

    private static final List<CollectionMember<AdditionalExecutorApplying>> EXECEUTORS_APPLYING_EMAIL_INVALID2 = Arrays.asList(
            new CollectionMember<>("id",
                    AdditionalExecutorApplying.builder()
                            .applyingExecutorName("Name surname")
                            .applyingExecutorFirstName("Name")
                            .applyingExecutorLastName("surname")
                            .applyingExecutorPhoneNumber("")
                            .applyingExecutorEmail("example@.test.com")
                            .applyingExecutorAddress(null)
                            .applyingExecutorOtherNames("")
                            .applyingExecutorOtherNamesReason("")
                            .applyingExecutorOtherReason("")
                            .build()));

    private static final List<CollectionMember<AdditionalExecutorApplying>> EXECEUTORS_APPLYING_EMAIL_INVALID3 = Arrays.asList(
            new CollectionMember<>("id",
                    AdditionalExecutorApplying.builder()
                            .applyingExecutorName("Name surname")
                            .applyingExecutorFirstName("Name")
                            .applyingExecutorLastName("surname")
                            .applyingExecutorPhoneNumber("")
                            .applyingExecutorEmail("example@test.com.")
                            .applyingExecutorAddress(null)
                            .applyingExecutorOtherNames("")
                            .applyingExecutorOtherNamesReason("")
                            .applyingExecutorOtherReason("")
                            .build()));

    private static final List<CollectionMember<AdditionalExecutorApplying>> EXECEUTORS_APPLYING_EMAIL_INVALID4 = Arrays.asList(
            new CollectionMember<>("id",
                    AdditionalExecutorApplying.builder()
                            .applyingExecutorName("Name surname")
                            .applyingExecutorFirstName("Name")
                            .applyingExecutorLastName("surname")
                            .applyingExecutorPhoneNumber("")
                            .applyingExecutorEmail(".example@test.com")
                            .applyingExecutorAddress(null)
                            .applyingExecutorOtherNames("")
                            .applyingExecutorOtherNamesReason("")
                            .applyingExecutorOtherReason("")
                            .build()));

    private static final List<CollectionMember<AdditionalExecutorApplying>> EXECEUTORS_APPLYING_EMAIL_VALID = Arrays.asList(
            new CollectionMember<>("id",
                    AdditionalExecutorApplying.builder()
                            .applyingExecutorName("Name surname")
                            .applyingExecutorFirstName("Name")
                            .applyingExecutorLastName("surname")
                            .applyingExecutorPhoneNumber("")
                            .applyingExecutorEmail("example@test.com")
                            .applyingExecutorAddress(null)
                            .applyingExecutorOtherNames("")
                            .applyingExecutorOtherNamesReason("")
                            .applyingExecutorOtherReason("")
                            .build()));

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        caseDataEmptyEmail = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .additionalExecutorsApplying(EXECEUTORS_APPLYING_EMAIL_EMPTY)
                .registryLocation("Bristol").build();

        caseDataEmailInvalid1 = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .additionalExecutorsApplying(EXECEUTORS_APPLYING_EMAIL_INVALID1)
                .registryLocation("Bristol").build();

        caseDataEmailInvalid2 = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .additionalExecutorsApplying(EXECEUTORS_APPLYING_EMAIL_INVALID2)
                .registryLocation("Bristol").build();

        caseDataEmailInvalid3 = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .additionalExecutorsApplying(EXECEUTORS_APPLYING_EMAIL_INVALID3)
                .registryLocation("Bristol").build();

        caseDataEmailInvalid4 = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .additionalExecutorsApplying(EXECEUTORS_APPLYING_EMAIL_INVALID4)
                .registryLocation("Bristol").build();

        caseDataEmailValid = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .additionalExecutorsApplying(EXECEUTORS_APPLYING_EMAIL_VALID)
                .registryLocation("Bristol").build();

        caseDataEmptyEmailNull = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .additionalExecutorsApplying(EXECEUTORS_APPLYING_EMAIL_NULL)
                .registryLocation("Bristol").build();
    }

    @Test
    public void shouldThrowApplyingExecEmailIsEmpty() {
        CaseDetails caseDetailsEmptyEmail =
                new CaseDetails(caseDataEmptyEmail, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            emailAddressExecutorsValidationRule.validate(caseDetailsEmptyEmail);
        })
                .isInstanceOf(BusinessValidationException.class);
    }


    @Test
    public void shouldThrowApplyingExecEmailIsInvalid1() {
        CaseDetails caseDetailsEmailInvalid =
                new CaseDetails(caseDataEmailInvalid1, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            emailAddressExecutorsValidationRule.validate(caseDetailsEmailInvalid);
        })
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("An applying exec email does not meet the criteria for case id 12345678987654321");
    }

    @Test
    public void shouldThrowApplyingExecEmailIsInvalid2() {
        CaseDetails caseDetailsEmailInvalid =
                new CaseDetails(caseDataEmailInvalid2, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            emailAddressExecutorsValidationRule.validate(caseDetailsEmailInvalid);
        })
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("An applying exec email does not meet the criteria for case id 12345678987654321");
    }

    @Test
    public void shouldThrowApplyingExecEmailIsInvalid3() {
        CaseDetails caseDetailsEmailInvalid =
                new CaseDetails(caseDataEmailInvalid3, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            emailAddressExecutorsValidationRule.validate(caseDetailsEmailInvalid);
        })
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("An applying exec email does not meet the criteria for case id 12345678987654321");
    }

    @Test
    public void shouldThrowApplyingExecEmailIsInvalid4() {
        CaseDetails caseDetailsEmailInvalid =
                new CaseDetails(caseDataEmailInvalid4, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            emailAddressExecutorsValidationRule.validate(caseDetailsEmailInvalid);
        })
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("An applying exec email does not meet the criteria for case id 12345678987654321");
    }

    @Test
    public void ApplyingExecEmailIsValid() {
        CaseDetails caseDetailsEmailValid =
                new CaseDetails(caseDataEmailValid, LAST_MODIFIED, CASE_ID);
        emailAddressExecutorsValidationRule.validate(caseDetailsEmailValid);
    }

    @Test
    public void ApplyingExecEmailIsNull() {
        CaseDetails caseDetailsEmailNull =
                new CaseDetails(caseDataEmptyEmailNull, LAST_MODIFIED, CASE_ID);
        emailAddressExecutorsValidationRule.validate(caseDetailsEmailNull);
    }
}
