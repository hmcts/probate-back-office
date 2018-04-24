package uk.gov.hmcts.probate.model.ccd.raw.response;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Builder
@Accessors(chain = true)
public class CallbackResponse {

    private ResponseCaseData data;
    private List<String> errors;
    private List<String> warnings;
}
