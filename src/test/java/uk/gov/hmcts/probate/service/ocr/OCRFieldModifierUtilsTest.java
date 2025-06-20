package uk.gov.hmcts.probate.service.ocr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.config.BulkScanConfig;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ModifiedOCRField;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;
import static uk.gov.hmcts.probate.model.Constants.FALSE;
import static uk.gov.hmcts.probate.model.Constants.TRUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.BILINGUAL_GRANT;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_IS_APPLYING;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SPOUSE_OR_PARTNER;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_IHT_FORM;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_DECEASED_SURNAME_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_POSTCODE_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_DOB_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_EMAIL_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_PHONE_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_DATE_OF_BIRTH;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_DECEASED_ANY_OTHER_NAMES_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_DECEASED_DOMICILE_IN_ENG_WALES_VALUE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_ALIAS_VALUE;
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
import static uk.gov.hmcts.probate.model.DummyValuesConstants.WILL_DATE;

class OCRFieldModifierUtilsTest {
    @InjectMocks
    private OCRFieldModifierUtils ocrFieldModifierUtils;
    @Mock
    private BulkScanConfig bulkScanConfig;

    @Mock
    private PrimaryApplicantFieldsHandler primaryApplicantFieldsHandler;
    @Mock
    private DeceasedFieldsHandler deceasedFieldsHandler;
    @Mock
    private ExecutorsApplyingHandler executorsApplyingHandler;
    @Mock
    private ExecutorsNotApplyingHandler executorsNotApplyingHandler;
    @Mock
    private IHTFieldHandler ihtFieldHandler;
    @Mock
    private  SolicitorFieldHandler solicitorFieldHandler;

    private ExceptionRecordOCRFields ocrFields;


    @BeforeEach
     void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        ocrFieldModifierUtils = new OCRFieldModifierUtils(bulkScanConfig,
                primaryApplicantFieldsHandler, deceasedFieldsHandler,
                executorsApplyingHandler, executorsNotApplyingHandler,
                ihtFieldHandler, solicitorFieldHandler);

        when(bulkScanConfig.getIhtForm()).thenReturn(DEFAULT_IHT_FORM);
        when(bulkScanConfig.getGrossNetValue()).thenReturn(DEFAULT_VALUE);
        when(bulkScanConfig.getName()).thenReturn(DEFAULT_DECEASED_SURNAME_VALUE);
        when(bulkScanConfig.getPostcode()).thenReturn(DEFAULT_POSTCODE_VALUE);
        when(bulkScanConfig.getDob()).thenReturn(DEFAULT_DOB_VALUE);
        when(bulkScanConfig.getEmail()).thenReturn(DEFAULT_EMAIL_VALUE);
        when(bulkScanConfig.getPhone()).thenReturn(DEFAULT_PHONE_VALUE);
        when(bulkScanConfig.getDob()).thenReturn(DEFAULT_DATE_OF_BIRTH);
        when(bulkScanConfig.getDeceasedAnyOtherNames()).thenReturn(DEFAULT_DECEASED_ANY_OTHER_NAMES_VALUE);
        when(bulkScanConfig.getDeceasedDomicileInEngWales()).thenReturn(DEFAULT_DECEASED_DOMICILE_IN_ENG_WALES_VALUE);
        when(bulkScanConfig.getPrimaryApplicantHasAlias()).thenReturn(DEFAULT_ALIAS_VALUE);
        when(bulkScanConfig.getDeceasedDiedOnOrAfterSwitchDateTrue()).thenReturn(TRUE);
        when(bulkScanConfig.getDeceasedDiedOnOrAfterSwitchDateFalse()).thenReturn(FALSE);
        when(bulkScanConfig.getDateOfDeathForDiedOnOrAfterSwitchDateTrue()).thenReturn("01012022");
        when(bulkScanConfig.getDateOfDeathForDiedOnOrAfterSwitchDateFalse()).thenReturn("01011990");
        when(bulkScanConfig.getExecutorsNotApplyingReason()).thenReturn("A");
        when(bulkScanConfig.getSolicitorApplying()).thenReturn(FALSE);

        Field bulkScanConfigField = OCRFieldModifierUtils.class.getDeclaredField("bulkScanConfig");
        bulkScanConfigField.setAccessible(true);
        bulkScanConfigField.set(ocrFieldModifierUtils, bulkScanConfig);

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
                .spouseOrPartner(TRUE)
                .notifiedApplicants(TRUE)
                .bilingualGrantRequested(TRUE)
                .willDate(VALID_DECEASED_DATE_OF_BIRTH)
                .build();
    }

    @Test
    void shouldReturnWarningWhenMoreThanOneIHTFormIsTrue() {
        ExceptionRecordOCRFields ocrWarningFields1 = ExceptionRecordOCRFields.builder()
                .iht400Completed(TRUE)
                .iht400421Completed(TRUE)
                .iht207Completed(FALSE)
                .iht205Completed(FALSE)
                .exceptedEstate(TRUE)
                .build();

        List<CollectionMember<String>> warnings = ocrFieldModifierUtils.checkWarnings(ocrWarningFields1);

        assertEquals(1, warnings.size());
        assertEquals("More than one IHT form is marked as TRUE. Only one form should be selected as TRUE.",
                warnings.getFirst().getValue());
    }

    @Test
    void shouldReturnWarningWhenOneIHTFormAndExceptedEstateIsTrue() {
        ExceptionRecordOCRFields ocrWarningFields2 = ExceptionRecordOCRFields.builder()
                .iht400Completed(TRUE)
                .iht400421Completed(FALSE)
                .iht207Completed(FALSE)
                .iht205Completed(FALSE)
                .formVersion("3")
                .exceptedEstate(TRUE)
                .build();

        List<CollectionMember<String>> warnings = ocrFieldModifierUtils.checkWarnings(ocrWarningFields2);

        assertEquals(1, warnings.size());
        assertEquals("More than one IHT form is marked as TRUE. Only one form should be selected as TRUE.",
                warnings.getFirst().getValue());
    }

    @Test
    void shouldReturnWarningWhenOneIHTFormAndDeceasedDiedOnAfterSwitchDateIsTrue() {
        ExceptionRecordOCRFields ocrWarningFields3 = ExceptionRecordOCRFields.builder()
                .iht400Completed(TRUE)
                .iht400421Completed(FALSE)
                .iht207Completed(FALSE)
                .iht205Completed(FALSE)
                .formVersion("2")
                .deceasedDiedOnAfterSwitchDate(TRUE)
                .build();

        List<CollectionMember<String>> warnings = ocrFieldModifierUtils.checkWarnings(ocrWarningFields3);

        assertEquals(1, warnings.size());
        assertEquals("More than one IHT form is marked as TRUE. Only one form should be selected as TRUE.",
                warnings.getFirst().getValue());
    }

    @Test
    void shouldReturnNoWarningWhenOnlyOneIHTFormIsTrue() {
        ExceptionRecordOCRFields ocrWarningFields4 = ExceptionRecordOCRFields.builder()
                .exceptedEstate(TRUE)
                .iht400Completed(FALSE)
                .iht400process(FALSE)
                .iht400421Completed(FALSE)
                .iht207Completed(FALSE)
                .iht205Completed(FALSE)
                .build();

        List<CollectionMember<String>> warnings = ocrFieldModifierUtils.checkWarnings(ocrWarningFields4);

        assertEquals(0, warnings.size());
    }

    @Test
    void shouldSetSolicitorIsApplyingToDefaultWhenEmpty() {
        ocrFields.setSolsSolicitorIsApplying("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields,
                GrantType.GRANT_OF_PROBATE);

        assertEquals(1, modifiedFields.size());
        assertEquals(SOLICITOR_IS_APPLYING, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(bulkScanConfig.getSolicitorApplying(), ocrFields.getSolsSolicitorIsApplying());
    }

    @Test
    void shouldSetSolicitorIsApplyingToDefaultWhenValueIsPresent() {
        ocrFields.setSolsSolicitorIsApplying(TRUE);
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields,
                GrantType.GRANT_OF_PROBATE);

        assertEquals(0, modifiedFields.size());
    }

    @Test
    void shouldSetSpouseOrPartnerToDefaultWhenEmpty() {
        ocrFields.setSpouseOrPartner("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields,
                GrantType.GRANT_OF_PROBATE);

        assertEquals(1, modifiedFields.size());
        assertEquals(SPOUSE_OR_PARTNER, modifiedFields.get(0).getValue().getFieldName());
        assertEquals(bulkScanConfig.getFieldsNotCompleted(), ocrFields.getSpouseOrPartner());
    }

    @Test
    void shouldNotModifySpouseOrPartnerWhenValueIsPresent() {
        ocrFields.setSpouseOrPartner(TRUE);
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields,
                GrantType.GRANT_OF_PROBATE);

        assertEquals(0, modifiedFields.size());
        assertEquals(TRUE, ocrFields.getSpouseOrPartner());
    }

    @Test
    void shouldCallPrimaryApplicantFieldsHandler() {
        ocrFieldModifierUtils.setDefaultGorValues(ocrFields, GrantType.GRANT_OF_PROBATE);

        verify(primaryApplicantFieldsHandler).handleGorPrimaryApplicantFields(eq(ocrFields), anyList());
        verify(deceasedFieldsHandler).handleDeceasedFields(eq(ocrFields), anyList());
        verify(ihtFieldHandler).handleIHTFields(eq(ocrFields), anyList());
        verify(executorsApplyingHandler).handleExecutorsApplyingFields(eq(ocrFields), anyList());
        verify(executorsNotApplyingHandler).handleExecutorsNotApplyingFields(eq(ocrFields), anyList());
    }

    @Test
    void shouldHandleSolicitorFieldsWhenSolicitorIsApplyingIsTrue() {
        ocrFields.setSolsSolicitorIsApplying(TRUE);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields,
                GrantType.GRANT_OF_PROBATE);

        verify(solicitorFieldHandler).handleGorSolicitorFields(ocrFields, modifiedFields);
        assertEquals(0, modifiedFields.size());
    }

    @Test
    void shouldNotHandleSolicitorFieldsWhenSolicitorIsApplyingIsFalse() {
        ocrFields.setSolsSolicitorIsApplying(FALSE);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields,
                GrantType.GRANT_OF_PROBATE);

        verify(solicitorFieldHandler, never()).handleGorSolicitorFields(any(), any());
        assertEquals(0, modifiedFields.size());
    }

    @Test
    void shouldSetBilingualGrantRequestedToDefaultWhenEmpty() {
        ocrFields.setBilingualGrantRequested("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields,
                GrantType.GRANT_OF_PROBATE);

        assertEquals(1, modifiedFields.size());
        assertEquals(BILINGUAL_GRANT, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(bulkScanConfig.getFieldsNotCompleted(), ocrFields.getBilingualGrantRequested());
    }

    @Test
    void shouldNotModifyBilingualGrantRequestedWhenValueIsPresent() {
        ocrFields.setBilingualGrantRequested(TRUE);
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields,
                GrantType.GRANT_OF_PROBATE);

        assertEquals(0, modifiedFields.size());
        assertEquals(TRUE, ocrFields.getBilingualGrantRequested());
    }

    @Test
    void shouldSetWillDateToDefaultWhenEmptyAndGrantTypeIsGrantOfProbate() {
        ocrFields.setWillDate("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields,
                GrantType.GRANT_OF_PROBATE);

        assertEquals(1, modifiedFields.size());
        assertEquals(WILL_DATE, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(bulkScanConfig.getDob(), ocrFields.getWillDate());
    }

    @Test
    void shouldNotModifyWillDateWhenValueIsPresentAndGrantTypeIsGrantOfProbate() {
        ocrFields.setWillDate("01012020");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields,
                GrantType.GRANT_OF_PROBATE);

        assertEquals(0, modifiedFields.size());
        assertEquals("01012020", ocrFields.getWillDate());
    }

    @Test
    void shouldNotSetWillDateWhenGrantTypeIsNotGrantOfProbate() {
        ocrFields.setWillDate("");
        List<CollectionMember<ModifiedOCRField>> modifiedFields = ocrFieldModifierUtils.setDefaultGorValues(ocrFields,
                GrantType.INTESTACY);

        assertEquals(0, modifiedFields.size());
        assertEquals("", ocrFields.getWillDate());
    }
}
