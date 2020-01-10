package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToYesOrNo;

@Slf4j
@Component
public class OCRFieldYesOrNoMapper {

    @SuppressWarnings({"squid:S1168", "squid:S2447"})
    @ToYesOrNo
    public Boolean toYesOrNo(String booleanValue) {
        log.info("Beginning mapping for Yes or No value: {}", booleanValue);

        if (booleanValue == null || booleanValue.isEmpty()) {
            return null;
        } else {
            switch (booleanValue.toUpperCase().trim()) {
                case "YES":
                    return true;
                case "NO":
                    return false;
                case "TRUE":
                    return true;
                case "FALSE":
                    return false;
                default:
                    String errorMessage = "Yes, no, true or false values expected but got '" + booleanValue + "'";
                    log.error(errorMessage);
                    throw new OCRMappingException(errorMessage);
            }
        }
    }
}