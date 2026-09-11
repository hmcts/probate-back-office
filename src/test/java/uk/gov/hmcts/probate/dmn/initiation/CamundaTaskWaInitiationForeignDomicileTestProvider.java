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
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.foreignDomicileHandOffReason;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_RESOLVE_STOP_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CHANGE_STATE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.HANDLE_EVIDENCE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_SME_REFERRAL_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CREATE_CASE_FROM_BULK_SCAN_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.SERVIVE_REQUEST_PAYMENT_SUCCESS_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.APPLY_FOR_GRANT_PAPER_APPLICATION_MAN_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CREATE_CASE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.PAYMENT_SUCCESS_APP_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CREATE_PAYMENT_SUCCESS_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.ATTACH_SCANNED_DOCS_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_FOREIGN_DOMICILE_CASE_PRINTED;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_FOREIGN_DOMICILE_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CASE_PRINTED_STATE;

public class CamundaTaskWaInitiationForeignDomicileTestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        Map<String,Object> examineForeignDomicileCasePrintedTaskAttributes = Map.of(
                "taskId", EXAMINE_FOREIGN_DOMICILE_CASE_PRINTED,
                "name", EXAMINE_FOREIGN_DOMICILE_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );

        return Stream.of(
                //Case Printed scenarios
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(foreignDomicileHandOffReason), false),
                        List.of(examineForeignDomicileCasePrintedTaskAttributes)
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(foreignDomicileHandOffReason), false),
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
                                handOffReasonListWithHandOffReason(foreignDomicileHandOffReason), false),
                        List.of(examineForeignDomicileCasePrintedTaskAttributes)
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(foreignDomicileHandOffReason), false),
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
                                handOffReasonListWithHandOffReason(foreignDomicileHandOffReason), false),
                        List.of(examineForeignDomicileCasePrintedTaskAttributes)
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(foreignDomicileHandOffReason), false),
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
                                handOffReasonListWithHandOffReason(foreignDomicileHandOffReason), false),
                        List.of(examineForeignDomicileCasePrintedTaskAttributes)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(foreignDomicileHandOffReason), false),
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
                        CREATE_CASE_FROM_BULK_SCAN_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(foreignDomicileHandOffReason), false),
                        List.of(examineForeignDomicileCasePrintedTaskAttributes)
                ),
                Arguments.of(
                        CREATE_CASE_FROM_BULK_SCAN_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(foreignDomicileHandOffReason), false),
                        emptyList()
                ),
                Arguments.of(
                        CREATE_CASE_FROM_BULK_SCAN_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        emptyList()
                ),
                Arguments.of(
                        CREATE_CASE_FROM_BULK_SCAN_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                emptyList(), false),
                        emptyList()
                ),
                Arguments.of(
                        CREATE_CASE_FROM_BULK_SCAN_EVENT,
                        CASE_PRINTED_STATE,
                        additionalDataNoHandOffList(),
                        emptyList()
                ),
                Arguments.of(
                        SERVIVE_REQUEST_PAYMENT_SUCCESS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(foreignDomicileHandOffReason), false),
                        List.of(examineForeignDomicileCasePrintedTaskAttributes)
                ),
                Arguments.of(
                        SERVIVE_REQUEST_PAYMENT_SUCCESS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(foreignDomicileHandOffReason), false),
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
                ),
                Arguments.of(
                        APPLY_FOR_GRANT_PAPER_APPLICATION_MAN_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(foreignDomicileHandOffReason), false),
                        List.of(examineForeignDomicileCasePrintedTaskAttributes)
                ),
                Arguments.of(
                        APPLY_FOR_GRANT_PAPER_APPLICATION_MAN_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(foreignDomicileHandOffReason), false),
                        emptyList()
                ),
                Arguments.of(
                        APPLY_FOR_GRANT_PAPER_APPLICATION_MAN_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        emptyList()
                ),
                Arguments.of(
                        APPLY_FOR_GRANT_PAPER_APPLICATION_MAN_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                emptyList(), false),
                        emptyList()
                ),
                Arguments.of(
                        APPLY_FOR_GRANT_PAPER_APPLICATION_MAN_EVENT,
                        CASE_PRINTED_STATE,
                        additionalDataNoHandOffList(),
                        emptyList()
                ),
                Arguments.of(
                        CREATE_CASE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(foreignDomicileHandOffReason), false),
                        List.of(examineForeignDomicileCasePrintedTaskAttributes)
                ),
                Arguments.of(
                        CREATE_CASE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(foreignDomicileHandOffReason), false),
                        emptyList()
                ),
                Arguments.of(
                        CREATE_CASE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        emptyList()
                ),
                Arguments.of(
                        CREATE_CASE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                emptyList(), false),
                        emptyList()
                ),
                Arguments.of(
                        CREATE_CASE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalDataNoHandOffList(),
                        emptyList()
                ),
                Arguments.of(
                        PAYMENT_SUCCESS_APP_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(foreignDomicileHandOffReason), false),
                        List.of(examineForeignDomicileCasePrintedTaskAttributes)
                ),
                Arguments.of(
                        PAYMENT_SUCCESS_APP_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(foreignDomicileHandOffReason), false),
                        emptyList()
                ),
                Arguments.of(
                        PAYMENT_SUCCESS_APP_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        emptyList()
                ),
                Arguments.of(
                        PAYMENT_SUCCESS_APP_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                emptyList(), false),
                        emptyList()
                ),
                Arguments.of(
                        PAYMENT_SUCCESS_APP_EVENT,
                        CASE_PRINTED_STATE,
                        additionalDataNoHandOffList(),
                        emptyList()
                ),
                Arguments.of(
                        CREATE_PAYMENT_SUCCESS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(foreignDomicileHandOffReason), false),
                        List.of(examineForeignDomicileCasePrintedTaskAttributes)
                ),
                Arguments.of(
                        CREATE_PAYMENT_SUCCESS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(foreignDomicileHandOffReason), false),
                        emptyList()
                ),
                Arguments.of(
                        CREATE_PAYMENT_SUCCESS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        emptyList()
                ),
                Arguments.of(
                        CREATE_PAYMENT_SUCCESS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                emptyList(), false),
                        emptyList()
                ),
                Arguments.of(
                        CREATE_PAYMENT_SUCCESS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalDataNoHandOffList(),
                        emptyList()
                ),
                Arguments.of(
                        ATTACH_SCANNED_DOCS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(foreignDomicileHandOffReason), false),
                        List.of(examineForeignDomicileCasePrintedTaskAttributes)
                ),
                Arguments.of(
                        ATTACH_SCANNED_DOCS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "",false,
                                handOffReasonListWithHandOffReason(foreignDomicileHandOffReason), false),
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
                )
        );
    }
}
