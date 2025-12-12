package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.config.BulkScanConfig;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.reform.probate.model.cases.ApplicationType;
import uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
class ExceptionRecordCaveatMapperAfterMappingTest {

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
    
    @Autowired
    private OCRFieldProbateFeeMapper ocrFieldProbateFeeMapper;
    
    @Autowired
    private OCRFieldProbateFeeNotIncludedReasonMapper ocrFieldProbateFeeNotIncludedReasonMapper;

    @Autowired
    private BulkScanConfig bulkScanConfig;
    
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
        public OCRFieldProbateFeeMapper ocrFieldProbateFeeMapper() {
            return new OCRFieldProbateFeeMapper();
        }

        @Bean
        public OCRFieldProbateFeeNotIncludedReasonMapper ocrFieldProbateFeeNotIncludedReasonMapper() {
            return new OCRFieldProbateFeeNotIncludedReasonMapper();
        }
        
        @Bean
        public ExceptionRecordCaveatMapper mainMapper() {
            return Mappers.getMapper(ExceptionRecordCaveatMapper.class);
        }

        @Bean
        public BulkScanConfig bulkScanConfig() {
            return new BulkScanConfig();
        }
    }

    @Test
    void testSetApplicationTypeSolicitor() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .legalRepresentative("true")
                .build();
        CaveatData response = exceptionRecordCaveatMapper.toCcdData(ocrFields);
        assertEquals(ApplicationType.SOLICITORS, response.getApplicationType());
    }

    @Test
    void testSetApplicationTypePersonal() {
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
    void testSetSolsSolicitorEmailIsSolicitor() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .legalRepresentative("true")
                .caveatorEmailAddress("caveator@probate-test.com")
                .solsSolicitorEmail("solicitor@probate-test.com")
                .build();
        CaveatData response = exceptionRecordCaveatMapper.toCcdData(ocrFields);
        assertEquals("solicitor@probate-test.com", response.getCaveatorEmailAddress());
    }

    @Test
    void testSetSolsSolicitorEmailIsCitizen() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .caveatorEmailAddress("caveator@probate-test.com")
                .solsSolicitorEmail("solicitor@probate-test.com")
                .build();
        CaveatData response = exceptionRecordCaveatMapper.toCcdData(ocrFields);
        assertEquals("caveator@probate-test.com", response.getCaveatorEmailAddress());
    }


    @Test
    void testSetSolsSolicitorRepresentativeNameNoNames() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .legalRepresentative("true")
                .caveatorForenames("Tom")
                .caveatorSurnames("Baker")
                .solsSolicitorRepresentativeName(null)
                .build();
        CaveatData response = exceptionRecordCaveatMapper.toCcdData(ocrFields);
        assertEquals("Baker", response.getCaveatorSurname());
        assertEquals("Tom", response.getCaveatorForenames());
    }
}
