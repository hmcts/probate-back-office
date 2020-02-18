package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.reform.probate.model.AdoptiveRelative;
import uk.gov.hmcts.reform.probate.model.InOut;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class OCRFieldAdoptiveRelativesMapperTest {

    private static final String ADOPTED_RELATIVE_1_NAME = "Conner O'Mailey";
    private static final String ADOPTED_RELATIVE_1_RELATIONSHIP = "Brother";
    private static final String ADOPTED_RELATIVE_1_INOUT = "in";
    private static final String ADOPTED_RELATIVE_2_NAME = "Marcus James";
    private static final String ADOPTED_RELATIVE_2_RELATIONSHIP = "Brother";
    private static final String ADOPTED_RELATIVE_2_INOUT = "adopted out";
    private static final String ADOPTED_RELATIVE_3_NAME = "Mandy James";
    private static final String ADOPTED_RELATIVE_3_RELATIONSHIP = "Daughter";
    private static final String ADOPTED_RELATIVE_3_INOUT = "in";
    private static final String ADOPTED_RELATIVE_4_NAME = "John James";
    private static final String ADOPTED_RELATIVE_4_RELATIONSHIP = "Uncle";
    private static final String ADOPTED_RELATIVE_4_INOUT = "adopted out";
    private static final String ADOPTED_RELATIVE_5_NAME = "Bob White";
    private static final String ADOPTED_RELATIVE_5_RELATIONSHIP = "Mother";
    private static final String ADOPTED_RELATIVE_5_INOUT = "adopted in";
    private static final String ADOPTED_RELATIVE_6_NAME = "Pater Brown";
    private static final String ADOPTED_RELATIVE_6_RELATIONSHIP = "Son";
    private static final String ADOPTED_RELATIVE_6_INOUT = "out";

    private static final String ADOPTED_RELATIVE_INOUT_ERROR = "this is not in or out";

    private static final String ADOPTED_EXPECTED_IN_VALUE = "in";
    private static final String ADOPTED_EXPECTED_OUT_VALUE = "out";

    private OCRFieldAdoptiveRelativesMapper ocrFieldAdoptiveRelativesMapper = new OCRFieldAdoptiveRelativesMapper();

    private ExceptionRecordOCRFields ocrFields;
    private ExceptionRecordOCRFields ocrFieldsMultiple;
    private ExceptionRecordOCRFields ocrFieldsInOutError;

    @Before
    public void setUpClass() throws Exception {
        ocrFields = ExceptionRecordOCRFields.builder()
                .adoptiveRelatives0name(ADOPTED_RELATIVE_1_NAME)
                .adoptiveRelatives0relationship(ADOPTED_RELATIVE_1_RELATIONSHIP)
                .adoptiveRelatives0adoptedInOrOut(ADOPTED_RELATIVE_1_INOUT)
                .build();

        ocrFieldsMultiple = ExceptionRecordOCRFields.builder()
                .adoptiveRelatives0name(ADOPTED_RELATIVE_1_NAME)
                .adoptiveRelatives0relationship(ADOPTED_RELATIVE_1_RELATIONSHIP)
                .adoptiveRelatives0adoptedInOrOut(ADOPTED_RELATIVE_1_INOUT)
                .adoptiveRelatives1name(ADOPTED_RELATIVE_2_NAME)
                .adoptiveRelatives1relationship(ADOPTED_RELATIVE_2_RELATIONSHIP)
                .adoptiveRelatives1adoptedInOrOut(ADOPTED_RELATIVE_2_INOUT)
                .adoptiveRelatives2name(ADOPTED_RELATIVE_3_NAME)
                .adoptiveRelatives2relationship(ADOPTED_RELATIVE_3_RELATIONSHIP)
                .adoptiveRelatives2adoptedInOrOut(ADOPTED_RELATIVE_3_INOUT)
                .adoptiveRelatives3name(ADOPTED_RELATIVE_4_NAME)
                .adoptiveRelatives3relationship(ADOPTED_RELATIVE_4_RELATIONSHIP)
                .adoptiveRelatives3adoptedInOrOut(ADOPTED_RELATIVE_4_INOUT)
                .adoptiveRelatives4name(ADOPTED_RELATIVE_5_NAME)
                .adoptiveRelatives4relationship(ADOPTED_RELATIVE_5_RELATIONSHIP)
                .adoptiveRelatives4adoptedInOrOut(ADOPTED_RELATIVE_5_INOUT)
                .adoptiveRelatives5name(ADOPTED_RELATIVE_6_NAME)
                .adoptiveRelatives5relationship(ADOPTED_RELATIVE_6_RELATIONSHIP)
                .adoptiveRelatives5adoptedInOrOut(ADOPTED_RELATIVE_6_INOUT)
                .build();

        ocrFieldsInOutError = ExceptionRecordOCRFields.builder()
                .adoptiveRelatives0name(ADOPTED_RELATIVE_1_NAME)
                .adoptiveRelatives0relationship(ADOPTED_RELATIVE_1_RELATIONSHIP)
                .adoptiveRelatives0adoptedInOrOut(ADOPTED_RELATIVE_INOUT_ERROR)
                .build();
    }

    @Test
    public void testGetAdoptedRelatives() {
        List<CollectionMember<AdoptiveRelative>> response = ocrFieldAdoptiveRelativesMapper.toAdoptiveRelativesCollectionMember(ocrFields);
        assertEquals(ADOPTED_RELATIVE_1_NAME, response.get(0).getValue().getName());
        assertEquals(ADOPTED_RELATIVE_1_RELATIONSHIP, response.get(0).getValue().getRelationship());
        assertEquals(InOut.IN, response.get(0).getValue().getAdoptedInOrOut());
        assertEquals(1, response.size());
    }

    @Test
    public void testGetMultipleAdoptedRelatives() {
        List<CollectionMember<AdoptiveRelative>> response
                = ocrFieldAdoptiveRelativesMapper.toAdoptiveRelativesCollectionMember(ocrFieldsMultiple);
        assertEquals(ADOPTED_RELATIVE_1_NAME, response.get(0).getValue().getName());
        assertEquals(ADOPTED_RELATIVE_1_RELATIONSHIP, response.get(0).getValue().getRelationship());
        assertEquals(ADOPTED_EXPECTED_IN_VALUE, response.get(0).getValue().getAdoptedInOrOut());
        assertEquals(ADOPTED_RELATIVE_2_NAME, response.get(1).getValue().getName());
        assertEquals(ADOPTED_RELATIVE_2_RELATIONSHIP, response.get(1).getValue().getRelationship());
        assertEquals(ADOPTED_EXPECTED_OUT_VALUE, response.get(1).getValue().getAdoptedInOrOut());
        assertEquals(ADOPTED_RELATIVE_3_NAME, response.get(2).getValue().getName());
        assertEquals(ADOPTED_RELATIVE_3_RELATIONSHIP, response.get(2).getValue().getRelationship());
        assertEquals(ADOPTED_EXPECTED_OUT_VALUE, response.get(2).getValue().getAdoptedInOrOut());
        assertEquals(ADOPTED_RELATIVE_4_NAME, response.get(3).getValue().getName());
        assertEquals(ADOPTED_RELATIVE_4_RELATIONSHIP, response.get(3).getValue().getRelationship());
        assertEquals(ADOPTED_EXPECTED_OUT_VALUE, response.get(3).getValue().getAdoptedInOrOut());
        assertEquals(ADOPTED_RELATIVE_5_NAME, response.get(4).getValue().getName());
        assertEquals(ADOPTED_RELATIVE_5_RELATIONSHIP, response.get(4).getValue().getRelationship());
        assertEquals(ADOPTED_EXPECTED_IN_VALUE, response.get(4).getValue().getAdoptedInOrOut());
        assertEquals(ADOPTED_RELATIVE_6_NAME, response.get(5).getValue().getName());
        assertEquals(ADOPTED_RELATIVE_6_RELATIONSHIP, response.get(5).getValue().getRelationship());
        assertEquals(ADOPTED_EXPECTED_OUT_VALUE, response.get(5).getValue().getAdoptedInOrOut());
        assertEquals(6, response.size());
    }

    @Test(expected = OCRMappingException.class)
    public void testGetAdoptedRelativesWithInvalidInOutValue() {
        List<CollectionMember<AdoptiveRelative>> response
                = ocrFieldAdoptiveRelativesMapper.toAdoptiveRelativesCollectionMember(ocrFieldsInOutError);
    }
}
