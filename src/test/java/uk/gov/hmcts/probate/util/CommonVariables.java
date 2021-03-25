package uk.gov.hmcts.probate.util;

import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplyingPowerReserved;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorPartners;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorTrustCorps;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;

public class CommonVariables {

    public static final String YES = "Yes";
    public static final String NO = "No";

    public static final String SOLICITOR_SOT_FULLNAME = "Solicitor Forename Solicitor Surname";
    public static final String SOLICITOR_SOT_FORENAME = "Solicitor Forename";
    public static final String SOLICITOR_SOT_SURNAME = "Solicitor Surname";
    public static final String SOLICITOR_FIRM_EMAIL = "solicitor@probate-test.com";
    public static final String SOLICITOR_FIRM_PHONE = "0123456789";
    public static final String SOLICITOR_FIRM_NAME = "Sol Firm Name";
    public static final String SOLICITOR_FIRM_LINE1 = "Sols Add Line 1";
    public static final SolsAddress SOLICITOR_ADDRESS =  mock(SolsAddress.class);
    public static final String SOLICITOR_NOT_APPLYING_REASON = "Not applying";
    public static final String SOLICITOR_ID = "solicitor";

    public static final String PRIMARY_APPLICANT_FORENAME = "Primary app forename";
    public static final String PRIMARY_APPLICANT_SURNAME = "Primary app surname";
    public static final String PRIMARY_EXEC_ALIAS_NAMES = "Alias names";

    public static final String EXEC_FIRST_NAME = "ExFName";
    public static final String EXEC_SURNAME = "EXSName";
    public static final String EXEC_NAME = "ExFName EXSName";
    public static final String EXEC_TRUST_CORP_POS = "Trustcorp Position";
    public static final String EXEC_NAME_DIFF = "Ex name difference comment";
    public static final String EXEC_WILL_NAME = "Ex will name";
    public static final String EXEC_OTHER_NAMES = EXEC_WILL_NAME;
    public static final String EXEC_OTHER_NAMES_REASON = "Marriage";
    public static final String EXEC_PHONE = "010101010101";
    public static final String EXEC_EMAIL = "executor1@probate-test.com";
    public static final String EXEC_NOTIFIED = YES;
    public static final SolsAddress EXEC_ADDRESS = mock(SolsAddress.class);
    public static final String EXEC_ID = "exec";
    public static final String EXECUTOR_NOT_APPLYING_REASON = "Reason";
    public static final String POWER_RESERVED = "PowerReserved";

    public static final String GOP = "WillLeft";

    public static final String ALIAS_FORENAME = "AliasFN";
    public static final String ALIAS_SURNAME = "AliasSN";
    public static final String SOLS_ALIAS_NAME = "AliasFN AliasSN";

    public static final String STOP_REASON = "Some reason";

    public static final AdditionalExecutorApplying EXECUTOR_APPLYING = AdditionalExecutorApplying.builder()
            .applyingExecutorName(EXEC_NAME)
            .applyingExecutorFirstName(EXEC_FIRST_NAME)
            .applyingExecutorLastName(EXEC_SURNAME)
            .applyingExecutorPhoneNumber(EXEC_PHONE)
            .applyingExecutorEmail(EXEC_EMAIL)
            .applyingExecutorAddress(EXEC_ADDRESS)
            .applyingExecutorTrustCorpPosition(EXEC_TRUST_CORP_POS)
            .build();

    public static final AdditionalExecutorNotApplying EXECUTOR_NOT_APPLYING = AdditionalExecutorNotApplying.builder()
            .notApplyingExecutorName(EXEC_NAME)
            .notApplyingExecutorReason(EXECUTOR_NOT_APPLYING_REASON)
            .notApplyingExecutorDispenseWithNotice(YES)
            .notApplyingExecutorDispenseWithNoticeLeaveGiven(YES)
            .notApplyingExecutorDispenseWithNoticeLeaveGivenDate(LocalDate.of(2021, 1, 1))
            .build();

    public static final CollectionMember<AdditionalExecutor> SOLS_EXEC_APPLYING = new CollectionMember(EXEC_ID,
            AdditionalExecutor.builder()
                    .additionalExecForenames(EXEC_FIRST_NAME)
                    .additionalExecLastname(EXEC_SURNAME)
                    .additionalExecAddress(EXEC_ADDRESS)
                    .additionalApplying(YES)
                    .additionalExecAliasNameOnWill(EXEC_WILL_NAME)
                    .build());

    public static final CollectionMember<AdditionalExecutor> SOLS_EXEC_NOT_APPLYING = new CollectionMember(EXEC_ID,
            AdditionalExecutor.builder()
                    .additionalExecForenames(EXEC_FIRST_NAME)
                    .additionalExecLastname(EXEC_SURNAME)
                    .additionalApplying(NO)
                    .additionalExecAliasNameOnWill(EXEC_WILL_NAME)
                    .additionalExecReasonNotApplying(EXECUTOR_NOT_APPLYING_REASON)
                    .build());

    public static final CollectionMember<AdditionalExecutorTrustCorps> TRUST_CORP_EXEC = new CollectionMember(EXEC_ID,
            AdditionalExecutorTrustCorps.builder()
                    .additionalExecForenames(EXEC_FIRST_NAME)
                    .additionalExecLastname(EXEC_SURNAME)
                    .additionalExecutorTrustCorpPosition(EXEC_TRUST_CORP_POS)
                    .build());

    public static final CollectionMember<AdditionalExecutorPartners> PARTNER_EXEC = new CollectionMember(EXEC_ID,
            AdditionalExecutorPartners.builder()
                    .additionalExecForenames(EXEC_FIRST_NAME)
                    .additionalExecLastname(EXEC_SURNAME)
                    .additionalExecAddress(EXEC_ADDRESS)
            .build());

    public static final CollectionMember<AdditionalExecutorNotApplyingPowerReserved> DISPENSE_WITH_NOTICE_EXEC =
            new CollectionMember(EXEC_ID, AdditionalExecutorNotApplyingPowerReserved.builder()
                    .notApplyingExecutorName(EXEC_NAME)
                    .build()
            );

    // added to prevent having a public constructor
    private CommonVariables() {
    }

}
