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
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.handOffReasonListWithHandOffReason;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.invalidHandOffReason;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.literaryEstateHandOffReason;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_RESOLVE_STOP_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CHANGE_STATE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_LITERARY_ESTATE_CASE_PRINTED;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_LITERARY_ESTATE_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.HANDLE_EVIDENCE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CASE_PRINTED_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_SME_REFERRAL_EVENT;


public class CamundaTaskWaInitiationLiteraryEstateTestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {

        Map<String,Object> examineLiteraryEstateCasePrintedTaskAttributes = Map.of(
                "taskId", EXAMINE_LITERARY_ESTATE_CASE_PRINTED,
                "name", EXAMINE_LITERARY_ESTATE_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );

        return Stream.of(
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(literaryEstateHandOffReason)),
                        List.of(examineLiteraryEstateCasePrintedTaskAttributes)
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(literaryEstateHandOffReason)),
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
                                handOffReasonListWithHandOffReason(literaryEstateHandOffReason)),
                        List.of(examineLiteraryEstateCasePrintedTaskAttributes)
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(literaryEstateHandOffReason)),
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
                                handOffReasonListWithHandOffReason(literaryEstateHandOffReason)),
                        List.of(examineLiteraryEstateCasePrintedTaskAttributes)
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(literaryEstateHandOffReason)),
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
                                handOffReasonListWithHandOffReason(literaryEstateHandOffReason)),
                        List.of(examineLiteraryEstateCasePrintedTaskAttributes)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(literaryEstateHandOffReason)),
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