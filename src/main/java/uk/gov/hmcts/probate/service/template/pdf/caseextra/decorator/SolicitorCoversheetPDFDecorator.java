package uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.businessrule.IhtEstate207BusinessRule;
import uk.gov.hmcts.probate.businessrule.PA14FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA15FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA16FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA17FormBusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.solicitorexecutor.NotApplyingExecutorsMapper;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.IhtEstate207CaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.NotApplyingExecutorFormPoint;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.PA14FormCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.PA15FormCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.PA16FormCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.PA17FormCaseExtra;

import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.model.Constants.IHT_ESTATE_207_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA14_FORM_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA14_FORM_URL;
import static uk.gov.hmcts.probate.model.Constants.PA15_FORM_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA15_FORM_URL;
import static uk.gov.hmcts.probate.model.Constants.PA16_FORM_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA16_FORM_URL;
import static uk.gov.hmcts.probate.model.Constants.PA17_FORM_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA17_FORM_URL;
import static uk.gov.hmcts.probate.model.Constants.REASON_FOR_NOT_APPLYING_MENTALLY_INCAPABLE;
import static uk.gov.hmcts.probate.model.Constants.REASON_FOR_NOT_APPLYING_RENUNCIATION;
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
    private final NotApplyingExecutorsMapper notApplyingExecutorsMapper;

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
