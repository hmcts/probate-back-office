package uk.gov.hmcts.probate.model.wa;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import uk.gov.hmcts.probate.service.wa.search.parameter.SearchParameter;


import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(
    name = "SearchTaskRequest",
    description = "Search task request containing a list of parameters"
)
@EqualsAndHashCode
@ToString
@SuppressWarnings("java:S1452")
public class SearchTaskRequest {

    @Schema(
        requiredMode = REQUIRED,
        name = "search_parameters",
        description = "https://tools.hmcts.net/confluence/display/WA/WA+Task+Management+API+Guidelines")
    @NotEmpty(message = "At least one search_parameter element is required.")
    private List<@Valid SearchParameter<?>> searchParameters;
    @Schema(name = "sorting_parameters")
    private List<SortingParameter> sortingParameters;
    @Schema(name = "request_context", allowableValues = "ALL_WORK, AVAILABLE_TASKS", example = "ALL_WORK")
    private RequestContext requestContext;

    private SearchTaskRequest() {
        //Default constructor for deserialization
        super();
    }

    public SearchTaskRequest(List<SearchParameter<?>> searchParameters) {
        this.searchParameters = searchParameters;
    }

    public SearchTaskRequest(RequestContext requestContext,
                             List<SearchParameter<?>> searchParameters) {
        this.searchParameters = searchParameters;
        this.requestContext = requestContext;
    }

    public SearchTaskRequest(List<SearchParameter<?>> searchParameters,
                             List<SortingParameter> sortingParameters) {
        this.searchParameters = searchParameters;
        this.sortingParameters = sortingParameters;
    }

    public SearchTaskRequest(RequestContext requestContext,
                             List<SearchParameter<?>> searchParameters,
                             List<SortingParameter> sortingParameters) {
        this.searchParameters = searchParameters;
        this.sortingParameters = sortingParameters;
        this.requestContext = requestContext;
    }

    public List<SearchParameter<?>> getSearchParameters() {
        return searchParameters;
    }

    public List<SortingParameter> getSortingParameters() {
        return sortingParameters;
    }

    public RequestContext getRequestContext() {
        return requestContext;
    }
}
