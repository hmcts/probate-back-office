package uk.gov.hmcts.probate.model.wa.search.parameter;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import uk.gov.hmcts.probate.model.wa.search.SearchOperator;

import java.util.List;

@EqualsAndHashCode
@ToString
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class SearchParameterList implements SearchParameter<List<String>> {

    private final SearchParameterKey key;
    private final SearchOperator operator;
    private final List<String> values;

    @JsonCreator
    public SearchParameterList(SearchParameterKey key, SearchOperator operator, List<String> values) {
        this.key = key;
        this.operator = operator;
        this.values = values;
    }

    @Override
    public SearchParameterKey getKey() {
        return key;
    }

    @Override
    public SearchOperator getOperator() {
        return operator;
    }

    @Override
    public List<String> getValues() {
        return values;
    }
}
