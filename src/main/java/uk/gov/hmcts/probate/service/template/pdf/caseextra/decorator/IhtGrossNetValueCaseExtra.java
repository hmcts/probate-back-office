package uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
public class IhtGrossNetValueCaseExtra {

    private final String grossValue;
    private final String netValue;

}