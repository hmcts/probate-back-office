package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.DocumentType;

import static uk.gov.hmcts.probate.model.Constants.YES;

@Data
@Builder(toBuilder = true)
public class UploadDocument {

    @JsonProperty("DocumentLink")
    private final DocumentLink documentLink;

    @JsonProperty("DocumentType")
    private final DocumentType documentType;

    @JsonProperty("FinalWill")
    private final String finalWill;

    @JsonProperty("Comment")
    private final String comment;

    public boolean isFinalWill() {
        return YES.equalsIgnoreCase(finalWill);
    }
}
