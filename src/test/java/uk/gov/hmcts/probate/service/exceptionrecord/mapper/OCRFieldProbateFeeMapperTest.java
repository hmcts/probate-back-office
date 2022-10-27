package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.reform.probate.model.cases.caveat.ProbateFee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.hmcts.reform.probate.model.cases.caveat.ProbateFee.Constants.PROBATE_FEE_ACCOUNT_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.caveat.ProbateFee.Constants.PROBATE_FEE_CHEQUE_OR_POSTAL_ORDER_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.caveat.ProbateFee.Constants.PROBATE_FEE_IN_PERSON_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.caveat.ProbateFee.Constants.PROBATE_FEE_NOT_INCLUDED_VALUE;

class OCRFieldProbateFeeMapperTest {
    
    private OCRFieldProbateFeeMapper ocrFieldProbateFeeMapper = new OCRFieldProbateFeeMapper();

    @Test
    void shouldReturnNullForNullInput() {
        assertNull(ocrFieldProbateFeeMapper.toProbateFee(null));
    }
    
    @Test
    void shouldReturnNullForEmptyInput() {
        assertNull(ocrFieldProbateFeeMapper.toProbateFee(""));
    }
    
    @Test
    void shouldMapProbateFeeNotIncluded() {
        assertEquals(ProbateFee.PROBATE_FEE_NOT_INCLUDED, ocrFieldProbateFeeMapper
                .toProbateFee(PROBATE_FEE_NOT_INCLUDED_VALUE));
    }

    @Test
    void shouldMapProbateFeeAccount() {
        assertEquals(ProbateFee.PROBATE_FEE_ACCOUNT, ocrFieldProbateFeeMapper.toProbateFee(PROBATE_FEE_ACCOUNT_VALUE));
    }
    
    @Test
    void shouldMapProbateFeeInPerson() {
        assertEquals(ProbateFee.PROBATE_FEE_IN_PERSON, ocrFieldProbateFeeMapper
            .toProbateFee(PROBATE_FEE_IN_PERSON_VALUE));
    }
    
    @Test
    void shouldMapProbateFeeChequeOrPostalOrder() {
        assertEquals(ProbateFee.PROBATE_FEE_CHEQUE_OR_POSTAL_ORDER, ocrFieldProbateFeeMapper
                .toProbateFee(PROBATE_FEE_CHEQUE_OR_POSTAL_ORDER_VALUE));
    }
    
    @Test
    void shouldThrowOCRMappingException() {
        final var unexpectedInput = "Wibble";
        OCRMappingException expectedEx = assertThrows(OCRMappingException.class, () 
            -> ocrFieldProbateFeeMapper.toProbateFee(unexpectedInput));
        assertEquals("Unexpected probateFee value: " + unexpectedInput, expectedEx.getMessage());    
    }
}