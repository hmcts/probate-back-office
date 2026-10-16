package uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.businessrule.IhtEstateNotCompletedBusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.IhtEstateConfirmCaseExtra;

import static uk.gov.hmcts.probate.model.Constants.IHT_ESTATE_CONFIRM;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
@AllArgsConstructor
public class SolicitorLegalStatementPDFDecorator {
    private final CaseExtraDecorator caseExtraDecorator;
    private final IhtEstateNotCompletedBusinessRule ihtEstateNotCompletedBusinessRule;

    public String decorate(CaseData caseData) {
        String decoration = "";
        if (ihtEstateNotCompletedBusinessRule.isApplicable(caseData)) {
            IhtEstateConfirmCaseExtra ihtEstateConfirmCaseExtra = IhtEstateConfirmCaseExtra.builder()
                .showIhtEstate(YES)
                .ihtEstateText(IHT_ESTATE_CONFIRM)
                .build();
            decoration = caseExtraDecorator.decorate(ihtEstateConfirmCaseExtra);
        }
        return decoration;
    }
}
