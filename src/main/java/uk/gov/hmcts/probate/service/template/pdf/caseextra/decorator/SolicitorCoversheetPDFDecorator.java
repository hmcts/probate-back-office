package uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.businessrule.AuthenticatedTranslationBusinessRule;
import uk.gov.hmcts.probate.businessrule.AdmonWillRenunicationRule;
import uk.gov.hmcts.probate.businessrule.IhtEstate207BusinessRule;
import uk.gov.hmcts.probate.businessrule.PA14FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA15FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA16FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA17FormBusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.businessrule.TCResolutionLodgedWithApplicationRule;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.AuthenticatedTranslationCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.AdmonWillRenunciationCaseExtra;
import uk.gov.hmcts.probate.service.solicitorexecutor.NotApplyingExecutorsMapper;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.IhtEstate207CaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.NotApplyingExecutorFormPoint;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.PA14FormCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.PA15FormCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.PA16FormCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.PA17FormCaseExtra;
import static uk.gov.hmcts.probate.model.Constants.AUTHENTICATED_TRANSLATION_WILL_TEXT;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.TCResolutionLodgedWithAppCaseExtra;

import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.model.Constants.ADMON_WILL_RENUNCIATION_AFTER_LINKS_TEXT;
import static uk.gov.hmcts.probate.model.Constants.ADMON_WILL_RENUNCIATION_BEFORE_LINKS_TEXT;
import static uk.gov.hmcts.probate.model.Constants.ADMON_WILL_RENUNCIATION_MID_LINKS_TEXT;
import static uk.gov.hmcts.probate.model.Constants.IHT_ESTATE_207_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA14_FORM_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA14_FORM_URL;
import static uk.gov.hmcts.probate.model.Constants.PA15_FORM_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA15_FORM_URL;
import static uk.gov.hmcts.probate.model.Constants.PA15_FORM_TEXT_ADMON_WILL;
import static uk.gov.hmcts.probate.model.Constants.PA16_FORM_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA16_FORM_URL;
import static uk.gov.hmcts.probate.model.Constants.PA17_FORM_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA17_FORM_TEXT_ADMON_WILL;
import static uk.gov.hmcts.probate.model.Constants.PA17_FORM_URL;
import static uk.gov.hmcts.probate.model.Constants.REASON_FOR_NOT_APPLYING_MENTALLY_INCAPABLE;
import static uk.gov.hmcts.probate.model.Constants.REASON_FOR_NOT_APPLYING_RENUNCIATION;
import static uk.gov.hmcts.probate.model.Constants.TC_RESOLUTION_LODGED_WITH_APP;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
@AllArgsConstructor
public class SolicitorCoversheetPDFDecorator {
    private final CaseExtraDecorator caseExtraDecorator;
    private final PA14FormBusinessRule pa14FormBusinessRule;
    private final PA15FormBusinessRule pa15FormBusinessRule;
    private final PA16FormBusinessRule pa16FormBusinessRule;
    private final PA17FormBusinessRule pa17FormBusinessRule;
    private final IhtEstate207BusinessRule ihtEstate207BusinessRule;
    private final AuthenticatedTranslationBusinessRule authenticatedTranslationBusinessRule;
    private final AdmonWillRenunicationRule admonWillRenunicationRule;
    private final NotApplyingExecutorsMapper notApplyingExecutorsMapper;
    private final TCResolutionLodgedWithApplicationRule tcResolutionLodgedWithApplicationRule;

    public String decorate(CaseData caseData) {
        String decoration = "";
        if (pa14FormBusinessRule.isApplicable(caseData)) {
            PA14FormCaseExtra pa14FormCaseExtra = PA14FormCaseExtra.builder()
                .notApplyingExecutorFormPoints(buildNotApplyingExecutorsLinks(caseData,
                    REASON_FOR_NOT_APPLYING_MENTALLY_INCAPABLE, PA14_FORM_URL, PA14_FORM_TEXT))
                .showPa14Form(YES)
                .build();
            decoration = caseExtraDecorator.combineDecorations(decoration,
                caseExtraDecorator.decorate(pa14FormCaseExtra));
        }
        if (pa15FormBusinessRule.isApplicable(caseData)) {
            PA15FormCaseExtra pa15FormCaseExtra = PA15FormCaseExtra.builder()
                .notApplyingExecutorFormPoints(buildNotApplyingExecutorsLinks(caseData,
                    REASON_FOR_NOT_APPLYING_RENUNCIATION, PA15_FORM_URL, PA15_FORM_TEXT))
                .showPa15Form(YES)
                .build();
            decoration = caseExtraDecorator.combineDecorations(decoration,
                caseExtraDecorator.decorate(pa15FormCaseExtra));
        }
        if (pa16FormBusinessRule.isApplicable(caseData)) {
            PA16FormCaseExtra pa16FormCaseExtra = PA16FormCaseExtra.builder()
                .pa16FormText(PA16_FORM_TEXT)
                .pa16FormUrl(PA16_FORM_URL)
                .showPa16Form(YES)
                .build();
            decoration = caseExtraDecorator.combineDecorations(decoration,
                caseExtraDecorator.decorate(pa16FormCaseExtra));
        }
        if (pa17FormBusinessRule.isApplicable(caseData)) {
            PA17FormCaseExtra pa17FormCaseExtra = PA17FormCaseExtra.builder()
                .pa17FormText(PA17_FORM_TEXT)
                .pa17FormUrl(PA17_FORM_URL)
                .showPa17Form(YES)
                .build();
            decoration = caseExtraDecorator.combineDecorations(decoration,
                caseExtraDecorator.decorate(pa17FormCaseExtra));
        }
        if (ihtEstate207BusinessRule.isApplicable(caseData)) {
            IhtEstate207CaseExtra ihtEstate207CaseExtra = IhtEstate207CaseExtra.builder()
                .ihtEstate207Text(IHT_ESTATE_207_TEXT)
                .showIhtEstate(YES)
                .build();
            decoration = caseExtraDecorator.combineDecorations(decoration,
                caseExtraDecorator.decorate(ihtEstate207CaseExtra));
        }
        if (authenticatedTranslationBusinessRule.isApplicable(caseData)) {
            AuthenticatedTranslationCaseExtra authenticatedTranslationCaseExtra =
                    AuthenticatedTranslationCaseExtra.builder()
                    .authenticatedTranslationText(AUTHENTICATED_TRANSLATION_WILL_TEXT)
                    .showAuthenticatedTranslation(YES)
                    .build();
            decoration = caseExtraDecorator.combineDecorations(decoration,
                    caseExtraDecorator.decorate(authenticatedTranslationCaseExtra));
        }
        if (admonWillRenunicationRule.isApplicable(caseData)) {
            AdmonWillRenunciationCaseExtra admonWillRenunciationCaseExtra = AdmonWillRenunciationCaseExtra.builder()
                .admonWillRenunciationBeforeLinksText(ADMON_WILL_RENUNCIATION_BEFORE_LINKS_TEXT)
                .admonWillRenunciationMidLinksText(ADMON_WILL_RENUNCIATION_MID_LINKS_TEXT)
                .admonWillRenunciationAfterLinksText(ADMON_WILL_RENUNCIATION_AFTER_LINKS_TEXT)
                .pa15FormText(PA15_FORM_TEXT_ADMON_WILL)
                .pa17FormText(PA17_FORM_TEXT_ADMON_WILL)
                .pa15FormUrl(PA15_FORM_URL)
                .pa17FormUrl(PA17_FORM_URL)
                .showAdmonWillRenunciation(YES)
                .build();
            decoration = caseExtraDecorator.combineDecorations(decoration,
                caseExtraDecorator.decorate(admonWillRenunciationCaseExtra));
        }
        if (tcResolutionLodgedWithApplicationRule.isApplicable(caseData)) {
            TCResolutionLodgedWithAppCaseExtra tcResolutionLodgedWithAppCaseExtra = TCResolutionLodgedWithAppCaseExtra
                .builder()
                .showTcResolutionLodgedWithApp(YES)
                .tcResolutionLodgedWithAppText(TC_RESOLUTION_LODGED_WITH_APP)
                .build();
            decoration = caseExtraDecorator.combineDecorations(decoration,
                caseExtraDecorator.decorate(tcResolutionLodgedWithAppCaseExtra));
        }
        return decoration;
    }

    private List<NotApplyingExecutorFormPoint> buildNotApplyingExecutorsLinks(CaseData caseData,
                                                                              String notApplyingReason, String formURL,
                                                                              String formText) {
        List<AdditionalExecutorNotApplying> renouncedExecs =
            notApplyingExecutorsMapper.getAllExecutorsNotApplying(caseData, notApplyingReason);
        return renouncedExecs.stream()
            .map(executor -> buildNotApplyingExececutorsLabel(executor.getNotApplyingExecutorName(), formURL, formText))
            .collect(Collectors.toList());

    }

    private NotApplyingExecutorFormPoint buildNotApplyingExececutorsLabel(String renouncingExecutorName, String formURL,
                                                                          String formText) {
        return NotApplyingExecutorFormPoint.builder()
            .url(formURL)
            .text(formText)
            .executor(renouncingExecutorName)
            .build();
    }

}
