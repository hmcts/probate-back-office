package uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.businessrule.AdmonWillRenunicationRule;
import uk.gov.hmcts.probate.businessrule.IhtEstate207BusinessRule;
import uk.gov.hmcts.probate.businessrule.PA16FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA17FormBusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.AdmonWillRenunciationCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.IhtEstate207CaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.PA16FormCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.PA17FormCaseExtra;

import static uk.gov.hmcts.probate.model.Constants.ADMON_WILL_RENUNCIATION_AFTER_LINKS_TEXT;
import static uk.gov.hmcts.probate.model.Constants.ADMON_WILL_RENUNCIATION_BEFORE_LINKS_TEXT;
import static uk.gov.hmcts.probate.model.Constants.ADMON_WILL_RENUNCIATION_MID_LINKS_TEXT;
import static uk.gov.hmcts.probate.model.Constants.IHT_ESTATE_207_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA15_FORM_TEXT_ADMON_WILL;
import static uk.gov.hmcts.probate.model.Constants.PA15_FORM_URL;
import static uk.gov.hmcts.probate.model.Constants.PA16_FORM_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA16_FORM_URL;
import static uk.gov.hmcts.probate.model.Constants.PA17_FORM_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA17_FORM_TEXT_ADMON_WILL;
import static uk.gov.hmcts.probate.model.Constants.PA17_FORM_URL;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
@AllArgsConstructor
public class SolicitorCoversheetPDFDecorator {
    private final CaseExtraDecorator caseExtraDecorator;
    private final PA16FormBusinessRule pa16FormBusinessRule;
    private final PA17FormBusinessRule pa17FormBusinessRule;
    private final IhtEstate207BusinessRule ihtEstate207BusinessRule;
    private final AdmonWillRenunicationRule admonWillRenunicationRule;

    public String decorate(CaseData caseData) {
        String decoration = "";
        if (pa16FormBusinessRule.isApplicable(caseData)) {
            PA16FormCaseExtra pa16FormCaseExtra = PA16FormCaseExtra.builder()
                .pa16FormText(PA16_FORM_TEXT)
                .pa16FormUrl(PA16_FORM_URL)
                .showPa16Form(YES)
                .build();
            decoration = caseExtraDecorator.decorate(pa16FormCaseExtra);
        }  
        if (pa17FormBusinessRule.isApplicable(caseData)) {
            PA17FormCaseExtra pa17FormCaseExtra = PA17FormCaseExtra.builder()
                .pa17FormText(PA17_FORM_TEXT)
                .pa17FormUrl(PA17_FORM_URL)
                .showPa17Form(YES)
                .build();
            decoration =  caseExtraDecorator.decorate(pa17FormCaseExtra);
        } 
        if (ihtEstate207BusinessRule.isApplicable(caseData)) {
            IhtEstate207CaseExtra ihtEstate207CaseExtra = IhtEstate207CaseExtra.builder()
                .ihtEstate207Text(IHT_ESTATE_207_TEXT)
                .showIhtEstate(YES)
                .build();
            decoration = caseExtraDecorator.combineDecorations(decoration,
                caseExtraDecorator.decorate(ihtEstate207CaseExtra));
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
        return decoration;
    }
}
