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

@Service
@Slf4j
@RequiredArgsConstructor
public class AssignCaseAccessService {

    private final CcdDataStoreService ccdDataStoreService;
    private final AuthTokenGenerator authTokenGenerator;
    private final AssignCaseAccessClient assignCaseAccessClient;
    private final IdamApi idamApi;


    public void assignCaseAccess(String caseId, String authorisationToken, String caseTypeId) {
        ResponseEntity<Map<String, Object>> userResponse = idamApi.getUserDetails(authorisationToken);
        Map<String, Object> result = Objects.requireNonNull(userResponse.getBody());
        String userId = result.get("id").toString().toLowerCase();

        log.info("CaseId: {} of type {} assigning case access to user {} for token {}", caseId, caseTypeId, userId,
                authorisationToken);

        String serviceToken = authTokenGenerator.generate();
        assignCaseAccessClient.assignCaseAccess(
            authorisationToken,
            serviceToken,
            true,
            buildAssignCaseAccessRequest(caseId, userId, caseTypeId)
        );
        ccdDataStoreService.removeCreatorRole(caseId, authorisationToken);

        log.info("CaseId: {} assigned case access to user {}", caseId, userId);
    }

    private AssignCaseAccessRequest buildAssignCaseAccessRequest(String caseId, String userId, String caseTypeId) {
        return AssignCaseAccessRequest
            .builder()
            .caseId(caseId)
            .assigneeId(userId)
            .caseTypeId(caseTypeId)
            .build();
    }
}
