package uk.gov.hmcts.probate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.api.ComplexType;

@ComplexType(name = "registrarEscalateReasonFixedList", generate = true)
@Getter
public enum RegistrarEscalateReason {

    @CCD(label = "Referrals")
    @JsonProperty("referrals")
    REFERRALS("referrals"),

    @CCD(label = "Orders")
    @JsonProperty("orders")
    ORDERS("orders");

    private final String registrarEscalateReasonCode;

    RegistrarEscalateReason(String registrarEscalateReasonCode) {
        this.registrarEscalateReasonCode = registrarEscalateReasonCode;
    }
}
