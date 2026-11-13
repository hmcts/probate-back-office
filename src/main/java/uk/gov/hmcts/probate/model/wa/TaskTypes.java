package uk.gov.hmcts.probate.model.wa;

import lombok.Getter;
import uk.gov.hmcts.probate.model.ccd.EventId;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static uk.gov.hmcts.probate.model.DocumentCaseType.ADMON_WILL;
import static uk.gov.hmcts.probate.model.DocumentCaseType.AD_COLLIGENDA_BONA;
import static uk.gov.hmcts.probate.model.DocumentCaseType.GOP;
import static uk.gov.hmcts.probate.model.DocumentCaseType.INTESTACY;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_CASE_STOPPED;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_REDEC_NOTIFICATION_SENT;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_CASE_PRINTED;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_CODICIL_MIS;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_DE_BONIS_NON;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_DOUBLE_PROBATE;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_FIAT_WILL;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_FOREIGN_DOMICILE;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_FOREIGN_WILL;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_HORIZON_SCHEME;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_IBCA;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_IBIS;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_CASE_WORKER_ESCALATION;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_ICE_ADMON_WILL;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_ICE_AD_COLLIGENDA_BONA;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_ICE_INTESTACY;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_ICE_PROBATE;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_INCAPACITY_RULE_35;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_LEADING_FOLLOWING_GRANTS;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_LITERARY_ESTATE;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_LOST_WILL;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_MINORITY_INTEREST;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_POA;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_RECTIFY_WILL_CODICIL;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_RESEAL_FOREIGN_GRANT;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_SECTION_116;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_TRUST_CORPORATION;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_WILL_CODICIL_NOTATED;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_WINDRUSH_SCHEME;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_WITNESS_INTERVIEW;

@Getter
public enum TaskTypes {

    EXAMINE_DIGITAL_CASE_PROBATE(STATE_CASE_PRINTED, "ExamineDigitalCaseProbate", GOP.getCaseType(),
            CLOSE_ICE_PROBATE),
    EXAMINE_DIGITAL_CASE_INTESTACY(STATE_CASE_PRINTED, "ExamineDigitalCaseIntestacy", INTESTACY.getCaseType(),
            CLOSE_ICE_INTESTACY),
    EXAMINE_DIGITAL_CASE_ADMON_WILL(STATE_CASE_PRINTED,"ExamineDigitalCaseAdmonWill", ADMON_WILL.getCaseType(),
            CLOSE_ICE_ADMON_WILL),
    EXAMINE_DIGITAL_CASE_ADCOLLIGENDA_BONA(STATE_CASE_PRINTED,"ExamineDigitalCaseAdcolligendaBona",
            AD_COLLIGENDA_BONA.getCaseType(), CLOSE_ICE_AD_COLLIGENDA_BONA),
    EXAMINE_DIGITAL_CASE_PROBATE_READY_TO_ISSUE(STATE_BO_READY_TO_ISSUE,"ExamineDigitalCaseProbateReadyToIssue",
            GOP.getCaseType(), CLOSE_ICE_PROBATE),
    EXAMINE_DIGITAL_CASE_INTESTACY_READY_TO_ISSUE(STATE_BO_READY_TO_ISSUE,"ExamineDigitalCaseIntestacyReadyToIssue",
            INTESTACY.getCaseType(), CLOSE_ICE_INTESTACY),
    EXAMINE_DIGITAL_CASE_ADMON_WILL_READY_TO_ISSUE(STATE_BO_READY_TO_ISSUE,
            "ExamineDigitalCaseAdmonWillReadyToIssue", ADMON_WILL.getCaseType(), CLOSE_ICE_ADMON_WILL),
    EXAMINE_DIGITAL_CASE_ADCOLLIGENDA_BONA_READY_TO_ISSUE(STATE_BO_READY_TO_ISSUE,
            "ExamineDigitalCaseAdColligendaBonaReadyToIssue", AD_COLLIGENDA_BONA.getCaseType(),
            CLOSE_ICE_AD_COLLIGENDA_BONA),
    EXAMINE_DE_BONIS_NON(STATE_BO_READY_TO_ISSUE,"ExamineDeBonisNon",
            null, CLOSE_DE_BONIS_NON),
    EXAMINE_DE_BONIS_NON_CASE_PRINTED(STATE_CASE_PRINTED,"ExamineDeBonisNonCasePrinted",
            null, CLOSE_DE_BONIS_NON),
    EXAMINE_DOUBLE_PROBATE(STATE_BO_READY_TO_ISSUE,"ExamineDoubleProbate",
            null, CLOSE_DOUBLE_PROBATE),
    EXAMINE_DOUBLE_PROBATE_CASE_PRINTED(STATE_CASE_PRINTED,"ExamineDoubleProbateCasePrinted",
            null, CLOSE_DOUBLE_PROBATE),
    EXAMINE_INCAPACITY_RULE_35(STATE_BO_READY_TO_ISSUE,"ExamineIncapacityRule35",
            null, CLOSE_INCAPACITY_RULE_35),
    EXAMINE_INCAPACITY_RULE_35_CASE_PRINTED(STATE_CASE_PRINTED,"ExamineIncapacityRule35CasePrinted",
            null, CLOSE_INCAPACITY_RULE_35),
    EXAMINE_LEADING_FOLLOWING_GRANTS(STATE_BO_READY_TO_ISSUE,"ExamineLeadingFollowingGrants",
            null, CLOSE_LEADING_FOLLOWING_GRANTS),
    EXAMINE_LEADING_FOLLOWING_GRANTS_CASE_PRINTED(STATE_CASE_PRINTED,"ExamineLeadingFollowingGrantsCasePrinted",
            null, CLOSE_LEADING_FOLLOWING_GRANTS),
    EXAMINE_RECTIFY_WILL_CODICIL(STATE_BO_READY_TO_ISSUE,"ExamineRectifyWillCodicil",
            null, CLOSE_RECTIFY_WILL_CODICIL),
    EXAMINE_RECTIFY_WILL_CODICIL_CASE_PRINTED(STATE_CASE_PRINTED,"ExamineRectifyWillCodicilCasePrinted",
            null, CLOSE_RECTIFY_WILL_CODICIL),
    EXAMINE_IBCA(STATE_BO_READY_TO_ISSUE,"ExamineIBCA",
            null, CLOSE_IBCA),
    EXAMINE_IBCA_CASE_PRINTED(STATE_CASE_PRINTED,"ExamineIBCACasePrinted",
            null, CLOSE_IBCA),
    EXAMINE_SECTION_116(STATE_BO_READY_TO_ISSUE,"ExamineSection116",
            null, CLOSE_SECTION_116),
    EXAMINE_SECTION_116_CASE_PRINTED(STATE_CASE_PRINTED,"ExamineSection116CasePrinted",
            null, CLOSE_SECTION_116),
    EXAMINE_POA(STATE_BO_READY_TO_ISSUE,"ExaminePOA",
            null, CLOSE_POA),
    EXAMINE_POA_CASE_PRINTED(STATE_CASE_PRINTED,"ExaminePOACasePrinted",
            null, CLOSE_POA),
    EXAMINE_RESEAL_FOREIGN_GRANT(STATE_BO_READY_TO_ISSUE,"ExamineResealForeignGrant",
            null, CLOSE_RESEAL_FOREIGN_GRANT),
    EXAMINE_RESEAL_FOREIGN_GRANT_CASE_PRINTED(STATE_CASE_PRINTED,"ExamineResealForeignGrantCasePrinted",
            null, CLOSE_RESEAL_FOREIGN_GRANT),
    EXAMINE_IBIS(STATE_BO_READY_TO_ISSUE,"ExamineIBIS",
            null, CLOSE_IBIS),
    EXAMINE_IBIS_CASE_PRINTED(STATE_CASE_PRINTED,"ExamineIBISCasePrinted",
            null, CLOSE_IBIS),
    EXAMINE_LITERARY_ESTATE(STATE_BO_READY_TO_ISSUE,"ExamineLiteraryEstate",
            null, CLOSE_LITERARY_ESTATE),
    EXAMINE_LITERARY_ESTATE_CASE_PRINTED(STATE_CASE_PRINTED,"ExamineLiteraryEstateCasePrinted",
            null, CLOSE_LITERARY_ESTATE),
    EXAMINE_LOST_WILL(STATE_BO_READY_TO_ISSUE,"ExamineLostWill",
            null, CLOSE_LOST_WILL),
    EXAMINE_LOST_WILL_CASE_PRINTED(STATE_CASE_PRINTED,"ExamineLostWillCasePrinted",
            null, CLOSE_LOST_WILL),
    EXAMINE_TRUST_CORPORATION(STATE_BO_READY_TO_ISSUE,"ExamineTrustCorporation",
            null, CLOSE_TRUST_CORPORATION),
    EXAMINE_TRUST_CORPORATION_CASE_PRINTED(STATE_CASE_PRINTED,"ExamineTrustCorporationCasePrinted",
            null, CLOSE_TRUST_CORPORATION),
    EXAMINE_HORIZON_SCHEME(STATE_BO_READY_TO_ISSUE,"ExamineHorizonScheme",
            null, CLOSE_HORIZON_SCHEME),
    EXAMINE_HORIZON_SCHEME_CASE_PRINTED(STATE_CASE_PRINTED,"ExamineHorizonSchemeCasePrinted",
            null, CLOSE_HORIZON_SCHEME),
    EXAMINE_MINORITY_INTEREST(STATE_BO_READY_TO_ISSUE,"ExamineMinorityInterest",
            null, CLOSE_MINORITY_INTEREST),
    EXAMINE_MINORITY_INTEREST_CASE_PRINTED(STATE_CASE_PRINTED,"ExamineMinorityInterestCasePrinted",
            null, CLOSE_MINORITY_INTEREST),
    EXAMINE_WILL_CODICIL_NOTATED(STATE_BO_READY_TO_ISSUE,"ExamineWillCodicilNotated",
            null, CLOSE_WILL_CODICIL_NOTATED),
    EXAMINE_WILL_CODICIL_NOTATED_CASE_PRINTED(STATE_CASE_PRINTED,"ExamineWillCodicilNotatedCasePrinted",
            null, CLOSE_WILL_CODICIL_NOTATED),
    EXAMINE_WITNESS_INTERVIEW(STATE_BO_READY_TO_ISSUE,"ExamineWitnessInterview",
            null, CLOSE_WITNESS_INTERVIEW),
    EXAMINE_WITNESS_INTERVIEW_CASE_PRINTED(STATE_CASE_PRINTED,"ExamineWitnessInterviewCasePrinted",
            null, CLOSE_WITNESS_INTERVIEW),
    EXAMINE_FIAT_WILL(STATE_BO_READY_TO_ISSUE,"ExamineFiatWill",
            null, CLOSE_FIAT_WILL),
    EXAMINE_FIAT_WILL_CASE_PRINTED(STATE_CASE_PRINTED,"ExamineFiatWillCasePrinted",
            null, CLOSE_FIAT_WILL),
    EXAMINE_FOREIGN_DOMICILE(STATE_BO_READY_TO_ISSUE,"ExamineForeignDomicile",
            null, CLOSE_FOREIGN_DOMICILE),
    EXAMINE_FOREIGN_DOMICILE_CASE_PRINTED(STATE_CASE_PRINTED,"ExamineForeignDomicileCasePrinted",
            null, CLOSE_FOREIGN_DOMICILE),
    EXAMINE_FOREIGN_WILL(STATE_BO_READY_TO_ISSUE,"ExamineForeignWill",
            null, CLOSE_FOREIGN_WILL),
    EXAMINE_FOREIGN_WILL_CASE_PRINTED(STATE_CASE_PRINTED,"ExamineForeignWillCasePrinted",
            null, CLOSE_FOREIGN_WILL),
    EXAMINE_WINDRUSH_SCHEME(STATE_BO_READY_TO_ISSUE,"ExamineWindrushScheme",
            null, CLOSE_WINDRUSH_SCHEME),
    EXAMINE_WINDRUSH_SCHEME_CASE_PRINTED(STATE_CASE_PRINTED,"ExamineWindrushSchemeCasePrinted",
            null, CLOSE_WINDRUSH_SCHEME),
    EXAMINE_CODICIL_MIS(STATE_BO_READY_TO_ISSUE,"ExamineCodicilMis",
            null, CLOSE_CODICIL_MIS),
    EXAMINE_CODICIL_MIS_CASE_PRINTED(STATE_CASE_PRINTED,"ExamineCodicilMisCasePrinted",
            null, CLOSE_CODICIL_MIS),
    EXAMINE_SME_REFERRAL(STATE_BO_CASE_WORKER_ESCALATION,
            "ExamineSMEReferral", null, null),
    RESOLVE_STOPPED_CASE(STATE_BO_CASE_STOPPED,
            "ResolveStoppedCase", null, null),
    RESOLVE_REDECLARATION(STATE_BO_REDEC_NOTIFICATION_SENT,
            "ResolveRedeclaration", null, null);

    final String state;
    final String value;
    final String caseType;
    final EventId closingEventId;

    TaskTypes(String state, String value, String caseType, EventId eventId) {
        this.state = state;
        this.value = value;
        this.caseType = caseType;
        this.closingEventId = eventId;
    }

    public static Set<TaskTypes> examineAwaitingDocTasks() {
        return Arrays.stream(values())
                .filter(taskType -> taskType.state.equals(STATE_CASE_PRINTED))
                .collect(Collectors.toSet());
    }

    public static Map<String, TaskTypes> getStateTasks(String state) {
        return Arrays.stream(values())
                .filter(taskType -> taskType.state.equals(state) && taskType.closingEventId != null)
                .collect(toMap(taskType ->
                        taskType.getValue().toUpperCase(),
                        taskType -> taskType));
    }

    public static Set<TaskTypes> examineReadyToIssueTasks() {
        return Arrays.stream(values())
                .filter(taskType -> taskType.state.equals(STATE_BO_READY_TO_ISSUE))
                .collect(Collectors.toSet());
    }

    public static Optional<TaskTypes> getAwaitingDocIceTask(String caseType) {
        return examineAwaitingDocTasks().stream()
                .filter(taskType -> taskType.caseType != null && taskType.caseType.equals(caseType))
                .findFirst();
    }

    public static Optional<TaskTypes> getReadyToIssueIceTask(String caseType) {
        return examineReadyToIssueTasks().stream()
                .filter(taskType -> taskType.caseType != null && taskType.caseType.equals(caseType))
                .findFirst();
    }



    public Optional<EventId> getOptionalClosingEventId() {
        return Optional.ofNullable(closingEventId);
    }
}
