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
// Handles some casedata mappings for when a solicitor application becomes a case
// for caseworker or solicitor journeys
public class SolicitorApplicationCompletionTransformer extends LegalStatementExecutorTransformer {

    public SolicitorApplicationCompletionTransformer(ExecutorListMapperService executorListMapperService) {
        super(executorListMapperService);
    }

    /**
     * Map all executors into executors applying and executors not applying lists for the solicitor legal statement.
     */
    public void mapSolicitorExecutorFieldsOnCompletion(CaseData caseData) {

        mapSolicitorExecutorFieldsToCaseworkerExecutorFields(caseData);
        createLegalStatementExecutorListsFromTransformedLists(caseData);
    }

    public void mapSolicitorExecutorFieldsOnAppDetailsComplete(CaseData caseData) {
        if (isSolicitorApplying(caseData)) {
            List<CollectionMember<AdditionalExecutorApplying>> execsApplying = createCaseworkerApplyingList(caseData);
            mapExecutorToPrimaryApplicantFields(execsApplying.get(0).getValue(), caseData);
        }
        mapSolicitorExecutorFieldsToLegalStatementExecutorFields(caseData);
    }
}
