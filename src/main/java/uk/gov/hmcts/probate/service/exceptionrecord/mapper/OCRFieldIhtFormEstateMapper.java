package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToIHTFormEstate;
import uk.gov.hmcts.probate.service.exceptionrecord.utils.ExceptedEstateDateOfDeathChecker;
import uk.gov.hmcts.reform.probate.model.IhtFormEstate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OCRFieldIhtFormEstateMapper {

    private static final String FORM_IHT207 = "IHT207";
    private static final String FORM_IHT400421 = "IHT400421";

    @Autowired
    ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;

    @ToIHTFormEstate
    public IhtFormEstate ihtFormEstate(ExceptionRecordOCRFields ocrFields) {
        String ihtFormEstate = ocrFields.getIhtFormEstate();
        log.info("Beginning mapping for IHT Form Type value: {}", ihtFormEstate);

        if (ihtFormEstate == null || ihtFormEstate.isEmpty()
            || !exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate(ocrFields.getDeceasedDateOfDeath())) {
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
