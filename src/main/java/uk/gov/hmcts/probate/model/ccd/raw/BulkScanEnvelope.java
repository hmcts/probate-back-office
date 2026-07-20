package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.ccd.sdk.api.CCD;

@Data
@Builder
@AllArgsConstructor
public class BulkScanEnvelope {

    @CCD(label = "ID")
    private final String id;

    @CCD(label = "Action")
    private final String action;

}
