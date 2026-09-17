package uk.gov.hmcts.probate.dmn.initiation;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.additionalData;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.handOffReasonListWithHandOffReason;
import static uk.gov.hmcts.probate.dmn.initiation.CamundaTaskWaInitiationBaseTest.invalidHandOffReason;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.REVIEW_SME_REFERRAL;

public class CamundaTaskWaInitiationReviewSmeReferralTestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        Map<String,Object> reviewSMEReferralTaskAttributes = Map.of(
                "taskId", REVIEW_SME_REFERRAL,
                "name", "Review SME Referral",
                "processCategories", "case progression"
        );

        return Stream.of(
                Arguments.of(
                        "someOtherEventId",
                        "BOCaseWorkerEscalation",
                        additionalData(false, "",true, handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "someOtherEventId",
                        "BOCaseWorkerEscalation",
                        additionalData(true, "",true, handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "moveToCWEscalation",
                        "BOCaseWorkerEscalation",
                        additionalData(true, "",true, handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        List.of(reviewSMEReferralTaskAttributes)
                ),
                Arguments.of(
                        "moveToCWEscalation",
                        "BOCaseWorkerEscalation",
                        additionalData(false, "",true, handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        List.of(reviewSMEReferralTaskAttributes)
                ),
                Arguments.of(
                        "moveToCWEscalation",
                        "SomeOtherState",
                        additionalData(true, "",true, handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "moveToCWEscalation",
                        "SomeOtherState",
                        additionalData(false, "",true, handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                )
        );
    }

}
