package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Declaration {

    private final String confirm;
    private final String confirmItem1;
    private final String confirmItem2;
    private final String confirmItem3;
    private final String requests;
    private final String requestsItem1;
    private final String requestsItem2;
    private final String understand;
    private final String understandItem1;
    private final String understandItem2;
    private final String accept;

}
