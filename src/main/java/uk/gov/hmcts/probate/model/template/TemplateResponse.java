package uk.gov.hmcts.probate.model.template;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TemplateResponse {
    private final String template;
}
