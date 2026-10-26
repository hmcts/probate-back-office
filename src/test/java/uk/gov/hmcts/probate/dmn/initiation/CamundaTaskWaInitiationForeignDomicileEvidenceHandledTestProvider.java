package uk.gov.hmcts.probate.dmn.initiation;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.additionalData;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.additionalDataEvidenceHandledBlank;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.additionalDataNoHandOffList;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.foreignDomicileCasePrintedHandOffReason;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.handOffReasonListWithHandOffReason;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.invalidHandOffReason;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CASE_PRINTED_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_FOREIGN_DOMICILE_CASE_PRINTED;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_FOREIGN_DOMICILE_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.SERVIVE_REQUEST_PAYMENT_SUCCESS_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.SOLICITOR_PAYMENT_SUCCESS_CASE_EVENT;

public class CamundaTaskWaInitiationForeignDomicileEvidenceHandledTestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {

        Map<String, Object> examineForeignDomicileCasePrintedTaskAttributes = Map.of(
                "taskId", EXAMINE_FOREIGN_DOMICILE_CASE_PRINTED,
                "name", EXAMINE_FOREIGN_DOMICILE_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );

        return Stream.of(
                // SERVIVE_REQUEST_PAYMENT_SUCCESS_EVENT - HSE No / Yes / Blank
                Arguments.of(
                        SERVIVE_REQUEST_PAYMENT_SUCCESS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "", true,
                                handOffReasonListWithHandOffReason(foreignDomicileCasePrintedHandOffReason), false),
                        List.of(examineForeignDomicileCasePrintedTaskAttributes)
                ),
                Arguments.of(
                        SERVIVE_REQUEST_PAYMENT_SUCCESS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(true, "", true,
                                handOffReasonListWithHandOffReason(foreignDomicileCasePrintedHandOffReason), false),
                        emptyList()
                ),
                Arguments.of(
                        SERVIVE_REQUEST_PAYMENT_SUCCESS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalDataEvidenceHandledBlank("", true,
                                handOffReasonListWithHandOffReason(foreignDomicileCasePrintedHandOffReason), false),
                        emptyList()
                ),
                // SOLICITOR_PAYMENT_SUCCESS_CASE_EVENT - Case Printed only
                Arguments.of(
                        SOLICITOR_PAYMENT_SUCCESS_CASE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "", true,
                                handOffReasonListWithHandOffReason(foreignDomicileCasePrintedHandOffReason), false),
                        List.of(examineForeignDomicileCasePrintedTaskAttributes)
                ),
                Arguments.of(
                        SOLICITOR_PAYMENT_SUCCESS_CASE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(true, "", true,
                                handOffReasonListWithHandOffReason(foreignDomicileCasePrintedHandOffReason), false),
                        emptyList()
                ),
                Arguments.of(
                        SOLICITOR_PAYMENT_SUCCESS_CASE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalDataEvidenceHandledBlank("", true,
                                handOffReasonListWithHandOffReason(foreignDomicileCasePrintedHandOffReason), false),
                        emptyList()
                ),
                Arguments.of(
                        SOLICITOR_PAYMENT_SUCCESS_CASE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "", false,
                                handOffReasonListWithHandOffReason(foreignDomicileCasePrintedHandOffReason), false),
                        emptyList()
                ),
                Arguments.of(
                        SOLICITOR_PAYMENT_SUCCESS_CASE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "", true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        emptyList()
                ),
                Arguments.of(
                        SOLICITOR_PAYMENT_SUCCESS_CASE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "", true, emptyList(), false),
                        emptyList()
                ),
                Arguments.of(
                        SOLICITOR_PAYMENT_SUCCESS_CASE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalDataNoHandOffList(),
                        emptyList()
                )
        );
    }
}