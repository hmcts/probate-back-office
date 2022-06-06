package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;
import uk.gov.hmcts.reform.probate.model.IhtFormType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
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
    private static final String UNKNOWN_FORM = "UNKNOWNFORM";
    private static final String PRE_EE_DECEASED_DATE_OF_DEATH = "01012021";
    private static final String POST_EE_DECEASED_DATE_OF_DEATH = "01012022";

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @BeforeEach
    public void setUp() {
        when(exceptedEstateDateOfDeathChecker
            .isOnOrAfterSwitchDate(PRE_EE_DECEASED_DATE_OF_DEATH)).thenReturn(false);
        when(exceptedEstateDateOfDeathChecker
            .isOnOrAfterSwitchDate(POST_EE_DECEASED_DATE_OF_DEATH)).thenReturn(true);
    }

    @Test
    public void testCorrectFormTypeIHT205() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .ihtFormId(IHT205_FORM)
            .build();
        IhtFormType response = ocrFieldIhtFormTypeMapper.ihtFormType(ocrFields);
        assertEquals(IhtFormType.optionIHT205, response);
    }

    @Test
    public void testCorrectFormTypeIHT207() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .ihtFormId(IHT207_FORM)
            .build();
        IhtFormType response = ocrFieldIhtFormTypeMapper.ihtFormType(ocrFields);
        assertEquals(IhtFormType.optionIHT207, response);
    }

    @Test
    public void testCorrectFormTypeIHT400421() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .ihtFormId(IHT400421_FORM)
            .build();
        IhtFormType response = ocrFieldIhtFormTypeMapper.ihtFormType(ocrFields);
        assertEquals(IhtFormType.optionIHT400421, response);
    }

    @Test
    public void testCorrectFormTypeIHT421() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .ihtFormId(IHT421_FORM)
            .build();
        IhtFormType response = ocrFieldIhtFormTypeMapper.ihtFormType(ocrFields);
        assertEquals(IhtFormType.optionIHT400421, response);
    }

    @Test
    public void testCorrectFormTypeIHT400() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .ihtFormId(IHT400_FORM)
            .build();
        IhtFormType response = ocrFieldIhtFormTypeMapper.ihtFormType(ocrFields);
        assertEquals(IhtFormType.optionIHT400421, response);
    }

    @Test
    public void testExceptionForUnknownForm5() {
        assertThrows(OCRMappingException.class, () -> {
            ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                    .ihtFormId(UNKNOWN_FORM)
                    .build();
            ocrFieldIhtFormTypeMapper.ihtFormType(ocrFields);
        });
    }

    @Test
    public void shouldReturnNullWhenIhtFormIdIsNull() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .build();
        IhtFormType response = ocrFieldIhtFormTypeMapper.ihtFormType(ocrFields);
        assertNull(response);
    }

    @Test
    public void shouldReturnNullWhenIhtFormIdIsEmptyString() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .ihtFormId("")
            .build();
        IhtFormType response = ocrFieldIhtFormTypeMapper.ihtFormType(ocrFields);
        assertNull(response);
    }

    @Test
    public void shouldReturnNullWhenPostEEDodFormType2() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .ihtFormId(IHT400421_FORM)
            .formVersion("2")
            .deceasedDateOfDeath(POST_EE_DECEASED_DATE_OF_DEATH)
            .build();
        IhtFormType response = ocrFieldIhtFormTypeMapper.ihtFormType(ocrFields);
        assertNull(response);
    }

    @Test
    public void shouldMap400421FormType2() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .formVersion("2")
            .deceasedDateOfDeath(PRE_EE_DECEASED_DATE_OF_DEATH)
            .iht400421Completed("true")
            .build();
        IhtFormType response = ocrFieldIhtFormTypeMapper.ihtFormType(ocrFields);
        assertEquals(IhtFormType.optionIHT400421, response);
    }

    @Test
    public void shouldMap207FormType2() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .formVersion("2")
            .deceasedDateOfDeath(PRE_EE_DECEASED_DATE_OF_DEATH)
            .iht207Completed("true")
            .build();
        IhtFormType response = ocrFieldIhtFormTypeMapper.ihtFormType(ocrFields);
        assertEquals(IhtFormType.optionIHT207, response);
    }

    @Test
    public void shouldMapIHT205completedOnlineFormType2() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .formVersion("2")
            .deceasedDateOfDeath(PRE_EE_DECEASED_DATE_OF_DEATH)
            .iht205completedOnline("false")
            .build();
        IhtFormType response = ocrFieldIhtFormTypeMapper.ihtFormType(ocrFields);
        assertEquals(IhtFormType.optionIHT205, response);
    }


    @Test
    public void shouldReturnNullIHT205completedOnlineTrueFormType2() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .formVersion("2")
            .deceasedDateOfDeath(PRE_EE_DECEASED_DATE_OF_DEATH)
            .iht205completedOnline("true")
            .build();
        IhtFormType response = ocrFieldIhtFormTypeMapper.ihtFormType(ocrFields);
        assertNull(response);
    }
}
