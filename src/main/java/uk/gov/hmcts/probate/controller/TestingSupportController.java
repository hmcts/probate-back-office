package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.SentEmail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.service.MarkdownTransformationService;
import uk.gov.hmcts.probate.service.template.pdf.PDFGeneratorService;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import static uk.gov.hmcts.probate.model.DocumentType.SENT_EMAIL;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/testing-support")
public class TestingSupportController {

    private final NotificationClient notificationClient;
    private final MarkdownTransformationService markdownTransformationService;
    private final PDFGeneratorService pdfGeneratorService;
    private final ObjectMapper objectMapper;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM Y HH:mm");

    @PostMapping(path = "/documents/gov-notify")
    public ResponseEntity<byte[]> getGovNotifyDocumentText(
        @RequestBody HashMap<String, String> personalisation) throws NotificationClientException {
        String templateId = personalisation.get("templateId");
        String emailAddress = "notify@probate-test.com";
        String reference = "email ref";
        String emailReplyToId = "3c2df3ad-6eaf-4395-b85c-1ef5c5c89d4f";
        SendEmailResponse response =
            notificationClient.sendEmail(templateId, emailAddress, personalisation, reference, emailReplyToId);

        SentEmail sentEmail = SentEmail.builder()
            .sentOn(LocalDateTime.now().format(formatter))
            .from(response.getFromEmail().orElse(""))
            .to(emailAddress)
            .subject(response.getSubject())
            .body(markdownTransformationService.toHtml(response.getBody()))
            .build();

        EvidenceManagementFileUpload fileUpload = pdfGeneratorService.generatePdf(SENT_EMAIL, toJson(sentEmail));
        return ResponseEntity.ok(fileUpload.getBytes());
    }

    @PostMapping(path = "/documents/pdf")
    public ResponseEntity<byte[]> getPdfDocumentText(
        @RequestParam String docymentType,
        @RequestBody CallbackRequest callbackRequest) throws NotificationClientException {

        DocumentType docType = DocumentType.valueOf(docymentType);
        EvidenceManagementFileUpload fileUpload = pdfGeneratorService.generatePdf(docType, toJson(callbackRequest));
        return ResponseEntity.ok(fileUpload.getBytes());
    }

    private String toJson(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new BadRequestException(e.getMessage());

        }
    }
}
