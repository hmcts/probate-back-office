package uk.gov.hmcts.probate.service.ocr;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.config.BulkScanConfig;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ModifiedOCRField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.FALSE;
import static uk.gov.hmcts.probate.model.Constants.TRUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_GROSS_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_NET_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_FORM_COMPLETED_ONLINE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_REFERENCE;
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
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_CAVEATOR_FORENAMES;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_CAVEATOR_SURNAME;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_CAVEATOR_ADDRESS_LINE_1;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.VALID_CAVEATOR_ADDRESS_POSTCODE;
import static uk.gov.hmcts.probate.service.ocr.OcrConstants.DEFAULT_FORM;

public class IHTFieldHandlerTest {
    @InjectMocks
    private IHTFieldHandler ihtFieldHandler;
    @Mock
    private BulkScanConfig bulkScanConfig;

    @Mock
    private ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;

    private ExceptionRecordOCRFields ocrFields;
    private Field formVersionField;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        ihtFieldHandler = new IHTFieldHandler(bulkScanConfig, exceptedEstateDateOfDeathChecker);

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
        when(bulkScanConfig.getDefaultForm()).thenReturn(DEFAULT_FORM);

        Field bulkScanConfigField = IHTFieldHandler.class.getDeclaredField("bulkScanConfig");
        bulkScanConfigField.setAccessible(true);
        bulkScanConfigField.set(ihtFieldHandler, bulkScanConfig);

        formVersionField = ExceptionRecordOCRFields.class.getDeclaredField("formVersion");
        formVersionField.setAccessible(true);



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
                .caveatorForenames(VALID_CAVEATOR_FORENAMES)
                .caveatorSurnames(VALID_CAVEATOR_SURNAME)
                .caveatorAddressLine1(VALID_CAVEATOR_ADDRESS_LINE_1)
                .caveatorAddressPostCode(VALID_CAVEATOR_ADDRESS_POSTCODE)
                .deceasedDateOfDeath("01012022")
                .deceasedDiedOnAfterSwitchDate(TRUE)
                .spouseOrPartner(TRUE)
                .notifiedApplicants(TRUE)
                .build();
    }

    @Test
    void shouldSetDefaultIHTFormWhenDeceasedDateOfDeathIsAfterAndFormVersionIsThree() throws IllegalAccessException {
        formVersionField.set(ocrFields, "3");

        ocrFields.setDeceasedDateOfDeath("01012022");
        ocrFields.setDeceasedDiedOnAfterSwitchDate(TRUE);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        ihtFieldHandler.handleIHTFields(ocrFields, modifiedFields);

        Assertions.assertEquals(TRUE, ocrFields.getIht400Completed());
        Assertions.assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht400421Completed());
        Assertions.assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht207Completed());
        Assertions.assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205Completed());
        Assertions.assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205completedOnline());
        Assertions.assertEquals("3", ocrFields.getFormVersion());
    }

    @Test
    void shouldNotSetDefaultIHTFormWhenExceptedEstateAndFormVersionIsTwo() throws IllegalAccessException {
        formVersionField.set(ocrFields, "2");

        ocrFields.setDeceasedDateOfDeath("01012022");
        ocrFields.setDeceasedDiedOnAfterSwitchDate("TRUE");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        ihtFieldHandler.handleIHTFields(ocrFields, modifiedFields);

        Assertions.assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateNetValue());
        Assertions.assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateGrossValue());
        Assertions.assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateNetQualifyingValue());
        Assertions.assertEquals("2", ocrFields.getFormVersion());
    }

    @Test
    void shouldSetDefaultIHTFormWhenDeceasedDateOfDeathIsBeforeAndFormVersionIsThree() throws IllegalAccessException {
        formVersionField.set(ocrFields, "3");

        ocrFields.setDeceasedDateOfDeath("01012020");
        ocrFields.setDeceasedDiedOnAfterSwitchDate(FALSE);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        ihtFieldHandler.handleIHTFields(ocrFields, modifiedFields);

        Assertions.assertEquals(TRUE, ocrFields.getIht400Completed());
        Assertions.assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht400421Completed());
        Assertions.assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht207Completed());
        Assertions.assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205Completed());
        Assertions.assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205completedOnline());
        Assertions.assertEquals("3", ocrFields.getFormVersion());
    }

    @Test
    void shouldSetDefaultIHTFormWhenDeceasedDateOfDeathIsBeforeAndFormVersionIsTwo() throws IllegalAccessException {
        formVersionField.set(ocrFields, "2");

        ocrFields.setDeceasedDateOfDeath("01012020");
        ocrFields.setDeceasedDiedOnAfterSwitchDate(FALSE);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        ihtFieldHandler.handleIHTFields(ocrFields, modifiedFields);

        Assertions.assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht400421Completed());
        Assertions.assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht207Completed());
        Assertions.assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205Completed());
        Assertions.assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205completedOnline());
        Assertions.assertEquals("2", ocrFields.getFormVersion());
    }

    @Test
    void shouldSetDefaultGrossNetValueWhenDeceasedDateOfDeathIsAfterAndFormVersionIsThreeAndIhtIs400421()
            throws IllegalAccessException {
        formVersionField.set(ocrFields, "3");

        ocrFields.setDeceasedDateOfDeath("01012022");
        ocrFields.setDeceasedDiedOnAfterSwitchDate(TRUE);
        ocrFields.setIht400421Completed(TRUE);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        ihtFieldHandler.handleIHTFields(ocrFields, modifiedFields);

        Assertions.assertEquals(DEFAULT_VALUE, ocrFields.getIht421grossValue());
        Assertions.assertEquals(DEFAULT_VALUE, ocrFields.getIht421netValue());
        Assertions.assertEquals("3", ocrFields.getFormVersion());
    }

    @Test
    void shouldSetDefaultGrossNetValueWhenDeceasedDateOfDeathIsAfterAndFormVersionIsThreeAndNoIht()
            throws IllegalAccessException {
        formVersionField.set(ocrFields, "3");

        ocrFields.setDeceasedDateOfDeath("01012022");
        ocrFields.setDeceasedDiedOnAfterSwitchDate(TRUE);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        ihtFieldHandler.handleIHTFields(ocrFields, modifiedFields);


        Assertions.assertEquals(TRUE, ocrFields.getDeceasedDiedOnAfterSwitchDate());
        Assertions.assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht400421Completed());
        Assertions.assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht207Completed());
        Assertions.assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205Completed());
        Assertions.assertEquals(TRUE, ocrFields.getIht400Completed());
        Assertions.assertEquals(DEFAULT_IHT_FORM, ocrFields.getIht205completedOnline());
        Assertions.assertEquals(DEFAULT_VALUE, ocrFields.getProbateGrossValueIht400());
        Assertions.assertEquals(DEFAULT_VALUE, ocrFields.getProbateNetValueIht400());
        Assertions.assertEquals("3", ocrFields.getFormVersion());
    }

    @Test
    void shouldSetDefaultGrossNetValueWhenDeceasedDateOfDeathIsAfterAndFormVersionIsTwoAndIhtIs400421()
            throws IllegalAccessException {
        formVersionField.set(ocrFields, "2");


        ocrFields.setDeceasedDateOfDeath("01012022");
        ocrFields.setDeceasedDiedOnAfterSwitchDate(TRUE);
        ocrFields.setIht400421Completed(TRUE);


        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        ihtFieldHandler.handleIHTFields(ocrFields, modifiedFields);

        Assertions.assertEquals(TRUE, ocrFields.getDeceasedDiedOnAfterSwitchDate());
        Assertions.assertEquals(DEFAULT_VALUE, ocrFields.getIhtGrossValue());
        Assertions.assertEquals(DEFAULT_VALUE, ocrFields.getIhtNetValue());
        Assertions.assertEquals("2", ocrFields.getFormVersion());
    }

    @Test
    void shouldSetDefaultGrossNetValueWhenDeceasedDateOfDeathIsAfterAndFormVersionIsThreeAndIhtIs207()
            throws IllegalAccessException {
        formVersionField.set(ocrFields, "3");

        ocrFields.setDeceasedDateOfDeath("01012022");
        ocrFields.setDeceasedDiedOnAfterSwitchDate(TRUE);
        ocrFields.setIht207Completed(TRUE);


        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        ihtFieldHandler.handleIHTFields(ocrFields, modifiedFields);

        Assertions.assertEquals(TRUE, ocrFields.getDeceasedDiedOnAfterSwitchDate());
        Assertions.assertEquals(DEFAULT_VALUE, ocrFields.getIht207grossValue());
        Assertions.assertEquals(DEFAULT_VALUE, ocrFields.getIht207netValue());
        Assertions.assertEquals("3", ocrFields.getFormVersion());
    }

    @Test
    void shouldSetDefaultGrossNetValueWhenDeceasedDateOfDeathIsAfterAndFormVersionIsTwoAndIhtIs207()
            throws IllegalAccessException {
        formVersionField.set(ocrFields, "2");

        ocrFields.setDeceasedDateOfDeath("01012022");
        ocrFields.setDeceasedDiedOnAfterSwitchDate(TRUE);
        ocrFields.setIht207Completed(TRUE);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        ihtFieldHandler.handleIHTFields(ocrFields, modifiedFields);

        Assertions.assertEquals(DEFAULT_VALUE, ocrFields.getIhtGrossValue());
        Assertions.assertEquals(DEFAULT_VALUE, ocrFields.getIhtNetValue());
        Assertions.assertEquals("2", ocrFields.getFormVersion());
    }

    @Test
    void shouldSetDefaultGrossNetValueWhenDeceasedDateOfDeathIsBeforeAndFormVersionIsThreeAndIhtIs205()
            throws IllegalAccessException {
        formVersionField.set(ocrFields, "3");

        ocrFields.setDeceasedDateOfDeath("01012020");
        ocrFields.setDeceasedDiedOnAfterSwitchDate(TRUE);
        ocrFields.setIht205Completed(TRUE);

        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate("01012020")).thenReturn(false);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        ihtFieldHandler.handleIHTFields(ocrFields, modifiedFields);

        Assertions.assertEquals(DEFAULT_VALUE, ocrFields.getIhtGrossValue205());
        Assertions.assertEquals(DEFAULT_VALUE, ocrFields.getIhtNetValue205());
        Assertions.assertEquals("3", ocrFields.getFormVersion());
    }

    @Test
    void shouldSetDefaultGrossNetValueWhenDeceasedDateOfDeathIsBeforeAndFormVersionIsTwoAndIhtIs205Online()
            throws IllegalAccessException {
        formVersionField.set(ocrFields, "3");

        ocrFields.setDeceasedDateOfDeath("01012020");
        ocrFields.setDeceasedDiedOnAfterSwitchDate(FALSE);
        ocrFields.setIht205completedOnline(TRUE);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        ihtFieldHandler.handleIHTFields(ocrFields, modifiedFields);

        Assertions.assertEquals(DEFAULT_VALUE, ocrFields.getIhtGrossValue205());
        Assertions.assertEquals(DEFAULT_VALUE, ocrFields.getIhtNetValue205());
        Assertions.assertEquals("1234", ocrFields.getIhtReferenceNumber());
        Assertions.assertEquals("3", ocrFields.getFormVersion());
    }

    @Test
    void shouldSetDefaultEstateGrossNetValueWhenDeceasedDateOfDeathIsBeforeAndFormVersionIsThree()
            throws IllegalAccessException, NoSuchFieldException {
        formVersionField.set(ocrFields, "3");

        Field exceptedEstateField = ExceptionRecordOCRFields.class.getDeclaredField("exceptedEstate");
        exceptedEstateField.setAccessible(true);
        exceptedEstateField.set(ocrFields, TRUE);

        ocrFields.setDeceasedDateOfDeath("01012020");
        ocrFields.setDeceasedDiedOnAfterSwitchDate(FALSE);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        ihtFieldHandler.handleIHTFields(ocrFields, modifiedFields);

        Assertions.assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateGrossValue());
        Assertions.assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateNetValue());
        Assertions.assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateNetQualifyingValue());
        Assertions.assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateGrossValue());

        Assertions.assertEquals("3", ocrFields.getFormVersion());
    }

    @Test
    void shouldSetDefaultEstateGrossNetValueWhenDeceasedDateOfDeathIsAfterAndFormVersionIsTwo()
            throws IllegalAccessException {
        formVersionField.set(ocrFields, "2");
        ocrFields.setDeceasedDateOfDeath("01012022");
        ocrFields.setDeceasedDiedOnAfterSwitchDate(TRUE);

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        ihtFieldHandler.handleIHTFields(ocrFields, modifiedFields);

        Assertions.assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateGrossValue());
        Assertions.assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateNetValue());
        Assertions.assertEquals(DEFAULT_VALUE, ocrFields.getIhtEstateNetQualifyingValue());
        Assertions.assertEquals(DEFAULT_VALUE, ocrFields.getIhtGrossValue());
        Assertions.assertEquals(DEFAULT_VALUE, ocrFields.getIhtNetValue());
        Assertions.assertEquals("2", ocrFields.getFormVersion());
    }

    @Test
    void shouldSetIhtFormCompletedOnlineToDefaultWhenBlank() throws IllegalAccessException {
        formVersionField.set(ocrFields, "1");

        ocrFields.setIhtFormCompletedOnline("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        ihtFieldHandler.handleIHTFields(ocrFields, modifiedFields);

        assertEquals(4, modifiedFields.size());
        assertEquals(IHT_FORM_COMPLETED_ONLINE, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(bulkScanConfig.getFieldsNotCompleted(), ocrFields.getIhtFormCompletedOnline());
        assertEquals(bulkScanConfig.getDefaultForm(), ocrFields.getIhtFormId());
    }

    @Test
    void shouldSetIhtReferenceNumberWhenIhtFormCompletedOnlineIsTrueAndBlank() throws IllegalAccessException {
        formVersionField.set(ocrFields, "1");
        ocrFields.setIhtFormCompletedOnline(TRUE);
        ocrFields.setIhtReferenceNumber("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        ihtFieldHandler.handleIHTFields(ocrFields, modifiedFields);

        assertEquals(1, modifiedFields.size());
        assertEquals(IHT_REFERENCE, modifiedFields.getFirst().getValue().getFieldName());
        assertEquals(bulkScanConfig.getName(), ocrFields.getIhtReferenceNumber());
    }

    @Test
    void shouldSetGrossAndNetValuesWhenIhtFormIdIsValidAndIhtFormCompletedOnlineIsFalse()
            throws IllegalAccessException {
        formVersionField.set(ocrFields, "1");
        ocrFields.setIhtFormCompletedOnline(FALSE);
        ocrFields.setIhtFormId(DEFAULT_FORM);
        ocrFields.setIhtGrossValue("");
        ocrFields.setIhtNetValue("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        ihtFieldHandler.handleIHTFields(ocrFields, modifiedFields);

        assertEquals(2, modifiedFields.size());
        assertEquals(IHT_GROSS_VALUE, modifiedFields.get(0).getValue().getFieldName());
        assertEquals(bulkScanConfig.getGrossNetValue(), ocrFields.getIhtGrossValue());
        assertEquals(IHT_NET_VALUE, modifiedFields.get(1).getValue().getFieldName());
        assertEquals(bulkScanConfig.getGrossNetValue(), ocrFields.getIhtNetValue());
    }

    @Test
    void shouldNotModifyFieldsWhenIhtFormCompletedOnlineIsTrueAndValuesArePresent() throws IllegalAccessException {
        formVersionField.set(ocrFields, "1");
        ocrFields.setIhtFormCompletedOnline(TRUE);
        ocrFields.setIhtReferenceNumber("12345");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        ihtFieldHandler.handleIHTFields(ocrFields, modifiedFields);

        assertEquals(0, modifiedFields.size());
    }

    @Test
    void shouldModifyFieldsWhenIhtFormIdIsValidAndValuesAreNotPresent() throws IllegalAccessException {
        formVersionField.set(ocrFields, "1");
        ocrFields.setIhtFormCompletedOnline(FALSE);
        ocrFields.setIhtFormId(DEFAULT_FORM);
        ocrFields.setIhtGrossValue("");
        ocrFields.setIhtNetValue("");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        ihtFieldHandler.handleIHTFields(ocrFields, modifiedFields);

        assertEquals(2, modifiedFields.size());
        assertEquals("1.11", ocrFields.getIhtGrossValue());
        assertEquals("1.11", ocrFields.getIhtNetValue());
    }

    @Test
    void shouldNotModifyFieldsWhenIhtFormIdIsValidAndValuesArePresent() throws IllegalAccessException {
        formVersionField.set(ocrFields, "1");
        ocrFields.setIhtFormCompletedOnline(FALSE);
        ocrFields.setIhtFormId(DEFAULT_FORM);
        ocrFields.setIhtGrossValue("1.11");
        ocrFields.setIhtNetValue("1.11");

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
        ihtFieldHandler.handleIHTFields(ocrFields, modifiedFields);

        assertEquals(0, modifiedFields.size());
        assertEquals("1.11", ocrFields.getIhtGrossValue());
        assertEquals("1.11", ocrFields.getIhtNetValue());
    }
}
