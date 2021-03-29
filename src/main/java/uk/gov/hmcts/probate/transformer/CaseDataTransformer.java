package uk.gov.hmcts.probate.transformer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.transformer.solicitorexecutors.LegalStatementExecutorTransformer;

@Component
@RequiredArgsConstructor
public class CaseDataTransformer {

    private final LegalStatementExecutorTransformer legalStatementExecutorTransformer;

    public void transformCaseData(CallbackRequest callbackRequest) {

        CaseData caseData = callbackRequest.getCaseDetails().getData();

        legalStatementExecutorTransformer.mapSolicitorExecutorFieldsToLegalStatementExecutorFields(caseData);
        legalStatementExecutorTransformer.formatDates(caseData);
    }

}
