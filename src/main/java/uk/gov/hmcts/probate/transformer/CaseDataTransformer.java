package uk.gov.hmcts.probate.transformer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.transformer.solicitorexecutors.ExecutorsTransformer;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CaseDataTransformer {

    private final ExecutorsTransformer solicitorExecutorTransformer;

    public void transformCaseData(CallbackRequest callbackRequest) {

        CaseData caseData = callbackRequest.getCaseDetails().getData();

        List<CollectionMember<AdditionalExecutorApplying>> execsApplying = solicitorExecutorTransformer.
                mapSolicitorExecutorFieldsToLegalStatementExecutorFields(caseData);
        caseData.setExecutorsApplyingLegalStatement(execsApplying);
    }

}
