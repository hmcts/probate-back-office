package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import uk.gov.hmcts.ccd.sdk.api.CCD;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@AllArgsConstructor
public class ChangeOfRepresentative {

    @CCD(label = "Date Triggered")
    private final LocalDateTime addedDateTime;
    @CCD(label = "Removed Representative")
    private final RemovedRepresentative removedRepresentative;
    @CCD(label = "Added Representative")
    private final AddedRepresentative addedRepresentative;
}
