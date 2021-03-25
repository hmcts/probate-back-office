package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;


@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@Data
public class CollectionMember<T> implements Serializable {
    private final String id;
    private final T value;

    public CollectionMember(T value) {
        id = null;
        this.value = value;
    }
}
