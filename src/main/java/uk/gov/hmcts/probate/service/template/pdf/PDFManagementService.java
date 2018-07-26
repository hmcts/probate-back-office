package uk.gov.hmcts.probate.service.template.pdf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.exception.ConnectionException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.BigDecimalNumberSerializer;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFile;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.service.evidencemanagement.upload.UploadService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class PDFManagementService {

    private final PDFGeneratorService pdfGeneratorService;
    private final UploadService uploadService;
    private final ObjectMapper pdfServiceObjectMapper;
    private final PDFServiceConfiguration pdfServiceConfiguration;
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public PDFManagementService(PDFServiceConfiguration pdfServiceConfiguration, PDFGeneratorService pdfGeneratorService,
                                UploadService uploadService, ObjectMapper objectMapper, HttpServletRequest httpServletRequest) {
        this.pdfServiceConfiguration = pdfServiceConfiguration;
        this.pdfGeneratorService = pdfGeneratorService;
        this.uploadService = uploadService;
        this.pdfServiceObjectMapper = objectMapper.copy();
        SimpleModule module = new SimpleModule();
        module.addSerializer(BigDecimal.class, new BigDecimalNumberSerializer());
        this.pdfServiceObjectMapper.registerModule(module);
        this.httpServletRequest = httpServletRequest;
    }

    public Document generateAndUpload(CallbackRequest callbackRequest, DocumentType documentType) {
        try {
            String json = pdfServiceObjectMapper.writeValueAsString(callbackRequest);
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
