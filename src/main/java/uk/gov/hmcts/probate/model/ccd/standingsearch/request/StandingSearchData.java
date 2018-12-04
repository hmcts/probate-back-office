package uk.gov.hmcts.probate.model.ccd.standingsearch.request;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.caveat.CavAddress;
import uk.gov.hmcts.probate.model.ccd.caveat.CavFullAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
public class StandingSearchData {

    private final ApplicationType applicationType;
    private final String registryLocation;

    // EVENT = createStandingSearch - deceased data

    private final String deceasedForenames;

    private final String deceasedSurname;

    private final LocalDate deceasedDateOfDeath;

    private final LocalDate deceasedDateOfBirth;

    private final String deceasedAnyOtherNames;

    private final List<CollectionMember<CavFullAliasName>> deceasedFullAliasNameList;

    private final CavAddress deceasedAddress;

    // EVENT = createStandingSearch - applicant data

    private final String applicantForenames;

    private final String applicantSurname;

    private final String applicantEmailAddress;

    private final CavAddress applicantAddress;

    // EVENT = createStandingSearch - standing search details

    private final long numberOfCopies;

    private LocalDate expiryDate;

    // EVENT = misc

    public String getDeceasedFullName() {
        return String.join(" ", deceasedForenames, deceasedSurname);
    }

    public String getApplicantFullName() {
        return String.join(" ", applicantForenames, applicantSurname);
    }

}
