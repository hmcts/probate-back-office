package uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.businessrule.IhtEstateNotCompletedBusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.IhtEstateConfirmCaseExtra;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import static uk.gov.hmcts.probate.model.Constants.IHT_ESTATE_CONFIRM;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.Constants.SOLICITOR_SPOUSE;
import static uk.gov.hmcts.probate.model.Constants.SOLICITOR_CHILD;
import static uk.gov.hmcts.probate.model.Constants.SOLICITOR_GRANDCHILD;
import static uk.gov.hmcts.probate.model.Constants.SOLICITOR_PARENT;
import static uk.gov.hmcts.probate.model.Constants.SOLICITOR_SIBLING;
import static uk.gov.hmcts.probate.model.Constants.CHILD;
import static uk.gov.hmcts.probate.model.Constants.GRAND_CHILD;
import static uk.gov.hmcts.probate.model.Constants.PARENT;
import static uk.gov.hmcts.probate.model.Constants.SIBLING;
import static uk.gov.hmcts.probate.model.Constants.WHOLE_BLOOD_SIBLING;
import static uk.gov.hmcts.probate.model.Constants.HALF_BLOOD_SIBLING;
import static uk.gov.hmcts.probate.model.Constants.WHOLE_BLOOD_NIECE_OR_NEPHEW;
import static uk.gov.hmcts.probate.model.Constants.HALF_BLOOD_NIECE_OR_NEPHEW;
import static uk.gov.hmcts.probate.model.Constants.LS_SPOUSE;
import static uk.gov.hmcts.probate.model.Constants.LS_WHOLE_SIBLING;
import static uk.gov.hmcts.probate.model.Constants.LS_HALF_SIBLING;
import static uk.gov.hmcts.probate.model.Constants.LS_WHOLE_NIECE_NEPHEW;
import static uk.gov.hmcts.probate.model.Constants.LS_HALF_NIECE_NEPHEW;
import static uk.gov.hmcts.probate.model.Constants.WELSH_SPOUSE;
import static uk.gov.hmcts.probate.model.Constants.WELSH_CHILD;
import static uk.gov.hmcts.probate.model.Constants.WELSH_GRANDCHILD;
import static uk.gov.hmcts.probate.model.Constants.WELSH_PARENT;
import static uk.gov.hmcts.probate.model.Constants.WELSH_SIBLING;
import static uk.gov.hmcts.probate.model.Constants.WELSH_WHOLE_BLOOD_SIBLING;
import static uk.gov.hmcts.probate.model.Constants.WELSH_HALF_BLOOD_SIBLING;
import static uk.gov.hmcts.probate.model.Constants.WELSH_WHOLE_BLOOD_NIECE_OR_NEPHEW;
import static uk.gov.hmcts.probate.model.Constants.WELSH_HALF_BLOOD_NIECE_OR_NEPHEW;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_INTESTACY;

@Component
@AllArgsConstructor
public class SolicitorLegalStatementPDFDecorator {
    private final CaseExtraDecorator caseExtraDecorator;
    private final IhtEstateNotCompletedBusinessRule ihtEstateNotCompletedBusinessRule;

    private static final Map<String, Map<String, String>> RELATIONSHIP_MAP = Map.of(
            "ENGLISH_APPLICANT", Map.of(
                    SOLICITOR_SPOUSE, LS_SPOUSE,
                    SOLICITOR_CHILD, CHILD,
                    SOLICITOR_GRANDCHILD, GRAND_CHILD,
                    SOLICITOR_PARENT, PARENT,
                    SOLICITOR_SIBLING, SIBLING
            ),
            "ENGLISH_EXECUTOR", Map.of(
                    CHILD, CHILD,
                    GRAND_CHILD, GRAND_CHILD,
                    PARENT, PARENT,
                    WHOLE_BLOOD_SIBLING, LS_WHOLE_SIBLING,
                    HALF_BLOOD_SIBLING, LS_HALF_SIBLING,
                    WHOLE_BLOOD_NIECE_OR_NEPHEW, LS_WHOLE_NIECE_NEPHEW,
                    HALF_BLOOD_NIECE_OR_NEPHEW, LS_HALF_NIECE_NEPHEW
            ),
            "WELSH_APPLICANT", Map.of(
                    SOLICITOR_SPOUSE, WELSH_SPOUSE,
                    SOLICITOR_CHILD, WELSH_CHILD,
                    SOLICITOR_GRANDCHILD, WELSH_GRANDCHILD,
                    SOLICITOR_PARENT, WELSH_PARENT,
                    SOLICITOR_SIBLING, WELSH_SIBLING
            ),
            "WELSH_EXECUTOR", Map.of(
                    CHILD, WELSH_CHILD,
                    GRAND_CHILD, WELSH_GRANDCHILD,
                    PARENT, WELSH_PARENT,
                    WHOLE_BLOOD_SIBLING, WELSH_WHOLE_BLOOD_SIBLING,
                    HALF_BLOOD_SIBLING, WELSH_HALF_BLOOD_SIBLING,
                    WHOLE_BLOOD_NIECE_OR_NEPHEW, WELSH_WHOLE_BLOOD_NIECE_OR_NEPHEW,
                    HALF_BLOOD_NIECE_OR_NEPHEW, WELSH_HALF_BLOOD_NIECE_OR_NEPHEW
            )
    );

    private static String getRelationship(CaseData caseData, int i, AdditionalExecutorApplying ex, String lang) {
        if (i == 0) {
            String key = lang.equals("ENGLISH") ? "ENGLISH_APPLICANT" : "WELSH_APPLICANT";
            return RELATIONSHIP_MAP.get(key).getOrDefault(caseData.getSolsApplicantRelationshipToDeceased(),
                    "");
        } else {
            String rel = ex.getApplicantFamilyDetails().getRelationshipToDeceased();
            String key = lang.equals("ENGLISH") ? "ENGLISH_EXECUTOR" : "WELSH_EXECUTOR";
            return RELATIONSHIP_MAP.get(key).getOrDefault(rel, "");
        }
    }

    public String decorate(CaseData caseData) {
        String decoration = "";
        if (ihtEstateNotCompletedBusinessRule.isApplicable(caseData)) {
            IhtEstateConfirmCaseExtra ihtEstateConfirmCaseExtra = IhtEstateConfirmCaseExtra.builder()
                    .showIhtEstate(YES)
                    .ihtEstateText(IHT_ESTATE_CONFIRM)
                    .build();
            decoration = caseExtraDecorator.decorate(ihtEstateConfirmCaseExtra);
        }
        if (GRANT_TYPE_INTESTACY.equals(caseData.getSolsWillType())) {
            List<String> englishDescriptions = new ArrayList<>();
            List<String> welshDescriptions = new ArrayList<>();
            List<CollectionMember<AdditionalExecutorApplying>> executors = caseData
                    .getExecutorsApplyingLegalStatement();

            if (executors != null && !executors.isEmpty()) {
                for (int i = 0; i < executors.size(); i++) {
                    AdditionalExecutorApplying ex = executors.get(i).getValue();
                    englishDescriptions.add(ex.getApplyingExecutorName() + " is the " + getRelationship(caseData, i, ex,
                            "ENGLISH"));
                    welshDescriptions.add(ex.getApplyingExecutorName() + " yw " + getRelationship(caseData, i, ex,
                            "WELSH"));
                }
            }

            decoration = caseExtraDecorator.combineDecorations(decoration,
                    caseExtraDecorator.decorate(IntestacyMultipleApplicantsCaseExtra.builder()
                            .englishCoApplicantDescriptions(englishDescriptions)
                            .welshCoApplicantDescriptions(welshDescriptions)
                            .build()));
        }
        return decoration;
    }
}
