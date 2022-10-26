package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.reform.probate.model.cases.ApplicationType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.probate.model.Constants.YES;

class ApplicationTypeMapperTest {

    private static final String SOLS_SOLICITORS_FIRM_NAME = "Chapman and Sons";
    private static final String SOLS_SOLICITORS_REPRESENTITIVE_NAME = "Bob Chapman";

    private ApplicationTypeMapper applicationTypeMapper = new ApplicationTypeMapper();

    private ExceptionRecordOCRFields ocrFields;
    private ExceptionRecordOCRFields ocrFieldsWithSolicitor;
    private ExceptionRecordOCRFields ocrFieldsWithSolicitorNoRepName;
    private ExceptionRecordOCRFields ocrFieldsWithSolicitorNoFirmName;
    private ExceptionRecordOCRFields ocrFieldsWithSolicitorCaveat;

    @BeforeEach
    public void setUpClass() throws Exception {
        ocrFields = ExceptionRecordOCRFields.builder()
            .build();

        ocrFieldsWithSolicitor = ExceptionRecordOCRFields.builder()
            .solsSolicitorIsApplying(YES)
            .solsSolicitorFirmName(SOLS_SOLICITORS_FIRM_NAME)
            .solsSolicitorRepresentativeName(SOLS_SOLICITORS_REPRESENTITIVE_NAME)
            .build();

        ocrFieldsWithSolicitorNoRepName = ExceptionRecordOCRFields.builder()
            .solsSolicitorIsApplying(YES)
            .solsSolicitorFirmName(SOLS_SOLICITORS_FIRM_NAME)
            .solsSolicitorRepresentativeName(null)
            .build();

        ocrFieldsWithSolicitorNoFirmName = ExceptionRecordOCRFields.builder()
            .solsSolicitorIsApplying(YES)
            .solsSolicitorFirmName("")
            .solsSolicitorRepresentativeName(SOLS_SOLICITORS_REPRESENTITIVE_NAME)
            .build();

        ocrFieldsWithSolicitorCaveat = ExceptionRecordOCRFields.builder()
            .legalRepresentative(YES)
            .build();
    }

    @Test
    void testApplicationTypeGrantOfRepresentationIsPersonal() {
        ApplicationType applicationType = applicationTypeMapper.toApplicationTypeGrantOfRepresentation(ocrFields);
        assertEquals(ApplicationType.PERSONAL, applicationType);
    }

    @Test
    void testApplicationTypeGrantOfRepresentationIsSolicitor() {
        ApplicationType applicationType =
            applicationTypeMapper.toApplicationTypeGrantOfRepresentation(ocrFieldsWithSolicitor);
        assertEquals(ApplicationType.SOLICITORS, applicationType);
    }

    @Test
    void testApplicationTypeGrantOfRepresentationIsSolicitorMissingFirmName() {
        ApplicationType applicationType =
            applicationTypeMapper.toApplicationTypeGrantOfRepresentation(ocrFieldsWithSolicitorNoFirmName);
        assertEquals(ApplicationType.SOLICITORS, applicationType);
    }

    @Test
    void testApplicationTypeGrantOfRepresentationIsSolicitorMissingRepName() {
        ApplicationType applicationType =
            applicationTypeMapper.toApplicationTypeGrantOfRepresentation(ocrFieldsWithSolicitorNoRepName);
        assertEquals(ApplicationType.SOLICITORS, applicationType);
    }

    @Test
    void testApplicationTypeCaveatIsPersonal() {
        ApplicationType applicationType = applicationTypeMapper.toApplicationTypeCaveat(ocrFields);
        assertEquals(ApplicationType.PERSONAL, applicationType);
    }

    @Test
    void testApplicationTypeCaveatIsSolicitor() {
        ApplicationType applicationType = applicationTypeMapper.toApplicationTypeCaveat(ocrFieldsWithSolicitorCaveat);
        assertEquals(ApplicationType.SOLICITORS, applicationType);
    }
}
