package uk.gov.hmcts.probate.service.template.markdown;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.businessrule.AdmonWillRenunicationRule;
import uk.gov.hmcts.probate.businessrule.AuthenticatedTranslationBusinessRule;
import uk.gov.hmcts.probate.businessrule.DispenseNoticeSupportDocsRule;
import uk.gov.hmcts.probate.businessrule.NotarialWillBusinessRule;
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
import static uk.gov.hmcts.probate.model.Constants.AUTHENTICATED_TRANSLATION_WILL_TEXT_WELSH;
import static uk.gov.hmcts.probate.model.Constants.DISPENSE_NOTICE_SUPPORT_TEXT;
import static uk.gov.hmcts.probate.model.Constants.DISPENSE_NOTICE_SUPPORT_TEXT_WELSH;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_INTESTACY;
import static uk.gov.hmcts.probate.model.Constants.NOTARIAL_COPY_WILL_TEXT;
import static uk.gov.hmcts.probate.model.Constants.NOTARIAL_COPY_WILL_TEXT_WELSH;
import static uk.gov.hmcts.probate.model.Constants.ORIGINAL_WILL_TEXT;
import static uk.gov.hmcts.probate.model.Constants.ORIGINAL_WILL_TEXT_WELSH;
import static uk.gov.hmcts.probate.model.Constants.ORIGINAL_WILL_WITH_CODICILS_TEXT;
import static uk.gov.hmcts.probate.model.Constants.ORIGINAL_WILL_WITH_CODICILS_TEXT_WELSH;
import static uk.gov.hmcts.probate.model.Constants.REASON_FOR_NOT_APPLYING_MENTALLY_INCAPABLE;
import static uk.gov.hmcts.probate.model.Constants.REASON_FOR_NOT_APPLYING_RENUNCIATION;
import static uk.gov.hmcts.probate.model.Constants.STATEMENT_OF_TRUTH_AND_EXHIBITS_TEXT;
import static uk.gov.hmcts.probate.model.Constants.STATEMENT_OF_TRUTH_AND_EXHIBITS_TEXT_WELSH;
import static uk.gov.hmcts.probate.model.Constants.TC_RESOLUTION_LODGED_WITH_APP;
import static uk.gov.hmcts.probate.model.Constants.TC_RESOLUTION_LODGED_WITH_APP_WELSH;
import static uk.gov.hmcts.probate.model.Constants.YES;

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
    private final NotarialWillBusinessRule notarialWillBusinessRule;

    public String getPA14FormLabel(CaseData caseData, boolean isWelsh) {
        String label = "";
        if (pa14FormBusinessRule.isApplicable(caseData)) {
            List<AdditionalExecutorNotApplying> renouncedExecs = notApplyingExecutorsMapper
                .getAllExecutorsNotApplying(caseData, REASON_FOR_NOT_APPLYING_MENTALLY_INCAPABLE);
            label = renouncedExecs.stream()
                .map(executor -> buildPA14NotApplyingExecLabel(executor.getNotApplyingExecutorName(),isWelsh))
                .collect(Collectors.joining());

        }
        return label;
    }

    public String getPA15FormLabel(CaseData caseData, boolean isWelsh) {
        String label = "";
        if (pa15FormBusinessRule.isApplicable(caseData)) {
            List<AdditionalExecutorNotApplying> renouncedExecs =
                notApplyingExecutorsMapper.getAllExecutorsNotApplying(caseData, REASON_FOR_NOT_APPLYING_RENUNCIATION);
            label = renouncedExecs.stream()
                .map(executor -> buildPA15NotApplyingExecLabel(executor.getNotApplyingExecutorName(),isWelsh))
                .collect(Collectors.joining());

        }
        return label;
    }

    public String getPA16FormLabel(CaseData caseData, boolean isWelsh) {
        if (pa16FormBusinessRule.isApplicable(caseData)) {
            return BULLET + (isWelsh ? sendDocumentsRenderer.getPA16FormTextWelsh() :
                    sendDocumentsRenderer.getPA16FormText());
        }
        return "";
    }

    public String getPA17FormLabel(CaseData caseData, boolean isWelsh) {
        if (pa17FormBusinessRule.isApplicable(caseData)) {
            return BULLET + (isWelsh ? sendDocumentsRenderer.getPA17FormTextWelsh() :
                    sendDocumentsRenderer.getPA17FormText());
        }
        return "";
    }

    public String getAdmonWillRenunciationFormLabel(CaseData caseData, boolean isWelsh) {
        if (admonWillRenunicationRule.isApplicable(caseData)) {
            return BULLET + (isWelsh ? sendDocumentsRenderer.getAdmonWillRenunciationTextWelsh() :
                    sendDocumentsRenderer.getAdmonWillRenunciationText());
        }
        return "";
    }

    private String buildPA15NotApplyingExecLabel(String renouncingExecutorName, boolean isWelsh) {
        return BULLET + (isWelsh ? sendDocumentsRenderer.getPA15NotApplyingExecutorTextWelsh(renouncingExecutorName) :
                sendDocumentsRenderer.getPA15NotApplyingExecutorText(renouncingExecutorName));
    }

    private String buildPA14NotApplyingExecLabel(String renouncingExecutorName, boolean isWelsh) {
        return BULLET + (isWelsh ? sendDocumentsRenderer.getPA14NotApplyingExecutorTextWelsh(renouncingExecutorName) :
                sendDocumentsRenderer.getPA14NotApplyingExecutorText(renouncingExecutorName));
    }


    public String getTcResolutionFormLabel(CaseData caseData, boolean isWelsh) {
        if (tcResolutionLodgedWithApplicationRule.isApplicable(caseData)) {
            return BULLET + (isWelsh ? TC_RESOLUTION_LODGED_WITH_APP_WELSH : TC_RESOLUTION_LODGED_WITH_APP);
        }
        return "";
    }

    public String getAuthenticatedTranslationLabel(CaseData caseData, boolean isWelsh) {
        if (authenticatedTranslationBusinessRule.isApplicable(caseData)) {
            return BULLET + (isWelsh ? AUTHENTICATED_TRANSLATION_WILL_TEXT_WELSH : AUTHENTICATED_TRANSLATION_WILL_TEXT);
        }
        return "";
    }

    public String getDispenseWithNoticeSupportDocsLabelAndList(CaseData caseData, boolean isWelsh) {
        if (dispenseNoticeSupportDocsRule.isApplicable(caseData)) {
            return BULLET + (isWelsh
                    ? DISPENSE_NOTICE_SUPPORT_TEXT_WELSH + caseData.getDispenseWithNoticeSupportingDocs()
                    : DISPENSE_NOTICE_SUPPORT_TEXT + caseData.getDispenseWithNoticeSupportingDocs());
        }
        return "";
    }

    public String getWillLabel(CaseData caseData) {
        String solsWillType = caseData.getSolsWillType();
        if (GRANT_TYPE_INTESTACY.equals(solsWillType)) {
            return "";
        }
        if (notarialWillBusinessRule.isApplicable(caseData)) {
            return BULLET + NOTARIAL_COPY_WILL_TEXT
                + BULLET + STATEMENT_OF_TRUTH_AND_EXHIBITS_TEXT;
        } else if (YES.equals(caseData.getWillHasCodicils())) {
            return BULLET + ORIGINAL_WILL_WITH_CODICILS_TEXT;
        } else {
            return BULLET + ORIGINAL_WILL_TEXT;
        }
    }

    public String getWillLabelWelsh(CaseData caseData) {
        String solsWillType = caseData.getSolsWillType();
        if (GRANT_TYPE_INTESTACY.equals(solsWillType)) {
            return "";
        }
        if (notarialWillBusinessRule.isApplicable(caseData)) {
            return BULLET + NOTARIAL_COPY_WILL_TEXT_WELSH
                    + BULLET + STATEMENT_OF_TRUTH_AND_EXHIBITS_TEXT_WELSH;
        } else if (YES.equals(caseData.getWillHasCodicils())) {
            return BULLET + ORIGINAL_WILL_WITH_CODICILS_TEXT_WELSH;
        } else {
            return BULLET + ORIGINAL_WILL_TEXT_WELSH;
        }
    }
}
