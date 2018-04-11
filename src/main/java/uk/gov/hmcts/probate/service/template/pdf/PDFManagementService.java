package uk.gov.hmcts.probate.service.template.pdf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.exception.ConnectionException;
import uk.gov.hmcts.probate.model.ccd.raw.CCDDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
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
    private final ObjectMapper objectMapper;


    public CCDDocument generateAndUpload(CallbackRequest callbackRequest, PDFServiceTemplate pdfServiceTemplate) {
        try {
            String json = objectMapper.writeValueAsString(callbackRequest);
            EvidenceManagementFileUpload fileUpload = pdfGeneratorService.generatePdf(pdfServiceTemplate, json);
            EvidenceManagementFile store = uploadService.store(fileUpload);
            return CCDDocument.builder()
                    .documentBinaryUrl(store.getLink("binary").getHref())
                    .documentUrl(store.getLink(Link.REL_SELF).getHref())
                    .documentFilename(store.getOriginalDocumentName())
                    .build();
        } catch (JsonProcessingException e) {
            throw new BadRequestException(e.getMessage(), null);
        } catch (IOException e) {
            throw new ConnectionException(e.getMessage());
        }
    }
}
