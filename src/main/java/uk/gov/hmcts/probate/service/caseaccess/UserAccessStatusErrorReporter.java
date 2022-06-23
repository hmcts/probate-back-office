package uk.gov.hmcts.probate.service.caseaccess;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.caseaccess.AssignCaseAccessRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.AfterSubmitCallbackResponse;
import uk.gov.hmcts.probate.service.ConfirmationResponseService;
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
    private static final String MESSAGE_PENDING = "If your account has recently been added to the organisation, " +
            "it usually takes 30 minutes for the account to be moved from PENDING to ACTIVE.\n" +
            "If has been longer, then contact MyHMCTSsupport@justice.gov.uk to resolve this issue";
    private static final String MESSAGE_SUSPENDED = "Your organisation's " +
            "administrator will need to update your account status to ACTIVE";
    private final IdamApi idamApi;
    private final OrganisationsRetrievalService organisationsRetrievalService;
    private final ConfirmationResponseService confirmationResponseService;


    public AfterSubmitCallbackResponse getAccessError(int status, String message, String authorisationToken, String caseId,
                                 String caseTypeId) {
        ResponseEntity<Map<String, Object>> userResponse = idamApi.getUserDetails(authorisationToken);
        Map<String, Object> result = Objects.requireNonNull(userResponse.getBody());
        String userId = result.get("id").toString().toLowerCase();
        String userEmail = result.get("email").toString().toLowerCase();

        log.info("SAC: attempting getAccountStatus for CaseId {} of type {} to user {}", caseId, caseTypeId, userId);
        String accountStatus = organisationsRetrievalService.getAccountStatus(userEmail, authorisationToken,
                caseId);
        if (status == 400 && message.contains(SAC_ERROR_USER_ORG)) {
            AfterSubmitCallbackResponse afterSubmitCallbackResponse = confirmationResponseService
                    .getCaseAccessErrorConfirmation(accountStatus, getErrorSteps(accountStatus));
                return afterSubmitCallbackResponse;
            }

        return null;
    }

    private String getErrorSteps(String accountStatus) {
        if ("PENDING".equalsIgnoreCase(accountStatus)) {
            return MESSAGE_PENDING;
        } else if ("SUSPENDED".equalsIgnoreCase(accountStatus)) {
            return MESSAGE_SUSPENDED;
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
