package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.Locale;

import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
@RequiredArgsConstructor
public class BulkPrintWillSelectionValidationRule implements CaseDetailsValidationRule {

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    private static final String NO_SINGLE_WILL_SELECTION_MADE_FOR_GRANT_ISSUE 
        = "noSingleWillSelectionMadeForGrantIssue";

    @Override
    public void validate(CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();

        long numSelectedWills = caseData.getWillSelection().stream()
            .map(CollectionMember::getValue)
            .filter(willDocument -> willDocument.getDocumentSelected().contains(YES))
            .count();

        if (numSelectedWills != 1) {
            String[] args = {caseDetails.getId().toString()};
            String userMessage = businessValidationMessageRetriever
                .getMessage(NO_SINGLE_WILL_SELECTION_MADE_FOR_GRANT_ISSUE, args, Locale.UK);
            throw new BusinessValidationException(userMessage,
                "No single will has been selected for Grant Issue for case id "
                    + caseDetails.getId());
        }
    }
}
