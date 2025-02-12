package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
public class TTL {

    @JsonProperty("SystemTTL")
    private LocalDate systemTTL;

    @JsonProperty("OverrideTTL")
    private LocalDate overrideTTL;

    @JsonProperty("Suspended")
    private String suspended;
}