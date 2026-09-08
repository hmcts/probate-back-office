package uk.gov.hmcts.probate.service.wa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.wa.GetTasksResponse;
import uk.gov.hmcts.probate.model.wa.SearchTaskRequest;
import uk.gov.hmcts.probate.model.wa.TaskData;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.wa.search.SearchOperator;
import uk.gov.hmcts.probate.service.wa.search.parameter.SearchParameterList;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static uk.gov.hmcts.probate.service.wa.search.parameter.SearchParameterKey.CASE_ID;
import static uk.gov.hmcts.probate.service.wa.search.parameter.SearchParameterKey.JURISDICTION;

@Slf4j
@RequiredArgsConstructor
@Service
class WaTaskService {
    private final WaApi waApi;
    private final SecurityUtils securityUtils;

    public Set<TaskData> searchTasks(String caseId) {
        SearchTaskRequest searchTaskRequest = new SearchTaskRequest(asList(
                new SearchParameterList(JURISDICTION, SearchOperator.IN, singletonList("PROBATE")),
                new SearchParameterList(CASE_ID, SearchOperator.IN, singletonList(caseId))
        ));

        ResponseEntity<GetTasksResponse<TaskData>> taskResponse = waApi.searchWithCriteria(
                securityUtils.getAuthorisation(),
                securityUtils.generateServiceToken(),
                searchTaskRequest
        );
        log.info("WA task response status: {} for case id {}",
                taskResponse.getStatusCode(),
                caseId);

        return Optional.ofNullable(taskResponse.getBody())
                .map(GetTasksResponse::getTasks)
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet());
    }
}
