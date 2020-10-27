package uk.gov.hmcts.probate.model;

import lombok.Getter;

@Getter
// A state that is not actually 1:1 with AuthorisationCaseState.json, as several states in here may map to 1 ApplicationState
// e.g. PAAppCreated and SolAppCreated map to CaseCreated.

// VH 27/10/2020 Have parked work on this for now, still need to map each state but for case progress rendering I need CaseProgressState

public enum ApplicationState {
    CASE_CREATED("Case created"),
    PAYMENT_FAILED( "Payment failed"),
    // If this ever gets displayed or used we must have missed a state, or a new state added that has not been catered for here
    CASE_PRINTED("Case printed"),
    READY_FOR_EXAMINATION("Ready for examination"),

    EXAMINING("Examining"),
    EXAMINING_REISSUE("Examining reissue"),

    PERMANENT_CAVEAT("Permanent caveat"),
    CASE_ESCALATED("Case escalated to the Registrar"),
    READY_TO_ISSUE ("Case ready to issue"),
    QA("Case in QA"),

    // TODO - should these be amalgamated to CASE_MATCHING?
    CASE_MATCHING_ISSUE_GRANT("Case matching - issue grant"),
    CASE_MATCHING_EXAMINING("Case matching - examining"),
    CASE_MATCHING_REISSUE("Case matching - reissue"),

    // TODO - should these be amalgamated to CASE_STOPPED?
    CASE_STOPPED( "Case stopped"),
    CASE_STOPPED_REISSUE ("Case stopped - reissue"),
    CASE_STOPPED_REDECLARATION("Case stopped - redeclaration"),

    GRANT_ISSUED("Grant issued"),

    CASE_CLOSED("Case closed"),
    UNKNOWN_STATE("Unknown case state");

    private String name;

    ApplicationState(String name) {
        this.name = name;
    }

    public static ApplicationState MapCaseState(String caseState) {
        switch (caseState) {
            case "CaseCreated":
                return CASE_CREATED;
            case "PAAppCreated":
                return CASE_CREATED;
            case "SolAppCreated":
                return CASE_CREATED;

            case "CasePrinted":
                return CASE_PRINTED;
            case "BOReadyForExamination":
                return READY_FOR_EXAMINATION;

            case "BOExamining":
                return EXAMINING;
            case "BOExaminingReissue":
                return EXAMINING_REISSUE;

            case "BOCaveatPermenant":
                return PERMANENT_CAVEAT;
            case "BORegistrarEscalation":
                return CASE_ESCALATED;
            case "BOReadyToIssue":
                return READY_TO_ISSUE;
            case "BOCaseQA":
                return QA;

            // TODO - should these be amalgamated to CASE_MATCHING?
            case "BOCaseMatchingIssueGrant":
                return CASE_MATCHING_ISSUE_GRANT;
            case "BOCaseMatchingExamining":
                return CASE_MATCHING_EXAMINING;
            case "BOCaseMatchingReissue":
                return CASE_MATCHING_REISSUE;

            // TODO - should these be amalgamated to CASE_STOPPED?
            case "BOCaseStopped":
                return CASE_STOPPED;
            case "BOCaseStoppedReissue":
                return CASE_STOPPED_REISSUE;
            case "BOCaseStoppedAwaitRedec":
                return CASE_STOPPED_REDECLARATION;

            case "BOGrantIssued":
                return GRANT_ISSUED;
            case "BOCaseClosed":
                return CASE_CLOSED;

            case "CasePaymentFailed":
                return PAYMENT_FAILED;
            default:
                return UNKNOWN_STATE;
        }
    }
}
