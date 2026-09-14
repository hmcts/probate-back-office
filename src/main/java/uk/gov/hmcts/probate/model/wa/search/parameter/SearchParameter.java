package uk.gov.hmcts.probate.model.wa.search.parameter;

import uk.gov.hmcts.probate.model.wa.search.SearchOperator;


public interface SearchParameter<T> {

    SearchParameterKey getKey();

    SearchOperator getOperator();

    T getValues();
}
