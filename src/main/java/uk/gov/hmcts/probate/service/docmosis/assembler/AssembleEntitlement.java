package uk.gov.hmcts.probate.service.docmosis.assembler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssembleEntitlement {
    private final AssemblerBase assemblerBase;

    public ParagraphDetail executorNotAccountedFor(ParagraphCode paragraphCode, CaseData caseData) {
        StringBuilder allApplicantsBuilder = new StringBuilder()
                .append(caseData.getPrimaryApplicantForenames() + " " + caseData.getPrimaryApplicantSurname())
                .append(",");
        if (caseData.getAdditionalExecutorsApplying() != null) {
            for (CollectionMember<AdditionalExecutorApplying> executor : caseData.getAdditionalExecutorsApplying()) {
                allApplicantsBuilder.append(executor.getValue().getApplyingExecutorName()).append(",");
            }
        }

        String applicants = allApplicantsBuilder.toString();
        applicants = applicants.substring(0, applicants.length() - 1);

        return assemblerBase.getTextParagraphDetailWithDefaultValue(paragraphCode, applicants);
    }

}
