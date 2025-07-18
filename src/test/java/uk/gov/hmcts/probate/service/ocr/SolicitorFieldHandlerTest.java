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
import static uk.gov.hmcts.probate.model.DummyValuesConstants.APPLYING_ATTORNEY;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_REPRESENTATIVE_NAME;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_APP_REFERENCE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_ADDRESS_LINE1;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_ADDRESS_POST_CODE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_PHONE_NUMBER;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_DECEASED_SURNAME_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_POSTCODE_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_DOB_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_EMAIL_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_PHONE_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_DECEASED_ANY_OTHER_NAMES_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_DECEASED_DOMICILE_IN_ENG_WALES_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_PRIMARY_APPLICANT_FORENAMES;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_PRIMARY_APPLICANT_SURNAME;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_PRIMARY_APPLICANT_ADDRESS_LINE_1;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_PRIMARY_APPLICANT_ADDRESS_POSTCODE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_PRIMARY_APPLICANT_ALIAS;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_PRIMARY_APPLICANT_HAS_ALIAS;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_SOLICITOR_IS_APPLYING;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_SOLICITOR_FIRM_NAME;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_SOLICITOR_REPRESENTATIVE_NAME;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_SOLICITOR_APP_REFERENCE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_SOLICITOR_ADDRESS_LINE_1;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_SOLICITOR_ADDRESS_LINE_2;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_SOLICITOR_ADDRESS_TOWN;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_SOLICITOR_ADDRESS_POSTCODE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_SOLICITOR_EMAIL;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_SOLICITOR_PHONE_NUMBER;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_FORENAMES;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_SURNAME;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_ADDRESS_LINE_1;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_ADDRESS_POSTCODE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_DATE_OF_BIRTH;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_ANY_OTHER_NAMES;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_DECEASED_DOMICILED_IN_ENG_WALES;



class SolicitorFieldHandlerTest {
    @InjectMocks
    private SolicitorFieldHandler solicitorFieldHandler;
    @Mock
    private BulkScanConfig bulkScanConfig;

    private ExceptionRecordOCRFields ocrFields;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        solicitorFieldHandler = new SolicitorFieldHandler(bulkScanConfig);

        when(bulkScanConfig.getName()).thenReturn(DEFAULT_DECEASED_SURNAME_VALUE);
        when(bulkScanConfig.getPostcode()).thenReturn(DEFAULT_POSTCODE_VALUE);
        when(bulkScanConfig.getDob()).thenReturn(DEFAULT_DOB_VALUE);
        when(bulkScanConfig.getEmail()).thenReturn(DEFAULT_EMAIL_VALUE);
        when(bulkScanConfig.getPhone()).thenReturn(DEFAULT_PHONE_VALUE);
        when(bulkScanConfig.getDeceasedAnyOtherNames()).thenReturn(DEFAULT_DECEASED_ANY_OTHER_NAMES_VALUE);
        when(bulkScanConfig.getDeceasedDomicileInEngWales()).thenReturn(DEFAULT_DECEASED_DOMICILE_IN_ENG_WALES_VALUE);
        when(bulkScanConfig.getSolicitorNotApplying()).thenReturn(FALSE);

        Field bulkScanConfigField = SolicitorFieldHandler.class.getDeclaredField("bulkScanConfig");
        bulkScanConfigField.setAccessible(true);
        bulkScanConfigField.set(solicitorFieldHandler, bulkScanConfig);

        ocrFields = ExceptionRecordOCRFields.builder()
                .primaryApplicantForenames(VALID_PRIMARY_APPLICANT_FORENAMES)
                .primaryApplicantSurname(VALID_PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantAddressLine1(VALID_PRIMARY_APPLICANT_ADDRESS_LINE_1)
                .primaryApplicantAddressPostCode(VALID_PRIMARY_APPLICANT_ADDRESS_POSTCODE)
                .primaryApplicantAlias(VALID_PRIMARY_APPLICANT_ALIAS)
                .primaryApplicantHasAlias(VALID_PRIMARY_APPLICANT_HAS_ALIAS)
                .solsSolicitorIsApplying(VALID_SOLICITOR_IS_APPLYING)
                .solsSolicitorFirmName(VALID_SOLICITOR_FIRM_NAME)
                .solsSolicitorRepresentativeName(VALID_SOLICITOR_REPRESENTATIVE_NAME)
                .solsSolicitorAppReference(VALID_SOLICITOR_APP_REFERENCE)
                .solsSolicitorAddressLine1(VALID_SOLICITOR_ADDRESS_LINE_1)
                .solsSolicitorAddressLine2(VALID_SOLICITOR_ADDRESS_LINE_2)
                .solsSolicitorAddressTown(VALID_SOLICITOR_ADDRESS_TOWN)
                .solsSolicitorAddressPostCode(VALID_SOLICITOR_ADDRESS_POSTCODE)
                .solsSolicitorEmail(VALID_SOLICITOR_EMAIL)
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
                .applyingAsAnAttorney(FALSE)
                .build();
    }

    @Test
    void shouldSetSolsSolicitorRepresentativeNameToFirmNameWhenEmpty() {
        ocrFields.setSolsSolicitorRepresentativeName("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        solicitorFieldHandler.handleGorSolicitorFields(ocrFields, modifiedFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(SOLICITOR_REPRESENTATIVE_NAME, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(VALID_SOLICITOR_FIRM_NAME, ocrFields.getSolsSolicitorRepresentativeName());
    }

    @Test
    void shouldSetSolsSolicitorAppReferenceToDeceasedSurnameWhenEmpty() {
        ocrFields.setSolsSolicitorAppReference("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        solicitorFieldHandler.handleGorSolicitorFields(ocrFields, modifiedFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(SOLICITOR_APP_REFERENCE, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(VALID_DECEASED_SURNAME, ocrFields.getSolsSolicitorAppReference());
    }

    @Test
    void should_AutoFill_SolsSolicitorAddressLine1_With_Missing_When_Empty() {
        ocrFields.setSolsSolicitorAddressLine1("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        solicitorFieldHandler.handleGorSolicitorFields(ocrFields, modifiedFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(SOLICITOR_ADDRESS_LINE1, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(bulkScanConfig.getName(), ocrFields.getSolsSolicitorAddressLine1());
    }

    @Test
    void should_Not_AutoFill_SolsSolicitorAddressLine2_When_Empty_And_Address_Street_And_Postcode_Exist() {
        ocrFields.setSolsSolicitorAddressLine2("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        solicitorFieldHandler.handleGorSolicitorFields(ocrFields, modifiedFields);

        assertEquals(0, modifiedFields.size());
        assertEquals("", ocrFields.getSolsSolicitorAddressLine2());
    }

    @Test
    void should_Not_AutoFill_SolsSolicitorAddressTown_When_Empty_And_Address_Street_And_Postcode_Exist() {
        ocrFields.setSolsSolicitorAddressTown("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        solicitorFieldHandler.handleGorSolicitorFields(ocrFields, modifiedFields);

        assertEquals(0, modifiedFields.size());
        assertEquals("", ocrFields.getSolsSolicitorAddressTown());
    }

    @Test
    void shouldSetPhoneTo1234WhenEmpty() {
        ocrFields.setSolsSolicitorPhoneNumber("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        solicitorFieldHandler.handleGorSolicitorFields(ocrFields, modifiedFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(SOLICITOR_PHONE_NUMBER, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(DEFAULT_PHONE_VALUE, ocrFields.getSolsSolicitorPhoneNumber());
    }

    @Test
    void shouldSetPostcodeWhenEmpty() {
        ocrFields.setSolsSolicitorAddressPostCode("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        solicitorFieldHandler.handleGorSolicitorFields(ocrFields, modifiedFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(SOLICITOR_ADDRESS_POST_CODE, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(DEFAULT_POSTCODE_VALUE, ocrFields.getSolsSolicitorAddressPostCode());
    }

    @Test
    void shouldSetApplyingAsAnAttorneyToDefaultWhenEmpty() {
        ocrFields.setApplyingAsAnAttorney("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        solicitorFieldHandler.handleGorSolicitorFields(ocrFields, modifiedFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(APPLYING_ATTORNEY, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(bulkScanConfig.getFieldsNotCompleted(), ocrFields.getApplyingAsAnAttorney());
    }

    @Test
    void shouldNotModifyApplyingAsAnAttorneyWhenValueIsPresent() {
        ocrFields.setApplyingAsAnAttorney(TRUE);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        solicitorFieldHandler.handleGorSolicitorFields(ocrFields, modifiedFields);

        assertEquals(0, modifiedFields.size());
        assertEquals(TRUE, ocrFields.getApplyingAsAnAttorney());
    }
}
