package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.DocumentType;

import java.time.LocalDate;

@Data
@Builder
public class Document {

    @JsonProperty("DocumentLink")
    private DocumentLink documentLink;

    @JsonProperty("DocumentType")
    private DocumentType documentType;

    @JsonProperty("DocumentFileName")
    private String documentFileName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("DocumentDateAdded")
    private LocalDate documentDateAdded;

    @JsonProperty("DocumentGeneratedBy")
    private String documentGeneratedBy;

    public Document() {
        super();
    }

    public Document(DocumentLink documentLink, DocumentType documentType, String documentFileName, LocalDate documentDateAdded, String documentGeneratedBy) {
        this.documentLink = documentLink;
        this.documentType = documentType;
        this.documentFileName = documentFileName;
        this.documentDateAdded = documentDateAdded;
        this.documentGeneratedBy = documentGeneratedBy;
    }
}
