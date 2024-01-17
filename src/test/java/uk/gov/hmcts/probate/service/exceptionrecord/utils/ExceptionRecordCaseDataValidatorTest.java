package uk.gov.hmcts.probate.service.exceptionrecord.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import static org.bouncycastle.util.Longs.valueOf;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.Assert.assertThrows;

class ExceptionRecordCaseDataValidatorTest {

    private static final String IHT_PROBATE_NET_GREATER_THAN_GROSS =
            "The gross probate value cannot be less than the net probate value";
    private static final String IHT_ESTATE_NET_GREATER_THAN_GROSS =
            "The gross IHT value cannot be less than the net IHT value";
    private static final String IHT_VALDIATION_ERROR = "IHT Values validation error";

    private static final Long HIGHER_VALUE = valueOf(20000);
    private static final Long LOWER_VALUE = valueOf(100);

    @BeforeEach
    public void setUp() {

    }

    @Test
    void shouldThrowExceptionForIhtNetGreaterThanGross() {
        GrantOfRepresentationData casedata = GrantOfRepresentationData.builder()
                .ihtGrossValue(LOWER_VALUE)
                .ihtNetValue(HIGHER_VALUE)
                .build();
        OCRMappingException exception = assertThrows(IHT_VALDIATION_ERROR,
                OCRMappingException.class,
                () -> ExceptionRecordCaseDataValidator.validateIhtValues(casedata));
        assertEquals(IHT_PROBATE_NET_GREATER_THAN_GROSS, exception.getWarnings().get(0));
    }

    @Test
    void shouldThrowExceptionForIhtEstateNetGreaterThanGross() {
        GrantOfRepresentationData casedata = GrantOfRepresentationData.builder()
                .ihtEstateGrossValue(LOWER_VALUE)
                .ihtEstateNetValue(HIGHER_VALUE)
                .build();
        OCRMappingException exception = assertThrows(IHT_VALDIATION_ERROR,
                OCRMappingException.class,
                () -> ExceptionRecordCaseDataValidator.validateIhtValues(casedata));
        assertEquals(IHT_ESTATE_NET_GREATER_THAN_GROSS, exception.getWarnings().get(0));
    }

    @Test
    void shouldDoNothingForIhtGrossGreaterThanNet() {
        GrantOfRepresentationData casedata = GrantOfRepresentationData.builder()
                .ihtGrossValue(HIGHER_VALUE)
                .ihtNetValue(LOWER_VALUE)
                .ihtEstateGrossValue(HIGHER_VALUE)
                .ihtNetValue(LOWER_VALUE)
                .build();
        assertDoesNotThrow(() -> ExceptionRecordCaseDataValidator.validateIhtValues(casedata));
    }
}
