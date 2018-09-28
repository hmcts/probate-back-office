package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

@Component
@RequiredArgsConstructor
class CheckListValidationRule implements CheckListAmendCaseValidationRule {

    private final BusinessValidationMessageService businessValidationMessageService;
    private static final String ANSWER_NO = "NO";

    @Override
    public List<FieldErrorResponse> validate(CCDData ccdData) {
        Set<FieldErrorResponse> errors = new HashSet<>();

        if (ccdData.getBoExaminationChecklistQ1().equalsIgnoreCase(ANSWER_NO) ||
                ccdData.getBoExaminationChecklistQ2().equalsIgnoreCase(ANSWER_NO)) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR, "questionCanNotBeNo"));
        }
        return new ArrayList<>(errors);
    }
}
