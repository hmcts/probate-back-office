package uk.gov.hmcts.probate.model;
import lombok.Getter;

@Getter
public enum CaseProgressState {
    IN_PROGRESS(Constants.STATE_DESC_IN_PROGRESS), // name is not used for this state
    CASE_ESCALATED(Constants.STATE_DESC_ESCALATED), // name here is displayed in the inset in Case Progress html
    CASE_STOPPED( Constants.STATE_DESC_CASE_STOPPED); // name here is displayed in the inset in Case Progress html

    private String name;

    CaseProgressState(String name) {
        this.name = name;
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
                return IN_PROGRESS;
        }
    }

    public static class Constants {
        public static final String STATE_BO_CASE_STOPPED = "BOCaseStopped";
        public static final String STATE_BO_CASE_STOPPED_REISSUE = "BOCaseStoppedReissue";
        public static final String STATE_BO_CASE_STOPPED_AWAIT_REDEC = "BOCaseStoppedAwaitRedec";
        public static final String STATE_BO_REGISTRAR_ESCALATION = "BORegistrarEscalation";

        public static final String STATE_DESC_IN_PROGRESS = "In progress";
        public static final String STATE_DESC_ESCALATED = "Case escalated to the Registrar";
        public static final String STATE_DESC_CASE_STOPPED = "Case stopped";

        private Constants() {
        }
    }
}
