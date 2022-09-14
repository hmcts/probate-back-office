package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.math.BigDecimal;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class IhtNetQualifyingValueValidationRule implements CaseDetailsValidationRule {

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    private static final String IHT_NQV_GT_ESTATE_GROSS = "ihtNQVLargerThanEstateGross";
    private static final String IHT_NQV_GT_ESTATE_NET = "ihtNQVLargerThanEstateNet";
    private static final String IHT_NQV_GT_PROBATE_GROSS = "ihtNQVLargerThanProbateGross";
    private static final String IHT_NQV_GT_PROBATE_NET = "ihtNQVLargerThanProbateNet";

    @Override
    public void validate(CaseDetails caseDetails) {
        BigDecimal nqv = caseDetails.getData().getIhtEstateNetQualifyingValue();
        BigDecimal estateNet = caseDetails.getData().getIhtEstateNetValue();
        BigDecimal estateGross = caseDetails.getData().getIhtEstateGrossValue();
        BigDecimal probateNet = caseDetails.getData().getIhtNetValue();
        BigDecimal probateGross = caseDetails.getData().getIhtGrossValue();

        if (nqv != null) {
            if (estateGross != null && nqv.doubleValue() > estateGross.doubleValue()) {
                String userMessage = businessValidationMessageRetriever.getMessage(IHT_NQV_GT_ESTATE_GROSS, null,
                        Locale.UK);
                throw new BusinessValidationException(userMessage,
                        "NQV cannot be larger than Estate Gross value for case:" + caseDetails.getId());
            }
            if (estateNet != null && nqv.doubleValue() > estateNet.doubleValue()) {
                String userMessage = businessValidationMessageRetriever.getMessage(IHT_NQV_GT_ESTATE_NET, null,
                        Locale.UK);
                throw new BusinessValidationException(userMessage,
                        "NQV cannot be larger than Estate Net value for case:" + caseDetails.getId());
            }
            if (probateGross != null && nqv.doubleValue() > probateGross.doubleValue()) {
                String userMessage = businessValidationMessageRetriever.getMessage(IHT_NQV_GT_PROBATE_GROSS, null,
                        Locale.UK);
                throw new BusinessValidationException(userMessage,
                        "NQV cannot be larger than Probate Gross value for case:" + caseDetails.getId());
            }
            if (probateNet != null && nqv.doubleValue() > probateNet.doubleValue()) {
                String userMessage = businessValidationMessageRetriever.getMessage(IHT_NQV_GT_PROBATE_NET, null,
                        Locale.UK);
                throw new BusinessValidationException(userMessage,
                        "NQV cannot be larger than Probate Net value for case:" + caseDetails.getId());
            }
        }
    }
}
