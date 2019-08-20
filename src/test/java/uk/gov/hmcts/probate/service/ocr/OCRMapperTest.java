package uk.gov.hmcts.probate.service.ocr;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.probate.model.ocr.OCRField;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OCRMapperTest {

    private List<OCRField> ocrFields = new ArrayList<>();
    private OCRMapper ocrMapper = new OCRMapper();

    @Before
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
    public void testFieldsAreAddedSuccessfully() {
        assertTrue(ocrMapper.ocrMapper(ocrFields).contains(ocrFields.get(0)));
    }

    @Test
    public void testAllPopulatedFieldsAreAdded() {
        assertEquals(5, ocrMapper.ocrMapper(ocrFields).size());
    }

    @Test
    public void testNullFieldValueIsNotAdded() {
        ocrFields.add(OCRField.builder().name("test").description("test").build());
        assertEquals(5, ocrMapper.ocrMapper(ocrFields).size());
    }

    @Test
    public void testNullObjectsAreNotAdded() {
        ocrFields.add(OCRField.builder().build());
        assertEquals(5, ocrMapper.ocrMapper(ocrFields).size());
    }

    @Test
    public void testEmptyValueFieldsAreNotAdded() {
        ocrFields.add(OCRField.builder().name("test").value("").description("test").build());
        assertEquals(5, ocrMapper.ocrMapper(ocrFields).size());
    }
}
