package uk.gov.hmcts.probate.service.template.pdf.caseextra;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
public class PA16FormCaseExtra {

    @JsonProperty(value = "showPa16Form")
    private final String showPa16Form;
    @JsonProperty(value = "pa16FormUrl")
    private final String pa16FormUrl;
    @JsonProperty(value = "pa16FormText")
    private final String pa16FormText;

}
