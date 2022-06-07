package uk.gov.hmcts.probate.service.ocr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ocr.OCRField;
import uk.gov.hmcts.probate.service.ocr.pa1a.PA1ACitizenMandatoryFieldsValidator;
import uk.gov.hmcts.probate.service.ocr.pa1a.PA1ACommonMandatoryFieldsValidator;
import uk.gov.hmcts.probate.service.ocr.pa1a.PA1ASolicitorMandatoryFieldsValidator;
import uk.gov.hmcts.probate.service.ocr.pa1p.PA1PCitizenMandatoryFieldsValidator;
import uk.gov.hmcts.probate.service.ocr.pa1p.PA1PCommonMandatoryFieldsValidator;
import uk.gov.hmcts.probate.service.ocr.pa1p.PA1PSolicitorMandatoryFieldsValidator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class OCRToCCDMandatoryFieldTest {

    private List<OCRField> ocrFields;
    private OCRFieldTestUtils ocrFieldTestUtils = new OCRFieldTestUtils();

    @Mock
    private PA1PCitizenMandatoryFieldsValidator pa1PCitizenMandatoryFieldsValidator;
    @Mock
    private PA1PSolicitorMandatoryFieldsValidator pa1PSolicitorMandatoryFieldsValidator;
    @Mock
    private PA1PCommonMandatoryFieldsValidator pa1PCommonMandatoryFieldsValidator;
    @Mock
    private PA1ACitizenMandatoryFieldsValidator pa1ACitizenMandatoryFieldsValidator;
    @Mock
    private PA1ASolicitorMandatoryFieldsValidator pa1ASolicitorMandatoryFieldsValidator;
    @Mock
    private PA1ACommonMandatoryFieldsValidator pa1ACommonMandatoryFieldsValidator;

    @InjectMocks
    private OCRToCCDMandatoryField ocrToCCDMandatoryField;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ocrFields = new ArrayList<>();
    }

    @Test
    void testCitizenMandatoryFieldsPresentPA1P() {
        ocrFields = ocrFieldTestUtils.addAllMandatoryGORCitizenFields();

        assertEquals(0, ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA1P).size());
        verify(pa1PCitizenMandatoryFieldsValidator).addWarnings(any(), any());
        verify(pa1PCommonMandatoryFieldsValidator).addWarnings(any(), any());
    }

    @Test
    void testSolicitorAllMandatoryFieldsPresentPA1P() {
        ocrFields = ocrFieldTestUtils.addAllMandatoryGORSolicitorFields();

        assertEquals(0, ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA1P).size());
        verify(pa1PSolicitorMandatoryFieldsValidator).addWarnings(any(), any());
        verify(pa1PCommonMandatoryFieldsValidator).addWarnings(any(), any());
    }

    @Test
    void testCitizenMandatoryFieldsPresentPA1A() {
        ocrFields = ocrFieldTestUtils.addAllMandatoryIntestacyCitizenFields();

        assertEquals(0, ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA1A).size());
        verify(pa1ACitizenMandatoryFieldsValidator).addWarnings(any(), any());
        verify(pa1ACommonMandatoryFieldsValidator).addWarnings(any(), any());
    }

    @Test
    void testSolicitorAllMandatoryFieldsPresentPA1A() {
        ocrFields = ocrFieldTestUtils.addAllMandatoryIntestacySolicitorFields();

        assertEquals(0, ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA1A).size());
        verify(pa1ASolicitorMandatoryFieldsValidator).addWarnings(any(), any());
        verify(pa1ACommonMandatoryFieldsValidator).addWarnings(any(), any());
    }

    @Test
    void testCitizenMandatoryFieldsPresentPA8A() {
        ocrFields = ocrFieldTestUtils.addAllMandatoryCaveatCitizenFields();

        assertEquals(0, ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA1A).size());
        verify(pa1ACitizenMandatoryFieldsValidator).addWarnings(any(), any());
        verify(pa1ACommonMandatoryFieldsValidator).addWarnings(any(), any());
    }

    @Test
    void testSolicitorMandatoryFieldsPresentPA8A() {
        ocrFields = ocrFieldTestUtils.addAllMandatoryCaveatCitizenFields();

        assertEquals(0, ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA1A).size());
        verify(pa1ACitizenMandatoryFieldsValidator).addWarnings(any(), any());
        verify(pa1ACommonMandatoryFieldsValidator).addWarnings(any(), any());
    }
}
