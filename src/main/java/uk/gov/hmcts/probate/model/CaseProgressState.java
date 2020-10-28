package uk.gov.hmcts.probate.model;
import lombok.Getter;

@Getter
public enum CaseProgressState {
    DEFAULT(Constants.STATE_DESC_DEFAULT),
    CASE_ESCALATED(Constants.STATE_DESC_ESCALATED),
    CASE_STOPPED(Constants.STATE_DESC_CASE_STOPPED);

    private String displayText;

    CaseProgressState(String displayText) {
        this.displayText = displayText;
    }

    public static CaseProgressState MapCaseState(String caseState) {
        switch (caseState) {
            case Constants.STATE_BO_CASE_STOPPED:
                return CASE_STOPPED;

            case Constants.STATE_BO_CASE_STOPPED_REISSUE:
                return CASE_STOPPED;

            case Constants.STATE_BO_CASE_STOPPED_AWAIT_REDEC:
                return CASE_STOPPED;

            case Constants.STATE_BO_REGISTRAR_ESCALATION:
                return CASE_ESCALATED;

            default:
                return DEFAULT;
        }
    }

    public static class Constants {
        public static final String STATE_BO_CASE_STOPPED = "BOCaseStopped";
        public static final String STATE_BO_CASE_STOPPED_REISSUE = "BOCaseStoppedReissue";
        public static final String STATE_BO_CASE_STOPPED_AWAIT_REDEC = "BOCaseStoppedAwaitRedec";
        public static final String STATE_BO_REGISTRAR_ESCALATION = "BORegistrarEscalation";

        public static final String STATE_DESC_DEFAULT = "Default";
        public static final String STATE_DESC_ESCALATED = "Case escalated to the Registrar";
        public static final String STATE_DESC_CASE_STOPPED = "Case stopped";

        private Constants() {
        }
    }
}
