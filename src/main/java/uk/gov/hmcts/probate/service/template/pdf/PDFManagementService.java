package uk.gov.hmcts.probate.service.template.pdf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.exception.ConnectionException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.SentEmail;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;

@Slf4j
@Service
public class PDFManagementService {

    private final PDFGeneratorService pdfGeneratorService;
    private final UploadService uploadService;
    private final ObjectMapper objectMapper;
    private final HttpServletRequest httpServletRequest;
    private final PDFServiceConfiguration pdfServiceConfiguration;

    @Autowired
    public PDFManagementService(PDFGeneratorService pdfGeneratorService, UploadService uploadService,
                                ObjectMapper objectMapper, HttpServletRequest httpServletRequest,
                                PDFServiceConfiguration pdfServiceConfiguration) {
        this.pdfGeneratorService = pdfGeneratorService;
        this.uploadService = uploadService;
        this.objectMapper = objectMapper.copy();
        SimpleModule module = new SimpleModule();
        module.addSerializer(BigDecimal.class, new BigDecimalNumberSerializer());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        this.objectMapper.setDateFormat(df);
        this.objectMapper.registerModule(module);
        this.httpServletRequest = httpServletRequest;
        this.pdfServiceConfiguration = pdfServiceConfiguration;
    }

    public Document generateAndUpload(CallbackRequest callbackRequest, DocumentType documentType) {
        if (DIGITAL_GRANT.equals(documentType)) {
            callbackRequest.getCaseDetails().setGrantSignatureBase64(pdfServiceConfiguration.getGrantSignatureBase64());
        }

        return generateAndUpload(toJson(callbackRequest), documentType);
    }

    public Document generateAndUpload(SentEmail sentEmail, DocumentType documentType) {
        return generateAndUpload(toJson(sentEmail), documentType);
    }

    private Document generateAndUpload(String json, DocumentType documentType) {
        try {
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
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ConnectionException(e.getMessage());
        }
    }

    private String toJson(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new BadRequestException(e.getMessage(), null);
        }
    }
}
