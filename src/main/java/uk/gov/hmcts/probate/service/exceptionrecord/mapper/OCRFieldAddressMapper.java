package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToCaveatorAddress;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToDeceasedAddress;
import uk.gov.hmcts.reform.probate.model.cases.Address;

@Slf4j
@Component
public class OCRFieldAddressMapper {

    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String postTown;
    private String county;
    private String country;
    private String postCode;

    @SuppressWarnings("squid:S1168")
    @ToCaveatorAddress
    public Address toCaveatorAddress(ExceptionRecordOCRFields ocrFields) {
        log.info("Beginning mapping for Caveator Address");
        this.addressLine1 = ocrFields.getCaveatorAddressLine1();
        this.addressLine2 = ocrFields.getCaveatorAddressLine2();
        this.addressLine3 = "";
        this.postTown = ocrFields.getCaveatorAddressTown();
        this.county = ocrFields.getCaveatorAddressCounty();
        this.country = "";
        this.postCode = ocrFields.getCaveatorAddressPostCode();
        return buildAddress();
    }

    @SuppressWarnings("squid:S1168")
    @ToDeceasedAddress
    public Address toDeceasedAddress(ExceptionRecordOCRFields ocrFields) {
        log.info("Beginning mapping for Deceased Address");
        this.addressLine1 = ocrFields.getDeceasedAddressLine1();
        this.addressLine2 = ocrFields.getDeceasedAddressLine2();
        this.addressLine3 = "";
        this.postTown = ocrFields.getDeceasedAddressTown();
        this.county = ocrFields.getDeceasedAddressCounty();
        this.country = "";
        this.postCode = ocrFields.getDeceasedAddressPostCode();
        return buildAddress();
    }

    private Address buildAddress() {
        Address address = Address.builder()
                .addressLine1(addressLine1)
                .addressLine2(addressLine2)
                .addressLine3(addressLine3)
                .postTown(postTown)
                .county(county)
                .country(country)
                .postCode(postCode)
                .build();
        return address;
    }
}