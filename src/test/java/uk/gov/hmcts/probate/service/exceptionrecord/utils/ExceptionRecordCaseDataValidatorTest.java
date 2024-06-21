package uk.gov.hmcts.probate.service.exceptionrecord.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.util.ArrayList;
import java.util.List;

import static org.bouncycastle.util.Longs.valueOf;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExceptionRecordCaseDataValidatorTest {

    private static final String IHT_PROBATE_NET_GREATER_THAN_GROSS =
            "The gross probate value cannot be less than the net probate value";
    private static final String IHT_ESTATE_NET_GREATER_THAN_GROSS =
            "The gross IHT value cannot be less than the net IHT value";
    private static final String IHT_NETQUALIFYING_VALUE_GREATER_THAN_ESTATE_GROSS_VAlUE =
            "Net qualifying value can't be greater than the gross amount";
    private static final String IHT_NETQUALIFYING_VALUE_GREATER_THAN_ESTATE_NET_VALUE =
            "Net qualifying value can't be greater than the net amount";
    private List<String> warnings;


    private static final Long HIGHER_VALUE = valueOf(20000);
    private static final Long LOWER_VALUE = valueOf(100);

    @BeforeEach
    public void setUp() {
        warnings = new ArrayList<>();
    }

    @Test
    void shouldThrowExceptionForIhtNetGreaterThanGross() {
        GrantOfRepresentationData casedata = GrantOfRepresentationData.builder()
                .ihtGrossValue(LOWER_VALUE)
                .ihtNetValue(HIGHER_VALUE)
                .build();
        ExceptionRecordCaseDataValidator.validateIhtValues(casedata, warnings);
        assertEquals(IHT_PROBATE_NET_GREATER_THAN_GROSS, warnings.get(0));
    }

    @Test
    void shouldThrowExceptionForIhtEstateNetGreaterThanGross() {
        GrantOfRepresentationData casedata = GrantOfRepresentationData.builder()
                .ihtEstateGrossValue(LOWER_VALUE)
                .ihtEstateNetValue(HIGHER_VALUE)
                .build();
        ExceptionRecordCaseDataValidator.validateIhtValues(casedata, warnings);
        assertEquals(IHT_ESTATE_NET_GREATER_THAN_GROSS, warnings.get(0));
    }

    @Test
    void shouldDoNothingForIhtGrossGreaterThanNet() {
        GrantOfRepresentationData casedata = GrantOfRepresentationData.builder()
                .ihtGrossValue(HIGHER_VALUE)
                .ihtNetValue(LOWER_VALUE)
                .ihtEstateGrossValue(HIGHER_VALUE)
                .ihtNetValue(LOWER_VALUE)
                .build();
        ExceptionRecordCaseDataValidator.validateIhtValues(casedata, warnings);
        assertTrue(warnings.isEmpty());
    }

    @Test
    void shouldDoNothingForIhtEstateNetQualifyingValueEqualToNetValue() {
        GrantOfRepresentationData casedata = GrantOfRepresentationData.builder()
                .ihtEstateNetValue(HIGHER_VALUE)
                .ihtEstateNetQualifyingValue(HIGHER_VALUE)
                .build();
        ExceptionRecordCaseDataValidator.validateIhtValues(casedata, warnings);
        assertTrue(warnings.isEmpty());
    }

    @Test
    void shouldDoNothingForIhtEstateNetQualifyingValueEqualToGrossValue() {
        GrantOfRepresentationData casedata = GrantOfRepresentationData.builder()
                .ihtEstateNetValue(HIGHER_VALUE)
                .ihtEstateNetQualifyingValue(HIGHER_VALUE)
                .build();
        ExceptionRecordCaseDataValidator.validateIhtValues(casedata, warnings);
        assertTrue(warnings.isEmpty());
    }

    @Test
    void shouldThrowExceptionForIhtNetQualifyingValueGraterThanGross() {
        GrantOfRepresentationData casedata = GrantOfRepresentationData.builder()
                .ihtEstateGrossValue(LOWER_VALUE)
                .ihtEstateNetQualifyingValue(HIGHER_VALUE)
                .build();
        ExceptionRecordCaseDataValidator.validateIhtValues(casedata, warnings);
        assertEquals(IHT_NETQUALIFYING_VALUE_GREATER_THAN_ESTATE_GROSS_VAlUE, warnings.get(0));
    }

    @Test
    void shouldThrowExceptionForIhtNetQualifyingValueGraterThanNet() {
        GrantOfRepresentationData casedata = GrantOfRepresentationData.builder()
                .ihtEstateNetValue(LOWER_VALUE)
                .ihtEstateNetQualifyingValue(HIGHER_VALUE)
                .build();
        ExceptionRecordCaseDataValidator.validateIhtValues(casedata, warnings);
        assertEquals(IHT_NETQUALIFYING_VALUE_GREATER_THAN_ESTATE_NET_VALUE, warnings.get(0));
    }
}
