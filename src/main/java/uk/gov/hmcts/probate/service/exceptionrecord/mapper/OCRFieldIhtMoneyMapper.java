package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToIHTFormId;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToPennies;
import uk.gov.hmcts.reform.probate.model.IhtFormType;

import java.math.BigDecimal;

@Slf4j
@Component
public class OCRFieldIhtMoneyMapper {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    @ToPennies
    public Long poundsToPennies(String monetaryValue) throws OCRMappingException {
        log.info("Beginning mapping for monetary value: {}", monetaryValue);
        Long returnValue;

        if (monetaryValue == null || monetaryValue.isEmpty()) {
            return null;
        } else {
            monetaryValue = monetaryValue.replaceAll("[^\\d^\\.]","");
            try {
                returnValue = new BigDecimal(monetaryValue).multiply(ONE_HUNDRED).longValue();
            } catch (Exception e) {
                String errorMessage = "Monetary field '" + monetaryValue + "' could not be mapped to a case: " + e.getMessage();
                log.error(errorMessage);
                throw new OCRMappingException(errorMessage);
            }
        }
        return returnValue;
    }

    @ToIHTFormId
    public IhtFormType ihtFormType(String ihtFormId) throws OCRMappingException {
        log.info("Beginning mapping for IHT Form Type value: {}", ihtFormId);

        if (ihtFormId == null || ihtFormId.isEmpty()) {
            return null;
        } else {
            switch (ihtFormId.toUpperCase().trim()) {
                case "IHT205":
                    return IhtFormType.IHT205;
                case "IHT207":
                    return IhtFormType.IHT207;
                case "IHT400421":
                    return IhtFormType.IHT400421;
                case "IHT421":
                    return IhtFormType.IHT400421;
                case "IHT400":
                    return IhtFormType.IHT400421;
                default:
                    String errorMessage = "Form type IHT205, IHT207 or IHT400421 expected but got '" + ihtFormId + "'";
                    log.error(errorMessage);
                    throw new OCRMappingException(errorMessage);
            }
        }
    }

}
