package uk.gov.hmcts.probate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExecutorsApplyingNotification {
    private String name;
    private String email;
}
