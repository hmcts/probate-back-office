package uk.gov.hmcts.probate.service.wa;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.wa.GetTasksCompletableResponse;
import uk.gov.hmcts.probate.model.wa.SearchEventAndCase;
import uk.gov.hmcts.probate.model.wa.TaskData;
import uk.gov.hmcts.probate.model.wa.TaskTypes;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.utils.TaskUtils;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReason;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReasonId;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.ccd.JurisdictionId.PROBATE;

@Slf4j
@RequiredArgsConstructor
@Service
public class WaTaskService {
    private final WaApi waApi;
    private final SecurityUtils securityUtils;
    private final TaskUtils taskUtils;

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

    public BiPredicate<CallbackRequest, String> getCaseTypePredicate() {
        return (callbackRequest, clientContext) ->
                !callbackRequest.getCaseDetails().getData().getCaseType()
                        .equals(callbackRequest.getCaseDetailsBefore().getData().getCaseType());
    }

    public BiPredicate<CallbackRequest, String> getHandOffPredicate() {
        return (callbackRequest, clientContext) -> {
            Set<HandoffReason> handOffReasonAfter = getGetHandOffReasons(callbackRequest.getCaseDetails());

            Optional<TaskData> taskData = taskUtils.getTaskData(clientContext);
            String currentWaTaskHandOff = taskData.map(TaskData::getType)
                    .orElse("");

            boolean caseHandedOffToLegacySite = Optional.ofNullable(callbackRequest.getCaseDetails()
                            .getData().getCaseHandedOffToLegacySite()).map(value -> value.equals(YES))
                    .orElse(false);

            if (caseHandedOffToLegacySite) {
                boolean isTaskHandOffReasonRetained = handOffReasonAfter.stream()
                        .map(HandoffReason::getCaseHandoffReason)
                        .map(HandoffReasonId::getCode)
                        .anyMatch(currentWaTaskHandOff::contains);

                log.info("Case id {} - current WA task  {}, isTaskHandOffReasonRetained: {}",
                        callbackRequest.getCaseDetails().getId(),
                        currentWaTaskHandOff,
                        isTaskHandOffReasonRetained);

                return !isTaskHandOffReasonRetained;
            }
            return true;
        };
    }

    public Set<HandoffReason> getGetHandOffReasons(CaseDetails caseDetails) {
        return ofNullable(caseDetails.getData())
                .map(CaseData::getBoHandoffReasonList)
                .stream()
                .flatMap(List::stream)
                .map(CollectionMember::getValue)
                .collect(Collectors.toSet());
    }
}
