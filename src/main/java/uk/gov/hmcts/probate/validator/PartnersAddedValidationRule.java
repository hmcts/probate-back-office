package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class PartnersAddedValidationRule {

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;
    private static final String PARTNERS_NEEDED = "partnersNeeded";

    public void validate(CaseDetails caseDetails) {

        CaseData caseData = caseDetails.getData();
        String[] args = {caseDetails.getId().toString()};
        String userMessage = businessValidationMessageRetriever.getMessage(PARTNERS_NEEDED, args, Locale.UK);

        if (caseData.getAnyOtherApplyingPartners() != null) {
            if (!(caseData.getSolsSolicitorIsExec().matches("No")
                && caseData.getSolsSolicitorIsApplying().matches("Yes"))
                && caseData.getAnyOtherApplyingPartners().matches("No")) {

                throw new BusinessValidationException(userMessage,
                    "'Yes' needs to be selected for question anyOtherApplyingPartners for case id "
                        + caseDetails.getId());
            }
        }
    }
}
