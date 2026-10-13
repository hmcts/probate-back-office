package uk.gov.hmcts.probate.model.wa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public record SearchEventAndCase(@JsonProperty("case_id") String caseId,
                                 @JsonProperty("event_id") String eventId,
                                 @JsonProperty("case_jurisdiction") String caseJurisdiction,
                                 @JsonProperty("case_type") String caseType) {

}
