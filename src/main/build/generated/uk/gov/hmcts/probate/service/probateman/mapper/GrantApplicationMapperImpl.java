package uk.gov.hmcts.probate.service.probateman.mapper;

import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.probateman.GrantApplication;
import uk.gov.hmcts.probate.model.probateman.LegacyCaseType;
import uk.gov.hmcts.reform.probate.model.cases.Address;
import uk.gov.hmcts.reform.probate.model.cases.ApplicationType;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-02-22T16:15:35+0000",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 11.0.10 (Ubuntu)"
)
@Component
public class GrantApplicationMapperImpl implements GrantApplicationMapper {

    @Autowired
    private SolsAliasNameMapper solsAliasNameMapper;
    @Autowired
    private AdditionalExecutorMapper additionalExecutorMapper;
    @Autowired
    private LegacyCaseViewUrlMapper legacyCaseViewUrlMapper;

    @Override
    public GrantOfRepresentationData toCcdData(GrantApplication grantApplication) {
        if ( grantApplication == null ) {
            return null;
        }

        GrantOfRepresentationData grantOfRepresentationData = new GrantOfRepresentationData();

        grantOfRepresentationData.setDeceasedAddress( grantApplicationToAddress( grantApplication ) );
        grantOfRepresentationData.setDeceasedDateOfBirth( grantApplication.getDateOfBirth() );
        grantOfRepresentationData.setSolsDeceasedAliasNamesList( solsAliasNameMapper.toCollectionMember( grantApplication.getAliasNames() ) );
        grantOfRepresentationData.setLegacyCaseViewUrl( legacyCaseViewUrlMapper.toLegacyCaseViewUrl( grantApplication ) );
        grantOfRepresentationData.setDeceasedForenames( grantApplication.getDeceasedForenames() );
        grantOfRepresentationData.setRecordId( grantApplication.getProbateNumber() );
        grantOfRepresentationData.setDeceasedDateOfDeath( grantApplication.getDateOfDeath1() );
        grantOfRepresentationData.setExecutorsApplying( additionalExecutorMapper.toAdditionalCollectionMember( grantApplication ) );
        if ( grantApplication.getId() != null ) {
            grantOfRepresentationData.setLegacyId( String.valueOf( grantApplication.getId() ) );
        }
        grantOfRepresentationData.setApplicationSubmittedDate( grantApplication.getAppReceivedDate() );
        grantOfRepresentationData.setDeceasedSurname( grantApplication.getDeceasedSurname() );
        grantOfRepresentationData.setPrimaryApplicantAddress( grantApplicationToAddress1( grantApplication ) );
        grantOfRepresentationData.setSolsSolicitorAddress( grantApplicationToAddress2( grantApplication ) );

        grantOfRepresentationData.setApplicationType( grantApplication.getSolicitorReference() == null ? ApplicationType.PERSONAL : ApplicationType.SOLICITORS );
        grantOfRepresentationData.setSolsSolicitorFirmName( grantApplication.getSolicitorReference() == null ? null : grantApplication.getApplicantForenames() + ' ' + grantApplication.getApplicantSurname() );
        grantOfRepresentationData.setLegacyType( LegacyCaseType.GRANT_OF_REPRESENTATION.getName() );
        grantOfRepresentationData.setIhtGrossValue( grantApplication.getGrossEstateValue() == null ? null : grantApplication.getGrossEstateValue() * 100 );
        grantOfRepresentationData.setPrimaryApplicantForenames( grantApplication.getSolicitorReference() == null ? grantApplication.getApplicantForenames() : grantApplication.getGrantee1Forenames() );
        grantOfRepresentationData.setPrimaryApplicantSurname( grantApplication.getSolicitorReference() == null ? grantApplication.getApplicantSurname() : grantApplication.getGrantee1Surname() );
        grantOfRepresentationData.setSolsSolicitorAppReference( grantApplication.getSolicitorReference() == null ? null : grantApplication.getSolicitorReference() );
        grantOfRepresentationData.setIhtNetValue( grantApplication.getNetEstateValue() == null ? null : grantApplication.getNetEstateValue() * 100 );
        grantOfRepresentationData.setGrantType( GrantType.GRANT_OF_PROBATE );

        return grantOfRepresentationData;
    }

    protected Address grantApplicationToAddress(GrantApplication grantApplication) {
        if ( grantApplication == null ) {
            return null;
        }

        Address address = new Address();

        address.setAddressLine1( grantApplication.getDeceasedAddress() );

        return address;
    }

    protected Address grantApplicationToAddress1(GrantApplication grantApplication) {
        if ( grantApplication == null ) {
            return null;
        }

        Address address = new Address();

        address.setAddressLine1( grantApplication.getSolicitorReference() == null ? grantApplication.getApplicantAddress() :  grantApplication.getGrantee1Address() );

        return address;
    }

    protected Address grantApplicationToAddress2(GrantApplication grantApplication) {
        if ( grantApplication == null ) {
            return null;
        }

        Address address = new Address();

        address.setAddressLine1( grantApplication.getSolicitorReference() == null ? null : grantApplication.getApplicantAddress() );

        return address;
    }
}
