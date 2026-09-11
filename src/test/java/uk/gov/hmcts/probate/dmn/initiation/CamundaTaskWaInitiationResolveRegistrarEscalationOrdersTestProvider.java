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
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.REGISTRAR_ESCALATION_REASON_ORDERS;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_REGISTRAR_ESCALATION_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_REGISTRAR_ESCALATION_ORDERS;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_REGISTRAR_ESCALATION_ORDERS_TASK_TYPE_NAME;


public class CamundaTaskWaInitiationResolveRegistrarEscalationOrdersTestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {

        Map<String, Object> ordersTask = Map.of(
                "taskId", RESOLVE_REGISTRAR_ESCALATION_ORDERS,
                "name", RESOLVE_REGISTRAR_ESCALATION_ORDERS_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );

        return Stream.of(
                Arguments.of(
                        RESOLVE_REGISTRAR_ESCALATION_EVENT,
                        BO_REGISTRAR_ESCALATION,
                        additionalData(false, "", false, Collections.emptyList(), false,
                                REGISTRAR_ESCALATION_REASON_ORDERS),
                        List.of(ordersTask)
                ),
                Arguments.of(
                        RESOLVE_REGISTRAR_ESCALATION_EVENT,
                        BO_REGISTRAR_ESCALATION,
                        additionalData(true, "", false, Collections.emptyList(), false,
                                REGISTRAR_ESCALATION_REASON_ORDERS),
                        emptyList()
                ),
                Arguments.of(
                        "someOtherEvent",
                        BO_REGISTRAR_ESCALATION,
                        additionalData(false, "", false, Collections.emptyList(), false,
                                REGISTRAR_ESCALATION_REASON_ORDERS),
                        emptyList()
                )
        );

    }
}
