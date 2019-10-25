package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

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

    private static final String ADOPTED_RELATIVE_INOUT_ERROR = "this is not in or out";

    private OCRFieldAdoptiveRelativesMapper ocrFieldAdoptiveRelativesMapper = new OCRFieldAdoptiveRelativesMapper();

    private ExceptionRecordOCRFields ocrFields;
    private ExceptionRecordOCRFields ocrFieldsInOutError;

    @Before
    public void setUpClass() throws Exception {
        ocrFields = ExceptionRecordOCRFields.builder()
                .adoptiveRelatives0name(ADOPTED_RELATIVE_1_NAME)
                .adoptiveRelatives0relationship(ADOPTED_RELATIVE_1_RELATIONSHIP)
                .adoptiveRelatives0adoptedInOrOut(ADOPTED_RELATIVE_1_INOUT)
                .adoptiveRelatives1name(ADOPTED_RELATIVE_2_NAME)
                .adoptiveRelatives1relationship(ADOPTED_RELATIVE_2_RELATIONSHIP)
                .adoptiveRelatives1adoptedInOrOut(ADOPTED_RELATIVE_2_INOUT)
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
        assertEquals(ADOPTED_RELATIVE_2_NAME, response.get(1).getValue().getName());
        assertEquals(ADOPTED_RELATIVE_2_RELATIONSHIP, response.get(1).getValue().getRelationship());
        assertEquals(InOut.OUT, response.get(1).getValue().getAdoptedInOrOut());
    }

    @Test(expected = OCRMappingException.class)
    public void testGetAdoptedRelativesWithInvalidInOutValue() {
        List<CollectionMember<AdoptiveRelative>> response
                = ocrFieldAdoptiveRelativesMapper.toAdoptiveRelativesCollectionMember(ocrFieldsInOutError);
    }
}
