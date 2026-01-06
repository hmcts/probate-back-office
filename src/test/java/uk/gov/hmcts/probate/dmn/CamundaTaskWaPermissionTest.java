package uk.gov.hmcts.probate.dmn;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionTableImpl;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionTableInputImpl;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionTableOutputImpl;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.hamcrest.MatcherAssert;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.hmcts.probate.DmnDecisionTableBaseUnitTest;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.probate.DmnDecisionTable.WA_TASK_PERMISSIONS_PROBATE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.EXAMINE_DIGITAL_CASE_PROBATE;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ROLE_CATEGORY_ADMIN;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ROLE_CATEGORY_CTSC;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ROLE_CATEGORY_LO;
import static uk.gov.hmcts.probate.dmnutils.CamundaTaskConstants.ROLE_CATEGORY_JUDICIAL;

@Ignore
class CamundaTaskWaPermissionTest extends DmnDecisionTableBaseUnitTest {

    private static final String DUMMY_CASE_DATA = "someCaseData";

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_PERMISSIONS_PROBATE;
    }

    static Stream<Arguments> scenarioProvider() {
        return Stream.of(
                Arguments.of(
                        EXAMINE_DIGITAL_CASE_PROBATE,
                        DUMMY_CASE_DATA,
                        defaultAdminAndCtscTaskPermissions()
                )
        );
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();

        List<String> inputColumnIds = asList("taskType", "case");
        //Inputs
        assertThat(logic.getInputs().size(), is(2));
        assertThatInputContainInOrder(inputColumnIds, logic.getInputs());
        //Outputs
        List<String> outputColumnIds = asList(
                "caseAccessCategory",
                "name",
                "value",
                "roleCategory",
                "authorisations",
                "assignmentPriority",
                "autoAssignable"
        );
        assertThat(logic.getOutputs().size(), is(7));
        assertThatOutputContainInOrder(outputColumnIds, logic.getOutputs());
        //Rules
        assertThat(logic.getRules().size(), is(15));
    }

    @ParameterizedTest(name = "task type: {0} case data: {1}")
    @MethodSource("scenarioProvider")
    void given_null_or_empty_inputs_when_evaluate_dmn_it_returns_expected_rules(String taskType,
                                                                                String caseData,
                                                                                List<Map<String, String>> expectation) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));
        inputVariables.putValue("case", caseData);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        MatcherAssert.assertThat(dmnDecisionTableResult.getResultList(), is(expectation));
    }

    private void assertThatInputContainInOrder(List<String> inputColumnIds, List<DmnDecisionTableInputImpl> inputs) {
        IntStream.range(0, inputs.size())
                .forEach(i -> assertThat(inputs.get(i).getInputVariable(), is(inputColumnIds.get(i))));
    }

    private void assertThatOutputContainInOrder(List<String> outputColumnIds, List<DmnDecisionTableOutputImpl> output) {
        IntStream.range(0, output.size())
                .forEach(i -> assertThat(output.get(i).getOutputName(), is(outputColumnIds.get(i))));
    }

    private static List<Map<String, Object>> defaultAdminAndCtscTaskPermissions() {
        return List.of(
                taskSupervisorPermissions(),
                regionalCentreAdminPermissions(),
                regionalCentreTeamLeaderPermissions(),
                hearingCentreAdminPermissions(),
                hearingCentreTeamLeaderPermissions(),
                ctscPermissions(),
                ctscTeamLeaderPermissions()
        );
    }

    private static List<Map<String, Object>> defaultLegalOperationsPermissions() {
        return List.of(
                taskSupervisorPermissions(),
                seniorTribunalCaseworkerPermissions(),
                tribunalCaseworkerPermissions()
        );
    }

    private static List<Map<String, Object>> defaultJudicialPermissions() {
        return List.of(
                taskSupervisorPermissions(),
                seniorJudgePermissions(),
                judgePermissions()
        );
    }

    private static List<Map<String, Object>> defaultSpecificAccessRequestJudicialPermissions() {
        return List.of(
                taskSupervisorPermissions(),
                leadershipJudgePermissions()
        );
    }

    private static List<Map<String, Object>> defaultSpecificAccessRequestLegalOpsPermissions() {
        return List.of(
                taskSupervisorPermissions(),
                seniorTribunalCaseworkerPermissions()
        );
    }

    private static List<Map<String, Object>> defaultSpecificAccessRequestAdminPermissions() {
        return List.of(
                taskSupervisorPermissions(),
                hearingCentreTeamLeaderSpecificAccessPermissions(),
                regionalHearingCentreTeamLeaderSpecificAccessPermissions()
        );
    }

    private static List<Map<String, Object>> defaultSpecificAccessRequestCtscPermissions() {
        return List.of(
                taskSupervisorPermissions(),
                ctscTeamLeaderSpecificAccessPermissions()
        );
    }

    private static Map<String, Object> taskSupervisorPermissions() {
        return Map.of(
                "name", "task-supervisor",
                "value", "Read,Claim,Unclaim,Manage,Assign,Unassign,Complete,Cancel",
                "autoAssignable", false
        );
    }

    private static Map<String, Object> regionalCentreAdminPermissions() {
        return Map.of(
                "name", "regional-centre-admin",
                "value", "Read,Own,Claim,Unclaim,Manage,Complete",
                "roleCategory", ROLE_CATEGORY_ADMIN,
                "assignmentPriority", 1,
                "autoAssignable", false
        );
    }

    private static Map<String, Object> regionalCentreTeamLeaderPermissions() {
        return Map.of(
                "name", "regional-centre-team-leader",
                "value", "Read,Own,Claim,Unclaim,Manage,UnclaimAssign,Assign,Unassign,Cancel,Complete",
                "roleCategory", ROLE_CATEGORY_ADMIN,
                "assignmentPriority", 2,
                "autoAssignable", false
        );
    }

    private static Map<String, Object> hearingCentreAdminPermissions() {
        return Map.of(
                "name", "hearing-centre-admin",
                "value", "Read,Own,Claim,Unclaim,Manage,Complete",
                "roleCategory", ROLE_CATEGORY_ADMIN,
                "assignmentPriority", 1,
                "autoAssignable", false
        );
    }

    private static Map<String, Object> hearingCentreTeamLeaderPermissions() {
        return Map.of(
                "name", "hearing-centre-team-leader",
                "value", "Read,Own,Claim,Unclaim,Manage,UnclaimAssign,Assign,Unassign,Cancel,Complete",
                "roleCategory", ROLE_CATEGORY_ADMIN,
                "assignmentPriority", 2,
                "autoAssignable", false
        );
    }

    private static Map<String, Object> seniorTribunalCaseworkerPermissions() {
        return Map.of(
                "name", "senior-tribunal-caseworker",
                "value", "Read,Own,Claim,Manage,Assign,Unassign,Complete,Cancel",
                "roleCategory", ROLE_CATEGORY_LO,
                "assignmentPriority", 1,
                "autoAssignable", false
        );
    }

    private static Map<String, Object> tribunalCaseworkerPermissions() {
        return Map.of(
                "name", "tribunal-caseworker",
                "value", "Read,Own,Claim,Assign,Unassign,Complete,Cancel",
                "roleCategory", ROLE_CATEGORY_LO,
                "assignmentPriority", 2,
                "autoAssignable", false
        );
    }

    private static Map<String, Object> seniorJudgePermissions() {
        return Map.of(
                "name", "senior-judge",
                "value", "Read,Execute,Claim,Manage,Assign,Unassign,Complete,Cancel",
                "roleCategory", ROLE_CATEGORY_JUDICIAL,
                "authorisations", "328",
                "assignmentPriority", 1,
                "autoAssignable", false
        );
    }

    private static Map<String, Object> judgePermissions() {
        return Map.of(
                "name", "judge",
                "value", "Read,Own,Claim,Assign,Unassign,Complete,Cancel",
                "roleCategory", ROLE_CATEGORY_JUDICIAL,
                "authorisations", "328",
                "assignmentPriority", 2,
                "autoAssignable", false
        );
    }

    private static Map<String, Object> leadershipJudgePermissions() {
        return Map.of(
                "name", "leadership-judge",
                "value", "Read,Own,Claim,Manage,Assign,Unassign,Complete,Cancel",
                "roleCategory", ROLE_CATEGORY_JUDICIAL,
                "authorisations", "328",
                "assignmentPriority", 1,
                "autoAssignable", false
        );
    }

    private static Map<String, Object> hearingCentreTeamLeaderSpecificAccessPermissions() {
        return Map.of(
                "name", "hearing-centre-team-leader",
                "value", "Read,Own,Claim,Manage,Assign,Unassign,Complete,Cancel",
                "roleCategory", ROLE_CATEGORY_ADMIN,
                "autoAssignable", false
        );
    }

    private static Map<String, Object> regionalHearingCentreTeamLeaderSpecificAccessPermissions() {
        return Map.of(
                "name", "regional-centre-team-leader",
                "value", "Read,Own,Claim,Manage,Assign,Unassign,Complete,Cancel",
                "roleCategory", ROLE_CATEGORY_ADMIN,
                "autoAssignable", false
        );
    }

    private static Map<String, Object> ctscTeamLeaderSpecificAccessPermissions() {
        return Map.of(
                "name", "ctsc-team-leader",
                "value", "Read,Own,Claim,Manage,Assign,Unassign,Complete,Cancel",
                "roleCategory", ROLE_CATEGORY_CTSC,
                "autoAssignable", false
        );
    }

    private static Map<String, Object> ctscPermissions() {
        return Map.of(
                "name", "ctsc",
                "value", "Read,Own,Claim,Unclaim,Manage,Complete",
                "roleCategory", ROLE_CATEGORY_CTSC,
                "assignmentPriority", 1,
                "autoAssignable", false
        );
    }

    private static Map<String, Object> ctscTeamLeaderPermissions() {
        return Map.of(
                "name", "ctsc-team-leader",
                "value", "Read,Own,Claim,Unclaim,Manage,UnclaimAssign,Assign,Unassign,Cancel,Complete",
                "roleCategory", ROLE_CATEGORY_CTSC,
                "assignmentPriority", 2,
                "autoAssignable", false
        );
    }

}
