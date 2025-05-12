package uk.gov.hmcts.probate.service.template.pdf;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.exception.ConnectionException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.SentEmail;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementCallbackRequest;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.service.FileSystemResourceService;
import uk.gov.hmcts.probate.service.documentmanagement.DocumentManagementService;
import uk.gov.hmcts.reform.ccd.document.am.model.UploadResponse;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Map;

import static uk.gov.hmcts.probate.model.DocumentType.WILL_LODGEMENT_DEPOSIT_RECEIPT;

@Slf4j
@Service
public class PDFManagementService {

    static final String SIGNATURE_DECRYPTION_IV = "P3oba73En3yp7ion";
    private final PDFGeneratorService pdfGeneratorService;
    private final DocumentManagementService documentManagementService;
    private final HttpServletRequest httpServletRequest;
    private final PDFServiceConfiguration pdfServiceConfiguration;
    private final FileSystemResourceService fileSystemResourceService;
    private final PDFDecoratorService pdfDecoratorService;

    @Autowired
    public PDFManagementService(PDFGeneratorService pdfGeneratorService,
                                HttpServletRequest httpServletRequest,
                                DocumentManagementService documentManagementService,
                                PDFServiceConfiguration pdfServiceConfiguration,
                                FileSystemResourceService fileSystemResourceService,
                                PDFDecoratorService pdfDecoratorService) {
        this.pdfGeneratorService = pdfGeneratorService;
        this.documentManagementService = documentManagementService;
        this.httpServletRequest = httpServletRequest;
        this.pdfServiceConfiguration = pdfServiceConfiguration;
        this.fileSystemResourceService = fileSystemResourceService;
        this.pdfDecoratorService = pdfDecoratorService;
    }

    public Document generateAndUpload(CallbackRequest callbackRequest, DocumentType documentType) {
        switch (documentType) {
            case DIGITAL_GRANT:
            case DIGITAL_GRANT_REISSUE:
                callbackRequest.getCaseDetails()
                    .setGrantSignatureBase64(decryptedFileAsBase64String(pdfServiceConfiguration
                        .getGrantSignatureEncryptedFile()));
                break;
            case ADMON_WILL_GRANT:
                callbackRequest.getCaseDetails()
                    .setGrantSignatureBase64(decryptedFileAsBase64String(pdfServiceConfiguration
                        .getGrantSignatureEncryptedFile()));
                break;
            case INTESTACY_GRANT:
                callbackRequest.getCaseDetails()
                    .setGrantSignatureBase64(decryptedFileAsBase64String(pdfServiceConfiguration
                        .getGrantSignatureEncryptedFile()));
                break;
            default:
                break;
        }

        return generateAndUpload(toJson(callbackRequest, documentType), documentType);
    }

    public Document generateAndUpload(WillLodgementCallbackRequest callbackRequest, DocumentType documentType) {
        if (WILL_LODGEMENT_DEPOSIT_RECEIPT.equals(documentType)) {
            callbackRequest.getCaseDetails().setGrantSignatureBase64(decryptedFileAsBase64String(pdfServiceConfiguration
                .getGrantSignatureEncryptedFile()));
        }
        return generateAndUpload(toJson(callbackRequest, documentType), documentType);
    }

    public Document generateAndUpload(CaveatCallbackRequest callbackRequest, DocumentType documentType) {
        return generateAndUpload(toJson(callbackRequest, documentType), documentType);
    }

    public Document generateAndUpload(SentEmail sentEmail, DocumentType documentType) {
        return generateAndUpload(toJson(sentEmail, documentType), documentType);
    }

    private Document generateAndUpload(String json, DocumentType documentType) {
        log.info("Generating pdf for template {}", documentType.getTemplateName());
        EvidenceManagementFileUpload fileUpload = pdfGeneratorService.generatePdf(documentType, json);
        log.info("Got the {}", documentType.getTemplateName());
        return uploadDocument(documentType, fileUpload);
    }

    public Document generateDocmosisDocumentAndUpload(Map<String, Object> placeholders, DocumentType documentType) {

        log.info("Generating pdf to docmosis for template {}", documentType.getTemplateName());
        EvidenceManagementFileUpload fileUpload =
            pdfGeneratorService.generateDocmosisDocumentFrom(documentType.getTemplateName(),
                placeholders);
        return uploadDocument(documentType, fileUpload);
    }

    private Document uploadDocument(DocumentType documentType, EvidenceManagementFileUpload fileUpload) {
        try {
            log.info("Uploading pdf for template {}", documentType.getTemplateName());
            UploadResponse uploadResponse = documentManagementService.upload(fileUpload, documentType);
            if (uploadResponse == null) {
                throw new IOException("uploadResponse is null");
            }
            if (uploadResponse.getDocuments() == null) {
                throw new IOException("Documents are null");
            }
            if (uploadResponse.getDocuments().isEmpty()) {
                throw new IOException("Documents are empty");
            }
            uk.gov.hmcts.reform.ccd.document.am.model.Document document = uploadResponse.getDocuments().get(0);
            if (document.links == null) {
                throw new IOException("No Document links");
            }
            if (document.links.binary == null) {
                throw new IOException("No Document binary link");
            }
            if (document.links.self == null) {
                throw new IOException("No Document self link");
            }
            DocumentLink documentLink = DocumentLink.builder()
                .documentBinaryUrl(document.links.binary.href)
                .documentUrl(document.links.self.href)
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

    private String toJson(Object data, DocumentType documentType) {
        return pdfDecoratorService.decorate(data, documentType);
    }

    public String getDecodedSignature() {
        return decryptedFileAsBase64String(pdfServiceConfiguration.getGrantSignatureEncryptedFile());
    }

    /** Converts an input HTML string to an XHTML string.
     *
     * This is needed because the underlying pdfGeneratorService uses an XML parser rather
     * than an HTML parser.
     *
     * @param inputHtml an HTML string
     * @return the input rendered as XHTML
     */
    public String rerenderAsXhtml(String inputHtml) {
        final Safelist safelist = Safelist.relaxed();


        final org.jsoup.nodes.Document.OutputSettings outputSettings = new org.jsoup.nodes.Document.OutputSettings()
                .syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml)
                .charset(StandardCharsets.UTF_8)
                .prettyPrint(false);

        return Jsoup.clean(inputHtml, "", safelist, outputSettings);
    }
}
