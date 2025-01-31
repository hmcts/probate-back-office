package uk.gov.hmcts.probate.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorPartners;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorTrustCorps;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.CHANNEL_CHOICE_DIGITAL;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_PROBATE;
import static uk.gov.hmcts.probate.model.DocumentType.UPLOADED_LEGAL_STATEMENT;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT207_VALUE;

class CCDDataTransformerTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String[] LAST_MODIFIED_STR = {"2018", "1", "2", "0", "0", "0", "0"};
    private static final String SOLICITOR_FIRM_NAME = "Sol Firm Name";
    private static final String SOLICITOR_FIRM_LINE1 = "Sol Add Line1";
    private static final String SOLICITOR_FIRM_POSTCODE = "SW13 6EA";
    private static final String SOLICITOR_SOT_NAME = "Andy Test";
    private static final String SOLICITOR_SOT_JOB_TITLE = "Lawyer";

    private static final String DECEASED_FIRSTNAME = "Firstname";
    private static final String DECEASED_LASTNAME = "Lastname";
    private static final LocalDate DOB = LocalDate.parse("2016-12-31", dateTimeFormatter);
    private static final LocalDate DOD = LocalDate.parse("2017-12-31", dateTimeFormatter);

    private static final String EXEC_FIRSTNAME = "Forename";

    private static final String IHT_FORM_ID = "IHT207";
    private static final BigDecimal IHT_GROSS = BigDecimal.valueOf(10000f);
    private static final BigDecimal IHT_NET = BigDecimal.valueOf(9000f);
    private static final BigDecimal TOTAL_FEE = new BigDecimal(155.00);
    private static final BigDecimal APPLICATION_FEE = new BigDecimal(200.00);
    private static final BigDecimal FEE_UK_COPIES = new BigDecimal(0.50);
    private static final BigDecimal FEE_NON_UK_COPIES = new BigDecimal(1.50);
    private static final String PAYMENT_METHOD_CHEQUE = "cheque";
    private static final String PAYMENT_METHOD_FEE = "fee account";
    private static final String SELECTED_PBA_NUMBER = "PBA1234";
    private static final String PBA_PAYMENT_REFERENCE = "PBA1234-PayRef";
    private static final BigDecimal IHT_ESTATE_GROSS = BigDecimal.valueOf(20000f);
    private static final BigDecimal IHT_ESTATE_NET = BigDecimal.valueOf(18000f);

    @Mock
    private CallbackRequest callbackRequestMock;

    @Mock
    private CaveatCallbackRequest caveatCallbackRequestMock;

    @Mock
    private CaveatDetails caveatDetailsMock;

    @Mock
    private CaseDetails caseDetailsMock;

    @Mock
    private CaseData caseDataMock;

    @Mock
    private CaveatData caveatDataMock;

    @InjectMocks
    private CCDDataTransformer underTest;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caveatCallbackRequestMock.getCaseDetails()).thenReturn(caveatDetailsMock);
        when(caveatDetailsMock.getData()).thenReturn(caveatDataMock);

        SolsAddress solsAddress = SolsAddress.builder()
                .addressLine1(SOLICITOR_FIRM_LINE1)
                .postCode(SOLICITOR_FIRM_POSTCODE)
                .build();

        when(caseDataMock.getSolsSolicitorFirmName()).thenReturn(SOLICITOR_FIRM_NAME);
        when(caseDataMock.getSolsSolicitorAddress()).thenReturn(solsAddress);
        when(caseDataMock.getSolsSOTName()).thenReturn(SOLICITOR_SOT_NAME);
        when(caseDataMock.getSolsSOTJobTitle()).thenReturn(SOLICITOR_SOT_JOB_TITLE);

        when(caseDataMock.getDeceasedForenames()).thenReturn(DECEASED_FIRSTNAME);
        when(caseDataMock.getDeceasedSurname()).thenReturn(DECEASED_LASTNAME);
        when(caseDataMock.getDeceasedDateOfBirth()).thenReturn(DOB);
        when(caseDataMock.getDeceasedDateOfDeath()).thenReturn(DOD);

        when(caseDataMock.getIhtFormId()).thenReturn(IHT_FORM_ID);
        when(caseDataMock.getIhtGrossValue()).thenReturn(IHT_GROSS);
        when(caseDataMock.getIhtNetValue()).thenReturn(IHT_NET);
        when(caseDataMock.getFeeForUkCopies()).thenReturn(FEE_UK_COPIES);
        when(caseDataMock.getFeeForNonUkCopies()).thenReturn(FEE_NON_UK_COPIES);
        when(caseDataMock.getTotalFee()).thenReturn(TOTAL_FEE);
        when(caseDataMock.getApplicationFee()).thenReturn(APPLICATION_FEE);
        when(caseDataMock.getApplicationType()).thenReturn(ApplicationType.SOLICITOR);
        when(caseDataMock.getSolsPBANumber()).thenReturn(DynamicList.builder()
            .value(DynamicListItem.builder().code(SELECTED_PBA_NUMBER).build()).build());
        when(caseDataMock.getSolsPBAPaymentReference()).thenReturn(PBA_PAYMENT_REFERENCE);

        when(caseDetailsMock.getLastModified()).thenReturn(LAST_MODIFIED_STR);

        List<CollectionMember<AdditionalExecutor>> additionalExecutors = new ArrayList<>();
        CollectionMember<AdditionalExecutor> additionalExecutors1 = mock(CollectionMember.class);
        CollectionMember<AdditionalExecutor> additionalExecutors2 = mock(CollectionMember.class);
        AdditionalExecutor additionalExecutor1 = mock(AdditionalExecutor.class);
        AdditionalExecutor additionalExecutor2 = mock(AdditionalExecutor.class);
        when(additionalExecutors1.getValue()).thenReturn(additionalExecutor1);
        when(additionalExecutors2.getValue()).thenReturn(additionalExecutor2);
        additionalExecutors.add(additionalExecutors1);
        additionalExecutors.add(additionalExecutors2);
        when(additionalExecutor1.getAdditionalApplying()).thenReturn(YES);
        when(additionalExecutor2.getAdditionalApplying()).thenReturn(NO);
        when(additionalExecutor2.getAdditionalExecReasonNotApplying()).thenReturn("Renunciation");
        when(caseDataMock.getSolsAdditionalExecutorList()).thenReturn(additionalExecutors);
        when(caseDataMock.getPrimaryApplicantForenames()).thenReturn(EXEC_FIRSTNAME);
        when(caseDataMock.isPrimaryApplicantApplying()).thenReturn(true);

        List<CollectionMember<AdditionalExecutorTrustCorps>> additionalExecutorsTrustCorp = new ArrayList<>();
        CollectionMember<AdditionalExecutorTrustCorps> additionalExecutorsTrustCorp1 = mock(CollectionMember.class);
        CollectionMember<AdditionalExecutorTrustCorps> additionalExecutorsTrustCorp2 = mock(CollectionMember.class);
        AdditionalExecutorTrustCorps additionalExecutorTrustCorp1 = mock(AdditionalExecutorTrustCorps.class);
        AdditionalExecutorTrustCorps additionalExecutorTrustCorp2 = mock(AdditionalExecutorTrustCorps.class);
        when(additionalExecutorsTrustCorp1.getValue()).thenReturn(additionalExecutorTrustCorp1);
        when(additionalExecutorsTrustCorp2.getValue()).thenReturn(additionalExecutorTrustCorp2);
        additionalExecutorsTrustCorp.add(additionalExecutorsTrustCorp1);
        additionalExecutorsTrustCorp.add(additionalExecutorsTrustCorp2);
        when(caseDataMock.getAdditionalExecutorsTrustCorpList()).thenReturn(additionalExecutorsTrustCorp);

        List<CollectionMember<AdditionalExecutorPartners>> additionalExecutorsPartner = new ArrayList<>();
        CollectionMember<AdditionalExecutorPartners> additionalExecutorsPartner1 = mock(CollectionMember.class);
        CollectionMember<AdditionalExecutorPartners> additionalExecutorsPartner2 = mock(CollectionMember.class);
        AdditionalExecutorPartners additionalExecutorPartner1 = mock(AdditionalExecutorPartners.class);
        AdditionalExecutorPartners additionalExecutorPartner2 = mock(AdditionalExecutorPartners.class);
        when(additionalExecutorsPartner1.getValue()).thenReturn(additionalExecutorPartner1);
        when(additionalExecutorsPartner2.getValue()).thenReturn(additionalExecutorPartner2);
        additionalExecutorsPartner.add(additionalExecutorsPartner1);
        additionalExecutorsPartner.add(additionalExecutorsPartner2);
        when(caseDataMock.getOtherPartnersApplyingAsExecutors()).thenReturn(additionalExecutorsPartner);

        List<CollectionMember<AdditionalExecutorNotApplying>> additionalExecutorsNotApplying =
            new ArrayList<>();
        AdditionalExecutorNotApplying addNot1 = AdditionalExecutorNotApplying.builder()
            .notApplyingExecutorReason("Renunciation")
            .build();
        AdditionalExecutorNotApplying addNot2 = AdditionalExecutorNotApplying.builder()
            .notApplyingExecutorReason("Renunciation")
            .build();
        additionalExecutorsNotApplying.add(new CollectionMember<>(addNot1));
        additionalExecutorsNotApplying.add(new CollectionMember<>(addNot2));
        when(caseDataMock.getAdditionalExecutorsNotApplying()).thenReturn(additionalExecutorsNotApplying);
        when(caseDataMock.getChannelChoice()).thenReturn(CHANNEL_CHOICE_DIGITAL);
    }

    @Test
    void shouldConvertRequestToDataBean() {

        CCDData ccdData = underTest.transform(callbackRequestMock);

        assertAll(ccdData);
        assertCaseSubmissionDate(ccdData);
    }

    @Test
    void shouldConvertRequestToDataBeanWithUploadedLegalStatement() {
        ArrayList<CollectionMember<UploadDocument>> uploaded = new ArrayList<>();
        uploaded.add(new CollectionMember<UploadDocument>(UploadDocument
                .builder().documentType(UPLOADED_LEGAL_STATEMENT).build()));
        uploaded.add(new CollectionMember<UploadDocument>(UploadDocument
                .builder().documentType(LEGAL_STATEMENT_PROBATE).build()));
        when(caseDataMock.getBoDocumentsUploaded()).thenReturn(uploaded);

        CCDData ccdData = underTest.transform(callbackRequestMock);

        assertAll(ccdData);
        assertCaseSubmissionDate(ccdData);
        assertTrue(ccdData.isHasUploadedLegalStatement());

    }

    @Test
    void shouldConvertRequestToDataBeanWithNoUploadedLegalStatements() {
        ArrayList<CollectionMember<UploadDocument>> uploaded = new ArrayList<>();
        uploaded.add(new CollectionMember<UploadDocument>(UploadDocument
                .builder().documentType(LEGAL_STATEMENT_PROBATE).build()));
        when(caseDataMock.getBoDocumentsUploaded()).thenReturn(uploaded);

        CCDData ccdData = underTest.transform(callbackRequestMock);

        assertAll(ccdData);
        assertCaseSubmissionDate(ccdData);
        assertFalse(ccdData.isHasUploadedLegalStatement());

    }

    @Test
    void shouldConvertRequestToDataBeanWithFeeServiceForCheque() {

        when(caseDataMock.getSolsPaymentMethods()).thenReturn(PAYMENT_METHOD_CHEQUE);
        when(caseDataMock.getTotalFee()).thenReturn(TOTAL_FEE);
        when(caseDataMock.getApplicationFee()).thenReturn(APPLICATION_FEE);
        when(caseDataMock.getSolsPBANumber()).thenReturn(null);

        CCDData ccdData = underTest.transform(callbackRequestMock);

        assertEquals(SOLICITOR_FIRM_NAME, ccdData.getSolicitor().getFirmName());
        assertEquals(SOLICITOR_FIRM_LINE1, ccdData.getSolicitor().getFirmAddress().getAddressLine1());
        assertEquals(SOLICITOR_FIRM_POSTCODE, ccdData.getSolicitor().getFirmAddress().getPostCode());
        assertEquals(SOLICITOR_SOT_NAME, ccdData.getSolicitor().getFullname());
        assertNull(ccdData.getFee().getSolsPBANumber());
        assertEquals(DECEASED_FIRSTNAME, ccdData.getDeceased().getFirstname());
        assertEquals(DECEASED_LASTNAME, ccdData.getDeceased().getLastname());
        assertEquals(DOB, ccdData.getDeceased().getDateOfBirth());
        assertEquals(DOD, ccdData.getDeceased().getDateOfDeath());
        assertEquals(IHT_FORM_ID, ccdData.getIht().getFormName());
        assertEquals(IHT_GROSS, ccdData.getIht().getGrossValue());
        assertEquals(IHT_NET, ccdData.getIht().getNetValue());
        assertTrue(ccdData.getExecutors().get(2).isApplying());
        assertEquals(TOTAL_FEE.floatValue(), ccdData.getFee().getAmount().floatValue(), 0.01);
        assertEquals(APPLICATION_FEE.floatValue(), ccdData.getFee().getApplicationFee().floatValue(), 0.01);
        assertFees(ccdData);

        assertCaseSubmissionDate(ccdData);
        assertEquals(APPLICATION_FEE.floatValue(), ccdData.getFee().getApplicationFee().floatValue(), 0.01);
    }

    @Test
    void shouldConvertRequestToDataBeanWithFeeServiceForFee() {

        when(caseDataMock.getSolsPaymentMethods()).thenReturn(PAYMENT_METHOD_FEE);
        when(caseDataMock.getTotalFee()).thenReturn(TOTAL_FEE);
        when(caseDataMock.getApplicationFee()).thenReturn(APPLICATION_FEE);

        CCDData ccdData = underTest.transform(callbackRequestMock);

        assertAll(ccdData);
        assertCaseSubmissionDate(ccdData);
        assertEquals(APPLICATION_FEE.floatValue(), ccdData.getFee().getApplicationFee().floatValue(), 0.01);
    }

    @Test
    void shouldConvertRequestToDataBeanWithNoPBAsSet() {

        when(caseDataMock.getSolsPaymentMethods()).thenReturn(PAYMENT_METHOD_FEE);
        when(caseDataMock.getTotalFee()).thenReturn(TOTAL_FEE);
        when(caseDataMock.getApplicationFee()).thenReturn(APPLICATION_FEE);
        when(caseDataMock.getSolsPBANumber()).thenReturn(null);

        CCDData ccdData = underTest.transform(callbackRequestMock);

        assertBasic(ccdData);
        assertFees(ccdData);
        assertEquals(null, ccdData.getFee().getSolsPBANumber());
        assertCaseSubmissionDate(ccdData);
        assertEquals(APPLICATION_FEE.floatValue(), ccdData.getFee().getApplicationFee().floatValue(), 0.01);
    }

    @Test
    void shouldConvertRequestToDataBeanWithNoPBAValueSet() {

        when(caseDataMock.getSolsPaymentMethods()).thenReturn(PAYMENT_METHOD_FEE);
        when(caseDataMock.getTotalFee()).thenReturn(TOTAL_FEE);
        when(caseDataMock.getApplicationFee()).thenReturn(APPLICATION_FEE);
        when(caseDataMock.getSolsPBANumber()).thenReturn(DynamicList.builder().build());

        CCDData ccdData = underTest.transform(callbackRequestMock);

        assertBasic(ccdData);
        assertFees(ccdData);
        assertEquals(null, ccdData.getFee().getSolsPBANumber());
        assertCaseSubmissionDate(ccdData);
        assertEquals(APPLICATION_FEE.floatValue(), ccdData.getFee().getApplicationFee().floatValue(), 0.01);
    }

    @Test
    void shouldConvertRequestToDataBeanWithFeeDataMissing() {
        when(caseDataMock.getFeeForUkCopies()).thenReturn(null);
        when(caseDataMock.getFeeForNonUkCopies()).thenReturn(null);

        CCDData ccdData = underTest.transform(callbackRequestMock);

        assertBasic(ccdData);
        assertNull(ccdData.getFee().getFeeForUkCopies());
        assertNull(ccdData.getFee().getFeeForNonUkCopies());
    }

    @Test
    void shouldConvertRequestToDataBeanWithLastModifiedDate() {

        when(caseDetailsMock.getLastModified()).thenReturn(new String[]{"2022", "1", "1", "1"});

        CCDData ccdData = underTest.transform(callbackRequestMock);

        assertAll(ccdData);
        assertEquals(LocalDate.of(2022, 1, 1), ccdData.getCaseSubmissionDate());
    }

    @Test
    void shouldConvertRequestToDataBeanWithNoLastModifiedDate() {
        when(caseDetailsMock.getLastModified()).thenReturn(null);

        CCDData ccdData = underTest.transform(callbackRequestMock);

        assertAll(ccdData);
        assertNull(ccdData.getCaseSubmissionDate());
    }

    @Test
    void shouldConvertRequestToDataBeanWithLastModifiedDateEmptyData() {
        String[] lmDate = {};
        when(caseDetailsMock.getLastModified()).thenReturn(lmDate);

        CCDData ccdData = underTest.transform(callbackRequestMock);

        assertAll(ccdData);
        assertNull(ccdData.getCaseSubmissionDate());
    }

    @Test
    void shouldConvertRequestToDataBeanWithLastModifiedDateMissingData() {
        String[] lmDate = {null, null, null, null, null, null, null, null};
        when(caseDetailsMock.getLastModified()).thenReturn(lmDate);

        CCDData ccdData = underTest.transform(callbackRequestMock);

        assertAll(ccdData);
        assertNull(ccdData.getCaseSubmissionDate());
    }

    @Test
    void shouldConvertRequestToDataBeanWhenLastModifiedDateHasNullSecondElement() {

        when(caseDetailsMock.getLastModified()).thenReturn(new String[]{"", null});

        CCDData ccdData = underTest.transform(callbackRequestMock);

        assertAll(ccdData);
        assertNull(ccdData.getCaseSubmissionDate());
    }

    @Test
    void shouldConvertRequestToDataBeanWhenLastModifiedDateHasNullThirdElement() {

        when(caseDetailsMock.getLastModified()).thenReturn(new String[]{"", "", null});

        CCDData ccdData = underTest.transform(callbackRequestMock);

        assertAll(ccdData);
        assertNull(ccdData.getCaseSubmissionDate());
    }

    @Test
    void shouldConvertRequestToDataBeanWhenLastModifiedDateNFE() {

        when(caseDetailsMock.getLastModified()).thenReturn(new String[]{"1", "2", "number"});

        CCDData ccdData = underTest.transform(callbackRequestMock);

        assertAll(ccdData);
        assertNull(ccdData.getCaseSubmissionDate());
    }

    @Test
    void shouldConvertRequestToDataBeanWhenLastModifiedDateInvalidDate() {

        when(caseDetailsMock.getLastModified()).thenReturn(new String[]{"2022", "1", "50"});

        CCDData ccdData = underTest.transform(callbackRequestMock);

        assertAll(ccdData);
        assertNull(ccdData.getCaseSubmissionDate());
    }

    @Test
    void shouldConvertRequestToDataBeanForPA16Form() {

        when(caseDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn("ChildAdopted");
        when(caseDataMock.getSolsApplicantSiblings()).thenReturn(NO);
        when(caseDataMock.getSolsSpouseOrCivilRenouncing()).thenReturn(YES);

        CCDData ccdData = underTest.transform(callbackRequestMock);

        assertAll(ccdData);
        assertEquals("ChildAdopted", ccdData.getSolsApplicantRelationshipToDeceased());
        assertEquals("No", ccdData.getSolsApplicantSiblings());
        assertEquals("Yes", ccdData.getSolsSpouseOrCivilRenouncing());

    }

    @Test
    void shouldConvertRequestToDataBeanForPA17Form() {

        when(caseDataMock.getTitleAndClearingType()).thenReturn("TCTPartAllRenouncing");

        CCDData ccdData = underTest.transform(callbackRequestMock);

        assertAll(ccdData);
        assertEquals("TCTPartAllRenouncing", ccdData.getTitleAndClearingType());
    }

    @Test
    void shouldConvertRequestToDataBeanForIhtEstate() {

        when(caseDataMock.getIhtFormEstateValuesCompleted()).thenReturn(YES);
        when(caseDataMock.getIhtFormEstate()).thenReturn(IHT207_VALUE);
        when(caseDataMock.getIhtEstateGrossValue()).thenReturn(IHT_ESTATE_GROSS);
        when(caseDataMock.getIhtEstateNetValue()).thenReturn(IHT_ESTATE_NET);

        CCDData ccdData = underTest.transform(callbackRequestMock);

        assertAll(ccdData);
        assertEquals("Yes", ccdData.getIht().getIhtFormEstateValuesCompleted());
        assertEquals("IHT207", ccdData.getIht().getIhtFormEstate());
        assertEquals(IHT_ESTATE_GROSS, ccdData.getIht().getIhtEstateGrossValue());
        assertEquals(IHT_ESTATE_NET, ccdData.getIht().getIhtEstateNetValue());
    }

    private void assertAll(CCDData ccdData) {
        assertBasic(ccdData);
        assertFees(ccdData);
        assertPBAs(ccdData);
    }

    private void assertBasic(CCDData ccdData) {
        assertEquals(SOLICITOR_FIRM_NAME, ccdData.getSolicitor().getFirmName());
        assertEquals(SOLICITOR_FIRM_LINE1, ccdData.getSolicitor().getFirmAddress().getAddressLine1());
        assertEquals(SOLICITOR_FIRM_POSTCODE, ccdData.getSolicitor().getFirmAddress().getPostCode());
        assertEquals(SOLICITOR_SOT_NAME, ccdData.getSolicitor().getFullname());
        assertEquals(DECEASED_FIRSTNAME, ccdData.getDeceased().getFirstname());
        assertEquals(DECEASED_LASTNAME, ccdData.getDeceased().getLastname());
        assertEquals(DOB, ccdData.getDeceased().getDateOfBirth());
        assertEquals(DOD, ccdData.getDeceased().getDateOfDeath());
        assertEquals(IHT_FORM_ID, ccdData.getIht().getFormName());
        assertEquals(IHT_GROSS, ccdData.getIht().getGrossValue());
        assertEquals(IHT_NET, ccdData.getIht().getNetValue());
        assertTrue(ccdData.getExecutors().get(2).isApplying());
        assertEquals(TOTAL_FEE.floatValue(), ccdData.getFee().getAmount().floatValue(), 0.01);
        assertEquals(APPLICATION_FEE.floatValue(), ccdData.getFee().getApplicationFee().floatValue(), 0.01);

        assertEquals(true, ccdData.getExecutors().get(0).isApplying());
        assertEquals(false, ccdData.getExecutors().get(1).isApplying());
        assertEquals("Renunciation", ccdData.getExecutors().get(1).getReasonNotApplying());
        assertEquals("Digital", ccdData.getChannelChoice());
    }

    private void assertCaseSubmissionDate(CCDData ccdData) {
        assertEquals(2018, ccdData.getCaseSubmissionDate().getYear());
        assertEquals(1, ccdData.getCaseSubmissionDate().getMonthValue());
        assertEquals(2, ccdData.getCaseSubmissionDate().getDayOfMonth());
        assertEquals(7, ccdData.getExecutors().size());

    }

    private void assertFees(CCDData ccdData) {
        assertEquals(FEE_UK_COPIES.floatValue(), ccdData.getFee().getFeeForUkCopies().floatValue(), 0.01);
        assertEquals(FEE_NON_UK_COPIES.floatValue(), ccdData.getFee().getFeeForNonUkCopies().floatValue(), 0.01);
    }

    private void assertPBAs(CCDData ccdData) {
        assertEquals(SELECTED_PBA_NUMBER, ccdData.getFee().getSolsPBANumber());
        assertEquals(PBA_PAYMENT_REFERENCE, ccdData.getFee().getSolsPBAPaymentReference());
    }


}
