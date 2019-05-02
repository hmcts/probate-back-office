package uk.gov.hmcts.probate.model.probateman;

import java.time.LocalDate;


public class StandingSearchCreator {

    public static StandingSearch create() {
        StandingSearch standingSearch = new StandingSearch();
        standingSearch.setId(34345L);
        standingSearch.setSsNumber("ssNumber");
        standingSearch.setProbateNumber("ssProbateNumber");
        standingSearch.setProbateVersion(3454L);
        standingSearch.setRegistryName("Oxford");
        standingSearch.setRegistryCode(999L);
        standingSearch.setDeceasedId(74646L);
        standingSearch.setDeceasedForenames("ssDeceasedForenames");
        standingSearch.setDeceasedSurname("ssDeceasedSurname");
        standingSearch.setDateOfBirth(LocalDate.of(1987, 1, 1));
        standingSearch.setDateOfDeath1(LocalDate.of(2018, 1, 1));
        standingSearch.setCcdCaseNo("432434");
        standingSearch.setDeceasedAddress("ssDeceasedAddress");
        standingSearch.setApplicantAddress("ssApplicantAddress");
        standingSearch.setRegistryRegLocationCode(23434L);
        standingSearch.setSsApplicantForename("ssApplicantForename");
        standingSearch.setSsApplicantSurname("ssApplicantSurname");
        standingSearch.setSsApplicantHonours("ssApplicantHonours");
        standingSearch.setSsApplicantTitle("ssApplicantTitle");
        standingSearch.setSsDateLastExtended(LocalDate.of(2019, 1, 1));
        standingSearch.setSsDateOfEntry(LocalDate.of(2019, 2, 2));
        standingSearch.setSsDateOfExpiry(LocalDate.of(2019, 3, 3));
        standingSearch.setSsWithdrawnDate(LocalDate.of(2019, 4, 4));
        standingSearch.setStandingSearchText("ssText");
        standingSearch.setDnmInd("N");
        standingSearch.setAliasNames("ssAliasNames");
        return standingSearch;
    }

    private StandingSearchCreator() {
    }
}
