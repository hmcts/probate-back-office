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
    private static final String TRUE = "true";

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
            .iht207Completed(TRUE)
            .deceasedDateOfDeath(POST_EE_DECEASED_DATE_OF_DEATH)
            .build();
        IhtFormEstate response = ocrFieldIhtFormEstateMapper.ihtFormEstate(ocrFields);
        assertEquals(IhtFormEstate.optionIHT207, response);
    }

    @Test
    void testCorrectFormTypeIHT400421() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .formVersion("2")
            .iht400421Completed(TRUE)
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

    @Test
    void testCorrectFormTypeIHT207Version3() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .formVersion("3")
                .iht207Completed(TRUE)
                .deceasedDiedOnAfterSwitchDate(TRUE)
                .build();
        IhtFormEstate response = ocrFieldIhtFormEstateMapper.ihtFormEstate(ocrFields);
        assertEquals(IhtFormEstate.optionIHT207, response);
    }

    @Test
    void testCorrectFormTypeIHT400421Version3() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .formVersion("3")
                .iht400421Completed(TRUE)
                .deceasedDiedOnAfterSwitchDate(TRUE)
                .build();
        IhtFormEstate response = ocrFieldIhtFormEstateMapper.ihtFormEstate(ocrFields);
        assertEquals(IhtFormEstate.optionIHT400421, response);
    }

    @Test
    void testCorrectFormTypeIHT400Version3() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .formVersion("3")
                .iht400Completed(TRUE)
                .deceasedDiedOnAfterSwitchDate(TRUE)
                .build();
        IhtFormEstate response = ocrFieldIhtFormEstateMapper.ihtFormEstate(ocrFields);
        assertEquals(IhtFormEstate.optionIHT400, response);
    }

    @Test
    void shouldReturnNullWhenPreEEDodVersion3() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .formVersion("3")
                .deceasedDiedOnAfterSwitchDate(TRUE)
                .iht205Completed(TRUE)
                .build();
        IhtFormEstate response = ocrFieldIhtFormEstateMapper.ihtFormEstate(ocrFields);
        assertNull(response);
    }
}
