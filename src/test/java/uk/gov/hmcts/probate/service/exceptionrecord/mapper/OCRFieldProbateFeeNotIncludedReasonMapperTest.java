package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.reform.probate.model.cases.caveat.ProbateFeeNotIncludedReason;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.hmcts.reform.probate.model.cases.caveat.ProbateFeeNotIncludedReason.Constants.HELP_WITH_FEES_APPLIED_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.caveat.ProbateFeeNotIncludedReason.Constants.HELP_WITH_FEES_APPLYING_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.caveat.ProbateFeeNotIncludedReason.Constants.OTHER_VALUE;

class OCRFieldProbateFeeNotIncludedReasonMapperTest {
    
    private OCRFieldProbateFeeNotIncludedReasonMapper ocrFieldProbateFeeNotIncludedReasonMapper 
        = new OCRFieldProbateFeeNotIncludedReasonMapper();

    @Test
    void shouldReturnNullForNullInput() {
        assertNull(ocrFieldProbateFeeNotIncludedReasonMapper.toProbateFeeNotIncludedReason(null));
    }

    @Test
    void shouldReturnNullForEmptyInput() {
        assertNull(ocrFieldProbateFeeNotIncludedReasonMapper.toProbateFeeNotIncludedReason(""));
    }

    @Test
    void shouldMapProbateFeeNotIncluded() {
        assertEquals(ProbateFeeNotIncludedReason.HELP_WITH_FEES_APPLIED, ocrFieldProbateFeeNotIncludedReasonMapper
                .toProbateFeeNotIncludedReason(HELP_WITH_FEES_APPLIED_VALUE));
    }

    @Test
    void shouldMapProbateFeeAccount() {
        assertEquals(ProbateFeeNotIncludedReason.HELP_WITH_FEES_APPLYING, ocrFieldProbateFeeNotIncludedReasonMapper
                .toProbateFeeNotIncludedReason(HELP_WITH_FEES_APPLYING_VALUE));
    }

    @Test
    void shouldMapProbateFeeInPerson() {
        assertEquals(ProbateFeeNotIncludedReason.OTHER, ocrFieldProbateFeeNotIncludedReasonMapper
                .toProbateFeeNotIncludedReason(OTHER_VALUE));
    }
    
    @Test
    void shouldThrowOCRMappingException() {
        final var unexpectedInput = "Wibble";
        OCRMappingException expectedEx = assertThrows(OCRMappingException.class, () 
            -> ocrFieldProbateFeeNotIncludedReasonMapper.toProbateFeeNotIncludedReason(unexpectedInput));
        assertEquals("Unexpected probateFeeNotIncludedReason value: " + unexpectedInput, expectedEx.getMessage());
    }
}