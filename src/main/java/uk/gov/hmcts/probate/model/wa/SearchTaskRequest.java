package uk.gov.hmcts.probate.model.wa;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import uk.gov.hmcts.probate.service.wa.search.parameter.SearchParameter;

import java.util.List;

@EqualsAndHashCode
@ToString
@SuppressWarnings("java:S1452")
public class SearchTaskRequest {
    @JsonProperty("search_parameters")
    private List<@Valid SearchParameter<?>> searchParameters;
    @JsonProperty("sorting_parameters")
    private List<SortingParameter> sortingParameters;
    @JsonProperty("request_context")
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
