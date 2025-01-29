package uk.gov.hmcts.probate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.model.ccd.ocr.ValidationResponse;
import uk.gov.hmcts.probate.model.ccd.ocr.ValidationResponseStatus;
import uk.gov.hmcts.probate.model.ocr.OCRField;
import uk.gov.hmcts.probate.model.ocr.OCRRequest;
import uk.gov.hmcts.probate.service.ocr.FormType;
import uk.gov.hmcts.probate.service.ocr.NonMandatoryFieldsValidator;
import uk.gov.hmcts.probate.service.ocr.OCRPopulatedValueMapper;
import uk.gov.hmcts.probate.service.ocr.OCRToCCDMandatoryField;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/forms", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@RestController
@Tag(name = "Manage bulk scanning data")
public class OCRFormsController {

    private final OCRPopulatedValueMapper ocrPopulatedValueMapper;
    private final OCRToCCDMandatoryField ocrToCCDMandatoryField;
    private final NonMandatoryFieldsValidator nonMandatoryFieldsValidator;

    @Operation(summary = "Pre-validate OCR data", description = "Will return validation errors as warnings. ")
    @ApiResponses({
        @ApiResponse(responseCode = "200",
                content = @Content(schema = @Schema(implementation = ValidationResponse.class)),
                description = "Validation executed successfully"),
        @ApiResponse(responseCode = "400", description = "Request failed due to malformed syntax"),
        @ApiResponse(responseCode = "403", description = "S2S token is not authorized, missing or invalid")
    })
    @PostMapping(path = "/{form-type}/validate-ocr", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<ValidationResponse> validateExceptionRecord(@PathVariable("form-type") String formType,
                                                                      @Valid @RequestBody OCRRequest ocrRequest) {
        log.info("Validate ocr data for form type: {}", formType);
        FormType.isFormTypeValid(formType);

        List<OCRField> ocrFields = ocrPopulatedValueMapper.ocrPopulatedValueMapper(ocrRequest.getOcrFields());
        List<String> warningsMandatory = ocrToCCDMandatoryField
            .ocrToCCDMandatoryFields(ocrFields, FormType.valueOf(formType));

        List<String> warningsNonMandatory = nonMandatoryFieldsValidator
            .ocrToCCDNonMandatoryWarnings(ocrFields, FormType.valueOf(formType));

        List<String> warnings = new ArrayList<String>();
        warnings.addAll(warningsMandatory);
        warnings.addAll(warningsNonMandatory);

        ValidationResponse validationResponse =
            ValidationResponse.builder().warnings(warnings)
                .status(warnings.isEmpty() ? ValidationResponseStatus.SUCCESS : ValidationResponseStatus.WARNINGS)
                .build();
        return ResponseEntity.ok(validationResponse);
    }
}
