package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToIHTFormCompletedOnline;

@Slf4j
@Component
public class OCRFieldIhtFormCompletedOnlineMapper {

    @Autowired
    OCRFieldYesOrNoMapper ocrFieldYesOrNoMapper;

    @ToIHTFormCompletedOnline
    public Boolean ihtFormCompletedOnline(ExceptionRecordOCRFields ocrFields) {
        if ("2".equals(ocrFields.getFormVersion())) {
            if ("true".equalsIgnoreCase(ocrFields.getIht205completedOnline())) {
                return true;
            } else {
                return null;
            }
        } else {
            return ocrFieldYesOrNoMapper.toYesOrNo(ocrFields.getIhtFormCompletedOnline());
        }
    }
}

