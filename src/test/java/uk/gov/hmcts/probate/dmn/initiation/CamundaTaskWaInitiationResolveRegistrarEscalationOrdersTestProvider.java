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
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_REGISTRAR_ESCALATION_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_REGISTRAR_ESCALATION_ORDERS;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_REGISTRAR_ESCALATION_ORDERS_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_REGISTRAR_ESCALATION_REFERRALS;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_REGISTRAR_ESCALATION_REFERRALS_TASK_TYPE_NAME;


public class CamundaTaskWaInitiationResolveRegistrarEscalationOrdersTestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {

        Map<String, Object> referralsTask = Map.of(
                "taskId", RESOLVE_REGISTRAR_ESCALATION_REFERRALS,
                "name", RESOLVE_REGISTRAR_ESCALATION_REFERRALS_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );

        Map<String, Object> ordersTask = Map.of(
                "taskId", RESOLVE_REGISTRAR_ESCALATION_ORDERS,
                "name", RESOLVE_REGISTRAR_ESCALATION_ORDERS_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );

        return Stream.of(
                Arguments.of(
                        RESOLVE_REGISTRAR_ESCALATION_EVENT,
                        BO_REGISTRAR_ESCALATION,
                        additionalData(false, "", false, Collections.emptyList(), false),
                        List.of(referralsTask, ordersTask)
                ),
                Arguments.of(
                        RESOLVE_REGISTRAR_ESCALATION_EVENT,
                        BO_REGISTRAR_ESCALATION,
                        additionalData(true, "", false, Collections.emptyList(), false),
                        emptyList()
                ),
                Arguments.of(
                        RESOLVE_REGISTRAR_ESCALATION_EVENT,
                        BO_REGISTRAR_ESCALATION,
                        null,
                        List.of(referralsTask, ordersTask)
                ),
                Arguments.of(
                        "someOtherEvent",
                        BO_REGISTRAR_ESCALATION,
                        additionalData(false, "", true, Collections.emptyList(), false),
                        Collections.emptyList()
                )
        );

    }
}
