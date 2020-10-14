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
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.BigDecimalNumberSerializer;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementCallbackRequest;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFile;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.service.FileSystemResourceService;
import uk.gov.hmcts.probate.service.evidencemanagement.upload.UploadService;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Map;

import static uk.gov.hmcts.probate.model.DocumentType.WILL_LODGEMENT_DEPOSIT_RECEIPT;

@Slf4j
@Service
public class PDFManagementService {

    private final PDFGeneratorService pdfGeneratorService;
    private final UploadService uploadService;
    private final ObjectMapper objectMapper;
    private final HttpServletRequest httpServletRequest;
    private final PDFServiceConfiguration pdfServiceConfiguration;
    private final FileSystemResourceService fileSystemResourceService;

    static final String SIGNATURE_DECRYPTION_IV = "P3oba73En3yp7ion"; 

    @Autowired
    public PDFManagementService(PDFGeneratorService pdfGeneratorService, UploadService uploadService,
                                ObjectMapper objectMapper, HttpServletRequest httpServletRequest,
                                PDFServiceConfiguration pdfServiceConfiguration,
                                FileSystemResourceService fileSystemResourceService) {
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
        this.fileSystemResourceService = fileSystemResourceService;
    }

    public Document generateAndUpload(CallbackRequest callbackRequest, DocumentType documentType) {
        switch (documentType) {
            case DIGITAL_GRANT:
                callbackRequest.getCaseDetails().setGrantSignatureBase64(decryptedFileAsBase64String(pdfServiceConfiguration
                        .getGrantSignatureEncryptedFile()));
                break;
            case ADMON_WILL_GRANT:
                callbackRequest.getCaseDetails().setGrantSignatureBase64(decryptedFileAsBase64String(pdfServiceConfiguration
                        .getGrantSignatureEncryptedFile()));
                break;
            case INTESTACY_GRANT:
                callbackRequest.getCaseDetails().setGrantSignatureBase64(decryptedFileAsBase64String(pdfServiceConfiguration
                        .getGrantSignatureEncryptedFile()));
                break;
            default:
                break;
        }

        return generateAndUpload(toJson(callbackRequest), documentType);
    }

    public Document generateAndUpload(WillLodgementCallbackRequest callbackRequest, DocumentType documentType) {
        if (WILL_LODGEMENT_DEPOSIT_RECEIPT.equals(documentType)) {
            callbackRequest.getCaseDetails().setGrantSignatureBase64(decryptedFileAsBase64String(pdfServiceConfiguration
                    .getGrantSignatureEncryptedFile()));
        }
        return generateAndUpload(toJson(callbackRequest), documentType);
    }

    public Document generateAndUpload(CaveatCallbackRequest callbackRequest, DocumentType documentType) {
        return generateAndUpload(toJson(callbackRequest), documentType);
    }

    public Document generateAndUpload(SentEmail sentEmail, DocumentType documentType) {
        return generateAndUpload(toJson(sentEmail), documentType);
    }

    private Document generateAndUpload(String json, DocumentType documentType) {
        log.info("Generating pdf for template {}", documentType.getTemplateName());
        EvidenceManagementFileUpload fileUpload = pdfGeneratorService.generatePdf(documentType, json);
        log.info("Got the ", documentType.getTemplateName());
        return uploadDocument(documentType, fileUpload);
    }

    public Document generateDocmosisDocumentAndUpload(Map<String, Object> placeholders, DocumentType documentType) {

        log.info("Generating pdf to docmosis for template {}", documentType.getTemplateName());
        EvidenceManagementFileUpload fileUpload = pdfGeneratorService.generateDocmosisDocumentFrom(documentType.getTemplateName(),
                placeholders);
        return uploadDocument(documentType, fileUpload);
    }

    private Document uploadDocument(DocumentType documentType, EvidenceManagementFileUpload fileUpload) {
        try {
            log.info("Uploading pdf for template {}", documentType.getTemplateName());
            EvidenceManagementFile store = uploadService.store(fileUpload);
            DocumentLink documentLink = DocumentLink.builder()
                    .documentBinaryUrl(store.getLink("binary").getHref())
                    .documentUrl(store.getLink(Link.REL_SELF).getHref())
                    .documentFilename(documentType.getTemplateName() + ".pdf")
                    .build();

            return Document.builder()
                    .documentFileName(fileUpload.getFileName())
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

    private String decryptedFileAsBase64String(String fileResource) {
        String cipherMessage = fileSystemResourceService.getFileFromResourceAsString(fileResource);
        log.info("Decrypting file: " + fileResource);
        String decryptedString = null;
        try {
            IvParameterSpec iv = new IvParameterSpec(SIGNATURE_DECRYPTION_IV.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec secretKey = new SecretKeySpec(pdfServiceConfiguration
                    .getGrantSignatureSecretKey().getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            decryptedString = Base64.getEncoder().encodeToString(cipher
                    .doFinal(Base64.getDecoder().decode(cipherMessage.getBytes())));
        } catch (Exception e) {
            log.error("Error while retrieving file resource " + fileResource + ": " + e.getMessage(), e);
            throw new BadRequestException(e.getMessage());
        }
        return decryptedString;
    }

    private String toJson(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new BadRequestException(e.getMessage());
        }
    }

    public String getDecodedSignature() {
        return decryptedFileAsBase64String(pdfServiceConfiguration.getGrantSignatureEncryptedFile());
    }
}
