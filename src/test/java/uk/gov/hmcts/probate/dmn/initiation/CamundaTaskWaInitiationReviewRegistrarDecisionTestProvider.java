package uk.gov.hmcts.probate.dmn.initiation;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.additionalData;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_REGISTRAR_ESCALATION;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.REGISTRAR_ESCALATION_REASON_REFERRALS;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_REGISTRAR_ESCALATION_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_REGISTRAR_ESCALATION_REFERRALS;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_REGISTRAR_ESCALATION_REFERRALS_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.REVIEW_REGISTRAR_DECISION;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.REVIEW_REGISTRAR_DECISION_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.REVIEW_REGISTRAR_DECISION_TASK_TYPE_NAME;


public class CamundaTaskWaInitiationReviewRegistrarDecisionTestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {

        Map<String, Object> reviewRegistrarDecisionTask = Map.of(
                "taskId", REVIEW_REGISTRAR_DECISION,
                "name", REVIEW_REGISTRAR_DECISION_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );

        return Stream.of(
                Arguments.of(
                        REVIEW_REGISTRAR_DECISION_EVENT,
                        BO_REGISTRAR_ESCALATION,
                        additionalData(false, "", false, Collections.emptyList(), false,
                                REVIEW_REGISTRAR_DECISION),
                        List.of(reviewRegistrarDecisionTask)
                ),
                Arguments.of(
                        REVIEW_REGISTRAR_DECISION_EVENT,
                        BO_REGISTRAR_ESCALATION,
                        additionalData(true, "", false, Collections.emptyList(), false,
                                REVIEW_REGISTRAR_DECISION),
                        emptyList()
                ),
                Arguments.of(
                        "someOtherEvent",
                        BO_REGISTRAR_ESCALATION,
                        additionalData(false, "", false, Collections.emptyList(), false,
                                REVIEW_REGISTRAR_DECISION),
                        emptyList()
                ),
                Arguments.of(
                        REVIEW_REGISTRAR_DECISION_EVENT,
                        BO_REGISTRAR_ESCALATION,
                        additionalData(false, "gop", false, Collections.emptyList(), false,
                                REVIEW_REGISTRAR_DECISION),
                        List.of(reviewRegistrarDecisionTask)
                ),
                Arguments.of(
                        REVIEW_REGISTRAR_DECISION_EVENT,
                        BO_REGISTRAR_ESCALATION,
                        additionalData(true, "gop", false, Collections.emptyList(), false,
                                REVIEW_REGISTRAR_DECISION),
                        emptyList()
                ),
                Arguments.of(
                        REVIEW_REGISTRAR_DECISION_EVENT,
                        BO_REGISTRAR_ESCALATION,
                        additionalData(false, "intestacy", false, Collections.emptyList(), false,
                                REVIEW_REGISTRAR_DECISION),
                        List.of(reviewRegistrarDecisionTask)
                ),
                Arguments.of(
                        REVIEW_REGISTRAR_DECISION_EVENT,
                        BO_REGISTRAR_ESCALATION,
                        additionalData(true, "intestacy", false, Collections.emptyList(), false,
                                REVIEW_REGISTRAR_DECISION),
                        emptyList()
                ),
                Arguments.of(
                        REVIEW_REGISTRAR_DECISION_EVENT,
                        BO_REGISTRAR_ESCALATION,
                        additionalData(false, "adColligendaBona", false, Collections.emptyList(), false,
                                REVIEW_REGISTRAR_DECISION),
                        List.of(reviewRegistrarDecisionTask)
                ),
                Arguments.of(
                        REVIEW_REGISTRAR_DECISION_EVENT,
                        BO_REGISTRAR_ESCALATION,
                        additionalData(true, "adColligendaBona", false, Collections.emptyList(), false,
                                REVIEW_REGISTRAR_DECISION),
                        emptyList()
                ),
                Arguments.of(
                        REVIEW_REGISTRAR_DECISION_EVENT,
                        BO_REGISTRAR_ESCALATION,
                        additionalData(false, "admonWill", false, Collections.emptyList(), false,
                                REVIEW_REGISTRAR_DECISION),
                        List.of(reviewRegistrarDecisionTask)
                ),
                Arguments.of(
                        REVIEW_REGISTRAR_DECISION_EVENT,
                        BO_REGISTRAR_ESCALATION,
                        additionalData(true, "admonWill", false, Collections.emptyList(), false,
                                REVIEW_REGISTRAR_DECISION),
                        emptyList()
                )
        );

    }
}
