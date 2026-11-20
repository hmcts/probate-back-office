package uk.gov.hmcts.probate.model.wa;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum TaskTypes {
    EXAMINE_DIGITAL_CASE_PROBATE("ExamineDigitalCaseProbate"),
    EXAMINE_DIGITAL_CASE_INTESTACY("ExamineDigitalCaseIntestacy"),
    EXAMINE_DIGITAL_CASE_ADMON_WILL("ExamineDigitalCaseAdmonWill"),
    EXAMINE_DIGITAL_CASE_ADCOLLIGENDA_BONA("ExamineDigitalCaseAdcolligendaBona"),
    EXAMINE_DIGITAL_CASE_PROBATE_READY_TO_ISSUE("ExamineDigitalCaseProbateReadyToIssue"),
    EXAMINE_DIGITAL_CASE_INTESTACY_READY_TO_ISSUE("ExamineDigitalCaseIntestacyReadyToIssue"),
    EXAMINE_DIGITAL_CASE_ADMON_WILL_READY_TO_ISSUE("ExamineDigitalCaseAdmonWillReadyToIssue"),
    EXAMINE_DIGITAL_CASE_ADCOLLIGENDA_BONA_READY_TO_ISSUE("ExamineDigitalCaseAdColligendaBonaReadyToIssue");

    final String value;

    TaskTypes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Optional<TaskTypes> fromValue(String value) {
        return Arrays.stream(values())
                .filter(taskType -> taskType.value.equals(value))
                .findFirst();
    }

}
