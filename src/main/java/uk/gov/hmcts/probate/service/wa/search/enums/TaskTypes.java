package uk.gov.hmcts.probate.service.wa.search.enums;

public enum TaskTypes {
    EXAMINE_DIGITAL_CASE_PROBATE("ExamineDigitalCaseProbate"),
    EXAMINE_DIGITAL_CASE_INTESTACY("ExamineDigitalCaseIntestacy"),
    EXAMINE_DIGITAL_CASE_ADMON_WILL("ExamineDigitalCaseAdmonWill"),
    EXAMINE_DIGITAL_CASE_ADCOLLIGENDA_BONA("ExamineDigitalCaseAdcolligendaBona");

    final String value;

    TaskTypes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
