package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToIHTGrossValue;

import java.util.ArrayList;

@Slf4j
@Component
public class OCRFieldIhtGrossValueMapper {
    private static final String TRUE = "true";

    @ToIHTGrossValue
    public Long toIHTGrossValue(ExceptionRecordOCRFields ocrFields) {
        if ("3".equals(ocrFields.getFormVersion())) {
            if (TRUE.equalsIgnoreCase(ocrFields.getDeceasedDiedOnAfterSwitchDate())) {
                if (TRUE.equalsIgnoreCase(ocrFields.getIht400421Completed())) {
                    return OCRFieldIhtMoneyMapper.poundsToPennies("Iht421grossValue", ocrFields.getIht421grossValue());
                } else if (TRUE.equalsIgnoreCase(ocrFields.getIht207Completed())) {
                    return OCRFieldIhtMoneyMapper.poundsToPennies("Iht207grossValue", ocrFields.getIht207grossValue());
                } else if (TRUE.equalsIgnoreCase(ocrFields.getIht400Completed())) {
                    return OCRFieldIhtMoneyMapper.poundsToPennies("ProbateGrossValueIht400",
                            ocrFields.getProbateGrossValueIht400());
                } else if (TRUE.equalsIgnoreCase(ocrFields.getExceptedEstate())) {
                    return OCRFieldIhtMoneyMapper.poundsToPennies("IhtGrossValueExceptedEstate",
                            ocrFields.getIhtGrossValueExceptedEstate());
                }
            } else {
                if (TRUE.equalsIgnoreCase(ocrFields.getIht205Completed())) {
                    return OCRFieldIhtMoneyMapper.poundsToPennies("IhtGrossValue205", ocrFields.getIhtGrossValue205());
                } else if (TRUE.equalsIgnoreCase(ocrFields.getIht400Completed())) {
                    return OCRFieldIhtMoneyMapper.poundsToPennies("ProbateGrossValueIht400",
                            ocrFields.getProbateGrossValueIht400());
                } else if (TRUE.equalsIgnoreCase(ocrFields.getIht400421Completed())) {
                    return OCRFieldIhtMoneyMapper.poundsToPennies("Iht421grossValue", ocrFields.getIht421grossValue());
                } else if (TRUE.equalsIgnoreCase(ocrFields.getIht207Completed())) {
                    return OCRFieldIhtMoneyMapper.poundsToPennies("Iht207grossValue", ocrFields.getIht207grossValue());
                }
            }
            ArrayList<String> warnings = new ArrayList<>();
            String errorMessage = "No ihtGrossValue mapped";
            log.error(errorMessage);
            warnings.add(errorMessage);
            throw new OCRMappingException(errorMessage, warnings);
        } else {
            return OCRFieldIhtMoneyMapper.poundsToPennies("IhtGrossValue", ocrFields.getIhtGrossValue());
        }
    }
}
