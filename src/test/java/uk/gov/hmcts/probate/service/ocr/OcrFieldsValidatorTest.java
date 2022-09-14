package uk.gov.hmcts.probate.service.ocr;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.bulkscan.type.OcrDataField;
import uk.gov.hmcts.bulkscan.type.OcrValidationResult;
import uk.gov.hmcts.bulkscan.type.OcrValidationStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class OcrFieldsValidatorTest {

    @MockBean
    private OCRPopulatedValueMapper ocrPopulatedValueMapper;

    @MockBean
    private OCRToCCDMandatoryField ocrToCCDMandatoryField;

    @MockBean
    private NonMandatoryFieldsValidator nonMandatoryFieldsValidator;

    private List<String> warnings;

    private OcrFieldsValidator ocrFieldsValidator;

    @BeforeEach
    public void setUp() throws IOException {
        OcrDataField field1 = new OcrDataField("deceasedForenames","John");
        List<OcrDataField> ocrFields = new ArrayList<>(Collections.singleton(field1));
        warnings = new ArrayList<>(Collections.singleton("test warning"));

        when(ocrPopulatedValueMapper.ocrPopulatedValueMapper(any())).thenReturn(ocrFields);
        when(ocrToCCDMandatoryField.ocrToCCDMandatoryFields(eq(ocrFields), any())).thenReturn(EMPTY_LIST);

        ocrFieldsValidator = new OcrFieldsValidator(ocrToCCDMandatoryField, nonMandatoryFieldsValidator);
    }

    @Test
    void testNoWarningsReturnOkResponseAndSuccessResponseStateForPA1P() {

        OcrValidationResult r = ocrFieldsValidator.validateEnvelope("PA1P", OcrHelper.expectedOCRData);
        Assertions.assertEquals(r.status(), OcrValidationStatus.SUCCESS);
        Assertions.assertEquals(r.warnings(), emptyList());
        Assertions.assertEquals(r.errors(), emptyList());
    }

    @Test
    void testWarningsPopulateListAndReturnOkWithWarningsResponseState() {
        when(nonMandatoryFieldsValidator.ocrToCCDNonMandatoryWarnings(any(), any())).thenReturn(warnings);

        OcrValidationResult r = ocrFieldsValidator.validateEnvelope("PA1P", OcrHelper.expectedOCRData);
        Assertions.assertEquals(OcrValidationStatus.WARNINGS, r.status());
        Assertions.assertEquals("test warning", r.warnings().get(0));
        Assertions.assertEquals(r.errors(), emptyList());
    }

    @Test
    void testNoWarningsReturnOkResponseAndSuccessResponseStateForPA1A() {
        OcrValidationResult r = ocrFieldsValidator.validateEnvelope("PA1A", OcrHelper.expectedOCRData);
        Assertions.assertEquals(r.status(), OcrValidationStatus.SUCCESS);
        Assertions.assertEquals(r.warnings(), emptyList());
        Assertions.assertEquals(r.errors(), emptyList());
    }

    @Test
    void testNoWarningsReturnOkResponseAndSuccessResponseStateForPA8A() {
        OcrValidationResult r = ocrFieldsValidator.validateEnvelope("PA8A", OcrHelper.expectedOCRData);
        Assertions.assertEquals(r.status(), OcrValidationStatus.SUCCESS);
        Assertions.assertEquals(r.warnings(), emptyList());
        Assertions.assertEquals(r.errors(), emptyList());
    }

    @Test
    void testInvalidFormTypeThrowsNotFound() throws Exception {

        OcrValidationResult r = ocrFieldsValidator.validateEnvelope("test", OcrHelper.expectedOCRData);
        Assertions.assertEquals(r.status(), OcrValidationStatus.ERRORS);
        Assertions.assertEquals(r.errors().get(0), "Form type 'test' not found");
        Assertions.assertEquals(r.warnings(), emptyList());
    }
}
