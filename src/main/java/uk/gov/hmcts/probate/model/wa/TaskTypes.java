package uk.gov.hmcts.probate.model.wa;

import lombok.Getter;
import uk.gov.hmcts.probate.model.ccd.EventId;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_AWAITING_DOCUMENTATION_ICE_ADMON_WILL;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_AWAITING_DOCUMENTATION_ICE_AD_COLLIGENDA_BONA;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_AWAITING_DOCUMENTATION_ICE_INTESTACY;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_AWAITING_DOCUMENTATION_ICE_PROBATE;

@Getter
public enum TaskTypes {
    EXAMINE_DIGITAL_CASE_PROBATE("ExamineDigitalCaseProbate", CLOSE_AWAITING_DOCUMENTATION_ICE_PROBATE),
    EXAMINE_DIGITAL_CASE_INTESTACY("ExamineDigitalCaseIntestacy", CLOSE_AWAITING_DOCUMENTATION_ICE_INTESTACY),
    EXAMINE_DIGITAL_CASE_ADMON_WILL("ExamineDigitalCaseAdmonWill", CLOSE_AWAITING_DOCUMENTATION_ICE_ADMON_WILL),
    EXAMINE_DIGITAL_CASE_ADCOLLIGENDA_BONA("ExamineDigitalCaseAdcolligendaBona",
            CLOSE_AWAITING_DOCUMENTATION_ICE_AD_COLLIGENDA_BONA),
    EXAMINE_DIGITAL_CASE_PROBATE_READY_TO_ISSUE("ExamineDigitalCaseProbateReadyToIssue", null),
    EXAMINE_DIGITAL_CASE_INTESTACY_READY_TO_ISSUE("ExamineDigitalCaseIntestacyReadyToIssue", null),
    EXAMINE_DIGITAL_CASE_ADMON_WILL_READY_TO_ISSUE("ExamineDigitalCaseAdmonWillReadyToIssue", null),
    EXAMINE_DIGITAL_CASE_ADCOLLIGENDA_BONA_READY_TO_ISSUE("ExamineDigitalCaseAdColligendaBonaReadyToIssue", null);

    final String value;
    final EventId closingEventId;

    TaskTypes(String value, EventId eventId) {
        this.value = value;
        this.closingEventId = eventId;
    }

    public static Optional<TaskTypes> fromValue(String value) {
        return Arrays.stream(values())
                .filter(taskType -> taskType.value.equals(value))
                .findFirst();
    }

    public static Set<TaskTypes> examineAwaitingDocIceTasks() {
        return Set.of(EXAMINE_DIGITAL_CASE_PROBATE,
                EXAMINE_DIGITAL_CASE_INTESTACY,
                EXAMINE_DIGITAL_CASE_ADMON_WILL,
                EXAMINE_DIGITAL_CASE_ADCOLLIGENDA_BONA);
    }

    public static Set<TaskTypes> examineReadyToIssueIceTasks() {
        return Set.of(EXAMINE_DIGITAL_CASE_PROBATE_READY_TO_ISSUE,
                EXAMINE_DIGITAL_CASE_INTESTACY_READY_TO_ISSUE,
                EXAMINE_DIGITAL_CASE_ADMON_WILL_READY_TO_ISSUE,
                EXAMINE_DIGITAL_CASE_ADCOLLIGENDA_BONA_READY_TO_ISSUE);
    }

    public Optional<EventId> getOptionalClosingEventId() {
        return Optional.ofNullable(closingEventId);
    }
}
