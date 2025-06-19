package uk.gov.hmcts.probate.service.ocr;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.config.BulkScanConfig;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ModifiedOCRField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.FALSE;
import static uk.gov.hmcts.probate.model.Constants.TRUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.CAVEATOR_ADDRESS_LINE1;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.CAVEATOR_POST_CODE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_POSTCODE_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_DOB_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_EMAIL_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_PHONE_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_DATE_OF_BIRTH;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_FORENAMES;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_SURNAME;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_ADDRESS_LINE_1;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_ADDRESS_POSTCODE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_DATE_OF_BIRTH;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_CAVEATOR_FORENAMES;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_CAVEATOR_SURNAME;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_CAVEATOR_ADDRESS_LINE_1;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_CAVEATOR_ADDRESS_POSTCODE;

@Component
@RequiredArgsConstructor
public class CaveatCitizenAddressHandlerTest {

    @InjectMocks
    private CaveatCitizenAddressHandler caveatCitizenAddressHandler;
    @Mock
    private BulkScanConfig bulkScanConfig;

    private ExceptionRecordOCRFields ocrFields;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        caveatCitizenAddressHandler = new CaveatCitizenAddressHandler(bulkScanConfig);

        when(bulkScanConfig.getPostcode()).thenReturn(DEFAULT_POSTCODE_VALUE);
        when(bulkScanConfig.getDob()).thenReturn(DEFAULT_DOB_VALUE);
        when(bulkScanConfig.getEmail()).thenReturn(DEFAULT_EMAIL_VALUE);
        when(bulkScanConfig.getPhone()).thenReturn(DEFAULT_PHONE_VALUE);
        when(bulkScanConfig.getDob()).thenReturn(DEFAULT_DATE_OF_BIRTH);
        when(bulkScanConfig.getDateOfDeathForDiedOnOrAfterSwitchDateTrue()).thenReturn("01012022");
        when(bulkScanConfig.getDateOfDeathForDiedOnOrAfterSwitchDateFalse()).thenReturn("01011990");

        Field bulkScanConfigField = CaveatCitizenAddressHandler.class.getDeclaredField("bulkScanConfig");
        bulkScanConfigField.setAccessible(true);
        bulkScanConfigField.set(caveatCitizenAddressHandler, bulkScanConfig);

        ocrFields = ExceptionRecordOCRFields.builder()
                .deceasedForenames(VALID_DECEASED_FORENAMES)
                .deceasedSurname(VALID_DECEASED_SURNAME)
                .deceasedAddressLine1(VALID_DECEASED_ADDRESS_LINE_1)
                .deceasedAddressPostCode(VALID_DECEASED_ADDRESS_POSTCODE)
                .deceasedDateOfBirth(VALID_DECEASED_DATE_OF_BIRTH)
                .caveatorForenames(VALID_CAVEATOR_FORENAMES)
                .caveatorSurnames(VALID_CAVEATOR_SURNAME)
                .caveatorAddressLine1(VALID_CAVEATOR_ADDRESS_LINE_1)
                .caveatorAddressPostCode(VALID_CAVEATOR_ADDRESS_POSTCODE)
                .deceasedDateOfDeath("01012022")
                .deceasedDiedOnAfterSwitchDate(TRUE)
                .build();
    }

    @Test
    void shouldSetCitizenAddressFieldsWhenLegalRepresentativeIsFalse() {
        ocrFields.setLegalRepresentative(FALSE);
        ocrFields.setCaveatorAddressLine1("");
        ocrFields.setCaveatorAddressPostCode("");
        ocrFields.setDeceasedDateOfDeath(VALID_DECEASED_DATE_OF_BIRTH);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        caveatCitizenAddressHandler.handleCaveatCitizenAddressFields(ocrFields, modifiedFields);

        assertEquals(2, modifiedFields.size());
        assertEquals(CAVEATOR_ADDRESS_LINE1, modifiedFields.get(0).getValue().getFieldName());
        assertEquals(CAVEATOR_POST_CODE, modifiedFields.get(1).getValue().getFieldName());
    }

}