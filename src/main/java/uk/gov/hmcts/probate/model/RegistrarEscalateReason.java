package uk.gov.hmcts.probate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum RegistrarEscalateReason {

    @JsonProperty("referrals")
    REFERRALS("referrals"),

    @JsonProperty("orders")
    ORDERS("orders");

    private final String registrarEscalateReasonCode;

    RegistrarEscalateReason(String registrarEscalateReasonCode) {
        this.registrarEscalateReasonCode = registrarEscalateReasonCode;
    }
}
