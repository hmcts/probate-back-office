package uk.gov.hmcts.probate.businessrule;

import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

public interface BusinessRule {

    boolean isApplicable(CaseData caseData);
}
