package uk.gov.hmcts.probate.config.properties.thirdParties;

import lombok.*;

@Data
public class ThirdParty {
    private String name;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String town;
    private String postcode;
}
