package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.Test;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.reform.probate.model.Relationship;
import uk.gov.hmcts.reform.probate.model.cases.MaritalStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Component
public class OCRFieldRelationshipMapperTest {

    private OCRFieldRelationshipMapper relationshipMapper = new OCRFieldRelationshipMapper();

    @Test
    public void testRelationshipAdoptedChild() {
        Relationship response = relationshipMapper.toRelationship(Relationship.Constants.ADOPTED_CHILD_DESC);
        assertEquals(Relationship.ADOPTED_CHILD, response);
    }

    @Test
    public void testRelationshipChild() {
        Relationship response = relationshipMapper.toRelationship(Relationship.Constants.CHILD_DESC);
        assertEquals(Relationship.CHILD, response);
    }

    @Test
    public void testRelationshipParent() {
        Relationship response = relationshipMapper.toRelationship(Relationship.Constants.PARENT_DESC);
        assertEquals(Relationship.PARENT, response);
    }

    @Test
    public void testRelationshipPartner() {
        Relationship response = relationshipMapper.toRelationship(Relationship.Constants.PARTNER_DESC);
        assertEquals(Relationship.PARTNER, response);
    }

    @Test
    public void testRelationshipSibling() {
        Relationship response = relationshipMapper.toRelationship(Relationship.Constants.SIBLING_DESC);
        assertEquals(Relationship.SIBLING, response);
    }

    @Test
    public void testRelationshipOther() {
        Relationship response = relationshipMapper.toRelationship(Relationship.Constants.OTHER_DESC);
        assertEquals(Relationship.OTHER, response);
    }

    @Test
    public void testRelationshipDefaultToOther() {
        Relationship response = relationshipMapper.toRelationship("DoesNotMatch");
        assertEquals(Relationship.OTHER, response);
    }

    @Test
    public void testToRelationshipNotOtherToReturnNull() {
        String response = relationshipMapper.toRelationshipOther(Relationship.Constants.CHILD_DESC);
        assertNull(response);
    }
}