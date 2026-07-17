package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.FieldType;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OriginalDocuments {

    @CCD(label = "Documents generated", typeOverride = FieldType.Collection, typeParameterOverride = "ProbateDocument")
    private List<CollectionMember<Document>> originalDocsGenerated;
    @CCD(label = "Documents scanned")
    private List<CollectionMember<ScannedDocument>> originalDocsScanned;
    @CCD(label = "Documents uploaded", typeOverride = FieldType.Collection, typeParameterOverride = "documentUpload")
    private List<CollectionMember<UploadDocument>> originalDocsUploaded;
}
