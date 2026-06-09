package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToIHTFormEstate;
import uk.gov.hmcts.reform.probate.model.IhtFormEstate;

@Slf4j
@Component
public class OCRFieldIhtFormEstateMapper {

    private static final String TRUE = "true";

    @Autowired
    ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;

    @ToIHTFormEstate
    public IhtFormEstate ihtFormEstate(ExceptionRecordOCRFields ocrFields) {
        if ("3".equals(ocrFields.getFormVersion())
                && TRUE.equalsIgnoreCase(ocrFields.getDeceasedDiedOnAfterSwitchDate())) {
            return getIhtFormEstateOption(ocrFields);
        } else if ("2".equals(ocrFields.getFormVersion())
            && exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate(ocrFields.getDeceasedDateOfDeath())) {
            return getIhtFormEstateOption(ocrFields);
        }
        return null;
    }

    private IhtFormEstate getIhtFormEstateOption(ExceptionRecordOCRFields ocrFields) {
        if (TRUE.equalsIgnoreCase(ocrFields.getIht400421Completed())) {
            return IhtFormEstate.optionIHT400421;
        } else if (TRUE.equalsIgnoreCase(ocrFields.getIht207Completed())) {
            return IhtFormEstate.optionIHT207;
        } else if (TRUE.equalsIgnoreCase(ocrFields.getIht400Completed())) {
            return IhtFormEstate.optionIHT400;
        }
        return null;
    }
}
