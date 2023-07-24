package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.SolicitorWillType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(SpringExtension.class)
class OCRFieldSolicitorWillTypeMapperTest {


    @InjectMocks
    OCRFieldSolicitorWillTypeMapper ocrFieldSolicitorWillTypeMapper = new OCRFieldSolicitorWillTypeMapper();


    @Test
    void testCorrectWillTypeAdmon() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .solsWillType("Admon Will")
            .build();
        SolicitorWillType response = ocrFieldSolicitorWillTypeMapper.toSolicitorWillType(ocrFields);
        assertEquals(SolicitorWillType.GRANT_TYPE_ADMON, response);
    }

    @Test
    void testCorrectWillTypeIntestacy() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .solsWillType("No Will")
                .build();
        SolicitorWillType response = ocrFieldSolicitorWillTypeMapper.toSolicitorWillType(ocrFields);
        assertEquals(SolicitorWillType.GRANT_TYPE_INTESTACY, response);
    }

    @Test
    void testCorrectWillTypeGrant() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .solsWillType("Grant")
                .build();
        SolicitorWillType response = ocrFieldSolicitorWillTypeMapper.toSolicitorWillType(ocrFields);
        assertEquals(SolicitorWillType.GRANT_TYPE_PROBATE, response);
    }

    @Test
    void testCorrectWillTypeProbate() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .solsWillType("Probate")
                .build();
        SolicitorWillType response = ocrFieldSolicitorWillTypeMapper.toSolicitorWillType(ocrFields);
        assertEquals(SolicitorWillType.GRANT_TYPE_PROBATE, response);
    }

    @Test
    void shouldReturnNullForEmpty() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .build();
        SolicitorWillType response = ocrFieldSolicitorWillTypeMapper.toSolicitorWillType(ocrFields);
        assertNull(response);
    }

    @Test
    void shouldThrowExceptionForInvalidWillType() {
        assertThrows(OCRMappingException.class, () -> {
            ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                    .solsWillType("Invalid")
                    .build();
            SolicitorWillType response = ocrFieldSolicitorWillTypeMapper.toSolicitorWillType(ocrFields);
        });
    }
}
