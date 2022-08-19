package uk.gov.hmcts.probate.service.caseaccess;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.response.AfterSubmitCallbackResponse;
import uk.gov.hmcts.probate.service.ConfirmationResponseService;
import uk.gov.hmcts.probate.service.IdamApi;
import uk.gov.hmcts.probate.service.organisations.OrganisationsRetrievalService;

import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserAccessStatusErrorReporter {

    private final IdamApi idamApi;
    private final OrganisationsRetrievalService organisationsRetrievalService;
    private final ConfirmationResponseService confirmationResponseService;
    static final String SAC_ERROR_USER_ORG = "Intended assignee has to be in the same organisation "
            + "as that of the invoker.";

    public AfterSubmitCallbackResponse getAccessError(String authorisationToken,
                                                      String caseId, String caseTypeId) {
        ResponseEntity<Map<String, Object>> userResponse = idamApi.getUserDetails(authorisationToken);
        Map<String, Object> result = Objects.requireNonNull(userResponse.getBody());
        String userId = result.get("id").toString().toLowerCase();
        String userEmail = result.get("email").toString();

        log.info("SAC: attempting getAccountStatus for CaseId {} of type {} to user {}", caseId, caseTypeId, userId);
        //TODO: leaving this in for now, incase we need to reflect the current status in the message
        String accountStatus = organisationsRetrievalService.getUserAccountStatus(userEmail, authorisationToken,
                caseId);
        return confirmationResponseService
                .getCaseAccessErrorConfirmation(caseId);
    }
}
