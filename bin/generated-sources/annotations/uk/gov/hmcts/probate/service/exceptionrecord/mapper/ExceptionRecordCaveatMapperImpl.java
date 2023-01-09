package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import javax.annotation.Generated;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.reform.probate.model.cases.ApplicationType;
import uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-12-15T11:02:50+0000",
    comments = "version: 1.2.0.Final, compiler: Eclipse JDT (IDE) 1.4.200.v20221012-0724, environment: Java 17.0.5 (Eclipse Adoptium)"
)
@Component
public class ExceptionRecordCaveatMapperImpl implements ExceptionRecordCaveatMapper {

    @Autowired
    private ApplicationTypeMapper applicationTypeMapper;
    @Autowired
    private OCRFieldAddressMapper oCRFieldAddressMapper;
    @Autowired
    private OCRFieldDefaultLocalDateFieldMapper oCRFieldDefaultLocalDateFieldMapper;
    @Autowired
    private OCRFieldProbateFeeMapper oCRFieldProbateFeeMapper;
    @Autowired
    private OCRFieldProbateFeeNotIncludedReasonMapper oCRFieldProbateFeeNotIncludedReasonMapper;
    @Autowired
    private OCRFieldYesOrNoMapper oCRFieldYesOrNoMapper;

    @Override
    public CaveatData toCcdData(ExceptionRecordOCRFields ocrFields) {
        if ( ocrFields == null ) {
            return null;
        }

        CaveatData caveatData = new CaveatData();

        caveatData.setCaveatorAddress( oCRFieldAddressMapper.toCaveatorAddress( ocrFields ) );
        caveatData.setDeceasedAddress( oCRFieldAddressMapper.toDeceasedAddress( ocrFields ) );
        if ( ocrFields.getDeceasedAnyOtherNames() != null ) {
            caveatData.setDeceasedAnyOtherNames( oCRFieldYesOrNoMapper.toYesOrNo( ocrFields.getDeceasedAnyOtherNames() ) );
        }
        if ( ocrFields.getHelpWithFeesReference() != null ) {
            caveatData.setHelpWithFeesReference( ocrFields.getHelpWithFeesReference() );
        }
        caveatData.setApplicationType( applicationTypeMapper.toApplicationTypeCaveat( ocrFields ) );
        if ( ocrFields.getSolsSolicitorPhoneNumber() != null ) {
            caveatData.setSolsSolicitorPhoneNumber( ocrFields.getSolsSolicitorPhoneNumber() );
        }
        if ( ocrFields.getProbateFeeNotIncludedReason() != null ) {
            caveatData.setProbateFeeNotIncludedReason( oCRFieldProbateFeeNotIncludedReasonMapper.toProbateFeeNotIncludedReason( ocrFields.getProbateFeeNotIncludedReason() ) );
        }
        if ( ocrFields.getSolsFeeAccountNumber() != null ) {
            caveatData.setSolsFeeAccountNumber( ocrFields.getSolsFeeAccountNumber() );
        }
        if ( ocrFields.getDeceasedDateOfBirth() != null ) {
            caveatData.setDeceasedDateOfBirth( oCRFieldDefaultLocalDateFieldMapper.toDefaultDateFieldMember( ocrFields.getDeceasedDateOfBirth() ) );
        }
        if ( ocrFields.getSolsSolicitorFirmName() != null ) {
            caveatData.setSolsSolicitorFirmName( ocrFields.getSolsSolicitorFirmName() );
        }
        if ( ocrFields.getDeceasedDateOfDeath() != null ) {
            caveatData.setDeceasedDateOfDeath( oCRFieldDefaultLocalDateFieldMapper.toDefaultDateFieldMember( ocrFields.getDeceasedDateOfDeath() ) );
        }
        if ( ocrFields.getSolsSolicitorRepresentativeName() != null ) {
            caveatData.setSolsSolicitorRepresentativeName( ocrFields.getSolsSolicitorRepresentativeName() );
        }
        if ( ocrFields.getProbateFeeNotIncludedExplanation() != null ) {
            caveatData.setProbateFeeNotIncludedExplanation( ocrFields.getProbateFeeNotIncludedExplanation() );
        }
        if ( ocrFields.getProbateFee() != null ) {
            caveatData.setProbateFee( oCRFieldProbateFeeMapper.toProbateFee( ocrFields.getProbateFee() ) );
        }
        if ( ocrFields.getDeceasedSurname() != null ) {
            caveatData.setDeceasedSurname( ocrFields.getDeceasedSurname() );
        }
        if ( ocrFields.getDxNumber() != null ) {
            caveatData.setDxNumber( ocrFields.getDxNumber() );
        }
        if ( ocrFields.getProbateFeeAccountReference() != null ) {
            caveatData.setProbateFeeAccountReference( ocrFields.getProbateFeeAccountReference() );
        }
        if ( ocrFields.getCaveatorEmailAddress() != null ) {
            caveatData.setCaveatorEmailAddress( ocrFields.getCaveatorEmailAddress() );
        }
        if ( ocrFields.getDeceasedForenames() != null ) {
            caveatData.setDeceasedForenames( ocrFields.getDeceasedForenames() );
        }
        if ( ocrFields.getCaveatorPhoneNumber() != null ) {
            caveatData.setCaveatorPhoneNumber( ocrFields.getCaveatorPhoneNumber() );
        }
        if ( ocrFields.getProbateFeeAccountNumber() != null ) {
            caveatData.setProbateFeeAccountNumber( ocrFields.getProbateFeeAccountNumber() );
        }
        if ( ocrFields.getBilingualCorrespondenceRequested() != null ) {
            caveatData.setLanguagePreferenceWelsh( oCRFieldYesOrNoMapper.toYesOrNo( ocrFields.getBilingualCorrespondenceRequested() ) );
        }
        if ( ocrFields.getCaveatorForenames() != null ) {
            caveatData.setCaveatorForenames( ocrFields.getCaveatorForenames() );
        }
        if ( ocrFields.getSolsSolicitorAppReference() != null ) {
            caveatData.setSolsSolicitorAppReference( ocrFields.getSolsSolicitorAppReference() );
        }
        if ( ocrFields.getPractitionerAcceptsServiceByEmail() != null ) {
            caveatData.setPractitionerAcceptsServiceByEmail( oCRFieldYesOrNoMapper.toYesOrNo( ocrFields.getPractitionerAcceptsServiceByEmail() ) );
        }
        if ( ocrFields.getCaveatorSurnames() != null ) {
            caveatData.setCaveatorSurname( ocrFields.getCaveatorSurnames() );
        }

        caveatData.setCaveatRaisedEmailNotificationRequested( Boolean.TRUE );
        caveatData.setPaperForm( Boolean.TRUE );

        setLanguagePreferenceWelsh( caveatData, ocrFields );
        setSolsPaymentMethod( caveatData, ocrFields );
        setSolsSolicitorEmail( caveatData, ocrFields );

        return caveatData;
    }
}
