package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToLong;

@Slf4j
@Component
public class OCRFieldNumberMapper {

    @ToLong
    public Long stringToLong(String longString) {
        log.info("Beginning mapping for Long value: {}", longString);
        Long returnValue;

        if (longString == null || longString.isEmpty()) {
            return null;
        } else {
            try {
                returnValue = Long.valueOf(longString);
            } catch (Exception e) {
                String errorMessage =
                    "Numerical field '" + longString + "' could not be converted to a Long number: " + e.getMessage();
                log.error(errorMessage);
                throw new OCRMappingException(errorMessage);
            }
        }
        return returnValue;
    }
}
