package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToRelationship;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToRelationshipOther;
import uk.gov.hmcts.reform.probate.model.Relationship;

import static uk.gov.hmcts.reform.probate.model.Relationship.Constants.ADOPTED_CHILD_DESC;
import static uk.gov.hmcts.reform.probate.model.Relationship.Constants.CHILD_DESC;
import static uk.gov.hmcts.reform.probate.model.Relationship.Constants.PARENT_DESC;
import static uk.gov.hmcts.reform.probate.model.Relationship.Constants.PARTNER_DESC;
import static uk.gov.hmcts.reform.probate.model.Relationship.Constants.SIBLING_DESC;

@Slf4j
@Component
public class OCRFieldRelationshipMapper {

    @ToRelationship
    public Relationship toRelationship(String relationshipValue) {
        log.info("Beginning mapping for Relationship value: {}", relationshipValue);

        if (relationshipValue == null || relationshipValue.isEmpty()) {
            return null;
        } else {
            switch (relationshipValue.trim()) {
                case ADOPTED_CHILD_DESC:
                    return Relationship.ADOPTED_CHILD;
                case CHILD_DESC:
                    return Relationship.CHILD;
                case PARENT_DESC:
                    return Relationship.PARENT;
                case PARTNER_DESC:
                    return Relationship.PARTNER;
                case SIBLING_DESC:
                    return Relationship.SIBLING;
                default:
                    return Relationship.OTHER;
            }
        }
    }

    @ToRelationshipOther
    public String toRelationshipOther(String relationshipValue) {
        log.info("Beginning mapping for Relationship Other");
        String otherNullValue = null;
        if (relationshipValue == null || relationshipValue.isEmpty()) {
            return otherNullValue;
        } else {
            Relationship relationship = toRelationship(relationshipValue);
            if (relationship.name().equals(Relationship.OTHER.name())) {
                log.info("Found Other value: {}", relationshipValue);
                return relationshipValue;
            }
        }

        return otherNullValue;
    }
}