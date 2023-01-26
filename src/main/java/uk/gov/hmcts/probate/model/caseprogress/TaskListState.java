package uk.gov.hmcts.probate.model.caseprogress;

import uk.gov.hmcts.probate.model.StateConstants;

// Note order of enum values is significant
public enum TaskListState {
    // not relevant for task list rendering
    TL_STATE_NOT_APPLICABLE(true),
    //
    TL_STATE_ADD_SOLICITOR_DETAILS(false),
    TL_STATE_ADD_DECEASED_DETAILS(false),
    TL_STATE_ADD_APPLICATION_DETAILS(false),
    TL_STATE_REVIEW_AND_SUBMIT(false),
    TL_STATE_SEND_DOCUMENTS(true),
    TL_STATE_AUTHENTICATE_DOCUMENTS(true),
    TL_STATE_EXAMINE_APPLICATION(true),
    TL_STATE_ISSUE_GRANT(true),
    TL_STATE_COMPLETE(true);

    public final boolean isMultiState;

    TaskListState(boolean multiState) {
        isMultiState = multiState;
    }

    // returns the current in progress state
    public static TaskListState mapCaseState(String caseState) {
        if (caseState == null) {
            return TL_STATE_ADD_SOLICITOR_DETAILS;
        }
        switch (caseState) {
            case "":
                return TL_STATE_ADD_SOLICITOR_DETAILS;

            case StateConstants.STATE_SOL_APP_CREATED_SOLICITOR_DTLS:
                return TL_STATE_ADD_SOLICITOR_DETAILS;

            case StateConstants.STATE_SOL_APP_CREATED_DECEASED_DTLS:
                return TL_STATE_ADD_DECEASED_DETAILS;

            case StateConstants.STATE_SOL_PROBATE_CREATED:
                return TL_STATE_ADD_APPLICATION_DETAILS;

            case StateConstants.STATE_SOL_INTESTACY_CREATED:
                return TL_STATE_ADD_APPLICATION_DETAILS;

            case StateConstants.STATE_SOL_ADMON_CREATED:
                return TL_STATE_ADD_APPLICATION_DETAILS;

            case StateConstants.STATE_BO_REDEC_NOTIFICATION_SENT:
                return TL_STATE_SEND_DOCUMENTS;

            case StateConstants.STATE_SOL_APP_UPDATED:
                return TL_STATE_REVIEW_AND_SUBMIT;

            case StateConstants.STATE_CASE_CREATED:
                return TL_STATE_SEND_DOCUMENTS;

            case StateConstants.STATE_CASE_PRINTED:
                return TL_STATE_SEND_DOCUMENTS;

            // Note - we never actually stop at TL_STATE_AUTHENTICATE_DOCUMENTS!

            case StateConstants.STATE_BO_READY_FOR_EXAMINATION:
                return TL_STATE_EXAMINE_APPLICATION;

            case StateConstants.STATE_BO_EXAMINING:
                return TL_STATE_EXAMINE_APPLICATION;

            case StateConstants.STATE_BO_EXAMINING_REISSUE:
                return TL_STATE_EXAMINE_APPLICATION;

            case StateConstants.STATE_BO_CASE_MATCHING_EXAMINING:
                return TL_STATE_EXAMINE_APPLICATION;

            case StateConstants.STATE_BO_READY_TO_ISSUE:
                return TL_STATE_EXAMINE_APPLICATION;

            case StateConstants.STATE_BO_CASE_MATCHING_ISSUE_GRANT:
                return TL_STATE_ISSUE_GRANT;

            case StateConstants.STATE_BO_GRANT_ISSUED:
                return TL_STATE_COMPLETE;

            default:
                return TL_STATE_NOT_APPLICABLE;
        }
    }
}
