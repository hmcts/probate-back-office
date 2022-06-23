package uk.gov.hmcts.probate.service.ocr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.model.ocr.OCRField;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OCRPopulatedValueMapperTest {

    private List<OCRField> ocrFields = new ArrayList<>();
    private OCRPopulatedValueMapper ocrPopulatedValueMapper = new OCRPopulatedValueMapper();

    @BeforeEach
    public void setup() {
        OCRField field1 = OCRField.builder()
                .name("deceasedForenames")
                .value("John")
                .description("Deceased forename").build();
        OCRField field2 = OCRField.builder()
                .name("deceasedSurname")
                .value("Johnson")
                .description("Deceased surname").build();
        OCRField field3 = OCRField.builder()
                .name("deceasedAddress")
                .value("Smith")
                .description("Deceased address").build();
        OCRField field4 = OCRField.builder()
                .name("deceasedDateOfBirth")
                .value("1900-01-01")
                .description("Deceased DOB").build();
        OCRField field5 = OCRField.builder()
                .name("dateOfDeathType")
                .value("diedOn")
                .description("DOD type").build();
        ocrFields.add(field1);
        ocrFields.add(field2);
        ocrFields.add(field3);
        ocrFields.add(field4);
        ocrFields.add(field5);
    }

    @Test
    void testFieldsAreAddedSuccessfully() {
        assertTrue(ocrPopulatedValueMapper.ocrPopulatedValueMapper(ocrFields).contains(ocrFields.get(0)));
    }

    @Test
    void testAllPopulatedFieldsAreAdded() {
        assertEquals(5, ocrPopulatedValueMapper.ocrPopulatedValueMapper(ocrFields).size());
    }

    @Test
    void testNullFieldValueIsNotAdded() {
        ocrFields.add(OCRField.builder().name("test").description("test").build());
        assertEquals(5, ocrPopulatedValueMapper.ocrPopulatedValueMapper(ocrFields).size());
    }

    @Test
    void testNullObjectsAreNotAdded() {
        ocrFields.add(OCRField.builder().build());
        assertEquals(5, ocrPopulatedValueMapper.ocrPopulatedValueMapper(ocrFields).size());
    }

    @Test
    void testEmptyValueFieldsAreNotAdded() {
        ocrFields.add(OCRField.builder().name("test").value("").description("test").build());
        assertEquals(5, ocrPopulatedValueMapper.ocrPopulatedValueMapper(ocrFields).size());
    }
}
