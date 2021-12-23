package uk.gov.hmcts.probate.service.ocr.pa8a;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ocr.OCRField;
import uk.gov.hmcts.probate.service.ocr.MandatoryFieldsValidatorUtils;
import uk.gov.hmcts.probate.service.ocr.OCRFieldTestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PA8ASolicitorMandatoryFieldsValidatorTest {
    private OCRFieldTestUtils ocrFieldTestUtils = new OCRFieldTestUtils();
    private ArrayList<String> warnings;

    @Mock
    private MandatoryFieldsValidatorUtils mandatoryFieldsValidatorUtils;

    @InjectMocks
    private PA8ASolicitorMandatoryFieldsValidator pa8ASolicitorMandatoryFieldsValidator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        warnings = new ArrayList<>();
    }
    
    @Test
    public void testSolictorMandatoryFieldsPA8A() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryCaveatSolicitorFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        pa8ASolicitorMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(0, warnings.size());
    }

    @Test
    public void testSolicitorMissingMandatoryFieldsPA8A() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryCaveatCitizenFields();
        ocrFields.add(
            OCRField.builder().name("solsSolicitorRepresentativeName").value("Solicitor Firm").description("Sols Firm")
                .build());
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        pa8ASolicitorMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(5, warnings.size());
        assertEquals("Solictor address line 1 (solsSolicitorAddressLine1) is mandatory.", warnings.get(0));
        assertEquals("Solictor address postcode (solsSolicitorAddressPostCode) is mandatory.", warnings.get(1));
        assertEquals("Solicitors Firm name (solsSolicitorFirmName) is mandatory.", warnings.get(2));
        assertEquals("Solictor application reference (solsSolicitorAppReference) is mandatory.", warnings.get(3));
        assertEquals("Solictor email address (solsSolicitorEmail) is mandatory.", warnings.get(4));
    }

}