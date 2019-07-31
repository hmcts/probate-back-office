package uk.gov.hmcts.probate.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.ocr.ValidationResponse;
import uk.gov.hmcts.probate.model.ccd.ocr.ValidationResponseState;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ocr.OCRRequest;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;
import uk.gov.hmcts.probate.service.ocr.OCRMapper;
import uk.gov.hmcts.probate.service.ocr.OCRToCCDMandatoryField;

import java.util.List;
import java.util.Locale;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/bulk-scanning", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_VALUE)
@RestController
@Api(tags = "Manage bulk scanning data")
public class BulkScanningController {

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;
    private final OCRMapper ocrMapper;
    private final OCRToCCDMandatoryField ocrToCCDMandatoryField;

    @PostMapping(path = "/attach-scanned-docs-error", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> displayAttachScanDocErrorIfUsedFromUI(@RequestBody CallbackRequest callbackRequest) {
        String[] args = {callbackRequest.getCaseDetails().getId().toString()};
        String userMessage = businessValidationMessageRetriever.getMessage("errorAttachScannedDocs", args, Locale.UK);
        throw new BusinessValidationException(userMessage,
                "User should not call attach scanned docs for case: " + callbackRequest.getCaseDetails().getId());
    }

    @ApiOperation(value = "Pre-validate OCR data", notes = "Will return validation errors as warnings. ")
    @PostMapping(path = "/{form-type}/validate-ocr-data", consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ValidationResponse> validateExceptionRecord(@PathVariable("form-type") String formType,
                                                                    @RequestBody OCRRequest ocrRequest) {
        log.info("Validate ocr data for form type: {}", formType);
        List<String> warnings =
                ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrMapper.ocrMapper(ocrRequest.getOcrFields()));

        ValidationResponse validationResponse =
                ValidationResponse.builder().warnings(warnings).state(warnings.isEmpty() ?
                        ValidationResponseState.SUCCESS : ValidationResponseState.WARNINGS).build();
        return ResponseEntity.ok(validationResponse);
    }

}
