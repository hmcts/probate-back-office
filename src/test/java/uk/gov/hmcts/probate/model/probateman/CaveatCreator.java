package uk.gov.hmcts.probate.model.probateman;

import java.time.LocalDate;

public class CaveatCreator {

    public static Caveat create() {
        Caveat caveat = new Caveat();
        caveat.setId(1234L);
        caveat.setCaveatNumber("cavCaveatNumber");
        caveat.setProbateNumber("cavProbateNumber");
        caveat.setProbateVersion(77L);
        caveat.setRegistryName("Oxford");
        caveat.setSubregistryName("Carlisle");
        caveat.setRegistryCode(999L);
        caveat.setDeceasedId(44L);
        caveat.setDeceasedForenames("cavDeceasedForenames");
        caveat.setDeceasedSurname("cavDeceasedSurnames");
        caveat.setDateOfBirth(LocalDate.of(1999, 1, 1));
        caveat.setDateOfDeath(LocalDate.of(2019, 1, 1));
        caveat.setAliasNames("cavAliasNames");
        caveat.setCcdCaseNo("cavCcdCaseNo");
        caveat.setCaveatType("cavCaveatType");
        caveat.setCaveatDateOfEntry(LocalDate.of(2019, 2, 2));
        caveat.setCavDateLastExtended(LocalDate.of(2019, 1, 2));
        caveat.setCavExpiryDate(LocalDate.of(2020, 1, 2));
        caveat.setCavWithDrawnDate(LocalDate.of(2020, 1, 2));
        caveat.setCaveatorTitle("cavCaveatorTitle");
        caveat.setCaveatorHonours("CaveatorHonours");
        caveat.setCaveatorForenames("caveatorForenames");
        caveat.setCaveatorSurname("caveatorSurname");
        caveat.setCavSolicitorName("cavSolName");
        caveat.setCavServiceAddress("cavServiceAddress");
        caveat.setCavDxNumber("cavDxNumber");
        caveat.setCavDxExchange("cavDxExchange");
        caveat.setCaveatText("cavCaveatText");
        caveat.setCaveatEventText("cavCaveatEventText");
        caveat.setDnmInd("N");
        return caveat;
    }

    private CaveatCreator() {
    }
}
