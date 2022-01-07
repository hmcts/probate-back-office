package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.reform.probate.model.IhtFormEstate;
import uk.gov.hmcts.reform.probate.model.cases.ApplicationType;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ContextConfiguration
public class ExceptionRecordGrantOfRepresentationMapperAfterMappingTest {

    private static final String TRUE = "True";
    private static final String FALSE = "False";
    private static GrantOfRepresentationData caseData;
    @Autowired
    private ExceptionRecordGrantOfRepresentationMapper exceptionRecordGrantOfRepresentationMapper;
    @Autowired
    private OCRFieldAddressMapper ocrFieldAddressMapper;
    @Autowired
    private OCRFieldAdditionalExecutorsApplyingMapper ocrFieldAdditionalExecutorsApplyingMapper;
    @Autowired
    private OCRFieldAdditionalExecutorsNotApplyingMapper ocrFieldAdditionalExecutorsNotApplyingMapper;
    @Autowired
    private OCRFieldDefaultLocalDateFieldMapper ocrFieldDefaultLocalDateFieldMapper;
    @Autowired
    private OCRFieldYesOrNoMapper ocrFieldYesOrNoMapper;
    @Autowired
    private OCRFieldMartialStatusMapper ocrFieldMartialStatusMapper;
    @Autowired
    private OCRFieldAdoptiveRelativesMapper ocrFieldAdoptiveRelativesMapper;
    @Autowired
    private OCRFieldIhtMoneyMapper ocrFieldIhtMoneyMapper;
    @Autowired
    private OCRFieldRelationshipMapper ocrFieldRelationshipMapper;
    @Autowired
    private OCRFieldNumberMapper ocrFieldNumberMapper;
    @Autowired
    private OCRFieldPaymentMethodMapper ocrFieldPaymentMethodMapper;
    @Autowired
    private ApplicationTypeMapper applicationTypeMapper;

    @Test
    public void testSetSolsPaymentMethodIsSolicitorGrantOfProbate() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .solsSolicitorIsApplying(TRUE)
            .solsSolicitorFirmName("Firm Name")
            .solsSolicitorRepresentativeName("Sonny Solicitor")
            .build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertEquals(ApplicationType.SOLICITORS, response.getApplicationType());
    }

    @Test
    public void testSetSolsPaymentMethodIsSolicitorIntestacy() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .solsSolicitorIsApplying(TRUE)
            .solsSolicitorFirmName("Firm Name")
            .solsSolicitorRepresentativeName("Sonny Solicitor")
            .build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.INTESTACY);
        assertEquals(ApplicationType.SOLICITORS, response.getApplicationType());
    }

    @Test
    public void testSetSolsPaymentMethodIsPersonal() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .solsSolicitorIsApplying(null)
            .primaryApplicantForenames("Joe")
            .primaryApplicantSurname("Smith")
            .solsSolicitorFirmName(null)
            .solsSolicitorRepresentativeName("")
            .build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertEquals(ApplicationType.PERSONAL, response.getApplicationType());
    }

    @Test
    public void testSetSolsSolicitorRepresentativeSingleNameGrantOfProbate() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .solsSolicitorIsApplying(TRUE)
            .solsSolicitorFirmName("Firm Name")
            .solsSolicitorRepresentativeName("Jim")
            .build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertEquals("", response.getSolsSOTSurname());
        assertEquals("Jim", response.getSolsSOTForenames());
    }

    @Test
    public void testSetSolsSolicitorRepresentativeThreeNameIntestacy() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .solsSolicitorIsApplying(TRUE)
            .solsSolicitorFirmName("Firm Name")
            .solsSolicitorRepresentativeName("Jim Young")
            .build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.INTESTACY);
        assertEquals("Young", response.getSolsSOTSurname());
        assertEquals("Jim", response.getSolsSOTForenames());
    }

    @Test
    public void testSetSolsSolicitorRepresentativeNameTwoNamesGrantOfProbate() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .solsSolicitorIsApplying(TRUE)
            .solsSolicitorFirmName("Firm Name")
            .solsSolicitorRepresentativeName("Jim Martyn Young")
            .build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertEquals("Young", response.getSolsSOTSurname());
        assertEquals("Jim Martyn", response.getSolsSOTForenames());
    }

    @Test
    public void testSetSolsSolicitorRepresentativeNameNoNamesIntestacy() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .solsSolicitorIsApplying(TRUE)
            .solsSolicitorFirmName("Firm Name")
            .solsSolicitorRepresentativeName(null)
            .build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.INTESTACY);
        assertEquals(null, response.getSolsSOTSurname());
        assertEquals(null, response.getSolsSOTForenames());
    }

    @Test
    public void testSetDomicilityIHTCertTrue() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .domicilityEntrustingDocument(null)
            .domicilitySuccessionIHTCert(TRUE)
            .build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertTrue(response.getDomicilityIHTCert());
    }

    @Test
    public void testSetDomicilityIHTCertNullIfNotTrue() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .domicilityEntrustingDocument(FALSE)
            .domicilitySuccessionIHTCert(null)
            .build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertNull(response.getDomicilityIHTCert());
    }

    @Test
    public void testIHTReferenceClearedIfNotOnline() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .ihtFormCompletedOnline(FALSE)
            .ihtReferenceNumber("REF123456789")
            .ihtFormId("IHT205")
            .build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertNotNull(response.getIhtFormId());
        assertNull(response.getIhtReferenceNumber());
    }

    @Test
    public void testIHTFormIdClearedIfOnline() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .ihtFormCompletedOnline(TRUE)
            .ihtReferenceNumber("REF123456789")
            .ihtFormId("IHT205")
            .build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertNotNull(response.getIhtReferenceNumber());
        assertNull(response.getIhtFormId());
    }

    @Test
    public void setDerivedFamilyBooleansTrue() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .childrenUnderEighteenSurvived("1")
            .childrenOverEighteenSurvived(null)
            .childrenDiedUnderEighteen("")
            .childrenDiedUnderEighteen("3")
            .grandChildrenSurvivedUnderEighteen("2")
            .grandChildrenSurvivedOverEighteen("0")
            .parentsExistUnderEighteenSurvived("2")
            .parentsExistOverEighteenSurvived("0")
            .wholeBloodSiblingsSurvivedUnderEighteen("1")
            .wholeBloodSiblingsSurvivedOverEighteen("0")
            .wholeBloodSiblingsDiedUnderEighteen("1")
            .wholeBloodSiblingsDiedOverEighteen(null)
            .wholeBloodNeicesAndNephewsUnderEighteen("1")
            .wholeBloodNeicesAndNephewsOverEighteen("")
            .halfBloodSiblingsSurvivedUnderEighteen("3")
            .halfBloodSiblingsSurvivedOverEighteen(null)
            .halfBloodSiblingsDiedUnderEighteen("1")
            .halfBloodSiblingsDiedOverEighteen("0")
            .halfBloodNeicesAndNephewsUnderEighteen("2")
            .halfBloodNeicesAndNephewsOverEighteen(null)
            .grandparentsDiedUnderEighteen("1")
            .grandparentsDiedOverEighteen("")
            .wholeBloodUnclesAndAuntsSurvivedUnderEighteen("1")
            .wholeBloodUnclesAndAuntsSurvivedOverEighteen(null)
            .wholeBloodUnclesAndAuntsDiedUnderEighteen("2")
            .wholeBloodUnclesAndAuntsDiedOverEighteen("0")
            .wholeBloodCousinsSurvivedUnderEighteen("1")
            .wholeBloodCousinsSurvivedOverEighteen(null)
            .halfBloodUnclesAndAuntsSurvivedUnderEighteen("1")
            .halfBloodUnclesAndAuntsSurvivedOverEighteen("0")
            .halfBloodUnclesAndAuntsDiedUnderEighteen("1")
            .halfBloodUnclesAndAuntsDiedOverEighteen(null)
            .halfBloodCousinsSurvivedUnderEighteen("2")
            .halfBloodCousinsSurvivedOverEighteen("")
            .build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.INTESTACY);
        assertTrue(response.getChildrenSurvived());
        assertTrue(response.getChildrenDied());
        assertTrue(response.getGrandChildrenSurvived());
        assertTrue(response.getParentsExistSurvived());
        assertTrue(response.getWholeBloodSiblingsSurvived());
        assertTrue(response.getWholeBloodSiblingsDied());
        assertTrue(response.getWholeBloodNeicesAndNephews());
        assertTrue(response.getHalfBloodSiblingsSurvived());
        assertTrue(response.getHalfBloodSiblingsDied());
        assertTrue(response.getHalfBloodNeicesAndNephews());
        assertTrue(response.getGrandparentsDied());
        assertTrue(response.getWholeBloodUnclesAndAuntsSurvived());
        assertTrue(response.getWholeBloodUnclesAndAuntsDied());
        assertTrue(response.getWholeBloodCousinsSurvived());
        assertTrue(response.getHalfBloodUnclesAndAuntsSurvived());
        assertTrue(response.getHalfBloodUnclesAndAuntsDied());
        assertTrue(response.getHalfBloodCousinsSurvived());
    }

    @Test
    public void setDerivedFamilyIntestacyBooleansNull() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .childrenUnderEighteenSurvived("0")
            .childrenOverEighteenSurvived(null)
            .childrenDiedUnderEighteen("")
            .childrenDiedUnderEighteen("0")
            .grandChildrenSurvivedUnderEighteen("0")
            .grandChildrenSurvivedOverEighteen("0")
            .parentsExistUnderEighteenSurvived("0")
            .parentsExistOverEighteenSurvived("0")
            .wholeBloodSiblingsSurvivedUnderEighteen("0")
            .wholeBloodSiblingsSurvivedOverEighteen("0")
            .wholeBloodSiblingsDiedUnderEighteen("0")
            .wholeBloodSiblingsDiedOverEighteen(null)
            .wholeBloodNeicesAndNephewsUnderEighteen("0")
            .wholeBloodNeicesAndNephewsOverEighteen("")
            .halfBloodSiblingsSurvivedUnderEighteen("0")
            .halfBloodSiblingsSurvivedOverEighteen(null)
            .halfBloodSiblingsDiedUnderEighteen("0")
            .halfBloodSiblingsDiedOverEighteen("0")
            .halfBloodNeicesAndNephewsUnderEighteen("0")
            .halfBloodNeicesAndNephewsOverEighteen(null)
            .grandparentsDiedUnderEighteen("0")
            .grandparentsDiedOverEighteen("")
            .wholeBloodUnclesAndAuntsSurvivedUnderEighteen("0")
            .wholeBloodUnclesAndAuntsSurvivedOverEighteen(null)
            .wholeBloodUnclesAndAuntsDiedUnderEighteen("0")
            .wholeBloodUnclesAndAuntsDiedOverEighteen("0")
            .wholeBloodCousinsSurvivedUnderEighteen("0")
            .wholeBloodCousinsSurvivedOverEighteen(null)
            .halfBloodUnclesAndAuntsSurvivedUnderEighteen("0")
            .halfBloodUnclesAndAuntsSurvivedOverEighteen("0")
            .halfBloodUnclesAndAuntsDiedUnderEighteen("0")
            .halfBloodUnclesAndAuntsDiedOverEighteen(null)
            .halfBloodCousinsSurvivedUnderEighteen("0")
            .halfBloodCousinsSurvivedOverEighteen("")
            .build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.INTESTACY);
        assertNull(response.getChildrenSurvived());
        assertNull(response.getChildrenOverEighteenSurvivedText());
        assertNull(response.getChildrenUnderEighteenSurvivedText());
        assertNull(response.getChildrenDied());
        assertNull(response.getChildrenDiedOverEighteenText());
        assertNull(response.getChildrenDiedUnderEighteenText());
        assertNull(response.getGrandChildrenSurvived());
        assertNull(response.getGrandChildrenSurvivedOverEighteenText());
        assertNull(response.getGrandChildrenSurvivedUnderEighteenText());
        assertNull(response.getParentsExistSurvived());
        assertNull(response.getParentsExistOverEighteenSurvived());
        assertNull(response.getParentsExistUnderEighteenSurvived());
        assertNull(response.getWholeBloodSiblingsSurvived());
        assertNull(response.getWholeBloodSiblingsSurvivedOverEighteen());
        assertNull(response.getWholeBloodSiblingsSurvivedUnderEighteen());
        assertNull(response.getWholeBloodSiblingsDied());
        assertNull(response.getWholeBloodSiblingsDiedOverEighteen());
        assertNull(response.getWholeBloodSiblingsDiedUnderEighteen());
        assertNull(response.getWholeBloodNeicesAndNephews());
        assertNull(response.getWholeBloodNeicesAndNephewsOverEighteen());
        assertNull(response.getWholeBloodNeicesAndNephewsUnderEighteen());
        assertNull(response.getHalfBloodSiblingsSurvived());
        assertNull(response.getHalfBloodSiblingsSurvivedOverEighteen());
        assertNull(response.getHalfBloodSiblingsSurvivedUnderEighteen());
        assertNull(response.getHalfBloodSiblingsDied());
        assertNull(response.getHalfBloodSiblingsDiedOverEighteen());
        assertNull(response.getHalfBloodSiblingsDiedUnderEighteen());
        assertNull(response.getHalfBloodNeicesAndNephews());
        assertNull(response.getHalfBloodNeicesAndNephewsOverEighteen());
        assertNull(response.getHalfBloodNeicesAndNephewsUnderEighteen());
        assertNull(response.getGrandparentsDied());
        assertNull(response.getGrandparentsDiedOverEighteen());
        assertNull(response.getGrandparentsDiedUnderEighteen());
        assertNull(response.getWholeBloodUnclesAndAuntsSurvived());
        assertNull(response.getWholeBloodUnclesAndAuntsSurvivedOverEighteen());
        assertNull(response.getWholeBloodUnclesAndAuntsSurvivedUnderEighteen());
        assertNull(response.getWholeBloodUnclesAndAuntsDied());
        assertNull(response.getWholeBloodUnclesAndAuntsDiedOverEighteen());
        assertNull(response.getWholeBloodUnclesAndAuntsDiedUnderEighteen());
        assertNull(response.getWholeBloodCousinsSurvived());
        assertNull(response.getWholeBloodCousinsSurvivedOverEighteen());
        assertNull(response.getWholeBloodCousinsSurvivedUnderEighteen());
        assertNull(response.getHalfBloodUnclesAndAuntsSurvived());
        assertNull(response.getHalfBloodUnclesAndAuntsSurvivedOverEighteen());
        assertNull(response.getHalfBloodUnclesAndAuntsSurvivedUnderEighteen());
        assertNull(response.getHalfBloodUnclesAndAuntsDied());
        assertNull(response.getHalfBloodUnclesAndAuntsDiedOverEighteen());
        assertNull(response.getHalfBloodUnclesAndAuntsDiedUnderEighteen());
        assertNull(response.getHalfBloodCousinsSurvived());
        assertNull(response.getHalfBloodCousinsSurvivedOverEighteen());
        assertNull(response.getHalfBloodCousinsSurvivedUnderEighteen());
    }

    @Test
    public void testSetApplyingAsAnAttorneyBooleanTrue() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .attorneyOnBehalfOfName("Fred and Sons")
            .attorneyOnBehalfOfAddressLine1("12 Grren Park")
            .attorneyOnBehalfOfAddressTown("London")
            .attorneyOnBehalfOfAddressPostCode("W1A 0AX")
            .build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.INTESTACY);
        assertTrue(response.getApplyingAsAnAttorney());
    }

    @Test
    public void testSetApplyingAsAnAttorneyBooleanFalse() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.INTESTACY);
        assertFalse(response.getApplyingAsAnAttorney());
    }

    @Test
    public void testIhtFormEstateIHT400421() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().ihtFormEstate("IHT400421").build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertEquals(IhtFormEstate.optionIHT400421, response.getIhtFormEstate());
    }

    @Test
    public void testIhtFormEstateIHT207() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().ihtFormEstate("IHT207").build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertEquals(IhtFormEstate.optionIHT207, response.getIhtFormEstate());
    }


    @Test
    public void testIhtFormEstateIHT205() {
        Exception exception = assertThrows(OCRMappingException.class, () -> {
            ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().ihtFormEstate("IHT205").build();
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        });

        String expectedMessage = "Form type IHT207 or IHT400421 expected but got 'IHT205'";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testIhtFormEstateNull() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertNull(response.getIhtFormEstate());
    }
    
    @Test
    public void testIhtEstateGrossValue() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().ihtEstateGrossValue("1,000,000").build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertEquals(Long.valueOf(100000000), response.getIhtEstateGrossValue());
    }
    
    @Test
    public void testIhtEstateGrossValueNull() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertNull( response.getIhtEstateGrossValue());
    } 
    
    @Test
    public void testIhtEstateNetValue() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().ihtEstateNetValue("900,0000").build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertEquals(Long.valueOf(900000000), response.getIhtEstateNetValue());
    }
    
    @Test
    public void testIhtEstateNetValueNull() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertNull( response.getIhtEstateNetValue());
    }
    
    @Test
    public void testIhtEstateNetQualifyingValue() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().ihtEstateNetQualifyingValue("800,000").build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertEquals(Long.valueOf(80000000), response.getIhtEstateNetQualifyingValue());
    }


    @Test
    public void testIhtEstateNetQualifyingValueNull() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertNull(response.getIhtEstateNetQualifyingValue());
    }
    
    @Test
    public void testDeceasedHadLateSpouseOrCivilPartnerTrue() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().deceasedHadLateSpouseOrCivilPartner(TRUE).build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertTrue(response.getDeceasedHadLateSpouseOrCivilPartner());
    }

    @Test
    public void testDeceasedHadLateSpouseOrCivilPartnerFalse() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().deceasedHadLateSpouseOrCivilPartner(FALSE).build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertFalse(response.getDeceasedHadLateSpouseOrCivilPartner());
    }

    @Test
    public void testDeceasedHadLateSpouseOrCivilPartnerNull() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertNull(response.getDeceasedHadLateSpouseOrCivilPartner());
    }

    @Test
    public void testIhtUnusedAllowanceClaimedTrue() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().ihtUnusedAllowanceClaimed(TRUE).build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertTrue(response.getIhtUnusedAllowanceClaimed());
    }

    @Test
    public void testIhtUnusedAllowanceClaimedFalse() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().ihtUnusedAllowanceClaimed(FALSE).build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertFalse(response.getIhtUnusedAllowanceClaimed());
    }

    @Test
    public void testIhtUnusedAllowanceClaimedNull() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertNull(response.getIhtUnusedAllowanceClaimed());
    }

    @Configuration
    public static class Config {

        @Bean
        public OCRFieldAddressMapper ocrFieldAddressMapper() {
            return new OCRFieldAddressMapper();
        }

        @Bean
        public OCRFieldAdditionalExecutorsApplyingMapper ocrFieldAdditionalExecutorsApplyingMapper() {
            return new OCRFieldAdditionalExecutorsApplyingMapper();
        }

        @Bean
        public OCRFieldAdditionalExecutorsNotApplyingMapper ocrFieldAdditionalExecutorsNotApplyingMapper() {
            return new OCRFieldAdditionalExecutorsNotApplyingMapper();
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
        public OCRFieldMartialStatusMapper ocrFieldMartialStatusMapper() {
            return new OCRFieldMartialStatusMapper();
        }

        @Bean
        public OCRFieldAdoptiveRelativesMapper ocrFieldAdoptiveRelativesMapper() {
            return new OCRFieldAdoptiveRelativesMapper();
        }

        @Bean
        public OCRFieldIhtMoneyMapper ocrFieldIhtMoneyMapper() {
            return new OCRFieldIhtMoneyMapper();
        }

        @Bean
        public OCRFieldRelationshipMapper ocrFieldRelationshipMapper() {
            return new OCRFieldRelationshipMapper();
        }

        @Bean
        public OCRFieldPaymentMethodMapper ocrFieldNumberMapper() {
            return new OCRFieldPaymentMethodMapper();
        }

        @Bean
        public OCRFieldNumberMapper ocrFieldPaymentMethodMapper() {
            return new OCRFieldNumberMapper();
        }

        @Bean
        public ApplicationTypeMapper applicationTypeMapper() {
            return new ApplicationTypeMapper();
        }

        @Bean
        public ExceptionRecordGrantOfRepresentationMapper mainMapper() {
            return Mappers.getMapper(ExceptionRecordGrantOfRepresentationMapper.class);
        }
    }
}
