package uk.gov.hmcts.probate.service.wa;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import uk.gov.hmcts.probate.model.wa.GetTasksResponse;
import uk.gov.hmcts.probate.model.wa.RequestContext;
import uk.gov.hmcts.probate.model.wa.SearchTaskRequest;
import uk.gov.hmcts.probate.model.wa.TaskData;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.wa.search.SearchOperator;
import uk.gov.hmcts.probate.service.wa.search.enums.TaskTypes;
import uk.gov.hmcts.probate.service.wa.search.parameter.SearchParameterList;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static uk.gov.hmcts.probate.model.ccd.JurisdictionId.PROBATE;
import static uk.gov.hmcts.probate.service.wa.search.parameter.SearchParameterKey.CASE_ID;
import static uk.gov.hmcts.probate.service.wa.search.parameter.SearchParameterKey.JURISDICTION;

@Slf4j
@RequiredArgsConstructor
@Service
class WaTaskService {
    private final WaApi waApi;
    private final SecurityUtils securityUtils;

    public boolean isTaskPresent(String authToken,
                                 String caseId,
                                 @NonNull List<TaskTypes> taskNames) {

        SearchTaskRequest searchTaskRequest = new SearchTaskRequest(
                RequestContext.AVAILABLE_TASKS,
                asList(
                    new SearchParameterList(JURISDICTION, SearchOperator.IN, singletonList(PROBATE.name())),
                    new SearchParameterList(CASE_ID, SearchOperator.IN, singletonList(caseId)))
        );

        //TODO: REMOVE THIS
        ObjectMapper mapper = new ObjectMapper();
        log.info("WA task search request: {} for case id {}",
                mapper.writeValueAsString(searchTaskRequest),
                caseId);

        ResponseEntity<GetTasksResponse<TaskData>> taskResponse = waApi.searchWithCriteria(
                authToken,
                securityUtils.generateServiceToken(),
                searchTaskRequest
        );
        log.info("WA task response status: {} for case id {}",
                taskResponse.getStatusCode(),
                caseId);

        //TODO: REMOVE THIS
        log.info("WA task response status: {} for case id {}",
                Optional.ofNullable(taskResponse.getBody())
                        .map(GetTasksResponse::getTasks)
                        .stream().toList());


        return Optional.ofNullable(taskResponse.getBody())
                .map(GetTasksResponse::getTasks)
                .stream()
                .flatMap(List::stream)
                .map(TaskData::getType)
                .map(TaskTypes::fromValue)
                .flatMap(Optional::stream)
                .anyMatch(taskNames::contains);
    }
}
