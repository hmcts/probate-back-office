package uk.gov.hmcts.probate.model.wa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WaMapper implements Serializable {
    @JsonProperty("client_context")
    private ClientContext clientContext;
}
