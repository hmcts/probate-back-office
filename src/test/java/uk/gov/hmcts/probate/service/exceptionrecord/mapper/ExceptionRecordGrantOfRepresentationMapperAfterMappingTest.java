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
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;
import uk.gov.hmcts.reform.probate.model.cases.ApplicationType;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.SolicitorWillType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
class ExceptionRecordGrantOfRepresentationMapperAfterMappingTest {

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
    private OCRFieldIhtFormEstateMapper ocrFieldIhtFormEstateMapper;
    @Autowired
    private OCRFieldIhtFormEstateValuesCompletedMapper ocrFieldIhtFormEstateValuesCompletedMapper;
    @Autowired
    private OCRFieldIhtFormCompletedOnlineMapper ocrFieldIhtFormCompletedOnlineMapper;
    @Autowired
    private OCRFieldDeceasedHadLateSpouseOrCivilPartnerMapper ocrFieldDeceasedHadLateSpouseOrCivilPartnerMapper;
    @Autowired
    private ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;
    @Autowired
    private OCRFieldIhtFormTypeMapper ocrFieldIhtFormTypeMapper;
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
    @Autowired
    private OCRFieldIhtGrossValueMapper ocrFieldIhtGrossValueMapper;
    @Autowired
    private OCRFieldIhtNetValueMapper ocrFieldIhtNetValueMapper;
    @Autowired
    private BulkScanConfig bulkScanConfig;

    @Test
    void testSetSolsPaymentMethodIsSolicitorGrantOfProbate() {
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
    void testSetSolsPaymentMethodIsSolicitorIntestacy() {
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
    void testSetSolsPaymentMethodIsPersonal() {
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
    void testSetSolsSolicitorRepresentativeSingleNameGrantOfProbate() {
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
    void testSetSolsSolicitorRepresentativeThreeNameIntestacy() {
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
    void testSetSolsSolicitorRepresentativeNameTwoNamesGrantOfProbate() {
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
    void testSetSolsSolicitorRepresentativeNameNoNamesIntestacy() {
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
    void testSetDomicilityIHTCertTrue() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .domicilityEntrustingDocument(null)
            .domicilitySuccessionIHTCert(TRUE)
            .build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertTrue(response.getDomicilityIHTCert());
    }

    @Test
    void testSetDomicilityIHTCertNullIfNotTrue() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .domicilityEntrustingDocument(FALSE)
            .domicilitySuccessionIHTCert(null)
            .build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertNull(response.getDomicilityIHTCert());
    }

    @Test
    void testIHTReferenceClearedIfNotOnline() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .ihtFormCompletedOnline(FALSE)
            .ihtReferenceNumber("REF123456789")
            .ihtFormId("IHT205")
            .formVersion("1")
            .build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertNotNull(response.getIhtFormId());
        assertNull(response.getIhtReferenceNumber());
    }

    @Test
    void testIHTFormIdClearedIfOnline() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .ihtFormCompletedOnline(TRUE)
            .ihtReferenceNumber("REF123456789")
            .ihtFormId("IHT205")
            .formVersion("1")
            .build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertNotNull(response.getIhtReferenceNumber());
        assertNull(response.getIhtFormId());
    }

    @Test
    void setDerivedFamilyBooleansTrue() {
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
    void setDerivedFamilyIntestacyBooleansNull() {
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
    void testSetApplyingAsAnAttorneyBooleanTrue() {
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
    void testSetApplyingAsAnAttorneyBooleanFalse() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.INTESTACY);
        assertFalse(response.getApplyingAsAnAttorney());
    }

    @Test
    void testIhtFormEstateNull() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertNull(response.getIhtFormEstate());
    }

    @Test
    void testIhtEstateGrossValue() {
        ExceptionRecordOCRFields ocrFields =
            ExceptionRecordOCRFields.builder().ihtEstateGrossValue("1,000,000").build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertEquals(Long.valueOf(100000000), response.getIhtEstateGrossValue());
    }

    @Test
    void testIhtEstateGrossValueNull() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertNull(response.getIhtEstateGrossValue());
    }

    @Test
    void testIhtEstateNetValue() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().ihtEstateNetValue("900,0000").build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertEquals(Long.valueOf(900000000), response.getIhtEstateNetValue());
    }

    @Test
    void testIhtEstateNetValueNull() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertNull(response.getIhtEstateNetValue());
    }

    @Test
    void testIhtEstateNetQualifyingValue() {
        ExceptionRecordOCRFields ocrFields =
            ExceptionRecordOCRFields.builder().ihtEstateNetQualifyingValue("800,000").build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertEquals(Long.valueOf(80000000), response.getIhtEstateNetQualifyingValue());
    }

    @Test
    void testIhtEstateNetQualifyingValueNull() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertNull(response.getIhtEstateNetQualifyingValue());
    }

    @Test
    void testIhtUnusedAllowanceClaimedTrue() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().ihtUnusedAllowanceClaimed(TRUE).build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertTrue(response.getIhtUnusedAllowanceClaimed());
    }

    @Test
    void testIhtUnusedAllowanceClaimedFalse() {
        ExceptionRecordOCRFields ocrFields =
            ExceptionRecordOCRFields.builder().ihtUnusedAllowanceClaimed(FALSE).build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertFalse(response.getIhtUnusedAllowanceClaimed());
    }

    @Test
    void testIhtUnusedAllowanceClaimedNull() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder().build();
        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertNull(response.getIhtUnusedAllowanceClaimed());
    }

    @Test
    void testSolicitorGetsHandedOffToLegacySiteSetYes() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
            .solsSolicitorIsApplying("True")
            .build();

        GrantOfRepresentationData response =
            exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertTrue(response.getCaseHandedOffToLegacySite());
    }

    @Test
    void testSolicitorWillTypeProbate() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .solsWillType("Probate")
                .solsSolicitorIsApplying("true")
                .build();
        GrantOfRepresentationData response =
                exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertEquals(SolicitorWillType.GRANT_TYPE_PROBATE, response.getSolsWillType());
    }

    @Test
    void testSolicitorWillTypeAdmon() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .solsWillType("Admon Will")
                .solsSolicitorIsApplying("true")
                .build();
        GrantOfRepresentationData response =
                exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.ADMON_WILL);
        assertEquals(SolicitorWillType.GRANT_TYPE_ADMON, response.getSolsWillType());
    }

    @Test
    void testSolicitorWillTypeIntestacy() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .solsWillType("Intestacy")
                .solsSolicitorIsApplying("true")
                .build();
        GrantOfRepresentationData response =
                exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.INTESTACY);
        assertEquals(SolicitorWillType.GRANT_TYPE_INTESTACY, response.getSolsWillType());
    }

    @Test
    void testSetEmptySolsWillTypeAndReason() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .solsWillType("WillLeftAnnexed")
                .solsWillTypeReason("someReason")
                .solsSolicitorIsApplying("false")
                .build();
        GrantOfRepresentationData response =
                exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertNull(response.getSolsWillType());
        assertNull(response.getSolsWillTypeReason());
    }

    @Test
    void testSetEmptySolsWillReason() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .solsWillTypeReason("someReason")
                .solsSolicitorIsApplying("true")
                .build();
        GrantOfRepresentationData response =
                exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);
        assertNull(response.getSolsWillTypeReason());
    }

    @Test
    void testNonRequiredEstateValueFieldsToBeNullWithFormVersion2() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .formVersion("2")
                .ihtEstateGrossValue("900,0000")
                .ihtEstateNetValue("900,0000")
                .ihtEstateNetQualifyingValue("900,0000")
                .deceasedDiedOnAfterSwitchDate("False")
                .build();

        GrantOfRepresentationData response =
                exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);

        assertNull(response.getIhtEstateGrossValue());
        assertNull(response.getIhtEstateNetValue());
        assertNull(response.getIhtEstateNetQualifyingValue());
    }

    @Test
    void testNonRequiredEstateValueFieldsToBeNullWithFormVersion3() {
        ExceptionRecordOCRFields ocrFields = ExceptionRecordOCRFields.builder()
                .formVersion("3")
                .ihtNetValue205("900,000")
                .ihtGrossValue205("900,000")
                .ihtEstateGrossValue("900,0000")
                .ihtEstateNetValue("900,0000")
                .ihtEstateNetQualifyingValue("900,0000")
                .exceptedEstate("False")
                .iht205Completed("True")
                .build();

        GrantOfRepresentationData response =
                exceptionRecordGrantOfRepresentationMapper.toCcdData(ocrFields, GrantType.GRANT_OF_PROBATE);

        assertNull(response.getIhtEstateGrossValue());
        assertNull(response.getIhtEstateNetValue());
        assertNull(response.getIhtEstateNetQualifyingValue());
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
        public OCRFieldIhtFormEstateMapper ocrFieldIhtFormEstateMapper() {
            return new OCRFieldIhtFormEstateMapper();
        }

        @Bean
        public OCRFieldIhtFormEstateValuesCompletedMapper ocrFieldIhtFormEstateValuesCompletedMapper() {
            return new OCRFieldIhtFormEstateValuesCompletedMapper();
        }

        @Bean
        public OCRFieldIhtFormCompletedOnlineMapper ocrFieldIhtFormCompletedOnlineMapper() {
            return new OCRFieldIhtFormCompletedOnlineMapper();
        }

        @Bean
        public OCRFieldDeceasedHadLateSpouseOrCivilPartnerMapper ocrFieldDeceasedHadLateSpouseOrCivilPartnerMapper() {
            return new OCRFieldDeceasedHadLateSpouseOrCivilPartnerMapper();
        }

        @Bean
        public ExceptedEstateDateOfDeathChecker eeDateOfDeathChecker() {
            return new ExceptedEstateDateOfDeathChecker();
        }

        @Bean
        public OCRFieldIhtFormTypeMapper ocrFieldIhtFormTypeMapper() {
            return new OCRFieldIhtFormTypeMapper();
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

        @Bean OCRFieldSolicitorWillTypeMapper ocrFieldSolicitorWillTypeMapper() {
            return new OCRFieldSolicitorWillTypeMapper();
        }

        @Bean
        public ApplicationTypeMapper applicationTypeMapper() {
            return new ApplicationTypeMapper();
        }

        @Bean
        public OCRFieldIhtGrossValueMapper ocrFieldIhtGrossValueMapper() {
            return new OCRFieldIhtGrossValueMapper();
        }

        @Bean
        public OCRFieldIhtNetValueMapper ocrFieldIhtNetValueMapper() {
            return new OCRFieldIhtNetValueMapper();
        }

        @Bean
        public ExceptionRecordGrantOfRepresentationMapper mainMapper() {
            return Mappers.getMapper(ExceptionRecordGrantOfRepresentationMapper.class);
        }

        @Bean
        public BulkScanConfig bulkScanConfig() {
            return new BulkScanConfig();
        }
    }
}
