package uk.gov.hmcts.probate.service.template.markdown;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.businessrule.AdmonWillRenunicationRule;
import uk.gov.hmcts.probate.businessrule.AuthenticatedTranslationBusinessRule;
import uk.gov.hmcts.probate.businessrule.DispenseNoticeSupportDocsRule;
import uk.gov.hmcts.probate.businessrule.PA14FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA15FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA16FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA17FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.TCResolutionLodgedWithApplicationRule;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.SendDocumentsRenderer;
import uk.gov.hmcts.probate.service.solicitorexecutor.NotApplyingExecutorsMapper;

import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.model.Constants.AUTHENTICATED_TRANSLATION_WILL_TEXT;
import static uk.gov.hmcts.probate.model.Constants.DISPENSE_NOTICE_SUPPORT_TEXT;
import static uk.gov.hmcts.probate.model.Constants.REASON_FOR_NOT_APPLYING_MENTALLY_INCAPABLE;
import static uk.gov.hmcts.probate.model.Constants.REASON_FOR_NOT_APPLYING_RENUNCIATION;
import static uk.gov.hmcts.probate.model.Constants.TC_RESOLUTION_LODGED_WITH_APP;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarkdownDecoratorService {
    public static final String BULLET = "\n*   ";
    private final PA14FormBusinessRule pa14FormBusinessRule;
    private final PA15FormBusinessRule pa15FormBusinessRule;
    private final PA16FormBusinessRule pa16FormBusinessRule;
    private final PA17FormBusinessRule pa17FormBusinessRule;
    private final AuthenticatedTranslationBusinessRule authenticatedTranslationBusinessRule;
    private final AdmonWillRenunicationRule admonWillRenunicationRule;
    private final NotApplyingExecutorsMapper notApplyingExecutorsMapper;
    private final SendDocumentsRenderer sendDocumentsRenderer;
    private final TCResolutionLodgedWithApplicationRule tcResolutionLodgedWithApplicationRule;
    private final DispenseNoticeSupportDocsRule dispenseNoticeSupportDocsRule;

    public String getPA14FormLabel(CaseData caseData) {
        String label = "";
        if (pa14FormBusinessRule.isApplicable(caseData)) {
            List<AdditionalExecutorNotApplying> renouncedExecs = notApplyingExecutorsMapper
                .getAllExecutorsNotApplying(caseData, REASON_FOR_NOT_APPLYING_MENTALLY_INCAPABLE);
            label = renouncedExecs.stream()
                .map(executor -> buildPA14NotApplyingExecLabel(executor.getNotApplyingExecutorName()))
                .collect(Collectors.joining());

        }
        return label;
    }

    public String getPA15FormLabel(CaseData caseData) {
        String label = "";
        if (pa15FormBusinessRule.isApplicable(caseData)) {
            List<AdditionalExecutorNotApplying> renouncedExecs =
                notApplyingExecutorsMapper.getAllExecutorsNotApplying(caseData, REASON_FOR_NOT_APPLYING_RENUNCIATION);
            label = renouncedExecs.stream()
                .map(executor -> buildPA15NotApplyingExecLabel(executor.getNotApplyingExecutorName()))
                .collect(Collectors.joining());

        }
        return label;
    }

    public String getPA16FormLabel(CaseData caseData) {
        if (pa16FormBusinessRule.isApplicable(caseData)) {
            return BULLET + sendDocumentsRenderer.getPA16FormText();
        }
        return "";
    }

    public String getPA17FormLabel(CaseData caseData) {
        if (pa17FormBusinessRule.isApplicable(caseData)) {
            return BULLET + sendDocumentsRenderer.getPA17FormText();
        }
        return "";
    }

    public String getAdmonWillRenunciationFormLabel(CaseData caseData) {
        if (admonWillRenunicationRule.isApplicable(caseData)) {
            return BULLET + sendDocumentsRenderer.getAdmonWillRenunciationText();
        }
        return "";
    }

    private String buildPA15NotApplyingExecLabel(String renouncingExecutorName) {
        return BULLET + sendDocumentsRenderer.getPA15NotApplyingExecutorText(renouncingExecutorName);
    }

    private String buildPA14NotApplyingExecLabel(String renouncingExecutorName) {
        return BULLET + sendDocumentsRenderer.getPA14NotApplyingExecutorText(renouncingExecutorName);
    }


    public String getTcResolutionFormLabel(CaseData caseData) {
        if (tcResolutionLodgedWithApplicationRule.isApplicable(caseData)) {
            return BULLET + TC_RESOLUTION_LODGED_WITH_APP;
        }
        return "";
    }

    public String getAuthenticatedTranslationLabel(CaseData caseData) {
        if (authenticatedTranslationBusinessRule.isApplicable(caseData)) {
            return BULLET + AUTHENTICATED_TRANSLATION_WILL_TEXT;
        }
        return "";
    }

    public String getDispenseWithNoticeSupportDocsLabelAndList(CaseData caseData) {
        if (dispenseNoticeSupportDocsRule.isApplicable(caseData)) {
            return BULLET + DISPENSE_NOTICE_SUPPORT_TEXT + caseData.getDispenseWithNoticeSupportingDocs();
        }
        return "";
    }
}
