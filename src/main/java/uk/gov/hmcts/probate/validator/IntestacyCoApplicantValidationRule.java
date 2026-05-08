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
    public static final String ADOPTED_OUTSIDE_ENGLAND_OR_WALES_WELSH = "adoptedOutsideEnglandOrWalesWelsh";
    public static final String ADOPTED_OUT = "coApplicantAdoptedOut";
    public static final String ADOPTED_OUT_WELSH = "coApplicantAdoptedOutWelsh";
    public static final String PARENT_IS_NOT_DECEASED = "parentIsNotDeceased";
    public static final String PARENT_IS_NOT_DECEASED_WELSH = "parentIsNotDeceasedWelsh";
    public static final String PARENT_ADOPTED_OUT = "coApplicantParentAdoptedOut";
    public static final String PARENT_ADOPTED_OUT_WELSH = "coApplicantParentAdoptedOutWelsh";

    private final BusinessValidationMessageService businessValidationMessageService;

    public List<FieldErrorResponse> validate(CCDData ccdData) {
        List<FieldErrorResponse> errors = new ArrayList<>();
        Optional.ofNullable(ccdData.getExecutors()).ifPresent(executors -> {
            for (var executor : executors) {
                var details = executor.getApplicantFamilyDetails();
                if (details == null) {
                    continue;
                }
                String relationshipToDeceased = null != details.getRelationship() ? details
                        .getRelationship().getValueCode() : null;
                boolean isNonParentRelation = isNonParentRelation(relationshipToDeceased);

                addAdoptedOutsideEnglandOrWalesErrors(errors, isNonParentRelation, relationshipToDeceased, details);
                addAdoptedOutErrors(errors, isNonParentRelation, details);
                addParentNotDeceasedErrors(errors, relationshipToDeceased, details);
                addParentAdoptedOutErrors(errors, relationshipToDeceased, details);
            }
        });
        return errors;
    }

    private void addAdoptedOutsideEnglandOrWalesErrors(List<FieldErrorResponse> errors,
                                                       boolean isNonParentRelation,
                                                       String relationshipToDeceased,
                                                       SolsApplicantFamilyDetails details) {
        if (isAdoptedOutsideEnglandOrWales(isNonParentRelation, relationshipToDeceased, details)) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR,
                    ADOPTED_OUTSIDE_ENGLAND_OR_WALES));
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR,
                    ADOPTED_OUTSIDE_ENGLAND_OR_WALES_WELSH));
        }
    }

    private void addAdoptedOutErrors(List<FieldErrorResponse> errors,
                                     boolean isNonParentRelation,
                                     SolsApplicantFamilyDetails details) {
        if (isNonParentRelation && NO.equalsIgnoreCase(details.getCoApplicantAdoptedIn())
                && YES.equalsIgnoreCase(details.getCoApplicantAdoptedOut())) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR, ADOPTED_OUT));
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR, ADOPTED_OUT_WELSH));
        }
    }

    private void addParentNotDeceasedErrors(List<FieldErrorResponse> errors,
                                            String relationshipToDeceased,
                                            SolsApplicantFamilyDetails details) {
        String grandchildParentIsDeceased = details.getGrandchildParentDieBeforeDeceased();
        String wholeNieceOrNephewParentIsDeceased = details.getWholeNieceOrNephewParentDieBeforeDeceased();
        String halfNieceOrNephewParentIsDeceased = details.getHalfNieceOrNephewParentDieBeforeDeceased();

        if (isParentNotDeceased(relationshipToDeceased, grandchildParentIsDeceased,
                wholeNieceOrNephewParentIsDeceased, halfNieceOrNephewParentIsDeceased)) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR, PARENT_IS_NOT_DECEASED));
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR, PARENT_IS_NOT_DECEASED_WELSH));
        }
    }

    private void addParentAdoptedOutErrors(List<FieldErrorResponse> errors, String relationshipToDeceased,
                                           SolsApplicantFamilyDetails details) {

        if (GRAND_CHILD.equalsIgnoreCase(relationshipToDeceased)
                && NO.equalsIgnoreCase(details.getGrandchildParentAdoptedIn())
                && YES.equalsIgnoreCase(details.getGrandchildParentAdoptedOut())) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR, PARENT_ADOPTED_OUT));
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR, PARENT_ADOPTED_OUT_WELSH));
        }
    }

    private boolean isParentNotDeceased(String relationshipToDeceased, String grandchildParentIsDeceased,
                                        String wholeNieceOrNephewParentIsDeceased,
                                        String halfNieceOrNephewParentIsDeceased) {
        return (GRAND_CHILD.equalsIgnoreCase(relationshipToDeceased) && NO.equalsIgnoreCase(grandchildParentIsDeceased))
                || (WHOLE_BLOOD_NIECE_OR_NEPHEW.equalsIgnoreCase(relationshipToDeceased)
                    && NO.equalsIgnoreCase(wholeNieceOrNephewParentIsDeceased))
                || (HALF_BLOOD_NIECE_OR_NEPHEW.equalsIgnoreCase(relationshipToDeceased)
                    && NO.equalsIgnoreCase(halfNieceOrNephewParentIsDeceased));
    }

    private boolean isAdoptedOutsideEnglandOrWales(boolean isNonParentRelation, String relationshipToDeceased,
                                                   SolsApplicantFamilyDetails details) {
        return (isNonParentRelation && YES.equalsIgnoreCase(details.getCoApplicantAdoptedIn())
                    && NO.equalsIgnoreCase(details.getCoApplicantAdoptionInEnglandOrWales()))
                || (GRAND_CHILD.equalsIgnoreCase(relationshipToDeceased)
                    && YES.equalsIgnoreCase(details.getGrandchildParentAdoptedIn())
                    && NO.equalsIgnoreCase(details.getGrandchildParentAdoptionInEnglandOrWales()));
    }

    private static boolean isNonParentRelation(String relationshipToDeceased) {

        return CHILD.equalsIgnoreCase(relationshipToDeceased)
                || GRAND_CHILD.equalsIgnoreCase(relationshipToDeceased)
                || WHOLE_BLOOD_SIBLING.equalsIgnoreCase(relationshipToDeceased)
                || HALF_BLOOD_SIBLING.equalsIgnoreCase(relationshipToDeceased)
                || WHOLE_BLOOD_NIECE_OR_NEPHEW.equalsIgnoreCase(relationshipToDeceased)
                || HALF_BLOOD_NIECE_OR_NEPHEW.equalsIgnoreCase(relationshipToDeceased);
    }
}
