package uk.gov.hmcts.probate.dmn.initiation;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.invalidHandOffReason;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.additionalData;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.handOffReasonListWithHandOffReason;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.infectedBloodCompensationAuthorityHandOffReason;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.additionalDataNoHandOffList;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_RESOLVE_STOP_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CHANGE_STATE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.HANDLE_EVIDENCE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.READY_TO_ISSUE_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_SME_REFERRAL_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.INFECTED_BLOOD_COMPENSATION_AUTHORITY_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_INFECTED_BLOOD_COMPENSATION_AUTHORITY;


public class CamundaTaskWaInitiationInfectedBloodTestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {

        Map<String,Object> examineInfectedBloodCompensationAuthorityTaskAttributes = Map.of(
                "taskId", EXAMINE_INFECTED_BLOOD_COMPENSATION_AUTHORITY,
                "name", INFECTED_BLOOD_COMPENSATION_AUTHORITY_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );

        return Stream.of(
            Arguments.of(
                    HANDLE_EVIDENCE_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",true,
                            handOffReasonListWithHandOffReason(infectedBloodCompensationAuthorityHandOffReason)),
                    List.of(examineInfectedBloodCompensationAuthorityTaskAttributes)
            ),
            Arguments.of(
                    HANDLE_EVIDENCE_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",false,
                            handOffReasonListWithHandOffReason(infectedBloodCompensationAuthorityHandOffReason)),
                    Collections.emptyList()
            ),
            Arguments.of(
                    HANDLE_EVIDENCE_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",true,
                            handOffReasonListWithHandOffReason(invalidHandOffReason)),
                    Collections.emptyList()
            ),
            Arguments.of(
                    HANDLE_EVIDENCE_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",true, Collections.emptyList()),
                    Collections.emptyList()
            ),
            Arguments.of(
                    CHANGE_STATE_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",true,
                            handOffReasonListWithHandOffReason(infectedBloodCompensationAuthorityHandOffReason)),
                    List.of(examineInfectedBloodCompensationAuthorityTaskAttributes)
            ),
            Arguments.of(
                    CHANGE_STATE_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",false,
                            handOffReasonListWithHandOffReason(infectedBloodCompensationAuthorityHandOffReason)),
                    Collections.emptyList()
            ),
            Arguments.of(
                    CHANGE_STATE_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",true,
                            handOffReasonListWithHandOffReason(invalidHandOffReason)),
                    Collections.emptyList()
            ),
            Arguments.of(
                    CHANGE_STATE_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",true, Collections.emptyList()),
                    Collections.emptyList()
            ),
            Arguments.of(
                    RESOLVE_SME_REFERRAL_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",true,
                            handOffReasonListWithHandOffReason(infectedBloodCompensationAuthorityHandOffReason)),
                    List.of(examineInfectedBloodCompensationAuthorityTaskAttributes)
            ),
            Arguments.of(
                    RESOLVE_SME_REFERRAL_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",false,
                            handOffReasonListWithHandOffReason(infectedBloodCompensationAuthorityHandOffReason)),
                    Collections.emptyList()
            ),
            Arguments.of(
                    RESOLVE_SME_REFERRAL_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",true,
                            handOffReasonListWithHandOffReason(invalidHandOffReason)),
                    Collections.emptyList()
            ),
            Arguments.of(
                    RESOLVE_SME_REFERRAL_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",true, Collections.emptyList()),
                    Collections.emptyList()
            ),
            Arguments.of(
                    BO_RESOLVE_STOP_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",true,
                            handOffReasonListWithHandOffReason(infectedBloodCompensationAuthorityHandOffReason)),
                    List.of(examineInfectedBloodCompensationAuthorityTaskAttributes)
            ),
            Arguments.of(
                    BO_RESOLVE_STOP_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",false,
                            handOffReasonListWithHandOffReason(infectedBloodCompensationAuthorityHandOffReason)),
                    Collections.emptyList()
            ),
            Arguments.of(
                    BO_RESOLVE_STOP_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",true,
                            handOffReasonListWithHandOffReason(invalidHandOffReason)),
                    Collections.emptyList()
            ),
            Arguments.of(
                    BO_RESOLVE_STOP_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalData(false, "",true, Collections.emptyList()),
                    Collections.emptyList()
            ),
            Arguments.of(
                    HANDLE_EVIDENCE_EVENT,
                    READY_TO_ISSUE_STATE,
                    null,
                    Collections.emptyList()
            ),
            Arguments.of(
                    HANDLE_EVIDENCE_EVENT,
                    READY_TO_ISSUE_STATE,
                    additionalDataNoHandOffList(),
                    Collections.emptyList()
            )
        );
    }
}
