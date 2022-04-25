package uk.gov.hmcts.probate.model.zip;

import lombok.Builder;
import lombok.Data;
import org.springframework.core.io.ByteArrayResource;

@Data
@Builder
public class ZippedDocumentFile {
    private final ByteArrayResource byteArrayResource;
    private final String caseNumber;
    private final String docType;
    private final String docFileType;
    private final String subType;

    public String getDocumentName() {
        return this.caseNumber + "_" + this.docType
                + (this.subType != null ? "_" + this.subType : "")
                + this.docFileType;
    }

}
