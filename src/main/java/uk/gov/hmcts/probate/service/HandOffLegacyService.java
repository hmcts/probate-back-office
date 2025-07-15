package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.DocumentCaseType;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReason;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReasonId;

import java.util.ArrayList;
import java.util.List;

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

            return isForeignWill(caseData) || isForeignDomicile(caseData)
                    || isTrustCorporation(caseData) || isExtendedIntestacy(caseData);
        } else {
            return YES.equalsIgnoreCase(caseData.getCaseHandedOffToLegacySite());
        }
    }

    public List<CollectionMember<HandoffReason>> setHandoffReason(CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();
        List<uk.gov.hmcts.probate.model.ccd.raw.CollectionMember<HandoffReason>> handoffReasons = caseData
                .getBoHandoffReasonList();

        List<CollectionMember<HandoffReason>> handoffReasonsList;
        if (handoffReasons == null) {
            handoffReasonsList = new ArrayList<>();
        } else {
            handoffReasonsList = handoffReasons.stream()
                    .map(cm -> new CollectionMember<>(cm.getId(), cm.getValue()))
                    .toList();
        }
        if ((StringUtils.isEmpty(caseData.getCaseHandedOffToLegacySite())
                || YES.equalsIgnoreCase(caseData.getCaseHandedOffToLegacySite()))
                && (null == caseData.getBoHandoffReasonList() || caseData.getBoHandoffReasonList().isEmpty())) {
            if (isTrustCorporation(caseData)) {
                handoffReasonsList.add(buildHandOffReason(HandoffReasonId.TRUST_CORPORATION));
            }

            if (isForeignDomicile(caseData)) {
                handoffReasonsList.add(buildHandOffReason(HandoffReasonId.FOREIGN_DOMICILE));
            }

            if (isForeignWill(caseData)) {
                handoffReasonsList.add(buildHandOffReason(HandoffReasonId.FOREIGN_WILL));
            }

            if (isExtendedIntestacy(caseData)) {
                handoffReasonsList.add(buildHandOffReason(HandoffReasonId.EXTENDED_INTESTACY));
            }
        }
        return handoffReasonsList;
    }

    private CollectionMember<HandoffReason> buildHandOffReason(HandoffReasonId reasonId) {
        HandoffReason handoffReason = HandoffReason.builder()
                .caseHandoffReason(reasonId)
                .build();
        return new CollectionMember<>(null, handoffReason);
    }

    private boolean isTrustCorporation(CaseData caseData) {
        return SOLICITOR.equals(caseData.getApplicationType())
            && (TITLE_AND_CLEARING_TRUST_CORP_SDJ.equals(caseData.getTitleAndClearingType())
            || TITLE_AND_CLEARING_TRUST_CORP.equals(caseData.getTitleAndClearingType()));
    }

    private boolean isForeignDomicile(CaseData caseData) {
        final String caseType = caseData.getCaseType();
        return SOLICITOR.equals(caseData.getApplicationType())
            && (DocumentCaseType.GOP.getCaseType().equals(caseType)
            || DocumentCaseType.ADMON_WILL.getCaseType().equals(caseType)
            || DocumentCaseType.INTESTACY.getCaseType().equals(caseType))
            && NO.equalsIgnoreCase(caseData.getDeceasedDomicileInEngWales());
    }

    private boolean isForeignWill(CaseData caseData) {
        final String caseType = caseData.getCaseType();
        return SOLICITOR.equals(caseData.getApplicationType())
            && (DocumentCaseType.GOP.getCaseType().equals(caseType)
            || DocumentCaseType.ADMON_WILL.getCaseType().equals(caseType))
            && NO.equalsIgnoreCase(caseData.getWillAccessOriginal())
            && YES.equalsIgnoreCase(caseData.getWillAccessNotarial());
    }

    private boolean isExtendedIntestacy(CaseData caseData) {
        final boolean isIntestacyCase = DocumentCaseType.INTESTACY.getCaseType().equals(caseData.getCaseType());
        return (SOLICITOR.equals(caseData.getApplicationType())
                && isIntestacyCase
                && SOLS_APP_RELATIONSHIP_TO_DECEASED_ADOPTED_CHILD
                    .equals(caseData.getSolsApplicantRelationshipToDeceased()))
            || (PERSONAL.equals(caseData.getApplicationType())
                && isIntestacyCase
                && PRIMARY_APP_RELATIONSHIP_TO_DECEASED_ADOPTED_CHILD
                    .equals(caseData.getPrimaryApplicantRelationshipToDeceased())
                && YES.equalsIgnoreCase(caseData.getPrimaryApplicantAdoptionInEnglandOrWales()));
    }
}
