package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToAttorneyOnBehalfOfAddress;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToCaveatorAddress;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToDeceasedAddress;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToPrimaryApplicantAddress;
import uk.gov.hmcts.reform.probate.model.AttorneyNamesAndAddress;
import uk.gov.hmcts.reform.probate.model.cases.Address;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ExecutorApplying;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class OCRFieldAddressMapper {

    private static final String POSTCODE_REGEX = "^[A-Z]{1,2}[0-9R][0-9A-Z]? [0-9][ABD-HJLNP-UW-Z]{2}$";

    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String postTown;
    private String county;
    private String country;
    private String postCode;

    @SuppressWarnings("squid:S1168")
    @ToPrimaryApplicantAddress
    public Address toPrimaryApplicantAddress(ExceptionRecordOCRFields ocrFields) {
        log.info("Beginning mapping for Primary Applicant Address");
        this.addressLine1 = ocrFields.getPrimaryApplicantAddressLine1();
        this.addressLine2 = ocrFields.getPrimaryApplicantAddressLine2();
        this.addressLine3 = "";
        this.postTown = ocrFields.getPrimaryApplicantAddressTown();
        this.county = ocrFields.getPrimaryApplicantAddressCounty();
        this.country = "";
        this.postCode = ocrFields.getPrimaryApplicantAddressPostCode();
        return buildAddress();
    }

    @SuppressWarnings("squid:S1168")
    @ToAttorneyOnBehalfOfAddress
    public List<CollectionMember<AttorneyNamesAndAddress>> toAttorneyOnBehalfOfAddress(ExceptionRecordOCRFields ocrFields) {
        log.info("Beginning mapping for Attorney On Behalf Of Address");
        this.addressLine1 = ocrFields.getAttorneyOnBehalfOfAddressLine1();
        this.addressLine2 = ocrFields.getAttorneyOnBehalfOfAddressLine2();
        this.addressLine3 = "";
        this.postTown = ocrFields.getAttorneyOnBehalfOfAddressTown();
        this.county = ocrFields.getAttorneyOnBehalfOfAddressCounty();
        this.country = "";
        this.postCode = ocrFields.getAttorneyOnBehalfOfAddressPostCode();
        AttorneyNamesAndAddress attorneyNamesAndAddress = AttorneyNamesAndAddress.builder()
                .name(ocrFields.getAttorneyOnBehalfOfName())
                .address(buildAddress())
                .build();
        List<CollectionMember<AttorneyNamesAndAddress>> collectionMemberList = new ArrayList<>();
        if ((attorneyNamesAndAddress.getName() != null
                && !attorneyNamesAndAddress.getName().isEmpty())
                || (attorneyNamesAndAddress.getAddress() != null
                && attorneyNamesAndAddress.getAddress().getPostCode() != null
                && !attorneyNamesAndAddress.getAddress().getPostCode().isEmpty())) {
            collectionMemberList.add(new CollectionMember<>(null, attorneyNamesAndAddress));
        }
        return collectionMemberList;
    }

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
        if (address.getPostCode() == null || address.getPostCode().isEmpty()) {
            return null;
        } else {
            validatePostcode(address.getPostCode());
        }
        return address;
    }

    private void validatePostcode(String postcodeValue) {
        Pattern pattern = Pattern.compile(POSTCODE_REGEX);
        Matcher matcher = pattern.matcher(postcodeValue);
        if (!matcher.matches()) {
            String errorMessage = "Postcode field '" + postcodeValue + "' has an invalid UK format.";
            log.error(errorMessage);
            throw new OCRMappingException(errorMessage);
        }
    }
}