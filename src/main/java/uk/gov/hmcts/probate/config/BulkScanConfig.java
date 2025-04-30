package uk.gov.hmcts.probate.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "bulkscan.default-value")
public class BulkScanConfig {
    // TODO - Remove unneeded non-generic fields

    // Generic fields
    private String postcode;
    private String name;
    private String names;
    private String addressLine;
    private String dob;
    private String solsSolicitorIsApplying;
    private String email;
    private String phone;
    private String solsSolicitorRepresentativeName;
    //Used
    private String deceasedAnyOtherNames;
    private String deceasedDateOfBirth;
    private String solsSolicitorFirmName;

    private String solsSolicitorAppReference;
    private String solsSolicitorAddressTown;
    private String solsSolicitorAddressPostCode;
    private String deceasedDomicileInEngWales;

    private String legalRepresentative;
    private String ihtForm;
    private String grossNetValue;
}