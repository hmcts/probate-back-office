package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.DocumentType;

@Data
@Builder
public class UploadDocument {

    @JsonProperty("DocumentLink")
    private final DocumentLink documentLink;

    @JsonProperty("DocumentType")
    private final DocumentType documentType;

    @JsonProperty("Comment")
    private final String comment;
}
