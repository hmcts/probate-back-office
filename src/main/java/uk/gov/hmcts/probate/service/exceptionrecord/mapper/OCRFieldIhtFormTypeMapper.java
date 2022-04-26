package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToIHTFormId;
import uk.gov.hmcts.reform.probate.model.IhtFormType;

@Slf4j
@Component
public class OCRFieldIhtFormTypeMapper {

    private static final String FORM_IHT205 = "IHT205";
    private static final String FORM_IHT207 = "IHT207";
    private static final String FORM_IHT400421 = "IHT400421";
    private static final String FORM_IHT421 = "IHT421";
    private static final String FORM_IHT400 = "IHT400";
    private static final String TRUE = "true";
    private static final String FALSE = "false";

    @Autowired
    ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;

    @ToIHTFormId
    public IhtFormType ihtFormType(ExceptionRecordOCRFields ocrFields) {
        String ihtFormId = ocrFields.getIhtFormId();
        log.info("Beginning mapping for IHT Form Type value: {}", ihtFormId);
        if ("2".equals(ocrFields.getFormVersion())) {
            if (!exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate(ocrFields.getDeceasedDateOfDeath())) {
                if (TRUE.equalsIgnoreCase(ocrFields.getIht400421Completed())) {
                    return IhtFormType.optionIHT400421;
                } else if (TRUE.equalsIgnoreCase(ocrFields.getIht207Completed())) {
                    return IhtFormType.optionIHT207;
                } else if (FALSE.equalsIgnoreCase(ocrFields.getIht205completedOnline())) {
                    return IhtFormType.optionIHT205;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            if (ihtFormId == null || ihtFormId.isEmpty()) {
                return null;
            }
            switch (ihtFormId.toUpperCase().trim()) {
                case FORM_IHT205:
                    return IhtFormType.optionIHT205;
                case FORM_IHT207:
                    return IhtFormType.optionIHT207;
                case FORM_IHT400421:
                    return IhtFormType.optionIHT400421;
                case FORM_IHT421:
                    return IhtFormType.optionIHT400421;
                case FORM_IHT400:
                    return IhtFormType.optionIHT400421;
                default:
                    String errorMessage = "Form type IHT205, IHT207 or IHT400421 expected but got '" + ihtFormId + "'";
                    log.error(errorMessage);
                    throw new OCRMappingException(errorMessage);
            }
        }
    }
}
