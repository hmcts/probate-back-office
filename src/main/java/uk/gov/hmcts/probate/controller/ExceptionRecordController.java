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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.ocr.ValidationResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.exceptionrecord.CaseCreationDetails;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordRequest;
import uk.gov.hmcts.probate.model.exceptionrecord.JourneyClassification;
import uk.gov.hmcts.probate.model.exceptionrecord.OCRFieldsList;
import uk.gov.hmcts.probate.model.exceptionrecord.SuccessfulTransformationResponse;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.ExceptionRecordMapper;
import uk.gov.hmcts.probate.service.ocr.FormType;
import uk.gov.hmcts.probate.service.ocr.OCRMapper;
import uk.gov.hmcts.probate.service.ocr.OCRToCCDMandatoryField;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
@RestController
@Api(tags = "Manage bulk scanning exception record data")
public class ExceptionRecordController {

    private final OCRMapper ocrMapper;
    private final OCRToCCDMandatoryField ocrToCCDMandatoryField;

    private static final String PA_APP_CREATED = "PAAppCreated";

    @Autowired
    ExceptionRecordMapper erMapper;

    @Autowired
    CaveatCallbackResponseTransformer caveatTransformer;

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

        String formType = erRequest.getFormType();
        log.info("Transform exception record data for form type: {}", formType);
        FormType.isFormTypeValid(formType);
        List<String> errors = new ArrayList<String>();
        List<String> warnings = ocrToCCDMandatoryField
                .ocrToCCDMandatoryFields(ocrMapper.ocrMapper(erRequest.getOcrFields()),
                        FormType.valueOf(formType));

        if (!warnings.isEmpty()) {
            errors.add("Please resolve all warnings before creating this case.");
        }

        if (!erRequest.getJourneyClassification().name().equals(JourneyClassification.NEW_APPLICATION.name())) {
            errors.add("This Exception Record can't be created as a case.");
        }

        if (!errors.isEmpty()) {
            SuccessfulTransformationResponse callbackResponse = SuccessfulTransformationResponse.builder()
                    .warnings(warnings)
                    .errors(errors)
                    .build();
            return ResponseEntity.ok(callbackResponse);
        }

        CaveatData caveatData = erMapper.toCcdData(erRequest.getOCRFieldsObject());

        long generatedLong = (long) (Math.random() * (1000));
        Long id = new Long(generatedLong);

        CaveatDetails caveatDetails = new CaveatDetails(caveatData, null, id);

        CaveatCallbackRequest caveatCallbackRequest = new CaveatCallbackRequest(caveatDetails);

        CaseCreationDetails caveatCaseDetailsResponse = caveatTransformer.transform(caveatCallbackRequest);

        SuccessfulTransformationResponse callbackResponse = SuccessfulTransformationResponse.builder()
                .caseCreationDetails(caveatCaseDetailsResponse)
                .warnings(warnings)
                .errors(errors)
                .build();

        return ResponseEntity.ok(callbackResponse);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handle(HttpMessageNotReadableException e) {
        log.warn("Returning HTTP 400 Bad Request", e);
    }

}
