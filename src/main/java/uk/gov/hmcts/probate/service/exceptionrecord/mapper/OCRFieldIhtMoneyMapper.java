package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToPennies;

import java.math.BigDecimal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OCRFieldIhtMoneyMapper {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    @ToPennies
    public Long poundsToPennies(final String monetaryValue) {
        log.info("Beginning mapping for monetary value: {}", monetaryValue);
        Long returnValue;

        if (monetaryValue == null || monetaryValue.isEmpty()) {
            return null;
        } else {
            String numericalMonetaryValue = monetaryValue.replaceAll("[^\\d^\\.]","");
            try {
                returnValue = new BigDecimal(numericalMonetaryValue).multiply(ONE_HUNDRED).longValue();
            } catch (Exception e) {
                String errorMessage = "Monetary field '" + monetaryValue + "' could not be converted to a number";
                log.error(errorMessage);
                throw new OCRMappingException(errorMessage);
            }
        }
        return returnValue;
    }
}
