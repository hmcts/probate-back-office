package uk.gov.hmcts.probate.transformer;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.DocumentCaseType;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.PRIMARY_APP_RELATIONSHIP_TO_DECEASED_ADOPTED_CHILD;
import static uk.gov.hmcts.probate.model.Constants.SOLS_APP_RELATIONSHIP_TO_DECEASED_ADOPTED_CHILD;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_TRUST_CORP;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_TRUST_CORP_SDJ;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
@RequiredArgsConstructor
public class HandOffLegacyTransformer {

    public void setHandOffToLegacySiteYes(CallbackRequest callbackRequest) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();

        if (StringUtils.isEmpty(caseData.getCaseHandedOffToLegacySite())
            || NO.equalsIgnoreCase(caseData.getCaseHandedOffToLegacySite())) {
            caseData.setCaseHandedOffToLegacySite(NO);

            if (SOLICITOR.equals(caseData.getApplicationType())
                && (TITLE_AND_CLEARING_TRUST_CORP_SDJ.equals(caseData.getTitleAndClearingType())
                || TITLE_AND_CLEARING_TRUST_CORP.equals(caseData.getTitleAndClearingType()))) {
                caseData.setCaseHandedOffToLegacySite(YES);
            }

            if (SOLICITOR.equals(caseData.getApplicationType())
                && (DocumentCaseType.GOP.getCaseType().equals(caseData.getCaseType())
                || DocumentCaseType.ADMON_WILL.getCaseType().equals(caseData.getCaseType())
                || DocumentCaseType.INTESTACY.getCaseType().equals(caseData.getCaseType()))
                && NO.equalsIgnoreCase(caseData.getDeceasedDomicileInEngWales())) {
                caseData.setCaseHandedOffToLegacySite(YES);
            }

            if (SOLICITOR.equals(caseData.getApplicationType())
                && (DocumentCaseType.GOP.getCaseType().equals(caseData.getCaseType())
                || DocumentCaseType.ADMON_WILL.getCaseType().equals(caseData.getCaseType()))
                && NO.equalsIgnoreCase(caseData.getWillAccessOriginal())
                && YES.equalsIgnoreCase(caseData.getWillAccessNotarial())) {
                caseData.setCaseHandedOffToLegacySite(YES);
            }

            if (SOLICITOR.equals(caseData.getApplicationType())
                && DocumentCaseType.INTESTACY.getCaseType().equals(caseData.getCaseType())
                && SOLS_APP_RELATIONSHIP_TO_DECEASED_ADOPTED_CHILD
                .equals(caseData.getSolsApplicantRelationshipToDeceased())) {
                caseData.setCaseHandedOffToLegacySite(YES);
            }

            if (PERSONAL.equals(caseData.getApplicationType())
                && DocumentCaseType.INTESTACY.getCaseType().equals(caseData.getCaseType())
                && PRIMARY_APP_RELATIONSHIP_TO_DECEASED_ADOPTED_CHILD
                .equals(caseData.getPrimaryApplicantRelationshipToDeceased())
                && YES.equalsIgnoreCase(caseData.getPrimaryApplicantAdoptionInEnglandOrWales())) {
                caseData.setCaseHandedOffToLegacySite(YES);
            }
        }
    }
}
