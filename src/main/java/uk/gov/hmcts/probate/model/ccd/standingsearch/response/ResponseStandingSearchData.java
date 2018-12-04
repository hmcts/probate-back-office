package uk.gov.hmcts.probate.model.ccd.standingsearch.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.caveat.CavAddress;
import uk.gov.hmcts.probate.model.ccd.caveat.CavFullAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;

import java.util.List;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseStandingSearchData {

    private final ApplicationType applicationType;
    private final String registryLocation;

    private final String deceasedForenames;
    private final String deceasedSurname;
    private final String deceasedDateOfDeath;
    private final String deceasedDateOfBirth;
    private final String deceasedAnyOtherNames;
    private final List<CollectionMember<CavFullAliasName>> deceasedFullAliasNameList;
    private final CavAddress deceasedAddress;

    private final String applicantForenames;
    private final String applicantSurname;
    private final String applicantEmailAddress;
    private final CavAddress applicantAddress;

    private final Long numberOfCopies;
    private final String expiryDate;
}
