package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OCRFieldIhtFormEstateValuesCompletedMapperTest {

    private static final String PRE_EE_DECEASED_DATE_OF_DEATH = "01012021";
    private static final String POST_EE_DECEASED_DATE_OF_DEATH = "01012022";
    
    OCRFieldIhtFormEstateValuesCompletedMapper ocrFieldIhtFormEstateValuesCompletedMapper;
    
    @Before
    public void setUp() throws Exception {
        ocrFieldIhtFormEstateValuesCompletedMapper = new OCRFieldIhtFormEstateValuesCompletedMapper();
        ReflectionTestUtils.setField(ocrFieldIhtFormEstateValuesCompletedMapper, "ihtEstateSwitchDate", "2022-01-01");
    }

    @Test
    public void shouldReturnFalseWhenIhtEstateFieldsAreAllPresent() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .deceasedDateOfDeath(POST_EE_DECEASED_DATE_OF_DEATH)
            .ihtEstateGrossValue("1000000")
            .ihtEstateNetValue("900000")
            .ihtEstateNetQualifyingValue("800000")
            .build();
        assertFalse(ocrFieldIhtFormEstateValuesCompletedMapper.toIhtFormEstateValuesCompleted(ocrFields));
    }

    @Test
    public void shouldReturnTrueWhenIHT207() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .deceasedDateOfDeath(POST_EE_DECEASED_DATE_OF_DEATH)
            .ihtFormEstate("IHT207")
            .deceasedDateOfDeath(POST_EE_DECEASED_DATE_OF_DEATH)
            .build();
        assertTrue(ocrFieldIhtFormEstateValuesCompletedMapper.toIhtFormEstateValuesCompleted(ocrFields));
    }

    @Test
    public void shouldReturnTrueWhenIHT400421() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .deceasedDateOfDeath(POST_EE_DECEASED_DATE_OF_DEATH)
            .ihtFormEstate("IHT400421")
            .build();
        assertTrue(ocrFieldIhtFormEstateValuesCompletedMapper.toIhtFormEstateValuesCompleted(ocrFields));
    }

    @Test
    public void shouldReturnNullWhenIHT400421PreEEDod() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .deceasedDateOfDeath(PRE_EE_DECEASED_DATE_OF_DEATH)
            .ihtFormEstate("IHT400421")
            .build();
        assertNull(ocrFieldIhtFormEstateValuesCompletedMapper.toIhtFormEstateValuesCompleted(ocrFields));
    }

    @Test
    public void shouldReturnNull() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .build();
        assertNull(ocrFieldIhtFormEstateValuesCompletedMapper.toIhtFormEstateValuesCompleted(ocrFields));
    }

}
