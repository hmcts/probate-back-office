package uk.gov.hmcts.probate.transformer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.transformer.reset.ResetCaseDataTransformer;
import uk.gov.hmcts.probate.transformer.solicitorexecutors.SolicitorJourneyCompletionTransformer;

@Component
@RequiredArgsConstructor
public class CaseDataTransformer {

    private final SolicitorJourneyCompletionTransformer solicitorJourneyCompletionTransformer;
    private final ResetCaseDataTransformer resetCaseDataTransformer;

    public void transformCaseDataForSolicitorJourneyCompletion(CallbackRequest callbackRequest) {

        CaseData caseData = callbackRequest.getCaseDetails().getData();

        resetCaseDataTransformer.resetExecutorLists(caseData);
        solicitorJourneyCompletionTransformer.setFieldsIfSolicitorIsNotExecutor(caseData);
        solicitorJourneyCompletionTransformer.mapSolicitorExecutorFieldsToCaseworkerExecutorFields(caseData);
        solicitorJourneyCompletionTransformer.mapSolicitorExecutorFieldsToLegalStatementExecutorFields(caseData);
        // Remove the solicitor exec lists. Will not be needed now mapped onto caseworker exec lists.
        solicitorJourneyCompletionTransformer.clearSolicitorExecutorLists(caseData);
    }

    public void transformCaseDataForSolicitorExecutorNames(CallbackRequest callbackRequest) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();

        resetCaseDataTransformer.resetExecutorLists(caseData);
    }

}
