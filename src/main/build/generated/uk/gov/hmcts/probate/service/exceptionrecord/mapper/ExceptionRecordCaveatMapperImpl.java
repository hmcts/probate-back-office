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
    date = "2021-02-22T16:15:35+0000",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 11.0.10 (Ubuntu)"
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
        caveatData.setApplicationType( applicationTypeMapper.toApplicationTypeCaveat( ocrFields ) );
        if ( ocrFields.getSolsSolicitorPhoneNumber() != null ) {
            caveatData.setSolsSolicitorPhoneNumber( ocrFields.getSolsSolicitorPhoneNumber() );
        }
        if ( ocrFields.getSolsFeeAccountNumber() != null ) {
            caveatData.setSolsFeeAccountNumber( ocrFields.getSolsFeeAccountNumber() );
        }
        if ( ocrFields.getCaveatorEmailAddress() != null ) {
            caveatData.setCaveatorEmailAddress( ocrFields.getCaveatorEmailAddress() );
        }
        if ( ocrFields.getDeceasedDateOfBirth() != null ) {
            caveatData.setDeceasedDateOfBirth( oCRFieldDefaultLocalDateFieldMapper.toDefaultDateFieldMember( ocrFields.getDeceasedDateOfBirth() ) );
        }
        if ( ocrFields.getDeceasedForenames() != null ) {
            caveatData.setDeceasedForenames( ocrFields.getDeceasedForenames() );
        }
        if ( ocrFields.getSolsSolicitorFirmName() != null ) {
            caveatData.setSolsSolicitorFirmName( ocrFields.getSolsSolicitorFirmName() );
        }
        if ( ocrFields.getDeceasedDateOfDeath() != null ) {
            caveatData.setDeceasedDateOfDeath( oCRFieldDefaultLocalDateFieldMapper.toDefaultDateFieldMember( ocrFields.getDeceasedDateOfDeath() ) );
        }
        if ( ocrFields.getCaveatorForenames() != null ) {
            caveatData.setCaveatorForenames( ocrFields.getCaveatorForenames() );
        }
        if ( ocrFields.getSolsSolicitorAppReference() != null ) {
            caveatData.setSolsSolicitorAppReference( ocrFields.getSolsSolicitorAppReference() );
        }
        if ( ocrFields.getCaveatorSurnames() != null ) {
            caveatData.setCaveatorSurname( ocrFields.getCaveatorSurnames() );
        }
        if ( ocrFields.getDeceasedSurname() != null ) {
            caveatData.setDeceasedSurname( ocrFields.getDeceasedSurname() );
        }

        caveatData.setCaveatRaisedEmailNotificationRequested( Boolean.TRUE );
        caveatData.setPaperForm( Boolean.TRUE );
        caveatData.setLanguagePreferenceWelsh( Boolean.FALSE );

        setSolsPaymentMethod( caveatData, ocrFields );
        setSolsSolicitorEmail( caveatData, ocrFields );
        setSolsSolicitorRepresentativeName( caveatData, ocrFields );

        return caveatData;
    }
}
