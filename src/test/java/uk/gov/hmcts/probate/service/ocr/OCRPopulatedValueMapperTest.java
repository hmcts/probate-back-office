package uk.gov.hmcts.probate.service.ocr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.bulkscan.type.OcrDataField;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OCRPopulatedValueMapperTest {

    private List<OcrDataField> ocrFields;
    private OCRPopulatedValueMapper ocrPopulatedValueMapper = new OCRPopulatedValueMapper();

    @BeforeEach
    public void setup() {
        ocrFields = new ArrayList<>() {
            {
                add(new OcrDataField("deceasedForenames", "John"));
                add(new OcrDataField("deceasedSurname", "Johnson"));
                add(new OcrDataField("deceasedAddress", "Smith"));
                add(new OcrDataField("deceasedDateOfBirth", "1900-01-01"));
                add(new OcrDataField("dateOfDeathType", "diedOn"));
            }
        };
    }

    @Test
    void testFieldsAreAddedSuccessfully() {
        assertTrue(ocrPopulatedValueMapper.ocrPopulatedValueMapper(ocrFields).contains(ocrFields.get(0)));
    }

    @Test
    void testAllPopulatedFieldsAreAdded() {
        assertEquals(5, ocrPopulatedValueMapper.ocrPopulatedValueMapper(ocrFields).size());
    }

//    @Test
//    void testNullFieldValueIsNotAdded() {
//        ocrFields.add(new OcrDataField("test", "test"));
//        assertEquals(5, ocrPopulatedValueMapper.ocrPopulatedValueMapper(ocrFields).size());
//    }
//
//    @Test
//    void testNullObjectsAreNotAdded() {
//        ocrFields.add(new OcrDataField(null, null));
//        assertEquals(5, ocrPopulatedValueMapper.ocrPopulatedValueMapper(ocrFields).size());
//    }
//
//    @Test
//    void testEmptyValueFieldsAreNotAdded() {
//        ocrFields.add(new OcrDataField("test", ""));
//        assertEquals(5, ocrPopulatedValueMapper.ocrPopulatedValueMapper(ocrFields).size());
//    }
}
