package uk.gov.hmcts.probate.validator;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ExecutorsApplyingNotification;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.Arrays;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.util.EmailAddressUtils.INVALID_EMAIL_ADDRESSES;

public class EmailAddressExecutorsApplyingValidationRuleTest {

    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;
    private static final List<CollectionMember<ExecutorsApplyingNotification>> EXECEUTORS_APPLYING_NOTIFICATION_EMPTY =
        Arrays.asList(
            new CollectionMember<>("id",
                ExecutorsApplyingNotification.builder()
                    .name("Name")
                    .email("")
                    .notification(YES)
                    .build()));
    private static final List<CollectionMember<ExecutorsApplyingNotification>> EXECEUTORS_APPLYING_NOTIFICATION_NULL =
        Arrays.asList(
            new CollectionMember<>("id",
                ExecutorsApplyingNotification.builder()
                    .name("Name")
                    .email(null)
                    .notification(YES)
                    .build()));
    private static final List<CollectionMember<ExecutorsApplyingNotification>> EXECEUTORS_APPLYING_NOTIFICATION =
        Arrays.asList(
            new CollectionMember<>("id",
                ExecutorsApplyingNotification.builder()
                    .name("Name")
                    .email("executor1@probate-test.com")
                    .notification(YES)
                    .build()));

    private static final List<CollectionMember<ExecutorsApplyingNotification>>
        EXECEUTORS_APPLYING_NOTIFICATION_INVALID = Arrays.asList(
            new CollectionMember<>("id",
                ExecutorsApplyingNotification.builder()
                    .name("Name")
                    .email(INVALID_EMAIL_ADDRESSES[0])
                    .notification(YES)
                    .build()));
    @InjectMocks
    private EmailAddressExecutorsApplyingValidationRule emailAddressExecutorsApplyingValidationRule;
    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;
    private CaseData caseDataEmpty;
    private CaseData caseDataNotEmpty;
    private CaseData caseDataNotEmptySolicitor;
    private CaseData caseDataNull;
    private CaseData caseDataInvalidEmailPersonal;
    private CaseData caseDataInvalidEmailSolicitor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        caseDataEmpty = CaseData.builder()
            .applicationType(ApplicationType.PERSONAL)
            .executorsApplyingNotifications(EXECEUTORS_APPLYING_NOTIFICATION_EMPTY)
            .registryLocation("Bristol").build();

        caseDataNotEmpty = CaseData.builder()
            .applicationType(ApplicationType.PERSONAL)
            .executorsApplyingNotifications(EXECEUTORS_APPLYING_NOTIFICATION)
            .registryLocation("Bristol").build();

        caseDataNull = CaseData.builder()
            .applicationType(ApplicationType.PERSONAL)
            .executorsApplyingNotifications(EXECEUTORS_APPLYING_NOTIFICATION_NULL)
            .registryLocation("Bristol").build();

        caseDataNotEmptySolicitor = CaseData.builder()
            .applicationType(ApplicationType.SOLICITOR)
            .executorsApplyingNotifications(EXECEUTORS_APPLYING_NOTIFICATION)
            .registryLocation("Bristol").build();

        caseDataInvalidEmailPersonal = CaseData.builder()
            .applicationType(ApplicationType.PERSONAL)
            .executorsApplyingNotifications(EXECEUTORS_APPLYING_NOTIFICATION_INVALID)
            .registryLocation("Bristol").build();

        caseDataInvalidEmailSolicitor = CaseData.builder()
            .applicationType(ApplicationType.SOLICITOR)
            .executorsApplyingNotifications(EXECEUTORS_APPLYING_NOTIFICATION_INVALID)
            .registryLocation("Bristol").build();
    }

    @Test
    public void shouldThrowApplyingExecEmailIsEmpty() {
        CaseDetails caseDetailsEmpty =
            new CaseDetails(caseDataEmpty, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            emailAddressExecutorsApplyingValidationRule.validate(caseDetailsEmpty);
        })
            .isInstanceOf(BusinessValidationException.class)
            .hasMessage("An applying exec email is empty for case id 12345678987654321");
    }

    @Test
    public void shouldThrowApplyingExecEmailIsNull() {
        CaseDetails caseDetailsNull =
            new CaseDetails(caseDataNull, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            emailAddressExecutorsApplyingValidationRule.validate(caseDetailsNull);
        })
            .isInstanceOf(BusinessValidationException.class)
            .hasMessage("An applying exec email is null for case id 12345678987654321");
    }

    @Test
    public void shouldNotThrowWhenApplyingExecEmailIsNotEmpty() {
        CaseDetails caseDetailsNotEmpty =
            new CaseDetails(caseDataNotEmpty, LAST_MODIFIED, CASE_ID);

        emailAddressExecutorsApplyingValidationRule.validate(caseDetailsNotEmpty);
    }

    @Test
    public void shouldNotThrowWhenApplyingExecEmailIsNotEmptyForSolicitor() {
        CaseDetails caseDetailsNotEmptySolicitor =
            new CaseDetails(caseDataNotEmptySolicitor, LAST_MODIFIED, CASE_ID);

        emailAddressExecutorsApplyingValidationRule.validate(caseDetailsNotEmptySolicitor);
    }

    @Test
    public void shouldThrowWhenApplyingExecEmailIsInvalid() {
        CaseDetails caseDetailsInvalidEmailPersonal =
            new CaseDetails(caseDataInvalidEmailPersonal, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            emailAddressExecutorsApplyingValidationRule.validate(caseDetailsInvalidEmailPersonal);
        })
            .isInstanceOf(BusinessValidationException.class)
            .hasMessage("An applying exec email: plainaddress, is invalid for case id 12345678987654321");

        CaseDetails caseDetailsInvalidSolicitorPersonal =
            new CaseDetails(caseDataInvalidEmailSolicitor, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            emailAddressExecutorsApplyingValidationRule.validate(caseDetailsInvalidSolicitorPersonal);
        })
            .isInstanceOf(BusinessValidationException.class)
            .hasMessage("An applying exec email: plainaddress, is invalid for case id 12345678987654321");
    }
}
