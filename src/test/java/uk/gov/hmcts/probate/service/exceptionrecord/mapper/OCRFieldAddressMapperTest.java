package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.Before;
import org.junit.Test;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.reform.probate.model.cases.Address;

import static org.junit.Assert.assertEquals;

@Component
public class OCRFieldAddressMapperTest {

    private static final String CAVEAT_ADDRESS_LINE1 = "Petty England";
    private static final String DECEASED_ADDRESS_LINE1 = "Petty Scotland";
    private static final String ADDRESS_LINE2 = "22 Green Park";
    private static final String ADDRESS_POST_TOWN = "London";
    private static final String ADDRESS_COUNTY = "Greater London";
    private static final String ADDRESS_POST_CODE = "NW1 1AB";

    private OCRFieldAddressMapper addressMapper = new OCRFieldAddressMapper();

    ExceptionRecordOCRFields ocrFields;

    @Before
    public void setUpClass() throws Exception {
        ocrFields = ExceptionRecordOCRFields.builder()
                .caveatorAddressLine1(CAVEAT_ADDRESS_LINE1)
                .caveatorAddressLine2(ADDRESS_LINE2)
                .caveatorAddressTown(ADDRESS_POST_TOWN)
                .caveatorAddressCounty(ADDRESS_COUNTY)
                .caveatorAddressPostCode(ADDRESS_POST_CODE)
                .deceasedAddressLine1(DECEASED_ADDRESS_LINE1)
                .deceasedAddressLine2(ADDRESS_LINE2)
                .deceasedAddressTown(ADDRESS_POST_TOWN)
                .deceasedAddressCounty(ADDRESS_COUNTY)
                .deceasedAddressPostCode(ADDRESS_POST_CODE)
                .build();
    }

    @Test
    public void testCaveatAddress() {
        Address response = addressMapper.toCaveatorAddress(ocrFields);
        assertEquals(CAVEAT_ADDRESS_LINE1, response.getAddressLine1());
        assertEquals(ADDRESS_LINE2, response.getAddressLine2());
        assertEquals(ADDRESS_POST_TOWN, response.getPostTown());
        assertEquals(ADDRESS_COUNTY, response.getCounty());
        assertEquals(ADDRESS_POST_CODE, response.getPostCode());
    }

    @Test
    public void testDeceasedAddress() {
        Address response = addressMapper.toDeceasedAddress(ocrFields);
        assertEquals(DECEASED_ADDRESS_LINE1, response.getAddressLine1());
        assertEquals(ADDRESS_LINE2, response.getAddressLine2());
        assertEquals(ADDRESS_POST_TOWN, response.getPostTown());
        assertEquals(ADDRESS_COUNTY, response.getCounty());
        assertEquals(ADDRESS_POST_CODE, response.getPostCode());
    }
}