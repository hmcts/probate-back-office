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
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.Constants.CHILD;
import static uk.gov.hmcts.probate.model.Constants.GRAND_CHILD;
import static uk.gov.hmcts.probate.model.Constants.PARENT;
import static uk.gov.hmcts.probate.model.Constants.SIBLING;

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
                List<String> codes = new ArrayList<>();
                String coApplicantAdoptedInEnglandOrWales = details.getChildAdoptionInEnglandOrWales();
                String coApplicantAdoptedOut = details.getChildAdoptedOut();
                String applicantParentIsDeceased = details.getChildDieBeforeDeceased();
                String relationshipToDeceased = details.getRelationship() != null
                        ? details.getRelationship().getValueCode() : null;

                boolean isRelevantRelation = CHILD.equalsIgnoreCase(relationshipToDeceased)
                        || GRAND_CHILD.equalsIgnoreCase(relationshipToDeceased)
                        || PARENT.equalsIgnoreCase(relationshipToDeceased)
                        || SIBLING.equalsIgnoreCase(relationshipToDeceased);

                if (isRelevantRelation && NO.equalsIgnoreCase(coApplicantAdoptedInEnglandOrWales)) {
                    codes.add(ADOPTED_OUTSIDE_ENGLAND_OR_WALES);
                    codes.add(ADOPTED_OUTSIDE_ENGLAND_OR_WALES_WELSH);
                }
                if (isRelevantRelation && YES.equalsIgnoreCase(coApplicantAdoptedOut)) {
                    codes.add(ADOPTED_OUT);
                    codes.add(ADOPTED_OUT_WELSH);
                }
                if (GRAND_CHILD.equalsIgnoreCase(relationshipToDeceased)
                        && NO.equalsIgnoreCase(applicantParentIsDeceased)) {
                    codes.add(PARENT_IS_NOT_DECEASED);
                    codes.add(PARENT_IS_NOT_DECEASED_WELSH);
                }

                for (String code : codes) {
                    errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR, code));
                }
            }
        });
        return errors;
    }
}
