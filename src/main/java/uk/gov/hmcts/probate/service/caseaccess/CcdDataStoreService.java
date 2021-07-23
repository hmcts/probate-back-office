package uk.gov.hmcts.probate.service.caseaccess;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.IdamApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Arrays.asList;

@Component
@Slf4j
@RequiredArgsConstructor
public class CcdDataStoreService {

    private final AuthTokenGenerator authTokenGenerator;
    private final IdamApi idamApi;
    private final CaseRoleClient caseRoleClient;

    public void removeCreatorRole(CaseDetails caseDetails, String authorisationToken) {
        removeRole(caseDetails, authorisationToken);
    }

    private void removeRole(CaseDetails caseDetails, String authorisationToken) {
        ResponseEntity<Map<String, Object>> userResponse = idamApi.getUserDetails(authorisationToken);
        Map<String, Object> result = Objects.requireNonNull(userResponse.getBody());
        String userId = result.get("id").toString().toLowerCase();
        String caseId = caseDetails.getId().toString();

        log.info("CaseID: {} removing [CREATOR] case roles from user {}", caseId, userId);

        caseRoleClient.removeCaseRoles(
            authorisationToken,
            authTokenGenerator.generate(),
            buildRemoveUserRolesRequest(caseId, userId)
        );

        log.info("CaseID: {} removed [CREATOR] case roles from user {}", caseId, userId);
    }

    private RemoveUserRolesRequest buildRemoveUserRolesRequest(String caseId, String userId) {
        return RemoveUserRolesRequest
            .builder()
            .caseUsers(getCaseUsers(caseId, userId))
            .build();
    }

    private CaseUser buildCaseUser(String caseId, String caseRole, String userId) {
        return CaseUser.builder()
            .caseId(caseId)
            .userId(userId)
            .caseRole(caseRole)
            .build();
    }

    private List<CaseUser> getCaseUsers(String caseId, String userId) {
        return asList(
            buildCaseUser(caseId, "[CREATOR]", userId)
        );
    }
}