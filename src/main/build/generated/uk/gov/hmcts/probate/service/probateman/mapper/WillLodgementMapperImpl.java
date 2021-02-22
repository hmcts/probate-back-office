package uk.gov.hmcts.probate.service.probateman.mapper;

import java.util.List;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.probateman.LegacyCaseType;
import uk.gov.hmcts.probate.model.probateman.WillLodgement;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.FullAliasName;
import uk.gov.hmcts.reform.probate.model.cases.willlodgement.WillLodgementData;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-02-22T16:15:35+0000",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 11.0.10 (Ubuntu)"
)
@Component
public class WillLodgementMapperImpl implements WillLodgementMapper {

    @Autowired
    private FullAliasNameMapper fullAliasNameMapper;
    @Autowired
    private LegacyCaseViewUrlMapper legacyCaseViewUrlMapper;

    @Override
    public WillLodgementData toCcdData(WillLodgement willLodgement) {
        if ( willLodgement == null ) {
            return null;
        }

        WillLodgementData willLodgementData = new WillLodgementData();

        if ( willLodgement.getRkNumber() != null ) {
            willLodgementData.setRecordId( willLodgement.getRkNumber() );
        }
        if ( willLodgement.getDateOfDeath1() != null ) {
            willLodgementData.setDeceasedDateOfDeath( willLodgement.getDateOfDeath1() );
        }
        if ( willLodgement.getDateOfBirth() != null ) {
            willLodgementData.setDeceasedDateOfBirth( willLodgement.getDateOfBirth() );
        }
        if ( willLodgement.getId() != null ) {
            willLodgementData.setLegacyId( String.valueOf( willLodgement.getId() ) );
        }
        willLodgementData.setLegacyCaseViewUrl( legacyCaseViewUrlMapper.toLegacyCaseViewUrl( willLodgement ) );
        if ( willLodgement.getDeceasedForenames() != null ) {
            willLodgementData.setDeceasedForenames( willLodgement.getDeceasedForenames() );
        }
        if ( willLodgement.getDeceasedSurname() != null ) {
            willLodgementData.setDeceasedSurname( willLodgement.getDeceasedSurname() );
        }
        List<CollectionMember<FullAliasName>> list = fullAliasNameMapper.toFullAliasNameMember( willLodgement.getAliasNames() );
        if ( list != null ) {
            willLodgementData.setDeceasedFullAliasNameList( list );
        }
        else {
            willLodgementData.setDeceasedFullAliasNameList( null );
        }

        willLodgementData.setDeceasedAnyOtherNames( willLodgement.getAliasNames() == null ? false : true );
        willLodgementData.setLegacyType( LegacyCaseType.WILL_LODGEMENT.getName() );

        return willLodgementData;
    }
}
