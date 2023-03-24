package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OriginalDocuments {

    private List<CollectionMember<Document>> originalDocsGenerated;
    private List<CollectionMember<ScannedDocument>> originalDocsScanned;
    private List<CollectionMember<UploadDocument>> originalDocsUploaded;
}
