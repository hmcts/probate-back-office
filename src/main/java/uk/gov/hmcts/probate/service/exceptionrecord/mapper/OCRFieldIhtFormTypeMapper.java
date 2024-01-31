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
        log.info("Beginning mapping for IHT Form Type");

        if ("3".equals(ocrFields.getFormVersion())) {
            return processVersion3(ocrFields);
        } else if ("2".equals(ocrFields.getFormVersion())) {
            return processVersion2(ocrFields);
        } else {
            return processVersion1(ocrFields);
        }
    }

    private IhtFormType processVersion3(ExceptionRecordOCRFields ocrFields) {
        boolean diedAfterSwitch = TRUE.equalsIgnoreCase(ocrFields.getDeceasedDiedOnAfterSwitchDate());
        if (diedAfterSwitch) {
            return null;
        }
        return getIhtFormType(ocrFields);
    }

    private IhtFormType processVersion2(ExceptionRecordOCRFields ocrFields) {
        boolean diedAfterSwitch = exceptedEstateDateOfDeathChecker
                .isOnOrAfterSwitchDate(ocrFields.getDeceasedDateOfDeath());
        if (diedAfterSwitch) {
            return null;
        }
        return getIhtFormType(ocrFields);
    }

    private IhtFormType processVersion1(ExceptionRecordOCRFields ocrFields) {
        String ihtFormId = ocrFields.getIhtFormId();
        if (null == ihtFormId || ihtFormId.isEmpty()) {
            return null;
        }

        return switch (ihtFormId.toUpperCase().trim()) {
            case FORM_IHT205 -> IhtFormType.optionIHT205;
            case FORM_IHT207 -> IhtFormType.optionIHT207;
            case FORM_IHT400421, FORM_IHT421, FORM_IHT400 -> IhtFormType.optionIHT400421;
            default ->
                    throw new OCRMappingException("ihtFormId: IHT205, IHT207, or IHT400421 expected but got '"
                            + ihtFormId + "'");
        };
    }

    private IhtFormType getIhtFormType(ExceptionRecordOCRFields ocrFields) {
        if (TRUE.equalsIgnoreCase(ocrFields.getIht400421Completed())) {
            return IhtFormType.optionIHT400421;
        } else if (TRUE.equalsIgnoreCase(ocrFields.getIht207Completed())) {
            return IhtFormType.optionIHT207;
        } else if (TRUE.equalsIgnoreCase(ocrFields.getIht400Completed())) {
            return IhtFormType.optionIHT400;
        } else if (TRUE.equalsIgnoreCase(ocrFields.getIht205Completed())
                || FALSE.equalsIgnoreCase(ocrFields.getIht205completedOnline())) {
            return IhtFormType.optionIHT205;
        }
        return null;
    }
}
