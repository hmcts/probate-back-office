package uk.gov.hmcts.probate.model.caseaccess;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class DecisionCCDRequest {

    @JsonProperty("case_details")
    private CaseDetails caseDetails;

    public static DecisionCCDRequest decisionCCDRequest(CaseDetails caseDetails) {
        return DecisionCCDRequest.builder().caseDetails(caseDetails).build();
    }
}
