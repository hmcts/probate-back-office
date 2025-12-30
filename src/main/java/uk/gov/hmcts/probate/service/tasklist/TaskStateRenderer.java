package uk.gov.hmcts.probate.service.tasklist;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.businessrule.DispenseNoticeSupportDocsRule;
import uk.gov.hmcts.probate.businessrule.AuthenticatedTranslationBusinessRule;
import uk.gov.hmcts.probate.businessrule.AdmonWillRenunicationRule;
import uk.gov.hmcts.probate.businessrule.NotarialWillBusinessRule;
import uk.gov.hmcts.probate.businessrule.TCResolutionLodgedWithApplicationRule;
import uk.gov.hmcts.probate.businessrule.IhtEstate207BusinessRule;
import uk.gov.hmcts.probate.businessrule.NoDocumentsRequiredBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA14FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA15FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA16FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA17FormBusinessRule;
import uk.gov.hmcts.probate.htmlrendering.DetailsComponentRenderer;
import uk.gov.hmcts.probate.htmlrendering.GridRenderer;
import uk.gov.hmcts.probate.htmlrendering.LinkRenderer;
import uk.gov.hmcts.probate.model.caseprogress.TaskListState;
import uk.gov.hmcts.probate.model.caseprogress.TaskState;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.htmltemplate.SendDocumentsDetailsHtmlTemplate;
import uk.gov.hmcts.probate.model.htmltemplate.StateChangeDateHtmlTemplate;
import uk.gov.hmcts.probate.model.htmltemplate.StatusTagHtmlTemplate;
import uk.gov.hmcts.probate.service.SendDocumentsRenderer;
import uk.gov.hmcts.probate.service.solicitorexecutor.NotApplyingExecutorsMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static uk.gov.hmcts.probate.model.CaseType.GRANT_OF_REPRESENTATION;
import static uk.gov.hmcts.probate.model.Constants.DISPENSE_NOTICE_SUPPORT_TEXT;
import static uk.gov.hmcts.probate.model.Constants.DISPENSE_NOTICE_SUPPORT_TEXT_WELSH;
import static uk.gov.hmcts.probate.model.Constants.AUTHENTICATED_TRANSLATION_WILL_TEXT;
import static uk.gov.hmcts.probate.model.Constants.AUTHENTICATED_TRANSLATION_WILL_TEXT_WELSH;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_INTESTACY;
import static uk.gov.hmcts.probate.model.Constants.IHT_ESTATE_207_TEXT;
import static uk.gov.hmcts.probate.model.Constants.IHT_ESTATE_207_TEXT_WELSH;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.NOTARIAL_COPY_WILL_TEXT;
import static uk.gov.hmcts.probate.model.Constants.NOTARIAL_COPY_WILL_TEXT_WELSH;
import static uk.gov.hmcts.probate.model.Constants.ORIGINAL_WILL_TEXT;
import static uk.gov.hmcts.probate.model.Constants.ORIGINAL_WILL_TEXT_WELSH;
import static uk.gov.hmcts.probate.model.Constants.ORIGINAL_WILL_WITH_CODICILS_TEXT;
import static uk.gov.hmcts.probate.model.Constants.ORIGINAL_WILL_WITH_CODICILS_TEXT_WELSH;
import static uk.gov.hmcts.probate.model.Constants.REASON_FOR_NOT_APPLYING_RENUNCIATION;
import static uk.gov.hmcts.probate.model.Constants.REASON_FOR_NOT_APPLYING_MENTALLY_INCAPABLE;
import static uk.gov.hmcts.probate.model.Constants.STATEMENT_OF_TRUTH_AND_EXHIBITS_TEXT;
import static uk.gov.hmcts.probate.model.Constants.STATEMENT_OF_TRUTH_AND_EXHIBITS_TEXT_WELSH;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.PageTextConstants.DISPENSE_NOTICE_SUPPORT_DOCS;
import static uk.gov.hmcts.probate.model.PageTextConstants.DISPENSE_NOTICE_SUPPORT_DOCS_WELSH;
import static uk.gov.hmcts.probate.model.PageTextConstants.AUTHENTICATED_TRANSLATION;
import static uk.gov.hmcts.probate.model.PageTextConstants.AUTHENTICATED_TRANSLATION_WELSH;
import static uk.gov.hmcts.probate.model.PageTextConstants.ADMON_WILL_RENUNCIATION;
import static uk.gov.hmcts.probate.model.PageTextConstants.ADMON_WILL_RENUNCIATION_WELSH;
import static uk.gov.hmcts.probate.model.Constants.TC_RESOLUTION_LODGED_WITH_APP;
import static uk.gov.hmcts.probate.model.Constants.TC_RESOLUTION_LODGED_WITH_APP_WELSH;
import static uk.gov.hmcts.probate.model.PageTextConstants.IHT_ESTATE_207;
import static uk.gov.hmcts.probate.model.PageTextConstants.IHT_FORM;
import static uk.gov.hmcts.probate.model.PageTextConstants.IHT_TEXT;
import static uk.gov.hmcts.probate.model.PageTextConstants.IHT_ESTATE_207_WELSH;
import static uk.gov.hmcts.probate.model.PageTextConstants.IHT_FORM_WELSH;
import static uk.gov.hmcts.probate.model.PageTextConstants.IHT_TEXT_WELSH;
import static uk.gov.hmcts.probate.model.PageTextConstants.ORIGINAL_WILL;
import static uk.gov.hmcts.probate.model.PageTextConstants.ORIGINAL_WILL_WELSH;
import static uk.gov.hmcts.probate.model.PageTextConstants.PA14_FORM;
import static uk.gov.hmcts.probate.model.PageTextConstants.PA15_FORM;
import static uk.gov.hmcts.probate.model.PageTextConstants.PA16_FORM;
import static uk.gov.hmcts.probate.model.PageTextConstants.PA17_FORM;
import static uk.gov.hmcts.probate.model.PageTextConstants.PA14_FORM_WELSH;
import static uk.gov.hmcts.probate.model.PageTextConstants.PA15_FORM_WELSH;
import static uk.gov.hmcts.probate.model.PageTextConstants.PA16_FORM_WELSH;
import static uk.gov.hmcts.probate.model.PageTextConstants.PA17_FORM_WELSH;
import static uk.gov.hmcts.probate.model.PageTextConstants.TC_RESOLUTION_WITH_APP;
import static uk.gov.hmcts.probate.model.PageTextConstants.TC_RESOLUTION_WITH_APP_WELSH;
import static uk.gov.hmcts.probate.model.caseprogress.UrlConstants.ADD_APPLICATION_DETAILS_URL_TEMPLATE_ADMON_WILL;
import static uk.gov.hmcts.probate.model.caseprogress.UrlConstants.ADD_APPLICATION_DETAILS_URL_TEMPLATE_GOP;
import static uk.gov.hmcts.probate.model.caseprogress.UrlConstants.ADD_APPLICATION_DETAILS_URL_TEMPLATE_INTESTACY;
import static uk.gov.hmcts.probate.model.caseprogress.UrlConstants.DECEASED_DETAILS_URL_TEMPLATE;
import static uk.gov.hmcts.probate.model.caseprogress.UrlConstants.REVIEW_OR_SUBMIT_URL_TEMPLATE;
import static uk.gov.hmcts.probate.model.caseprogress.UrlConstants.SOLICITOR_DETAILS_URL_TEMPLATE;
import static uk.gov.hmcts.probate.model.caseprogress.UrlConstants.TL_COVERSHEET_URL_TEMPLATE;
import static uk.gov.hmcts.probate.model.caseprogress.UrlConstants.TL_SERVICE_REQUEST_URL_TEMPLATE;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT400_VALUE;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.NOT_APPLICABLE_VALUE;

// Renders links / text and also the status tag - i.e. details varying by state
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskStateRenderer {
    private static final String ADD_SOLICITOR_DETAILS_TEXT = "Add Probate practitioner details";
    private static final String ADD_SOLICITOR_DETAILS_TEXT_WELSH = "Ychwanegu manylion ymarferydd profiant";
    private static final String ADD_DECEASED_DETAILS_TEXT = "Add deceased details";
    private static final String ADD_DECEASED_DETAILS_TEXT_WELSH = "Ychwanegu manylion yr ymadawedig ";
    private static final String ADD_APPLICATION_DETAILS_TEXT = "Add application details";
    private static final String ADD_APPLICATION_DETAILS_TEXT_WELSH = "Ychwanegu manylion y cais";
    private static final String REVIEW_OR_SUBMIT_TEXT = "Review and sign legal statement and submit application";
    private static final String REVIEW_OR_SUBMIT_TEXT_WELSH
            = "Adolygwch a llofnodwch y datganiad cyfreithiol a chyflwynwch y cais";
    private static final String MAKE_PAYMENT_TEXT = "Make payment";
    private static final String MAKE_PAYMENT_TEXT_WELSH = "Gwneud taliad";
    private static final String NO_PAYMENT_REQUIRED_TEXT = "</p><p><secText>No payment is required.</secText>";
    private static final String NO_PAYMENT_REQUIRED_TEXT_WELSH = "</p><p>Nid oes angen unrhyw daliad.</secText>";
    private static final String PAYMENT_HINT_TEXT = "</p><p><secText>Once payment is made, "
        + "you'll need to refresh the page or  re-enter the case for the payment status to update.</secText>";
    private static final String PAYMENT_HINT_TEXT_WELSH = "</p><p><secText>Unwaith y bydd y taliad wedi'i wneud, "
            + "bydd angen i chi ail-lwytho'r dudalen neu fynd i mewn i'r "
            + "achos eto er mwyn i'r statws talu gael ei ddiweddaru.</secText>";
    static final String SEND_DOCS_DETAILS_TITLE = "View the documents needed by HM Courts and Tribunal Service";

    static final String SEND_DOCS_DETAILS_TITLE_WELSH = "Gweld y dogfennau sydd eu hangen gan Wasanaeth "
            + "Llysoedd a Thribiwnlysoedd EF";
    private static final String AUTH_DOCS_TEXT = "Authenticate documents";
    private static final String AUTH_DOCS_TEXT_WELSH = "Dilysu dogfennau";
    private static final String EXAMINE_APP_TEXT = "Examine application";
    private static final String EXAMINE_APP_TEXT_WELSH = "Archwilio'r cais";
    private static final String ISSUE_GRANT_TEXT = "Issue grant of representation";
    private static final String ISSUE_GRANT_TEXT_WELSH = "Cyhoeddi grant cynrychiolaeth";
    private static final String COVERSHEET = "coversheet";
    private static final String IHT_400421 = "IHT400421";
    private static final String LIST_ITEM_START = "<li>";
    private static final String LIST_ITEM_END = "</li>";
    private static final String CASE_ID_STRING = "<CASE_ID>";
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final Locale welshLocale = new Locale("cy", "GB");
    private static final DateTimeFormatter welshDateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy", welshLocale);
    private final AuthenticatedTranslationBusinessRule authenticatedTranslationBusinessRule;
    private final PA14FormBusinessRule pa14FormBusinessRule;
    private final PA15FormBusinessRule pa15FormBusinessRule;
    private final PA16FormBusinessRule pa16FormBusinessRule;
    private final PA17FormBusinessRule pa17FormBusinessRule;
    private final IhtEstate207BusinessRule ihtEstate207BusinessRule;
    private final AdmonWillRenunicationRule admonWillRenunicationRule;
    private final NotApplyingExecutorsMapper notApplyingExecutorsMapper;
    private final SendDocumentsRenderer sendDocumentsRenderer;
    private final TCResolutionLodgedWithApplicationRule tcResolutionLodgedWithApplicationRule;
    private final DispenseNoticeSupportDocsRule dispenseNoticeSupportDocsRule;
    private final NotarialWillBusinessRule notarialWillBusinessRule;
    private final NoDocumentsRequiredBusinessRule noDocumentsRequiredBusinessRule;

    // isProbate - true if application for probate, false if for caveat
    public String renderByReplace(TaskListState currState, String html, Long caseId,
                                  String willType, String solSOTNeedToUpdate,
                                  LocalDate authDate, LocalDate submitDate, CaseDetails details) {

        final TaskState addSolState = getTaskState(currState, TaskListState.TL_STATE_ADD_SOLICITOR_DETAILS,
                solSOTNeedToUpdate, details.getData().getEvidenceHandled(), details.getData().getAttachDocuments(),
                details.getData().getHmrcLetterId());
        final TaskState addDeceasedState = getTaskState(currState, TaskListState.TL_STATE_ADD_DECEASED_DETAILS,
                solSOTNeedToUpdate, details.getData().getEvidenceHandled(), details.getData().getAttachDocuments(),
                details.getData().getHmrcLetterId());
        final TaskState addAppState = getTaskState(currState, TaskListState.TL_STATE_ADD_APPLICATION_DETAILS,
                solSOTNeedToUpdate, details.getData().getEvidenceHandled(), details.getData().getAttachDocuments(),
                details.getData().getHmrcLetterId());
        final TaskState rvwState = getTaskState(currState, TaskListState.TL_STATE_REVIEW_AND_SUBMIT,
                solSOTNeedToUpdate, details.getData().getEvidenceHandled(), details.getData().getAttachDocuments(),
                details.getData().getHmrcLetterId());
        final TaskState paymentState = getPaymentTaskState(currState, TaskListState.TL_STATE_PAYMENT_ATTEMPTED);
        final TaskState sendDocsState = getTaskState(currState, TaskListState.TL_STATE_SEND_DOCUMENTS,
                solSOTNeedToUpdate, details.getData().getEvidenceHandled(), details.getData().getAttachDocuments(),
                details.getData().getHmrcLetterId());
        final TaskState authDocsState = getTaskState(currState, TaskListState.TL_STATE_AUTHENTICATE_DOCUMENTS,
                solSOTNeedToUpdate, details.getData().getEvidenceHandled(), details.getData().getAttachDocuments(),
                details.getData().getHmrcLetterId());
        final TaskState examineState = getTaskState(currState, TaskListState.TL_STATE_EXAMINE_APPLICATION,
                solSOTNeedToUpdate, details.getData().getEvidenceHandled(), details.getData().getAttachDocuments(),
                details.getData().getHmrcLetterId());
        final TaskState issueState = getTaskState(currState, TaskListState.TL_STATE_ISSUE_GRANT,
                solSOTNeedToUpdate, details.getData().getEvidenceHandled(), details.getData().getAttachDocuments(),
                details.getData().getHmrcLetterId());

        // the only time caseId will be null is when running unit tests!
        final String caseIdStr = caseId == null ? "" : caseId.toString();

        return html == null ? null : html
                .replaceFirst("<addSolicitorLink/>",
                    renderLinkOrText(TaskListState.TL_STATE_ADD_SOLICITOR_DETAILS,
                        currState, addSolState, ADD_SOLICITOR_DETAILS_TEXT, caseIdStr, willType, details))
                .replaceFirst("<addSolicitorLinkWelsh/>",
                    renderLinkOrText(TaskListState.TL_STATE_ADD_SOLICITOR_DETAILS,
                        currState, addSolState, ADD_SOLICITOR_DETAILS_TEXT_WELSH, caseIdStr, willType, details)
                )
                .replaceFirst("<status-addSolicitor/>", renderTaskStateTag(addSolState))
                .replaceFirst("<status-addSolicitorWelsh/>", renderTaskStateTagWelsh(addSolState))
                .replaceFirst("<addDeceasedLink/>", renderLinkOrText(TaskListState.TL_STATE_ADD_DECEASED_DETAILS,
                        currState, addDeceasedState, ADD_DECEASED_DETAILS_TEXT, caseIdStr, willType, details))
                .replaceFirst("<addDeceasedLinkWelsh/>", renderLinkOrText(TaskListState.TL_STATE_ADD_DECEASED_DETAILS,
                        currState, addDeceasedState, ADD_DECEASED_DETAILS_TEXT_WELSH, caseIdStr, willType, details))
                .replaceFirst("<status-addDeceasedDetails/>", renderTaskStateTag(addDeceasedState))
                .replaceFirst("<status-addDeceasedDetailsWelsh/>", renderTaskStateTagWelsh(addDeceasedState))
                .replaceFirst("<addAppLink/>", renderLinkOrText(TaskListState.TL_STATE_ADD_APPLICATION_DETAILS,
                        currState, addAppState, ADD_APPLICATION_DETAILS_TEXT, caseIdStr, willType, details))
                .replaceFirst("<addAppLinkWelsh/>", renderLinkOrText(TaskListState.TL_STATE_ADD_APPLICATION_DETAILS,
                        currState, addAppState, ADD_APPLICATION_DETAILS_TEXT_WELSH, caseIdStr, willType, details))
                .replaceFirst("<status-addApplicationDetails/>", renderTaskStateTag(addAppState))
                .replaceFirst("<status-addApplicationDetailsWelsh/>", renderTaskStateTagWelsh(addAppState))
                .replaceFirst("<rvwLink/>", renderLinkOrText(TaskListState.TL_STATE_REVIEW_AND_SUBMIT,
                        currState, rvwState, REVIEW_OR_SUBMIT_TEXT, caseIdStr, willType, details))
                .replaceFirst("<rvwLinkWelsh/>", renderLinkOrText(TaskListState.TL_STATE_REVIEW_AND_SUBMIT,
                        currState, rvwState, REVIEW_OR_SUBMIT_TEXT_WELSH, caseIdStr, willType, details))
                .replaceFirst("<status-reviewAndSubmit/>", renderTaskStateTag(rvwState))
                .replaceFirst("<status-reviewAndSubmitWelsh/>", renderTaskStateTagWelsh(rvwState))
                .replaceFirst("<reviewAndSubmitDate/>", renderSubmitDate(submitDate))
                .replaceFirst("<reviewAndSubmitDateWelsh/>", renderSubmitDateWelsh(submitDate))
                .replaceFirst("<reviewAndSubmitDateWelsh/>", renderSubmitDate(submitDate))
                .replaceFirst("<paymentTabLink/>", renderPaymentLinkOrText(paymentState, currState, caseIdStr,
                        willType))
                .replaceFirst("<paymentTabLinkWelsh/>", renderPaymentLinkOrTextWelsh(paymentState, currState, caseIdStr,
                        willType))
                .replaceFirst("<paymentHintText/>", renderPaymentHintText(currState, details))
                .replaceFirst("<paymentHintTextWelsh/>", renderPaymentHintTextWelsh(currState, details))
                .replaceFirst("<status-paymentMade/>", renderTaskStateTag(paymentState))
                .replaceFirst("<status-paymentMadeWelsh/>", renderTaskStateTagWelsh(paymentState))
                .replaceFirst("<sendDocsLink/>", renderSendDocsDetails(sendDocsState, caseIdStr, details))
                .replaceFirst("<sendDocsLinkWelsh/>", renderSendDocsDetailsWelsh(sendDocsState, caseIdStr, details))
                .replaceFirst("<status-sendDocuments/>", renderTaskStateTag(sendDocsState))
                .replaceFirst("<status-sendDocumentsWelsh/>", renderTaskStateTagWelsh(sendDocsState))
                .replaceFirst("<authDocsLink/>", renderLinkOrText(TaskListState.TL_STATE_EXAMINE_APPLICATION,
                        currState, authDocsState, AUTH_DOCS_TEXT, caseIdStr, willType, details))
                .replaceFirst("<authDocsLinkWelsh/>", renderLinkOrText(TaskListState.TL_STATE_EXAMINE_APPLICATION,
                        currState, authDocsState, AUTH_DOCS_TEXT_WELSH, caseIdStr, willType, details))
                .replaceFirst("<authenticatedDate/>", renderAuthenticatedDate(authDate))
                .replaceFirst("<authenticatedDateWelsh/>", renderAuthenticatedDateWelsh(authDate))
                .replaceFirst("<status-authDocuments/>", renderTaskStateTag(authDocsState))
                .replaceFirst("<status-authDocumentsWelsh/>", renderTaskStateTagWelsh(authDocsState))
                .replaceFirst("<examAppLink/>", renderLinkOrText(TaskListState.TL_STATE_EXAMINE_APPLICATION,
                        currState, examineState, EXAMINE_APP_TEXT, caseIdStr, willType, details))
                .replaceFirst("<examAppLinkWelsh/>", renderLinkOrText(TaskListState.TL_STATE_EXAMINE_APPLICATION,
                        currState, examineState, EXAMINE_APP_TEXT_WELSH, caseIdStr, willType, details))
                .replaceFirst("<status-examineApp/>", renderTaskStateTag(examineState))
                .replaceFirst("<status-examineAppWelsh/>", renderTaskStateTagWelsh(examineState))
                .replaceFirst("<issueGrantLink/>", renderLinkOrText(TaskListState.TL_STATE_ISSUE_GRANT,
                        currState, issueState, ISSUE_GRANT_TEXT, caseIdStr, willType, details))
                .replaceFirst("<issueGrantLinkWelsh/>", renderLinkOrText(TaskListState.TL_STATE_ISSUE_GRANT,
                        currState, issueState, ISSUE_GRANT_TEXT_WELSH, caseIdStr, willType, details))
                .replaceFirst("<status-issueGrant/>", renderTaskStateTag(issueState))
                .replaceFirst("<status-issueGrantWelsh/>", renderTaskStateTagWelsh(issueState))
                .replaceFirst("<coversheet/>", renderLinkOrText(TaskListState.TL_STATE_SEND_DOCUMENTS,
                        currState, sendDocsState, COVERSHEET, caseIdStr, willType, details));
    }

    private TaskState getPaymentTaskState(TaskListState currState, TaskListState renderState) {
        TaskState taskState;
        if (currState == TaskListState.TL_STATE_MAKE_PAYMENT) {
            taskState = TaskState.NOT_STARTED;
        } else if (currState == TaskListState.TL_STATE_PAYMENT_ATTEMPTED) {
            taskState =  TaskState.IN_PROGRESS;
        } else if (currState.compareTo(renderState) > 0) {
            taskState =  TaskState.COMPLETED;
        } else {
            taskState = TaskState.NOT_AVAILABLE;
        }
        return taskState;
    }

    private String renderPaymentLinkOrText(TaskState currTaskState, TaskListState currState, String caseId,
                                           String willType) {
        String linkUrlTemplate = getLinkUrlTemplate(currState, willType)
                .replaceFirst("<CASE_ID>", GRANT_OF_REPRESENTATION.getCode());
        return linkUrlTemplate != null
                && (currState == TaskListState.TL_STATE_MAKE_PAYMENT
                    || currState == TaskListState.TL_STATE_PAYMENT_ATTEMPTED)
                && (currTaskState == TaskState.NOT_STARTED || currTaskState == TaskState.IN_PROGRESS)
                ? LinkRenderer.render(MAKE_PAYMENT_TEXT, linkUrlTemplate.replaceFirst(CASE_ID_STRING, caseId))
                : MAKE_PAYMENT_TEXT;
    }

    private String renderPaymentLinkOrTextWelsh(TaskState currTaskState, TaskListState currState, String caseId,
                                           String willType) {
        String linkUrlTemplate = getLinkUrlTemplate(currState, willType)
                .replaceFirst("<CASE_ID>", GRANT_OF_REPRESENTATION.getCode());
        return linkUrlTemplate != null
                && (currState == TaskListState.TL_STATE_MAKE_PAYMENT
                || currState == TaskListState.TL_STATE_PAYMENT_ATTEMPTED)
                && (currTaskState == TaskState.NOT_STARTED || currTaskState == TaskState.IN_PROGRESS)
                ? LinkRenderer.render(MAKE_PAYMENT_TEXT_WELSH, linkUrlTemplate.replaceFirst(CASE_ID_STRING, caseId))
                : MAKE_PAYMENT_TEXT_WELSH;
    }

    private String renderPaymentHintText(TaskListState currState, CaseDetails details) {
        if (currState == TaskListState.TL_STATE_SEND_DOCUMENTS
                && (details.getData().getTotalFee() == null
                || details.getData().getTotalFee().compareTo(BigDecimal.ZERO) == 0)) {
            return NO_PAYMENT_REQUIRED_TEXT;
        }
        return currState == TaskListState.TL_STATE_MAKE_PAYMENT
                || currState == TaskListState.TL_STATE_PAYMENT_ATTEMPTED ? PAYMENT_HINT_TEXT : "";
    }

    private String renderPaymentHintTextWelsh(TaskListState currState, CaseDetails details) {
        if (currState == TaskListState.TL_STATE_SEND_DOCUMENTS
                && (details.getData().getTotalFee() == null
                || details.getData().getTotalFee().compareTo(BigDecimal.ZERO) == 0)) {
            return NO_PAYMENT_REQUIRED_TEXT_WELSH;
        }
        return currState == TaskListState.TL_STATE_MAKE_PAYMENT
                || currState == TaskListState.TL_STATE_PAYMENT_ATTEMPTED ? PAYMENT_HINT_TEXT_WELSH : "";
    }

    private TaskState getTaskState(TaskListState currState, TaskListState renderState,
                                   String solSOTNeedToUpdate, String evidenceHandled, String attachDocuments,
                                   String hmrcLetterId) {
        if (solSOTNeedToUpdate != null && solSOTNeedToUpdate.equals(YES)
                && renderState.compareTo(TaskListState.TL_STATE_REVIEW_AND_SUBMIT) <= 0) {
            if (currState.compareTo(renderState) > 0) {
                return TaskState.COMPLETED;
            }
            return TaskState.IN_PROGRESS;
        }

        if (currState == TaskListState.TL_STATE_ADD_DECEASED_DETAILS
                && renderState == TaskListState.TL_STATE_ADD_DECEASED_DETAILS && NO.equals(hmrcLetterId)) {
            return TaskState.IN_PROGRESS;
        }

        if (currState == renderState) {
            if (renderState == TaskListState.TL_STATE_SEND_DOCUMENTS
                    && NO.equals(evidenceHandled)) {
                return TaskState.COMPLETED;
            } else if (YES.equals(attachDocuments) && renderState == TaskListState.TL_STATE_SEND_DOCUMENTS) {
                return TaskState.COMPLETED;
            } else {
                return currState.isMultiState ? TaskState.IN_PROGRESS : TaskState.NOT_STARTED;
            }
        }
        if (currState.compareTo(renderState) > 0) {
            return TaskState.COMPLETED;
        }
        if (currState == TaskListState.TL_STATE_SEND_DOCUMENTS
                && renderState == TaskListState.TL_STATE_AUTHENTICATE_DOCUMENTS) {
            if (YES.equals(attachDocuments)) {
                return TaskState.IN_PROGRESS;
            } else if (NO.equals(evidenceHandled)) {
                return TaskState.COMPLETED;
            }
        }
        if (currState == TaskListState.TL_STATE_SEND_DOCUMENTS
                && renderState == TaskListState.TL_STATE_EXAMINE_APPLICATION
                && NO.equals(evidenceHandled)) {
            return TaskState.IN_PROGRESS;
        }
        return TaskState.NOT_AVAILABLE;
    }


    private String renderTaskStateTag(TaskState taskState) {
        if (taskState == TaskState.NOT_AVAILABLE) {
            return "";
        }
        return StatusTagHtmlTemplate.STATUS_TAG
                .replaceFirst("<imgSrc/>", taskState.imageUrl)
                .replaceFirst("<imgAlt/>", taskState.displayText)
                .replaceFirst("<imgTitle/>", taskState.displayText);
    }

    private String renderTaskStateTagWelsh(TaskState taskState) {
        if (taskState == TaskState.NOT_AVAILABLE) {
            return "";
        }
        return StatusTagHtmlTemplate.STATUS_TAG
                .replaceFirst("<imgSrc/>", taskState.imageUrlWelsh)
                .replaceFirst("<imgAlt/>", taskState.displayTextWelsh)
                .replaceFirst("<imgTitle/>", taskState.displayTextWelsh);
    }

    String renderSendDocsDetails(TaskState sendDocsState, String caseId, CaseDetails details) {
        if (noDocumentsRequiredBusinessRule.isApplicable(details.getData())) {
            return DetailsComponentRenderer.renderByReplace(SEND_DOCS_DETAILS_TITLE,"");
        }
        Map<String, String> keyValues = getKeyValues(details.getData());
        return sendDocsState == TaskState.NOT_AVAILABLE ? "" :
                DetailsComponentRenderer.renderByReplace(SEND_DOCS_DETAILS_TITLE,
                        SendDocumentsDetailsHtmlTemplate.DOC_DETAILS.replaceFirst("<refNum/>", caseId)
                .replaceFirst(ORIGINAL_WILL, keyValues.getOrDefault("originalWill", ""))
                .replaceFirst(IHT_TEXT, keyValues.getOrDefault("ihtText", ""))
                .replaceFirst(IHT_FORM, keyValues.getOrDefault("ihtForm", ""))
                .replaceFirst(PA14_FORM, keyValues.getOrDefault("pa14Form", ""))
                .replaceFirst(PA15_FORM, keyValues.getOrDefault("pa15Form", ""))
                .replaceFirst(PA16_FORM, keyValues.getOrDefault("pa16Form", ""))
                .replaceFirst(PA17_FORM, keyValues.getOrDefault("pa17Form", ""))
                .replaceFirst(IHT_ESTATE_207, keyValues.getOrDefault("ihtEstate207", ""))
                .replaceFirst(AUTHENTICATED_TRANSLATION,
                                        keyValues.getOrDefault("authenticatedTranslation", ""))
                .replaceFirst(ADMON_WILL_RENUNCIATION,
                    keyValues.getOrDefault("admonWillRenForms", ""))
                .replaceFirst(TC_RESOLUTION_WITH_APP,
                    keyValues.getOrDefault("tcResolutionLodgedWithApp", ""))
                .replaceFirst(DISPENSE_NOTICE_SUPPORT_DOCS,
                    keyValues.getOrDefault("dispenseWithNoticeSupportingDocs", ""))
                );
    }

    String renderSendDocsDetailsWelsh(TaskState sendDocsState, String caseId, CaseDetails details) {
        if (noDocumentsRequiredBusinessRule.isApplicable(details.getData())) {
            return DetailsComponentRenderer.renderByReplace(SEND_DOCS_DETAILS_TITLE_WELSH,"");
        }
        Map<String, String> keyValues = getKeyValues(details.getData());
        return sendDocsState == TaskState.NOT_AVAILABLE ? "" :
                DetailsComponentRenderer.renderByReplace(SEND_DOCS_DETAILS_TITLE_WELSH,
                        SendDocumentsDetailsHtmlTemplate.DOC_DETAILS_WELSH.replaceFirst("<refNum/>", caseId)
                                .replaceFirst(ORIGINAL_WILL_WELSH, keyValues.getOrDefault("originalWillWelsh", ""))
                                .replaceFirst(IHT_TEXT_WELSH, keyValues.getOrDefault("ihtTextWelsh", ""))
                                .replaceFirst(IHT_FORM_WELSH, keyValues.getOrDefault("ihtFormWelsh", ""))
                                .replaceFirst(PA14_FORM_WELSH, keyValues.getOrDefault("pa14FormWelsh", ""))
                                .replaceFirst(PA15_FORM_WELSH, keyValues.getOrDefault("pa15FormWelsh", ""))
                                .replaceFirst(PA16_FORM_WELSH, keyValues.getOrDefault("pa16FormWelsh", ""))
                                .replaceFirst(PA17_FORM_WELSH, keyValues.getOrDefault("pa17FormWelsh", ""))
                                .replaceFirst(IHT_ESTATE_207_WELSH, keyValues.getOrDefault("ihtEstate207Welsh", ""))
                                .replaceFirst(AUTHENTICATED_TRANSLATION_WELSH,
                                        keyValues.getOrDefault("authenticatedTranslationWelsh", ""))
                                .replaceFirst(ADMON_WILL_RENUNCIATION_WELSH,
                                        keyValues.getOrDefault("admonWillRenFormsWelsh", ""))
                                .replaceFirst(TC_RESOLUTION_WITH_APP_WELSH,
                                        keyValues.getOrDefault("tcResolutionLodgedWithAppWelsh", ""))
                                .replaceFirst(DISPENSE_NOTICE_SUPPORT_DOCS_WELSH,
                                        keyValues.getOrDefault("dispenseWithNoticeSupportingDocsWelsh", ""))
                );
    }

    private String renderLinkOrText(TaskListState taskListState, TaskListState currState,
                                    TaskState currTaskState, String linkText, String caseId,
                                    String willType, CaseDetails details) {

        String linkUrlTemplate = getLinkUrlTemplate(taskListState, willType)
                .replaceFirst("<CASE_ID>", GRANT_OF_REPRESENTATION.getCode());
        String coversheetUrl = details.getData().getSolsCoversheetDocument() == null ? "#" : details
            .getData().getSolsCoversheetDocument().getDocumentBinaryUrl();

        if (linkUrlTemplate != null && currState == taskListState
            && (currState == TaskListState.TL_STATE_SEND_DOCUMENTS)) {
            return LinkRenderer.renderOutside(linkText, linkUrlTemplate.replaceFirst(CASE_ID_STRING, caseId)
                .replaceFirst("<DOCUMENT_LINK>", coversheetUrl));
        }

        return linkUrlTemplate != null && currState == taskListState
                && (currTaskState == TaskState.NOT_STARTED || currTaskState == TaskState.IN_PROGRESS)
                ? LinkRenderer.render(linkText, linkUrlTemplate.replaceFirst(CASE_ID_STRING, caseId)) : linkText;
    }

    private static String renderAuthenticatedDate(LocalDate authDate) {
        if (authDate == null) {
            return ""; // mustn't be null as we are chaining .replaceFirst methods
        }
        String authDateTemplate = StateChangeDateHtmlTemplate.STATE_CHANGE_DATE_TEMPLATE
                .replaceFirst("<stateChangeDateText/>",
                    format("Authenticated on %s", authDate.format(dateFormat)));
        return GridRenderer.renderByReplace(authDateTemplate);
    }

    private static String renderAuthenticatedDateWelsh(LocalDate authDate) {
        if (authDate == null) {
            return ""; // mustn't be null as we are chaining .replaceFirst methods
        }
        String authDateTemplate = StateChangeDateHtmlTemplate.STATE_CHANGE_DATE_TEMPLATE
                .replaceFirst("<stateChangeDateText/>",
                        format("Dilyswyd ar %s", authDate.format(welshDateFormat)));
        return GridRenderer.renderByReplace(authDateTemplate);
    }

    private String renderSubmitDate(LocalDate submitDate) {
        if (submitDate == null) {
            return ""; // mustn't be null as we are chaining .replaceFirst methods
        }
        String submitDateTemplate = StateChangeDateHtmlTemplate.STATE_CHANGE_DATE_TEMPLATE
                .replaceFirst("<stateChangeDateText/>",
                    format("Submitted on %s", submitDate.format(dateFormat)));
        return GridRenderer.renderByReplace(submitDateTemplate);
    }

    private String renderSubmitDateWelsh(LocalDate submitDate) {
        if (submitDate == null) {
            return ""; // mustn't be null as we are chaining .replaceFirst methods
        }
        String submitDateTemplate = StateChangeDateHtmlTemplate.STATE_CHANGE_DATE_TEMPLATE
                .replaceFirst("<stateChangeDateText/>",
                        format("Cyflwynwyd ar %s", submitDate.format(welshDateFormat)));
        return GridRenderer.renderByReplace(submitDateTemplate);
    }

    private String getLinkUrlTemplate(TaskListState taskListState, String willType) {
        switch (taskListState) {
            case TL_STATE_ADD_SOLICITOR_DETAILS:
                return SOLICITOR_DETAILS_URL_TEMPLATE;
            case TL_STATE_ADD_DECEASED_DETAILS:
                return DECEASED_DETAILS_URL_TEMPLATE;
            case TL_STATE_ADD_APPLICATION_DETAILS:
                switch (willType) {
                    case "NoWill":
                        return ADD_APPLICATION_DETAILS_URL_TEMPLATE_INTESTACY;
                    case "WillLeftAnnexed":
                        return ADD_APPLICATION_DETAILS_URL_TEMPLATE_ADMON_WILL;
                    default:
                        return ADD_APPLICATION_DETAILS_URL_TEMPLATE_GOP;
                }
            case TL_STATE_REVIEW_AND_SUBMIT:
                return REVIEW_OR_SUBMIT_URL_TEMPLATE;
            case TL_STATE_SEND_DOCUMENTS:
                return TL_COVERSHEET_URL_TEMPLATE;
            case TL_STATE_MAKE_PAYMENT:
                return TL_SERVICE_REQUEST_URL_TEMPLATE;
            case TL_STATE_PAYMENT_ATTEMPTED:
                return TL_SERVICE_REQUEST_URL_TEMPLATE;
            default:
                return null;
        }
    }

    private Map<String, String> getKeyValues(CaseData data) {
        Map<String, String> keyValue = new HashMap<>();
        String willText = getWillCaseTypeLabel(data);
        String willTextWelsh = getWillCaseTypeLabelWelsh(data);
        keyValue.put("originalWill", willText);
        keyValue.put("originalWillWelsh", willTextWelsh);
        String ihtFormValue = data.getIhtFormId() == null ? "" : data.getIhtFormId();
        String ihtText = "";
        String ihtTextWelsh = "";
        String ihtForm = "";
        String ihtFormWelsh = "";
        if (!"".equals(ihtFormValue) && !ihtFormValue.contentEquals(IHT400_VALUE)
                && !ihtFormValue.contentEquals(IHT_400421)
                && !ihtFormValue.contentEquals(NOT_APPLICABLE_VALUE)) {
            ihtText = LIST_ITEM_START + "the inheritance tax form ";
            ihtTextWelsh = LIST_ITEM_START + "y ffurflen treth etifeddiant ";
            if (YES.equals(data.getIht217())) {
                ihtForm = "IHT205 and IHT217" + LIST_ITEM_END;
                ihtFormWelsh = "IHT205 ac IHT217" + LIST_ITEM_END;
            } else {
                ihtForm = ihtFormValue + LIST_ITEM_END;
                ihtFormWelsh = ihtFormValue + LIST_ITEM_END;
            }
        }

        keyValue.put("ihtText", ihtText);
        keyValue.put("ihtTextWelsh", ihtTextWelsh);
        keyValue.put("ihtForm", ihtForm);
        keyValue.put("ihtFormWelsh", ihtFormWelsh);
        String pa14Form = "";
        if (pa14FormBusinessRule.isApplicable(data)) {
            pa14Form = buildPA14NotApplyingExecutorsLinks(data, REASON_FOR_NOT_APPLYING_MENTALLY_INCAPABLE);
        }
        keyValue.put("pa14Form", pa14Form);

        String pa14FormWelsh = "";
        if (pa14FormBusinessRule.isApplicable(data)) {
            pa14FormWelsh = buildPA14NotApplyingExecutorsLinksWelsh(data, REASON_FOR_NOT_APPLYING_MENTALLY_INCAPABLE);
        }
        keyValue.put("pa14FormWelsh", pa14FormWelsh);

        String pa15Form = "";
        if (pa15FormBusinessRule.isApplicable(data)) {
            pa15Form = buildPA15NotApplyingExecutorsLinks(data, REASON_FOR_NOT_APPLYING_RENUNCIATION);
        }
        keyValue.put("pa15Form", pa15Form);

        String pa15FormWelsh = "";
        if (pa15FormBusinessRule.isApplicable(data)) {
            pa15FormWelsh = buildPA15NotApplyingExecutorsLinksWelsh(data, REASON_FOR_NOT_APPLYING_RENUNCIATION);
        }
        keyValue.put("pa15FormWelsh", pa15FormWelsh);

        String pa16Form = "";
        if (pa16FormBusinessRule.isApplicable(data)) {
            pa16Form = LIST_ITEM_START + sendDocumentsRenderer.getPA16FormText() + LIST_ITEM_END;
        }
        keyValue.put("pa16Form", pa16Form);

        String pa16FormWelsh = "";
        if (pa16FormBusinessRule.isApplicable(data)) {
            pa16FormWelsh = LIST_ITEM_START + sendDocumentsRenderer.getPA16FormTextWelsh() + LIST_ITEM_END;
        }
        keyValue.put("pa16FormWelsh", pa16FormWelsh);

        String pa17Form = "";
        if (pa17FormBusinessRule.isApplicable(data)) {
            pa17Form = LIST_ITEM_START + sendDocumentsRenderer.getPA17FormText() + LIST_ITEM_END;
        }
        keyValue.put("pa17Form", pa17Form);

        String pa17FormWelsh = "";
        if (pa17FormBusinessRule.isApplicable(data)) {
            pa17FormWelsh = LIST_ITEM_START + sendDocumentsRenderer.getPA17FormTextWelsh() + LIST_ITEM_END;
        }
        keyValue.put("pa17FormWelsh", pa17FormWelsh);

        String ihtEstate207 = "";
        if (ihtEstate207BusinessRule.isApplicable(data)) {
            ihtEstate207 = LIST_ITEM_START + IHT_ESTATE_207_TEXT + LIST_ITEM_END;
        }
        keyValue.put("ihtEstate207", ihtEstate207);

        String ihtEstate207Welsh = "";
        if (ihtEstate207BusinessRule.isApplicable(data)) {
            ihtEstate207Welsh = LIST_ITEM_START + IHT_ESTATE_207_TEXT_WELSH + LIST_ITEM_END;
        }
        keyValue.put("ihtEstate207Welsh", ihtEstate207Welsh);

        String admonWillRenForms = "";
        if (admonWillRenunicationRule.isApplicable(data)) {
            admonWillRenForms = LIST_ITEM_START + sendDocumentsRenderer.getAdmonWillRenunciationText() + LIST_ITEM_END;
        }
        keyValue.put("admonWillRenForms", admonWillRenForms);

        String admonWillRenFormsWelsh = "";
        if (admonWillRenunicationRule.isApplicable(data)) {
            admonWillRenFormsWelsh = LIST_ITEM_START + sendDocumentsRenderer.getAdmonWillRenunciationTextWelsh()
                    + LIST_ITEM_END;
        }
        keyValue.put("admonWillRenFormsWelsh", admonWillRenFormsWelsh);

        String tcResolutionLodgedWithApp = "";
        if (tcResolutionLodgedWithApplicationRule.isApplicable(data)) {
            tcResolutionLodgedWithApp = LIST_ITEM_START + TC_RESOLUTION_LODGED_WITH_APP + LIST_ITEM_END;
        }
        keyValue.put("tcResolutionLodgedWithApp", tcResolutionLodgedWithApp);

        String tcResolutionLodgedWithAppWelsh = "";
        if (tcResolutionLodgedWithApplicationRule.isApplicable(data)) {
            tcResolutionLodgedWithAppWelsh = LIST_ITEM_START + TC_RESOLUTION_LODGED_WITH_APP_WELSH + LIST_ITEM_END;
        }
        keyValue.put("tcResolutionLodgedWithAppWelsh", tcResolutionLodgedWithAppWelsh);

        String authenticatedTranslation = "";
        if (authenticatedTranslationBusinessRule.isApplicable(data)) {
            authenticatedTranslation = LIST_ITEM_START + AUTHENTICATED_TRANSLATION_WILL_TEXT + LIST_ITEM_END;
        }
        keyValue.put("authenticatedTranslation", authenticatedTranslation);

        String authenticatedTranslationWelsh = "";
        if (authenticatedTranslationBusinessRule.isApplicable(data)) {
            authenticatedTranslationWelsh = LIST_ITEM_START + AUTHENTICATED_TRANSLATION_WILL_TEXT_WELSH + LIST_ITEM_END;
        }
        keyValue.put("authenticatedTranslationWelsh", authenticatedTranslationWelsh);

        String dispenseWithNoticeSupportingDocs = "";
        String dispenseWithNotice = NO;
        if (dispenseNoticeSupportDocsRule.isApplicable(data)) {
            dispenseWithNotice = YES;
            dispenseWithNoticeSupportingDocs = LIST_ITEM_START + DISPENSE_NOTICE_SUPPORT_TEXT
                + data.getDispenseWithNoticeSupportingDocs() + LIST_ITEM_END;
        }
        keyValue.put("dispenseWithNotice", dispenseWithNotice);
        keyValue.put("dispenseWithNoticeSupportingDocs", dispenseWithNoticeSupportingDocs);

        String dispenseWithNoticeSupportingDocsWelsh = "";
        String dispenseWithNoticeWelsh = NO;
        if (dispenseNoticeSupportDocsRule.isApplicable(data)) {
            dispenseWithNoticeWelsh = YES;
            dispenseWithNoticeSupportingDocsWelsh = LIST_ITEM_START + DISPENSE_NOTICE_SUPPORT_TEXT_WELSH
                    + data.getDispenseWithNoticeSupportingDocs() + LIST_ITEM_END;
        }
        keyValue.put("dispenseWithNoticeWelsh", dispenseWithNoticeWelsh);
        keyValue.put("dispenseWithNoticeSupportingDocsWelsh", dispenseWithNoticeSupportingDocsWelsh);
        return keyValue;
    }

    private String getWillCaseTypeLabel(CaseData data) {
        String solsWillType = data.getSolsWillType() == null ? "" : data.getSolsWillType();
        String willHasCodicils = data.getWillHasCodicils() == null ? "" : data.getWillHasCodicils();

        String willText = LIST_ITEM_START + ORIGINAL_WILL_TEXT + LIST_ITEM_END;
        if (solsWillType.equals(GRANT_TYPE_INTESTACY)) {
            willText = "";
        } else if (YES.equals(willHasCodicils)) {
            willText = LIST_ITEM_START + ORIGINAL_WILL_WITH_CODICILS_TEXT + LIST_ITEM_END;
        } else if (YES.equals(data.getWillAccessNotarial())) {
            willText = LIST_ITEM_START + NOTARIAL_COPY_WILL_TEXT + LIST_ITEM_END
                + LIST_ITEM_START + STATEMENT_OF_TRUTH_AND_EXHIBITS_TEXT + LIST_ITEM_END;
        }
        return willText;
    }

    private String getWillCaseTypeLabelWelsh(CaseData data) {
        String solsWillType = data.getSolsWillType() == null ? "" : data.getSolsWillType();
        String willHasCodicils = data.getWillHasCodicils() == null ? "" : data.getWillHasCodicils();

        String willText = LIST_ITEM_START + ORIGINAL_WILL_TEXT_WELSH + LIST_ITEM_END;
        if (solsWillType.equals(GRANT_TYPE_INTESTACY)) {
            willText = "";
        } else if (YES.equals(willHasCodicils)) {
            willText = LIST_ITEM_START + ORIGINAL_WILL_WITH_CODICILS_TEXT_WELSH + LIST_ITEM_END;
        } else if (YES.equals(data.getWillAccessNotarial())) {
            willText = LIST_ITEM_START + NOTARIAL_COPY_WILL_TEXT_WELSH + LIST_ITEM_END
                    + LIST_ITEM_START + STATEMENT_OF_TRUTH_AND_EXHIBITS_TEXT_WELSH + LIST_ITEM_END;
        }
        return willText;
    }

    private String buildPA14NotApplyingExecutorsLinks(CaseData caseData, String reason) {
        List<AdditionalExecutorNotApplying> notApplyingExecs =
            notApplyingExecutorsMapper.getAllExecutorsNotApplying(caseData, reason);
        return notApplyingExecs.stream()
            .map(executor -> buildPA14NotApplyingExecLabel(executor.getNotApplyingExecutorName()))
            .collect(Collectors.joining());
    }

    private String buildPA14NotApplyingExecutorsLinksWelsh(CaseData caseData, String reason) {
        List<AdditionalExecutorNotApplying> notApplyingExecs =
                notApplyingExecutorsMapper.getAllExecutorsNotApplying(caseData, reason);
        return notApplyingExecs.stream()
                .map(executor -> buildPA14NotApplyingExecLabelWelsh(executor.getNotApplyingExecutorName()))
                .collect(Collectors.joining());
    }

    private String buildPA15NotApplyingExecutorsLinks(CaseData caseData, String reason) {
        List<AdditionalExecutorNotApplying> notApplyingExecs =
            notApplyingExecutorsMapper.getAllExecutorsNotApplying(caseData, reason);
        return notApplyingExecs.stream()
            .map(executor -> buildPA15NotApplyingExecLabel(executor.getNotApplyingExecutorName()))
            .collect(Collectors.joining());

    }

    private String buildPA15NotApplyingExecutorsLinksWelsh(CaseData caseData, String reason) {
        List<AdditionalExecutorNotApplying> notApplyingExecs =
                notApplyingExecutorsMapper.getAllExecutorsNotApplying(caseData, reason);
        return notApplyingExecs.stream()
                .map(executor -> buildPA15NotApplyingExecLabelWelsh(executor.getNotApplyingExecutorName()))
                .collect(Collectors.joining());

    }

    private String buildPA15NotApplyingExecLabel(String renouncingExecutorName) {
        return LIST_ITEM_START + sendDocumentsRenderer.getPA15NotApplyingExecutorText(renouncingExecutorName)
            + LIST_ITEM_END;
    }

    private String buildPA15NotApplyingExecLabelWelsh(String renouncingExecutorName) {
        return LIST_ITEM_START + sendDocumentsRenderer.getPA15NotApplyingExecutorTextWelsh(renouncingExecutorName)
                + LIST_ITEM_END;
    }

    private String buildPA14NotApplyingExecLabel(String renouncingExecutorName) {
        return LIST_ITEM_START + sendDocumentsRenderer.getPA14NotApplyingExecutorText(renouncingExecutorName)
            + LIST_ITEM_END;
    }

    private String buildPA14NotApplyingExecLabelWelsh(String renouncingExecutorName) {
        return LIST_ITEM_START + sendDocumentsRenderer.getPA14NotApplyingExecutorTextWelsh(renouncingExecutorName)
                + LIST_ITEM_END;
    }
}
