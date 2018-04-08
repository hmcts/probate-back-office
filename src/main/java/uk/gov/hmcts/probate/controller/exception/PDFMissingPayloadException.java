package uk.gov.hmcts.probate.controller.exception;

import uk.gov.hmcts.probate.model.pdf.PDFServiceTemplate;

import java.util.List;

public class PDFMissingPayloadException extends RuntimeException {

    private final List<String> missingPayloadKeys;

    private final PDFServiceTemplate pdfServiceTemplate;

    public PDFMissingPayloadException(List<String> missingPayloadKeys, PDFServiceTemplate pdfServiceTemplate) {
        this.missingPayloadKeys = missingPayloadKeys;
        this.pdfServiceTemplate = pdfServiceTemplate;
    }

    public List<String> getMissingPayloadKeys() {
        return missingPayloadKeys;
    }

    public PDFServiceTemplate getPdfServiceTemplate() {
        return pdfServiceTemplate;
    }
}
