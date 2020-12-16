package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.reform.probate.model.cases.ApplicationType;
import uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration
public class ExceptionRecordCavestMapperAfterMappingTest {

    @Autowired
    private ExceptionRecordCaveatMapper exceptionRecordCaveatMapper;

    @Autowired
    private OCRFieldAddressMapper ocrFieldAddressMapper;

    @Autowired
    private OCRFieldDefaultLocalDateFieldMapper ocrFieldDefaultLocalDateFieldMapper;

    @Autowired
    private OCRFieldYesOrNoMapper ocrFieldYesOrNoMapper;

    @Autowired
    private ApplicationTypeMapper applicationTypeMapper;

    private static CaveatData caseData;

    @Configuration
    public static class Config {

        @Bean
        public OCRFieldAddressMapper ocrFieldAddressMapper() {
            return new OCRFieldAddressMapper();
        }

        @Bean
        public OCRFieldDefaultLocalDateFieldMapper ocrFieldDefaultLocalDateFieldMapper() {
            return new OCRFieldDefaultLocalDateFieldMapper();
        }

        @Bean
        public OCRFieldYesOrNoMapper ocrFieldYesOrNoMapper() {
            return new OCRFieldYesOrNoMapper();
        }

        @Bean
        public ApplicationTypeMapper applicationTypeMapper() {
            return new ApplicationTypeMapper();
        }

        @Bean
        public ExceptionRecordCaveatMapper mainMapper() {
            return Mappers.getMapper(ExceptionRecordCaveatMapper.class);
        }
    }

    @Test
    public void testSetSolsPaymentMethodIsSolicitor() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .solsSolicitorFirmName("Firm Name")
                .solsSolicitorRepresentativeName("Sonny Solicitor")
                .build();
        CaveatData response = exceptionRecordCaveatMapper.toCcdData(ocrFields);
        assertEquals(ApplicationType.SOLICITORS, response.getApplicationType());
    }

    @Test
    public void testSetSolsPaymentMethodIsPersonal() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .caveatorForenames("Joe")
                .caveatorSurnames("Smith")
                .solsSolicitorFirmName(null)
                .solsSolicitorRepresentativeName("")
                .build();
        CaveatData response = exceptionRecordCaveatMapper.toCcdData(ocrFields);
        assertEquals(ApplicationType.PERSONAL, response.getApplicationType());
    }

    @Test
    public void testSetSolsSolicitorEmailIsSolicitor() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .solsSolicitorFirmName("Firm Name")
                .caveatorEmailAddress("caveator@probate-test.com")
                .solsSolicitorEmail("solicitor@probate-test.com")
                .build();
        CaveatData response = exceptionRecordCaveatMapper.toCcdData(ocrFields);
        assertEquals("solicitor@probate-test.com", response.getCaveatorEmailAddress());
    }

    @Test
    public void testSetSolsSolicitorEmailIsCitizen() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .caveatorEmailAddress("caveator@probate-test.com")
                .solsSolicitorEmail("solicitor@probate-test.com")
                .build();
        CaveatData response = exceptionRecordCaveatMapper.toCcdData(ocrFields);
        assertEquals("caveator@probate-test.com", response.getCaveatorEmailAddress());
    }

    @Test
    public void testSetSolsSolicitorRepresentativeSingleName() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .solsSolicitorFirmName("Firm Name")
                .caveatorForenames("Tom")
                .caveatorSurnames("Baker")
                .solsSolicitorRepresentativeName("Jim")
                .build();
        CaveatData response = exceptionRecordCaveatMapper.toCcdData(ocrFields);
        assertEquals("", response.getCaveatorSurname());
        assertEquals("Jim", response.getCaveatorForenames());
    }

    @Test
    public void testSetSolsSolicitorRepresentativeThreeName() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .solsSolicitorFirmName("Firm Name")
                .caveatorForenames("Tom")
                .caveatorSurnames("Baker")
                .solsSolicitorRepresentativeName("Jim Young")
                .build();
        CaveatData response = exceptionRecordCaveatMapper.toCcdData(ocrFields);
        assertEquals("Young", response.getCaveatorSurname());
        assertEquals("Jim", response.getCaveatorForenames());
    }

    @Test
    public void testSetSolsSolicitorRepresentativeNameTwoNames() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .solsSolicitorFirmName("Firm Name")
                .caveatorForenames("Tom")
                .caveatorSurnames("Baker")
                .solsSolicitorRepresentativeName("Jim Martyn Young")
                .build();
        CaveatData response = exceptionRecordCaveatMapper.toCcdData(ocrFields);
        assertEquals("Young", response.getCaveatorSurname());
        assertEquals("Jim Martyn", response.getCaveatorForenames());
    }

    @Test
    public void testSetSolsSolicitorRepresentativeNameNoNames() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .solsSolicitorFirmName("Firm Name")
                .caveatorForenames("Tom")
                .caveatorSurnames("Baker")
                .solsSolicitorRepresentativeName(null)
                .build();
        CaveatData response = exceptionRecordCaveatMapper.toCcdData(ocrFields);
        assertEquals("Baker", response.getCaveatorSurname());
        assertEquals("Tom", response.getCaveatorForenames());
    }
}
