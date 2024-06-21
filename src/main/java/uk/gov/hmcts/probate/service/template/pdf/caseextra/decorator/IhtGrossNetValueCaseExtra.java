package uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
public class IhtGrossNetValueCaseExtra {

    private final BigDecimal grossValue;
    private final BigDecimal netValue;

}
