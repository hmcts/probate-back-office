package uk.gov.hmcts.probate.service.caseaccess;

import feign.FeignException;
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


    public void assignCaseAccess(String authorisationToken, String caseId, String caseTypeId) {
        ResponseEntity<Map<String, Object>> userResponse = idamApi.getUserDetails(authorisationToken);
        Map<String, Object> result = Objects.requireNonNull(userResponse.getBody());
        String userId = result.get("id").toString().toLowerCase();

        String serviceToken = authTokenGenerator.generate();
        log.info("SAC: attempting assignCaseAccess for CaseId {} of type {} to user {}", caseId, caseTypeId, userId);
        try {
//            assignCaseAccessClient.assignCaseAccess(
//                    authorisationToken,
//                    serviceToken,
//                    true,
//                    buildAssignCaseAccessRequest(caseId, userId, caseTypeId)
//            );

            log.info("SAC: assignCaseAccess completed for CaseId {} of type {} to user {}", caseId, caseTypeId, userId);
            ccdDataStoreService.removeCreatorRole(caseId, authorisationToken);
            log.info("SAC: removeCreatorRole completed for CaseId {} of type {} to user {}",
                    caseId, caseTypeId, userId);
        } catch (FeignException feignException) {
            log.info("SAC: assignCaseAccess errored for CaseId {} of type {} to user {}, with exeption {}",
                    caseId, caseTypeId, userId, feignException.getMessage());
        }

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
