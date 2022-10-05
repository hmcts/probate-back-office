package uk.gov.hmcts.probate.transformer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.businessrule.NoDocumentsRequiredBusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType.INTESTACY;

@RequiredArgsConstructor
@Service
public class EvidenceHandledTransformer {

    private final NoDocumentsRequiredBusinessRule noDocumentsRequiredBusinessRule;
    private static final String INTESTACY_NAME = INTESTACY.getName();

    public void updateEvidenceHandled(CaseData data) {
        if (data.getApplicationType() != null) {
            String evidenceHandled = null;

            if ((SOLICITOR.equals(data.getApplicationType())
                    && noDocumentsRequiredBusinessRule.isApplicable(data))
                    || (PERSONAL.equals(data.getApplicationType())
                    && INTESTACY_NAME.equals(data.getCaseType())
                    && YES.equals(data.getPrimaryApplicantNotRequiredToSendDocuments()))) {
                evidenceHandled = NO;
            }
            data.setEvidenceHandled(evidenceHandled);
        }
    }

    public void updateEvidenceHandledToNo(CaseData data) {
        data.setEvidenceHandled(NO);
    }
}
