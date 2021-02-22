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
    date = "2021-02-22T16:15:34+0000",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 11.0.10 (Ubuntu)"
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
    private OCRFieldDefaultLocalDateFieldMapper oCRFieldDefaultLocalDateFieldMapper;
    @Autowired
    private OCRFieldYesOrNoMapper oCRFieldYesOrNoMapper;
    @Autowired
    private OCRFieldMartialStatusMapper oCRFieldMartialStatusMapper;
    @Autowired
    private OCRFieldAdoptiveRelativesMapper oCRFieldAdoptiveRelativesMapper;
    @Autowired
    private OCRFieldIhtMoneyMapper oCRFieldIhtMoneyMapper;
    @Autowired
    private OCRFieldRelationshipMapper oCRFieldRelationshipMapper;
    @Autowired
    private OCRFieldPaymentMethodMapper oCRFieldPaymentMethodMapper;
    @Autowired
    private OCRFieldNumberMapper oCRFieldNumberMapper;

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
            if ( ocrFields.getTotalFeePaperForm() != null ) {
                grantOfRepresentationData.setTotalFeePaperForm( oCRFieldIhtMoneyMapper.poundsToPennies( ocrFields.getTotalFeePaperForm() ) );
            }
            if ( ocrFields.getWholeBloodUnclesAndAuntsSurvivedUnderEighteen() != null ) {
                grantOfRepresentationData.setWholeBloodUnclesAndAuntsSurvivedUnderEighteen( ocrFields.getWholeBloodUnclesAndAuntsSurvivedUnderEighteen() );
            }
            if ( ocrFields.getDeceasedDateOfBirth() != null ) {
                grantOfRepresentationData.setDeceasedDateOfBirth( oCRFieldDefaultLocalDateFieldMapper.toDefaultDateFieldMember( ocrFields.getDeceasedDateOfBirth() ) );
            }
            if ( ocrFields.getWholeBloodUnclesAndAuntsDiedUnderEighteen() != null ) {
                grantOfRepresentationData.setWholeBloodUnclesAndAuntsDiedUnderEighteen( ocrFields.getWholeBloodUnclesAndAuntsDiedUnderEighteen() );
            }
            if ( ocrFields.getWillHasCodicils() != null ) {
                grantOfRepresentationData.setWillHasCodicils( oCRFieldYesOrNoMapper.toYesOrNo( ocrFields.getWillHasCodicils() ) );
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
            if ( ocrFields.getHalfBloodUnclesAndAuntsSurvivedUnderEighteen() != null ) {
                grantOfRepresentationData.setHalfBloodUnclesAndAuntsSurvivedUnderEighteen( ocrFields.getHalfBloodUnclesAndAuntsSurvivedUnderEighteen() );
            }
            if ( ocrFields.getGrandparentsDiedUnderEighteen() != null ) {
                grantOfRepresentationData.setGrandparentsDiedUnderEighteen( ocrFields.getGrandparentsDiedUnderEighteen() );
            }
            if ( ocrFields.getApplyingAsAnAttorney() != null ) {
                grantOfRepresentationData.setApplyingAsAnAttorney( oCRFieldYesOrNoMapper.toYesOrNo( ocrFields.getApplyingAsAnAttorney() ) );
            }
            if ( ocrFields.getWholeBloodUnclesAndAuntsSurvivedOverEighteen() != null ) {
                grantOfRepresentationData.setWholeBloodUnclesAndAuntsSurvivedOverEighteen( ocrFields.getWholeBloodUnclesAndAuntsSurvivedOverEighteen() );
            }
            if ( ocrFields.getWholeBloodCousinsSurvivedUnderEighteen() != null ) {
                grantOfRepresentationData.setWholeBloodCousinsSurvivedUnderEighteen( ocrFields.getWholeBloodCousinsSurvivedUnderEighteen() );
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
            if ( ocrFields.getIhtGrossValue() != null ) {
                grantOfRepresentationData.setIhtGrossValue( oCRFieldIhtMoneyMapper.poundsToPennies( ocrFields.getIhtGrossValue() ) );
            }
            if ( ocrFields.getIhtFormId() != null ) {
                grantOfRepresentationData.setIhtFormId( oCRFieldIhtMoneyMapper.ihtFormType( ocrFields.getIhtFormId() ) );
            }
            if ( ocrFields.getWholeBloodSiblingsDiedOverEighteen() != null ) {
                grantOfRepresentationData.setWholeBloodSiblingsDiedOverEighteen( ocrFields.getWholeBloodSiblingsDiedOverEighteen() );
            }
            if ( ocrFields.getHalfBloodNeicesAndNephewsOverEighteen() != null ) {
                grantOfRepresentationData.setHalfBloodNeicesAndNephewsOverEighteen( ocrFields.getHalfBloodNeicesAndNephewsOverEighteen() );
            }
            if ( ocrFields.getSolsFeeAccountNumber() != null ) {
                grantOfRepresentationData.setSolsFeeAccountNumber( ocrFields.getSolsFeeAccountNumber() );
            }
            if ( ocrFields.getFeeForCopiesPaperForm() != null ) {
                grantOfRepresentationData.setFeeForCopiesPaperForm( oCRFieldIhtMoneyMapper.poundsToPennies( ocrFields.getFeeForCopiesPaperForm() ) );
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
            if ( ocrFields.getForeignAssetEstateValue() != null ) {
                grantOfRepresentationData.setForeignAssetEstateValue( oCRFieldIhtMoneyMapper.poundsToPennies( ocrFields.getForeignAssetEstateValue() ) );
            }
            if ( ocrFields.getEpaRegistered() != null ) {
                grantOfRepresentationData.setEpaRegistered( oCRFieldYesOrNoMapper.toYesOrNo( ocrFields.getEpaRegistered() ) );
            }
            if ( ocrFields.getApplicationFeePaperForm() != null ) {
                grantOfRepresentationData.setApplicationFeePaperForm( oCRFieldIhtMoneyMapper.poundsToPennies( ocrFields.getApplicationFeePaperForm() ) );
            }
            if ( ocrFields.getDeceasedDomicileInEngWales() != null ) {
                grantOfRepresentationData.setDeceasedDomicileInEngWales( oCRFieldYesOrNoMapper.toYesOrNo( ocrFields.getDeceasedDomicileInEngWales() ) );
            }
            if ( ocrFields.getPrimaryApplicantPhoneNumber() != null ) {
                grantOfRepresentationData.setPrimaryApplicantPhoneNumber( ocrFields.getPrimaryApplicantPhoneNumber() );
            }
            if ( ocrFields.getSolsSolicitorIsApplying() != null ) {
                grantOfRepresentationData.setSolsSolicitorIsApplying( oCRFieldYesOrNoMapper.toYesOrNo( ocrFields.getSolsSolicitorIsApplying() ) );
            }
            if ( ocrFields.getHalfBloodUnclesAndAuntsDiedOverEighteen() != null ) {
                grantOfRepresentationData.setHalfBloodUnclesAndAuntsDiedOverEighteen( ocrFields.getHalfBloodUnclesAndAuntsDiedOverEighteen() );
            }
            if ( ocrFields.getCourtOfDecree() != null ) {
                grantOfRepresentationData.setCourtOfDecree( ocrFields.getCourtOfDecree() );
            }
            if ( ocrFields.getForeignAsset() != null ) {
                grantOfRepresentationData.setForeignAsset( oCRFieldYesOrNoMapper.toYesOrNo( ocrFields.getForeignAsset() ) );
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
            if ( ocrFields.getHalfBloodUnclesAndAuntsSurvivedOverEighteen() != null ) {
                grantOfRepresentationData.setHalfBloodUnclesAndAuntsSurvivedOverEighteen( ocrFields.getHalfBloodUnclesAndAuntsSurvivedOverEighteen() );
            }
            if ( ocrFields.getMentalCapacity() != null ) {
                grantOfRepresentationData.setMentalCapacity( oCRFieldYesOrNoMapper.toYesOrNo( ocrFields.getMentalCapacity() ) );
            }
            if ( ocrFields.getAdopted() != null ) {
                grantOfRepresentationData.setAdopted( oCRFieldYesOrNoMapper.toYesOrNo( ocrFields.getAdopted() ) );
            }
            grantOfRepresentationData.setDeceasedAddress( oCRFieldAddressMapper.toDeceasedAddress( ocrFields ) );
            if ( ocrFields.getHalfBloodSiblingsDiedOverEighteen() != null ) {
                grantOfRepresentationData.setHalfBloodSiblingsDiedOverEighteen( ocrFields.getHalfBloodSiblingsDiedOverEighteen() );
            }
            if ( ocrFields.getDeceasedMartialStatus() != null ) {
                grantOfRepresentationData.setDeceasedMaritalStatus( oCRFieldMartialStatusMapper.toMartialStatus( ocrFields.getDeceasedMartialStatus() ) );
            }
            if ( ocrFields.getCourtOfProtection() != null ) {
                grantOfRepresentationData.setCourtOfProtection( oCRFieldYesOrNoMapper.toYesOrNo( ocrFields.getCourtOfProtection() ) );
            }
            List<CollectionMember<ExecutorNotApplying>> list2 = oCRFieldAdditionalExecutorsNotApplyingMapper.toAdditionalCollectionMember( ocrFields );
            if ( list2 != null ) {
                grantOfRepresentationData.setExecutorsNotApplying( list2 );
            }
            else {
                grantOfRepresentationData.setExecutorsNotApplying( null );
            }
            if ( ocrFields.getDeceasedDateOfDeath() != null ) {
                grantOfRepresentationData.setDeceasedDateOfDeath( oCRFieldDefaultLocalDateFieldMapper.toDefaultDateFieldMember( ocrFields.getDeceasedDateOfDeath() ) );
            }
            if ( ocrFields.getPrimaryApplicantForenames() != null ) {
                grantOfRepresentationData.setPrimaryApplicantForenames( ocrFields.getPrimaryApplicantForenames() );
            }
            if ( ocrFields.getNotifiedApplicants() != null ) {
                grantOfRepresentationData.setNotifiedApplicants( oCRFieldYesOrNoMapper.toYesOrNo( ocrFields.getNotifiedApplicants() ) );
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
            if ( ocrFields.getEpaOrLpa() != null ) {
                grantOfRepresentationData.setEpaOrLpa( oCRFieldYesOrNoMapper.toYesOrNo( ocrFields.getEpaOrLpa() ) );
            }
            if ( ocrFields.getHalfBloodNeicesAndNephewsUnderEighteen() != null ) {
                grantOfRepresentationData.setHalfBloodNeicesAndNephewsUnderEighteen( ocrFields.getHalfBloodNeicesAndNephewsUnderEighteen() );
            }
            if ( ocrFields.getDomicilityCountry() != null ) {
                grantOfRepresentationData.setDomicilityCountry( ocrFields.getDomicilityCountry() );
            }
            if ( ocrFields.getPrimaryApplicantHasAlias() != null ) {
                grantOfRepresentationData.setPrimaryApplicantHasAlias( oCRFieldYesOrNoMapper.toYesOrNo( ocrFields.getPrimaryApplicantHasAlias() ) );
            }
            if ( ocrFields.getPrimaryApplicantSecondPhoneNumber() != null ) {
                grantOfRepresentationData.setPrimaryApplicantSecondPhoneNumber( ocrFields.getPrimaryApplicantSecondPhoneNumber() );
            }
            if ( ocrFields.getGrandparentsDiedOverEighteen() != null ) {
                grantOfRepresentationData.setGrandparentsDiedOverEighteen( ocrFields.getGrandparentsDiedOverEighteen() );
            }
            if ( ocrFields.getDateOfDivorcedCPJudicially() != null ) {
                grantOfRepresentationData.setDateOfDivorcedCPJudicially( oCRFieldDefaultLocalDateFieldMapper.toDefaultDateFieldMember( ocrFields.getDateOfDivorcedCPJudicially() ) );
            }
            if ( ocrFields.getDeceasedAnyOtherNames() != null ) {
                grantOfRepresentationData.setDeceasedAnyOtherNames( oCRFieldYesOrNoMapper.toYesOrNo( ocrFields.getDeceasedAnyOtherNames() ) );
            }
            if ( ocrFields.getDateOfMarriageOrCP() != null ) {
                grantOfRepresentationData.setDateOfMarriageOrCP( oCRFieldDefaultLocalDateFieldMapper.toDefaultDateFieldMember( ocrFields.getDateOfMarriageOrCP() ) );
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
            if ( ocrFields.getWillDate() != null ) {
                grantOfRepresentationData.setWillDate( oCRFieldDefaultLocalDateFieldMapper.toDefaultDateFieldMember( ocrFields.getWillDate() ) );
            }
            if ( ocrFields.getWillsOutsideOfUK() != null ) {
                grantOfRepresentationData.setWillsOutsideOfUK( oCRFieldYesOrNoMapper.toYesOrNo( ocrFields.getWillsOutsideOfUK() ) );
            }
            grantOfRepresentationData.setPrimaryApplicantAddress( oCRFieldAddressMapper.toPrimaryApplicantAddress( ocrFields ) );
            if ( ocrFields.getSolsSolicitorEmail() != null ) {
                grantOfRepresentationData.setSolsSolicitorEmail( ocrFields.getSolsSolicitorEmail() );
            }
            if ( ocrFields.getIhtNetValue() != null ) {
                grantOfRepresentationData.setIhtNetValue( oCRFieldIhtMoneyMapper.poundsToPennies( ocrFields.getIhtNetValue() ) );
            }
            if ( ocrFields.getPrimaryApplicantRelationshipToDeceased() != null ) {
                grantOfRepresentationData.setPaRelationshipToDeceasedOther( oCRFieldRelationshipMapper.toRelationshipOther( ocrFields.getPrimaryApplicantRelationshipToDeceased() ) );
            }
            if ( ocrFields.getDeceasedMarriedAfterWillOrCodicilDate() != null ) {
                grantOfRepresentationData.setDeceasedMarriedAfterWillOrCodicilDate( oCRFieldYesOrNoMapper.toYesOrNo( ocrFields.getDeceasedMarriedAfterWillOrCodicilDate() ) );
            }
            if ( ocrFields.getWillGiftUnderEighteen() != null ) {
                grantOfRepresentationData.setWillGiftUnderEighteen( oCRFieldYesOrNoMapper.toYesOrNo( ocrFields.getWillGiftUnderEighteen() ) );
            }
            if ( ocrFields.getExtraCopiesOfGrant() != null ) {
                grantOfRepresentationData.setExtraCopiesOfGrant( oCRFieldNumberMapper.stringToLong( ocrFields.getExtraCopiesOfGrant() ) );
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
            if ( ocrFields.getBilingualGrantRequested() != null ) {
                grantOfRepresentationData.setLanguagePreferenceWelsh( oCRFieldYesOrNoMapper.toYesOrNo( ocrFields.getBilingualGrantRequested() ) );
            }
            if ( ocrFields.getIhtFormCompletedOnline() != null ) {
                grantOfRepresentationData.setIhtFormCompletedOnline( oCRFieldYesOrNoMapper.toYesOrNo( ocrFields.getIhtFormCompletedOnline() ) );
            }
            if ( ocrFields.getPrimaryApplicantSurname() != null ) {
                grantOfRepresentationData.setPrimaryApplicantSurname( ocrFields.getPrimaryApplicantSurname() );
            }
            if ( ocrFields.getSpouseOrPartner() != null ) {
                grantOfRepresentationData.setSpouseOrPartner( oCRFieldYesOrNoMapper.toYesOrNo( ocrFields.getSpouseOrPartner() ) );
            }
            if ( ocrFields.getSolsSolicitorAppReference() != null ) {
                grantOfRepresentationData.setSolsSolicitorAppReference( ocrFields.getSolsSolicitorAppReference() );
            }
            if ( ocrFields.getOutsideUKGrantCopies() != null ) {
                grantOfRepresentationData.setOutsideUkGrantCopies( oCRFieldNumberMapper.stringToLong( ocrFields.getOutsideUKGrantCopies() ) );
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
        grantOfRepresentationData.setGrandChildrenSurvivedUnderEighteenText( ocrFields.getGrandChildrenSurvivedUnderEighteen() );
        grantOfRepresentationData.setChildrenOverEighteenSurvivedText( ocrFields.getChildrenOverEighteenSurvived() );
        grantOfRepresentationData.setChildrenUnderEighteenSurvivedText( ocrFields.getChildrenUnderEighteenSurvived() );
        grantOfRepresentationData.setGrandChildrenSurvivedOverEighteenText( ocrFields.getGrandChildrenSurvivedOverEighteen() );
        grantOfRepresentationData.setChildrenDiedUnderEighteenText( ocrFields.getChildrenDiedUnderEighteen() );
        grantOfRepresentationData.setPaperForm( Boolean.TRUE );
        grantOfRepresentationData.setChildrenDiedOverEighteenText( ocrFields.getChildrenDiedOverEighteen() );

        setDomicilityIHTCert( grantOfRepresentationData, ocrFields );
        setSolsPaymentMethod( grantOfRepresentationData, ocrFields );
        setSolsSolicitorRepresentativeName( grantOfRepresentationData, ocrFields );
        clearIhtFormOrReferenceIfNotSelected( grantOfRepresentationData, ocrFields );
        setDerivedFamilyBooleans( grantOfRepresentationData, ocrFields, grantType );
        setApplyingAsAnAttorneyBoolean( grantOfRepresentationData, ocrFields );

        return grantOfRepresentationData;
    }
}
