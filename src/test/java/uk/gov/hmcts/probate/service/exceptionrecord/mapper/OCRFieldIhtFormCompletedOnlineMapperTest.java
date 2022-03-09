package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.Before;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.Spy;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;


@RunWith(SpringRunner.class)
public class OCRFieldIhtFormCompletedOnlineMapperTest {
    
    @Mock
    ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;
    @Spy
    OCRFieldYesOrNoMapper ocrFieldYesOrNoMapper;
    
    @InjectMocks
    OCRFieldIhtFormCompletedOnlineMapper ocrFieldIhtFormCompletedOnlineMapper 
        = new OCRFieldIhtFormCompletedOnlineMapper();

    private static final String PRE_EE_DECEASED_DATE_OF_DEATH = "01012021";
    private static final String POST_EE_DECEASED_DATE_OF_DEATH = "01012022";
    
    @Before
    public void setUp() {
        when(exceptedEstateDateOfDeathChecker
            .isOnOrAfterSwitchDate(PRE_EE_DECEASED_DATE_OF_DEATH)).thenReturn(false);
        when(exceptedEstateDateOfDeathChecker
            .isOnOrAfterSwitchDate(POST_EE_DECEASED_DATE_OF_DEATH)).thenReturn(true);
    }
    
    @Test
    public void shouldReturnTrueWhenIht205completedOnline() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .formVersion("2")
            .iht205completedOnline("true")
            .build();
        Boolean response = ocrFieldIhtFormCompletedOnlineMapper.ihtFormCompletedOnline(ocrFields);
        assertTrue(response);
    }
    
    @Test
    public void shouldReturnNull() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .formVersion("2")
            .iht205completedOnline("false")
            .build();
        Boolean response = ocrFieldIhtFormCompletedOnlineMapper.ihtFormCompletedOnline(ocrFields);
        assertNull(response);
    }
    
    @Test
    public void shouldReturnUseYesNoMapperForExistingFormFalse() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .ihtFormCompletedOnline("false")
            .build();
        Boolean response = ocrFieldIhtFormCompletedOnlineMapper.ihtFormCompletedOnline(ocrFields);
        assertFalse(response);
    }

    @Test
    public void shouldReturnUseYesNoMapperForExistingFormTrue() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .ihtFormCompletedOnline("true")
            .build();
        Boolean response = ocrFieldIhtFormCompletedOnlineMapper.ihtFormCompletedOnline(ocrFields);
        assertTrue(response);
    }
}
