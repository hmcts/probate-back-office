package uk.gov.hmcts.probate.service.ocr;

import uk.gov.hmcts.probate.exception.NotFoundException;

import java.util.Arrays;

public enum FormType {
    PA1P,
    PA1A,
    PA8A;

    public static void isFormTypeValid(String formType) {
        if (Arrays.stream(FormType.values()).noneMatch(type -> type.name().equals(formType))) {
            throw new NotFoundException("Form type '" + formType + "' not found");
        }
    }
}
