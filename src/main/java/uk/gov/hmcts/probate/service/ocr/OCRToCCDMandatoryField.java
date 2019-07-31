package uk.gov.hmcts.probate.service.ocr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.ocr.MandatoryFields;
import uk.gov.hmcts.probate.model.ocr.OCRField;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class OCRToCCDMandatoryField {

    //private List<String> ccdMandatoryFields() {
    //    List<String> MandatoryFields = new ArrayList<>();
    //    MandatoryFields.add("primaryApplicantForenames");
    //    MandatoryFields.add("primaryApplicantSurname");
    //    MandatoryFields.add("primaryApplicantAddress.addressLine1");
    //    MandatoryFields.add("deceasedForenames");
    //    MandatoryFields.add("deceasedSurname");
    //    MandatoryFields.add("deceasedAddress.addressLine1");
    //    MandatoryFields.add("deceasedDateOfBirth");
    //    MandatoryFields.add("deceasedDateOfDeath");
    //
    //    return MandatoryFields;
    //}

    //public List<String> ocrToCCDMandatoryFields(List<OCRField> ocrFields) {
    //    List<String> missingFields = new ArrayList<>();
    //    //List<String> ccdfields = ccdMandatoryFields();
    //    List<String> ocrFieldNames = new ArrayList<>();
    //    ocrFields.forEach(ocrField -> {
    //        ocrFieldNames.add(ocrField.getName());
    //    });
    //    for (String field : ccdfields) {
    //        log.info("Checking {} against ocr fields", field);
    //        if (!ocrFieldNames.contains(field)) {
    //            log.warn("{} was not found in ocr fields", field);
    //            missingFields.add(field);
    //        }
    //    }
    //    return missingFields;
    //}

    public List<String> ocrToCCDMandatoryFields(List<OCRField> ocrFields) {
        List<String> descriptions = new ArrayList<>();
        List<String> ocrFieldNames = new ArrayList<>();
        ocrFields.forEach(ocrField -> {
            ocrFieldNames.add(ocrField.getName());
        });
        Stream.of(MandatoryFields.values()).forEach(field -> {
            log.info("Checking {} against ocr fields", field.getKey());
            if (!ocrFieldNames.contains(field.getKey())) {
                log.warn("{} was not found in ocr fields", field.getKey());
                descriptions.add(field.getValue());
            }
        });
        return descriptions;
    }
}
