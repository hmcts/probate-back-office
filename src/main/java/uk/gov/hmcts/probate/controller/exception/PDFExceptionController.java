package uk.gov.hmcts.probate.controller.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uk.gov.hmcts.probate.model.BusinessValidationError;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.ArrayList;
import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.keyValue;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;


@RestControllerAdvice()
public class PDFExceptionController {

    private static final Logger logger = LoggerFactory.getLogger(PDFExceptionController.class);

    private final BusinessValidationMessageService businessValidationMessageService;

    @Autowired
    PDFExceptionController(BusinessValidationMessageService businessValidationMessageService) {
        this.businessValidationMessageService = businessValidationMessageService;
    }

    @ExceptionHandler(PDFClientException.class)
    public ResponseEntity<byte[]> handle(PDFClientException pdfClientException) {
        byte[] emptyBytes = {};
        if (pdfClientException.getHttpClientErrorException().getStatusCode().is4xxClientError()) {
            logger.error("Can't connect to pdf-service-api, response code: {}",
                pdfClientException.getHttpClientErrorException().getStatusCode().value(),
                pdfClientException);
            return new ResponseEntity<>(emptyBytes, null, SERVICE_UNAVAILABLE);
        } else {
            return new ResponseEntity<>(emptyBytes, null, OK);
        }
    }

    @ExceptionHandler(PDFMissingPayloadException.class)
    public @ResponseBody ResponseEntity<List<BusinessValidationError>> handle(PDFMissingPayloadException exception) {
        List<BusinessValidationError> bves = new ArrayList<>();
        for (String missingKey : exception.getMissingPayloadKeys()) {
            String htmlFileName = exception.getPdfServiceTemplate().getHtmlFileName();
            String[] args = {htmlFileName, missingKey};
            BusinessValidationError bve = businessValidationMessageService
                .generateError(null, "missingPDFPayload", args);
            bves.add(bve);
        }

        logger.warn("Missing PDF Payload", keyValue("missingKeys", exception.getMissingPayloadKeys()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(bves, headers, UNPROCESSABLE_ENTITY);
    }
}
