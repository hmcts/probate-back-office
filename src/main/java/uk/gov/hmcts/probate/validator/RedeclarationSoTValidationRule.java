package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.PaperApplicationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.Locale;

import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
@RequiredArgsConstructor
public class RedeclarationSoTValidationRule implements CaseDetailsValidationRule {

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    private static final String REDEC_SOT = "redeclarationSotPaper";

    @Override
    public void validate(CaseDetails caseDetails) {
        String[] args = {caseDetails.getId().toString()};
        String userMessage = businessValidationMessageRetriever.getMessage(REDEC_SOT, args, Locale.UK);
        CaseData caseData = caseDetails.getData();

        if (caseData.getPaperForm().equals(YES)) {
            throw new PaperApplicationException(userMessage,
                    "A caseworker is trying to access redeclaration event with a paper case for case id "
                            + caseDetails.getId());
        }
    }

}
