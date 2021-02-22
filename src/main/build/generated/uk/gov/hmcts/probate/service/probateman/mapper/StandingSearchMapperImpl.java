package uk.gov.hmcts.probate.service.probateman.mapper;

import java.util.List;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.probateman.LegacyCaseType;
import uk.gov.hmcts.probate.model.probateman.StandingSearch;
import uk.gov.hmcts.reform.probate.model.cases.Address;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.FullAliasName;
import uk.gov.hmcts.reform.probate.model.cases.standingsearch.StandingSearchData;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-02-22T16:15:35+0000",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 11.0.10 (Ubuntu)"
)
@Component
public class StandingSearchMapperImpl implements StandingSearchMapper {

    @Autowired
    private FullAliasNameMapper fullAliasNameMapper;
    @Autowired
    private LegacyCaseViewUrlMapper legacyCaseViewUrlMapper;

    @Override
    public StandingSearchData toCcdData(StandingSearch standingSearch) {
        if ( standingSearch == null ) {
            return null;
        }

        StandingSearchData standingSearchData = new StandingSearchData();

        standingSearchData.setDeceasedAddress( standingSearchToAddress( standingSearch ) );
        standingSearchData.setApplicantAddress( standingSearchToAddress1( standingSearch ) );
        if ( standingSearch.getSsApplicantForename() != null ) {
            standingSearchData.setApplicantForenames( standingSearch.getSsApplicantForename() );
        }
        if ( standingSearch.getDateOfBirth() != null ) {
            standingSearchData.setDeceasedDateOfBirth( standingSearch.getDateOfBirth() );
        }
        standingSearchData.setLegacyCaseViewUrl( legacyCaseViewUrlMapper.toLegacyCaseViewUrl( standingSearch ) );
        if ( standingSearch.getDeceasedForenames() != null ) {
            standingSearchData.setDeceasedForenames( standingSearch.getDeceasedForenames() );
        }
        if ( standingSearch.getSsDateOfExpiry() != null ) {
            standingSearchData.setExpiryDate( standingSearch.getSsDateOfExpiry() );
        }
        if ( standingSearch.getSsNumber() != null ) {
            standingSearchData.setRecordId( standingSearch.getSsNumber() );
        }
        if ( standingSearch.getDateOfDeath1() != null ) {
            standingSearchData.setDeceasedDateOfDeath( standingSearch.getDateOfDeath1() );
        }
        if ( standingSearch.getSsApplicantSurname() != null ) {
            standingSearchData.setApplicantSurname( standingSearch.getSsApplicantSurname() );
        }
        if ( standingSearch.getId() != null ) {
            standingSearchData.setLegacyId( String.valueOf( standingSearch.getId() ) );
        }
        if ( standingSearch.getSsDateOfEntry() != null ) {
            standingSearchData.setApplicationSubmittedDate( standingSearch.getSsDateOfEntry() );
        }
        if ( standingSearch.getDeceasedSurname() != null ) {
            standingSearchData.setDeceasedSurname( standingSearch.getDeceasedSurname() );
        }
        List<CollectionMember<FullAliasName>> list = fullAliasNameMapper.toFullAliasNameMember( standingSearch.getAliasNames() );
        if ( list != null ) {
            standingSearchData.setDeceasedFullAliasNameList( list );
        }
        else {
            standingSearchData.setDeceasedFullAliasNameList( null );
        }

        standingSearchData.setDeceasedAnyOtherNames( standingSearch.getAliasNames() == null ? false : true );
        standingSearchData.setLegacyType( LegacyCaseType.STANDING_SEARCH.getName() );

        return standingSearchData;
    }

    protected Address standingSearchToAddress(StandingSearch standingSearch) {
        if ( standingSearch == null ) {
            return null;
        }

        Address address = new Address();

        if ( standingSearch.getDeceasedAddress() != null ) {
            address.setAddressLine1( standingSearch.getDeceasedAddress() );
        }

        return address;
    }

    protected Address standingSearchToAddress1(StandingSearch standingSearch) {
        if ( standingSearch == null ) {
            return null;
        }

        Address address = new Address();

        if ( standingSearch.getApplicantAddress() != null ) {
            address.setAddressLine1( standingSearch.getApplicantAddress() );
        }

        return address;
    }
}
