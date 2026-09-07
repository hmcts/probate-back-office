package uk.gov.hmcts.probate.service.wa.search;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uk.gov.hmcts.probate.service.wa.search.enums.CFTTaskState;
import uk.gov.hmcts.probate.service.wa.search.enums.RoleCategory;

import java.util.List;

@Getter
@Builder
@EqualsAndHashCode
@ToString
public class SearchRequest {
    private List<CFTTaskState> cftTaskStates;
    private List<String> jurisdictions;
    private List<String> locations;
    private List<String> regions;
    private List<String> caseIds;
    private List<String> users;
    private List<String> taskTypes;
    private List<String> workTypes;
    private List<RoleCategory> roleCategories;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private RequestContext requestContext;
    private List<SortingParameter> sortingParameters;

    public boolean isAvailableTasksOnly() {
        return requestContext != null && requestContext.equals(RequestContext.AVAILABLE_TASKS);
    }

    public boolean isAllWork() {
        return requestContext != null && requestContext.equals(RequestContext.ALL_WORK);
    }
}
