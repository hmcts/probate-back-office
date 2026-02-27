package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.PARENT;
import static uk.gov.hmcts.probate.model.Constants.SIBLING;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
@RequiredArgsConstructor
public class IntestacyDeceasedDetailsValidationRule implements ValidationRule {
    public static final String ADOPTED_OUTSIDE_ENGLAND_OR_WALES = "adoptedOutsideEnglandOrWales";
    public static final String ADOPTED_OUTSIDE_ENGLAND_OR_WALES_WELSH = "adoptedOutsideEnglandOrWalesWelsh";
    public static final String DECEASED_ADOPTED_OUT = "deceasedAdoptedOut";
    public static final String DECEASED_ADOPTED_OUT_WELSH = "deceasedAdoptedOutWelsh";
    public static final String LIVING_DESCENDANTS = "livingDescendants";
    public static final String LIVING_DESCENDANTS_WELSH = "livingDescendantsWelsh";
    public static final String LIVING_PARENTS = "livingParents";
    public static final String LIVING_PARENTS_WELSH = "livingParentsWelsh";

    private final BusinessValidationMessageService businessValidationMessageService;

    public List<FieldErrorResponse> validate(CCDData ccdData) {
        List<FieldErrorResponse> errors = new ArrayList<>();
        var deceased = ccdData.getDeceased();
        if (deceased != null) {
            List<String> codes = new ArrayList<>();

            String relationship = ccdData.getSolsApplicantRelationshipToDeceased();
            boolean isSibling = SIBLING.equalsIgnoreCase(relationship);
            boolean isSiblingOrParent = isSibling || PARENT.equalsIgnoreCase(relationship);

            if (isSibling && YES.equalsIgnoreCase(deceased.getDeceasedAnyLivingDescendants())) {
                codes.add(LIVING_DESCENDANTS);
                codes.add(LIVING_DESCENDANTS_WELSH);
            }
            if (isSiblingOrParent && YES.equalsIgnoreCase(deceased.getDeceasedAnyLivingParents())) {
                codes.add(LIVING_PARENTS);
                codes.add(LIVING_PARENTS_WELSH);
            }
            if (isSiblingOrParent && NO.equalsIgnoreCase(deceased.getDeceasedAdoptionInEnglandOrWales())) {
                codes.add(ADOPTED_OUTSIDE_ENGLAND_OR_WALES);
                codes.add(ADOPTED_OUTSIDE_ENGLAND_OR_WALES_WELSH);
            }
            if (isSiblingOrParent && YES.equalsIgnoreCase(deceased.getDeceasedAdoptedOut())) {
                codes.add(DECEASED_ADOPTED_OUT);
                codes.add(DECEASED_ADOPTED_OUT_WELSH);
            }
            codes.forEach(code -> errors.add(businessValidationMessageService
                    .generateError(BUSINESS_ERROR, code)));
        }
        return errors;
    }
}
