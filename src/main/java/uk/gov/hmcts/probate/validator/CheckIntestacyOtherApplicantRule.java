package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

@Component
@RequiredArgsConstructor
public class CheckIntestacyOtherApplicantRule implements ValidationRule {

    private static final String RELATIONSHIP_SPOUSE_CIVIL = "SpouseOrCivil";
    private static final String INTESTACY_SPOUSE_WITH_OTHER_APPLICANT = "errorCannotProceed";
    private static final String INTESTACY_SPOUSE_WITH_OTHER_APPLICANT_WELSH = "errorCannotProceedWelsh";

    private final BusinessValidationMessageService businessValidationMessageService;

    public List<FieldErrorResponse> validate(CCDData ccdData) {

        if (RELATIONSHIP_SPOUSE_CIVIL.equals(ccdData.getSolsApplicantRelationshipToDeceased())
                && Constants.YES.equals(ccdData.getOtherExecutorExists())) {

            return List.of(
                    businessValidationMessageService.generateError(
                            BUSINESS_ERROR, INTESTACY_SPOUSE_WITH_OTHER_APPLICANT),
                    businessValidationMessageService.generateError(
                            BUSINESS_ERROR, INTESTACY_SPOUSE_WITH_OTHER_APPLICANT_WELSH)
            );
        }

        return List.of();
    }
}
