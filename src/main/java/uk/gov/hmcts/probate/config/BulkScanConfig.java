package uk.gov.hmcts.probate.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "bulkscan.default-value")
public class BulkScanConfig {
    private String postcode;
    private String name;
    private String names;
    private String addressLine;
    private String dob;
    private String solsSolicitorIsApplying; //True if sols details are present (no default)
    private String email; //Remove after case creation
    private String phone;

    private String deceasedAnyOtherNames;
    private String deceasedDomicileInEngWales;

    private String solsSolicitorRepresentativeName; //sols Firm name if empty (no default)
    private String solsSolicitorFirmName;
    private String solsSolicitorAppReference; //Deceased surname if empty (no default)

    private String legalRepresentative;
    private String ihtForm;
    private String grossNetValue;
}