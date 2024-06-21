package uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@AllArgsConstructor
public class RemovePenceDecorator {
    private final CaseExtraDecorator caseExtraDecorator;

    public String decorate(CaseData caseData) {
        String decoration = "";
        IhtGrossNetValueCaseExtra ihtGrossNetValueCaseExtra = IhtGrossNetValueCaseExtra.builder()
                .grossValue(truncateValue(caseData.getIhtGrossValue()))
                .netValue(truncateValue(caseData.getIhtNetValue())).build();
        decoration = caseExtraDecorator.decorate(ihtGrossNetValueCaseExtra);
        return decoration;
    }

    private BigDecimal truncateValue(BigDecimal value) {
        BigDecimal divisor = new BigDecimal("100");
        return value.divide(divisor, RoundingMode.FLOOR);
    }
}
