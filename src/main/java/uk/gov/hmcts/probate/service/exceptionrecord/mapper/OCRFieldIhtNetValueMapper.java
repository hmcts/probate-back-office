package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToIHTNetValue;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Component
public class OCRFieldIhtNetValueMapper {
    private static final String TRUE = "true";
    private static final String ERROR_MESSAGE = "No ihtNetValue mapped";
    private static final OCRMappingException ocrMappingException = new OCRMappingException(ERROR_MESSAGE,
            Collections.singletonList(ERROR_MESSAGE));

    @ToIHTNetValue
    public Long toIHTNetValue(ExceptionRecordOCRFields ocrFields) {
        if ("3".equals(ocrFields.getFormVersion())) {
            Long ihtNetValue = mapNetValueVersion3(ocrFields);
            return Optional.ofNullable(ihtNetValue)
                    .orElseThrow(() -> ocrMappingException);
        } else {
            return OCRFieldIhtMoneyMapper.poundsToPennies("IhtNetValue", ocrFields.getIhtNetValue());
        }
    }

    private Long mapNetValueVersion3(ExceptionRecordOCRFields ocrFields) {
        if (TRUE.equalsIgnoreCase(ocrFields.getDeceasedDiedOnAfterSwitchDate())) {
            if (TRUE.equalsIgnoreCase(ocrFields.getIht400421Completed())) {
                return OCRFieldIhtMoneyMapper.poundsToPennies("Iht421netValue", ocrFields.getIht421netValue());
            } else if (TRUE.equalsIgnoreCase(ocrFields.getIht207Completed())) {
                return OCRFieldIhtMoneyMapper.poundsToPennies("Iht207netValue", ocrFields.getIht207netValue());
            } else if (TRUE.equalsIgnoreCase(ocrFields.getIht400Completed())) {
                return getIht400NetValue(ocrFields);
            } else if (TRUE.equalsIgnoreCase(ocrFields.getExceptedEstate())) {
                return OCRFieldIhtMoneyMapper.poundsToPennies("IhtNetValueExceptedEstate",
                        ocrFields.getIhtNetValueExceptedEstate());
            }
        } else {
            if (TRUE.equalsIgnoreCase(ocrFields.getIht205Completed())) {
                return OCRFieldIhtMoneyMapper.poundsToPennies("IhtNetValue205", ocrFields.getIhtNetValue205());
            } else if (TRUE.equalsIgnoreCase(ocrFields.getIht400Completed())) {
                return getIht400NetValue(ocrFields);
            } else if (TRUE.equalsIgnoreCase(ocrFields.getIht400421Completed())) {
                return OCRFieldIhtMoneyMapper.poundsToPennies("Iht421netValue", ocrFields.getIht421netValue());
            } else if (TRUE.equalsIgnoreCase(ocrFields.getIht207Completed())) {
                return OCRFieldIhtMoneyMapper.poundsToPennies("Iht207netValue", ocrFields.getIht207netValue());
            }
        }
        return null;
    }


    private Long getIht400NetValue(ExceptionRecordOCRFields ocrFields) {
        if (TRUE.equalsIgnoreCase(ocrFields.getIht400process())) {
            return OCRFieldIhtMoneyMapper.poundsToPennies("ProbateNetValueIht400",
                    ocrFields.getProbateNetValueIht400());
        } else {
            return OCRFieldIhtMoneyMapper.poundsToPennies("Iht421netValue", ocrFields.getIht421netValue());
        }
    }
}
