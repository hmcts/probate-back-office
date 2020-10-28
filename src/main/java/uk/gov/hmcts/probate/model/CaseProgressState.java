package uk.gov.hmcts.probate.model;

import lombok.Getter;

@Getter
public enum CaseProgressState {
    DEFAULT("Default"),
    CASE_ESCALATED("Case escalated to the Registrar"),
    CASE_STOPPED( "Case stopped");

    private String displayText;

    CaseProgressState(String displayText) {
        this.displayText = displayText;
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
                return DEFAULT;
        }
    }
}
