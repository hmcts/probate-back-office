package uk.gov.hmcts.probate.service.wa;

import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.service.IdamApi;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.wa.GetTasksCompletableResponse;
import uk.gov.hmcts.probate.model.wa.SearchEventAndCase;
import uk.gov.hmcts.probate.model.wa.TaskData;
import uk.gov.hmcts.probate.model.wa.TaskTypes;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.Event;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static uk.gov.hmcts.probate.model.ccd.JurisdictionId.PROBATE;

@Slf4j
@RequiredArgsConstructor
@Service
public class WaTaskService {
    private final WaApi waApi;
    private final SecurityUtils securityUtils;
    private final CoreCaseDataApi coreCaseDataApi;
    private final IdamApi idamApi;

    public boolean isTaskPresent(String authToken,
                                 String caseId,
                                 String eventId,
                                 @NonNull List<TaskTypes> taskNames) {
        SearchEventAndCase searchEventAndCase = new SearchEventAndCase(
                caseId,
                eventId,
                PROBATE.name(),
                "grantofrepresentation"
        );

        ResponseEntity<GetTasksCompletableResponse<TaskData>> taskResponse =
                waApi.searchWithCriteriaForAutomaticCompletion(
                        authToken,
                        securityUtils.generateServiceToken(),
                        searchEventAndCase);

        log.info("WA task response status: {} for case id {}",
                taskResponse.getStatusCode(),
                caseId);

        return Optional.ofNullable(taskResponse.getBody())
                .map(GetTasksCompletableResponse::tasks)
                .stream()
                .flatMap(List::stream)
                .map(TaskData::getType)
                .map(TaskTypes::fromValue)
                .flatMap(Optional::stream)
                .anyMatch(taskNames::contains);
    }

    private SecurityDTO getCaseworkerSecurityDTO() {
        securityUtils.setSecurityContextUserAsCaseworker();
        ResponseEntity<Map<String, Object>> userResponse = idamApi.getUserDetails(securityUtils.getAuthorisation());
        Map<String, Object> result = Objects.requireNonNull(userResponse.getBody());
        String userId = result.get("id").toString().toLowerCase();
        return SecurityDTO.builder().authorisation(securityUtils.getAuthorisation())
                .serviceAuthorisation(securityUtils.generateServiceToken())
                .userId(userId)
                .build();
    }

    public void createAndSubmitTaskForSystemUser(CallbackRequest callbackRequest,
                                                 EventId eventId, String summary, String description) {
        SecurityDTO securityDTO = getCaseworkerSecurityDTO();
        StartEventResponse startEventResponse = coreCaseDataApi.startEventForCaseWorker(
                securityDTO.getAuthorisation(),
                securityDTO.getServiceAuthorisation(),
                securityDTO.getUserId(),
                PROBATE.name(),
                callbackRequest.getCaseDetails().getData().getCaseType(),
                callbackRequest.getCaseDetails().getId().toString(),
                eventId.getName()
        );

        CaseDataContent caseDataContent = CaseDataContent.builder()
                .event(Event.builder()
                        .id(startEventResponse.getEventId())
                        .summary(summary)
                        .description(description)
                        .build())
                .eventToken(startEventResponse.getToken())
                .data(startEventResponse.getCaseDetails().getData())
                .build();

        coreCaseDataApi.submitEventForCaseWorker(
                securityDTO.getAuthorisation(),
                securityDTO.getServiceAuthorisation(),
                securityDTO.getUserId(),
                PROBATE.name(),
                callbackRequest.getCaseDetails().getData().getCaseType(),
                callbackRequest.getCaseDetails().getId().toString(),
                false,
                caseDataContent
        );
    }
}
