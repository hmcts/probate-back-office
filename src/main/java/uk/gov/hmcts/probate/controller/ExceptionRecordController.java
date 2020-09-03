package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import uk.gov.hmcts.probate.model.exceptionrecord.CaveatCaseUpdateRequest;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordErrorResponse;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordRequest;
import uk.gov.hmcts.probate.model.exceptionrecord.JourneyClassification;
import uk.gov.hmcts.probate.model.exceptionrecord.SuccessfulCaveatUpdateResponse;
import uk.gov.hmcts.probate.model.exceptionrecord.SuccessfulTransformationResponse;
import uk.gov.hmcts.probate.service.exceptionrecord.ExceptionRecordService;
import uk.gov.hmcts.probate.service.ocr.FormType;
import uk.gov.hmcts.probate.service.ocr.OCRPopulatedValueMapper;
import uk.gov.hmcts.probate.service.ocr.OCRToCCDMandatoryField;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;

import javax.validation.Valid;
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
    private final ObjectMapper objectMapper;

    private static final String OCR_EXCEPTION_WARNING_PREFIX = "OCR Data Mapping Error: ";
    private static final String OCR_EXCEPTION_ERROR = "OCR fields could not be mapped to a case";

    @Autowired
    ExceptionRecordService erService;

    @ApiOperation(value = "Transforms OCR data to case data", notes = "Will return errors if the transformation is unsuccessful.")
    @ApiResponses({
            @ApiResponse(code = 200, response = ValidationResponse.class, message = "Validation executed successfully"),
            @ApiResponse(code = 400, message = "Request failed due to malformed syntax"),
            @ApiResponse(code = 401, message = "Unauthorised"),
            @ApiResponse(code = 403, message = "S2S token is not authorized, missing or invalid")
    })
    @PostMapping(path = "/transform-scanned-data",
            consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessfulTransformationResponse> transformCase(@Valid @RequestBody ExceptionRecordRequest erRequest) {

        log.info("Transform exception record data for form type: {}, case: {}", erRequest.getFormType(), erRequest.getExceptionRecordId());
        FormType.isFormTypeValid(erRequest.getFormType());
        FormType formType = FormType.valueOf(erRequest.getFormType());
        SuccessfulTransformationResponse callbackResponse = SuccessfulTransformationResponse.builder().build();
        List<String> warnings = ocrToCCDMandatoryField
                .ocrToCCDMandatoryFields(ocrPopulatedValueMapper.ocrPopulatedValueMapper(erRequest.getOcrFields()), formType);

        if (!warnings.isEmpty()) {
            throw new OCRMappingException("Please resolve all warnings before creating the case", warnings);
        }

        if (!erRequest.getJourneyClassification().name().equals(JourneyClassification.NEW_APPLICATION.name())) {
            throw new OCRMappingException("This Exception Record can not be created as a case: " + erRequest.getExceptionRecordId());
        }

        log.info("Validation check passed, attempting to transform case for form-type {}, caseId {}", formType, erRequest.getExceptionRecordId());
        switch (formType) {
            case PA8A:
                callbackResponse = erService.createCaveatCaseFromExceptionRecord(erRequest, warnings);
                break;
            case PA1P:
                callbackResponse = erService.createGrantOfRepresentationCaseFromExceptionRecord(
                        erRequest, GrantType.GRANT_OF_PROBATE, warnings);
                break;
            case PA1A:
                callbackResponse = erService.createGrantOfRepresentationCaseFromExceptionRecord(
                        erRequest, GrantType.INTESTACY, warnings);
                break;
            default:
                throw new OCRMappingException("This Exception Record form currently has no case mapping for case "+erRequest.getExceptionRecordId());
        }

        return ResponseEntity.ok(callbackResponse);
    }

    @ApiOperation(value = "Transforms OCR data to case data", notes = "Will return errors if the transformation is unsuccessful.")
    @ApiResponses({
            @ApiResponse(code = 200, response = ValidationResponse.class, message = "Validation executed successfully"),
            @ApiResponse(code = 400, message = "Request failed due to malformed syntax"),
            @ApiResponse(code = 401, message = "Unauthorised"),
            @ApiResponse(code = 403, message = "S2S token is not authorized, missing or invalid")
    })
    @PostMapping(path = "/transform-exception-record",
            consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessfulTransformationResponse> transformCaseNonAutomation(@Valid @RequestBody ExceptionRecordRequest erRequest) {
        return transformCase(erRequest);
    }

    @ApiOperation(value = "Updates a case based on availability of OCR data and documents", notes = "Will return errors if unsuccessful or no new documents found.")
    @ApiResponses({
            @ApiResponse(code = 200, response = ValidationResponse.class, message = "Validation executed successfully"),
            @ApiResponse(code = 400, message = "Request failed due to malformed syntax"),
            @ApiResponse(code = 401, message = "Unauthorised"),
            @ApiResponse(code = 403, message = "S2S token is not authorized, missing or invalid"),
            @ApiResponse(code = 404, message = "Form type not found")
    })
    @PostMapping(path = "/update-case",
            consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessfulCaveatUpdateResponse> updateCase(@Valid @RequestBody CaveatCaseUpdateRequest erCaseUpdateRequest) {

        logRequest(erCaseUpdateRequest);
        
        ExceptionRecordRequest erRequest = erCaseUpdateRequest.getExceptionRecord();
        log.info("Update case data from exception record for form type: {}, case: {}", erRequest.getFormType(), erRequest.getExceptionRecordId());
        FormType.isFormTypeValid(erRequest.getFormType());
        FormType formType = FormType.valueOf(erRequest.getFormType());
        SuccessfulCaveatUpdateResponse callbackResponse;

        if (!erRequest.getJourneyClassification().name().equals(JourneyClassification.SUPPLEMENTARY_EVIDENCE_WITH_OCR.name())) {
            log.error("This Exception Record can not be created as a case update {}", erRequest.getExceptionRecordId());
            throw new OCRMappingException("This Exception Record can not be created as a case update for case:" + erRequest.getExceptionRecordId());
        }

        log.info("Validation check passed, attempting to update case for form-type {}, case {}", formType, erRequest.getExceptionRecordId());
        switch (formType) {
            case PA8A: {
                callbackResponse = erService.updateCaveatCaseFromExceptionRecord(erCaseUpdateRequest);
                break;
            }
            default: {
                log.error("This Exception Record form currently has no case mapping");
                throw new OCRMappingException("This Exception Record form currently has no case mapping for case: "+erRequest.getExceptionRecordId());
            }
        }

        logResponse(callbackResponse);
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
        List<String> warnings;
        if (!exception.getWarnings().isEmpty()) {
            warnings = exception.getWarnings();
        } else {
            warnings = Arrays.asList(OCR_EXCEPTION_WARNING_PREFIX + exception.getMessage());
        }
        List<String> errors = Arrays.asList(OCR_EXCEPTION_ERROR);
        ExceptionRecordErrorResponse errorResponse = new ExceptionRecordErrorResponse(errors, warnings);
        return new ResponseEntity(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private void logRequest(CaveatCaseUpdateRequest caveatCaseUpdateRequest) {
        try {
            log.info("logging request on ExceptionRecordController: {}", objectMapper.writeValueAsString(caveatCaseUpdateRequest));
        } catch (JsonProcessingException e) {
            log.error("POST: {}", e);
        }
    }
    
    private void logResponse(SuccessfulCaveatUpdateResponse successfulCaveatUpdateResponse) {
        try {
            log.info("logging response on ExceptionRecordController: {}", objectMapper.writeValueAsString(successfulCaveatUpdateResponse));
        } catch (JsonProcessingException e) {
            log.error("POST: {}", e);
        }
    }

}
