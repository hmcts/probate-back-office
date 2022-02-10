package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;
import uk.gov.hmcts.reform.probate.model.IhtFormEstate;


@RunWith(SpringRunner.class)
public class OCRFieldIhtFormEstateMapperTest {
    
    @Mock
    ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;

    @InjectMocks
    OCRFieldIhtFormEstateMapper ocrFieldIhtFormEstateMapper = new OCRFieldIhtFormEstateMapper();

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
            .formVersion("2")
            .iht207Completed("true")
            .deceasedDateOfDeath(POST_EE_DECEASED_DATE_OF_DEATH)
            .build();
        IhtFormEstate response = ocrFieldIhtFormEstateMapper.ihtFormEstate(ocrFields);
        assertEquals(IhtFormEstate.optionIHT207, response);
    }

    @Test
    public void testCorrectFormTypeIHT400421() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .formVersion("2")
            .iht400421Completed("true")
            .deceasedDateOfDeath(POST_EE_DECEASED_DATE_OF_DEATH)
            .build();        
        IhtFormEstate response = ocrFieldIhtFormEstateMapper.ihtFormEstate(ocrFields);
        assertEquals(IhtFormEstate.optionIHT400421, response);
    }
    
    @Test
    public void shouldReturnNullWhenPreEEDod() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .formVersion("2")
            .deceasedDateOfDeath(PRE_EE_DECEASED_DATE_OF_DEATH)
            .build();
        IhtFormEstate response = ocrFieldIhtFormEstateMapper.ihtFormEstate(ocrFields);
        assertNull(response);
    }

    @Test
    public void shouldReturnNullForOldform() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .build();
        IhtFormEstate response = ocrFieldIhtFormEstateMapper.ihtFormEstate(ocrFields);
        assertNull(response);
    }
    
}
