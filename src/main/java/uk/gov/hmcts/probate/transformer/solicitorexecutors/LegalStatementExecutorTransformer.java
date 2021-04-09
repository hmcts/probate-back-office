package uk.gov.hmcts.probate.transformer.solicitorexecutors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
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
     * Map all executors into executors applying and executors not applying lists for the solicitor legal statement.
     */
    public void mapSolicitorExecutorFieldsToLegalStatementExecutorFields(CaseData caseData) {

        // Create executor lists
        List<CollectionMember<AdditionalExecutorApplying>> execsApplying = createCaseworkerApplyingList(caseData);
        List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying =
                createCaseworkerNotApplyingList(caseData);

        createLegalStatementExecutorLists(execsApplying, execsNotApplying, caseData);
    }

    public void createLegalStatementExecutorListsFromTransformedLists(CaseData caseData) {
        List<CollectionMember<AdditionalExecutorApplying>> execsApplying = cloneExecsApplying(caseData);
        List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying = cloneExecsNotApplying(caseData);

        createLegalStatementExecutorLists(execsApplying, execsNotApplying, caseData);
    }

    protected void createLegalStatementExecutorLists(List<CollectionMember<AdditionalExecutorApplying>> execsApplying,
                                             List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying,
                                             CaseData caseData) {

        // Add primary applicant to list
        if (caseData.isPrimaryApplicantApplying()) {
            execsApplying.add(0, executorListMapperService.mapFromPrimaryApplicantToApplyingExecutor(caseData));
        } else if (caseData.isPrimaryApplicantNotApplying()) {
            execsNotApplying.add(0, executorListMapperService
                    .mapFromPrimaryApplicantToNotApplyingExecutor(caseData));
        }

        /* old code - remove once new code tested more thoroughly
            // Add primary applicant to list
            if (isSolicitorExecutor(caseData) && isSolicitorApplying(caseData)) {
                execsApplying.add(executorListMapperService.mapFromSolicitorToApplyingExecutor(caseData));
            } else if (caseData.isPrimaryApplicantApplying()) {
                execsApplying.add(executorListMapperService.mapFromPrimaryApplicantToApplyingExecutor(caseData));
            } else if (caseData.isPrimaryApplicantNotApplying()) {
                execsNotApplying.add(executorListMapperService.mapFromPrimaryApplicantToNotApplyingExecutor(caseData));
            }
         */

        caseData.setExecutorsApplyingLegalStatement(execsApplying);
        caseData.setExecutorsNotApplyingLegalStatement(execsNotApplying);
    }
}