package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToAdditionalExecutorsNotApplying;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ExecutorNotApplying;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ExecutorNotApplyingReason;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class OCRFieldAdditionalExecutorsNotApplyingMapper {

    @SuppressWarnings("squid:S1168")
    @ToAdditionalExecutorsNotApplying
    public List<CollectionMember<ExecutorNotApplying>> toAdditionalCollectionMember(ExceptionRecordOCRFields ocrFields) {
        log.info("Beginning mapping for Additional Executor Not Applying collection");

        List<CollectionMember<ExecutorNotApplying>> collectionMemberList = new ArrayList<>();

        if (ocrFields.getExecutorsNotApplying0notApplyingExecutorName() != null
                && !ocrFields.getExecutorsNotApplying0notApplyingExecutorName().isEmpty()) {
            collectionMemberList.add(buildExecutorNotApplying(
                    ocrFields.getExecutorsNotApplying0notApplyingExecutorName(),
                    ocrFields.getExecutorsNotApplying0notApplyingExecutorReason()
            ));
        }

        if (ocrFields.getExecutorsNotApplying1notApplyingExecutorName() != null
                && !ocrFields.getExecutorsNotApplying1notApplyingExecutorName().isEmpty()) {
            log.info("Adding Executor 2");
            collectionMemberList.add(buildExecutorNotApplying(
                    ocrFields.getExecutorsNotApplying1notApplyingExecutorName(),
                    ocrFields.getExecutorsNotApplying1notApplyingExecutorReason()
            ));
        }

        if (ocrFields.getExecutorsNotApplying2notApplyingExecutorName() != null
                && !ocrFields.getExecutorsNotApplying2notApplyingExecutorName().isEmpty()) {
            log.info("Adding Executor 3");
            collectionMemberList.add(buildExecutorNotApplying(
                    ocrFields.getExecutorsNotApplying2notApplyingExecutorName(),
                    ocrFields.getExecutorsNotApplying2notApplyingExecutorReason()
            ));
        }

        return collectionMemberList;
    }

    private CollectionMember<ExecutorNotApplying> buildExecutorNotApplying(
            String executorNotApplyingName,
            String executorNotApplyingReason
    ) {
        ExecutorNotApplyingReason notApplyingReason = identifyReason(executorNotApplyingReason);
        ExecutorNotApplying notApplying = ExecutorNotApplying.builder()
                .notApplyingExecutorName(executorNotApplyingName)
                .notApplyingExecutorReason(notApplyingReason)
                .build();

        if (notApplying.getNotApplyingExecutorReason() == ExecutorNotApplyingReason.DIED_BEFORE) {
            notApplying.setNotApplyingExecutorDiedBefore(Boolean.TRUE);
        } else {
            notApplying.setNotApplyingExecutorDiedBefore(Boolean.FALSE);
        }

        if (notApplying.getNotApplyingExecutorReason() == ExecutorNotApplyingReason.DIED_AFTER) {
            notApplying.setNotApplyingExecutorIsDead(Boolean.TRUE);
        } else {
            notApplying.setNotApplyingExecutorIsDead(Boolean.FALSE);
        }

        return new CollectionMember<>(null, notApplying);
    }

    private ExecutorNotApplyingReason identifyReason(String reasonValue) {
        if (reasonValue == null || reasonValue.isEmpty()) {
            return null;
        } else {
            switch (reasonValue.toUpperCase().trim()) {
                case "A":
                    return ExecutorNotApplyingReason.DIED_BEFORE;
                case "B":
                    return ExecutorNotApplyingReason.DIED_AFTER;
                case "C":
                    return ExecutorNotApplyingReason.POWER_RESERVED;
                case "D":
                    return ExecutorNotApplyingReason.RENUNCIATION;
                case "E":
                    return ExecutorNotApplyingReason.RENUNCIATION;
                case "F":
                    return ExecutorNotApplyingReason.POWER_OF_ATTORNEY;
                default:
                    String errorMessage = "Not applying reason A, B, C, D, E, or F values expected but got '" + reasonValue + "'";
                    log.error(errorMessage);
                    throw new OCRMappingException(errorMessage);
            }
        }
    }
}