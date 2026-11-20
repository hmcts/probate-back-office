package uk.gov.hmcts.probate.service.wa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.wa.TaskData;
import uk.gov.hmcts.probate.model.wa.TaskSearchResponse;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class WaClientApi {
    private final SecurityUtils securityUtils;
    private final WaApi waApi;
    private static final Set<String> OPEN_TASK_STATE = Set.of("assigned", "unassigned");

    public Set<String> searchActiveTasksByCaseId(String caseId) {
        SecurityDTO securityDTO = securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO();
        ResponseEntity<TaskSearchResponse> taskSearchResponseResponseEntity = waApi.searchTasksByCaseId(
                securityDTO.getAuthorisation(),
                securityDTO.getServiceAuthorisation(),
                caseId
        );
        //TODO: TO REMOVE
        List<TaskData> tasks = Optional.ofNullable(taskSearchResponseResponseEntity.getBody())
                .map(TaskSearchResponse::tasks)
                .orElseGet(Collections::emptyList);
        log.info("Tasks received {}", tasks);
        //TODO: END

        Set<String> activeTasks = Optional.ofNullable(taskSearchResponseResponseEntity.getBody())
                .map(TaskSearchResponse::tasks)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(taskData -> OPEN_TASK_STATE.contains(taskData.getTaskState().toLowerCase()))
                .map(TaskData::getType)
                .collect(Collectors.toSet());

        log.info("For case id {}: active task are {}",
                caseId,
                activeTasks);

        return activeTasks;
    }
}
