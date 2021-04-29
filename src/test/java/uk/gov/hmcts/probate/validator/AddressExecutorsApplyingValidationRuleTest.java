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
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.Arrays;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.YES;

public class AddressExecutorsApplyingValidationRuleTest {

    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;
    private static final List<CollectionMember<ExecutorsApplyingNotification>> EXECEUTORS_APPLYING_NOTIFICATION_EMPTY =
        Arrays.asList(
            new CollectionMember<>("id",
                ExecutorsApplyingNotification.builder()
                    .name("Name")
                    .address(SolsAddress.builder().addressLine1("").postCode("").build())
                    .notification(YES)
                    .build()));
    private static final List<CollectionMember<ExecutorsApplyingNotification>> EXECEUTORS_APPLYING_NOTIFICATION_NULL =
        Arrays.asList(
            new CollectionMember<>("id",
                ExecutorsApplyingNotification.builder()
                    .name("Name")
                    .address(SolsAddress.builder().build())
                    .notification(YES)
                    .build()));
    private static final List<CollectionMember<ExecutorsApplyingNotification>> EXECEUTORS_APPLYING_NOTIFICATION =
        Arrays.asList(
            new CollectionMember<>("id",
                ExecutorsApplyingNotification.builder()
                    .name("Name")
                    .address(SolsAddress.builder().addressLine1("123").postCode("AB12CD").build())
                    .notification(YES)
                    .build()));
    @InjectMocks
    private AddressExecutorsApplyingValidationRule addressExecutorsApplyingValidationRule;
    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;
    private CaseData caseDataEmpty;
    private CaseData caseDataNotEmpty;
    private CaseData caseDataNotEmptySolicitor;
    private CaseData caseDataNull;

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
    }

    @Test
    public void shouldThrowApplyingExecAddressIsEmpty() {
        CaseDetails caseDetailsEmpty =
            new CaseDetails(caseDataEmpty, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            addressExecutorsApplyingValidationRule.validate(caseDetailsEmpty);
        })
            .isInstanceOf(BusinessValidationException.class)
            .hasMessage(
                "An applying exec address has empty value for Address line 1 or postcode with case id "
                    + "12345678987654321");
    }

    @Test
    public void shouldThrowApplyingExecAddressIsNull() {
        CaseDetails caseDetailsNull =
            new CaseDetails(caseDataNull, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            addressExecutorsApplyingValidationRule.validate(caseDetailsNull);
        })
            .isInstanceOf(BusinessValidationException.class)
            .hasMessage(
                "An applying exec address has null value for Address line 1 or postcode with case id "
                    + "12345678987654321");
    }

    @Test
    public void shouldNotThrowWhenApplyingExecEmailIsNotEmpty() {
        CaseDetails caseDetailsNotEmpty =
            new CaseDetails(caseDataNotEmpty, LAST_MODIFIED, CASE_ID);

        addressExecutorsApplyingValidationRule.validate(caseDetailsNotEmpty);
    }

    @Test
    public void shouldNotThrowWhenApplyingExecEmailIsNotEmptyForSolicitor() {
        CaseDetails caseDetailsNotEmptySolicitor =
            new CaseDetails(caseDataNotEmptySolicitor, LAST_MODIFIED, CASE_ID);

        addressExecutorsApplyingValidationRule.validate(caseDetailsNotEmptySolicitor);
    }
}

