package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToDeceasedHadLateSpouseOrCivilPartner;

@Slf4j
@Component
public class OCRFieldDeceasedHadLateSpouseOrCivilPartnerMapper {

    @ToDeceasedHadLateSpouseOrCivilPartner
    public Boolean decasedHadLateSpouseOrCivilPartner(ExceptionRecordOCRFields ocrFields) {
        if ("2".equals(ocrFields.getFormVersion())) {
            return "widowed".equalsIgnoreCase(ocrFields.getDeceasedMartialStatus());
        } else {
            return null;
        }
    }
}
