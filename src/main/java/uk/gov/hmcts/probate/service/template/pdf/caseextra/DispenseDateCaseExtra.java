package uk.gov.hmcts.probate.service.template.pdf.caseextra;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
public class DispenseDateCaseExtra {

    private final String showDispenseDate;
    private final String dispenseDateWelshFormatted;

}
