package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.raw.SolsApplicantFamilyDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.Constants.CHILD;
import static uk.gov.hmcts.probate.model.Constants.GRAND_CHILD;
import static uk.gov.hmcts.probate.model.Constants.HALF_BLOOD_SIBLING;
import static uk.gov.hmcts.probate.model.Constants.HALF_BLOOD_NIECE_OR_NEPHEW;
import static uk.gov.hmcts.probate.model.Constants.WHOLE_BLOOD_SIBLING;
import static uk.gov.hmcts.probate.model.Constants.WHOLE_BLOOD_NIECE_OR_NEPHEW;

@Component
@RequiredArgsConstructor
public class IntestacyCoApplicantValidationRule implements ValidationRule {
    public static final String ADOPTED_OUTSIDE_ENGLAND_OR_WALES = "adoptedOutsideEnglandOrWales";
    public static final String ADOPTED_OUTSIDE_ENGLAND_OR_WALES_WELSH = "adoptedOutsideEnglandOrWales";
    public static final String ADOPTED_OUT = "adoptedOut";
    public static final String ADOPTED_OUT_WELSH = "adoptedOut";
    public static final String PARENT_IS_NOT_DECEASED = "parentIsNotDeceased";
    public static final String PARENT_IS_NOT_DECEASED_WELSH = "parentIsNotDeceased";

    private final BusinessValidationMessageService businessValidationMessageService;

    public List<FieldErrorResponse> validate(CCDData ccdData) {
        List<FieldErrorResponse> errors = new ArrayList<>();
        Optional.ofNullable(ccdData.getExecutors()).ifPresent(executors -> {
            for (var executor : executors) {
                var details = executor.getApplicantFamilyDetails();
                if (details == null) {
                    continue;
                }
                List<String> codes = new ArrayList<>();
                String coApplicantAdoptedInEnglandOrWales = details.getCoApplicantAdoptionInEnglandOrWales();
                String coApplicantAdoptedOut = details.getCoApplicantAdoptedOut();
                String grandchildParentIsDeceased = details.getGrandchildParentDieBeforeDeceased();
                String wholeNieceOrNephewParentIsDeceased = details.getWholeNieceOrNephewParentDieBeforeDeceased();
                String halfNieceOrNephewParentIsDeceased = details.getHalfNieceOrNephewParentDieBeforeDeceased();
                boolean isRelevantRelation = isRelevantRelation(details);

                if (isRelevantRelation && NO.equalsIgnoreCase(coApplicantAdoptedInEnglandOrWales)) {
                    codes.add(ADOPTED_OUTSIDE_ENGLAND_OR_WALES);
                    codes.add(ADOPTED_OUTSIDE_ENGLAND_OR_WALES_WELSH);
                }
                if (isRelevantRelation && YES.equalsIgnoreCase(coApplicantAdoptedOut)) {
                    codes.add(ADOPTED_OUT);
                    codes.add(ADOPTED_OUT_WELSH);
                }
                if (NO.equalsIgnoreCase(grandchildParentIsDeceased)
                        || NO.equalsIgnoreCase(wholeNieceOrNephewParentIsDeceased)
                        || NO.equalsIgnoreCase(halfNieceOrNephewParentIsDeceased)) {
                    codes.add(PARENT_IS_NOT_DECEASED);
                    codes.add(PARENT_IS_NOT_DECEASED_WELSH);
                }

                codes.forEach(code -> errors.add(businessValidationMessageService
                        .generateError(BUSINESS_ERROR, code)));
            }
        });
        return errors;
    }

    private static boolean isRelevantRelation(SolsApplicantFamilyDetails details) {
        String relationshipToDeceased = null != details.getRelationship() ? details
                .getRelationship().getValueCode() : null;


        return CHILD.equalsIgnoreCase(relationshipToDeceased)
                || GRAND_CHILD.equalsIgnoreCase(relationshipToDeceased)
                || WHOLE_BLOOD_SIBLING.equalsIgnoreCase(relationshipToDeceased)
                || HALF_BLOOD_SIBLING.equalsIgnoreCase(relationshipToDeceased)
                || WHOLE_BLOOD_NIECE_OR_NEPHEW.equalsIgnoreCase(relationshipToDeceased)
                || HALF_BLOOD_NIECE_OR_NEPHEW.equalsIgnoreCase(relationshipToDeceased);
    }
}
