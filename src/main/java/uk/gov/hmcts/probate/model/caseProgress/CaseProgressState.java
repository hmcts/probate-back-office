package uk.gov.hmcts.probate.model;
import lombok.Getter;

@Getter
public enum CaseProgressState {
    DEFAULT(StateConstants.STATE_DESC_DEFAULT),
    CASE_ESCALATED(StateConstants.STATE_DESC_ESCALATED),
    CASE_STOPPED(StateConstants.STATE_DESC_CASE_STOPPED);

    private String displayText;

    CaseProgressState(String displayText) {
        this.displayText = displayText;
    }

    public static CaseProgressState MapCaseState(String caseState) {
        switch (caseState) {
            case StateConstants.STATE_BO_CASE_STOPPED:
                return CASE_STOPPED;

            case StateConstants.STATE_BO_CASE_STOPPED_REISSUE:
                return CASE_STOPPED;

            case StateConstants.STATE_BO_CASE_STOPPED_AWAIT_REDEC:
                return CASE_STOPPED;

            case StateConstants.STATE_BO_REGISTRAR_ESCALATION:
                return CASE_ESCALATED;

            default:
                return DEFAULT;
        }
    }
}
