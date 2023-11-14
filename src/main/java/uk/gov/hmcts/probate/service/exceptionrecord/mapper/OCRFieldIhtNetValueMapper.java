package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToIHTNetValue;

import java.util.ArrayList;

@Slf4j
@Component
public class OCRFieldIhtNetValueMapper {
    private static final String TRUE = "true";

    @ToIHTNetValue
    public Long toIHTNetValue(ExceptionRecordOCRFields ocrFields) {
        if ("3".equals(ocrFields.getFormVersion())) {
            if (TRUE.equalsIgnoreCase(ocrFields.getDeceasedDiedOnAfterSwitchDate())) {
                if (TRUE.equalsIgnoreCase(ocrFields.getIht400421Completed())) {
                    return OCRFieldIhtMoneyMapper.poundsToPennies("Iht421netValue", ocrFields.getIht421netValue());
                } else if (TRUE.equalsIgnoreCase(ocrFields.getIht207Completed())) {
                    return OCRFieldIhtMoneyMapper.poundsToPennies("Iht207netValue", ocrFields.getIht207netValue());
                } else if (TRUE.equalsIgnoreCase(ocrFields.getIht400Completed())) {
                    return OCRFieldIhtMoneyMapper.poundsToPennies("ProbateNetValueIht400",
                            ocrFields.getProbateNetValueIht400());
                } else if (TRUE.equalsIgnoreCase(ocrFields.getExceptedEstate())) {
                    return OCRFieldIhtMoneyMapper.poundsToPennies("IhtNetValueExceptedEstate",
                            ocrFields.getIhtNetValueExceptedEstate());
                }
            } else {
                if (TRUE.equalsIgnoreCase(ocrFields.getIht205Completed())) {
                    return OCRFieldIhtMoneyMapper.poundsToPennies("IhtNetValue205", ocrFields.getIhtNetValue205());
                } else if (TRUE.equalsIgnoreCase(ocrFields.getIht400Completed())) {
                    return OCRFieldIhtMoneyMapper.poundsToPennies("ProbateNetValueIht400",
                            ocrFields.getProbateNetValueIht400());
                } else if (TRUE.equalsIgnoreCase(ocrFields.getIht400421Completed())) {
                    return OCRFieldIhtMoneyMapper.poundsToPennies("Iht421netValue", ocrFields.getIht421netValue());
                } else if (TRUE.equalsIgnoreCase(ocrFields.getIht207Completed())) {
                    return OCRFieldIhtMoneyMapper.poundsToPennies("Iht207netValue", ocrFields.getIht207netValue());
                }
            }
            ArrayList<String> warnings = new ArrayList<>();
            String errorMessage = "No ihtNetValue mapped";
            log.error(errorMessage);
            warnings.add(errorMessage);
            throw new OCRMappingException(errorMessage, warnings);
        } else {
            return OCRFieldIhtMoneyMapper.poundsToPennies("IhtNetValue", ocrFields.getIhtNetValue());
        }
    }
}
