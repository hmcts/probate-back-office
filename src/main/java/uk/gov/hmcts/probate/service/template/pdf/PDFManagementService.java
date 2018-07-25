package uk.gov.hmcts.probate.service.template.pdf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.exception.ConnectionException;
import uk.gov.hmcts.probate.model.ccd.raw.BigDecimalNumberSerializer;
import uk.gov.hmcts.probate.model.ccd.raw.CCDDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFile;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.model.template.PDFServiceTemplate;
import uk.gov.hmcts.probate.service.evidencemanagement.upload.UploadService;

import java.io.IOException;
import java.math.BigDecimal;

@Service
public class PDFManagementService {

    private final PDFGeneratorService pdfGeneratorService;
    private final UploadService uploadService;
    private final ObjectMapper pdfServiceObjectMapper;
    private final PDFServiceConfiguration pdfServiceConfiguration;
    private static final Logger log = LoggerFactory.getLogger(PDFManagementService.class);

    @Autowired
    public PDFManagementService(PDFServiceConfiguration pdfServiceConfiguration, PDFGeneratorService pdfGeneratorService,
                                UploadService uploadService, ObjectMapper objectMapper) {
        this.pdfServiceConfiguration = pdfServiceConfiguration;
        this.pdfGeneratorService = pdfGeneratorService;
        this.uploadService = uploadService;
        this.pdfServiceObjectMapper = objectMapper.copy();
        SimpleModule module = new SimpleModule();
        module.addSerializer(BigDecimal.class, new BigDecimalNumberSerializer());
        this.pdfServiceObjectMapper.registerModule(module);
    }

    public CCDDocument generateAndUpload(CallbackRequest callbackRequest, PDFServiceTemplate pdfServiceTemplate) {
        try {
            String json = pdfServiceObjectMapper.writeValueAsString(callbackRequest);
            EvidenceManagementFileUpload fileUpload = pdfGeneratorService.generatePdf(pdfServiceTemplate, json);
            EvidenceManagementFile store = uploadService.store(fileUpload);
            return CCDDocument.builder()
                    .documentBinaryUrl(store.getLink("binary").getHref())
                    .documentUrl(store.getLink(Link.REL_SELF).getHref())
                    .documentFilename(pdfServiceConfiguration.getDefaultDisplayFilename())
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
