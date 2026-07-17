package uk.gov.hmcts.probate.model;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.FieldType;

@Data
@Builder
public class ExecutorsApplyingNotification {
    @CCD(label = "Name of the executor")
    private String name;
    @CCD(
            label = "Email of the executor",
            regex = "[a-zA-Z0-9#$%'+=?^_`{|}~-]{1,30}(?:\\.[^.\\n]{1,30}){0,30}@[a-zA-Z0-9](?:[a-zA-Z0-9-.]{0,30}[a-zA-Z0-9])?\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,10}[a-zA-Z0-9])?"
    )
    private String email;
    @CCD(label = "Address of the executor")
    private SolsAddress address;
    @CCD(label = "Do you wish to send a notification?", typeOverride = FieldType.YesOrNo)
    private String notification;
    @CCD(label = "Has a response been received?", typeOverride = FieldType.YesOrNo)
    private String responseReceived;
}
