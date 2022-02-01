package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToIHTFormEstateValuesCompleted;
import uk.gov.hmcts.probate.service.exceptionrecord.utils.EeDateOfDeathChecker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OCRFieldIhtFormEstateValuesCompletedMapper {
    
    @Autowired
    EeDateOfDeathChecker eeDateOfDeathChecker;

    @ToIHTFormEstateValuesCompleted
    public Boolean toIhtFormEstateValuesCompleted(ExceptionRecordOCRFields ocrFields) {
        log.info("Beginning mapping for ihtFormEstateValuesCompleted");

        if (ocrFields.getIhtEstateGrossValue() != null
            && ocrFields.getIhtEstateNetValue() != null
            && ocrFields.getIhtEstateNetQualifyingValue() != null) {
            return Boolean.FALSE;
        } else if (
            "IHT207".equalsIgnoreCase(ocrFields.getIhtFormEstate())
                || "IHT400421".equalsIgnoreCase(ocrFields.getIhtFormEstate())
        ) {
            return eeDateOfDeathChecker.isOnOrAfterSwitchDate(ocrFields.getDeceasedDateOfDeath()) 
                ? Boolean.TRUE : null;
        }
        return null;
    }
}