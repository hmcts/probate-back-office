package uk.gov.hmcts.probate.service.caseaccess;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class AssignCaseAccessRequest {
    @JsonProperty("case_id")
    private String caseId;

    @JsonProperty("assignee_id")
    private String assigneeId;

    @JsonProperty("case_type_id")
    private String caseTypeId;
}