package uk.gov.hmcts.probate.model.zip;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ZippedManifestData {
    private final String documentId;
    private final String caseNumber;
    private final String docType;
    private final String docFileType;
    private final String subType;
    private final String caseType;
    private String errorDescription;

    public String getDocumentName() {
        return this.caseNumber + "_" + this.docType
                + (this.subType != null ? "_" + this.subType : "")
                + this.docFileType;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

}
