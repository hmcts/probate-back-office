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
class OCRFieldDeceasedHadLateSpouseOrCivilPartnerMapperTest {

    private static final String POST_EE_DECEASED_DATE_OF_DEATH = "01012022";
    private static final String PRE_EE_DECEASED_DATE_OF_DEATH = "01012021";

    @Mock
    ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;

    @BeforeEach
    public void setUp() {
        when(exceptedEstateDateOfDeathChecker
            .isOnOrAfterSwitchDate(POST_EE_DECEASED_DATE_OF_DEATH)).thenReturn(true);
        when(exceptedEstateDateOfDeathChecker
                .isOnOrAfterSwitchDate(PRE_EE_DECEASED_DATE_OF_DEATH)).thenReturn(false);
    }

    @InjectMocks
    OCRFieldDeceasedHadLateSpouseOrCivilPartnerMapper ocrFieldDeceasedHadLateSpouseOrCivilPartnerMapper
        = new OCRFieldDeceasedHadLateSpouseOrCivilPartnerMapper();



    @Test
    void shouldReturnTrue() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .formVersion("2")
            .deceasedDateOfDeath(POST_EE_DECEASED_DATE_OF_DEATH)
            .deceasedMartialStatus("widowed")
            .iht400421Completed("false")
            .iht207Completed("false")
            .build();
        Boolean response = ocrFieldDeceasedHadLateSpouseOrCivilPartnerMapper
            .deceasedHadLateSpouseOrCivilPartner(ocrFields);
        assertTrue(response);
    }

    @Test
    void shouldReturnFalse() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .formVersion("2")
            .deceasedDateOfDeath(POST_EE_DECEASED_DATE_OF_DEATH)
            .deceasedMartialStatus("divorced")
            .iht400421Completed("false")
            .iht207Completed("false")
            .build();
        Boolean response = ocrFieldDeceasedHadLateSpouseOrCivilPartnerMapper
            .deceasedHadLateSpouseOrCivilPartner(ocrFields);
        assertFalse(response);
    }

    @Test
    void shouldReturnNullAfterSwitchDateAndSubmittedFormV2() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .formVersion("2")
                .deceasedDateOfDeath(POST_EE_DECEASED_DATE_OF_DEATH)
                .iht400421Completed("true")
                .iht207Completed("false")
                .build();
        Boolean response = ocrFieldDeceasedHadLateSpouseOrCivilPartnerMapper
                .deceasedHadLateSpouseOrCivilPartner(ocrFields);
        assertNull(response);
    }

    @Test
    void shouldReturnNullBeforeSwitchDate() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .deceasedDateOfDeath(PRE_EE_DECEASED_DATE_OF_DEATH)
                .build();
        Boolean response = ocrFieldDeceasedHadLateSpouseOrCivilPartnerMapper
                .deceasedHadLateSpouseOrCivilPartner(ocrFields);
        assertNull(response);
    }

    @Test
    void shouldReturnTrueVersion3() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .formVersion("3")
                .deceasedDateOfDeath(POST_EE_DECEASED_DATE_OF_DEATH)
                .deceasedMartialStatus("widowed")
                .iht400421Completed("false")
                .iht207Completed("false")
                .iht400Completed("false")
                .build();
        Boolean response = ocrFieldDeceasedHadLateSpouseOrCivilPartnerMapper
                .deceasedHadLateSpouseOrCivilPartner(ocrFields);
        assertTrue(response);
    }

    @Test
    void shouldReturnFalseV3() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .formVersion("2")
                .deceasedDateOfDeath(POST_EE_DECEASED_DATE_OF_DEATH)
                .deceasedMartialStatus("divorced")
                .iht400421Completed("false")
                .iht207Completed("false")
                .build();
        Boolean response = ocrFieldDeceasedHadLateSpouseOrCivilPartnerMapper
                .deceasedHadLateSpouseOrCivilPartner(ocrFields);
        assertFalse(response);
    }

    @Test
    void shouldReturnNullAfterSwitchDateAndSubmittedFormV3() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .formVersion("2")
                .deceasedDateOfDeath(POST_EE_DECEASED_DATE_OF_DEATH)
                .iht400Completed("true")
                .iht207Completed("false")
                .build();
        Boolean response = ocrFieldDeceasedHadLateSpouseOrCivilPartnerMapper
                .deceasedHadLateSpouseOrCivilPartner(ocrFields);
        assertNull(response);
    }

    @Test
    void shouldReturnNull() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .build();
        Boolean response = ocrFieldDeceasedHadLateSpouseOrCivilPartnerMapper
            .deceasedHadLateSpouseOrCivilPartner(ocrFields);
        assertNull(response);
    }
}
