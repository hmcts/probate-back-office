package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.config.BulkScanConfig;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.reform.probate.model.AttorneyNamesAndAddress;
import uk.gov.hmcts.reform.probate.model.cases.Address;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_POSTCODE_VALUE;

@Component
class OCRFieldAddressMapperTest {

    private static final String ATTORNEY_ON_BEHALF_OF_NAME = "Conner O'Mailey";
    private static final String ATTORNEY_ON_BEHALF_OF_ADDRESS_LINE1 = "Petty Ireland";
    private static final String PRIMARY_APPLICANT_ADDRESS_LINE1 = "Petty Wales";
    private static final String CAVEAT_ADDRESS_LINE1 = "Petty England";
    private static final String SOLICITORS_ADDRESS_LINE1 = "Petty Ireland";
    private static final String DECEASED_ADDRESS_LINE1 = "Petty Scotland";
    private static final String SOLICITOR_ADDRESS_LINE1 = "Petty Northen Ireland";
    private static final String ADDRESS_LINE2 = "22 Green Park";
    private static final String ADDRESS_POST_TOWN = "London";
    private static final String ADDRESS_COUNTY = "Greater London";
    private static final String ADDRESS_POST_CODE = "nW1 1Ab";
    private static final String ADDRESS_POST_CODE_ERROR = "NW1";

    private static final String ADDRESS_POST_CODE_CORRECT_ERROR_MESSAGE =
        "primaryApplicantAddressPostCode: An invalid postcode has been found 'NW1', please provide a valid postcode";

    @Mock
    private static BulkScanConfig bulkScanConfig;
    private static OCRFieldAddressMapper addressMapper;

    private ExceptionRecordOCRFields ocrFields;
    private ExceptionRecordOCRFields ocrFieldsSolicitorsAddress;
    private ExceptionRecordOCRFields ocrFieldsCaveatAddress;
    private ExceptionRecordOCRFields ocrFieldsAttorneyMissingAddress;
    private ExceptionRecordOCRFields ocrFieldsAttorneyMissingName;

    private ExceptionRecordOCRFields ocrFieldsPostcodeError;


    @BeforeEach
    public void setUpClass() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(bulkScanConfig.getPostcode()).thenReturn(DEFAULT_POSTCODE_VALUE);
        addressMapper = new OCRFieldAddressMapper(bulkScanConfig);
        ocrFields = ExceptionRecordOCRFields.builder()
            .attorneyOnBehalfOfName(ATTORNEY_ON_BEHALF_OF_NAME)
            .attorneyOnBehalfOfAddressLine1(ATTORNEY_ON_BEHALF_OF_ADDRESS_LINE1)
            .attorneyOnBehalfOfAddressLine2(ADDRESS_LINE2)
            .attorneyOnBehalfOfAddressTown(ADDRESS_POST_TOWN)
            .attorneyOnBehalfOfAddressCounty(ADDRESS_COUNTY)
            .attorneyOnBehalfOfAddressPostCode(ADDRESS_POST_CODE)

            .primaryApplicantAddressLine1(PRIMARY_APPLICANT_ADDRESS_LINE1)
            .primaryApplicantAddressLine2(ADDRESS_LINE2)
            .primaryApplicantAddressTown(ADDRESS_POST_TOWN)
            .primaryApplicantAddressCounty(ADDRESS_COUNTY)
            .primaryApplicantAddressPostCode(ADDRESS_POST_CODE)

            .solsSolicitorAddressLine1(SOLICITOR_ADDRESS_LINE1)
            .solsSolicitorAddressLine2(ADDRESS_LINE2)
            .solsSolicitorAddressTown(ADDRESS_POST_TOWN)
            .solsSolicitorAddressCounty(ADDRESS_COUNTY)
            .solsSolicitorAddressPostCode(ADDRESS_POST_CODE)

            .deceasedAddressLine1(DECEASED_ADDRESS_LINE1)
            .deceasedAddressLine2(ADDRESS_LINE2)
            .deceasedAddressTown(ADDRESS_POST_TOWN)
            .deceasedAddressCounty(ADDRESS_COUNTY)
            .deceasedAddressPostCode(ADDRESS_POST_CODE)
            .build();

        ocrFieldsCaveatAddress = ExceptionRecordOCRFields.builder()
            .caveatorAddressLine1(CAVEAT_ADDRESS_LINE1)
            .caveatorAddressLine2(ADDRESS_LINE2)
            .caveatorAddressTown(ADDRESS_POST_TOWN)
            .caveatorAddressCounty(ADDRESS_COUNTY)
            .caveatorAddressPostCode(ADDRESS_POST_CODE)
            .build();


        ocrFieldsSolicitorsAddress = ExceptionRecordOCRFields.builder()
            .solsSolicitorAddressLine1(SOLICITORS_ADDRESS_LINE1)
            .solsSolicitorAddressLine2(ADDRESS_LINE2)
            .solsSolicitorAddressTown(ADDRESS_POST_TOWN)
            .solsSolicitorAddressCounty(ADDRESS_COUNTY)
            .solsSolicitorAddressPostCode(ADDRESS_POST_CODE)
            .build();

        ocrFieldsPostcodeError = ExceptionRecordOCRFields.builder()
            .attorneyOnBehalfOfName(ATTORNEY_ON_BEHALF_OF_NAME)
            .attorneyOnBehalfOfAddressLine1(ATTORNEY_ON_BEHALF_OF_ADDRESS_LINE1)
            .attorneyOnBehalfOfAddressLine2(ADDRESS_LINE2)
            .attorneyOnBehalfOfAddressTown(ADDRESS_POST_TOWN)
            .attorneyOnBehalfOfAddressCounty(ADDRESS_COUNTY)
            .attorneyOnBehalfOfAddressPostCode(ADDRESS_POST_CODE_ERROR)

            .primaryApplicantAddressLine1(PRIMARY_APPLICANT_ADDRESS_LINE1)
            .primaryApplicantAddressLine2(ADDRESS_LINE2)
            .primaryApplicantAddressTown(ADDRESS_POST_TOWN)
            .primaryApplicantAddressCounty(ADDRESS_COUNTY)
            .primaryApplicantAddressPostCode(ADDRESS_POST_CODE_ERROR)

            .caveatorAddressLine1(CAVEAT_ADDRESS_LINE1)
            .caveatorAddressLine2(ADDRESS_LINE2)
            .caveatorAddressTown(ADDRESS_POST_TOWN)
            .caveatorAddressCounty(ADDRESS_COUNTY)
            .caveatorAddressPostCode(ADDRESS_POST_CODE_ERROR)

            .solsSolicitorAddressLine1(SOLICITOR_ADDRESS_LINE1)
            .solsSolicitorAddressLine2(ADDRESS_LINE2)
            .solsSolicitorAddressTown(ADDRESS_POST_TOWN)
            .solsSolicitorAddressCounty(ADDRESS_COUNTY)
            .solsSolicitorAddressPostCode(ADDRESS_POST_CODE_ERROR)

            .deceasedAddressLine1(DECEASED_ADDRESS_LINE1)
            .deceasedAddressLine2(ADDRESS_LINE2)
            .deceasedAddressTown(ADDRESS_POST_TOWN)
            .deceasedAddressCounty(ADDRESS_COUNTY)
            .deceasedAddressPostCode(ADDRESS_POST_CODE_ERROR)
            .build();

        ocrFieldsAttorneyMissingAddress = ExceptionRecordOCRFields.builder()
            .attorneyOnBehalfOfName(ATTORNEY_ON_BEHALF_OF_NAME)
            .build();

        ocrFieldsAttorneyMissingName = ExceptionRecordOCRFields.builder()
            .attorneyOnBehalfOfAddressLine1(ATTORNEY_ON_BEHALF_OF_ADDRESS_LINE1)
            .attorneyOnBehalfOfAddressLine2(ADDRESS_LINE2)
            .attorneyOnBehalfOfAddressTown(ADDRESS_POST_TOWN)
            .attorneyOnBehalfOfAddressCounty(ADDRESS_COUNTY)
            .attorneyOnBehalfOfAddressPostCode(ADDRESS_POST_CODE)
            .build();
    }

    @Test
    void testPrimaryApplicantAddress() {
        Address response = addressMapper.toPrimaryApplicantAddress(ocrFields);
        assertEquals(PRIMARY_APPLICANT_ADDRESS_LINE1, response.getAddressLine1());
        assertEquals(ADDRESS_LINE2, response.getAddressLine2());
        assertEquals(ADDRESS_POST_TOWN, response.getPostTown());
        assertEquals(ADDRESS_COUNTY, response.getCounty());
        assertEquals(ADDRESS_POST_CODE.toUpperCase(), response.getPostCode());
    }

    @Test
    void testCaveatWithCaveatAddress() {
        Address response = addressMapper.toCaveatorAddress(ocrFieldsCaveatAddress);
        assertEquals(CAVEAT_ADDRESS_LINE1, response.getAddressLine1());
        assertEquals(ADDRESS_LINE2, response.getAddressLine2());
        assertEquals(ADDRESS_POST_TOWN, response.getPostTown());
        assertEquals(ADDRESS_COUNTY, response.getCounty());
        assertEquals(ADDRESS_POST_CODE.toUpperCase(), response.getPostCode());
    }

    @Test
    void testCaveatWithSolicitorsAddress() {
        Address response = addressMapper.toCaveatorAddress(ocrFieldsSolicitorsAddress);
        assertEquals(SOLICITORS_ADDRESS_LINE1, response.getAddressLine1());
        assertEquals(ADDRESS_LINE2, response.getAddressLine2());
        assertEquals(ADDRESS_POST_TOWN, response.getPostTown());
        assertEquals(ADDRESS_COUNTY, response.getCounty());
        assertEquals(ADDRESS_POST_CODE.toUpperCase(), response.getPostCode());
    }

    @Test
    void testDeceasedAddress() {
        Address response = addressMapper.toDeceasedAddress(ocrFields);
        assertEquals(DECEASED_ADDRESS_LINE1, response.getAddressLine1());
        assertEquals(ADDRESS_LINE2, response.getAddressLine2());
        assertEquals(ADDRESS_POST_TOWN, response.getPostTown());
        assertEquals(ADDRESS_COUNTY, response.getCounty());
        assertEquals(ADDRESS_POST_CODE.toUpperCase(), response.getPostCode());
    }

    @Test
    void testSolicitorAddress() {
        Address response = addressMapper.toSolicitorAddress(ocrFields);
        assertEquals(SOLICITOR_ADDRESS_LINE1, response.getAddressLine1());
        assertEquals(ADDRESS_LINE2, response.getAddressLine2());
        assertEquals(ADDRESS_POST_TOWN, response.getPostTown());
        assertEquals(ADDRESS_COUNTY, response.getCounty());
        assertEquals(ADDRESS_POST_CODE.toUpperCase(), response.getPostCode());
    }

    @Test
    void testAttorneyNamesAndAddress() {
        List<CollectionMember<AttorneyNamesAndAddress>> response = addressMapper.toAttorneyOnBehalfOfAddress(ocrFields);
        assertEquals(ATTORNEY_ON_BEHALF_OF_NAME, response.get(0).getValue().getName());
        assertEquals(ATTORNEY_ON_BEHALF_OF_ADDRESS_LINE1, response.get(0).getValue().getAddress().getAddressLine1());
        assertEquals(ADDRESS_LINE2, response.get(0).getValue().getAddress().getAddressLine2());
        assertEquals(ADDRESS_POST_TOWN, response.get(0).getValue().getAddress().getPostTown());
        assertEquals(ADDRESS_COUNTY, response.get(0).getValue().getAddress().getCounty());
        assertEquals(ADDRESS_POST_CODE.toUpperCase(), response.get(0).getValue().getAddress().getPostCode());
    }

    @Test
    void testPrimaryApplicantAddressPostcodeErrorWithDefault() {
        Address response = addressMapper.toPrimaryApplicantAddress(ocrFieldsPostcodeError);
        assertEquals(DEFAULT_POSTCODE_VALUE,response.getPostCode());
    }

    @Test
    void testCaveatAddressPostcodeErrorWithDefault() {
        Address response = addressMapper.toCaveatorAddress(ocrFieldsPostcodeError);
        assertEquals(DEFAULT_POSTCODE_VALUE,response.getPostCode());
    }

    @Test
    void testDeceasedAddressPostcodeErrorWithDefault() {
        Address response = addressMapper.toDeceasedAddress(ocrFieldsPostcodeError);
        assertEquals(DEFAULT_POSTCODE_VALUE,response.getPostCode());
    }

    @Test
    void testSolicitorAddressPostcodeErrorWithDefault() {
        Address response = addressMapper.toSolicitorAddress(ocrFieldsPostcodeError);
        assertEquals(DEFAULT_POSTCODE_VALUE,response.getPostCode());
    }

    @Test
    void testAttorneyNamesAndAddressPostcodeErrorWithDefault() {
        List<CollectionMember<AttorneyNamesAndAddress>> response =
                addressMapper.toAttorneyOnBehalfOfAddress(ocrFieldsPostcodeError);
        assertEquals(DEFAULT_POSTCODE_VALUE, response.get(0).getValue().getAddress().getPostCode());
    }

    @Test
    void testAttorneyNameWithMissingNameError() {
        assertThrows(OCRMappingException.class, () -> {
            List<CollectionMember<AttorneyNamesAndAddress>> response =
                    addressMapper.toAttorneyOnBehalfOfAddress(ocrFieldsAttorneyMissingName);
        });
    }

    @Test
    void testAttorneyNameWithMissingAddressError() {
        assertThrows(OCRMappingException.class, () -> {
            List<CollectionMember<AttorneyNamesAndAddress>> response =
                    addressMapper.toAttorneyOnBehalfOfAddress(ocrFieldsAttorneyMissingAddress);
        });
    }
}
