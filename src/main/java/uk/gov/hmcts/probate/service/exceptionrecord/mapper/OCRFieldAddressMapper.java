package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToAttorneyOnBehalfOfAddress;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToCaveatorAddress;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToDeceasedAddress;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToPrimaryApplicantAddress;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToSolicitorAddress;
import uk.gov.hmcts.reform.probate.model.AttorneyNamesAndAddress;
import uk.gov.hmcts.reform.probate.model.cases.Address;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;

import java.util.ArrayList;
import java.util.List;

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

    private final static String POSTCODE_REGEX_PATTERN = "^([A-Z]{1,2}\\d[A-Z\\d]? ?\\d[A-Z]{2}|GIR ?0A{2})$";

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
        if (StringUtils.isNotBlank(attorneyNamesAndAddress.getName())
                && attorneyNamesAndAddress.getAddress() != null
                && StringUtils.isNotBlank(attorneyNamesAndAddress.getAddress().getPostCode())) {
            collectionMemberList.add(new CollectionMember<>(null, attorneyNamesAndAddress));
        } else if (StringUtils.isBlank(attorneyNamesAndAddress.getName())
                && attorneyNamesAndAddress.getAddress() != null) {
            String errorMessage = "Attorney name is missing but an attorney address has been supplied";
            log.error(errorMessage);
            throw new OCRMappingException(errorMessage);
        } else if (StringUtils.isNotBlank(attorneyNamesAndAddress.getName())
                && attorneyNamesAndAddress.getAddress() == null) {
            String errorMessage = "Attorney address is missing but an attorney name has been supplied";
            log.error(errorMessage);
            throw new OCRMappingException(errorMessage);
        }
        return collectionMemberList;
    }

    @SuppressWarnings("squid:S1168")
    @ToCaveatorAddress
    public Address toCaveatorAddress(ExceptionRecordOCRFields ocrFields) {
        if (StringUtils.isNotBlank(ocrFields.getSolsSolicitorAddressLine1()) ||
                StringUtils.isNotBlank(ocrFields.getSolsSolicitorAddressPostCode())) {
            return buildSolicitorAddress(ocrFields);
        } else {
            return buildCaveatorAddress(ocrFields);
        }
    }

    @SuppressWarnings("squid:S1168")
    @ToSolicitorAddress
    public Address toSolicitorAddress(ExceptionRecordOCRFields ocrFields) {
        log.info("Beginning mapping for Solicitor Address");
        this.addressLine1 = ocrFields.getSolsSolicitorAddressLine1();
        this.addressLine2 = ocrFields.getSolsSolicitorAddressLine2();
        this.addressLine3 = "";
        this.postTown = ocrFields.getSolsSolicitorAddressTown();
        this.county = ocrFields.getSolsSolicitorAddressCounty();
        this.country = "";
        this.postCode = ocrFields.getSolsSolicitorAddressPostCode();
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

    private Address buildCaveatorAddress(ExceptionRecordOCRFields ocrFields) {
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

    private Address buildSolicitorAddress(ExceptionRecordOCRFields ocrFields) {
        log.info("Beginning mapping for Solicitor Address");
        this.addressLine1 = ocrFields.getSolsSolicitorAddressLine1();
        this.addressLine2 = ocrFields.getSolsSolicitorAddressLine2();
        this.addressLine3 = "";
        this.postTown = ocrFields.getSolsSolicitorAddressTown();
        this.county = ocrFields.getSolsSolicitorAddressCounty();
        this.country = "";
        this.postCode = ocrFields.getSolsSolicitorAddressPostCode();
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
        if (StringUtils.isBlank(address.getPostCode())) {
            return null;
        } else {
            address.setPostCode(postCode.toUpperCase());
            validatePostCode(address.getPostCode());
        }
        return address;
    }

    private void validatePostCode(final String postCode) {
        if (!postCode.matches(POSTCODE_REGEX_PATTERN)) {
            String errorMessage = "An invalid postcode has been found '" + postCode + "', please provide a valid postcode";
            log.error(errorMessage);
            throw new OCRMappingException(errorMessage);
        }
    }
}