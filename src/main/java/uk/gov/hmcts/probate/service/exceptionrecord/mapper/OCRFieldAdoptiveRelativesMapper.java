package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToAdditionalExecutorsApplying;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToAdoptiveRelatives;
import uk.gov.hmcts.reform.probate.model.AdoptiveRelative;
import uk.gov.hmcts.reform.probate.model.InOut;
import uk.gov.hmcts.reform.probate.model.cases.Address;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.MaritalStatus;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ExecutorApplying;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class OCRFieldAdoptiveRelativesMapper {

    @SuppressWarnings("squid:S1168")
    @ToAdoptiveRelatives
    public List<CollectionMember<AdoptiveRelative>> toAdoptiveRelativesCollectionMember(ExceptionRecordOCRFields ocrFields) {
        log.info("Beginning mapping for Adoptive Relatives collection");

        List<CollectionMember<AdoptiveRelative>> collectionMemberList = new ArrayList<>();

        if (ocrFields.getAdoptiveRelatives0name() != null
                && !ocrFields.getAdoptiveRelatives0name().isEmpty()) {
            collectionMemberList.add(buildExecutor(
                    ocrFields.getAdoptiveRelatives0name(),
                    ocrFields.getAdoptiveRelatives0relationship(),
                    getInOutValue(ocrFields.getAdoptiveRelatives0adoptedInOrOut())
            ));
        }

        if (ocrFields.getAdoptiveRelatives1name() != null
                && !ocrFields.getAdoptiveRelatives1name().isEmpty()) {
            collectionMemberList.add(buildExecutor(
                    ocrFields.getAdoptiveRelatives1name(),
                    ocrFields.getAdoptiveRelatives1relationship(),
                    getInOutValue(ocrFields.getAdoptiveRelatives1adoptedInOrOut())
            ));
        }

        if (ocrFields.getAdoptiveRelatives2name() != null
                && !ocrFields.getAdoptiveRelatives2name().isEmpty()) {
            collectionMemberList.add(buildExecutor(
                    ocrFields.getAdoptiveRelatives2name(),
                    ocrFields.getAdoptiveRelatives2relationship(),
                    getInOutValue(ocrFields.getAdoptiveRelatives2adoptedInOrOut())
            ));
        }

        if (ocrFields.getAdoptiveRelatives3name() != null
                && !ocrFields.getAdoptiveRelatives3name().isEmpty()) {
            collectionMemberList.add(buildExecutor(
                    ocrFields.getAdoptiveRelatives3name(),
                    ocrFields.getAdoptiveRelatives3relationship(),
                    getInOutValue(ocrFields.getAdoptiveRelatives3adoptedInOrOut())
            ));
        }

        if (ocrFields.getAdoptiveRelatives4name() != null
                && !ocrFields.getAdoptiveRelatives4name().isEmpty()) {
            collectionMemberList.add(buildExecutor(
                    ocrFields.getAdoptiveRelatives4name(),
                    ocrFields.getAdoptiveRelatives4relationship(),
                    getInOutValue(ocrFields.getAdoptiveRelatives4adoptedInOrOut())
            ));
        }

        if (ocrFields.getAdoptiveRelatives5name() != null
                && !ocrFields.getAdoptiveRelatives5name().isEmpty()) {
            collectionMemberList.add(buildExecutor(
                    ocrFields.getAdoptiveRelatives5name(),
                    ocrFields.getAdoptiveRelatives5relationship(),
                    getInOutValue(ocrFields.getAdoptiveRelatives5adoptedInOrOut())
            ));
        }

        return collectionMemberList;
    }

    private CollectionMember<AdoptiveRelative> buildExecutor(
            String name,
            String relationship,
            InOut inOrOut
    ) {
        AdoptiveRelative adoptiveRelative = AdoptiveRelative.builder()
                .name(name)
                .relationship(relationship)
                .adoptedInOrOut(inOrOut)
                .build();
        return new CollectionMember<>(null, adoptiveRelative);
    }

    private InOut getInOutValue(String adoptedInOutValue) throws OCRMappingException {
        if (adoptedInOutValue == null || adoptedInOutValue.isEmpty()) {
            return null;
        } else {
            boolean matchesIn = adoptedInOutValue.toLowerCase().matches("^.*\\bin\\b.*$");
            boolean matchesOut = adoptedInOutValue.toLowerCase().matches("^.*\\bout\\b.*$");
            if (matchesIn && !matchesOut) {
                return InOut.IN;
            } else if (!matchesIn && matchesOut) {
                return InOut.OUT;
            } else {
                String errorMessage = "Adopted In or Out field '" + adoptedInOutValue + "' could not be mapped to 'in' or 'out' values";
                log.error(errorMessage);
                throw new OCRMappingException(errorMessage);
            }
        }
    }
}