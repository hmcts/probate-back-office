package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.math.BigDecimal;
import java.util.Locale;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
@RequiredArgsConstructor
public class IhtEstateValidationRule implements CaseDetailsValidationRule {

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    private static final String MUST_ANSWER_UNUSED_ALLOWANCE = "answerUnusedAllowanceClaimed";
    private static final String IHT_ESTATE_VALUE_NEEDS_TAX = "ihtEstateValueNeedsTax";
    private static final double NQV_LOWER = 32500000;
    private static final double NQV_UPPER = 65000000;

    @Override
    public void validate(CaseDetails caseDetails) {

        CaseData caseData = caseDetails.getData();
        boolean estateValuesEntered =
            caseData.getIhtEstateGrossValue() != null && caseData.getIhtEstateNetValue() != null;
        boolean unusedClaimedNotSet = StringUtils.isEmpty(caseData.getIhtUnusedAllowanceClaimed());
        
        BigDecimal nqv = caseData.getIhtEstateNetQualifyingValue();
        if (estateValuesEntered && nqv != null) {
            boolean deceasedHadLateSpouseOrCivilPartner = YES.equals(caseData.getDeceasedHadLateSpouseOrCivilPartner());
            boolean nqvBetweenValues = (nqv.doubleValue() >= NQV_LOWER && nqv.doubleValue() <= NQV_UPPER);
            if (nqvBetweenValues && unusedClaimedNotSet && deceasedHadLateSpouseOrCivilPartner) {
                String userMessage = businessValidationMessageRetriever.getMessage(MUST_ANSWER_UNUSED_ALLOWANCE, null,
                    Locale.UK);
                throw new BusinessValidationException(userMessage,
                    "User must answer iht estate unused allowance question for case:" + caseDetails.getId());
            }
            boolean nqvLarger = nqv.doubleValue() > NQV_UPPER;
            boolean deceasedNOTHaveLateSpouseOrCivilPartner =
                NO.equals(caseData.getDeceasedHadLateSpouseOrCivilPartner());
            if (nqvLarger && (deceasedHadLateSpouseOrCivilPartner || deceasedNOTHaveLateSpouseOrCivilPartner)) {
                String userMessage = businessValidationMessageRetriever.getMessage(IHT_ESTATE_VALUE_NEEDS_TAX, null,
                    Locale.UK);
                throw new BusinessValidationException(userMessage,
                    "The estate does not qualify as excepted for case:" + caseDetails.getId());
            }
        }
    }
}
