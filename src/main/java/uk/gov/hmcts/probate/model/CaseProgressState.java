package uk.gov.hmcts.probate.model;
import lombok.Getter;

@Getter
public enum CaseProgressState {
    IN_PROGRESS("In progress"), // name is not used for this state
    CASE_ESCALATED("Case escalated to the Registrar"), // name here is displayed in the inset in Case Progress html
    CASE_STOPPED( "Case stopped"); // name here is displayed in the inset in Case Progress html

    private String name;

    CaseProgressState(String name) {
        this.name = name;
    }

    public static CaseProgressState MapCaseState(String caseState) {
        switch (caseState) {
            case "BOCaseStopped":
                return CASE_STOPPED;
            case "BOCaseStoppedReissue":
                return CASE_STOPPED;
            case "BOCaseStoppedAwaitRedec":
                return CASE_STOPPED;
            case "BORegistrarEscalation":
                return CASE_ESCALATED;
            default:
                return IN_PROGRESS;
        }
    }
}
