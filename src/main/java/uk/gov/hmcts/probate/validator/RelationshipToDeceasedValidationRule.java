package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class RelationshipToDeceasedValidationRule {

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    public void validate(CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();
        String mainApplicantRelationship = caseData.getSolsApplicantRelationshipToDeceased();
        List<CollectionMember<AdditionalExecutor>> additionalExecutorList = caseData.getSolsAdditionalExecutorList();
        if (additionalExecutorList != null && !additionalExecutorList.isEmpty()) {
            for (CollectionMember<AdditionalExecutor> member : additionalExecutorList) {
                AdditionalExecutor executor = member.getValue();
                String coApplicantRelationship = executor.getApplicantFamilyDetails().getRelationshipToDeceased();
                if ("child".equalsIgnoreCase(mainApplicantRelationship)) {
                    if (!"child".equalsIgnoreCase(coApplicantRelationship)
                            && !"grandchild".equalsIgnoreCase(coApplicantRelationship)) {
                        String userMessage = businessValidationMessageRetriever
                                .getMessage("allowedChildOrGrandchild", null, Locale.UK);
                        String userMessageWelsh = businessValidationMessageRetriever
                                .getMessage("allowedChildOrGrandchild", null, Locale.UK);
                        throw new BusinessValidationException(userMessage,
                                "Relationship selection is invalid: " + caseDetails.getId(), userMessageWelsh);
                    }
                } else if ("parent".equalsIgnoreCase(mainApplicantRelationship)) {
                    if (!"parent".equalsIgnoreCase(coApplicantRelationship)) {
                        String userMessage = businessValidationMessageRetriever
                                .getMessage("allowedParent", null, Locale.UK);
                        String userMessageWelsh = businessValidationMessageRetriever
                                .getMessage("allowedParent", null, Locale.UK);
                        throw new BusinessValidationException(userMessage,
                                "Relationship selection is invalid: " + caseDetails.getId(), userMessageWelsh);
                    }
                }
            }
        }
    }
}
