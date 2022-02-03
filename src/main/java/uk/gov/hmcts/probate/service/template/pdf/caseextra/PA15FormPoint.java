package uk.gov.hmcts.probate.service.template.pdf.caseextra;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
public class PA15FormPoint {

    @JsonProperty(value = "url")
    private final String url;
    @JsonProperty(value = "text")
    private final String text;
    @JsonProperty(value = "executor")
    private final String executor;

}
