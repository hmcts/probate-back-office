package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToProbateFee;
import uk.gov.hmcts.reform.probate.model.cases.caveat.ProbateFee;

@Slf4j
@Component
public class OCRFieldProbateFeeMapper {

    private static final String PROBATE_FEE_NOT_INCLUDED_VALUE = "probateFeeNotIncluded";
    private static final String PROBATE_FEE_CHEQUE_OR_POSTAL_ORDER_VALUE = "probateFeeChequeOrPostalOrder";
    private static final String PROBATE_FEE_IN_PERSON_VALUE = "probateFeeInPerson";
    private static final String PROBATE_FEE_ACCOUNT_VALUE = "probateFeeAccount";

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
                    String errorMessage = "probateFee 'probateFeeNotIncluded', 'probateFeeChequeOrPostalOrder', 'probateFeeInPerson', "
                        + " or 'probateFeeAccount' expected but got '" + probateFeeValue + "'";
                    log.error(errorMessage);
                    throw new OCRMappingException(errorMessage);
            }
        }
    }

}