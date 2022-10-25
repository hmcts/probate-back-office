package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToProbateFeeNotIncludedReason;
import uk.gov.hmcts.reform.probate.model.cases.caveat.ProbateFeeNotIncludedReason;

import static uk.gov.hmcts.reform.probate.model.cases.caveat.ProbateFeeNotIncludedReason.Constants.HELP_WITH_FEES_APPLIED_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.caveat.ProbateFeeNotIncludedReason.Constants.HELP_WITH_FEES_APPLYING_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.caveat.ProbateFeeNotIncludedReason.Constants.OTHER_VALUE;

@Slf4j
@Component
public class OCRFieldProbateFeeNotIncludedReasonMapper {

    @SuppressWarnings("squid:S1168")
    @ToProbateFeeNotIncludedReason
    public ProbateFeeNotIncludedReason toProbateFeeNotIncludedReason(String probateFeeNotIncludedReasonValue) {

        if (probateFeeNotIncludedReasonValue == null || probateFeeNotIncludedReasonValue.isEmpty()) {
            return null;
        } else {
            switch (probateFeeNotIncludedReasonValue.trim()) {
                case HELP_WITH_FEES_APPLIED_VALUE:
                    return ProbateFeeNotIncludedReason.HELP_WITH_FEES_APPLIED;
                case HELP_WITH_FEES_APPLYING_VALUE:
                    return ProbateFeeNotIncludedReason.HELP_WITH_FEES_APPLYING;
                case OTHER_VALUE:
                    return ProbateFeeNotIncludedReason.OTHER;
                default:
                    String errorMessage = "Unexpected probateFeeNotIncludedReason value: " 
                        + probateFeeNotIncludedReasonValue;
                    log.error(errorMessage);
                    throw new OCRMappingException(errorMessage);
            }
        }
    }
}