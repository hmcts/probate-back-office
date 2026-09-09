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
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_REGISTRAR_ESCALATION_REFERRAL;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_REGISTRAR_ESCALATION_REFERRAL_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_REGISTRAR_ESCALATION_REFERRAL_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_REGISTRAR_ESCALATION_REFERRAL_TASK_TYPE_NAME;


public class CamundaTaskWaInitiationResolveRegistrarEscalationReferralTestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {

        Map<String,Object> resolveRegistrationEscalationReferralTaskAttributes = Map.of(
                "taskId", RESOLVE_REGISTRAR_ESCALATION_REFERRAL,
                "name", RESOLVE_REGISTRAR_ESCALATION_REFERRAL_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );

        return Stream.of(
            Arguments.of(
                    RESOLVE_REGISTRAR_ESCALATION_REFERRAL_EVENT,
                    RESOLVE_REGISTRAR_ESCALATION_REFERRAL_STATE,
                    additionalData(false, "", false, Collections.emptyList(), false),
                    List.of(resolveRegistrationEscalationReferralTaskAttributes)
            ),
            Arguments.of(
                    RESOLVE_REGISTRAR_ESCALATION_REFERRAL_EVENT,
                    RESOLVE_REGISTRAR_ESCALATION_REFERRAL_STATE,
                    additionalData(true, "", false, Collections.emptyList(), false),
                    emptyList()
            ),
            Arguments.of(
                    RESOLVE_REGISTRAR_ESCALATION_REFERRAL_EVENT,
                    RESOLVE_REGISTRAR_ESCALATION_REFERRAL_STATE,
                    null,
                    List.of(resolveRegistrationEscalationReferralTaskAttributes)
            )
        );

    }
}
