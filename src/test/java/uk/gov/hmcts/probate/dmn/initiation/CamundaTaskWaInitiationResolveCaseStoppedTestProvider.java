package uk.gov.hmcts.probate.dmn.initiation;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.additionalData;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.ADMON_WILL_CASE_TYPE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.AD_COLLIGENDA_BONA_CASE_TYPE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.ATTACH_SCANNED_DOCS_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_CASE_STOPPED_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_REDECLARATION_COMPLETE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_REDECLARATION_SOT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_STOP_CASE_FOR_CASE_MATCHING_FOR_EXAMINING;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_STOP_CASE_FOR_CASE_PRINTED;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_STOP_CASE_FOR_REGISTRAR_ESCALATIONS;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CHANGE_STATE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CITIZEN_HUB_RESPONSE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.GOP_CASE_TYPE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.HANDLE_EVIDENCE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.INTESTACY_CASE_TYPE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.READY_TO_ISSUE_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_STOPPED_CASE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_STOPPED_CASE_TASK_TYPE_NAME;

public class CamundaTaskWaInitiationResolveCaseStoppedTestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {

        Map<String, Object> resolveStoppedCaseTaskAttributes = Map.of(
                "taskId", RESOLVE_STOPPED_CASE,
                "name", RESOLVE_STOPPED_CASE_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );

        return Stream.of(
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, "", false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        ATTACH_SCANNED_DOCS_EVENT,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, "", false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        CITIZEN_HUB_RESPONSE,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, "", false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        BO_REDECLARATION_COMPLETE,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, "", false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        BO_STOP_CASE_FOR_CASE_MATCHING_FOR_EXAMINING,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, "", false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        BO_STOP_CASE_FOR_REGISTRAR_ESCALATIONS,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, "", false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        BO_STOP_CASE_FOR_CASE_PRINTED,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, "", false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, "", false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        BO_REDECLARATION_SOT,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, "", false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        "someOtherEvent",
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, "", false, Collections.emptyList(), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        BO_STOP_CASE_FOR_CASE_PRINTED,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "", false, Collections.emptyList(), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        BO_CASE_STOPPED_STATE,
                        additionalData(true, "", false, Collections.emptyList(), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, GOP_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, ADMON_WILL_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, INTESTACY_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, AD_COLLIGENDA_BONA_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        ATTACH_SCANNED_DOCS_EVENT,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, GOP_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        ATTACH_SCANNED_DOCS_EVENT,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, ADMON_WILL_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        ATTACH_SCANNED_DOCS_EVENT,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, INTESTACY_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        ATTACH_SCANNED_DOCS_EVENT,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, AD_COLLIGENDA_BONA_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        CITIZEN_HUB_RESPONSE,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, GOP_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        CITIZEN_HUB_RESPONSE,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, ADMON_WILL_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        CITIZEN_HUB_RESPONSE,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, INTESTACY_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        CITIZEN_HUB_RESPONSE,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, AD_COLLIGENDA_BONA_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        BO_REDECLARATION_COMPLETE,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, GOP_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        BO_REDECLARATION_COMPLETE,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, ADMON_WILL_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        BO_REDECLARATION_COMPLETE,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, INTESTACY_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        BO_REDECLARATION_COMPLETE,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, AD_COLLIGENDA_BONA_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        BO_STOP_CASE_FOR_CASE_MATCHING_FOR_EXAMINING,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, GOP_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        BO_STOP_CASE_FOR_CASE_MATCHING_FOR_EXAMINING,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, ADMON_WILL_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        BO_STOP_CASE_FOR_CASE_MATCHING_FOR_EXAMINING,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, INTESTACY_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        BO_STOP_CASE_FOR_CASE_MATCHING_FOR_EXAMINING,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, AD_COLLIGENDA_BONA_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        BO_STOP_CASE_FOR_REGISTRAR_ESCALATIONS,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, GOP_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        BO_STOP_CASE_FOR_REGISTRAR_ESCALATIONS,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, ADMON_WILL_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        BO_STOP_CASE_FOR_REGISTRAR_ESCALATIONS,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, INTESTACY_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        BO_STOP_CASE_FOR_REGISTRAR_ESCALATIONS,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, AD_COLLIGENDA_BONA_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        BO_STOP_CASE_FOR_CASE_PRINTED,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, GOP_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        BO_STOP_CASE_FOR_CASE_PRINTED,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, ADMON_WILL_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        BO_STOP_CASE_FOR_CASE_PRINTED,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, INTESTACY_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        BO_STOP_CASE_FOR_CASE_PRINTED,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, AD_COLLIGENDA_BONA_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, GOP_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, ADMON_WILL_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, INTESTACY_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, AD_COLLIGENDA_BONA_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        BO_REDECLARATION_SOT,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, GOP_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        BO_REDECLARATION_SOT,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, ADMON_WILL_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        BO_REDECLARATION_SOT,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, INTESTACY_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                ),
                Arguments.of(
                        BO_REDECLARATION_SOT,
                        BO_CASE_STOPPED_STATE,
                        additionalData(false, AD_COLLIGENDA_BONA_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(resolveStoppedCaseTaskAttributes)
                )
        );
    }
}
