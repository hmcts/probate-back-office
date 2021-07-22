package uk.gov.hmcts.probate.service.caseaccess;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.IdamApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.util.Map;
import java.util.Objects;

import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.GRANT_OF_REPRESENTATION;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssignCaseAccessService {

    private final AuthTokenGenerator authTokenGenerator;
    private final AssignCaseAccessClient assignCaseAccessClient;
    private final IdamApi idamApi;


    public void assignCaseAccess(CaseDetails caseDetails, String authorisationToken) {
        ResponseEntity<Map<String, Object>> userResponse = idamApi.getUserDetails(authorisationToken);
        Map<String, Object> result = Objects.requireNonNull(userResponse.getBody());
        String userId = result.get("id").toString().toLowerCase();

        log.info("CaseId: {} assigning case access to user {}", caseDetails.getId(), userId);

        String serviceToken = authTokenGenerator.generate();
        assignCaseAccessClient.assignCaseAccess(
            authorisationToken,
            serviceToken,
            true,
            buildAssignCaseAccessRequest(caseDetails, userId)
        );

        log.info("CaseId: {} assigned case access to user {}", caseDetails.getId(), userId);
    }

    private AssignCaseAccessRequest buildAssignCaseAccessRequest(CaseDetails caseDetails, String userId) {
        return AssignCaseAccessRequest
            .builder()
            .caseId(caseDetails.getId().toString())
            .assigneeId(userId)
            .caseTypeId(GRANT_OF_REPRESENTATION.getName())
            .build();
    }
}