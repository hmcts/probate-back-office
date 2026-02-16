package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;
import static uk.gov.hmcts.probate.model.Constants.CHILD;
import static uk.gov.hmcts.probate.model.Constants.GRAND_CHILD;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.SIBLING;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
@RequiredArgsConstructor
public class IntestacyApplicantDetailsValidationRule implements ValidationRule {
    public static final String ADOPTED_OUTSIDE_ENGLAND_OR_WALES = "adoptedOutsideEnglandOrWales";
    public static final String ADOPTED_OUTSIDE_ENGLAND_OR_WALES_WELSH = "adoptedOutsideEnglandOrWales";
    public static final String DECEASED_CHILD_DEAD = "deceasedChildDead";
    public static final String DECEASED_CHILD_DEAD_WELSH = "deceasedChildDeadWales";
    public static final String ADOPTED_OUT = "adoptedOut";
    public static final String ADOPTED_OUT_WELSH = "adoptedOut";
    public static final String SIBLING_NOT_DIED = "siblingNotDied";
    public static final String SIBLING_NOT_DIED_WELSH = "siblingNotDied";

    private final BusinessValidationMessageService businessValidationMessageService;

    public List<FieldErrorResponse> validate(CCDData ccdData) {
        List<FieldErrorResponse> errors = new ArrayList<>();
        var applicant = ccdData.getApplicant();
        if (applicant != null) {
            List<String> codes = new ArrayList<>();
            String childAlive = applicant.getChildAlive();
            String primaryApplicantParentAdoptionInEnglandOrWales =
                    applicant.getPrimaryApplicantParentAdoptionInEnglandOrWales();
            String primaryApplicantParentAdoptedOut = applicant.getPrimaryApplicantParentAdoptedOut();
            String applicantAdoptedInEnglandOrWales = applicant.getPrimaryApplicantAdoptionInEnglandOrWales();
            String applicantAdoptedOut = applicant.getPrimaryApplicantAdoptedOut();

            boolean isRelevantRelation = SIBLING.equalsIgnoreCase(ccdData.getSolsApplicantRelationshipToDeceased())
                    || CHILD.equalsIgnoreCase(ccdData.getSolsApplicantRelationshipToDeceased())
                    || GRAND_CHILD.equalsIgnoreCase(ccdData.getSolsApplicantRelationshipToDeceased());

            if (SIBLING.equalsIgnoreCase(ccdData.getSolsApplicantRelationshipToDeceased()) && NO
                    .equalsIgnoreCase(applicant.getAnyLivingWholeBloodSiblings())) {
                codes.add(SIBLING_NOT_DIED);
                codes.add(SIBLING_NOT_DIED_WELSH);
            }

            if (NO.equalsIgnoreCase(childAlive)) {
                codes.add(DECEASED_CHILD_DEAD);
                codes.add(DECEASED_CHILD_DEAD_WELSH);
            }

            if (isRelevantRelation && (NO.equalsIgnoreCase(applicantAdoptedInEnglandOrWales)
                    || NO.equalsIgnoreCase(primaryApplicantParentAdoptionInEnglandOrWales))) {
                codes.add(ADOPTED_OUTSIDE_ENGLAND_OR_WALES);
                codes.add(ADOPTED_OUTSIDE_ENGLAND_OR_WALES_WELSH);
            }
            if (isRelevantRelation && (YES.equalsIgnoreCase(applicantAdoptedOut)
                    || YES.equalsIgnoreCase(primaryApplicantParentAdoptedOut))) {
                codes.add(ADOPTED_OUT);
                codes.add(ADOPTED_OUT_WELSH);
            }

            for (String code : codes) {
                errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR, code));
            }
        }
        return errors;
    }
}
