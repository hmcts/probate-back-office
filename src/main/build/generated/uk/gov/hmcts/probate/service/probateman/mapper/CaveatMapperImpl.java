package uk.gov.hmcts.probate.service.probateman.mapper;

import java.util.List;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.probateman.Caveat;
import uk.gov.hmcts.probate.model.probateman.LegacyCaseType;
import uk.gov.hmcts.reform.probate.model.cases.Address;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.FullAliasName;
import uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-02-22T16:15:35+0000",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 11.0.10 (Ubuntu)"
)
@Component
public class CaveatMapperImpl implements CaveatMapper {

    @Autowired
    private FullAliasNameMapper fullAliasNameMapper;
    @Autowired
    private LegacyCaseViewUrlMapper legacyCaseViewUrlMapper;

    @Override
    public CaveatData toCcdData(Caveat caveat) {
        if ( caveat == null ) {
            return null;
        }

        CaveatData caveatData = new CaveatData();

        caveatData.setCaveatorAddress( caveatToAddress( caveat ) );
        if ( caveat.getDateOfBirth() != null ) {
            caveatData.setDeceasedDateOfBirth( caveat.getDateOfBirth() );
        }
        caveatData.setLegacyCaseViewUrl( legacyCaseViewUrlMapper.toLegacyCaseViewUrl( caveat ) );
        if ( caveat.getDeceasedForenames() != null ) {
            caveatData.setDeceasedForenames( caveat.getDeceasedForenames() );
        }
        if ( caveat.getCavExpiryDate() != null ) {
            caveatData.setExpiryDate( caveat.getCavExpiryDate() );
        }
        if ( caveat.getCaveatNumber() != null ) {
            caveatData.setRecordId( caveat.getCaveatNumber() );
        }
        if ( caveat.getDateOfDeath() != null ) {
            caveatData.setDeceasedDateOfDeath( caveat.getDateOfDeath() );
        }
        if ( caveat.getCaveatorForenames() != null ) {
            caveatData.setCaveatorForenames( caveat.getCaveatorForenames() );
        }
        if ( caveat.getId() != null ) {
            caveatData.setLegacyId( String.valueOf( caveat.getId() ) );
        }
        if ( caveat.getCaveatDateOfEntry() != null ) {
            caveatData.setApplicationSubmittedDate( caveat.getCaveatDateOfEntry() );
        }
        if ( caveat.getCaveatorSurname() != null ) {
            caveatData.setCaveatorSurname( caveat.getCaveatorSurname() );
        }
        if ( caveat.getDeceasedSurname() != null ) {
            caveatData.setDeceasedSurname( caveat.getDeceasedSurname() );
        }
        List<CollectionMember<FullAliasName>> list = fullAliasNameMapper.toFullAliasNameMember( caveat.getAliasNames() );
        if ( list != null ) {
            caveatData.setDeceasedFullAliasNameList( list );
        }
        else {
            caveatData.setDeceasedFullAliasNameList( null );
        }

        caveatData.setDeceasedAnyOtherNames( caveat.getAliasNames() == null ? false : true );
        caveatData.setLegacyType( LegacyCaseType.CAVEAT.getName() );

        return caveatData;
    }

    protected Address caveatToAddress(Caveat caveat) {
        if ( caveat == null ) {
            return null;
        }

        Address address = new Address();

        if ( caveat.getCavServiceAddress() != null ) {
            address.setAddressLine1( caveat.getCavServiceAddress() );
        }

        return address;
    }
}
