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

    @SuppressWarnings({"squid:S2447"})
    @ToIHTFormCompletedOnline
    public Boolean ihtFormCompletedOnline(ExceptionRecordOCRFields ocrFields) {
        if (null == ocrFields.getFormVersion()) {
            return null;
        }
        return switch (ocrFields.getFormVersion()) {
            case "3" -> null;
            case "2" -> "true".equalsIgnoreCase(ocrFields.getIht205completedOnline()) ? true : null;
            default -> ocrFieldYesOrNoMapper.toYesOrNo("ihtFormCompletedOnline", ocrFields.getIhtFormCompletedOnline());
        };
    }
}

