package uk.gov.hmcts.probate.service;

import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.util.List;

public interface LegacySearchService {

    List<CollectionMember<CaseMatch>> findLegacyCaseMatches(CaseDetails caseDetails);

    List<CollectionMember<CaseMatch>> importLegacyRows(CaseData data);
}
