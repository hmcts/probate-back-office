package uk.gov.hmcts.probate.service.caseaccess;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.caseaccess.AssignCaseAccessRequest;
import uk.gov.hmcts.probate.service.IdamApi;
import uk.gov.hmcts.probate.service.organisations.OrganisationsRetrievalService;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserAccessStatusErrorReporter {

    private static final String SAC_ERROR_USER_ORG = "Intended assignee has to be in the same organisation "
            + "as that of the invoker.";
    private final IdamApi idamApi;
    private final OrganisationsRetrievalService organisationsRetrievalService;


    public String getAccessError(int status, String message, String authorisationToken, String caseId,
                                 String caseTypeId) {
        ResponseEntity<Map<String, Object>> userResponse = idamApi.getUserDetails(authorisationToken);
        Map<String, Object> result = Objects.requireNonNull(userResponse.getBody());
        String userId = result.get("id").toString().toLowerCase();
        String userEmail = result.get("email").toString().toLowerCase();

        log.info("SAC: attempting userStatus for CaseId {} of type {} to user {}", caseId, caseTypeId, userId);
        String accountStatus = organisationsRetrievalService.getAccountStatus(userEmail, authorisationToken,
                caseTypeId);
        if (status == 400) {
            if (SAC_ERROR_USER_ORG.equals(message)) {
                if (!"ACTIVE".equalsIgnoreCase(accountStatus)) {
                    return "Account status is: " + accountStatus + ". please correct";
                }

            }
        }

        return null;
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
