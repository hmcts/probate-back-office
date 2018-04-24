package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CCDDocument {

    @JsonProperty(value = "document_url")
    private final String documentUrl;

    @JsonProperty(value = "document_binary_url")
    private final String documentBinaryUrl;

    @JsonProperty(value = "document_filename")
    private final String documentFilename;
}
