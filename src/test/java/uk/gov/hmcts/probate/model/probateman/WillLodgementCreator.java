package uk.gov.hmcts.probate.model.probateman;

import java.time.LocalDate;

public class WillLodgementCreator {

    public static WillLodgement create() {
        WillLodgement willLodgement = new WillLodgement();
        willLodgement.setId(3454L);
        willLodgement.setRkNumber("wlRkNumber");
        willLodgement.setProbateNumber("wlProbateNumber");
        willLodgement.setProbateVersion(23432L);
        willLodgement.setDeceasedId(1232L);
        willLodgement.setDeceasedForenames("wlDeceasedForenames");
        willLodgement.setDeceasedSurname("wlDeceasedSurnames");
        willLodgement.setDateOfBirth(LocalDate.of(1999, 1, 1));
        willLodgement.setDateOfDeath1(LocalDate.of(2019, 1, 1));
        willLodgement.setAliasNames("wlAliasNames");
        willLodgement.setRecordKeepersText("wlRecordKeepersText");
        willLodgement.setCcdCaseNo("wlCcdCaseNo");
        willLodgement.setDnmInd("N");
        return willLodgement;
    }

    private WillLodgementCreator() {
    }
}
