package uk.gov.hmcts.probate.service.ocr;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
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
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_0_ADDRESS_LINE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_0_OTHER_NAMES;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_0_ADDRESS_TOWN;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_0_ADDRESS_POST_CODE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_1_ADDRESS_LINE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_2_ADDRESS_LINE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_DECEASED_SURNAME_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_POSTCODE_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_DOB_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_EMAIL_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_PHONE_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_DATE_OF_BIRTH;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_DECEASED_ANY_OTHER_NAMES_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_DECEASED_DOMICILE_IN_ENG_WALES_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_PRIMARY_APPLICANT_FORENAMES;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_PRIMARY_APPLICANT_SURNAME;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_PRIMARY_APPLICANT_ADDRESS_LINE_1;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_PRIMARY_APPLICANT_ADDRESS_POSTCODE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_PRIMARY_APPLICANT_ALIAS;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_PRIMARY_APPLICANT_HAS_ALIAS;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_SOLICITOR_PHONE_NUMBER;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_FORENAMES;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_SURNAME;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_ADDRESS_LINE_1;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_ADDRESS_POSTCODE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_DATE_OF_BIRTH;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_ANY_OTHER_NAMES;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_DOMICILED_IN_ENG_WALES;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_FORM;

class ExecutorsApplyingHandlerTest {
    @InjectMocks
    private ExecutorsApplyingHandler executorsApplyingHandler;
    @Mock
    private BulkScanConfig bulkScanConfig;

    private ExceptionRecordOCRFields ocrFields;
    private Field applyingExecutorName0;
    private Field applyingExecutorName1;
    private Field applyingExecutorName2;

    private Field applyingExecutorName0DifferentName;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        executorsApplyingHandler = new ExecutorsApplyingHandler(bulkScanConfig);

        when(bulkScanConfig.getName()).thenReturn(DEFAULT_DECEASED_SURNAME_VALUE);
        when(bulkScanConfig.getPostcode()).thenReturn(DEFAULT_POSTCODE_VALUE);
        when(bulkScanConfig.getDob()).thenReturn(DEFAULT_DOB_VALUE);
        when(bulkScanConfig.getEmail()).thenReturn(DEFAULT_EMAIL_VALUE);
        when(bulkScanConfig.getPhone()).thenReturn(DEFAULT_PHONE_VALUE);
        when(bulkScanConfig.getDob()).thenReturn(DEFAULT_DATE_OF_BIRTH);
        when(bulkScanConfig.getDeceasedAnyOtherNames()).thenReturn(DEFAULT_DECEASED_ANY_OTHER_NAMES_VALUE);
        when(bulkScanConfig.getDeceasedDomicileInEngWales()).thenReturn(DEFAULT_DECEASED_DOMICILE_IN_ENG_WALES_VALUE);
        when(bulkScanConfig.getDateOfDeathForDiedOnOrAfterSwitchDateTrue()).thenReturn("01012022");
        when(bulkScanConfig.getDateOfDeathForDiedOnOrAfterSwitchDateFalse()).thenReturn("01011990");
        when(bulkScanConfig.getExecutorsNotApplyingReason()).thenReturn("A");
        when(bulkScanConfig.getSolicitorApplying()).thenReturn(FALSE);
        when(bulkScanConfig.getDefaultForm()).thenReturn(DEFAULT_FORM);

        Field bulkScanConfigField = ExecutorsApplyingHandler.class.getDeclaredField("bulkScanConfig");
        bulkScanConfigField.setAccessible(true);
        bulkScanConfigField.set(executorsApplyingHandler, bulkScanConfig);

        applyingExecutorName0 = ExceptionRecordOCRFields.class
                .getDeclaredField("executorsApplying0applyingExecutorName");
        applyingExecutorName0.setAccessible(true);

        applyingExecutorName0DifferentName = ExceptionRecordOCRFields.class
                .getDeclaredField("executorsApplying0applyingExecutorDifferentNameToWill");
        applyingExecutorName0DifferentName.setAccessible(true);

        applyingExecutorName1 = ExceptionRecordOCRFields.class
                .getDeclaredField("executorsApplying1applyingExecutorName");
        applyingExecutorName1.setAccessible(true);

        applyingExecutorName2 = ExceptionRecordOCRFields.class
                .getDeclaredField("executorsApplying2applyingExecutorName");
        applyingExecutorName2.setAccessible(true);


        ocrFields = ExceptionRecordOCRFields.builder()
                .primaryApplicantForenames(VALID_PRIMARY_APPLICANT_FORENAMES)
                .primaryApplicantSurname(VALID_PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantAddressLine1(VALID_PRIMARY_APPLICANT_ADDRESS_LINE_1)
                .primaryApplicantAddressPostCode(VALID_PRIMARY_APPLICANT_ADDRESS_POSTCODE)
                .primaryApplicantAlias(VALID_PRIMARY_APPLICANT_ALIAS)
                .primaryApplicantHasAlias(VALID_PRIMARY_APPLICANT_HAS_ALIAS)
                .solsSolicitorPhoneNumber(VALID_SOLICITOR_PHONE_NUMBER)
                .deceasedForenames(VALID_DECEASED_FORENAMES)
                .deceasedSurname(VALID_DECEASED_SURNAME)
                .deceasedAddressLine1(VALID_DECEASED_ADDRESS_LINE_1)
                .deceasedAddressPostCode(VALID_DECEASED_ADDRESS_POSTCODE)
                .deceasedDateOfBirth(VALID_DECEASED_DATE_OF_BIRTH)
                .deceasedAnyOtherNames(VALID_DECEASED_ANY_OTHER_NAMES)
                .deceasedDomicileInEngWales(VALID_DECEASED_DOMICILED_IN_ENG_WALES)
                .deceasedDateOfDeath("01012022")
                .deceasedDiedOnAfterSwitchDate(TRUE)
                .build();
    }

    @Test
    void shouldSetExecutorAddressFieldsWhenExecutorNameIsPresent() throws IllegalAccessException {
        applyingExecutorName0.set(ocrFields, "Executor Name");
        ocrFields.setExecutorsApplying0applyingExecutorAddressLine1("");
        ocrFields.setExecutorsApplying0applyingExecutorAddressTown("");
        ocrFields.setExecutorsApplying0applyingExecutorAddressPostCode("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        executorsApplyingHandler.handleExecutorsApplyingFields(ocrFields, modifiedFields);

        assertEquals(3, modifiedFields.size());
        assertEquals(EXECUTORS_APPLYING_0_ADDRESS_LINE, modifiedFields.get(0).getValue()
                .getFieldName());
        assertEquals(EXECUTORS_APPLYING_0_ADDRESS_TOWN,
                modifiedFields.get(1).getValue().getFieldName());
        assertEquals(EXECUTORS_APPLYING_0_ADDRESS_POST_CODE,
                modifiedFields.get(2).getValue().getFieldName());
        assertEquals(bulkScanConfig.getName(), ocrFields.getExecutorsApplying0applyingExecutorAddressLine1());
        assertEquals(bulkScanConfig.getName(), ocrFields.getExecutorsApplying0applyingExecutorAddressTown());
        assertEquals(bulkScanConfig.getPostcode(), ocrFields.getExecutorsApplying0applyingExecutorAddressPostCode());
    }

    @Test
    void shouldSetExecutorOtherNameFieldsWhenExecutorDifferentNameIsPresent() throws IllegalAccessException {
        applyingExecutorName0.set(ocrFields, "Executor Name");
        applyingExecutorName0DifferentName.set(ocrFields, TRUE);
        ocrFields.setExecutorsApplying0applyingExecutorOtherNames("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        executorsApplyingHandler.handleExecutorsApplyingFields(ocrFields, modifiedFields);

        assertEquals(4, modifiedFields.size());
        assertEquals(EXECUTORS_APPLYING_0_OTHER_NAMES, modifiedFields.getFirst().getValue()
                .getFieldName());
        assertEquals(bulkScanConfig.getName(), ocrFields.getExecutorsApplying0applyingExecutorOtherNames());
    }

    @Test
    void shouldNotModifyExecutorAddressFieldsWhenExecutorNameIsBlank() throws IllegalAccessException {
        applyingExecutorName0.set(ocrFields, "");
        ocrFields.setExecutorsApplying0applyingExecutorAddressLine1("");
        ocrFields.setExecutorsApplying0applyingExecutorAddressTown("");
        ocrFields.setExecutorsApplying0applyingExecutorAddressPostCode("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        executorsApplyingHandler.handleExecutorsApplyingFields(ocrFields, modifiedFields);

        assertEquals(0, modifiedFields.size());
        assertEquals("", ocrFields.getExecutorsApplying0applyingExecutorAddressLine1());
        assertEquals("", ocrFields.getExecutorsApplying0applyingExecutorAddressTown());
        assertEquals("", ocrFields.getExecutorsApplying0applyingExecutorAddressPostCode());
    }

    @Test
    void shouldHandleMultipleExecutorsApplyingFields() throws IllegalAccessException {
        applyingExecutorName0.set(ocrFields, "Executor Name0");
        applyingExecutorName1.set(ocrFields, "Executor Name1");
        applyingExecutorName2.set(ocrFields, "Executor Name2");
        ocrFields.setExecutorsApplying0applyingExecutorAddressLine1("");
        ocrFields.setExecutorsApplying1applyingExecutorAddressLine1("");
        ocrFields.setExecutorsApplying2applyingExecutorAddressLine1("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        executorsApplyingHandler.handleExecutorsApplyingFields(ocrFields, modifiedFields);

        assertEquals(9, modifiedFields.size());
        assertEquals(EXECUTORS_APPLYING_0_ADDRESS_LINE,
                modifiedFields.get(0).getValue().getFieldName());
        assertEquals(EXECUTORS_APPLYING_1_ADDRESS_LINE,
                modifiedFields.get(3).getValue().getFieldName());
        assertEquals(EXECUTORS_APPLYING_2_ADDRESS_LINE,
                modifiedFields.get(6).getValue().getFieldName());
        assertEquals(bulkScanConfig.getName(), ocrFields.getExecutorsApplying0applyingExecutorAddressLine1());
        assertEquals(bulkScanConfig.getName(), ocrFields.getExecutorsApplying1applyingExecutorAddressLine1());
        assertEquals(bulkScanConfig.getName(), ocrFields.getExecutorsApplying2applyingExecutorAddressLine1());
    }
}
