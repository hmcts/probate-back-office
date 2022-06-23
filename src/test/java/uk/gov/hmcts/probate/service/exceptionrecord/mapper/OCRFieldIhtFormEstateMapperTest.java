package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;
import uk.gov.hmcts.reform.probate.model.IhtFormEstate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
class OCRFieldIhtFormEstateMapperTest {

    @Mock
    ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;

    @InjectMocks
    OCRFieldIhtFormEstateMapper ocrFieldIhtFormEstateMapper = new OCRFieldIhtFormEstateMapper();

    private static final String PRE_EE_DECEASED_DATE_OF_DEATH = "01012021";
    private static final String POST_EE_DECEASED_DATE_OF_DEATH = "01012022";

    @BeforeEach
    public void setUp() {
        when(exceptedEstateDateOfDeathChecker
            .isOnOrAfterSwitchDate(PRE_EE_DECEASED_DATE_OF_DEATH)).thenReturn(false);
        when(exceptedEstateDateOfDeathChecker
            .isOnOrAfterSwitchDate(POST_EE_DECEASED_DATE_OF_DEATH)).thenReturn(true);
    }

    @Test
    void testCorrectFormTypeIHT207() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .formVersion("2")
            .iht207Completed("true")
            .deceasedDateOfDeath(POST_EE_DECEASED_DATE_OF_DEATH)
            .build();
        IhtFormEstate response = ocrFieldIhtFormEstateMapper.ihtFormEstate(ocrFields);
        assertEquals(IhtFormEstate.optionIHT207, response);
    }

    @Test
    void testCorrectFormTypeIHT400421() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .formVersion("2")
            .iht400421Completed("true")
            .deceasedDateOfDeath(POST_EE_DECEASED_DATE_OF_DEATH)
            .build();
        IhtFormEstate response = ocrFieldIhtFormEstateMapper.ihtFormEstate(ocrFields);
        assertEquals(IhtFormEstate.optionIHT400421, response);
    }

    @Test
    void shouldReturnNullWhenPreEEDod() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .formVersion("2")
            .deceasedDateOfDeath(PRE_EE_DECEASED_DATE_OF_DEATH)
            .build();
        IhtFormEstate response = ocrFieldIhtFormEstateMapper.ihtFormEstate(ocrFields);
        assertNull(response);
    }

    @Test
    void shouldReturnNullForOldform() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .build();
        IhtFormEstate response = ocrFieldIhtFormEstateMapper.ihtFormEstate(ocrFields);
        assertNull(response);
    }

}
