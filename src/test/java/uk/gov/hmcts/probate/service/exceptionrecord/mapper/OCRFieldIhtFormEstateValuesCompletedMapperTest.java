package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class OCRFieldIhtFormEstateValuesCompletedMapperTest {

    private static final String PRE_EE_DECEASED_DATE_OF_DEATH = "01012021";
    private static final String POST_EE_DECEASED_DATE_OF_DEATH = "01012022";

    @Mock
    ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;

    @InjectMocks
    OCRFieldIhtFormEstateValuesCompletedMapper ocrFieldIhtFormEstateValuesCompletedMapper;

    @BeforeEach
    public void setUp() {
        when(exceptedEstateDateOfDeathChecker
            .isOnOrAfterSwitchDate(PRE_EE_DECEASED_DATE_OF_DEATH)).thenReturn(false);
        when(exceptedEstateDateOfDeathChecker
            .isOnOrAfterSwitchDate(POST_EE_DECEASED_DATE_OF_DEATH)).thenReturn(true);
    }

    @Test
    void shouldReturnFalseWhenIhtEstateFieldsAreAllPresent() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .deceasedDateOfDeath(POST_EE_DECEASED_DATE_OF_DEATH)
            .ihtEstateGrossValue("1000000")
            .ihtEstateNetValue("900000")
            .ihtEstateNetQualifyingValue("800000")
            .build();
        assertFalse(ocrFieldIhtFormEstateValuesCompletedMapper.toIhtFormEstateValuesCompleted(ocrFields));
    }

    @Test
    void shouldReturnFalseWhenIhtEstateFieldsAreAllEmpty() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .deceasedDateOfDeath(POST_EE_DECEASED_DATE_OF_DEATH)
            .ihtEstateGrossValue("")
            .ihtEstateNetValue("")
            .ihtEstateNetQualifyingValue("")
            .build();
        assertNull(ocrFieldIhtFormEstateValuesCompletedMapper.toIhtFormEstateValuesCompleted(ocrFields));
    }

    @Test
    void shouldReturnTrueWhenIHT207() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .deceasedDateOfDeath(POST_EE_DECEASED_DATE_OF_DEATH)
            .iht207Completed("true")
            .deceasedDateOfDeath(POST_EE_DECEASED_DATE_OF_DEATH)
            .build();
        assertTrue(ocrFieldIhtFormEstateValuesCompletedMapper.toIhtFormEstateValuesCompleted(ocrFields));
    }

    @Test
    void shouldReturnTrueWhenIHT400421() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .deceasedDateOfDeath(POST_EE_DECEASED_DATE_OF_DEATH)
            .iht400421Completed("true")
            .build();
        assertTrue(ocrFieldIhtFormEstateValuesCompletedMapper.toIhtFormEstateValuesCompleted(ocrFields));
    }

    @Test
    void shouldReturnNullWhenIHT400421PreEEDod() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .deceasedDateOfDeath(PRE_EE_DECEASED_DATE_OF_DEATH)
            .ihtFormEstate("IHT400421")
            .build();
        assertNull(ocrFieldIhtFormEstateValuesCompletedMapper.toIhtFormEstateValuesCompleted(ocrFields));
    }

    @Test
    void shouldReturnNull() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .build();
        assertNull(ocrFieldIhtFormEstateValuesCompletedMapper.toIhtFormEstateValuesCompleted(ocrFields));
    }

}
