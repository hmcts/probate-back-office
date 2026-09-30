package uk.gov.hmcts.probate.dmn.initiation;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.additionalData;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_REDECLARATION;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_REDECLARATION_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.HANDLE_EVIDENCE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.ATTACH_SCANNED_DOCS_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CHANGE_STATE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_REDEC_NOTIFICATION_SENT_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.READY_TO_ISSUE_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.GOP_CASE_TYPE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.ADMON_WILL_CASE_TYPE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.INTESTACY_CASE_TYPE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.AD_COLLIGENDA_BONA_CASE_TYPE;

public class CamundaTaskWaInitiationResolveRedeclarationTestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        Map<String,Object> resolveRedeclarationTaskAttributes = Map.of(
                "taskId", RESOLVE_REDECLARATION,
                "name", RESOLVE_REDECLARATION_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );

        return Stream.of(
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        BO_REDEC_NOTIFICATION_SENT_STATE,
                        additionalData(false, "",true,
                                Collections.emptyList(), false),
                        List.of(resolveRedeclarationTaskAttributes)
                ),
                Arguments.of(
                        "someOtherEvent",
                        BO_REDEC_NOTIFICATION_SENT_STATE,
                        additionalData(false, "",true,
                                Collections.emptyList(), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                Collections.emptyList(), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        BO_REDEC_NOTIFICATION_SENT_STATE,
                        additionalData(false, GOP_CASE_TYPE,true,
                                Collections.emptyList(), false),
                        List.of(resolveRedeclarationTaskAttributes)
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        BO_REDEC_NOTIFICATION_SENT_STATE,
                        additionalData(false, ADMON_WILL_CASE_TYPE,true,
                                Collections.emptyList(), false),
                        List.of(resolveRedeclarationTaskAttributes)
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        BO_REDEC_NOTIFICATION_SENT_STATE,
                        additionalData(false, INTESTACY_CASE_TYPE,true,
                                Collections.emptyList(), false),
                        List.of(resolveRedeclarationTaskAttributes)
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        BO_REDEC_NOTIFICATION_SENT_STATE,
                        additionalData(false, AD_COLLIGENDA_BONA_CASE_TYPE,true,
                                Collections.emptyList(), false),
                        List.of(resolveRedeclarationTaskAttributes)
                ),
                Arguments.of(
                        ATTACH_SCANNED_DOCS_EVENT,
                        BO_REDEC_NOTIFICATION_SENT_STATE,
                        additionalData(false, "",true,
                                Collections.emptyList(), false),
                        List.of(resolveRedeclarationTaskAttributes)
                ),
                Arguments.of(
                        "someOtherEvent",
                        BO_REDEC_NOTIFICATION_SENT_STATE,
                        additionalData(false, "",true,
                                Collections.emptyList(), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        ATTACH_SCANNED_DOCS_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                Collections.emptyList(), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        ATTACH_SCANNED_DOCS_EVENT,
                        BO_REDEC_NOTIFICATION_SENT_STATE,
                        additionalData(false, GOP_CASE_TYPE,true,
                                Collections.emptyList(), false),
                        List.of(resolveRedeclarationTaskAttributes)
                ),
                Arguments.of(
                        ATTACH_SCANNED_DOCS_EVENT,
                        BO_REDEC_NOTIFICATION_SENT_STATE,
                        additionalData(false, ADMON_WILL_CASE_TYPE,true,
                                Collections.emptyList(), false),
                        List.of(resolveRedeclarationTaskAttributes)
                ),
                Arguments.of(
                        ATTACH_SCANNED_DOCS_EVENT,
                        BO_REDEC_NOTIFICATION_SENT_STATE,
                        additionalData(false, INTESTACY_CASE_TYPE,true,
                                Collections.emptyList(), false),
                        List.of(resolveRedeclarationTaskAttributes)
                ),
                Arguments.of(
                        ATTACH_SCANNED_DOCS_EVENT,
                        BO_REDEC_NOTIFICATION_SENT_STATE,
                        additionalData(false, AD_COLLIGENDA_BONA_CASE_TYPE,true,
                                Collections.emptyList(), false),
                        List.of(resolveRedeclarationTaskAttributes)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        BO_REDEC_NOTIFICATION_SENT_STATE,
                        additionalData(false, "",true,
                                Collections.emptyList(), false),
                        List.of(resolveRedeclarationTaskAttributes)
                ),
                Arguments.of(
                        "someOtherEvent",
                        BO_REDEC_NOTIFICATION_SENT_STATE,
                        additionalData(false, "",true,
                                Collections.emptyList(), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, "",true,
                                Collections.emptyList(), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        BO_REDEC_NOTIFICATION_SENT_STATE,
                        additionalData(false, GOP_CASE_TYPE,true,
                                Collections.emptyList(), false),
                        List.of(resolveRedeclarationTaskAttributes)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        BO_REDEC_NOTIFICATION_SENT_STATE,
                        additionalData(false, ADMON_WILL_CASE_TYPE,true,
                                Collections.emptyList(), false),
                        List.of(resolveRedeclarationTaskAttributes)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        BO_REDEC_NOTIFICATION_SENT_STATE,
                        additionalData(false, INTESTACY_CASE_TYPE,true,
                                Collections.emptyList(), false),
                        List.of(resolveRedeclarationTaskAttributes)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        BO_REDEC_NOTIFICATION_SENT_STATE,
                        additionalData(false, AD_COLLIGENDA_BONA_CASE_TYPE,true,
                                Collections.emptyList(), false),
                        List.of(resolveRedeclarationTaskAttributes)
                )
        );
    }

}
