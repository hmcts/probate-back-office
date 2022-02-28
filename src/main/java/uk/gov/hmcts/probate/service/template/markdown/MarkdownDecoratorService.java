package uk.gov.hmcts.probate.service.template.markdown;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.businessrule.AdmonWillRenunicationRule;
import uk.gov.hmcts.probate.businessrule.PA14FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA15FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA16FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA17FormBusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.SendDocumentsRenderer;
import uk.gov.hmcts.probate.service.solicitorexecutor.NotApplyingExecutorsMapper;

import static uk.gov.hmcts.probate.model.Constants.ADMON_WILL_RENUNCIATION_AFTER_LINKS_TEXT;
import static uk.gov.hmcts.probate.model.Constants.ADMON_WILL_RENUNCIATION_BEFORE_LINKS_TEXT;
import static uk.gov.hmcts.probate.model.Constants.ADMON_WILL_RENUNCIATION_MID_LINKS_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA15_FORM_TEXT_ADMON_WILL;
import static uk.gov.hmcts.probate.model.Constants.PA15_FORM_URL;
import static uk.gov.hmcts.probate.model.Constants.PA17_FORM_TEXT_ADMON_WILL;
import static uk.gov.hmcts.probate.model.Constants.PA17_FORM_URL;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.model.Constants.REASON_FOR_NOT_APPLYING_MENTALLY_INCAPABLE;
import static uk.gov.hmcts.probate.model.Constants.REASON_FOR_NOT_APPLYING_RENUNCIATION;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarkdownDecoratorService {
    public static final String BULLET = "\n*   ";
    private final PA14FormBusinessRule pa14FormBusinessRule;
    private final PA15FormBusinessRule pa15FormBusinessRule;
    private final PA16FormBusinessRule pa16FormBusinessRule;
    private final PA17FormBusinessRule pa17FormBusinessRule;
    private final AdmonWillRenunicationRule admonWillRenunicationRule;
    private final NotApplyingExecutorsMapper notApplyingExecutorsMapper;
    private final SendDocumentsRenderer sendDocumentsRenderer;

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
            return "\n*   " + ADMON_WILL_RENUNCIATION_BEFORE_LINKS_TEXT + "<a href=\"" + PA15_FORM_URL
                + "\" target=\"_blank\">" + PA15_FORM_TEXT_ADMON_WILL + "</a>" + ADMON_WILL_RENUNCIATION_MID_LINKS_TEXT
                + "<a href=\"" + PA17_FORM_URL + "\" target=\"_blank\">" + PA17_FORM_TEXT_ADMON_WILL + "</a>"
                + ADMON_WILL_RENUNCIATION_AFTER_LINKS_TEXT;
        }
        return "";
    }

    private String buildPA15NotApplyingExecLabel(String renouncingExecutorName) {
        return BULLET + sendDocumentsRenderer.getPA15NotApplyingExecutorText(renouncingExecutorName);
    }

    private String buildPA14NotApplyingExecLabel(String renouncingExecutorName) {
        return BULLET + sendDocumentsRenderer.getPA14NotApplyingExecutorText(renouncingExecutorName);
    }

}
