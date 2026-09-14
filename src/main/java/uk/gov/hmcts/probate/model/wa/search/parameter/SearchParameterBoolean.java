package uk.gov.hmcts.probate.model.wa.search.parameter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import uk.gov.hmcts.probate.model.wa.search.SearchOperator;

@EqualsAndHashCode
@ToString
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class SearchParameterBoolean implements SearchParameter<Boolean> {

    private final SearchParameterKey key;
    private final SearchOperator operator;
    private final boolean values;

    @JsonCreator
    public SearchParameterBoolean(SearchParameterKey key, SearchOperator operator, boolean values) {
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
    @JsonProperty("value")
    public Boolean getValues() {
        return values;
    }
}
