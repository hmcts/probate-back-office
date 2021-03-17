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

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

public class NotificationsExecutorsApplyingValidationRuleTest {

    @InjectMocks
    private NotificationExecutorsApplyingValidationRule notificationExecutorsApplyingValidationRule;

    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;

    private CaseData caseDataNotificationYesSingle;
    private CaseData caseDataNotificationNoSingle;
    private CaseData caseDataNotificationYesMultiple;
    private CaseData caseDataNotificationNoMultiple;

    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;
    private static final List<CollectionMember<ExecutorsApplyingNotification>> EXECEUTORS_APPLYING_NOTIFICATION_YES_SINGLE =
            Arrays.asList(
            new CollectionMember<>("id",
                    ExecutorsApplyingNotification.builder()
                            .name("Name")
                            .email("executor1@probate-test.com")
                            .notification(YES)
                            .build()));

    private static final List<CollectionMember<ExecutorsApplyingNotification>> EXECEUTORS_APPLYING_NOTIFICATION_NO_SINGLE =
            Arrays.asList(
            new CollectionMember<>("id",
                    ExecutorsApplyingNotification.builder()
                            .name("Name")
                            .email("executor1@probate-test.com")
                            .notification(NO)
                            .build()));

    private static final List<CollectionMember<ExecutorsApplyingNotification>> EXECEUTORS_APPLYING_NOTIFICATION_YES_MULTIPLE =
            Arrays.asList(
            new CollectionMember<>("id",
                    ExecutorsApplyingNotification.builder()
                            .name("Name")
                            .email("executor1@probate-test.com")
                            .notification(YES)
                            .build()),
            new CollectionMember<>("id",
                    ExecutorsApplyingNotification.builder()
                            .name("Name")
                            .email("executor1@probate-test.com")
                            .notification(YES)
                            .build()),
            new CollectionMember<>("id",
                    ExecutorsApplyingNotification.builder()
                            .name("Name")
                            .email("executor1@probate-test.com")
                            .notification(NO)
                            .build()));


    private static final List<CollectionMember<ExecutorsApplyingNotification>> EXECEUTORS_APPLYING_NOTIFICATION_NO_MULTIPLE = Arrays.asList(
            new CollectionMember<>("id",
                    ExecutorsApplyingNotification.builder()
                            .name("Name")
                            .email("executor1@probate-test.com")
                            .notification(NO)
                            .build()),
            new CollectionMember<>("id",
                    ExecutorsApplyingNotification.builder()
                            .name("Name")
                            .email("executor1@probate-test.com")
                            .notification(NO)
                            .build()),
            new CollectionMember<>("id",
                    ExecutorsApplyingNotification.builder()
                            .name("Name")
                            .email("executor1@probate-test.com")
                            .notification(NO)
                            .build()));

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        caseDataNotificationYesSingle = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .executorsApplyingNotifications(EXECEUTORS_APPLYING_NOTIFICATION_YES_SINGLE)
                .registryLocation("Bristol").build();

        caseDataNotificationNoSingle = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .executorsApplyingNotifications(EXECEUTORS_APPLYING_NOTIFICATION_NO_SINGLE)
                .registryLocation("Bristol").build();

        caseDataNotificationYesMultiple = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .executorsApplyingNotifications(EXECEUTORS_APPLYING_NOTIFICATION_YES_MULTIPLE)
                .registryLocation("Bristol").build();

        caseDataNotificationNoMultiple = CaseData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .executorsApplyingNotifications(EXECEUTORS_APPLYING_NOTIFICATION_NO_MULTIPLE)
                .registryLocation("Bristol").build();
    }

    @Test
    public void shouldThrowErrorNotificationIsNoSingle() {
        CaseDetails caseDetailsNotificationSingleNo =
                new CaseDetails(caseDataNotificationNoSingle, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            notificationExecutorsApplyingValidationRule.validate(caseDetailsNotificationSingleNo);
        })
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("No applicant selected to send notification for case id 12345678987654321");
    }

    @Test
    public void shouldThrowErrorNotificationIsNoMultiple() {
        CaseDetails caseDetailsNotificationMultipleNo =
                new CaseDetails(caseDataNotificationNoMultiple, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            notificationExecutorsApplyingValidationRule.validate(caseDetailsNotificationMultipleNo);
        })
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("No applicant selected to send notification for case id 12345678987654321");
    }

    @Test
    public void shouldNotThrowNotificationIsYesSingle() {
        CaseDetails caseDetailsNotificationYesSingle =
                new CaseDetails(caseDataNotificationYesSingle, LAST_MODIFIED, CASE_ID);


        notificationExecutorsApplyingValidationRule.validate(caseDetailsNotificationYesSingle);
    }

    @Test
    public void shouldNotThrowNotificationIsYesMultiple() {
        CaseDetails caseDetailsNotificationYesMultiple =
                new CaseDetails(caseDataNotificationYesMultiple, LAST_MODIFIED, CASE_ID);

        notificationExecutorsApplyingValidationRule.validate(caseDetailsNotificationYesMultiple);

    }
}
