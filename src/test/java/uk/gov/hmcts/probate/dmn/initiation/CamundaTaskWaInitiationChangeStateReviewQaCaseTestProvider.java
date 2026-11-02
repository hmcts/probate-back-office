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
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.fiatWillHandOffReason;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.handOffReasonListWithHandOffReason;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.ADMON_WILL_CASE_TYPE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.AD_COLLIGENDA_BONA_CASE_TYPE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_CASE_QA_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CHANGE_STATE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.GOP_CASE_TYPE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.HANDLE_EVIDENCE_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.INTESTACY_CASE_TYPE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.REVIEW_QA_CASE_ADMON;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.REVIEW_QA_CASE_ADMON_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.REVIEW_QA_CASE_AD_COLLIGENDA_BONA;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.REVIEW_QA_CASE_AD_COLLIGENDA_BONA_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.REVIEW_QA_CASE_INTESTACY;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.REVIEW_QA_CASE_INTESTACY_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.REVIEW_QA_CASE_PROBATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.REVIEW_QA_CASE_PROBATE_TASK_TYPE_NAME;


public class CamundaTaskWaInitiationChangeStateReviewQaCaseTestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {

        Map<String, Object> reviewQaCaseProbateTask = Map.of(
                "taskId", REVIEW_QA_CASE_PROBATE,
                "name", REVIEW_QA_CASE_PROBATE_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );

        Map<String, Object> reviewQaCaseAdmonTask = Map.of(
                "taskId", REVIEW_QA_CASE_ADMON,
                "name", REVIEW_QA_CASE_ADMON_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );

        Map<String, Object> reviewQaCaseIntestacyTask = Map.of(
                "taskId", REVIEW_QA_CASE_INTESTACY,
                "name", REVIEW_QA_CASE_INTESTACY_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );

        Map<String, Object> reviewQaCaseAdColligendaBonaTask = Map.of(
                "taskId", REVIEW_QA_CASE_AD_COLLIGENDA_BONA,
                "name", REVIEW_QA_CASE_AD_COLLIGENDA_BONA_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );

        return Stream.of(
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        BO_CASE_QA_STATE,
                        additionalData(false, GOP_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(reviewQaCaseProbateTask)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        BO_CASE_QA_STATE,
                        additionalData(true, GOP_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(reviewQaCaseProbateTask)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        BO_CASE_QA_STATE,
                        additionalData(false, GOP_CASE_TYPE, true,
                                handOffReasonListWithHandOffReason(fiatWillHandOffReason), false),
                        List.of(reviewQaCaseProbateTask)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        BO_CASE_QA_STATE,
                        additionalData(false, GOP_CASE_TYPE, false, Collections.emptyList(), true),
                        List.of(reviewQaCaseProbateTask)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        BO_CASE_QA_STATE,
                        additionalData(false, ADMON_WILL_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(reviewQaCaseAdmonTask)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        BO_CASE_QA_STATE,
                        additionalData(true, ADMON_WILL_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(reviewQaCaseAdmonTask)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        BO_CASE_QA_STATE,
                        additionalData(false, ADMON_WILL_CASE_TYPE, true,
                                handOffReasonListWithHandOffReason(fiatWillHandOffReason), false),
                        List.of(reviewQaCaseAdmonTask)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        BO_CASE_QA_STATE,
                        additionalData(false, INTESTACY_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(reviewQaCaseIntestacyTask)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        BO_CASE_QA_STATE,
                        additionalData(true, INTESTACY_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(reviewQaCaseIntestacyTask)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        BO_CASE_QA_STATE,
                        additionalData(false, INTESTACY_CASE_TYPE, true,
                                handOffReasonListWithHandOffReason(fiatWillHandOffReason), false),
                        List.of(reviewQaCaseIntestacyTask)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        BO_CASE_QA_STATE,
                        additionalData(false, AD_COLLIGENDA_BONA_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(reviewQaCaseAdColligendaBonaTask)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        BO_CASE_QA_STATE,
                        additionalData(true, AD_COLLIGENDA_BONA_CASE_TYPE, false, Collections.emptyList(), false),
                        List.of(reviewQaCaseAdColligendaBonaTask)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        BO_CASE_QA_STATE,
                        additionalData(false, AD_COLLIGENDA_BONA_CASE_TYPE, true,
                                handOffReasonListWithHandOffReason(fiatWillHandOffReason), false),
                        List.of(reviewQaCaseAdColligendaBonaTask)
                ),
                Arguments.of(
                        CHANGE_STATE_EVENT,
                        BO_CASE_QA_STATE,
                        additionalData(false, "", false, Collections.emptyList(), false),
                        emptyList()
                ),
                Arguments.of(
                        HANDLE_EVIDENCE_EVENT,
                        BO_CASE_QA_STATE,
                        additionalData(false, GOP_CASE_TYPE, false, Collections.emptyList(), false),
                        emptyList()
                ),
                Arguments.of(
                        "someOtherEvent",
                        BO_CASE_QA_STATE,
                        additionalData(false, INTESTACY_CASE_TYPE, false, Collections.emptyList(), false),
                        emptyList()
                )
        );

    }
}