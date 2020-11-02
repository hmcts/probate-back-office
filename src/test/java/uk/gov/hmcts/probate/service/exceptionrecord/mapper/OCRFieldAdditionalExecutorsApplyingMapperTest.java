package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.reform.probate.model.AttorneyNamesAndAddress;
import uk.gov.hmcts.reform.probate.model.cases.Address;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ExecutorApplying;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OCRFieldAdditionalExecutorsApplyingMapperTest {

    private static final String EXECUTOR_APPLYING_1_NAME = "Conner O'Mailey";
    private static final String EXECUTOR_APPLYING_1_OTHER_NAMES = "Connie O";
    private static final Boolean EXECUTOR_APPLYING_1_HAS_OTHER_NAMES = Boolean.TRUE;
    private static final String EXECUTOR_APPLYING_1_EMAIL = "connie.omailey@gmail.com";
    private static final String EXECUTOR_APPLYING_1_ADDRESS_LINE1 = "Petty England";

    private static final String EXECUTOR_APPLYING_2_NAME = "Mark Strong";
    private static final String EXECUTOR_APPLYING_2_OTHER_NAMES = null;
    private static final Boolean EXECUTOR_APPLYING_2_HAS_OTHER_NAMES = Boolean.FALSE;
    private static final String EXECUTOR_APPLYING_2_EMAIL = "mark.strong@gmail.com";
    private static final String EXECUTOR_APPLYING_2_ADDRESS_LINE1 = "Petty Scotland";

    private static final String EXECUTOR_APPLYING_3_NAME = "Executor Three";
    private static final String EXECUTOR_APPLYING_3_OTHER_NAMES = null;
    private static final Boolean EXECUTOR_APPLYING_3_HAS_OTHER_NAMES = Boolean.FALSE;
    private static final String EXECUTOR_APPLYING_3_EMAIL = "exec.three@gmail.com";
    private static final String EXECUTOR_APPLYING_3_ADDRESS_LINE1 = "Petty Wales";

    private static final String EXECUTOR_APPLYING_4_NAME = "Executor Four";
    private static final String EXECUTOR_APPLYING_4_OTHER_NAMES = null;
    private static final Boolean EXECUTOR_APPLYING_4_HAS_OTHER_NAMES = Boolean.FALSE;
    private static final String EXECUTOR_APPLYING_4_EMAIL = "exec.four@gmail.com";
    private static final String EXECUTOR_APPLYING_4_ADDRESS_LINE1 = "Petty Northern Ireland";

    private static final String ADDRESS_LINE2 = "22 Green Park";
    private static final String ADDRESS_POST_TOWN = "London";
    private static final String ADDRESS_COUNTY = "Greater London";
    private static final String ADDRESS_POST_CODE = "NW1 1AB";

    private OCRFieldAdditionalExecutorsApplyingMapper ocrFieldAdditionalExecutorsApplyingMapper
            = new OCRFieldAdditionalExecutorsApplyingMapper();

    private ExceptionRecordOCRFields ocrFields;
    private ExceptionRecordOCRFields ocrFields2;
    private ExceptionRecordOCRFields ocrFields3;
    private ExceptionRecordOCRFields ocrFields4;
    private ExceptionRecordOCRFields ocrFields5;

    @Before
    public void setUpClass() throws Exception {
        ocrFields = ExceptionRecordOCRFields.builder()
                .executorsApplying0applyingExecutorName(EXECUTOR_APPLYING_1_NAME)
                .executorsApplying0applyingExecutorOtherNames(EXECUTOR_APPLYING_1_OTHER_NAMES)
                .executorsApplying0applyingExecutorEmail(EXECUTOR_APPLYING_1_EMAIL)
                .executorsApplying0applyingExecutorAddressLine1(EXECUTOR_APPLYING_1_ADDRESS_LINE1)
                .executorsApplying0applyingExecutorAddressLine2(ADDRESS_LINE2)
                .executorsApplying0applyingExecutorAddressTown(ADDRESS_POST_TOWN)
                .executorsApplying0applyingExecutorAddressCounty(ADDRESS_COUNTY)
                .executorsApplying0applyingExecutorAddressPostCode(ADDRESS_POST_CODE)
                .build();

        ocrFields2 = ExceptionRecordOCRFields.builder()
                .executorsApplying0applyingExecutorName(EXECUTOR_APPLYING_1_NAME)
                .executorsApplying0applyingExecutorOtherNames(EXECUTOR_APPLYING_1_OTHER_NAMES)
                .executorsApplying0applyingExecutorEmail(EXECUTOR_APPLYING_1_EMAIL)
                .executorsApplying0applyingExecutorAddressLine1(EXECUTOR_APPLYING_1_ADDRESS_LINE1)
                .executorsApplying0applyingExecutorAddressLine2(ADDRESS_LINE2)
                .executorsApplying0applyingExecutorAddressTown(ADDRESS_POST_TOWN)
                .executorsApplying0applyingExecutorAddressCounty(ADDRESS_COUNTY)
                .executorsApplying0applyingExecutorAddressPostCode(ADDRESS_POST_CODE)
                .executorsApplying1applyingExecutorName(EXECUTOR_APPLYING_2_NAME)
                .executorsApplying1applyingExecutorOtherNames(EXECUTOR_APPLYING_2_OTHER_NAMES)
                .executorsApplying1applyingExecutorEmail(EXECUTOR_APPLYING_2_EMAIL)
                .executorsApplying1applyingExecutorAddressLine1(EXECUTOR_APPLYING_2_ADDRESS_LINE1)
                .executorsApplying1applyingExecutorAddressLine2(ADDRESS_LINE2)
                .executorsApplying1applyingExecutorAddressTown(ADDRESS_POST_TOWN)
                .executorsApplying1applyingExecutorAddressCounty(ADDRESS_COUNTY)
                .executorsApplying1applyingExecutorAddressPostCode(ADDRESS_POST_CODE)
                .build();

        ocrFields3 = ExceptionRecordOCRFields.builder()
                .build();

        ocrFields4 = ExceptionRecordOCRFields.builder()
                .executorsApplying0applyingExecutorName(EXECUTOR_APPLYING_1_NAME)
                .executorsApplying0applyingExecutorOtherNames(EXECUTOR_APPLYING_1_OTHER_NAMES)
                .executorsApplying0applyingExecutorEmail(EXECUTOR_APPLYING_1_EMAIL)
                .executorsApplying0applyingExecutorAddressLine1(EXECUTOR_APPLYING_1_ADDRESS_LINE1)
                .executorsApplying0applyingExecutorAddressLine2(ADDRESS_LINE2)
                .executorsApplying0applyingExecutorAddressTown(ADDRESS_POST_TOWN)
                .executorsApplying0applyingExecutorAddressCounty(ADDRESS_COUNTY)
                .executorsApplying0applyingExecutorAddressPostCode(ADDRESS_POST_CODE)
                .executorsApplying1applyingExecutorName(EXECUTOR_APPLYING_2_NAME)
                .executorsApplying1applyingExecutorOtherNames(EXECUTOR_APPLYING_2_OTHER_NAMES)
                .executorsApplying1applyingExecutorEmail(EXECUTOR_APPLYING_2_EMAIL)
                .executorsApplying1applyingExecutorAddressLine1(EXECUTOR_APPLYING_2_ADDRESS_LINE1)
                .executorsApplying1applyingExecutorAddressLine2(ADDRESS_LINE2)
                .executorsApplying1applyingExecutorAddressTown(ADDRESS_POST_TOWN)
                .executorsApplying1applyingExecutorAddressCounty(ADDRESS_COUNTY)
                .executorsApplying1applyingExecutorAddressPostCode(ADDRESS_POST_CODE)
                .executorsApplying2applyingExecutorName(EXECUTOR_APPLYING_3_NAME)
                .executorsApplying2applyingExecutorOtherNames(EXECUTOR_APPLYING_3_OTHER_NAMES)
                .executorsApplying2applyingExecutorEmail(EXECUTOR_APPLYING_3_EMAIL)
                .executorsApplying2applyingExecutorAddressLine1(EXECUTOR_APPLYING_3_ADDRESS_LINE1)
                .executorsApplying2applyingExecutorAddressLine2(ADDRESS_LINE2)
                .executorsApplying2applyingExecutorAddressTown(ADDRESS_POST_TOWN)
                .executorsApplying2applyingExecutorAddressCounty(ADDRESS_COUNTY)
                .executorsApplying2applyingExecutorAddressPostCode(ADDRESS_POST_CODE)
                .build();

        ocrFields5 = ExceptionRecordOCRFields.builder()
                .executorsApplying0applyingExecutorName(EXECUTOR_APPLYING_1_NAME)
                .executorsApplying0applyingExecutorOtherNames(EXECUTOR_APPLYING_1_OTHER_NAMES)
                .executorsApplying0applyingExecutorEmail(EXECUTOR_APPLYING_1_EMAIL)
                .executorsApplying0applyingExecutorAddressLine1(EXECUTOR_APPLYING_1_ADDRESS_LINE1)
                .executorsApplying0applyingExecutorAddressLine2(ADDRESS_LINE2)
                .executorsApplying0applyingExecutorAddressTown(ADDRESS_POST_TOWN)
                .executorsApplying0applyingExecutorAddressCounty(ADDRESS_COUNTY)
                .executorsApplying0applyingExecutorAddressPostCode(ADDRESS_POST_CODE)
                .executorsApplying1applyingExecutorName(EXECUTOR_APPLYING_2_NAME)
                .executorsApplying1applyingExecutorOtherNames(EXECUTOR_APPLYING_2_OTHER_NAMES)
                .executorsApplying1applyingExecutorEmail(EXECUTOR_APPLYING_2_EMAIL)
                .executorsApplying1applyingExecutorAddressLine1(EXECUTOR_APPLYING_2_ADDRESS_LINE1)
                .executorsApplying1applyingExecutorAddressLine2(ADDRESS_LINE2)
                .executorsApplying1applyingExecutorAddressTown(ADDRESS_POST_TOWN)
                .executorsApplying1applyingExecutorAddressCounty(ADDRESS_COUNTY)
                .executorsApplying1applyingExecutorAddressPostCode(ADDRESS_POST_CODE)
                .executorsApplying2applyingExecutorName(EXECUTOR_APPLYING_3_NAME)
                .executorsApplying2applyingExecutorOtherNames(EXECUTOR_APPLYING_3_OTHER_NAMES)
                .executorsApplying2applyingExecutorEmail(EXECUTOR_APPLYING_3_EMAIL)
                .executorsApplying2applyingExecutorAddressLine1(EXECUTOR_APPLYING_3_ADDRESS_LINE1)
                .executorsApplying2applyingExecutorAddressLine2(ADDRESS_LINE2)
                .executorsApplying2applyingExecutorAddressTown(ADDRESS_POST_TOWN)
                .executorsApplying2applyingExecutorAddressCounty(ADDRESS_COUNTY)
                .executorsApplying2applyingExecutorAddressPostCode(ADDRESS_POST_CODE)
                .executorsApplying3applyingExecutorName(EXECUTOR_APPLYING_4_NAME)
                .executorsApplying3applyingExecutorOtherNames(EXECUTOR_APPLYING_4_OTHER_NAMES)
                .executorsApplying3applyingExecutorEmail(EXECUTOR_APPLYING_4_EMAIL)
                .executorsApplying3applyingExecutorAddressLine1(EXECUTOR_APPLYING_4_ADDRESS_LINE1)
                .executorsApplying3applyingExecutorAddressLine2(ADDRESS_LINE2)
                .executorsApplying3applyingExecutorAddressTown(ADDRESS_POST_TOWN)
                .executorsApplying3applyingExecutorAddressCounty(ADDRESS_COUNTY)
                .executorsApplying3applyingExecutorAddressPostCode(ADDRESS_POST_CODE)
                .build();
    }

    @Test
    public void testAdditionalExecutorApplying() {
        List<CollectionMember<ExecutorApplying>> response
                = ocrFieldAdditionalExecutorsApplyingMapper.toAdditionalCollectionMember(ocrFields);
        assertEquals(EXECUTOR_APPLYING_1_NAME, response.get(0).getValue().getApplyingExecutorName());
        assertEquals(EXECUTOR_APPLYING_1_OTHER_NAMES, response.get(0).getValue().getApplyingExecutorOtherNames());
        assertEquals(EXECUTOR_APPLYING_1_HAS_OTHER_NAMES, response.get(0).getValue().getApplyingExecutorHasOtherName());
        assertEquals(EXECUTOR_APPLYING_1_EMAIL, response.get(0).getValue().getApplyingExecutorEmail());
        assertEquals(EXECUTOR_APPLYING_1_ADDRESS_LINE1, response.get(0).getValue().getApplyingExecutorAddress().getAddressLine1());
        assertEquals(ADDRESS_LINE2, response.get(0).getValue().getApplyingExecutorAddress().getAddressLine2());
        assertEquals(ADDRESS_POST_TOWN, response.get(0).getValue().getApplyingExecutorAddress().getPostTown());
        assertEquals(ADDRESS_COUNTY, response.get(0).getValue().getApplyingExecutorAddress().getCounty());
        assertEquals(ADDRESS_POST_CODE, response.get(0).getValue().getApplyingExecutorAddress().getPostCode());
        assertEquals(1, response.size());
    }

    @Test
    public void testTwoAdditionalExecutorApplying() {
        List<CollectionMember<ExecutorApplying>> response
                = ocrFieldAdditionalExecutorsApplyingMapper.toAdditionalCollectionMember(ocrFields2);
        assertEquals(EXECUTOR_APPLYING_1_NAME, response.get(0).getValue().getApplyingExecutorName());
        assertEquals(EXECUTOR_APPLYING_1_OTHER_NAMES, response.get(0).getValue().getApplyingExecutorOtherNames());
        assertEquals(EXECUTOR_APPLYING_1_HAS_OTHER_NAMES, response.get(0).getValue().getApplyingExecutorHasOtherName());
        assertEquals(EXECUTOR_APPLYING_1_EMAIL, response.get(0).getValue().getApplyingExecutorEmail());
        assertEquals(EXECUTOR_APPLYING_1_ADDRESS_LINE1, response.get(0).getValue().getApplyingExecutorAddress().getAddressLine1());
        assertEquals(ADDRESS_LINE2, response.get(0).getValue().getApplyingExecutorAddress().getAddressLine2());
        assertEquals(ADDRESS_POST_TOWN, response.get(0).getValue().getApplyingExecutorAddress().getPostTown());
        assertEquals(ADDRESS_COUNTY, response.get(0).getValue().getApplyingExecutorAddress().getCounty());
        assertEquals(ADDRESS_POST_CODE, response.get(0).getValue().getApplyingExecutorAddress().getPostCode());
        assertEquals(EXECUTOR_APPLYING_2_NAME, response.get(1).getValue().getApplyingExecutorName());
        assertEquals(EXECUTOR_APPLYING_2_OTHER_NAMES, response.get(1).getValue().getApplyingExecutorOtherNames());
        assertEquals(EXECUTOR_APPLYING_2_HAS_OTHER_NAMES, response.get(1).getValue().getApplyingExecutorHasOtherName());
        assertEquals(EXECUTOR_APPLYING_2_EMAIL, response.get(1).getValue().getApplyingExecutorEmail());
        assertEquals(EXECUTOR_APPLYING_2_ADDRESS_LINE1, response.get(1).getValue().getApplyingExecutorAddress().getAddressLine1());
        assertEquals(ADDRESS_LINE2, response.get(1).getValue().getApplyingExecutorAddress().getAddressLine2());
        assertEquals(ADDRESS_POST_TOWN, response.get(1).getValue().getApplyingExecutorAddress().getPostTown());
        assertEquals(ADDRESS_COUNTY, response.get(1).getValue().getApplyingExecutorAddress().getCounty());
        assertEquals(ADDRESS_POST_CODE, response.get(1).getValue().getApplyingExecutorAddress().getPostCode());
        assertEquals(2, response.size());
    }

    @Test
    public void testThreeAdditionalExecutorApplying() {
        List<CollectionMember<ExecutorApplying>> response
                = ocrFieldAdditionalExecutorsApplyingMapper.toAdditionalCollectionMember(ocrFields4);
        assertEquals(EXECUTOR_APPLYING_1_NAME, response.get(0).getValue().getApplyingExecutorName());
        assertEquals(EXECUTOR_APPLYING_1_OTHER_NAMES, response.get(0).getValue().getApplyingExecutorOtherNames());
        assertEquals(EXECUTOR_APPLYING_1_HAS_OTHER_NAMES, response.get(0).getValue().getApplyingExecutorHasOtherName());
        assertEquals(EXECUTOR_APPLYING_1_EMAIL, response.get(0).getValue().getApplyingExecutorEmail());
        assertEquals(EXECUTOR_APPLYING_1_ADDRESS_LINE1, response.get(0).getValue().getApplyingExecutorAddress().getAddressLine1());
        assertEquals(ADDRESS_LINE2, response.get(0).getValue().getApplyingExecutorAddress().getAddressLine2());
        assertEquals(ADDRESS_POST_TOWN, response.get(0).getValue().getApplyingExecutorAddress().getPostTown());
        assertEquals(ADDRESS_COUNTY, response.get(0).getValue().getApplyingExecutorAddress().getCounty());
        assertEquals(ADDRESS_POST_CODE, response.get(0).getValue().getApplyingExecutorAddress().getPostCode());
        assertEquals(EXECUTOR_APPLYING_2_NAME, response.get(1).getValue().getApplyingExecutorName());
        assertEquals(EXECUTOR_APPLYING_2_OTHER_NAMES, response.get(1).getValue().getApplyingExecutorOtherNames());
        assertEquals(EXECUTOR_APPLYING_2_HAS_OTHER_NAMES, response.get(1).getValue().getApplyingExecutorHasOtherName());
        assertEquals(EXECUTOR_APPLYING_2_EMAIL, response.get(1).getValue().getApplyingExecutorEmail());
        assertEquals(EXECUTOR_APPLYING_2_ADDRESS_LINE1, response.get(1).getValue().getApplyingExecutorAddress().getAddressLine1());
        assertEquals(ADDRESS_LINE2, response.get(1).getValue().getApplyingExecutorAddress().getAddressLine2());
        assertEquals(ADDRESS_POST_TOWN, response.get(1).getValue().getApplyingExecutorAddress().getPostTown());
        assertEquals(ADDRESS_COUNTY, response.get(1).getValue().getApplyingExecutorAddress().getCounty());
        assertEquals(ADDRESS_POST_CODE, response.get(1).getValue().getApplyingExecutorAddress().getPostCode());
        assertEquals(EXECUTOR_APPLYING_3_NAME, response.get(2).getValue().getApplyingExecutorName());
        assertEquals(EXECUTOR_APPLYING_3_OTHER_NAMES, response.get(2).getValue().getApplyingExecutorOtherNames());
        assertEquals(EXECUTOR_APPLYING_3_HAS_OTHER_NAMES, response.get(2).getValue().getApplyingExecutorHasOtherName());
        assertEquals(EXECUTOR_APPLYING_3_EMAIL, response.get(2).getValue().getApplyingExecutorEmail());
        assertEquals(EXECUTOR_APPLYING_3_ADDRESS_LINE1, response.get(2).getValue().getApplyingExecutorAddress().getAddressLine1());
        assertEquals(ADDRESS_LINE2, response.get(2).getValue().getApplyingExecutorAddress().getAddressLine2());
        assertEquals(ADDRESS_POST_TOWN, response.get(2).getValue().getApplyingExecutorAddress().getPostTown());
        assertEquals(ADDRESS_COUNTY, response.get(2).getValue().getApplyingExecutorAddress().getCounty());
        assertEquals(ADDRESS_POST_CODE, response.get(2).getValue().getApplyingExecutorAddress().getPostCode());
        assertEquals(3, response.size());
    }

    @Test
    public void testFourAdditionalExecutorApplying() {
        List<CollectionMember<ExecutorApplying>> response
                = ocrFieldAdditionalExecutorsApplyingMapper.toAdditionalCollectionMember(ocrFields5);
        assertEquals(EXECUTOR_APPLYING_1_NAME, response.get(0).getValue().getApplyingExecutorName());
        assertEquals(EXECUTOR_APPLYING_1_OTHER_NAMES, response.get(0).getValue().getApplyingExecutorOtherNames());
        assertEquals(EXECUTOR_APPLYING_1_HAS_OTHER_NAMES, response.get(0).getValue().getApplyingExecutorHasOtherName());
        assertEquals(EXECUTOR_APPLYING_1_EMAIL, response.get(0).getValue().getApplyingExecutorEmail());
        assertEquals(EXECUTOR_APPLYING_1_ADDRESS_LINE1, response.get(0).getValue().getApplyingExecutorAddress().getAddressLine1());
        assertEquals(ADDRESS_LINE2, response.get(0).getValue().getApplyingExecutorAddress().getAddressLine2());
        assertEquals(ADDRESS_POST_TOWN, response.get(0).getValue().getApplyingExecutorAddress().getPostTown());
        assertEquals(ADDRESS_COUNTY, response.get(0).getValue().getApplyingExecutorAddress().getCounty());
        assertEquals(ADDRESS_POST_CODE, response.get(0).getValue().getApplyingExecutorAddress().getPostCode());
        assertEquals(EXECUTOR_APPLYING_2_NAME, response.get(1).getValue().getApplyingExecutorName());
        assertEquals(EXECUTOR_APPLYING_2_OTHER_NAMES, response.get(1).getValue().getApplyingExecutorOtherNames());
        assertEquals(EXECUTOR_APPLYING_2_HAS_OTHER_NAMES, response.get(1).getValue().getApplyingExecutorHasOtherName());
        assertEquals(EXECUTOR_APPLYING_2_EMAIL, response.get(1).getValue().getApplyingExecutorEmail());
        assertEquals(EXECUTOR_APPLYING_2_ADDRESS_LINE1, response.get(1).getValue().getApplyingExecutorAddress().getAddressLine1());
        assertEquals(ADDRESS_LINE2, response.get(1).getValue().getApplyingExecutorAddress().getAddressLine2());
        assertEquals(ADDRESS_POST_TOWN, response.get(1).getValue().getApplyingExecutorAddress().getPostTown());
        assertEquals(ADDRESS_COUNTY, response.get(1).getValue().getApplyingExecutorAddress().getCounty());
        assertEquals(ADDRESS_POST_CODE, response.get(1).getValue().getApplyingExecutorAddress().getPostCode());
        assertEquals(EXECUTOR_APPLYING_3_NAME, response.get(2).getValue().getApplyingExecutorName());
        assertEquals(EXECUTOR_APPLYING_3_OTHER_NAMES, response.get(2).getValue().getApplyingExecutorOtherNames());
        assertEquals(EXECUTOR_APPLYING_3_HAS_OTHER_NAMES, response.get(2).getValue().getApplyingExecutorHasOtherName());
        assertEquals(EXECUTOR_APPLYING_3_EMAIL, response.get(2).getValue().getApplyingExecutorEmail());
        assertEquals(EXECUTOR_APPLYING_3_ADDRESS_LINE1, response.get(2).getValue().getApplyingExecutorAddress().getAddressLine1());
        assertEquals(ADDRESS_LINE2, response.get(2).getValue().getApplyingExecutorAddress().getAddressLine2());
        assertEquals(ADDRESS_POST_TOWN, response.get(2).getValue().getApplyingExecutorAddress().getPostTown());
        assertEquals(ADDRESS_COUNTY, response.get(2).getValue().getApplyingExecutorAddress().getCounty());
        assertEquals(ADDRESS_POST_CODE, response.get(2).getValue().getApplyingExecutorAddress().getPostCode());
        assertEquals(EXECUTOR_APPLYING_4_NAME, response.get(3).getValue().getApplyingExecutorName());
        assertEquals(EXECUTOR_APPLYING_4_OTHER_NAMES, response.get(3).getValue().getApplyingExecutorOtherNames());
        assertEquals(EXECUTOR_APPLYING_4_HAS_OTHER_NAMES, response.get(3).getValue().getApplyingExecutorHasOtherName());
        assertEquals(EXECUTOR_APPLYING_4_EMAIL, response.get(3).getValue().getApplyingExecutorEmail());
        assertEquals(EXECUTOR_APPLYING_4_ADDRESS_LINE1, response.get(3).getValue().getApplyingExecutorAddress().getAddressLine1());
        assertEquals(ADDRESS_LINE2, response.get(3).getValue().getApplyingExecutorAddress().getAddressLine2());
        assertEquals(ADDRESS_POST_TOWN, response.get(3).getValue().getApplyingExecutorAddress().getPostTown());
        assertEquals(ADDRESS_COUNTY, response.get(3).getValue().getApplyingExecutorAddress().getCounty());
        assertEquals(ADDRESS_POST_CODE, response.get(3).getValue().getApplyingExecutorAddress().getPostCode());
        assertEquals(4, response.size());
    }

    @Test
    public void testNoAdditionalExecutorApplying() {
        List<CollectionMember<ExecutorApplying>> response
                = ocrFieldAdditionalExecutorsApplyingMapper.toAdditionalCollectionMember(ocrFields3);
        assertEquals(0, response.size());
    }
}
