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
import static uk.gov.hmcts.probate.model.DummyValuesConstants.PRIMARY_APPLICANT_ADDRESS_POST_CODE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.PRIMARY_APPLICANT_FORENAMES;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.PRIMARY_APPLICANT_SURNAME;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.PRIMARY_APPLICANT_HAS_ALIAS;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.PRIMARY_APPLICANT_ALIAS;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.PRIMARY_APPLICANT_ADDRESS_LINE1;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_IHT_FORM;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_DECEASED_SURNAME_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_POSTCODE_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_DOB_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_EMAIL_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_PHONE_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_DATE_OF_BIRTH;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_PRIMARY_APPLICANT_FORENAMES;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_PRIMARY_APPLICANT_SURNAME;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_PRIMARY_APPLICANT_ADDRESS_LINE_1;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_PRIMARY_APPLICANT_ADDRESS_POSTCODE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_PRIMARY_APPLICANT_ALIAS;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_PRIMARY_APPLICANT_HAS_ALIAS;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_SOLICITOR_IS_APPLYING;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_FORENAMES;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_SURNAME;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_ADDRESS_LINE_1;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_ADDRESS_POSTCODE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_DATE_OF_BIRTH;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_ANY_OTHER_NAMES;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_DOMICILED_IN_ENG_WALES;

class PrimaryApplicantFieldsHandlerTest {
    @InjectMocks
    private PrimaryApplicantFieldsHandler primaryApplicantFieldsHandler;
    @Mock
    private BulkScanConfig bulkScanConfig;
    private ExceptionRecordOCRFields ocrFields;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        primaryApplicantFieldsHandler = new PrimaryApplicantFieldsHandler(bulkScanConfig);

        when(bulkScanConfig.getIhtForm()).thenReturn(DEFAULT_IHT_FORM);
        when(bulkScanConfig.getGrossNetValue()).thenReturn(DEFAULT_VALUE);
        when(bulkScanConfig.getName()).thenReturn(DEFAULT_DECEASED_SURNAME_VALUE);
        when(bulkScanConfig.getPostcode()).thenReturn(DEFAULT_POSTCODE_VALUE);
        when(bulkScanConfig.getDob()).thenReturn(DEFAULT_DOB_VALUE);
        when(bulkScanConfig.getEmail()).thenReturn(DEFAULT_EMAIL_VALUE);
        when(bulkScanConfig.getPhone()).thenReturn(DEFAULT_PHONE_VALUE);
        when(bulkScanConfig.getDob()).thenReturn(DEFAULT_DATE_OF_BIRTH);
        when(bulkScanConfig.getDeceasedDiedOnOrAfterSwitchDateTrue()).thenReturn(TRUE);
        when(bulkScanConfig.getDeceasedDiedOnOrAfterSwitchDateFalse()).thenReturn(FALSE);
        when(bulkScanConfig.getDateOfDeathForDiedOnOrAfterSwitchDateTrue()).thenReturn("01012022");
        when(bulkScanConfig.getDateOfDeathForDiedOnOrAfterSwitchDateFalse()).thenReturn("01011990");
        when(bulkScanConfig.getSolicitorApplying()).thenReturn(FALSE);

        Field bulkScanConfigField = PrimaryApplicantFieldsHandler.class.getDeclaredField("bulkScanConfig");
        bulkScanConfigField.setAccessible(true);
        bulkScanConfigField.set(primaryApplicantFieldsHandler, bulkScanConfig);


        ocrFields = ExceptionRecordOCRFields.builder()
                .primaryApplicantForenames(VALID_PRIMARY_APPLICANT_FORENAMES)
                .primaryApplicantSurname(VALID_PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantAddressLine1(VALID_PRIMARY_APPLICANT_ADDRESS_LINE_1)
                .primaryApplicantAddressPostCode(VALID_PRIMARY_APPLICANT_ADDRESS_POSTCODE)
                .primaryApplicantAlias(VALID_PRIMARY_APPLICANT_ALIAS)
                .primaryApplicantHasAlias(VALID_PRIMARY_APPLICANT_HAS_ALIAS)
                .solsSolicitorIsApplying(VALID_SOLICITOR_IS_APPLYING)
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
    void shouldSetApplicantForenameToMissingWhenEmpty() {
        ocrFields.setSolsSolicitorIsApplying(FALSE);
        ocrFields.setPrimaryApplicantForenames("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        primaryApplicantFieldsHandler.handleGorPrimaryApplicantFields(ocrFields, modifiedFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(PRIMARY_APPLICANT_FORENAMES, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(bulkScanConfig.getName(), ocrFields.getPrimaryApplicantForenames());
    }

    @Test
    void shouldSetApplicantSurnameToMissingWhenEmpty() {
        ocrFields.setSolsSolicitorIsApplying(FALSE);
        ocrFields.setPrimaryApplicantSurname("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        primaryApplicantFieldsHandler.handleGorPrimaryApplicantFields(ocrFields, modifiedFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(PRIMARY_APPLICANT_SURNAME, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(bulkScanConfig.getName(), ocrFields.getPrimaryApplicantSurname());
    }

    @Test
    void shouldSetApplicantAddressLineToMissingWhenEmpty() {
        ocrFields.setSolsSolicitorIsApplying(FALSE);
        ocrFields.setPrimaryApplicantAddressLine1("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        primaryApplicantFieldsHandler.handleGorPrimaryApplicantFields(ocrFields, modifiedFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(PRIMARY_APPLICANT_ADDRESS_LINE1, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(bulkScanConfig.getName(), ocrFields.getPrimaryApplicantAddressLine1());
    }

    @Test
    void shouldNotSetApplicantForenameToMissingWhenValueIsPresent() {
        ocrFields.setSolsSolicitorIsApplying(TRUE);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        primaryApplicantFieldsHandler.handleGorPrimaryApplicantFields(ocrFields, modifiedFields);

        assertEquals(0, modifiedFields.size());
    }

    @Test
    void shouldSetPostcodeToM1551NGWhenEmpty() {
        ocrFields.setSolsSolicitorIsApplying(FALSE);
        ocrFields.setPrimaryApplicantAddressPostCode("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        primaryApplicantFieldsHandler.handleGorPrimaryApplicantFields(ocrFields, modifiedFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(PRIMARY_APPLICANT_ADDRESS_POST_CODE, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(DEFAULT_POSTCODE_VALUE, ocrFields.getPrimaryApplicantAddressPostCode());
    }

    @Test
    void shouldNotSetPostcodeToM1551NGWhenPostcodeIsPresent() {
        ocrFields.setSolsSolicitorIsApplying(FALSE);
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        primaryApplicantFieldsHandler.handleGorPrimaryApplicantFields(ocrFields, modifiedFields);

        assertEquals(0, modifiedFields.size());
    }

    @Test
    void shouldSetPrimaryApplicantHasAliasToDummyWhenEmpty() {
        ocrFields.setSolsSolicitorIsApplying(FALSE);
        ocrFields.setPrimaryApplicantHasAlias("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        primaryApplicantFieldsHandler.handleGorPrimaryApplicantFields(ocrFields, modifiedFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(PRIMARY_APPLICANT_HAS_ALIAS, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(bulkScanConfig.getPrimaryApplicantHasAlias(), ocrFields.getPrimaryApplicantHasAlias());
    }

    @Test
    void shouldSetPrimaryApplicantAliasToDummyWhenHasAliasIsPresent() {
        ocrFields.setSolsSolicitorIsApplying(FALSE);
        ocrFields.setPrimaryApplicantHasAlias(TRUE);
        ocrFields.setPrimaryApplicantAlias("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        primaryApplicantFieldsHandler.handleGorPrimaryApplicantFields(ocrFields, modifiedFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(PRIMARY_APPLICANT_ALIAS, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(bulkScanConfig.getName(), ocrFields.getPrimaryApplicantAlias());
    }

    @Test
    void shouldNotSetPrimaryApplicantAliasToDummyWhenHasAliasIsNo() {
        ocrFields.setSolsSolicitorIsApplying(FALSE);
        ocrFields.setPrimaryApplicantHasAlias(FALSE);
        ocrFields.setPrimaryApplicantAlias("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        primaryApplicantFieldsHandler.handleGorPrimaryApplicantFields(ocrFields, modifiedFields);

        assertEquals(0, modifiedFields.size());
        assertEquals("", ocrFields.getPrimaryApplicantAlias());
    }
}
