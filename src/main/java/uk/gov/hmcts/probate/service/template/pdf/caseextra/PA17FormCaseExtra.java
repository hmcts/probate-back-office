package uk.gov.hmcts.probate.service.template.pdf.caseextra;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
public class PA17FormCaseExtra {

    @JsonProperty(value = "showPa17Form")
    private final String showPa17Form;
    @JsonProperty(value = "pa17FormUrl")
    private final String pa17FormUrl;
    @JsonProperty(value = "pa17FormText")
    private final String pa17FormText;

}
