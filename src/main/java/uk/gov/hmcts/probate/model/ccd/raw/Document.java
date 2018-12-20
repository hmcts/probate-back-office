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
    private final DocumentLink documentLink;

    @JsonProperty("DocumentType")
    private final DocumentType documentType;

    @JsonProperty("DocumentFileName")
    private final String documentFileName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("DocumentDateAdded")
    private final LocalDate documentDateAdded;

    @JsonProperty("DocumentGeneratedBy")
    private final String documentGeneratedBy;
}
