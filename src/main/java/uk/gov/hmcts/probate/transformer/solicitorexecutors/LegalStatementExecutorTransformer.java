package uk.gov.hmcts.probate.transformer.solicitorexecutors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.solicitorexecutor.ExecutorListMapperService;

import java.util.List;

@Component
@Slf4j
public class LegalStatementExecutorTransformer extends ExecutorsTransformer {

    public LegalStatementExecutorTransformer(ExecutorListMapperService executorListMapperService) {
        super(executorListMapperService);
    }

    /**
     * Todo check if this is a good place for this
     */
    public List<CollectionMember<AdditionalExecutorApplying>> mapSolicitorExecutorFieldsToLegalStatementExecutorFields(
            CaseData caseData) {

        // Create executor lists
        List<CollectionMember<AdditionalExecutorApplying>> execsApplying = createCaseworkerApplyingList(caseData);

        // Add primary applicant to list
        if (isSolicitorExecutor(caseData) && isSolicitorApplying(caseData)) {
            execsApplying.add(executorListMapperService.mapFromSolicitorToApplyingExecutor(caseData));
        } else if (caseData.isPrimaryApplicantApplying()) {
            execsApplying.add(executorListMapperService.mapFromPrimaryApplicantToApplyingExecutor(caseData));
        }

        return execsApplying;
    }


}
