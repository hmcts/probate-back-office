package uk.gov.hmcts.probate.model.exceptionrecord;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Wrapper class for CCD list items.
 */
public class Item<T> {

    @JsonProperty("value")
    public final T value;

    public Item(T value) {
        this.value = value;
    }
}