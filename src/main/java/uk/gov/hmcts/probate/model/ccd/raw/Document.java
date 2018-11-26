package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
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

    @JsonProperty("DocumentDateAdded")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private final LocalDate documentDateAdded;

    @JsonProperty("DocumentGeneratedBy")
    private final String documentGeneratedBy;
}
