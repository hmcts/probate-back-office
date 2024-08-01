package uk.gov.hmcts.probate.service.exceptionrecord.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.CaseType;

import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;
import uk.gov.hmcts.probate.model.exceptionrecord.InputScannedDoc;
import java.util.ArrayList;
import java.util.List;


import static org.bouncycastle.util.Longs.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ExceptionRecordCaseDataValidatorTest {

    private static final String IHT_PROBATE_NET_GREATER_THAN_GROSS =
            "The gross probate value cannot be less than the net probate value";
    private static final String IHT_ESTATE_NET_GREATER_THAN_GROSS =
            "The gross IHT value cannot be less than the net IHT value";
    private static final String IHT_VALDIATION_ERROR = "IHT Values validation error";

    private static final String IHT_NETQUALIFYING_VALUE_GREATER_THAN_ESTATE_GROSS_VAlUE =
            "Net qualifying value can't be greater than the gross amount";
    private static final String IHT_NETQUALIFYING_VALUE_GREATER_THAN_ESTATE_NET_VALUE =
            "Net qualifying value can't be greater than the net amount";


    private static final String SCANNED_DOCUMENT_TYPE_VALDIATION_ERROR = "Scan Document Type validation error";
    private static final String INVALID_SCAN_DOC_GOP =
            "Invalid scanned Document Type Error for case type 'GRANT_OF_REPRESENTATION': [invalid]";
    private static final String INVALID_SCAN_DOC_CAVEAT =
            "Invalid scanned Document Type Error for case type 'CAVEAT': [will, invalid]";
    private static final Long HIGHER_VALUE = valueOf(20000);
    private static final Long LOWER_VALUE = valueOf(100);


    private static final InputScannedDoc inputScannedDocWill = new InputScannedDoc("will",
            "",null,"","",null,null);
    private static final InputScannedDoc inputScannedDocCherished = new InputScannedDoc("cherished",
            "",null,"","",null,null);

    private static final InputScannedDoc inputScannedDocInvalid = new InputScannedDoc("invalid",
            "",null,"","",null,null);

    @BeforeEach
    public void setUp() {

    }

    @Test
    void shouldThrowExceptionForIhtNetGreaterThanGross() {
        GrantOfRepresentationData casedata = GrantOfRepresentationData.builder()
                .ihtGrossValue(LOWER_VALUE)
                .ihtNetValue(HIGHER_VALUE)
                .build();
        OCRMappingException exception = assertThrows(OCRMappingException.class,
                () -> ExceptionRecordCaseDataValidator.validateIhtValues(casedata));
        assertEquals(IHT_PROBATE_NET_GREATER_THAN_GROSS, exception.getWarnings().get(0));
    }

    @Test
    void shouldThrowExceptionForIhtEstateNetGreaterThanGross() {
        GrantOfRepresentationData casedata = GrantOfRepresentationData.builder()
                .ihtEstateGrossValue(LOWER_VALUE)
                .ihtEstateNetValue(HIGHER_VALUE)
                .build();
        OCRMappingException exception = assertThrows(OCRMappingException.class,
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


    @Test
    void shouldDoNothingForCorrectScannedDocumentType() {

        List<InputScannedDoc> inputScannedDocWillList = new ArrayList<>(1);
        inputScannedDocWillList.add(inputScannedDocWill);
        assertDoesNotThrow(() -> ExceptionRecordCaseDataValidator
                .validateInputScannedDocumentTypes(inputScannedDocWillList, CaseType.GRANT_OF_REPRESENTATION));

        List<InputScannedDoc> inputScannedDocCherishedList = new ArrayList<>(1);
        inputScannedDocCherishedList.add(inputScannedDocCherished);
        assertDoesNotThrow(() -> ExceptionRecordCaseDataValidator
                .validateInputScannedDocumentTypes(inputScannedDocCherishedList, CaseType.CAVEAT));
    }

    @Test
    void shouldThrowExceptionForGopInvalidScanDoc() {
        List<InputScannedDoc> invalidInputScannedDocWillList = new ArrayList<>(2);
        invalidInputScannedDocWillList.add(inputScannedDocWill);
        invalidInputScannedDocWillList.add(inputScannedDocInvalid);
        OCRMappingException exception = assertThrows(OCRMappingException.class, () -> ExceptionRecordCaseDataValidator
                        .validateInputScannedDocumentTypes(invalidInputScannedDocWillList,
                                CaseType.GRANT_OF_REPRESENTATION));
        assertEquals(INVALID_SCAN_DOC_GOP, exception.getWarnings().get(0));
    }

    @Test
    void shouldThrowExceptionForCaveatInvalidScanDoc() {
        List<InputScannedDoc> invalidInputScannedDocWillList = new ArrayList<>(2);
        invalidInputScannedDocWillList.add(inputScannedDocWill);
        invalidInputScannedDocWillList.add(inputScannedDocInvalid);
        OCRMappingException exception = assertThrows(
                OCRMappingException.class, () -> ExceptionRecordCaseDataValidator
                        .validateInputScannedDocumentTypes(invalidInputScannedDocWillList, CaseType.CAVEAT));
        assertEquals(INVALID_SCAN_DOC_CAVEAT, exception.getWarnings().get(0));
    }

    @Test
    void shouldDoNothingForIhtEstateNetQualifyingValueEqualToNetValue() {
        GrantOfRepresentationData casedata = GrantOfRepresentationData.builder()
                .ihtEstateNetValue(HIGHER_VALUE)
                .ihtEstateNetQualifyingValue(HIGHER_VALUE)
                .build();
        assertDoesNotThrow(() -> ExceptionRecordCaseDataValidator.validateIhtValues(casedata));
    }

    @Test
    void shouldDoNothingForIhtEstateNetQualifyingValueEqualToGrossValue() {
        GrantOfRepresentationData casedata = GrantOfRepresentationData.builder()
                .ihtEstateNetValue(HIGHER_VALUE)
                .ihtEstateNetQualifyingValue(HIGHER_VALUE)
                .build();
        assertDoesNotThrow(() -> ExceptionRecordCaseDataValidator.validateIhtValues(casedata));
    }

    @Test
    void shouldThrowExceptionForIhtNetQualifyingValueGraterThanGross() {
        GrantOfRepresentationData casedata = GrantOfRepresentationData.builder()
                .ihtEstateGrossValue(LOWER_VALUE)
                .ihtEstateNetQualifyingValue(HIGHER_VALUE)
                .build();
        OCRMappingException exception = assertThrows(OCRMappingException.class,
                () -> ExceptionRecordCaseDataValidator.validateIhtValues(casedata));
        assertEquals(IHT_NETQUALIFYING_VALUE_GREATER_THAN_ESTATE_GROSS_VAlUE, exception.getWarnings().get(0));
    }

    @Test
    void shouldThrowExceptionForIhtNetQualifyingValueGraterThanNet() {
        GrantOfRepresentationData casedata = GrantOfRepresentationData.builder()
                .ihtEstateNetValue(LOWER_VALUE)
                .ihtEstateNetQualifyingValue(HIGHER_VALUE)
                .build();
        OCRMappingException exception = assertThrows(OCRMappingException.class,
                () -> ExceptionRecordCaseDataValidator.validateIhtValues(casedata));
        assertEquals(IHT_NETQUALIFYING_VALUE_GREATER_THAN_ESTATE_NET_VALUE, exception.getWarnings().get(0));
    }
}
