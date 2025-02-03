package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.InheritanceTax;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;
import static uk.gov.hmcts.probate.model.Constants.CHANNEL_CHOICE_DIGITAL;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
@Slf4j
@RequiredArgsConstructor
public class IHTValidationRule implements SolAddDeceasedEstateDetailsValidationRule {

    public static final String IHT_PROBATE_NET_GREATER_THAN_GROSS = "ihtProbateNetGreaterThanGross";
    public static final String IHT_ESTATE_NET_GREATER_THAN_GROSS = "ihtEstateNetGreaterThanGross";
    public static final String
            IHT_NETQUALIFYING_VALUE_GREATER_THAN_ESTATE_GROSS_VAlUE = "ihtEstateNetQualifyingValueGreaterThanGross";
    public static final String
            IHT_NETQUALIFYING_VALUE_GREATER_THAN_ESTATE_NET_VALUE = "ihtEstateNetQualifyingValueGreaterThanNet";

    public static final String IHT_VALUE_VALIDATION = "ihtValueValidation";
    public static final String WELSH = "Welsh";
    private static final String IHT400 = "IHT400";

    private final BusinessValidationMessageService businessValidationMessageService;
    private final ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;
    private static final String REGEX_PATTERN = "^\\d\\d*$";

    @Override
    public List<FieldErrorResponse> validate(CCDData ccdData) {
        InheritanceTax iht = ccdData.getIht();
        if (iht == null) {
            return Collections.emptyList();
        }

        List<String> codes = new ArrayList<>();
        validateIhtValues(iht, codes);
        validateGrossAndNetComparisons(ccdData, codes);

        return codes.stream()
                .map(code -> businessValidationMessageService.generateError(BUSINESS_ERROR, code))
                .collect(Collectors.toList());
    }

    private void validateIhtValues(InheritanceTax iht, List<String> codes) {
        if (isInvalidIhtValue(iht.getGrossValue())
            || isInvalidIhtValue(iht.getNetValue())
            || isInvalidIhtValue(iht.getIhtFormNetValue())
            || isInvalidIhtValue(iht.getIhtEstateGrossValue())
            || isInvalidIhtValue(iht.getIhtEstateNetValue())) {
            addValidationMessageCodes(codes, IHT_VALUE_VALIDATION);
        }
    }

    private boolean isInvalidIhtValue(BigDecimal value) {
        return value != null && !value.toPlainString().matches(REGEX_PATTERN);
    }

    private void validateGrossAndNetComparisons(CCDData ccdData, List<String> codes) {
        InheritanceTax iht = ccdData.getIht();
        if (shouldValidateIhtFormNetValue(ccdData)) {
            validateComparison(iht.getIhtFormNetValue(), iht.getGrossValue(), codes,
                    IHT_PROBATE_NET_GREATER_THAN_GROSS);
        } else {
            validateComparison(iht.getNetValue(), iht.getGrossValue(), codes,
                    IHT_PROBATE_NET_GREATER_THAN_GROSS);
        }
        validateComparison(iht.getIhtEstateNetValue(), iht.getIhtEstateGrossValue(), codes,
                IHT_ESTATE_NET_GREATER_THAN_GROSS);
        validateComparison(iht.getIhtEstateNetQualifyingValue(), iht.getIhtEstateNetValue(), codes,
                IHT_NETQUALIFYING_VALUE_GREATER_THAN_ESTATE_NET_VALUE);
        validateComparison(iht.getIhtEstateNetQualifyingValue(), iht.getIhtEstateGrossValue(), codes,
                IHT_NETQUALIFYING_VALUE_GREATER_THAN_ESTATE_GROSS_VAlUE);
    }

    private void validateComparison(BigDecimal comparisonValue, BigDecimal referenceValue,
                                    List<String> codes, String code) {
        if (comparisonValue != null && referenceValue != null && comparisonValue.compareTo(referenceValue) > 0) {
            addValidationMessageCodes(codes, code);
        }
    }

    private void addValidationMessageCodes(List<String> codes, String code) {
        codes.add(code);
        codes.add(code + WELSH);
    }

    private boolean shouldValidateIhtFormNetValue(CCDData ccdData) {
        boolean isOnOrAfterSwitchDate =
                exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate(ccdData.getDeceasedDateOfDeath());
        return (SOLICITOR.toString().equals(ccdData.getApplicationType())
                && CHANNEL_CHOICE_DIGITAL.equalsIgnoreCase(ccdData.getChannelChoice()))
            && ((!isOnOrAfterSwitchDate && IHT400.equals(ccdData.getIht().getFormName()))
                || (isOnOrAfterSwitchDate && IHT400.equals(ccdData.getIht().getIhtFormEstate())
                    && YES.equalsIgnoreCase(ccdData.getIht().getIhtFormEstateValuesCompleted())));
    }
}
