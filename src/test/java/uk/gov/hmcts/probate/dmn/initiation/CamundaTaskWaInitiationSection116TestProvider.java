package uk.gov.hmcts.probate.dmn.initiation;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.additionalData;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.additionalDataNoHandOffList;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.section116HandOffReason;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.handOffReasonListWithHandOffReason;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.invalidHandOffReason;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_RESOLVE_STOP_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CHANGE_STATE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_SECTION_116_CASE_PRINTED;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_SECTION_116_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_SECTION_116;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.HANDLE_EVIDENCE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.READY_TO_ISSUE_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_SME_REFERRAL_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CASE_PRINTED_STATE;


public class CamundaTaskWaInitiationSection116TestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {


        Map<String,Object> examineSection116TaskAttributes = Map.of(
                "taskId", EXAMINE_SECTION_116,
                "name", EXAMINE_SECTION_116_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );

        Map<String,Object> examineSection116CasePrintedTaskAttributes = Map.of(
                "taskId", EXAMINE_SECTION_116_CASE_PRINTED,
                "name", EXAMINE_SECTION_116_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );

        return Stream.of(
                //Ready to Issue scenarios
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(section116HandOffReason)),
                        List.of(examineSection116TaskAttributes)
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(section116HandOffReason)),
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
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalDataNoHandOffList(),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        null,
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(section116HandOffReason)),
                        List.of(examineSection116TaskAttributes)
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(section116HandOffReason)),
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
                        BO_RESOLVE_STOP_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalDataNoHandOffList(),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(section116HandOffReason)),
                        List.of(examineSection116TaskAttributes)
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(section116HandOffReason)),
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
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(section116HandOffReason)),
                        List.of(examineSection116TaskAttributes)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(section116HandOffReason)),
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
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalDataNoHandOffList(),
                        Collections.emptyList()
                ),
                //Case Printed scenarios
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(section116HandOffReason)),
                        List.of(examineSection116CasePrintedTaskAttributes)
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(section116HandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalDataNoHandOffList(),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        CASE_PRINTED_STATE,
                        null,
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(section116HandOffReason)),
                        List.of(examineSection116CasePrintedTaskAttributes)
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(section116HandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        CASE_PRINTED_STATE,
                        additionalDataNoHandOffList(),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(section116HandOffReason)),
                        List.of(examineSection116CasePrintedTaskAttributes)
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(section116HandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(section116HandOffReason)),
                        List.of(examineSection116CasePrintedTaskAttributes)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(section116HandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason)),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true, Collections.emptyList()),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalDataNoHandOffList(),
                        Collections.emptyList()
                )
        );
    }
}