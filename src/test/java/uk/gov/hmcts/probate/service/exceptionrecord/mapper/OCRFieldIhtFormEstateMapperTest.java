package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;
import uk.gov.hmcts.reform.probate.model.IhtFormEstate;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
public class OCRFieldIhtFormEstateMapperTest {
    
    @Mock
    ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;

    @InjectMocks
    OCRFieldIhtFormEstateMapper ocrFieldIhtFormEstateMapper = new OCRFieldIhtFormEstateMapper();

    private static final String IHT207_FORM = "IHT207";
    private static final String IHT400421_FORM = "IHT400421";
    private static final String UNKNOWN_FORM = "UNKOWNFORM";
    private static final String PRE_EE_DECEASED_DATE_OF_DEATH = "01012021";
    private static final String POST_EE_DECEASED_DATE_OF_DEATH = "01012022";
    
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() {
        when(exceptedEstateDateOfDeathChecker
            .isOnOrAfterSwitchDate(eq(PRE_EE_DECEASED_DATE_OF_DEATH))).thenReturn(false);
        when(exceptedEstateDateOfDeathChecker
            .isOnOrAfterSwitchDate(eq(POST_EE_DECEASED_DATE_OF_DEATH))).thenReturn(true);
    }
    
    @Test
    public void testCorrectFormTypeIHT207() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .ihtFormEstate(IHT207_FORM)
            .deceasedDateOfDeath(POST_EE_DECEASED_DATE_OF_DEATH)
            .build();
        IhtFormEstate response = ocrFieldIhtFormEstateMapper.ihtFormEstate(ocrFields);
        assertEquals(IhtFormEstate.optionIHT207, response);
    }

    @Test
    public void testCorrectFormTypeIHT400421() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .ihtFormEstate(IHT400421_FORM)
            .deceasedDateOfDeath(POST_EE_DECEASED_DATE_OF_DEATH)
            .build();        
        IhtFormEstate response = ocrFieldIhtFormEstateMapper.ihtFormEstate(ocrFields);
        assertEquals(IhtFormEstate.optionIHT400421, response);
    }

    @Test(expected = OCRMappingException.class)
    public void testExceptionForUnknownForm() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .ihtFormEstate(UNKNOWN_FORM)
            .deceasedDateOfDeath(POST_EE_DECEASED_DATE_OF_DEATH)
            .build();
        ocrFieldIhtFormEstateMapper.ihtFormEstate(ocrFields);
    }
    
    @Test
    public void shouldReturnNullWhenihtFormEstateIsNull() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .deceasedDateOfDeath(POST_EE_DECEASED_DATE_OF_DEATH)
            .build();
        IhtFormEstate response = ocrFieldIhtFormEstateMapper.ihtFormEstate(ocrFields);
        assertNull(response);
    }
    
    @Test
    public void shouldReturnNullWhenihtFormEstateIsEmptyString() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .deceasedDateOfDeath(POST_EE_DECEASED_DATE_OF_DEATH)
            .ihtFormEstate("")
            .build();
        IhtFormEstate response = ocrFieldIhtFormEstateMapper.ihtFormEstate(ocrFields);
        assertNull(response);
    }


    @Test
    public void shouldReturnNullWhenPreEEDod() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .ihtFormEstate(IHT400421_FORM)
            .deceasedDateOfDeath(PRE_EE_DECEASED_DATE_OF_DEATH)
            .build();
        IhtFormEstate response = ocrFieldIhtFormEstateMapper.ihtFormEstate(ocrFields);
        assertNull(response);
    }
}
