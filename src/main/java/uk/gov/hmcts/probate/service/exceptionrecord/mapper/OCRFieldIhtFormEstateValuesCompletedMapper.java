package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToIHTFormEstateValuesCompleted;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Slf4j
@Component
public class OCRFieldIhtFormEstateValuesCompletedMapper {

    @Autowired
    ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;

    @SuppressWarnings({"squid:S2447"})
    @ToIHTFormEstateValuesCompleted
    public Boolean toIhtFormEstateValuesCompleted(ExceptionRecordOCRFields ocrFields) {
        log.info("Beginning mapping for ihtFormEstateValuesCompleted");

        if (isExpectedEstate(ocrFields) && !isEmpty(ocrFields.getIhtEstateGrossValue())
            && !isEmpty(ocrFields.getIhtEstateNetValue())
            && !isEmpty(ocrFields.getIhtEstateNetQualifyingValue())) {
            return Boolean.FALSE;
        } else if (
            "true".equalsIgnoreCase(ocrFields.getIht207Completed())
                || "true".equalsIgnoreCase(ocrFields.getIht400421Completed())
                || "true".equalsIgnoreCase(ocrFields.getIht400Completed())
        ) {
            return exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate(ocrFields.getDeceasedDateOfDeath())
                ? Boolean.TRUE : null;
        }
        return null;
    }

    private Boolean isExpectedEstate(ExceptionRecordOCRFields ocrField) {
        String formVersion = ocrField.getFormVersion();

        return (("2".equals(formVersion) && "True".equalsIgnoreCase(ocrField.getDeceasedDiedOnAfterSwitchDate())
                || ("3".equals(formVersion)) && "True".equalsIgnoreCase(ocrField.getExceptedEstate())));
    }
}