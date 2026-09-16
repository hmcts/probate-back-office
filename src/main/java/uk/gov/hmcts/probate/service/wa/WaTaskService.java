package uk.gov.hmcts.probate.service.wa;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import uk.gov.hmcts.probate.model.wa.SearchEventAndCase;
import uk.gov.hmcts.probate.model.wa.TaskData;
import uk.gov.hmcts.probate.model.wa.GetTasksCompletableResponse;
import uk.gov.hmcts.probate.model.wa.TaskTypes;
import uk.gov.hmcts.probate.security.SecurityUtils;

import java.util.List;
import java.util.Optional;

import static uk.gov.hmcts.probate.model.ccd.JurisdictionId.PROBATE;

@Slf4j
@RequiredArgsConstructor
@Service
class WaTaskService {
    private final WaApi waApi;
    private final SecurityUtils securityUtils;

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
}
