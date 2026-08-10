package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.FieldType;
import uk.gov.hmcts.ccd.sdk.api.ComplexType;

@ComplexType(name = "documentUpload", generate = true)
@Data
@Builder
public class UploadDocument {

    @CCD(label = "Document Url", typeOverride = FieldType.Document)
    @JsonProperty("DocumentLink")
    private final DocumentLink documentLink;

    @CCD(label = "Type", typeOverride = FieldType.FixedList, typeParameterOverride = "documentUploadTypeEnum")
    @JsonProperty("DocumentType")
    private final DocumentType documentType;

    @CCD(label = "Comment", typeOverride = FieldType.TextArea)
    @JsonProperty("Comment")
    private final String comment;
}
