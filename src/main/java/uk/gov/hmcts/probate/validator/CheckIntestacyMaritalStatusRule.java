package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

@Component
@RequiredArgsConstructor
public class CheckIntestacyMaritalStatusRule implements ValidationRule {

    private static final String MARITAL_STATUS_MARRIED = "marriedCivilPartnership";
    private static final String RELATIONSHIP_SPOUSE_CIVIL = "SpouseOrCivil";
    private static final String INTESTACY_SPOUSE_INVALID_MARITAL_STATUS = "errorNotPossible";
    private static final String INTESTACY_SPOUSE_INVALID_MARITAL_STATUS_WELSH = "errorNotPossibleWelsh";

    private final BusinessValidationMessageService businessValidationMessageService;

    public List<FieldErrorResponse> validate(CCDData ccdData) {

        if (RELATIONSHIP_SPOUSE_CIVIL.equals(ccdData.getSolsApplicantRelationshipToDeceased())
                && MARITAL_STATUS_MARRIED.equals(ccdData.getDeceasedMaritalStatus())) {

            return List.of(
                    businessValidationMessageService.generateError(
                            BUSINESS_ERROR, INTESTACY_SPOUSE_INVALID_MARITAL_STATUS),
                    businessValidationMessageService.generateError(
                            BUSINESS_ERROR, INTESTACY_SPOUSE_INVALID_MARITAL_STATUS_WELSH)
            );
        }

        return List.of();
    }
}
