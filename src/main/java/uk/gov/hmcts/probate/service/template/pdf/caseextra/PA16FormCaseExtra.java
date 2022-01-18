package uk.gov.hmcts.probate.service.template.pdf.caseextra;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
public class PA16FormCaseExtra {

    private final String showPa16Form;
    private final String pa16FormUrl;
    private final String pa16FormText;

}
