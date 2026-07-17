package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.FieldType;

@Data
@Builder
public class BulkPrint {

    @CCD(
            label = "Document sent to bulk print",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "probateDocumentTypeEnum"
    )
    @JsonProperty("templateName")
    private final String templateName;

    @CCD(label = "Send Letter Id")
    @JsonProperty("sendLetterId")
    private final String sendLetterId;

}
