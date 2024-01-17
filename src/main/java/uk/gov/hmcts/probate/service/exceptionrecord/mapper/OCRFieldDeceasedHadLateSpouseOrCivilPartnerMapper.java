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

    private static final String FALSE = "false";
    private static final String WIDOWED = "widowed";

    @SuppressWarnings({"squid:S2447"})
    @ToDeceasedHadLateSpouseOrCivilPartner
    public Boolean deceasedHadLateSpouseOrCivilPartner(ExceptionRecordOCRFields ocrFields) {
        if (null == ocrFields.getFormVersion()) {
            return null;
        }
        return switch (ocrFields.getFormVersion()) {
            case "2" -> exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate(ocrFields.getDeceasedDateOfDeath())
                    && FALSE.equalsIgnoreCase(ocrFields.getIht207Completed())
                    && FALSE.equalsIgnoreCase(ocrFields.getIht400421Completed())
                    ? WIDOWED.equalsIgnoreCase(ocrFields.getDeceasedMartialStatus()) : null;
            case "3" -> exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate(ocrFields.getDeceasedDateOfDeath())
                    && FALSE.equalsIgnoreCase(ocrFields.getIht207Completed())
                    && FALSE.equalsIgnoreCase(ocrFields.getIht400421Completed())
                    && FALSE.equalsIgnoreCase(ocrFields.getIht400Completed())
                    ? WIDOWED.equalsIgnoreCase(ocrFields.getDeceasedMartialStatus()) : null;
            default -> null;
        };
    }
}
