package uk.gov.hmcts.probate.service.ocr.pa1p;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.String.format;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_FIELD_NOT_FOUND_LOG;
import static uk.gov.hmcts.probate.service.ocr.CCDMandatoryFieldKeys.MANDATORY_FIELD_WARNING_STIRNG;

@Slf4j
@Service
@RequiredArgsConstructor
public class PA1PCitizenMandatoryFieldsValidator {

    private static final DefaultKeyValue IHT_207_COMPLETED = new DefaultKeyValue("iht207completed",
        "Did you complete an IHT400 and IHT421 form?");
    private static final DefaultKeyValue IHT_205_COMPLETED_ONLINE = new DefaultKeyValue("iht205completedOnline",
        "Did you complete the IHT205 online with HMRC?");
    private static final DefaultKeyValue DIED_AFTER_SWITCH_DATE = new DefaultKeyValue("deceasedDiedOnAfterSwitchDate",
        "Did the deceased die on or after 1 January 2022?");

    public void addWarnings(Map<String, String> ocrFieldValues, List<String> warnings) {
        Stream.of(GORCitizenMandatoryFields.values()).filter(field -> field.isVersion1()).forEach(field -> {
            log.info("Checking {} against ocr fields", field.getKey());
            if (!ocrFieldValues.containsKey(field.getKey())) {
                log.warn(MANDATORY_FIELD_NOT_FOUND_LOG, field.getKey());
                warnings.add(format(MANDATORY_FIELD_WARNING_STIRNG, field.getValue(), field.getKey()));
            }
        });
    }

}
