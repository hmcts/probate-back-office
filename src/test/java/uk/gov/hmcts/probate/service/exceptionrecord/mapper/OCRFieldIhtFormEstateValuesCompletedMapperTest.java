package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.exceptionrecord.utils.ExceptedEstateDateOfDeathChecker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class OCRFieldIhtFormEstateValuesCompletedMapperTest {

    private static final String PRE_EE_DECEASED_DATE_OF_DEATH = "01012021";
    private static final String POST_EE_DECEASED_DATE_OF_DEATH = "01012022";

    @Mock
    ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;

    @InjectMocks
    OCRFieldIhtFormEstateValuesCompletedMapper ocrFieldIhtFormEstateValuesCompletedMapper;

    @Before
    public void setUp() {
        when(exceptedEstateDateOfDeathChecker
            .isOnOrAfterSwitchDate(eq(PRE_EE_DECEASED_DATE_OF_DEATH))).thenReturn(false);
        when(exceptedEstateDateOfDeathChecker
            .isOnOrAfterSwitchDate(eq(POST_EE_DECEASED_DATE_OF_DEATH))).thenReturn(true);
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
