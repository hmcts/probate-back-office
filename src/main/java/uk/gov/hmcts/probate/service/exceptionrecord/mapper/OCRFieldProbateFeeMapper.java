package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToProbateFee;
import uk.gov.hmcts.reform.probate.model.cases.caveat.ProbateFee;

import static uk.gov.hmcts.reform.probate.model.cases.caveat.ProbateFee.Constants.PROBATE_FEE_ACCOUNT_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.caveat.ProbateFee.Constants.PROBATE_FEE_CHEQUE_OR_POSTAL_ORDER_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.caveat.ProbateFee.Constants.PROBATE_FEE_IN_PERSON_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.caveat.ProbateFee.Constants.PROBATE_FEE_NOT_INCLUDED_VALUE;

@Slf4j
@Component
public class OCRFieldProbateFeeMapper {

    @SuppressWarnings("squid:S1168")
    @ToProbateFee
    public ProbateFee toProbateFee(String probateFeeValue) {
        if (probateFeeValue == null || probateFeeValue.isEmpty()) {
            return null;
        } else {
            switch (probateFeeValue.trim()) {
                case PROBATE_FEE_NOT_INCLUDED_VALUE:
                    return ProbateFee.PROBATE_FEE_NOT_INCLUDED;
                case PROBATE_FEE_CHEQUE_OR_POSTAL_ORDER_VALUE:
                    return ProbateFee.PROBATE_FEE_CHEQUE_OR_POSTAL_ORDER;
                case PROBATE_FEE_IN_PERSON_VALUE:
                    return ProbateFee.PROBATE_FEE_IN_PERSON;
                case PROBATE_FEE_ACCOUNT_VALUE:
                    return ProbateFee.PROBATE_FEE_ACCOUNT;
                default:
                    String errorMessage = "Unexpected probateFee value: " + probateFeeValue;
                    log.error(errorMessage);
                    throw new OCRMappingException(errorMessage);
            }
        }
    }

}