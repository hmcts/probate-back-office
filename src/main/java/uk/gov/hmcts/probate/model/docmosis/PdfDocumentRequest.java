package uk.gov.hmcts.probate.model.docmosis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

@Value
@Builder
public class PdfDocumentRequest {
    @JsonProperty(value = "accessKey", required = true)
    @NotBlank
    private final String accessKey;
    @JsonProperty(value = "templateName", required = true)
    @NotBlank
    private final String templateName;
    @JsonProperty(value = "outputFormat", required = true)
    @NotBlank
    private final String outputFormat;
    @JsonProperty(value = "outputName", required = true)
    @NotBlank
    private final String outputName;
    @JsonProperty(value = "data", required = true)
    private final Map<String, Object> data;
    @JsonProperty(value = "pdfArchiveMode")
    private final boolean pdfArchiveMode;
}