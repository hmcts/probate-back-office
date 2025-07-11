package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToIHTGrossValue;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Component
public class OCRFieldIhtGrossValueMapper {
    private static final String TRUE = "true";
    private static final String ERROR_MESSAGE = "No ihtGrossValue mapped";
    private static final OCRMappingException ocrMappingException = new OCRMappingException(ERROR_MESSAGE,
                                                                        Collections.singletonList(ERROR_MESSAGE));

    @ToIHTGrossValue
    public Long toIHTGrossValue(ExceptionRecordOCRFields ocrFields) {
        if ("3".equals(ocrFields.getFormVersion())) {
            Long ihtGrossValue = getGrossValueVersion3(ocrFields);
            return Optional.ofNullable(ihtGrossValue)
                    .orElseThrow(() -> ocrMappingException);
        } else {
            return OCRFieldIhtMoneyMapper.poundsToPennies("IhtGrossValue", ocrFields.getIhtGrossValue());
        }
    }

    private Long getGrossValueVersion3(ExceptionRecordOCRFields ocrFields) {
        if (TRUE.equalsIgnoreCase(ocrFields.getDeceasedDiedOnAfterSwitchDate())) {
            if (TRUE.equalsIgnoreCase(ocrFields.getIht400421Completed())) {
                return OCRFieldIhtMoneyMapper.poundsToPennies("Iht421grossValue", ocrFields.getIht421grossValue());
            } else if (TRUE.equalsIgnoreCase(ocrFields.getIht207Completed())) {
                return OCRFieldIhtMoneyMapper.poundsToPennies("Iht207grossValue", ocrFields.getIht207grossValue());
            } else if (TRUE.equalsIgnoreCase(ocrFields.getIht400Completed())) {
                return getIht400GrossValue(ocrFields);
            } else if (TRUE.equalsIgnoreCase(ocrFields.getExceptedEstate())) {
                return OCRFieldIhtMoneyMapper.poundsToPennies("IhtGrossValueExceptedEstate",
                        ocrFields.getIhtGrossValueExceptedEstate());
            }
        } else {
            if (TRUE.equalsIgnoreCase(ocrFields.getIht205Completed())) {
                return OCRFieldIhtMoneyMapper.poundsToPennies("IhtGrossValue205", ocrFields.getIhtGrossValue205());
            } else if (TRUE.equalsIgnoreCase(ocrFields.getIht400Completed())) {
                return getIht400GrossValue(ocrFields);
            } else if (TRUE.equalsIgnoreCase(ocrFields.getIht400421Completed())) {
                return OCRFieldIhtMoneyMapper.poundsToPennies("Iht421grossValue", ocrFields.getIht421grossValue());
            } else if (TRUE.equalsIgnoreCase(ocrFields.getIht207Completed())) {
                return OCRFieldIhtMoneyMapper.poundsToPennies("Iht207grossValue", ocrFields.getIht207grossValue());
            }
        }
        return null;
    }

    private Long getIht400GrossValue(ExceptionRecordOCRFields ocrFields) {
        if (TRUE.equalsIgnoreCase(ocrFields.getIht400process())) {
            return OCRFieldIhtMoneyMapper.poundsToPennies("ProbateGrossValueIht400",
                    ocrFields.getProbateGrossValueIht400());
        } else {
            return OCRFieldIhtMoneyMapper.poundsToPennies("Iht421grossValue", ocrFields.getIht421grossValue());
        }
    }
}
