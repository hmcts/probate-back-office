package uk.gov.hmcts.probate.transformer.solicitorexecutors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.solicitorexecutor.ExecutorListMapperService;
import uk.gov.hmcts.probate.service.solicitorexecutor.ExecutorTypeService;

import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
@Slf4j
public class LegalStatementExecutorTransformer extends ExecutorsTransformer {

    public LegalStatementExecutorTransformer(ExecutorListMapperService executorListMapperService,
                                             ExecutorTypeService executorTypeService) {
        super(executorListMapperService, executorTypeService);
    }

    /**
     * Map all executors into executors applying and executors not applying lists for the solicitor legal statement.
     */
    public void mapSolicitorExecutorFieldsToLegalStatementExecutorFields(CaseData caseData) {

        // Create executor lists
        List<CollectionMember<AdditionalExecutorApplying>> execsApplying = createCaseworkerApplyingList(caseData);
        List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying =
                createCaseworkerNotApplyingList(caseData);

        // Add primary applicant to list
        // Todo change this to is solicitor main applicant
        if ((executorTypeService.isSolicitorExecutorNamedOnWill(caseData)
                && isSolicitorNamedOnWillApplying(caseData)) || YES.equals(caseData.getSolTitleAndClearingExecutor())) {
            execsApplying.add(executorListMapperService.mapFromSolicitorToApplyingExecutor(caseData));
        } else if (caseData.isPrimaryApplicantApplying()) {
            execsApplying.add(executorListMapperService.mapFromPrimaryApplicantToApplyingExecutor(caseData));
        } else if (caseData.isPrimaryApplicantNotApplying()) {
            execsNotApplying.add(executorListMapperService.mapFromPrimaryApplicantToNotApplyingExecutor(caseData));
        }

        caseData.setExecutorsApplyingLegalStatement(execsApplying);
        caseData.setExecutorsNotApplyingLegalStatement(execsNotApplying);
    }



}
