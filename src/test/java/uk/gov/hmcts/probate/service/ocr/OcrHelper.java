package uk.gov.hmcts.probate.service.ocr;

import uk.gov.hmcts.bulkscan.type.FormData;
import uk.gov.hmcts.bulkscan.type.OcrDataField;

import java.util.ArrayList;
import java.util.Arrays;

public class OcrHelper {

    private OcrHelper() {
    }

    public static final FormData expectedOCRData = new FormData(new ArrayList<OcrDataField>(
            Arrays.asList(
                    new OcrDataField("deceasedSurname", "Smith"),
                    new OcrDataField("deceasedAddressLine1", "123 Alphabet Street")
            )
    ));
}
