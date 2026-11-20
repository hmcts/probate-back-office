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
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CASE_PRINTED_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CREATE_CASE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CREATE_CASE_PAYMENT_SUCCESS_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_INTESTACY;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.INTESTACY_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.PAYMENT_SUCCESS_APP_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.SERVICE_REQUEST_PAYMENT_SUCCESS;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.SOLICITOR_PAYMENT_SUCCESS_CASE_EVENT;

public class CamundaTaskWaInitiationEvidenceHandledPresentTestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {

        Map<String, Object> examineDigitalCaseIntestacyTaskAttributes = Map.of(
                "taskId", EXAMINE_DIGITAL_CASE_INTESTACY,
                "name", INTESTACY_TASK_TYPE_NAME,
                "processCategories", "case progression,examineDigitalCaseTypes"
        );

        return Stream.of(
                Arguments.of(
                        CREATE_CASE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "intestacy", false, emptyList(), false),
                        List.of(examineDigitalCaseIntestacyTaskAttributes)
                ),
                Arguments.of(
                        CREATE_CASE_PAYMENT_SUCCESS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "intestacy", false, emptyList(), false),
                        List.of(examineDigitalCaseIntestacyTaskAttributes)
                ),
                Arguments.of(
                        PAYMENT_SUCCESS_APP_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "intestacy", false, emptyList(), false),
                        List.of(examineDigitalCaseIntestacyTaskAttributes)
                ),
                Arguments.of(
                        SOLICITOR_PAYMENT_SUCCESS_CASE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, "intestacy", false, emptyList(), false),
                        List.of(examineDigitalCaseIntestacyTaskAttributes)
                ),
                Arguments.of(
                        SERVICE_REQUEST_PAYMENT_SUCCESS,
                        CASE_PRINTED_STATE,
                        additionalData(false, "intestacy", false, emptyList(), false),
                        List.of(examineDigitalCaseIntestacyTaskAttributes)
                ),
                Arguments.of(
                        CREATE_CASE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(true, "intestacy", false, emptyList(), false),
                        emptyList()
                ),
                Arguments.of(
                        CREATE_CASE_PAYMENT_SUCCESS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(true, "intestacy", false, emptyList(), false),
                        emptyList()
                ),
                Arguments.of(
                        PAYMENT_SUCCESS_APP_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(true, "intestacy", false, emptyList(), false),
                        emptyList()
                ),
                Arguments.of(
                        SOLICITOR_PAYMENT_SUCCESS_CASE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(true, "intestacy", false, emptyList(), false),
                        emptyList()
                ),
                Arguments.of(
                        SERVICE_REQUEST_PAYMENT_SUCCESS,
                        CASE_PRINTED_STATE,
                        additionalData(true, "intestacy", false, emptyList(), false),
                        emptyList()
                ),
                Arguments.of(
                        CREATE_CASE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalDataEvidenceHandledBlank("intestacy", false, emptyList(), false),
                        emptyList()
                ),
                Arguments.of(
                        CREATE_CASE_PAYMENT_SUCCESS_EVENT,
                        CASE_PRINTED_STATE,
                        additionalDataEvidenceHandledBlank("intestacy", false, emptyList(), false),
                        emptyList()
                ),
                Arguments.of(
                        PAYMENT_SUCCESS_APP_EVENT,
                        CASE_PRINTED_STATE,
                        additionalDataEvidenceHandledBlank("intestacy", false, emptyList(), false),
                        emptyList()
                ),
                Arguments.of(
                        SOLICITOR_PAYMENT_SUCCESS_CASE_EVENT,
                        CASE_PRINTED_STATE,
                        additionalDataEvidenceHandledBlank("intestacy", false, emptyList(), false),
                        emptyList()
                ),
                Arguments.of(
                        SERVICE_REQUEST_PAYMENT_SUCCESS,
                        CASE_PRINTED_STATE,
                        additionalDataEvidenceHandledBlank("intestacy", false, emptyList(), false),
                        emptyList()
                )
        );
    }
}