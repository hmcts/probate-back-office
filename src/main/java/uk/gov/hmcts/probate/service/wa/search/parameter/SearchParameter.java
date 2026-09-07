package uk.gov.hmcts.probate.service.wa.search.parameter;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import uk.gov.hmcts.probate.service.wa.search.SearchOperator;


@Schema(
    description = "SearchParameters",
    discriminatorProperty = "values",
    discriminatorMapping = {
        @DiscriminatorMapping(value = "SearchParameterBoolean", schema = SearchParameterBoolean.class),
        @DiscriminatorMapping(value = "SearchParameterList", schema = SearchParameterList.class)
    }
)
public interface SearchParameter<T> {

    SearchParameterKey getKey();

    SearchOperator getOperator();

    T getValues();
}
