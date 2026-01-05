package uk.gov.hmcts.probate.service.ocr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTOR_NOT_APPLYING_0_REASON;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTOR_NOT_APPLYING_1_REASON;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTOR_NOT_APPLYING_2_REASON;
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

class ExecutorsNotApplyingHandlerTest {
    @InjectMocks
    private ExecutorsNotApplyingHandler executorsNotApplyingHandler;
    @Mock
    private BulkScanConfig bulkScanConfig;

    private ExceptionRecordOCRFields ocrFields;
    private Field notApplyingExecutorName0;
    private Field notApplyingExecutorName1;
    private Field notApplyingExecutorName2;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        executorsNotApplyingHandler = new ExecutorsNotApplyingHandler(bulkScanConfig);

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
        when(bulkScanConfig.getSolicitorNotApplying()).thenReturn(FALSE);
        when(bulkScanConfig.getDefaultForm()).thenReturn(DEFAULT_FORM);

        Field bulkScanConfigField = ExecutorsNotApplyingHandler.class.getDeclaredField("bulkScanConfig");
        bulkScanConfigField.setAccessible(true);
        bulkScanConfigField.set(executorsNotApplyingHandler, bulkScanConfig);

        notApplyingExecutorName0 = ExceptionRecordOCRFields.class
                .getDeclaredField("executorsNotApplying0notApplyingExecutorName");
        notApplyingExecutorName0.setAccessible(true);

        notApplyingExecutorName1 = ExceptionRecordOCRFields.class
                .getDeclaredField("executorsNotApplying1notApplyingExecutorName");
        notApplyingExecutorName1.setAccessible(true);

        notApplyingExecutorName2 = ExceptionRecordOCRFields.class
                .getDeclaredField("executorsNotApplying2notApplyingExecutorName");
        notApplyingExecutorName2.setAccessible(true);


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
    void shouldSetExecutorReasonWhenExecutorNameIsPresentAndReasonIsBlank() throws IllegalAccessException {
        notApplyingExecutorName0.set(ocrFields, "Executor Name");
        ocrFields.setExecutorsNotApplying0notApplyingExecutorReason("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        executorsNotApplyingHandler.handleExecutorsNotApplyingFields(ocrFields, modifiedFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(EXECUTOR_NOT_APPLYING_0_REASON,
                modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(bulkScanConfig.getExecutorsNotApplyingReason(),
                ocrFields.getExecutorsNotApplying0notApplyingExecutorReason());
    }

    @Test
    void shouldNotModifyExecutorReasonWhenExecutorNameIsBlank() throws IllegalAccessException {
        notApplyingExecutorName0.set(ocrFields, "");
        ocrFields.setExecutorsNotApplying0notApplyingExecutorReason("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        executorsNotApplyingHandler.handleExecutorsNotApplyingFields(ocrFields, modifiedFields);

        assertEquals(0, modifiedFields.size());
        assertEquals("", ocrFields.getExecutorsNotApplying0notApplyingExecutorReason());
    }

    @Test
    void shouldHandleMultipleExecutorsNotApplyingFields() throws IllegalAccessException {
        notApplyingExecutorName0.set(ocrFields, "Executor Name0");
        notApplyingExecutorName1.set(ocrFields, "Executor Name1");
        notApplyingExecutorName2.set(ocrFields, "Executor Name2");
        ocrFields.setExecutorsNotApplying0notApplyingExecutorReason("");
        ocrFields.setExecutorsNotApplying1notApplyingExecutorReason("");
        ocrFields.setExecutorsNotApplying2notApplyingExecutorReason("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        executorsNotApplyingHandler.handleExecutorsNotApplyingFields(ocrFields, modifiedFields);

        assertEquals(3, modifiedFields.size());
        assertEquals(EXECUTOR_NOT_APPLYING_0_REASON,
                modifiedFields.get(0).getValue().getFieldName());
        assertEquals(EXECUTOR_NOT_APPLYING_1_REASON,
                modifiedFields.get(1).getValue().getFieldName());
        assertEquals(EXECUTOR_NOT_APPLYING_2_REASON,
                modifiedFields.get(2).getValue().getFieldName());
        assertEquals(bulkScanConfig.getExecutorsNotApplyingReason(),
                ocrFields.getExecutorsNotApplying0notApplyingExecutorReason());
        assertEquals(bulkScanConfig.getExecutorsNotApplyingReason(),
                ocrFields.getExecutorsNotApplying1notApplyingExecutorReason());
        assertEquals(bulkScanConfig.getExecutorsNotApplyingReason(),
                ocrFields.getExecutorsNotApplying2notApplyingExecutorReason());
    }
}
