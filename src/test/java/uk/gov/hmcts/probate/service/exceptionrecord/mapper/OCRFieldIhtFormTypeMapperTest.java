package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.exceptionrecord.utils.ExceptedEstateDateOfDeathChecker;
import uk.gov.hmcts.reform.probate.model.IhtFormType;

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
public class OCRFieldIhtFormTypeMapperTest {


    @Mock
    ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;

    @InjectMocks
    OCRFieldIhtFormTypeMapper ocrFieldIhtFormTypeMapper = new OCRFieldIhtFormTypeMapper();

    private static final String IHT205_FORM = "IHT205";
    private static final String IHT207_FORM = "IHT207";
    private static final String IHT400421_FORM = "IHT400421";
    private static final String IHT421_FORM = "IHT421";
    private static final String IHT400_FORM = "IHT400";
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
    public void testCorrectFormTypeIHT205() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .ihtFormId(IHT205_FORM)
            .deceasedDateOfDeath(PRE_EE_DECEASED_DATE_OF_DEATH)
            .build();
        IhtFormType response = ocrFieldIhtFormTypeMapper.ihtFormType(ocrFields);
        assertEquals(IhtFormType.optionIHT205, response);
    }

    @Test
    public void testCorrectFormTypeIHT207() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .ihtFormId(IHT207_FORM)
            .deceasedDateOfDeath(PRE_EE_DECEASED_DATE_OF_DEATH)
            .build();
        IhtFormType response = ocrFieldIhtFormTypeMapper.ihtFormType(ocrFields);
        assertEquals(IhtFormType.optionIHT207, response);
    }

    @Test
    public void testCorrectFormTypeIHT400421() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .ihtFormId(IHT400421_FORM)
            .deceasedDateOfDeath(PRE_EE_DECEASED_DATE_OF_DEATH)
            .build();
        IhtFormType response = ocrFieldIhtFormTypeMapper.ihtFormType(ocrFields);
        assertEquals(IhtFormType.optionIHT400421, response);
    }

    @Test
    public void testCorrectFormTypeIHT421() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .ihtFormId(IHT421_FORM)
            .deceasedDateOfDeath(PRE_EE_DECEASED_DATE_OF_DEATH)
            .build();
        IhtFormType response = ocrFieldIhtFormTypeMapper.ihtFormType(ocrFields);
        assertEquals(IhtFormType.optionIHT400421, response);
    }

    @Test
    public void testCorrectFormTypeIHT400() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .ihtFormId(IHT400_FORM)
            .deceasedDateOfDeath(PRE_EE_DECEASED_DATE_OF_DEATH)
            .build();
        IhtFormType response = ocrFieldIhtFormTypeMapper.ihtFormType(ocrFields);
        assertEquals(IhtFormType.optionIHT400421, response);
    }

    @Test(expected = OCRMappingException.class)
    public void testExceptionForUnknownForm5() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .ihtFormId(UNKNOWN_FORM)
            .deceasedDateOfDeath(PRE_EE_DECEASED_DATE_OF_DEATH)
            .build();
        IhtFormType response = ocrFieldIhtFormTypeMapper.ihtFormType(ocrFields);
    }

    @Test
    public void shouldReturnNullWhenihtFormIdIsNull() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .deceasedDateOfDeath(PRE_EE_DECEASED_DATE_OF_DEATH)
            .build();
        IhtFormType response = ocrFieldIhtFormTypeMapper.ihtFormType(ocrFields);
        assertNull(response);
    }

    @Test
    public void shouldReturnNullWhenihtFormIdIsEmptyString() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .deceasedDateOfDeath(PRE_EE_DECEASED_DATE_OF_DEATH)
            .ihtFormId("")
            .build();
        IhtFormType response = ocrFieldIhtFormTypeMapper.ihtFormType(ocrFields);
        assertNull(response);
    }


    @Test
    public void shouldReturnNullWhenPreEEDod() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .ihtFormId(IHT400421_FORM)
            .deceasedDateOfDeath(POST_EE_DECEASED_DATE_OF_DEATH)
            .build();
        IhtFormType response = ocrFieldIhtFormTypeMapper.ihtFormType(ocrFields);
        assertNull(response);
    }
}
