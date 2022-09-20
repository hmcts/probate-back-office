package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT207_VALUE;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT400421_VALUE;

import java.util.Locale;

import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
@RequiredArgsConstructor
public class IHTValidationRule implements CaseDetailsValidationRule {

    public static final String IHT_PROBATE_NET_GREATER_THAN_GROSS = "ihtProbateNetGreaterThanGross";
    public static final String IHT_ESTATE_NET_GREATER_THAN_GROSS = "ihtEstateNetGreaterThanGross";

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    @Override
    public void validate(CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();
        boolean ihtFormCompleted = YES.equals(caseData.getIhtFormEstateValuesCompleted());
        boolean iht207 = IHT207_VALUE.equals(caseData.getIhtFormEstate());
        boolean iht400421 = IHT400421_VALUE.equals(caseData.getIhtFormEstate());
        if (ihtFormCompleted && (iht207 || iht400421)) {
            validateIHTProbateValue(caseData, caseDetails.getId());
        } else if (!ihtFormCompleted) {
            validateIHTEstateValue(caseData, caseDetails.getId());
            validateIHTProbateValue(caseData, caseDetails.getId());
        }
    }

    public void validateIHTProbateValue(CaseData caseData, Long caseId) {
        if (caseData.getIhtNetValue() != null && caseData.getIhtGrossValue() != null
                && caseData.getIhtNetValue().compareTo(caseData.getIhtGrossValue()) > 0) {
            String userMessage = businessValidationMessageRetriever.getMessage(IHT_PROBATE_NET_GREATER_THAN_GROSS, null,
                    Locale.UK);
            throw new BusinessValidationException(userMessage,
                    "The gross probate value cannot be less than the net probate value for case:" + caseId);
        }
    }

    public void validateIHTEstateValue(CaseData caseData, Long caseId) {
        if (caseData.getIhtEstateNetValue() != null && caseData.getIhtEstateGrossValue() != null
                && caseData.getIhtEstateNetValue().compareTo(caseData.getIhtEstateGrossValue()) > 0) {
            String userMessage = businessValidationMessageRetriever.getMessage(IHT_ESTATE_NET_GREATER_THAN_GROSS, null,
                    Locale.UK);
            throw new BusinessValidationException(userMessage,
                    "The gross IHT value cannot be less than the net IHT value for case:" + caseId);
        }
    }
}
