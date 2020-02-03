package uk.gov.hmcts.probate.config.notifications;

import lombok.Data;

@Data
public class EmailTemplates {
    private String applicationReceived;
    private String documentReceived;
    private String caseStopped;
    private String caseStoppedCaveat;
    private String grantIssued;
    private String grantReissued;
    private String generalCaveatMessage;
    private String excelaData;
    private String caveatRaised;
    private String caveatRaisedCtsc;
    private String caveatRaisedSols;
    private String requestInformation;
    private String redeclarationSot;
}
