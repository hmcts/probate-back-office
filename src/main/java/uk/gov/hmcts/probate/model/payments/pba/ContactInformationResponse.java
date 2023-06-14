package uk.gov.hmcts.probate.model.payments.pba;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactInformationResponse {

    @JsonProperty
    private String uprn;
    @JsonProperty
    private LocalDateTime created;
    @JsonProperty
    private String addressLine1;
    @JsonProperty
    private String addressLine2;
    @JsonProperty
    private String addressLine3;
    @JsonProperty
    private String townCity;
    @JsonProperty
    private String county;
    @JsonProperty
    private String country;
    @JsonProperty
    private String postCode;
}
