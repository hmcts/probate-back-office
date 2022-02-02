package uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.businessrule.IhtEstate207BusinessRule;
import uk.gov.hmcts.probate.businessrule.PA15FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA16FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA17FormBusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.SendDocumentsRenderer;
import uk.gov.hmcts.probate.service.solicitorexecutor.RenouncingExecutorsMapper;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.IhtEstate207CaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.PA15FormCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.PA16FormCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.PA17FormCaseExtra;

import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.model.Constants.IHT_ESTATE_207_TEXT;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
@AllArgsConstructor
public class SolicitorCoversheetPDFDecorator {
    private final CaseExtraDecorator caseExtraDecorator;
    private final PA15FormBusinessRule pa15FormBusinessRule;
    private final PA16FormBusinessRule pa16FormBusinessRule;
    private final PA17FormBusinessRule pa17FormBusinessRule;
    private final IhtEstate207BusinessRule ihtEstate207BusinessRule;
    private final RenouncingExecutorsMapper renouncingExecutorsMapper;
    private final SendDocumentsRenderer sendDocumentsRenderer;

    public String decorate(CaseData caseData) {
        String decoration = "";
        if (pa15FormBusinessRule.isApplicable(caseData)) {
            PA15FormCaseExtra pa15FormCaseExtra = PA15FormCaseExtra.builder()
                .pa15FormText(buildPA15RenouncingExecutorsLinks(caseData))
                .showPa15Form(YES)
                .build();
            decoration = caseExtraDecorator.combineDecorations(decoration,
                caseExtraDecorator.decorate(pa15FormCaseExtra));
        }
        if (pa16FormBusinessRule.isApplicable(caseData)) {
            PA16FormCaseExtra pa16FormCaseExtra = PA16FormCaseExtra.builder()
                .pa16FormText(sendDocumentsRenderer.getPA16FormText())
                .showPa16Form(YES)
                .build();
            decoration = caseExtraDecorator.combineDecorations(decoration,
                caseExtraDecorator.decorate(pa16FormCaseExtra));
        }
        if (pa17FormBusinessRule.isApplicable(caseData)) {
            PA17FormCaseExtra pa17FormCaseExtra = PA17FormCaseExtra.builder()
                .pa17FormText(sendDocumentsRenderer.getPA17FormText())
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

    private String buildPA15RenouncingExecutorsLinks(CaseData caseData) {
        List<AdditionalExecutorNotApplying> renouncedExecs =
            renouncingExecutorsMapper.getAllRenouncingExecutors(caseData);
        return renouncedExecs.stream()
            .map(executor -> buildRenouncingExecLabel(executor.getNotApplyingExecutorName()))
            .collect(Collectors.joining());

    }

    private String buildRenouncingExecLabel(String renouncingExecutorName) {
        return "<li>" + sendDocumentsRenderer.getSingleRenouncingExecutorText(renouncingExecutorName) + "</li>";
    }


}
