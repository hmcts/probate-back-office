package uk.gov.hmcts.probate.service.docmosis.assembler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssembleEntitlement {
    private final AssemblerBase assemblerBase;

    public List<ParagraphDetail> executorNotAccountedFor(ParagraphCode paragraphCode, CaseData caseData) {
        StringBuilder allApplicantsBuilder = new StringBuilder()
                .append(caseData.getPrimaryApplicantForenames() + " " + caseData.getPrimaryApplicantSurname())
                .append(", ");
        if (caseData.getAdditionalExecutorsApplying() != null) {
            for (CollectionMember<AdditionalExecutorApplying> executor : caseData.getAdditionalExecutorsApplying()) {
                allApplicantsBuilder.append(executor.getValue().getApplyingExecutorName()).append(", ");
            }
        }

        String applicants = allApplicantsBuilder.toString();
        applicants = applicants.substring(0, applicants.length() - 2);

        List<String> fieldValues = Arrays.asList(applicants);
        return assemblerBase.getTextParagraphDetailWithDefaultValue(paragraphCode, fieldValues);
    }

    public List<ParagraphDetail> entitlementAttorneyAndExec(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> entitlementLeadingGrantApplication(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getTextParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> entitlementNoTitle(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getTextParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> entitlementFamilyTree(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> entitlementConfirmDeath(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getTextParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> entitlementSubstitutedExec(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getTextParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> entitlementPrejudice(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> entitlementWrongExec(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getTextParagraphDetails(paragraphCode);
    }


}
