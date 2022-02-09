package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;


@RunWith(SpringRunner.class)
public class OCRFieldDeceasedHadLateSpouseOrCivilPartnerMapperTest {
    
    OCRFieldDeceasedHadLateSpouseOrCivilPartnerMapper ocrFieldDeceasedHadLateSpouseOrCivilPartnerMapper 
        = new OCRFieldDeceasedHadLateSpouseOrCivilPartnerMapper();
    
    @Test
    public void shouldReturnTrue() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .formVersion("2")
            .deceasedMartialStatus("widowed")
            .build();
        Boolean response = ocrFieldDeceasedHadLateSpouseOrCivilPartnerMapper
            .decasedHadLateSpouseOrCivilPartner(ocrFields);
        assertTrue(response);
    }

    @Test
    public void shouldReturnFalse() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .formVersion("2")
            .deceasedMartialStatus("divorced")
            .build();
        Boolean response = ocrFieldDeceasedHadLateSpouseOrCivilPartnerMapper
            .decasedHadLateSpouseOrCivilPartner(ocrFields);
        assertFalse(response);
    }
    
    @Test
    public void shouldReturnNull() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .build();
        Boolean response = ocrFieldDeceasedHadLateSpouseOrCivilPartnerMapper
            .decasedHadLateSpouseOrCivilPartner(ocrFields);
        assertNull(response);
    }
}
