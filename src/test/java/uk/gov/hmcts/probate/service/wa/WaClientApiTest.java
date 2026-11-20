package uk.gov.hmcts.probate.service.wa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.probate.model.wa.TaskData;
import uk.gov.hmcts.probate.model.wa.TaskSearchResponse;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.wa.TaskTypes.EXAMINE_DE_BONIS_NON_CASE_PRINTED;
import static uk.gov.hmcts.probate.model.wa.TaskTypes.EXAMINE_HORIZON_SCHEME_CASE_PRINTED;
import static uk.gov.hmcts.probate.model.wa.TaskTypes.EXAMINE_TRUST_CORPORATION;

@ExtendWith(MockitoExtension.class)
class WaClientApiTest {
    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private WaApi waApi;
    @InjectMocks
    private WaClientApi waClientApi;



    @Test
    void shouldRetrieveTaskAssociatedWithTheCase() {
        String caseId = "1234";
        TaskSearchResponse taskSearchResponse = new TaskSearchResponse(List.of(
                TaskData.builder()
                        .type(EXAMINE_DE_BONIS_NON_CASE_PRINTED.getValue())
                        .taskState("ASSIGNED")
                        .build(),
                TaskData.builder()
                        .type(EXAMINE_TRUST_CORPORATION.getValue())
                        .taskState("TERMINATED")
                        .build(),
                TaskData.builder()
                        .type(EXAMINE_HORIZON_SCHEME_CASE_PRINTED.getValue())
                        .taskState("UNASSIGNED")
                        .build()

        ));
        SecurityDTO securityDTO = SecurityDTO.builder()
                .authorisation("auth-token")
                .serviceAuthorisation("service-token")
                .build();
        when(securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO())
                .thenReturn(securityDTO);
        when(waApi.searchTasksByCaseId(securityDTO.getAuthorisation(),
                securityDTO.getServiceAuthorisation(),
                caseId))
                .thenReturn(ResponseEntity.ok(taskSearchResponse));
        Set<String> activeTasks = waClientApi.searchActiveTasksByCaseId(caseId);
        assertThat(activeTasks)
                .containsAll(Set.of("ExamineDeBonisNonCasePrinted", "ExamineHorizonSchemeCasePrinted"));
    }

    @ParameterizedTest(name = "Should handle tasks: {0}")
    @MethodSource("emptyTaskScenarios")
    void shouldNotFailWhenThereAreNoAssociatedTasks(List<TaskData> tasks) {
        String caseId = "1234";

        TaskSearchResponse taskSearchResponse = new TaskSearchResponse(tasks);

        SecurityDTO securityDTO = SecurityDTO.builder()
                .authorisation("auth-token")
                .serviceAuthorisation("service-token")
                .build();
        when(securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO())
                .thenReturn(securityDTO);
        when(waApi.searchTasksByCaseId(securityDTO.getAuthorisation(),
                securityDTO.getServiceAuthorisation(),
                caseId))
                .thenReturn(ResponseEntity.ok(taskSearchResponse));
        Set<String> activeTasks = waClientApi.searchActiveTasksByCaseId(caseId);
        assertThat(activeTasks).isNullOrEmpty();
    }

    public static Stream<Arguments> emptyTaskScenarios() {
        return Stream.of(
                Arguments.of((Object) null),
                Arguments.of(Collections.emptyList())
        );
    }
}