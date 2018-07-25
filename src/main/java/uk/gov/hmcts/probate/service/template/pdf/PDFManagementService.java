package uk.gov.hmcts.probate.service.template.pdf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.exception.ConnectionException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFile;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.service.evidencemanagement.upload.UploadService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDate;

@Data
@Service
public class PDFManagementService {
    private final PDFServiceConfiguration pdfServiceConfiguration;
    private final PDFGeneratorService pdfGeneratorService;
    private final UploadService uploadService;
    private final ObjectMapper objectMapper;
    private final HttpServletRequest httpServletRequest;

    private static final Logger log = LoggerFactory.getLogger(PDFManagementService.class);

    public Document generateAndUpload(CallbackRequest callbackRequest, DocumentType documentType) {
        try {
            String json = objectMapper.writeValueAsString(callbackRequest);
            EvidenceManagementFileUpload fileUpload = pdfGeneratorService.generatePdf(documentType, json);
            EvidenceManagementFile store = uploadService.store(fileUpload);
            DocumentLink documentLink = DocumentLink.builder()
                    .documentBinaryUrl(store.getLink("binary").getHref())
                    .documentUrl(store.getLink(Link.REL_SELF).getHref())
                    .documentFilename(documentType.getTemplateName() + ".pdf")
                    .build();

            return Document.builder()
                    .documentLink(documentLink)
                    .documentType(documentType)
                    .documentDateAdded(LocalDate.now())
                    .documentGeneratedBy(httpServletRequest.getHeader("user-id"))
                    .build();

        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new BadRequestException(e.getMessage(), null);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ConnectionException(e.getMessage());
        }
    }
}
