package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToIHTFormEstate;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToPennies;
import uk.gov.hmcts.reform.probate.model.IhtFormEstate;

import java.math.BigDecimal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OCRFieldIhtMoneyMapper {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private static final String FORM_IHT207 = "IHT207";
    private static final String FORM_IHT400421 = "IHT400421";

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

    @ToIHTFormEstate
    public IhtFormEstate ihtFormEstate(String ihtFormEstate) {
        log.info("Beginning mapping for IHT Form Type value: {}", ihtFormEstate);

        if (ihtFormEstate == null || ihtFormEstate.isEmpty()) {
            return null;
        } else {
            switch (ihtFormEstate.toUpperCase().trim()) {
                case FORM_IHT207:
                    return IhtFormEstate.optionIHT207;
                case FORM_IHT400421:
                    return IhtFormEstate.optionIHT400421;
                default:
                    String errorMessage = "Form type IHT207 or IHT400421 expected but got '" + ihtFormEstate + "'";
                    log.error(errorMessage);
                    throw new OCRMappingException(errorMessage);
            }
        }
    }
}
