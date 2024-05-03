package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;
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
        SolicitorWillType response = ocrFieldSolicitorWillTypeMapper
                .toSolicitorWillType("Admon Will", GrantType.GRANT_OF_PROBATE);
        assertEquals(SolicitorWillType.GRANT_TYPE_ADMON, response);
    }

    @Test
    void testCorrectWillTypeIntestacy() {
        SolicitorWillType response = ocrFieldSolicitorWillTypeMapper
                .toSolicitorWillType("No Will", GrantType.INTESTACY);
        assertEquals(SolicitorWillType.GRANT_TYPE_INTESTACY, response);
    }

    @Test
    void testCorrectWillTypeGrant() {
        SolicitorWillType response = ocrFieldSolicitorWillTypeMapper
                .toSolicitorWillType("Grant", GrantType.GRANT_OF_PROBATE);
        assertEquals(SolicitorWillType.GRANT_TYPE_PROBATE, response);
    }

    @Test
    void testCorrectWillTypeProbate() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .solsWillType("Probate")
                .build();
        SolicitorWillType response = ocrFieldSolicitorWillTypeMapper
                .toSolicitorWillType("Probate", GrantType.GRANT_OF_PROBATE);
        assertEquals(SolicitorWillType.GRANT_TYPE_PROBATE, response);
    }

    @Test
    void testCorrectWillTypeWillLeftAnnexed() {
        SolicitorWillType response = ocrFieldSolicitorWillTypeMapper
                .toSolicitorWillType("Will Annexed", GrantType.GRANT_OF_PROBATE);
        assertEquals(SolicitorWillType.GRANT_TYPE_ADMON, response);
    }

    @Test
    void shouldReturnNullForEmpty() {
        SolicitorWillType response = ocrFieldSolicitorWillTypeMapper
                .toSolicitorWillType(null, GrantType.GRANT_OF_PROBATE);
        assertNull(response);
    }

    @Test
    void shouldThrowExceptionForInvalidWillType() {
        assertThrows(OCRMappingException.class, () -> {
            SolicitorWillType response = ocrFieldSolicitorWillTypeMapper
                    .toSolicitorWillType("Invalid will type", GrantType.GRANT_OF_PROBATE);
        });
    }
}
