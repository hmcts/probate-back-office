package uk.gov.hmcts.probate.model;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;

@Data
@Builder
public class ExecutorsApplyingNotification {
    private String name;
    private String email;
    private SolsAddress address;
    private String notification;
    private String responseReceived;
}
