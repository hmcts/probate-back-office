package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToDeceasedHadLateSpouseOrCivilPartner;

@Slf4j
@Component
public class OCRFieldDeceasedHadLateSpouseOrCivilPartnerMapper {

    @Autowired
    ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;

    @SuppressWarnings({"squid:S2447"})
    @ToDeceasedHadLateSpouseOrCivilPartner
    public Boolean decasedHadLateSpouseOrCivilPartner(ExceptionRecordOCRFields ocrFields) {
        if ("2".equals(ocrFields.getFormVersion())
            && exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate(ocrFields.getDeceasedDateOfDeath())
            && "false".equalsIgnoreCase(ocrFields.getIht207Completed())
            && "false".equalsIgnoreCase(ocrFields.getIht400421Completed())) {
            return "widowed".equalsIgnoreCase(ocrFields.getDeceasedMartialStatus());
        } else {
            return null;
        }
    }
}
