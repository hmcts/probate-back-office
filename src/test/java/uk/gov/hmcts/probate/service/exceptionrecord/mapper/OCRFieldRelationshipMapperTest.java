package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.probate.model.Relationship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Component
class OCRFieldRelationshipMapperTest {

    private OCRFieldRelationshipMapper relationshipMapper = new OCRFieldRelationshipMapper();

    @Test
    void testRelationshipAdoptedChild() {
        Relationship response = relationshipMapper.toRelationship(Relationship.Constants.ADOPTED_CHILD_DESC);
        assertEquals(Relationship.ADOPTED_CHILD, response);
    }

    @Test
    void testRelationshipChild() {
        Relationship response = relationshipMapper.toRelationship(Relationship.Constants.CHILD_DESC);
        assertEquals(Relationship.CHILD, response);
    }

    @Test
    void testRelationshipParent() {
        Relationship response = relationshipMapper.toRelationship(Relationship.Constants.PARENT_DESC);
        assertEquals(Relationship.PARENT, response);
    }

    @Test
    void testRelationshipPartner() {
        Relationship response = relationshipMapper.toRelationship(Relationship.Constants.PARTNER_DESC);
        assertEquals(Relationship.PARTNER, response);
    }

    @Test
    void testRelationshipSibling() {
        Relationship response = relationshipMapper.toRelationship(Relationship.Constants.SIBLING_DESC);
        assertEquals(Relationship.SIBLING, response);
    }

    @Test
    void testRelationshipOther() {
        Relationship response = relationshipMapper.toRelationship(Relationship.Constants.OTHER_DESC);
        assertEquals(Relationship.OTHER, response);
    }

    @Test
    void testRelationshipDefaultToOther() {
        Relationship response = relationshipMapper.toRelationship("DoesNotMatch");
        assertEquals(Relationship.OTHER, response);
    }

    @Test
    void testToRelationshipNotOtherToReturnNull() {
        String response = relationshipMapper.toRelationshipOther(Relationship.Constants.CHILD_DESC);
        assertNull(response);
    }
}
