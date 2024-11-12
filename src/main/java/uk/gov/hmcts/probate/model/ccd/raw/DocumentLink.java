package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class DocumentLink {

    @JsonProperty(value = "document_url")
    private String documentUrl;

    @JsonProperty(value = "document_binary_url")
    private String documentBinaryUrl;

    @JsonProperty(value = "document_filename")
    private String documentFilename;

    @JsonProperty(value = "document_hash")
    private String documentHash;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty(value = "upload_timestamp")
    private LocalDateTime uploadTimestamp;

}
