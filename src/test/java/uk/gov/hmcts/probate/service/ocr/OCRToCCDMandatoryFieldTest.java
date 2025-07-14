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
import uk.gov.hmcts.probate.service.ocr.pa8a.PA8ACitizenMandatoryFieldsValidator;
import uk.gov.hmcts.probate.service.ocr.pa8a.PA8ASolicitorMandatoryFieldsValidator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

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
    @Mock
    private PA8ACitizenMandatoryFieldsValidator pa8ACitizenMandatoryFieldsValidator;
    @Mock
    private PA8ASolicitorMandatoryFieldsValidator pa8ASolicitorMandatoryFieldsValidator;

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

        ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA1P);
        verify(pa1PCitizenMandatoryFieldsValidator).addWarnings(any(), any());
        verifyNoInteractions(pa1PSolicitorMandatoryFieldsValidator);
        verifyNoInteractions(pa1ACitizenMandatoryFieldsValidator);
        verifyNoInteractions(pa1ASolicitorMandatoryFieldsValidator);
        verifyNoInteractions(pa1ACommonMandatoryFieldsValidator);
        verifyNoInteractions(pa8ACitizenMandatoryFieldsValidator);
        verifyNoInteractions(pa8ASolicitorMandatoryFieldsValidator);

    }

    @Test
    void testSolicitorAllMandatoryFieldsPresentPA1P() {
        ocrFields = ocrFieldTestUtils.addAllMandatoryGORSolicitorFields();
        ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA1P);
        verifyNoInteractions(pa1PCitizenMandatoryFieldsValidator);
        verify(pa1PSolicitorMandatoryFieldsValidator).addWarnings(any(), any());
        verifyNoInteractions(pa1ACitizenMandatoryFieldsValidator);
        verifyNoInteractions(pa1ASolicitorMandatoryFieldsValidator);
        verifyNoInteractions(pa1ACommonMandatoryFieldsValidator);
        verifyNoInteractions(pa8ACitizenMandatoryFieldsValidator);
        verifyNoInteractions(pa8ASolicitorMandatoryFieldsValidator);
    }

    @Test
    void testCitizenMandatoryFieldsPresentPA1A() {
        ocrFields = ocrFieldTestUtils.addAllMandatoryIntestacyCitizenFields();
        ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA1A);
        verifyNoInteractions(pa1PCitizenMandatoryFieldsValidator);
        verifyNoInteractions(pa1PSolicitorMandatoryFieldsValidator);
        verifyNoInteractions(pa1PCommonMandatoryFieldsValidator);
        verify(pa1ACitizenMandatoryFieldsValidator).addWarnings(any(), any());
        verifyNoInteractions(pa1ASolicitorMandatoryFieldsValidator);
        verifyNoInteractions(pa8ACitizenMandatoryFieldsValidator);
        verifyNoInteractions(pa8ASolicitorMandatoryFieldsValidator);
    }

    @Test
    void testSolicitorAllMandatoryFieldsPresentPA1A() {
        ocrFields = ocrFieldTestUtils.addAllMandatoryIntestacySolicitorFields();
        ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA1A);
        verifyNoInteractions(pa1PCitizenMandatoryFieldsValidator);
        verifyNoInteractions(pa1PSolicitorMandatoryFieldsValidator);
        verifyNoInteractions(pa1PCommonMandatoryFieldsValidator);
        verifyNoInteractions(pa1ACitizenMandatoryFieldsValidator);
        verify(pa1ASolicitorMandatoryFieldsValidator).addWarnings(any(), any());
        verifyNoInteractions(pa8ACitizenMandatoryFieldsValidator);
        verifyNoInteractions(pa8ASolicitorMandatoryFieldsValidator);
    }

    @Test
    void testCitizenMandatoryFieldsPresentPA8A() {
        ocrFields = ocrFieldTestUtils.addAllMandatoryCaveatCitizenFields();
        ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA8A);
        verifyNoInteractions(pa1PCitizenMandatoryFieldsValidator);
        verifyNoInteractions(pa1PSolicitorMandatoryFieldsValidator);
        verifyNoInteractions(pa1PCommonMandatoryFieldsValidator);
        verifyNoInteractions(pa1ACitizenMandatoryFieldsValidator);
        verifyNoInteractions(pa1ASolicitorMandatoryFieldsValidator);
        verifyNoInteractions(pa1ACommonMandatoryFieldsValidator);
    }

    @Test
    void testSolicitorMandatoryFieldsPresentPA8A() {
        ocrFields = ocrFieldTestUtils.addAllMandatoryCaveatSolicitorFields();

        assertEquals(0, ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA8A).size());
        verifyNoInteractions(pa1PCitizenMandatoryFieldsValidator);
        verifyNoInteractions(pa1PSolicitorMandatoryFieldsValidator);
        verifyNoInteractions(pa1PCommonMandatoryFieldsValidator);
        verifyNoInteractions(pa1ACitizenMandatoryFieldsValidator);
        verifyNoInteractions(pa1ASolicitorMandatoryFieldsValidator);
        verifyNoInteractions(pa1ACommonMandatoryFieldsValidator);
    }
}
