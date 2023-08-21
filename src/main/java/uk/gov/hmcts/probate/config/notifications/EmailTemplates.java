package uk.gov.hmcts.probate.config.notifications;

import lombok.Data;

@Data
public class EmailTemplates {
    private String applicationReceived;
    private String applicationReceivedNoDocs;
    private String applicationReceivedPaperFormCaseworker;
    private String documentReceived;
    private String caseStopped;
    private String caseStoppedCaveat;
    private String grantIssued;
    private String grantReissued;
    private String generalCaveatMessage;
    private String exelaData;
    private String smeeAndFordData;
    private String grantRaised;
    private String grantRaisedPaperFormBulkScan;
    private String caveatRaised;
    private String caveatExtend;
    private String caveatRaisedCtsc;
    private String caveatRaisedSols;
    private String requestInformation;
    private String redeclarationSot;
    private String caveatWithdrawn;
    private String grantIssuedIntestacy;
    private String grantDelayed;
    private String grantAwaitingDocumentation;
    private String hmrcExtract;
}
