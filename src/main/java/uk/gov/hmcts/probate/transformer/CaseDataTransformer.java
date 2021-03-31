package uk.gov.hmcts.probate.transformer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.transformer.reset.ResetCaseDataTransformer;
import uk.gov.hmcts.probate.transformer.solicitorexecutors.LegalStatementExecutorTransformer;

@Component
@RequiredArgsConstructor
public class CaseDataTransformer {

    private final LegalStatementExecutorTransformer legalStatementExecutorTransformer;
    private final ResetCaseDataTransformer resetCaseDataTransformer;

    public void transformCaseDataForLegalStatement(CallbackRequest callbackRequest) {

        CaseData caseData = callbackRequest.getCaseDetails().getData();

        resetCaseDataTransformer.resetExecutorLists(caseData);
        legalStatementExecutorTransformer.mapSolicitorExecutorFieldsToLegalStatementExecutorFields(caseData);
        legalStatementExecutorTransformer.formatFields(caseData);
    }

    public void transformCaseDataForSolicitorExecutorNames(CallbackRequest callbackRequest) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();

        resetCaseDataTransformer.resetExecutorLists(caseData);
    }

}
