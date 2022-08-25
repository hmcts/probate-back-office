package uk.gov.hmcts.probate.model.zip;

import lombok.Builder;
import lombok.Data;
import org.springframework.core.io.ByteArrayResource;

@Data
@Builder
public class ZippedDocumentFile {
    private final ByteArrayResource byteArrayResource;
    private final ZippedManifestData zippedManifestData;
}
