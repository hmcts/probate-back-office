package uk.gov.hmcts.probate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.service.pdf.PDFTemplateService;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static uk.gov.hmcts.probate.model.pdf.PDFServiceTemplate.LEGAL_STATEMENT;

@RestController
public class PDFGeneratorController {

    private static final Logger log = LoggerFactory.getLogger(PDFGeneratorController.class);

    private final PDFTemplateService pdfTemplateService;

    @Autowired
    PDFGeneratorController(PDFTemplateService pdfTemplateService) {
        this.pdfTemplateService = pdfTemplateService;
    }

    @PostMapping(path = "/pdf-generator/legal-statement", consumes = APPLICATION_JSON_UTF8_VALUE,
        produces = {APPLICATION_JSON_VALUE, APPLICATION_PDF_VALUE})
    public ResponseEntity<byte[]> generateLegalStatement(@RequestBody String pdfGenerationData) {
        log.debug("POST /pdf-generator/legal-statement: {}", pdfGenerationData);

        return pdfTemplateService.validateAndGeneratePDF(pdfGenerationData, LEGAL_STATEMENT);
    }

}
