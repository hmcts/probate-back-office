package uk.gov.hmcts.probate.service.consumer;

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

    public AssignCaseAccessRequest(String caseId,String assigneeId,String caseTypeId){
        this.caseId=caseId;
        this.assigneeId=assigneeId;
        this.caseTypeId=caseTypeId;
    }
}