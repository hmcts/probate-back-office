package uk.gov.hmcts.probate.model.ccd;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EventId {

    CREATE_DRAFT("createDraft"),
    UPDATE_DRAFT("updateDraft"),
    CREATE_APPLICATION("createApplication"),
    CREATE_CASE("createCase"),
    PAYMENT_FAILED("createCasePaymentFailed"),
    PAYMENT_FAILED_TO_SUCCESS("createCasePaymentSuccess"),
    PAYMENT_FAILED_AGAIN("createCasePaymentFailedMultiple");

    @Getter
    private final String name;

}
