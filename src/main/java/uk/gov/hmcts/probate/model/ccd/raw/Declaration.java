package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.ccd.sdk.api.CCD;

@Data
@Builder
public class Declaration {

    @CCD(label = "Confirm")
    private final String confirm;
    @CCD(label = "Confirmitem1")
    private final String confirmItem1;
    @CCD(label = "Confirmitem2")
    private final String confirmItem2;
    @CCD(label = "Confirmitem3")
    private final String confirmItem3;
    @CCD(label = "Requests")
    private final String requests;
    @CCD(label = "Requestsitem1")
    private final String requestsItem1;
    @CCD(label = "Requestsitem2")
    private final String requestsItem2;
    @CCD(label = "Understand")
    private final String understand;
    @CCD(label = "Understanditem1")
    private final String understandItem1;
    @CCD(label = "Understanditem2")
    private final String understandItem2;
    @CCD(label = "Accept")
    private final String accept;

  // ==== ccd-definition-converter: synthesised definition-only fields (retrofit) ====
  @CCD(label = "SubmitWarning")
  private String submitWarning;
  // ==== end synthesised definition-only fields ====
}
