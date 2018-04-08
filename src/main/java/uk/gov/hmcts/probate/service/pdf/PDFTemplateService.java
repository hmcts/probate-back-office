package uk.gov.hmcts.probate.service.pdf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.pdf.PDFServiceTemplate;

@Component
public class PDFTemplateService {

    private final PDFGeneratorService pdfGeneratorService;

    private final PDFPayloadValidator pdfPayloadValidator;

    @Autowired
    PDFTemplateService(PDFGeneratorService pdfGeneratorService, PDFPayloadValidator pdfPayloadValidator) {
        this.pdfGeneratorService = pdfGeneratorService;
        this.pdfPayloadValidator = pdfPayloadValidator;
    }

    public ResponseEntity<byte[]> validateAndGeneratePDF(String pdfGenerationData, PDFServiceTemplate pdfServiceTemplate) {

        pdfPayloadValidator.validatePayload(pdfGenerationData, pdfServiceTemplate);

        return pdfGeneratorService.generatePdf(pdfServiceTemplate, pdfGenerationData);

    }
}
