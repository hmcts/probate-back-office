package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentLink {

    @JsonProperty(value = "document_url")
    private String documentUrl;

    @JsonProperty(value = "document_binary_url")
    private String documentBinaryUrl;

    @JsonProperty(value = "document_filename")
    private String documentFilename;

    public DocumentLink() {
        super();
    }

    public DocumentLink(String documentUrl, String documentBinaryUrl, String documentFilename) {
        this.documentUrl = documentUrl;
        this.documentBinaryUrl = documentBinaryUrl;
        this.documentFilename = documentFilename;
    }
}
