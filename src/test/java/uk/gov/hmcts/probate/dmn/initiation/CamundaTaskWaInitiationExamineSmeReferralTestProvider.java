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
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.ADMON_WILL_CASE_TYPE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.AD_COLLIGENDA_BONA_CASE_TYPE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_SME_REFERRAL;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_SME_REFERRAL_TASK_TYPE_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.GOP_CASE_TYPE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.INTESTACY_CASE_TYPE;

public class CamundaTaskWaInitiationExamineSmeReferralTestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        Map<String,Object> examineSMEReferralTaskAttributes = Map.of(
                "taskId", EXAMINE_SME_REFERRAL,
                "name", EXAMINE_SME_REFERRAL_TASK_TYPE_NAME,
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
                        additionalData(true, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOCaseWorkerEscalation",
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        List.of(examineSMEReferralTaskAttributes)
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOCaseWorkerEscalation",
                        additionalData(true, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "SomeOtherState",
                        additionalData(true, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "SomeOtherState",
                        additionalData(false, "",true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "someOtherEventId",
                        "BOCaseWorkerEscalation",
                        additionalData(false, GOP_CASE_TYPE,true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "someOtherEventId",
                        "BOCaseWorkerEscalation",
                        additionalData(true, GOP_CASE_TYPE,true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOCaseWorkerEscalation",
                        additionalData(false, GOP_CASE_TYPE,true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        List.of(examineSMEReferralTaskAttributes)
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOCaseWorkerEscalation",
                        additionalData(true, GOP_CASE_TYPE,true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "SomeOtherState",
                        additionalData(true, GOP_CASE_TYPE,true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "SomeOtherState",
                        additionalData(false, GOP_CASE_TYPE,true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "someOtherEventId",
                        "BOCaseWorkerEscalation",
                        additionalData(false, ADMON_WILL_CASE_TYPE,true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "someOtherEventId",
                        "BOCaseWorkerEscalation",
                        additionalData(true, ADMON_WILL_CASE_TYPE,true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOCaseWorkerEscalation",
                        additionalData(false, ADMON_WILL_CASE_TYPE,true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        List.of(examineSMEReferralTaskAttributes)
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOCaseWorkerEscalation",
                        additionalData(true, ADMON_WILL_CASE_TYPE,true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "SomeOtherState",
                        additionalData(true, ADMON_WILL_CASE_TYPE,true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "SomeOtherState",
                        additionalData(false, ADMON_WILL_CASE_TYPE,true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "someOtherEventId",
                        "BOCaseWorkerEscalation",
                        additionalData(false, INTESTACY_CASE_TYPE,true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "someOtherEventId",
                        "BOCaseWorkerEscalation",
                        additionalData(true, INTESTACY_CASE_TYPE,true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOCaseWorkerEscalation",
                        additionalData(false, INTESTACY_CASE_TYPE,true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        List.of(examineSMEReferralTaskAttributes)
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOCaseWorkerEscalation",
                        additionalData(true, INTESTACY_CASE_TYPE,true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "SomeOtherState",
                        additionalData(true, INTESTACY_CASE_TYPE,true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "SomeOtherState",
                        additionalData(false, INTESTACY_CASE_TYPE,true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),

                Arguments.of(
                        "someOtherEventId",
                        "BOCaseWorkerEscalation",
                        additionalData(false, AD_COLLIGENDA_BONA_CASE_TYPE,true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "someOtherEventId",
                        "BOCaseWorkerEscalation",
                        additionalData(true, AD_COLLIGENDA_BONA_CASE_TYPE,true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOCaseWorkerEscalation",
                        additionalData(false, AD_COLLIGENDA_BONA_CASE_TYPE,true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        List.of(examineSMEReferralTaskAttributes)
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOCaseWorkerEscalation",
                        additionalData(true, AD_COLLIGENDA_BONA_CASE_TYPE,true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "SomeOtherState",
                        additionalData(true, AD_COLLIGENDA_BONA_CASE_TYPE,true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "SomeOtherState",
                        additionalData(false, AD_COLLIGENDA_BONA_CASE_TYPE,true,
                                handOffReasonListWithHandOffReason(invalidHandOffReason), false),
                        Collections.emptyList()
                )
        );
    }

}
