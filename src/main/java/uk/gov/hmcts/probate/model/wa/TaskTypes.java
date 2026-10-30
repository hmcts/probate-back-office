package uk.gov.hmcts.probate.model.wa;

import lombok.Getter;
import uk.gov.hmcts.probate.model.ccd.EventId;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_CASE_PRINTED;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_ICE_ADMON_WILL;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_ICE_AD_COLLIGENDA_BONA;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_ICE_INTESTACY;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_ICE_PROBATE;

@Getter
public enum TaskTypes {

    EXAMINE_DIGITAL_CASE_PROBATE(STATE_CASE_PRINTED, "ExamineDigitalCaseProbate", "gop",
            CLOSE_ICE_PROBATE),
    EXAMINE_DIGITAL_CASE_INTESTACY(STATE_CASE_PRINTED, "ExamineDigitalCaseIntestacy", "intestacy",
            CLOSE_ICE_INTESTACY),
    EXAMINE_DIGITAL_CASE_ADMON_WILL(STATE_CASE_PRINTED,"ExamineDigitalCaseAdmonWill", "admonWill",
            CLOSE_ICE_ADMON_WILL),
    EXAMINE_DIGITAL_CASE_ADCOLLIGENDA_BONA(STATE_CASE_PRINTED,"ExamineDigitalCaseAdcolligendaBona",
            "adColligendaBona", CLOSE_ICE_AD_COLLIGENDA_BONA),
    EXAMINE_DIGITAL_CASE_PROBATE_READY_TO_ISSUE(STATE_BO_READY_TO_ISSUE,"ExamineDigitalCaseProbateReadyToIssue",
            "gop", CLOSE_ICE_PROBATE),
    EXAMINE_DIGITAL_CASE_INTESTACY_READY_TO_ISSUE(STATE_BO_READY_TO_ISSUE,"ExamineDigitalCaseIntestacyReadyToIssue",
            "intestacy", CLOSE_ICE_INTESTACY),
    EXAMINE_DIGITAL_CASE_ADMON_WILL_READY_TO_ISSUE(STATE_BO_READY_TO_ISSUE,
            "ExamineDigitalCaseAdmonWillReadyToIssue", "admonWill", CLOSE_ICE_ADMON_WILL),
    EXAMINE_DIGITAL_CASE_ADCOLLIGENDA_BONA_READY_TO_ISSUE(STATE_BO_READY_TO_ISSUE,
            "ExamineDigitalCaseAdColligendaBonaReadyToIssue", "adColligendaBona",
            CLOSE_ICE_AD_COLLIGENDA_BONA);

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

    public Optional<EventId> getOptionalClosingEventId() {
        return Optional.ofNullable(closingEventId);
    }
}
