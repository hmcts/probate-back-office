package uk.gov.hmcts.probate.dmn.initiation;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.additionalData;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.additionalDataNoHandOffList;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.handOffReasonListWithHandOffReason;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.invalidHandOffReason;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.trustCorporationHandOffReason;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_RESOLVE_STOP_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CHANGE_STATE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.HANDLE_EVIDENCE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_SME_REFERRAL_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.SERVIVE_REQUEST_PAYMENT_SUCCESS_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.ATTACH_SCANNED_DOCS_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_TRUST_CORPORATION_CASE_PRINTED;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_TRUST_CORPORATION_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CASE_PRINTED_STATE;

public class CamundaTaskWaInitiationTrustCorporationTestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        Map<String,Object> examineTrustCorporationCasePrintedTaskAttributes = Map.of(
                "taskId", EXAMINE_TRUST_CORPORATION_CASE_PRINTED,
                "name", EXAMINE_TRUST_CORPORATION_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );

        return Stream.of(
                //Case Printed scenarios
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(trustCorporationHandOffReason), false),
                        List.of(examineTrustCorporationCasePrintedTaskAttributes)
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(trustCorporationHandOffReason), false),
                        emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                emptyList(), false),
                        emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalDataNoHandOffList(),
                        emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        CASE_PRINTED_STATE,
                        null,
                        emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(trustCorporationHandOffReason), false),
                        List.of(examineTrustCorporationCasePrintedTaskAttributes)
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(trustCorporationHandOffReason), false),
                        emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                emptyList(), false),
                        emptyList()
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        CASE_PRINTED_STATE,
                        additionalDataNoHandOffList(),
                        emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(trustCorporationHandOffReason), false),
                        List.of(examineTrustCorporationCasePrintedTaskAttributes)
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(trustCorporationHandOffReason), false),
                        emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        emptyList()
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                emptyList(), false),
                        emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(trustCorporationHandOffReason), false),
                        List.of(examineTrustCorporationCasePrintedTaskAttributes)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(trustCorporationHandOffReason), false),
                        emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                emptyList(), false),
                        emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalDataNoHandOffList(),
                        emptyList()
                ),
                Arguments.of(
                        ATTACH_SCANNED_DOCS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(trustCorporationHandOffReason), false),
                        List.of(examineTrustCorporationCasePrintedTaskAttributes)
                ),
                Arguments.of(
                        ATTACH_SCANNED_DOCS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(trustCorporationHandOffReason), false),
                        emptyList()
                ),
                Arguments.of(
                        ATTACH_SCANNED_DOCS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        emptyList()
                ),
                Arguments.of(
                        ATTACH_SCANNED_DOCS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                emptyList(), false),
                        emptyList()
                ),
                Arguments.of(
                        ATTACH_SCANNED_DOCS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalDataNoHandOffList(),
                        emptyList()
                ),
                Arguments.of(
                        SERVIVE_REQUEST_PAYMENT_SUCCESS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(trustCorporationHandOffReason), false),
                        emptyList()
                ),
                Arguments.of(
                        SERVIVE_REQUEST_PAYMENT_SUCCESS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        emptyList()
                ),
                Arguments.of(
                        SERVIVE_REQUEST_PAYMENT_SUCCESS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                emptyList(), false),
                        emptyList()
                ),
                Arguments.of(
                        SERVIVE_REQUEST_PAYMENT_SUCCESS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalDataNoHandOffList(),
                        emptyList()
                )
        );
    }
}
