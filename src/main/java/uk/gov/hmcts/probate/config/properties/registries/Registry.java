package uk.gov.hmcts.probate.config.properties.registries;

import lombok.Data;

@Data
public class Registry {
    private String name;
    private String phone;
    private String emailReplyToId;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String addressLine4;
    private String town;
    private String postcode;
}
