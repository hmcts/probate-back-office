package uk.gov.hmcts.probate.service.tasklist;

import uk.gov.hmcts.probate.htmlRendering.DetailsComponentRenderer;
import uk.gov.hmcts.probate.htmlRendering.GridRenderer;
import uk.gov.hmcts.probate.htmlRendering.LinkRenderer;
import uk.gov.hmcts.probate.model.caseProgress.TaskListState;
import uk.gov.hmcts.probate.model.caseProgress.TaskState;
import uk.gov.hmcts.probate.model.htmlTemplate.SendDocumentsDetailsHtmlTemplate;
import uk.gov.hmcts.probate.model.htmlTemplate.StateChangeDateHtmlTemplate;
import uk.gov.hmcts.probate.model.htmlTemplate.StatusTagHtmlTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.lang.String.format;
import static uk.gov.hmcts.probate.model.UrlConstants.*;

// Renders links / text and also the status tag - i.e. details varying by state
public class TaskStateRenderer {
    private static final String ADD_SOLICITOR_DETAILS_TEXT = "Add solicitor details";
    private static final String ADD_DECEASED_DETAILS_TEXT = "Add deceased details";
    private static final String ADD_APPLICATION_DETAILS_TEXT = "Add application details";
    private static final String REVIEW_OR_SUBMIT_TEXT = "Review and sign legal statement and submit application";
    private static final String SEND_DOCS_DETAILS_TITLE = "View the documents needed by HM Courts and Tribunal Service";
    private static final String AUTH_DOCS_TEXT = "Authenticate documents";
    private static final String EXAMINE_APP_TEXT = "Examine application";
    private static final String ISSUE_GRANT_TEXT = "Issue grant of representation<";

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy");

    // isProbate - true if application for probate, false if for caveat
    public static String renderByReplace(TaskListState currState, String html, Long caseId, LocalDate authDate, LocalDate submitDate) {
        final TaskState addSolState = GetTaskState(currState, TaskListState.TL_STATE_ADD_SOLICITOR_DETAILS);
        final TaskState addDeceasedState = GetTaskState(currState, TaskListState.TL_STATE_ADD_DECEASED_DETAILS);
        final TaskState addAppState = GetTaskState(currState, TaskListState.TL_STATE_ADD_APPLICATION_DETAILS);
        final TaskState rvwState = GetTaskState(currState, TaskListState.TL_STATE_REVIEW_AND_SUBMIT);
        final TaskState sendDocsState = GetTaskState(currState, TaskListState.TL_STATE_SEND_DOCUMENTS);
        final TaskState authDocsState = GetTaskState(currState, TaskListState.TL_STATE_AUTHENTICATE_DOCUMENTS);
        final TaskState examineState = GetTaskState(currState, TaskListState.TL_STATE_EXAMINE_APPLICATION);
        final TaskState issueState = GetTaskState(currState, TaskListState.TL_STATE_ISSUE_GRANT);

        final String caseIdStr = caseId.toString();

        return html == null ? null : html
                .replaceFirst("<addSolicitorLink/>", renderLinkOrText(TaskListState.TL_STATE_ADD_SOLICITOR_DETAILS, addSolState, ADD_SOLICITOR_DETAILS_TEXT, caseIdStr))
                .replaceFirst("<status-addSolicitor/>", renderTaskStateTag(addSolState))
                .replaceFirst("<addDeceasedLink/>", renderLinkOrText(TaskListState.TL_STATE_ADD_DECEASED_DETAILS, addDeceasedState, ADD_DECEASED_DETAILS_TEXT, caseIdStr))
                .replaceFirst("<status-addDeceasedDetails/>", renderTaskStateTag(addDeceasedState))
                .replaceFirst("<addAppLink/>", renderLinkOrText(TaskListState.TL_STATE_ADD_APPLICATION_DETAILS, addAppState, ADD_APPLICATION_DETAILS_TEXT, caseIdStr))
                .replaceFirst("<status-addApplicationDetails/>", renderTaskStateTag(addAppState))
                .replaceFirst("<rvwLink/>", renderLinkOrText(TaskListState.TL_STATE_REVIEW_AND_SUBMIT, rvwState, REVIEW_OR_SUBMIT_TEXT, caseIdStr))
                .replaceFirst("<status-reviewAndSubmit/>", renderTaskStateTag(rvwState))
                .replaceFirst("<submitDate/>", renderSubmitDate(submitDate))
                .replaceFirst("<sendDocsLink/>", renderSendDocsDetails(sendDocsState, caseIdStr))
                .replaceFirst("<status-sendDocuments/>", renderTaskStateTag(sendDocsState))
                .replaceFirst("<authDocsLink/>", renderLinkOrText(TaskListState.TL_STATE_EXAMINE_APPLICATION, authDocsState, AUTH_DOCS_TEXT, caseIdStr))
                .replaceFirst("<authenticatedDate/>", renderAuthenticatedDate(authDate))
                .replaceFirst("<status-authDocuments/>", renderTaskStateTag(authDocsState))
                .replaceFirst("<examAppLink/>", renderLinkOrText(TaskListState.TL_STATE_EXAMINE_APPLICATION, examineState, EXAMINE_APP_TEXT, caseIdStr))
                .replaceFirst("<status-examineApp/>", renderTaskStateTag(examineState))
                .replaceFirst("<issueGrantLink/>", renderLinkOrText(TaskListState.TL_STATE_ISSUE_GRANT, issueState, ISSUE_GRANT_TEXT, caseIdStr))
                .replaceFirst("<status-issueGrant/>", renderTaskStateTag(issueState));

    }

    private static TaskState GetTaskState  (TaskListState currState, TaskListState renderState) {
        if (currState == renderState) {
            return currState.isMultiState ? TaskState.IN_PROGRESS : TaskState.NOT_STARTED;
        }
        if (currState.compareTo(renderState) > 0) {
            return TaskState.COMPLETED;
        }
        return TaskState.NOT_AVAILABLE;
    }

    private static String renderTaskStateTag (TaskState taskState) {
        if (taskState == TaskState.NOT_AVAILABLE) {
            return "";
        }
        return StatusTagHtmlTemplate.statusTag
                .replaceFirst("<imgSrc/>", taskState.imageUrl)
                .replaceFirst("<imgAlt/>", taskState.displayText)
                .replaceFirst("<imgTitle/>", taskState.displayText);
    }

    private static String renderSendDocsDetails(TaskState sendDocsState, String caseId) {
        return sendDocsState == TaskState.NOT_AVAILABLE ? "" :
                DetailsComponentRenderer.renderByReplace(SEND_DOCS_DETAILS_TITLE,
                        SendDocumentsDetailsHtmlTemplate.docDetails.replaceFirst("<refNum/>", caseId));
    }

    private static String renderLinkOrText(TaskListState taskListState, TaskState currState, String linkText, String caseId) {
        String linkUrlTemplate = getLinkUrlTemplate(taskListState);
        return linkUrlTemplate != null && (currState == TaskState.NOT_STARTED || currState == TaskState.IN_PROGRESS) ?
                LinkRenderer.render(linkText, getLinkUrlTemplate(taskListState).replaceFirst("<CASE_ID>", caseId))
                : linkText;
    }

    private static String renderAuthenticatedDate(LocalDate authDate) {
        if (authDate == null) {
            return ""; // mustn't be null as we are chaining .replaceFirst methods
        }
        String authDateTemplate = StateChangeDateHtmlTemplate.stateChangeDateTemplate.replaceFirst("<stateChangeDateText/>", format("Authenticated on %s", authDate.format(dateFormat)));
        return GridRenderer.renderByReplace(authDateTemplate);
    }

    private static String renderSubmitDate(LocalDate submitDate) {
        if (submitDate == null) {
            return ""; // mustn't be null as we are chaining .replaceFirst methods
        }
        String submitDateTemplate = StateChangeDateHtmlTemplate.stateChangeDateTemplate.replaceFirst("<stateChangeDateText/>", format("Submitted on %s", submitDate.format(dateFormat)));
        return GridRenderer.renderByReplace(submitDateTemplate);
    }

    private static String getLinkUrlTemplate(TaskListState taskListState) {
        switch (taskListState) {
            case TL_STATE_ADD_SOLICITOR_DETAILS:
                return null;
            case TL_STATE_ADD_DECEASED_DETAILS:
                return deceasedDetailsUrlTemplate;
            case TL_STATE_ADD_APPLICATION_DETAILS:
                return addApplicationDetailsUrlTemplate;
            case TL_STATE_REVIEW_AND_SUBMIT:
                return reviewOrSubmitUrlTemplate;
            case TL_STATE_SEND_DOCUMENTS:
                return viewDocumentsToBeSentInUrlTemplate;
            default:
                return null;

        }
    }
}
