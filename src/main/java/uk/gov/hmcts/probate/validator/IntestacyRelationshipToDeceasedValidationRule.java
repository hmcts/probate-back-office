package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;
import static uk.gov.hmcts.probate.model.Constants.SOLICITOR_SPOUSE;
import static uk.gov.hmcts.probate.model.Constants.SPOUSE;

@Component
@RequiredArgsConstructor
public class IntestacyRelationshipToDeceasedValidationRule implements CaseworkerAmendAndCreateValidationRule {
    public static final String INVALID_RELATIONSHIP_TO_DECEASED = "invalidRelationshipToDeceased";

    private final BusinessValidationMessageService businessValidationMessageService;

    public List<FieldErrorResponse> validate(CCDData ccdData) {
        List<FieldErrorResponse> errors = new ArrayList<>();
        String solicitorApplicantRelationshipToDeceased = ccdData.getSolsApplicantRelationshipToDeceased();
        String personalApplicantRelationshipToDeceased = ccdData.getPrimaryApplicantRelationshipToDeceased();
        Optional.ofNullable(ccdData.getCaseworkerExecutorsList()).ifPresent(executors -> {
            for (var executor : executors) {
                var coApplicantDetails = executor.getApplicantFamilyDetails();
                if (coApplicantDetails == null) {
                    continue;
                }
                String coApplicantRelationshipToDeceased = coApplicantDetails.getRelationshipToDeceased();
                if ((SOLICITOR_SPOUSE.equalsIgnoreCase(solicitorApplicantRelationshipToDeceased)
                        || SPOUSE.equalsIgnoreCase(personalApplicantRelationshipToDeceased))
                        && SPOUSE.equalsIgnoreCase(coApplicantRelationshipToDeceased)) {
                    errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR,
                            INVALID_RELATIONSHIP_TO_DECEASED));
                }
            }
        });
        return errors;
    }
}
