package uk.gov.hmcts.probate.service.caseaccess;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.caseaccess.AssignCaseAccessRequest;
import uk.gov.hmcts.probate.service.IdamApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.util.Map;
import java.util.Objects;

import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.GRANT_OF_REPRESENTATION;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssignCaseAccessService {

    private final CcdDataStoreService ccdDataStoreService;
    private final AuthTokenGenerator authTokenGenerator;
    private final AssignCaseAccessClient assignCaseAccessClient;
    private final IdamApi idamApi;


    public void assignCaseAccess(String caseId, String authorisationToken) {
        ResponseEntity<Map<String, Object>> userResponse = idamApi.getUserDetails(authorisationToken);
        Map<String, Object> result = Objects.requireNonNull(userResponse.getBody());
        String userId = result.get("id").toString().toLowerCase();

        log.info("CaseId: {} assigning case access to user {}", caseId, userId);

        String serviceToken = authTokenGenerator.generate();
        log.info("serviceToken: {}", serviceToken);
        log.info("authorisationToken: {}", authorisationToken);
        assignCaseAccessClient.assignCaseAccess(
            authorisationToken,
            serviceToken,
            true,
            buildAssignCaseAccessRequest(caseId, userId)
        );
        ccdDataStoreService.removeCreatorRole(caseId, authorisationToken);

        log.info("CaseId: {} assigned case access to user {}", caseId, userId);
    }

    private AssignCaseAccessRequest buildAssignCaseAccessRequest(String caseId, String userId) {
        return AssignCaseAccessRequest
            .builder()
            .caseId(caseId)
            .assigneeId(userId)
            .caseTypeId(GRANT_OF_REPRESENTATION.getName())
            .build();
    }
}