package uk.gov.hmcts.probate.service.ocr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields;
import uk.gov.hmcts.probate.validator.IhtEstateValidationRule;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_FIELD_WARNING_STRING;

@Slf4j
@Service
@RequiredArgsConstructor
public class MandatoryFieldsValidatorUtils {

    private final IhtEstateValidationRule ihtEstateValidationRule;
    private static final String VERSION_KEY = "formVersion";
    private static final String DECEASED_MARITAL_STATUS_KEY = "deceasedMartialStatus";
    private static final String DECEASED_MARITAL_STATUS_WIDOWED = "widowed";
    private static final String IHT_ESTATE_NET_QUALIFYING_VALUE = "ihtEstateNetQualifyingValue";

    public void addWarningIfEmpty(Map<String, String> ocrFieldValues, List<String> warnings,
                                  DefaultKeyValue keyValue) {
        if (isEmpty(ocrFieldValues.get(keyValue.getKey()))) {
            log.warn("{} was not found in ocr fields when expected", keyValue.getKey());
            warnings.add(format(MANDATORY_FIELD_WARNING_STRING, keyValue.getValue(),
                keyValue.getKey()));
        }
    }

    public void addWarningsForConditionalFields(Map<String, String> ocrFieldValues, List<String> warnings,
                                                GORCitizenMandatoryFields... toCheck) {
        Stream.of(toCheck).forEach(field -> {
            if (!ocrFieldValues.containsKey(field.getKey())) {
                log.warn("{} was not found in ocr fields when expected", field.getKey());
                warnings.add(format(MANDATORY_FIELD_WARNING_STRING, field.getValue(), field.getKey()));
            }
        });
    }

    public void addWarning(String warning, List<String> warnings) {
        log.warn(warning);
        warnings.add(warning);
    }

    public boolean isVersion2(Map<String, String> ocrFieldValues) {
        return "2".equals(ocrFieldValues.get(VERSION_KEY));
    }

    public boolean isVersion3(Map<String, String> ocrFieldValues) {
        return "3".equals(ocrFieldValues.get(VERSION_KEY));
    }

    public boolean nqvBetweenThresholds(Map<String, String> ocrFieldValues) {
        String ihtEstateNetQualifyingValue = ocrFieldValues.get(IHT_ESTATE_NET_QUALIFYING_VALUE);
        if (ihtEstateNetQualifyingValue != null) {
            String numericalMonetaryValue = ihtEstateNetQualifyingValue.replaceAll("[^\\d^\\.]","");
            if (NumberUtils.isCreatable((numericalMonetaryValue))) {
                BigDecimal nqv = new BigDecimal(numericalMonetaryValue).multiply(BigDecimal.valueOf(100));
                return ihtEstateValidationRule.isNqvBetweenValues(nqv);
            }
        }
        return false;
    }

    public boolean hasLateSpouseCivilPartner(Map<String, String> ocrFieldValues) {
        String deceasedMaritalStatus = ocrFieldValues.get(DECEASED_MARITAL_STATUS_KEY);
        if (deceasedMaritalStatus != null) {
            return DECEASED_MARITAL_STATUS_WIDOWED.equals(deceasedMaritalStatus);
        }
        return false;
    }
}
