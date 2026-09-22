package uk.gov.hmcts.probate.dmn.initiation;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.additionalData;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.AD_COLLIGENDA_BONA_CASE_TYPE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_CASE_QA_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_RESOLVE_STOP_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_SELECT_FOR_QA_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CASE_PRINTED_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.READY_TO_ISSUE_STATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.RESOLVE_SME_REFERRAL_EVENT;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.REVIEW_QA_CASE_AD_COLLIGENDA_BONA;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.REVIEW_QA_CASE_AD_COLLIGENDA_BONA_TASK_TYPE_NAME;

public class CamundaTaskWaInitiationReviewQaCaseAdColligendaBonaTestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {

        Map<String,Object> reviewQaCaseProbateTaskAttributes = Map.of(
                "taskId", REVIEW_QA_CASE_AD_COLLIGENDA_BONA,
                "name", REVIEW_QA_CASE_AD_COLLIGENDA_BONA_TASK_TYPE_NAME,
                "processCategories", "case progression"
        );

        return Stream.of(
                Arguments.of(
                        BO_SELECT_FOR_QA_EVENT,
                        BO_CASE_QA_STATE,
                        additionalData(false, AD_COLLIGENDA_BONA_CASE_TYPE, false, emptyList(), false),
                        List.of(reviewQaCaseProbateTaskAttributes)
                ),
                Arguments.of(
                        BO_RESOLVE_STOP_EVENT,
                        BO_CASE_QA_STATE,
                        additionalData(false, AD_COLLIGENDA_BONA_CASE_TYPE, false, emptyList(), false),
                        List.of(reviewQaCaseProbateTaskAttributes)
                ),
                Arguments.of(
                        RESOLVE_SME_REFERRAL_EVENT,
                        BO_CASE_QA_STATE,
                        additionalData(false, AD_COLLIGENDA_BONA_CASE_TYPE, false, emptyList(), false),
                        List.of(reviewQaCaseProbateTaskAttributes)
                ),
                // Negative: wrong event
                Arguments.of(
                        "someOtherEvent",
                        BO_CASE_QA_STATE,
                        additionalData(false, AD_COLLIGENDA_BONA_CASE_TYPE, false, emptyList(), false),
                        emptyList()
                ),
                // Negative: wrong state
                Arguments.of(
                        BO_SELECT_FOR_QA_EVENT,
                        CASE_PRINTED_STATE,
                        additionalData(false, AD_COLLIGENDA_BONA_CASE_TYPE, false, emptyList(), false),
                        emptyList()
                ),
                Arguments.of(
                        BO_SELECT_FOR_QA_EVENT,
                        READY_TO_ISSUE_STATE,
                        additionalData(false, AD_COLLIGENDA_BONA_CASE_TYPE, false, emptyList(), false),
                        emptyList()
                ),
                // Negative: wrong case type
                Arguments.of(
                        BO_SELECT_FOR_QA_EVENT,
                        BO_CASE_QA_STATE,
                        additionalData(false, "", false, emptyList(), false),
                        emptyList()
                ),
                // Negative: evidence already handled
                Arguments.of(
                        BO_SELECT_FOR_QA_EVENT,
                        BO_CASE_QA_STATE,
                        additionalData(true, AD_COLLIGENDA_BONA_CASE_TYPE, false, emptyList(), false),
                        emptyList()
                ),
                // Negative: no additional data
                Arguments.of(
                        BO_SELECT_FOR_QA_EVENT,
                        BO_CASE_QA_STATE,
                        null,
                        emptyList()
                )
        );
    }
}