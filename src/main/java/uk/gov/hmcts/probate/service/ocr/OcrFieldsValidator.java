package uk.gov.hmcts.probate.service.ocr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.ListUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.bulkscan.type.FormData;
import uk.gov.hmcts.bulkscan.type.IServiceOcrValidator;
import uk.gov.hmcts.bulkscan.type.OcrDataField;
import uk.gov.hmcts.bulkscan.type.OcrValidationResult;
import uk.gov.hmcts.bulkscan.type.OcrValidationStatus;
import uk.gov.hmcts.probate.exception.NotFoundException;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static uk.gov.hmcts.bulkscan.type.OcrValidationStatus.ERRORS;

@Slf4j
@RequiredArgsConstructor
@Service
public class OcrFieldsValidator  implements IServiceOcrValidator {

    private final OCRToCCDMandatoryField ocrToCCDMandatoryField;
    private final NonMandatoryFieldsValidator nonMandatoryFieldsValidator;

    @Override
    public OcrValidationResult validateEnvelope(String formType, FormData docWithOcr) {

        log.info("Validate ocr data for form type: {}", formType);
        logOcrRequest(docWithOcr);

        try {
            FormType.isFormTypeValid(formType);
        } catch (NotFoundException nfe) {
            return new OcrValidationResult(
                    ERRORS,
                    Collections.emptyList(),
                    singletonList("Form type '" + formType + "' not found")
            );
        }

        List<String> warningsMandatory = ocrToCCDMandatoryField.ocrToCCDMandatoryFields(
                docWithOcr.ocrDataFields(),
                FormType.valueOf(formType)
        );

        List<String> warningsNonMandatory = nonMandatoryFieldsValidator.ocrToCCDNonMandatoryWarnings(
                docWithOcr.ocrDataFields(),
                FormType.valueOf(formType)
        );

        List<String> warnings = ListUtils.union(warningsMandatory, warningsNonMandatory);

        return new OcrValidationResult(
                warnings.isEmpty() ? OcrValidationStatus.SUCCESS : OcrValidationStatus.WARNINGS,
                warnings,
                emptyList()
        );
    }

    private void logOcrRequest(FormData docWithOcr) {
        StringBuilder sb = new StringBuilder();
        for (OcrDataField ocrField : docWithOcr.ocrDataFields()) {
            sb.append(ocrField.name() + ":" + ocrField.value() + ",");
        }
        log.info(sb.toString());
    }
}
