package uk.gov.hmcts.probate.service.ocr;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.probate.exception.NotFoundException;
import uk.gov.hmcts.probate.exception.OCRMappingException;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
public enum FormType {
    PA1P,
    PA1A,
    PA8A;

    public static void isFormTypeValid(String formType) {
        if (Arrays.stream(FormType.values()).noneMatch(type -> type.name().equals(formType))) {
            log.error("Form type '{}' not found", formType);
            throw new OCRMappingException("Form type not found or invalid");
        }
    }
}
