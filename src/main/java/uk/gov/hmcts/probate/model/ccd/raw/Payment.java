package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.FieldType;

@Data
@Builder
public class Payment {

    @CCD(label = "Payment status", typeOverride = FieldType.FixedList, typeParameterOverride = "paymentStatusEnum")
    private final String status;
    @CCD(label = "Payment date", typeOverride = FieldType.Date)
    private final String date;
    @CCD(label = "Payment reference")
    private final String reference;
    @CCD(label = "Payment amount", typeOverride = FieldType.MoneyGBP)
    private final String amount;
    @CCD(label = "Payment method", typeOverride = FieldType.FixedList, typeParameterOverride = "paymentMethodEnum")
    private final String method;
    @CCD(label = "Payment transaction ID")
    private final String transactionId;
    @CCD(label = "Payment site ID ")
    private final String siteId;

  // ==== ccd-definition-converter: synthesised definition-only fields (retrofit) ====
  @CCD(label = "Payment fee ID")
  private String feeId;
  // ==== end synthesised definition-only fields ====
}
