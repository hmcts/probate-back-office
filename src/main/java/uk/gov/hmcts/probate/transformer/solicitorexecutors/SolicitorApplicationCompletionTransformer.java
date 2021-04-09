package uk.gov.hmcts.probate.transformer.solicitorexecutors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.solicitorexecutor.ExecutorListMapperService;

@Component
@Slf4j
// Handles some casedata mappings for when a solicitor application becomes a case
// for caseworker or solicitor journeys
public class SolicitorJourneyCompletionTransformer extends LegalStatementExecutorTransformer {

    public SolicitorJourneyCompletionTransformer(ExecutorListMapperService executorListMapperService) {
        super(executorListMapperService);
    }

    /**
     * Map all executors into executors applying and executors not applying lists for the solicitor legal statement.
     */
    public void mapSolicitorExecutorFieldsOnCompletion(CaseData caseData) {

        mapSolicitorExecutorFieldsToCaseworkerExecutorFields(caseData);
        createLegalStatementExecutorListsFromTransformedLists(caseData);
    }

}
