package uk.gov.hmcts.probate.service;

import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.probate.model.cases.CaseData;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CoreCaseDataService {

    CaseDetails createCase(Object object, CcdCaseType ccdCaseType, EventId eventId, SecurityDTO securityDTO);

    Optional<CaseDetails> retrieveCaseByLegacyId(String caseType, Long legacyId, SecurityDTO securityDTO);

    CaseDetails updateCaseAsCaseworker(CcdCaseType caseType, String caseId, LocalDateTime lastModified,
                                       Object caseData, EventId eventId,
                                       SecurityDTO securityDTO, String description, String summary);

    CaseDetails updateCaseAsCitizen(CcdCaseType ccdCaseType, String caseId, CaseData caseData, EventId eventId,
                                       SecurityDTO securityDTO, String description, String summary);

    CaseDetails readForCaseWorker(CcdCaseType caseType, String caseId, SecurityDTO securityDTO);
}
