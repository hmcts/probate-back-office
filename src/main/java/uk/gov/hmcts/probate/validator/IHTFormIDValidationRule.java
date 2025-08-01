package uk.gov.hmcts.probate.validator;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;

import java.util.Locale;

import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.NOT_APPLICABLE_VALUE;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT400421_VALUE;
import static uk.gov.hmcts.probate.model.Constants.YES;


@Component
public class IHTFormIDValidationRule {

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;
    private final ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;

    public IHTFormIDValidationRule(
            final BusinessValidationMessageRetriever businessValidationMessageRetriever,
            final ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker) {
        this.businessValidationMessageRetriever = businessValidationMessageRetriever;
        this.exceptedEstateDateOfDeathChecker = exceptedEstateDateOfDeathChecker;
    }

    public void validate(CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();
        if (!exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate(caseData.getDeceasedDateOfDeath())) {
            if (NOT_APPLICABLE_VALUE.equalsIgnoreCase(caseData.getIhtFormId())
                    || IHT400421_VALUE.equalsIgnoreCase(caseData.getIhtFormId())) {
                String userMessage = businessValidationMessageRetriever
                        .getMessage("ihtFormIDInvalid", null, Locale.UK);
                String userMessageWelsh = businessValidationMessageRetriever
                        .getMessage("ihtFormIDInvalidWelsh", null, Locale.UK);
                throw new BusinessValidationException(userMessage,
                        "IHTFormID is invalid: " + caseDetails.getId(), userMessageWelsh);
            }
        } else {
            final String ihtFormEstateValuesCompleted = caseData.getIhtFormEstateValuesCompleted();
            if (YES.equalsIgnoreCase(ihtFormEstateValuesCompleted)) {
                final String ihtFormEstate = caseData.getIhtFormEstate();
                if (NOT_APPLICABLE_VALUE.equalsIgnoreCase(ihtFormEstate)
                        || IHT400421_VALUE.equalsIgnoreCase(ihtFormEstate)) {
                    String userMessage = businessValidationMessageRetriever
                            .getMessage("ihtFormEstateInvalid", null, Locale.UK);
                    String userMessageWelsh = businessValidationMessageRetriever
                            .getMessage("ihtFormEstateInvalidWelsh", null, Locale.UK);
                    throw new BusinessValidationException(userMessage,
                            "IHTFormEstate is invalid: " + caseDetails.getId(), userMessageWelsh);
                }
            }
        }
    }
}
