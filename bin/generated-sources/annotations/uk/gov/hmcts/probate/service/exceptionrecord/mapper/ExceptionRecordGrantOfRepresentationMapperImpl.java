package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import io.micrometer.core.instrument.util.StringUtils;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.reform.probate.model.AdoptiveRelative;
import uk.gov.hmcts.reform.probate.model.AttorneyNamesAndAddress;
import uk.gov.hmcts.reform.probate.model.cases.ApplicationType;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ExecutorApplying;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ExecutorNotApplying;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-12T13:50:48+0100",
    comments = "version: 1.2.0.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class ExceptionRecordGrantOfRepresentationMapperImpl implements ExceptionRecordGrantOfRepresentationMapper {

    @Autowired
    private ApplicationTypeMapper applicationTypeMapper;
    @Autowired
    private OCRFieldAddressMapper oCRFieldAddressMapper;
    @Autowired
    private OCRFieldAdditionalExecutorsApplyingMapper oCRFieldAdditionalExecutorsApplyingMapper;
    @Autowired
    private OCRFieldAdditionalExecutorsNotApplyingMapper oCRFieldAdditionalExecutorsNotApplyingMapper;
    @Autowired
    private OCRFieldMartialStatusMapper oCRFieldMartialStatusMapper;
    @Autowired
    private OCRFieldAdoptiveRelativesMapper oCRFieldAdoptiveRelativesMapper;
    @Autowired
    private OCRFieldIhtFormEstateMapper oCRFieldIhtFormEstateMapper;
    @Autowired
    private OCRFieldIhtFormTypeMapper oCRFieldIhtFormTypeMapper;
    @Autowired
    private OCRFieldRelationshipMapper oCRFieldRelationshipMapper;
    @Autowired
    private OCRFieldPaymentMethodMapper oCRFieldPaymentMethodMapper;
    @Autowired
    private OCRFieldIhtFormEstateValuesCompletedMapper oCRFieldIhtFormEstateValuesCompletedMapper;
    @Autowired
    private OCRFieldIhtFormCompletedOnlineMapper oCRFieldIhtFormCompletedOnlineMapper;
    @Autowired
    private OCRFieldDeceasedHadLateSpouseOrCivilPartnerMapper oCRFieldDeceasedHadLateSpouseOrCivilPartnerMapper;
    @Autowired
    private OCRFieldIhtGrossValueMapper oCRFieldIhtGrossValueMapper;
    @Autowired
    private OCRFieldIhtNetValueMapper oCRFieldIhtNetValueMapper;

    @Override
    public GrantOfRepresentationData toCcdData(ExceptionRecordOCRFields ocrFields, GrantType grantType) {
        if ( ocrFields == null && grantType == null ) {
            return null;
        }

        GrantOfRepresentationData grantOfRepresentationData = new GrantOfRepresentationData();

        if ( ocrFields != null ) {
            grantOfRepresentationData.setApplicationType( applicationTypeMapper.toApplicationTypeGrantOfRepresentation( ocrFields ) );
            if ( ocrFields.getPaymentReferenceNumberPaperform() != null ) {
                grantOfRepresentationData.setPaymentReferenceNumberPaperform( ocrFields.getPaymentReferenceNumberPaperform() );
            }
            if ( ocrFields.getWholeBloodUnclesAndAuntsSurvivedUnderEighteen() != null ) {
                grantOfRepresentationData.setWholeBloodUnclesAndAuntsSurvivedUnderEighteen( ocrFields.getWholeBloodUnclesAndAuntsSurvivedUnderEighteen() );
            }
            if ( ocrFields.getWholeBloodUnclesAndAuntsDiedUnderEighteen() != null ) {
                grantOfRepresentationData.setWholeBloodUnclesAndAuntsDiedUnderEighteen( ocrFields.getWholeBloodUnclesAndAuntsDiedUnderEighteen() );
            }
            if ( ocrFields.getPrimaryApplicantRelationshipToDeceased() != null ) {
                grantOfRepresentationData.setPrimaryApplicantRelationshipToDeceased( oCRFieldRelationshipMapper.toRelationship( ocrFields.getPrimaryApplicantRelationshipToDeceased() ) );
            }
            if ( ocrFields.getSolsSolicitorFirmName() != null ) {
                grantOfRepresentationData.setSolsSolicitorFirmName( ocrFields.getSolsSolicitorFirmName() );
            }
            if ( ocrFields.getHalfBloodCousinsSurvivedOverEighteen() != null ) {
                grantOfRepresentationData.setHalfBloodCousinsSurvivedOverEighteen( ocrFields.getHalfBloodCousinsSurvivedOverEighteen() );
            }
            if ( ocrFields.getSolsWillTypeReason() != null ) {
                grantOfRepresentationData.setSolsWillTypeReason( ocrFields.getSolsWillTypeReason() );
            }
            if ( ocrFields.getHalfBloodUnclesAndAuntsSurvivedUnderEighteen() != null ) {
                grantOfRepresentationData.setHalfBloodUnclesAndAuntsSurvivedUnderEighteen( ocrFields.getHalfBloodUnclesAndAuntsSurvivedUnderEighteen() );
            }
            if ( ocrFields.getGrandparentsDiedUnderEighteen() != null ) {
                grantOfRepresentationData.setGrandparentsDiedUnderEighteen( ocrFields.getGrandparentsDiedUnderEighteen() );
            }
            if ( ocrFields.getWholeBloodUnclesAndAuntsSurvivedOverEighteen() != null ) {
                grantOfRepresentationData.setWholeBloodUnclesAndAuntsSurvivedOverEighteen( ocrFields.getWholeBloodUnclesAndAuntsSurvivedOverEighteen() );
            }
            if ( ocrFields.getWholeBloodCousinsSurvivedUnderEighteen() != null ) {
                grantOfRepresentationData.setWholeBloodCousinsSurvivedUnderEighteen( ocrFields.getWholeBloodCousinsSurvivedUnderEighteen() );
            }
            if ( ocrFields.getIhtCode() != null ) {
                grantOfRepresentationData.setUniqueProbateCodeId( ocrFields.getIhtCode() );
            }
            List<CollectionMember<ExecutorApplying>> list = oCRFieldAdditionalExecutorsApplyingMapper.toAdditionalCollectionMember( ocrFields );
            if ( list != null ) {
                grantOfRepresentationData.setExecutorsApplying( list );
            }
            else {
                grantOfRepresentationData.setExecutorsApplying( null );
            }
            if ( ocrFields.getSolsSolicitorRepresentativeName() != null ) {
                grantOfRepresentationData.setSolsSOTName( ocrFields.getSolsSolicitorRepresentativeName() );
            }
            List<CollectionMember<AttorneyNamesAndAddress>> list1 = oCRFieldAddressMapper.toAttorneyOnBehalfOfAddress( ocrFields );
            if ( list1 != null ) {
                grantOfRepresentationData.setAttorneyOnBehalfOfNameAndAddress( list1 );
            }
            else {
                grantOfRepresentationData.setAttorneyOnBehalfOfNameAndAddress( null );
            }
            grantOfRepresentationData.setIhtGrossValue( oCRFieldIhtGrossValueMapper.toIHTGrossValue( ocrFields ) );
            grantOfRepresentationData.setIhtFormId( oCRFieldIhtFormTypeMapper.ihtFormType( ocrFields ) );
            if ( ocrFields.getWholeBloodSiblingsDiedOverEighteen() != null ) {
                grantOfRepresentationData.setWholeBloodSiblingsDiedOverEighteen( ocrFields.getWholeBloodSiblingsDiedOverEighteen() );
            }
            if ( ocrFields.getHalfBloodNeicesAndNephewsOverEighteen() != null ) {
                grantOfRepresentationData.setHalfBloodNeicesAndNephewsOverEighteen( ocrFields.getHalfBloodNeicesAndNephewsOverEighteen() );
            }
            if ( ocrFields.getSolsFeeAccountNumber() != null ) {
                grantOfRepresentationData.setSolsFeeAccountNumber( ocrFields.getSolsFeeAccountNumber() );
            }
            if ( ocrFields.getHalfBloodSiblingsSurvivedUnderEighteen() != null ) {
                grantOfRepresentationData.setHalfBloodSiblingsSurvivedUnderEighteen( ocrFields.getHalfBloodSiblingsSurvivedUnderEighteen() );
            }
            if ( ocrFields.getPrimaryApplicantEmailAddress() != null ) {
                grantOfRepresentationData.setPrimaryApplicantEmailAddress( ocrFields.getPrimaryApplicantEmailAddress() );
            }
            if ( ocrFields.getParentsExistOverEighteenSurvived() != null ) {
                grantOfRepresentationData.setParentsExistOverEighteenSurvived( ocrFields.getParentsExistOverEighteenSurvived() );
            }
            if ( ocrFields.getHalfBloodSiblingsSurvivedOverEighteen() != null ) {
                grantOfRepresentationData.setHalfBloodSiblingsSurvivedOverEighteen( ocrFields.getHalfBloodSiblingsSurvivedOverEighteen() );
            }
            if ( ocrFields.getPrimaryApplicantPhoneNumber() != null ) {
                grantOfRepresentationData.setPrimaryApplicantPhoneNumber( ocrFields.getPrimaryApplicantPhoneNumber() );
            }
            if ( ocrFields.getHalfBloodUnclesAndAuntsDiedOverEighteen() != null ) {
                grantOfRepresentationData.setHalfBloodUnclesAndAuntsDiedOverEighteen( ocrFields.getHalfBloodUnclesAndAuntsDiedOverEighteen() );
            }
            if ( ocrFields.getCourtOfDecree() != null ) {
                grantOfRepresentationData.setCourtOfDecree( ocrFields.getCourtOfDecree() );
            }
            if ( ocrFields.getWholeBloodNeicesAndNephewsUnderEighteen() != null ) {
                grantOfRepresentationData.setWholeBloodNeicesAndNephewsUnderEighteen( ocrFields.getWholeBloodNeicesAndNephewsUnderEighteen() );
            }
            if ( ocrFields.getPaperPaymentMethod() != null ) {
                grantOfRepresentationData.setPaperPaymentMethod( oCRFieldPaymentMethodMapper.validateKnownPaymentMethod( ocrFields.getPaperPaymentMethod() ) );
            }
            grantOfRepresentationData.setSolsSolicitorAddress( oCRFieldAddressMapper.toSolicitorAddress( ocrFields ) );
            if ( ocrFields.getHalfBloodCousinsSurvivedUnderEighteen() != null ) {
                grantOfRepresentationData.setHalfBloodCousinsSurvivedUnderEighteen( ocrFields.getHalfBloodCousinsSurvivedUnderEighteen() );
            }
            if ( ocrFields.getWholeBloodUnclesAndAuntsDiedOverEighteen() != null ) {
                grantOfRepresentationData.setWholeBloodUnclesAndAuntsDiedOverEighteen( ocrFields.getWholeBloodUnclesAndAuntsDiedOverEighteen() );
            }
            if ( ocrFields.getPrimaryApplicantAlias() != null ) {
                grantOfRepresentationData.setPrimaryApplicantAlias( ocrFields.getPrimaryApplicantAlias() );
            }
            grantOfRepresentationData.setIhtFormEstateValuesCompleted( oCRFieldIhtFormEstateValuesCompletedMapper.toIhtFormEstateValuesCompleted( ocrFields ) );
            if ( ocrFields.getHalfBloodUnclesAndAuntsSurvivedOverEighteen() != null ) {
                grantOfRepresentationData.setHalfBloodUnclesAndAuntsSurvivedOverEighteen( ocrFields.getHalfBloodUnclesAndAuntsSurvivedOverEighteen() );
            }
            grantOfRepresentationData.setDeceasedAddress( oCRFieldAddressMapper.toDeceasedAddress( ocrFields ) );
            if ( ocrFields.getHalfBloodSiblingsDiedOverEighteen() != null ) {
                grantOfRepresentationData.setHalfBloodSiblingsDiedOverEighteen( ocrFields.getHalfBloodSiblingsDiedOverEighteen() );
            }
            if ( ocrFields.getDeceasedMartialStatus() != null ) {
                grantOfRepresentationData.setDeceasedMaritalStatus( oCRFieldMartialStatusMapper.toMartialStatus( ocrFields.getDeceasedMartialStatus() ) );
            }
            List<CollectionMember<ExecutorNotApplying>> list2 = oCRFieldAdditionalExecutorsNotApplyingMapper.toAdditionalCollectionMember( ocrFields );
            if ( list2 != null ) {
                grantOfRepresentationData.setExecutorsNotApplying( list2 );
            }
            else {
                grantOfRepresentationData.setExecutorsNotApplying( null );
            }
            if ( ocrFields.getPrimaryApplicantForenames() != null ) {
                grantOfRepresentationData.setPrimaryApplicantForenames( ocrFields.getPrimaryApplicantForenames() );
            }
            if ( ocrFields.getWholeBloodCousinsSurvivedOverEighteen() != null ) {
                grantOfRepresentationData.setWholeBloodCousinsSurvivedOverEighteen( ocrFields.getWholeBloodCousinsSurvivedOverEighteen() );
            }
            if ( ocrFields.getIhtReferenceNumber() != null ) {
                grantOfRepresentationData.setIhtReferenceNumber( ocrFields.getIhtReferenceNumber() );
            }
            if ( ocrFields.getWholeBloodSiblingsSurvivedOverEighteen() != null ) {
                grantOfRepresentationData.setWholeBloodSiblingsSurvivedOverEighteen( ocrFields.getWholeBloodSiblingsSurvivedOverEighteen() );
            }
            if ( ocrFields.getWholeBloodNeicesAndNephewsOverEighteen() != null ) {
                grantOfRepresentationData.setWholeBloodNeicesAndNephewsOverEighteen( ocrFields.getWholeBloodNeicesAndNephewsOverEighteen() );
            }
            if ( ocrFields.getWholeBloodSiblingsSurvivedUnderEighteen() != null ) {
                grantOfRepresentationData.setWholeBloodSiblingsSurvivedUnderEighteen( ocrFields.getWholeBloodSiblingsSurvivedUnderEighteen() );
            }
            if ( ocrFields.getDeceasedSurname() != null ) {
                grantOfRepresentationData.setDeceasedSurname( ocrFields.getDeceasedSurname() );
            }
            if ( ocrFields.getHalfBloodNeicesAndNephewsUnderEighteen() != null ) {
                grantOfRepresentationData.setHalfBloodNeicesAndNephewsUnderEighteen( ocrFields.getHalfBloodNeicesAndNephewsUnderEighteen() );
            }
            grantOfRepresentationData.setIhtFormEstate( oCRFieldIhtFormEstateMapper.ihtFormEstate( ocrFields ) );
            if ( ocrFields.getDomicilityCountry() != null ) {
                grantOfRepresentationData.setDomicilityCountry( ocrFields.getDomicilityCountry() );
            }
            if ( ocrFields.getPrimaryApplicantSecondPhoneNumber() != null ) {
                grantOfRepresentationData.setPrimaryApplicantSecondPhoneNumber( ocrFields.getPrimaryApplicantSecondPhoneNumber() );
            }
            if ( ocrFields.getGrandparentsDiedOverEighteen() != null ) {
                grantOfRepresentationData.setGrandparentsDiedOverEighteen( ocrFields.getGrandparentsDiedOverEighteen() );
            }
            if ( ocrFields.getSolsSolicitorPhoneNumber() != null ) {
                grantOfRepresentationData.setSolsSolicitorPhoneNumber( ocrFields.getSolsSolicitorPhoneNumber() );
            }
            if ( ocrFields.getHalfBloodSiblingsDiedUnderEighteen() != null ) {
                grantOfRepresentationData.setHalfBloodSiblingsDiedUnderEighteen( ocrFields.getHalfBloodSiblingsDiedUnderEighteen() );
            }
            if ( ocrFields.getParentsExistUnderEighteenSurvived() != null ) {
                grantOfRepresentationData.setParentsExistUnderEighteenSurvived( ocrFields.getParentsExistUnderEighteenSurvived() );
            }
            grantOfRepresentationData.setPrimaryApplicantAddress( oCRFieldAddressMapper.toPrimaryApplicantAddress( ocrFields ) );
            if ( ocrFields.getSolsSolicitorEmail() != null ) {
                grantOfRepresentationData.setSolsSolicitorEmail( ocrFields.getSolsSolicitorEmail() );
            }
            grantOfRepresentationData.setIhtNetValue( oCRFieldIhtNetValueMapper.toIHTNetValue( ocrFields ) );
            if ( ocrFields.getPrimaryApplicantRelationshipToDeceased() != null ) {
                grantOfRepresentationData.setPaRelationshipToDeceasedOther( oCRFieldRelationshipMapper.toRelationshipOther( ocrFields.getPrimaryApplicantRelationshipToDeceased() ) );
            }
            if ( ocrFields.getHalfBloodUnclesAndAuntsDiedUnderEighteen() != null ) {
                grantOfRepresentationData.setHalfBloodUnclesAndAuntsDiedUnderEighteen( ocrFields.getHalfBloodUnclesAndAuntsDiedUnderEighteen() );
            }
            if ( ocrFields.getDeceasedForenames() != null ) {
                grantOfRepresentationData.setDeceasedForenames( ocrFields.getDeceasedForenames() );
            }
            if ( ocrFields.getWholeBloodSiblingsDiedUnderEighteen() != null ) {
                grantOfRepresentationData.setWholeBloodSiblingsDiedUnderEighteen( ocrFields.getWholeBloodSiblingsDiedUnderEighteen() );
            }
            grantOfRepresentationData.setDeceasedHadLateSpouseOrCivilPartner( oCRFieldDeceasedHadLateSpouseOrCivilPartnerMapper.deceasedHadLateSpouseOrCivilPartner( ocrFields ) );
            grantOfRepresentationData.setIhtFormCompletedOnline( oCRFieldIhtFormCompletedOnlineMapper.ihtFormCompletedOnline( ocrFields ) );
            if ( ocrFields.getPrimaryApplicantSurname() != null ) {
                grantOfRepresentationData.setPrimaryApplicantSurname( ocrFields.getPrimaryApplicantSurname() );
            }
            if ( ocrFields.getSolsSolicitorAppReference() != null ) {
                grantOfRepresentationData.setSolsSolicitorAppReference( ocrFields.getSolsSolicitorAppReference() );
            }
            List<CollectionMember<AdoptiveRelative>> list3 = oCRFieldAdoptiveRelativesMapper.toAdoptiveRelativesCollectionMember( ocrFields );
            if ( list3 != null ) {
                grantOfRepresentationData.setAdoptiveRelatives( list3 );
            }
            else {
                grantOfRepresentationData.setAdoptiveRelatives( null );
            }
            if ( ocrFields.getChildrenDiedOverEighteen() != null ) {
                grantOfRepresentationData.setChildrenDiedOverEighteen( greaterThenZero( ocrFields.getChildrenDiedOverEighteen() ) );
            }
            if ( ocrFields.getChildrenDiedUnderEighteen() != null ) {
                grantOfRepresentationData.setChildrenDiedUnderEighteen( greaterThenZero( ocrFields.getChildrenDiedUnderEighteen() ) );
            }
            if ( ocrFields.getChildrenOverEighteenSurvived() != null ) {
                grantOfRepresentationData.setChildrenOverEighteenSurvived( greaterThenZero( ocrFields.getChildrenOverEighteenSurvived() ) );
            }
            if ( ocrFields.getChildrenUnderEighteenSurvived() != null ) {
                grantOfRepresentationData.setChildrenUnderEighteenSurvived( greaterThenZero( ocrFields.getChildrenUnderEighteenSurvived() ) );
            }
            if ( ocrFields.getGrandChildrenSurvivedUnderEighteen() != null ) {
                grantOfRepresentationData.setGrandChildrenSurvivedUnderEighteen( greaterThenZero( ocrFields.getGrandChildrenSurvivedUnderEighteen() ) );
            }
            if ( ocrFields.getGrandChildrenSurvivedOverEighteen() != null ) {
                grantOfRepresentationData.setGrandChildrenSurvivedOverEighteen( greaterThenZero( ocrFields.getGrandChildrenSurvivedOverEighteen() ) );
            }
        }
        if ( grantType != null ) {
            grantOfRepresentationData.setGrantType( grantType );
        }
        grantOfRepresentationData.setTotalFeePaperForm( OCRFieldIhtMoneyMapper.poundsToPennies(new String("totalFeePaperForm"), ocrFields.getTotalFeePaperForm()) );
        grantOfRepresentationData.setDeceasedDateOfBirth( OCRFieldDefaultLocalDateFieldMapper.toDefaultDateFieldMember(new String("deceasedDateOfBirth"), ocrFields.getDeceasedDateOfBirth()) );
        grantOfRepresentationData.setWillHasCodicils( OCRFieldYesOrNoMapper.toYesOrNo(new String("willHasCodicils"), ocrFields.getWillHasCodicils()) );
        grantOfRepresentationData.setGrandChildrenSurvivedUnderEighteenText( ocrFields.getGrandChildrenSurvivedUnderEighteen() );
        grantOfRepresentationData.setHmrcLetterId( OCRFieldYesOrNoMapper.toYesOrNo(new String("iht400process"), ocrFields.getIht400process()) );
        grantOfRepresentationData.setSolsWillType( OCRFieldSolicitorWillTypeMapper.toSolicitorWillType(ocrFields.getSolsWillType(), grantType) );
        grantOfRepresentationData.setApplyingAsAnAttorney( OCRFieldYesOrNoMapper.toYesOrNo(new String("applyingAsAnAttorney"), ocrFields.getApplyingAsAnAttorney()) );
        grantOfRepresentationData.setChannelChoice( new String("BulkScan") );
        grantOfRepresentationData.setChildrenOverEighteenSurvivedText( ocrFields.getChildrenOverEighteenSurvived() );
        grantOfRepresentationData.setFeeForCopiesPaperForm( OCRFieldIhtMoneyMapper.poundsToPennies(new String("feeForCopiesPaperForm"), ocrFields.getFeeForCopiesPaperForm()) );
        grantOfRepresentationData.setChildrenUnderEighteenSurvivedText( ocrFields.getChildrenUnderEighteenSurvived() );
        grantOfRepresentationData.setForeignAssetEstateValue( OCRFieldIhtMoneyMapper.poundsToPennies(new String("foreignAssetEstateValue"), ocrFields.getForeignAssetEstateValue()) );
        grantOfRepresentationData.setEpaRegistered( OCRFieldYesOrNoMapper.toYesOrNo(new String("epaRegistered"), ocrFields.getEpaRegistered()) );
        grantOfRepresentationData.setApplicationFeePaperForm( OCRFieldIhtMoneyMapper.poundsToPennies(new String("applicationFeePaperForm"), ocrFields.getApplicationFeePaperForm()) );
        grantOfRepresentationData.setDeceasedDomicileInEngWales( OCRFieldYesOrNoMapper.toYesOrNo(new String("deceasedDomicileInEngWales"), ocrFields.getDeceasedDomicileInEngWales()) );
        grantOfRepresentationData.setSolsSolicitorIsApplying( OCRFieldYesOrNoMapper.toYesOrNo(new String("solsSolicitorIsApplying"), ocrFields.getSolsSolicitorIsApplying()) );
        grantOfRepresentationData.setForeignAsset( OCRFieldYesOrNoMapper.toYesOrNo(new String("foreignAsset"), ocrFields.getForeignAsset()) );
        grantOfRepresentationData.setIhtEstateNetValue( OCRFieldIhtMoneyMapper.poundsToPennies(new String("ihtEstateNetValue"), ocrFields.getIhtEstateNetValue()) );
        grantOfRepresentationData.setMentalCapacity( OCRFieldYesOrNoMapper.toYesOrNo(new String("mentalCapacity"), ocrFields.getMentalCapacity()) );
        grantOfRepresentationData.setAdopted( OCRFieldYesOrNoMapper.toYesOrNo(new String("adopted"), ocrFields.getAdopted()) );
        grantOfRepresentationData.setGrandChildrenSurvivedOverEighteenText( ocrFields.getGrandChildrenSurvivedOverEighteen() );
        grantOfRepresentationData.setCourtOfProtection( OCRFieldYesOrNoMapper.toYesOrNo(new String("courtOfProtection"), ocrFields.getCourtOfProtection()) );
        grantOfRepresentationData.setDeceasedDateOfDeath( OCRFieldDefaultLocalDateFieldMapper.toDefaultDateFieldMember(new String("deceasedDateOfDeath"), ocrFields.getDeceasedDateOfDeath()) );
        grantOfRepresentationData.setNotifiedApplicants( OCRFieldYesOrNoMapper.toYesOrNo(new String("notifiedApplicants"), ocrFields.getNotifiedApplicants()) );
        grantOfRepresentationData.setEpaOrLpa( OCRFieldYesOrNoMapper.toYesOrNo(new String("epaOrLpa"), ocrFields.getEpaOrLpa()) );
        grantOfRepresentationData.setIhtEstateGrossValue( OCRFieldIhtMoneyMapper.poundsToPennies(new String("ihtEstateGrossValue"), ocrFields.getIhtEstateGrossValue()) );
        grantOfRepresentationData.setPrimaryApplicantHasAlias( OCRFieldYesOrNoMapper.toYesOrNo(new String("primaryApplicantHasAlias"), ocrFields.getPrimaryApplicantHasAlias()) );
        grantOfRepresentationData.setIhtUnusedAllowanceClaimed( OCRFieldYesOrNoMapper.toYesOrNo(new String("ihtUnusedAllowanceClaimed"), ocrFields.getIhtUnusedAllowanceClaimed()) );
        grantOfRepresentationData.setDateOfDivorcedCPJudicially( OCRFieldDefaultLocalDateFieldMapper.toDefaultDateFieldMember(new String("dateOfDivorcedCPJudicially"), ocrFields.getDateOfDivorcedCPJudicially()) );
        grantOfRepresentationData.setDeceasedAnyOtherNames( OCRFieldYesOrNoMapper.toYesOrNo(new String("deceasedAnyOtherNames"), ocrFields.getDeceasedAnyOtherNames()) );
        grantOfRepresentationData.setDateOfMarriageOrCP( OCRFieldDefaultLocalDateFieldMapper.toDefaultDateFieldMember(new String("dateOfMarriageOrCP"), ocrFields.getDateOfMarriageOrCP()) );
        grantOfRepresentationData.setChildrenDiedUnderEighteenText( ocrFields.getChildrenDiedUnderEighteen() );
        grantOfRepresentationData.setPaperForm( Boolean.TRUE );
        grantOfRepresentationData.setWillDate( OCRFieldDefaultLocalDateFieldMapper.toDefaultDateFieldMember(new String("willDate"), ocrFields.getWillDate()) );
        grantOfRepresentationData.setWillsOutsideOfUK( OCRFieldYesOrNoMapper.toYesOrNo(new String("willsOutsideOfUK"), ocrFields.getWillsOutsideOfUK()) );
        grantOfRepresentationData.setDeceasedMarriedAfterWillOrCodicilDate( OCRFieldYesOrNoMapper.toYesOrNo(new String("deceasedMarriedAfterWillOrCodicilDate"), ocrFields.getDeceasedMarriedAfterWillOrCodicilDate()) );
        grantOfRepresentationData.setWillGiftUnderEighteen( OCRFieldYesOrNoMapper.toYesOrNo(new String("willGiftUnderEighteen"), ocrFields.getWillGiftUnderEighteen()) );
        grantOfRepresentationData.setExtraCopiesOfGrant( OCRFieldNumberMapper.stringToLong(new String("extraCopiesOfGrant"), ocrFields.getExtraCopiesOfGrant()) );
        grantOfRepresentationData.setIhtEstateNetQualifyingValue( OCRFieldIhtMoneyMapper.poundsToPennies(new String("ihtEstateNetQualifyingValue"), ocrFields.getIhtEstateNetQualifyingValue()) );
        grantOfRepresentationData.setChildrenDiedOverEighteenText( ocrFields.getChildrenDiedOverEighteen() );
        grantOfRepresentationData.setLanguagePreferenceWelsh( OCRFieldYesOrNoMapper.toYesOrNo(new String("bilingualGrantRequested"), ocrFields.getBilingualGrantRequested()) );
        grantOfRepresentationData.setSpouseOrPartner( OCRFieldYesOrNoMapper.toYesOrNo(new String("spouseOrPartner"), ocrFields.getSpouseOrPartner()) );
        grantOfRepresentationData.setOutsideUkGrantCopies( OCRFieldNumberMapper.stringToLong(new String("outsideUKGrantCopies"), ocrFields.getOutsideUKGrantCopies()) );

        clearSolsWillTypeAndReason( grantOfRepresentationData );
        setDomicilityIHTCert( grantOfRepresentationData, ocrFields );
        setSolsPaymentMethod( grantOfRepresentationData, ocrFields );
        setSolsSolicitorRepresentativeName( grantOfRepresentationData, ocrFields );
        clearIhtFormOrReferenceIfNotSelected( grantOfRepresentationData, ocrFields );
        setDerivedFamilyBooleans( grantOfRepresentationData, ocrFields, grantType );
        setApplyingAsAnAttorneyBoolean( grantOfRepresentationData, ocrFields );
        setHandOffToLegacySiteBoolean( grantOfRepresentationData, ocrFields );
        setNonRequiredEstateValuesToNull( grantOfRepresentationData, ocrFields );

        return grantOfRepresentationData;
    }
}
