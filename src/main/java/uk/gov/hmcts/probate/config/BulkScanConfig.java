package uk.gov.hmcts.probate.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "bulkscan.default-value")
public class BulkScanConfig {

    //Caveat
    private String caveatorForenames;
    private String caveatorSurname;
    private String caveatorAddressLine1;
    private String caveatorAddressPostCode;

    private String deceasedForenames;
    private String deceasedSurname;
    private String deceasedAnyOtherNames;
    private String deceasedAddressLine1;
    private String postcode; // deceasedAddressPostCode
    private String deceasedDateOfBirth;
    private String solsSolicitorRepresentativeName;
    private String solsSolicitorFirmName;

    private String primaryApplicantForenames;
    private String primaryApplicantSurname;

    private String primaryApplicantAddressLine1;
    private String primaryApplicantAddressPostCode;

    private String solsSolicitorAppReference;
    private String solsSolicitorAddressLine1;
    private String solsSolicitorAddressLine2;
    private String solsSolicitorAddressTown;
    private String solsSolicitorAddressPostCode;
    private String solsSolicitorEmail;
    private String solsSolicitorPhoneNumber;
    private String deceasedDomicileInEngWales;

    private String legalRepresentative;
    private String ihtForm;
    private String grossNetValue;
}