package uk.gov.hmcts.probate.model.wa;

import lombok.Getter;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.model.DocumentCaseType.ADMON_WILL;
import static uk.gov.hmcts.probate.model.DocumentCaseType.AD_COLLIGENDA_BONA;
import static uk.gov.hmcts.probate.model.DocumentCaseType.GOP;
import static uk.gov.hmcts.probate.model.DocumentCaseType.INTESTACY;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_CASE_PRINTED;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_CASE_WORKER_ESCALATION;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_ICE_ADMON_WILL;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_ICE_AD_COLLIGENDA_BONA;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_ICE_INTESTACY;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_ICE_PROBATE;

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
    EXAMINE_SME_REFERRAL(STATE_BO_CASE_WORKER_ESCALATION,
            "ExamineSMEReferral", null, null,
            caseData -> !Boolean.TRUE.equals(caseData.getEvidenceHandled()));

    final String state;
    final String value;
    final String caseType;
    final EventId closingEventId;
    final Predicate<ResponseCaseData> createPredicate;

    TaskTypes(String state, String value, String caseType, EventId eventId) {
        this.state = state;
        this.value = value;
        this.caseType = caseType;
        this.closingEventId = eventId;
        this.createPredicate = x -> true;
    }

    TaskTypes(String state, String value, String caseType, EventId eventId,
              Predicate<ResponseCaseData> createPredicate) {
        this.state = state;
        this.value = value;
        this.caseType = caseType;
        this.closingEventId = eventId;
        this.createPredicate = createPredicate;
    }

    public static Set<TaskTypes> examineAwaitingDocIceTasks() {
        return Arrays.stream(values())
                .filter(taskType -> taskType.state.equals(STATE_CASE_PRINTED))
                .collect(Collectors.toSet());
    }

    public static Set<TaskTypes> examineReadyToIssueIceTasks() {
        return Arrays.stream(values())
                .filter(taskType -> taskType.state.equals(STATE_BO_READY_TO_ISSUE))
                .collect(Collectors.toSet());
    }

    public static Optional<TaskTypes> getAwaitingDocIceTask(String caseType) {
        return examineAwaitingDocIceTasks().stream()
                .filter(taskType -> taskType.caseType.equals(caseType))
                .findFirst();
    }

    public static Optional<TaskTypes> getReadyToIssueIceTask(String caseType) {
        return examineReadyToIssueIceTasks().stream()
                .filter(taskType -> taskType.caseType.equals(caseType))
                .findFirst();
    }


    public Optional<EventId> getOptionalClosingEventId() {
        return Optional.ofNullable(closingEventId);
    }
}
