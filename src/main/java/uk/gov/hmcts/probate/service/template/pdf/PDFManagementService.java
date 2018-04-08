package uk.gov.hmcts.probate.service.template.pdf;

import lombok.Data;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.CCDDocument;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFile;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.model.template.PDFServiceTemplate;
import uk.gov.hmcts.probate.service.evidencemanagement.upload.UploadService;

import java.io.IOException;

@Data
@Service
public class PDFManagementService {
    private final PDFGeneratorService pdfGeneratorService;
    private final UploadService uploadService;

    public CCDDocument generateAndUpload(PDFServiceTemplate pdfServiceTemplate, String json) throws IOException {
        EvidenceManagementFileUpload fileUpload = pdfGeneratorService.generatePdf(pdfServiceTemplate, json);
        EvidenceManagementFile store = uploadService.store(fileUpload);
        return CCDDocument.builder()
            .documentBinaryUrl(store.getLink("binary").getHref())
            .documentUrl(store.getLink(Link.REL_SELF).getHref())
            .documentFilename(store.getOriginalDocumentName())
            .build();
    }
}
