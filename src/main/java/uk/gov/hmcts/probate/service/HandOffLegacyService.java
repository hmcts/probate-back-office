package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.DocumentCaseType;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.PRIMARY_APP_RELATIONSHIP_TO_DECEASED_ADOPTED_CHILD;
import static uk.gov.hmcts.probate.model.Constants.SOLS_APP_RELATIONSHIP_TO_DECEASED_ADOPTED_CHILD;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_TRUST_CORP;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_TRUST_CORP_SDJ;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Slf4j
@Component
@Service
@RequiredArgsConstructor
public class HandOffLegacyService {

    public Boolean setCaseToHandedOffToLegacySite(CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();
        if (StringUtils.isEmpty(caseData.getCaseHandedOffToLegacySite())
            || NO.equalsIgnoreCase(caseData.getCaseHandedOffToLegacySite())) {

            if (SOLICITOR.equals(caseData.getApplicationType())
                && (TITLE_AND_CLEARING_TRUST_CORP_SDJ.equals(caseData.getTitleAndClearingType())
                || TITLE_AND_CLEARING_TRUST_CORP.equals(caseData.getTitleAndClearingType()))) {
                return true;
            }

            if (SOLICITOR.equals(caseData.getApplicationType())
                && (DocumentCaseType.GOP.getCaseType().equals(caseData.getCaseType())
                || DocumentCaseType.ADMON_WILL.getCaseType().equals(caseData.getCaseType())
                || DocumentCaseType.INTESTACY.getCaseType().equals(caseData.getCaseType()))
                && NO.equalsIgnoreCase(caseData.getDeceasedDomicileInEngWales())) {
                return true;
            }

            if (SOLICITOR.equals(caseData.getApplicationType())
                && (DocumentCaseType.GOP.getCaseType().equals(caseData.getCaseType())
                || DocumentCaseType.ADMON_WILL.getCaseType().equals(caseData.getCaseType()))
                && NO.equalsIgnoreCase(caseData.getWillAccessOriginal())
                && YES.equalsIgnoreCase(caseData.getWillAccessNotarial())) {
                return true;
            }

            if (SOLICITOR.equals(caseData.getApplicationType())
                && DocumentCaseType.INTESTACY.getCaseType().equals(caseData.getCaseType())
                && SOLS_APP_RELATIONSHIP_TO_DECEASED_ADOPTED_CHILD
                .equals(caseData.getSolsApplicantRelationshipToDeceased())) {
                return true;
            }

            if (PERSONAL.equals(caseData.getApplicationType())
                && DocumentCaseType.INTESTACY.getCaseType().equals(caseData.getCaseType())
                && PRIMARY_APP_RELATIONSHIP_TO_DECEASED_ADOPTED_CHILD
                .equals(caseData.getPrimaryApplicantRelationshipToDeceased())
                && YES.equalsIgnoreCase(caseData.getPrimaryApplicantAdoptionInEnglandOrWales())) {
                return true;
            }

            return false;

        } else {
            return YES.equalsIgnoreCase(caseData.getCaseHandedOffToLegacySite());
        }
    }
}
