package uk.gov.hmcts.probate.service.wa.search.parameter;

import uk.gov.hmcts.probate.service.wa.search.SearchOperator;


public interface SearchParameter<T> {

    SearchParameterKey getKey();

    SearchOperator getOperator();

    T getValues();
}
