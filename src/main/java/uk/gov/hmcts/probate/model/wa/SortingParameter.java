package uk.gov.hmcts.probate.model.wa;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.lucene.search.SortField;


@EqualsAndHashCode
@ToString
public class SortingParameter {

    private final SortField sortBy;

    private final SortOrder sortOrder;

    public SortingParameter(SortField sortBy, SortOrder sortOrder) {
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    public SortField getSortBy() {
        return sortBy;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }
}
