package uk.gov.hmcts.probate.transformer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.transformer.reset.ResetCaseDataTransformer;
import uk.gov.hmcts.probate.transformer.solicitorexecutors.SolicitorApplicationCompletionTransformer;

@Component
@RequiredArgsConstructor
public class CaseDataTransformer {

    private final SolicitorApplicationCompletionTransformer solicitorApplicationCompletionTransformer;
    private final ResetCaseDataTransformer resetCaseDataTransformer;

    public void transformCaseDataForSolicitorJourneyCompletion(CallbackRequest callbackRequest) {

        final CaseData caseData = callbackRequest.getCaseDetails().getData();
        resetCaseDataTransformer.resetExecutorLists(caseData);
        solicitorApplicationCompletionTransformer.setFieldsIfSolicitorIsNotExecutor(caseData);
        solicitorApplicationCompletionTransformer
                .mapSolicitorExecutorFieldsOnCompletion(caseData);

        // Remove the solicitor exec lists. Will not be needed now mapped onto caseworker exec lists.
        solicitorApplicationCompletionTransformer.clearSolicitorExecutorLists(caseData);
    }

    public void transformCaseDataForValidateProbate(CallbackRequest callbackRequest) {
        final CaseData caseData = callbackRequest.getCaseDetails().getData();
        solicitorApplicationCompletionTransformer
                .mapSolicitorExecutorFieldsToLegalStatementExecutorFields(caseData);
    }

    public void transformCaseDataForLegalStatementRegeneration(CallbackRequest callbackRequest) {
        final CaseData caseData = callbackRequest.getCaseDetails().getData();
        solicitorApplicationCompletionTransformer.createLegalStatementExecutorListsFromTransformedLists(caseData);
    }

    public void transformCaseDataForSolicitorExecutorNames(CallbackRequest callbackRequest) {
        final CaseData caseData = callbackRequest.getCaseDetails().getData();
        resetCaseDataTransformer.resetExecutorLists(caseData);
    }

    public void transformSolCaseDataForCaseworkerCompletion(CallbackRequest callbackRequest) {
        final CaseData caseData = callbackRequest.getCaseDetails().getData();
        solicitorApplicationCompletionTransformer.mapPrimaryApplicantFields(caseData);
    }
}
