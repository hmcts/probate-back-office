package uk.gov.hmcts.probate.config.notifications;

import lombok.Data;

@Data
public class EmailTemplates {
    private String documentReceived;
    private String caseStopped;
    private String grantIssued;
    private String generalCaveatMessage;
}
