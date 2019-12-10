package uk.gov.hmcts.probate.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.ccd.ocr.ValidationResponse;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordErrorResponse;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordRequest;
import uk.gov.hmcts.probate.model.exceptionrecord.JourneyClassification;
import uk.gov.hmcts.probate.model.exceptionrecord.SuccessfulTransformationResponse;
import uk.gov.hmcts.probate.service.exceptionrecord.ExceptionRecordService;
import uk.gov.hmcts.probate.service.ocr.FormType;
import uk.gov.hmcts.probate.service.ocr.OCRPopulatedValueMapper;
import uk.gov.hmcts.probate.service.ocr.OCRToCCDMandatoryField;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_VALUE)
@Api(tags = "Manage bulk scanning exception record data")
public class ExceptionRecordController {

    private final OCRPopulatedValueMapper ocrPopulatedValueMapper;
    private final OCRToCCDMandatoryField ocrToCCDMandatoryField;

    private static final String OCR_EXCEPTION_WARNING_PREFIX = "OCR Data Mapping Error: ";
    private static final String OCR_EXCEPTION_ERROR = "Caveat OCR fields could not be mapped to a case";

    public static final String PA8A_FORM = FormType.PA8A.name();
    public static final String PA1A_FORM = FormType.PA1A.name();
    public static final String PA1P_FORM = FormType.PA1P.name();

    @Autowired
    ExceptionRecordService erService;

    @ApiOperation(value = "Pre-validate OCR data", notes = "Will return validation errors as warnings. ")
    @ApiResponses({
            @ApiResponse(code = 200, response = ValidationResponse.class, message = "Validation executed successfully"),
            @ApiResponse(code = 400, message = "Request failed due to malformed syntax"),
            @ApiResponse(code = 403, message = "S2S token is not authorized, missing or invalid"),
            @ApiResponse(code = 404, message = "Form type not found")
    })
    @PostMapping(path = "/transform-exception-record",
            consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessfulTransformationResponse> transformExceptionRecord(@Valid @RequestBody ExceptionRecordRequest erRequest) {

        log.info("Transform exception record data for form type: {}", erRequest.getFormType());
        FormType.isFormTypeValid(erRequest.getFormType());
        FormType formType = FormType.valueOf(erRequest.getFormType());
        SuccessfulTransformationResponse callbackResponse = SuccessfulTransformationResponse.builder().build();
        List<String> errors = new ArrayList<>();
        List<String> warnings = ocrToCCDMandatoryField
                .ocrToCCDMandatoryFields(ocrPopulatedValueMapper.ocrPopulatedValueMapper(erRequest.getOcrFields()), formType);

        if (!warnings.isEmpty()) {
            errors.add("Please resolve all warnings before creating this case");
        }

        if (!erRequest.getJourneyClassification().name().equals(JourneyClassification.NEW_APPLICATION.name())) {
            errors.add("This Exception Record can not be created as a case");
        }

        if (!errors.isEmpty()) {
            log.info("Validation check failed, returning error response for form-type {}", formType);
            callbackResponse = SuccessfulTransformationResponse.builder()
                    .warnings(warnings)
                    .errors(errors)
                    .build();

        } else {
            log.info("Validation check passed, attempting to transform case for form-type {}", formType);
            switch (formType) {
                case PA8A:
                    callbackResponse = erService.createCaveatCaseFromExceptionRecord(erRequest, warnings);
                    return ResponseEntity.ok(callbackResponse);
                default:
                    errors.add("This Exception Record form currently has no case mapping");
                    callbackResponse = SuccessfulTransformationResponse.builder()
                            .warnings(warnings)
                            .errors(errors)
                            .build();
                    return ResponseEntity.ok(callbackResponse);
            }
        }

        return ResponseEntity.ok(callbackResponse);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handle(HttpMessageNotReadableException e) {
        log.error("Returning HTTP 400 Bad Request", e);
    }

    @ExceptionHandler(OCRMappingException.class)
    public ResponseEntity<ExceptionRecordErrorResponse> handle(OCRMappingException exception) {
        log.error("An error has occured during the bulk scanning OCR transformation process: {}", exception.getMessage(), exception);
        List<String> warnings = Arrays.asList(OCR_EXCEPTION_WARNING_PREFIX + exception.getMessage());
        List<String> errors = Arrays.asList(OCR_EXCEPTION_ERROR);
        ExceptionRecordErrorResponse errorResponse = new ExceptionRecordErrorResponse(errors, warnings);
        return ResponseEntity.ok(errorResponse);
    }
}
