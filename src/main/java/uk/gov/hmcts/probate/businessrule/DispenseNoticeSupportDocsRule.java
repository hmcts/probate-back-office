package uk.gov.hmcts.probate.businessrule;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
public class DispenseNoticeSupportDocsRule implements BusinessRule {

    public boolean isApplicable(CaseData caseData) {
        boolean isDispenseWithNotice = YES.equals(caseData.getDispenseWithNotice());
        boolean hasSupportingDocs = StringUtils.isNotEmpty(
                caseData.getDispenseWithNoticeSupportingDocs());

        return isDispenseWithNotice && hasSupportingDocs;
    }
}