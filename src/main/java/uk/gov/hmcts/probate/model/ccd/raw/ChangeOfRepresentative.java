package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@AllArgsConstructor
public class ChangeOfRepresentative {

    private final LocalDateTime addedDateTime;
    private final RemovedRepresentative removedRepresentative;
    private final AddedRepresentative addedRepresentative;
}
