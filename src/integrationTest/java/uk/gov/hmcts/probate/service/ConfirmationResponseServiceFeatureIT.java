package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.Deceased;
import uk.gov.hmcts.probate.model.ccd.Executor;
import uk.gov.hmcts.probate.model.ccd.Fee;
import uk.gov.hmcts.probate.model.ccd.InheritanceTax;
import uk.gov.hmcts.probate.model.ccd.Solicitor;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.AfterSubmitCallbackResponse;
import uk.gov.hmcts.probate.util.TestUtils;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.YES;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(properties = {"markdown.templatesDirectory=templates/markdown/"})
class ConfirmationResponseServiceFeatureIT {

    private static final String REASON_MENTALLY_INCAPABLE = "MentallyIncapable";
    private static final String REASON_RENOUNCED = "Renunciation";
    private static final String REASON_DIED_BEFORE = "DiedBefore";
    private static final String SOLICITOR_REFERENCE = "SOL_REF_X12345";
    private static final LocalDate DOB = LocalDate.of(1990, 4, 4);
    private static final LocalDate DOD = LocalDate.of(2017, 4, 4);
    private static final String FORENAME = "Andy";
    private static final String SURNAME = "Michael";
    private static final String SOLICITOR_FIRM_NAME = "Legal Service Ltd";
    private static final String SOLICITOR_FIRM_LINE1 = "Sols Add Line 1";
    private static final String SOLICITOR_FIRM_POSTCODE = "SW1E 6EA";
    private static final String IHT_FORM = "IHT207";
    private static final String SOLICITOR_NAME = "Peter Crouch";
    private static final String SOLICITOR_JOB_TITLE = "Lawyer";
    private static final String PAYMENT_METHOD = "fee account";
    private static final BigDecimal APPLICATION_FEE = BigDecimal.TEN;
    private static final BigDecimal TOTAL_FEE = BigDecimal.TEN;
    private static final BigDecimal FEE_UK = new BigDecimal(100);
    private static final BigDecimal FEE_NON_UK = new BigDecimal(200);
    private static final BigDecimal NET = BigDecimal.valueOf(900f);
    private static final BigDecimal GROSS = BigDecimal.valueOf(1000f);
    private static final Long EXTRA_UK = 1L;
    private static final Long EXTRA_OUTSIDE_UK = 2L;
    private static final String SOLS_FEE_PBA = "PBA-12345";
    private static final String SOLS_PBA_PAY_REF = "Fee account PBA-12345";
    private static final String ADDITIONAL_INFO = "ADDITIONAL INFO";
    private static final String WILL_TYPE_INTESTACY = "NoWill";
    private static final String WILL_TYPE_PROBATE = "WillLeft";
    public static final Long ID = 1L;
    private final TestUtils testUtils = new TestUtils();
    @Autowired
    private ConfirmationResponseService confirmationResponseService;

    @MockitoBean
    private CoreCaseDataApi coreCaseDataApi;

    @MockitoBean
    private CaseData caseDataMock;

    @Test
    void shouldGenerateCorrectConfirmationBodyWithNoAdditionalOptions() throws Exception {
        CCDData ccdData = createCCDataBuilder().build();
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData,
            caseDataMock);

        String expectedConfirmationBody = testUtils.getStringFromFile("expectedConfirmationBody.md");

        assertThat(stopConfirmation.getConfirmationBody(), is(expectedConfirmationBody));
    }

    @Test
    void shouldGenerateCorrectConfirmationBodyWithNoWill() throws Exception {
        CCDData ccdData = createCCDataBuilder().solsWillType(WILL_TYPE_INTESTACY).build();
        when(caseDataMock.getSolsWillType()).thenReturn(WILL_TYPE_INTESTACY);
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData,
            caseDataMock);

        String expectedConfirmationBody = testUtils.getStringFromFile("expectedConfirmationBodyWithNoWill.md");

        assertThat(stopConfirmation.getConfirmationBody(), is(expectedConfirmationBody));
    }

    @Test
    void shouldGenerateCorrectConfirmationBodyWithRenouncingExecutor() throws Exception {
        when(caseDataMock.getSolsAdditionalExecutorList()).thenReturn(createAdditionalExecutor("Tim", "Smith", false,
            REASON_RENOUNCED));
        when(caseDataMock.getOtherExecutorExists()).thenReturn("Yes");
        CCDData ccdData = createCCDataBuilder().build();
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData,
            caseDataMock);

        String expectedConfirmationBody =
            testUtils.getStringFromFile("expectedConfirmationBodyWithRenouncingExecutor.md");

        assertThat(stopConfirmation.getConfirmationBody(), is(expectedConfirmationBody));
    }

    @Test
    void shouldGenerateCorrectConfirmationBodyWithMultipleRenouncingExecutors() throws Exception {
        when(caseDataMock.getSolsAdditionalExecutorList()).thenReturn(createAdditionalExecutor("Tim", "Smith", false,
            REASON_RENOUNCED));
        when(caseDataMock.getAdditionalExecutorsNotApplying()).thenReturn(createAdditionalExecutorNotApplying("John",
            "Smith", REASON_RENOUNCED));
        when(caseDataMock.getSolsSOTName()).thenReturn("Toby Smith");
        when(caseDataMock.getSolsSolicitorIsApplying()).thenReturn("No");
        when(caseDataMock.getSolsSolicitorNotApplyingReason()).thenReturn(REASON_RENOUNCED);
        when(caseDataMock.getOtherExecutorExists()).thenReturn("Yes");
        when(caseDataMock.getWillAccessOriginal()).thenReturn("Yes");
        CCDData ccdData =
            createCCDataBuilder().build();
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData,
            caseDataMock);

        String expectedConfirmationBody =
            testUtils.getStringFromFile("expectedConfirmationBodyWithMultipleRenouncingExecutors.md");

        assertThat(stopConfirmation.getConfirmationBody(), is(expectedConfirmationBody));
    }

    @Test
    void shouldGenerateCorrectConfirmationBodyWithDeadExecutor() throws Exception {
        Executor deadExecutor = createDeadExecutor("Bob", "Martin");
        CCDData ccdData = createCCDataBuilder().executors(Collections.singletonList(deadExecutor)).build();
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData,
            caseDataMock);

        String expectedConfirmationBody = testUtils.getStringFromFile("expectedConfirmationBodyWithDeadExecutor.md");

        assertThat(stopConfirmation.getConfirmationBody(), is(expectedConfirmationBody));
    }

    @Test
    void shouldGenerateCorrectConfirmationBodyWithMultipleDeadExecutors() throws Exception {
        Executor deadExecutor = createDeadExecutor("Bob", "Martin");
        Executor deadExecutor2 = createDeadExecutor("John", "Martin");
        CCDData ccdData = createCCDataBuilder().executors(Arrays.asList(deadExecutor, deadExecutor2)).build();
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData,
            caseDataMock);

        String expectedConfirmationBody =
            testUtils.getStringFromFile("expectedConfirmationBodyWithMultipleDeadExecutors.md");

        assertThat(stopConfirmation.getConfirmationBody(), is(expectedConfirmationBody));
    }

    @Test
    void shouldGenerateCorrectConfirmationBodyWithIHT400421() throws Exception {
        CCDData ccdData = createCCDataBuilder().iht(createInheritanceTax("IHT400421")).build();
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData,
            caseDataMock);

        String expectedConfirmationBody = testUtils.getStringFromFile("expectedConfirmationBodyWithIHT400421.md");

        assertThat(stopConfirmation.getConfirmationBody(), is(expectedConfirmationBody));
    }

    @Test
    void shouldGenerateCorrectConfirmationBodyWithIHT400() throws Exception {
        CCDData ccdData = createCCDataBuilder().iht(createInheritanceTax("IHT400")).build();
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData,
                caseDataMock);

        String expectedConfirmationBody = testUtils.getStringFromFile("expectedConfirmationBodyWithIHT400421.md");

        assertThat(stopConfirmation.getConfirmationBody(), is(expectedConfirmationBody));
    }

    @Test
    void shouldGenerateCorrectConfirmationBodyWithTcResolutionLodgedWithinApp() throws Exception {
        CCDData ccdData = createCCDataBuilder().titleAndClearingType("TCTTrustCorpResWithApp").build();
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData,
            caseDataMock);

        String expectedConfirmationBody = testUtils
                .getStringFromFile("expectedConfirmationBodyWithTcResolutionLodged.md");

        assertEquals(stopConfirmation.getConfirmationBody(), expectedConfirmationBody);
    }

    @Test
    void shouldGenerateCorrectConfirmationBodyWithAllCombinationsForAdditionalOptions() throws Exception {
        Executor deadExecutor = createDeadExecutor("Bob", "Martin");
        CCDData ccdData = createCCDataBuilder()
            .executors(Arrays.asList(deadExecutor))
            .iht(createInheritanceTax("IHT400421")).build();
        when(caseDataMock.getSolsAdditionalExecutorList()).thenReturn(createAdditionalExecutor("Tim", "Smith", false,
            REASON_RENOUNCED));
        when(caseDataMock.getOtherExecutorExists()).thenReturn("Yes");
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData,
            caseDataMock);

        String expectedConfirmationBody = testUtils.getStringFromFile("expectedConfirmationBodyWithAllCombinations.md");

        assertThat(stopConfirmation.getConfirmationBody(), is(expectedConfirmationBody));
    }

    @Test
    void shouldGenerateCorrectConfirmationBodyWithAllCombinationsForAdditionalOptionsAndMultiples()
        throws Exception {
        Executor deadExecutor = createDeadExecutor("Bob", "Martin");
        Executor deadExecutor2 = createDeadExecutor("John", "Martin");
        CCDData ccdData = createCCDataBuilder()
            .executors(Arrays.asList(deadExecutor, deadExecutor2))
            .iht(createInheritanceTax("IHT400421")).build();
        when(caseDataMock.getOtherExecutorExists()).thenReturn("Yes");
        when(caseDataMock.getSolsAdditionalExecutorList()).thenReturn(createAdditionalExecutor("Tim", "Smith", false,
            REASON_RENOUNCED));
        when(caseDataMock.getAdditionalExecutorsNotApplying()).thenReturn(createAdditionalExecutorNotApplying("John",
            "Smith", REASON_RENOUNCED));

        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData,
            caseDataMock);

        String expectedConfirmationBody =
            testUtils.getStringFromFile("expectedConfirmationBodyWithAllCombinationsAndMultiples.md");

        assertThat(stopConfirmation.getConfirmationBody(), is(expectedConfirmationBody));
    }

    @Test
    void shouldGenerateCorrectConfirmationBodyCaveats() throws Exception {
        CaveatData caveatData = createCaveatDataBuilder().build();
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(caveatData,
                123L);

        String expectedConfirmationBody = testUtils.getStringFromFile("expectedConfirmationBodyCaveat.md");

        assertThat(stopConfirmation.getConfirmationBody(), is(expectedConfirmationBody));
    }

    @Test
    void shouldGenerateCorrectCordicilsConfirmationBody() throws Exception {
        CCDData ccdData = createCCDataBuilder().iht(createInheritanceTax("IHT205")).willHasCodicils("Yes").build();
        when(caseDataMock.getWillHasCodicils()).thenReturn(YES);
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData,
            caseDataMock);

        String expectedConfirmationBody = testUtils.getStringFromFile("expectedConfirmationBodyWithWillCordicils.md");

        assertThat(stopConfirmation.getConfirmationBody(), is(expectedConfirmationBody));
    }

    @Test
    void shouldGenerateCorrectIHT217ConfirmationBody() throws Exception {
        CCDData ccdData = createCCDataBuilder().iht(createInheritanceTax("IHT205")).iht217("Yes").build();
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData,
            caseDataMock);

        String expectedConfirmationBody = testUtils.getStringFromFile("expectedConfirmationBodyWithIHT217.md");

        assertThat(stopConfirmation.getConfirmationBody(), is(expectedConfirmationBody));
    }

    @Test
    void shouldGenerateCorrectPA14FormConfirmationBody() throws Exception {
        when(caseDataMock.getOtherExecutorExists()).thenReturn("Yes");
        List<CollectionMember<AdditionalExecutor>> all = new ArrayList<>();
        all.addAll(createAdditionalExecutor("Tim", "Smith", false, REASON_MENTALLY_INCAPABLE));
        all.addAll(createAdditionalExecutor("John", "Smith", true, REASON_MENTALLY_INCAPABLE));
        when(caseDataMock.getSolsAdditionalExecutorList()).thenReturn(all);

        CCDData ccdData = createCCDataBuilder().build();
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData,
            caseDataMock);

        String expectedConfirmationBody = testUtils.getStringFromFile("expectedConfirmationBodyWithPA14Form.md");

        assertThat(stopConfirmation.getConfirmationBody(), is(expectedConfirmationBody));
    }

    @Test
    void shouldGenerateCorrectPA15FormConfirmationBody() throws Exception {
        when(caseDataMock.getOtherExecutorExists()).thenReturn("Yes");
        List<CollectionMember<AdditionalExecutor>> all = new ArrayList<>();
        all.addAll(createAdditionalExecutor("Tim", "Smith", false, REASON_RENOUNCED));
        all.addAll(createAdditionalExecutor("John", "Smith", true, REASON_RENOUNCED));
        when(caseDataMock.getSolsAdditionalExecutorList()).thenReturn(all);

        CCDData ccdData = createCCDataBuilder().build();
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData,
            caseDataMock);

        String expectedConfirmationBody = testUtils.getStringFromFile("expectedConfirmationBodyWithPA15Form.md");

        assertThat(stopConfirmation.getConfirmationBody(), is(expectedConfirmationBody));
    }

    @Test
    void shouldGenerateCorrectPA16FormConfirmationBody() throws Exception {
        CCDData ccdData = createCCDataBuilder().build();
        when(caseDataMock.getSolsApplicantSiblings()).thenReturn("No");
        when(caseDataMock.getSolsSpouseOrCivilRenouncing()).thenReturn("Yes");
        when(caseDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn("ChildAdopted");
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData,
            caseDataMock);

        String expectedConfirmationBody = testUtils.getStringFromFile("expectedConfirmationBodyWithPA16Form.md");

        assertThat(stopConfirmation.getConfirmationBody(), is(expectedConfirmationBody));
    }

    @Test
    void shouldGenerateCorrectPA17FormConfirmationBody() throws Exception {
        CCDData ccdData = createCCDataBuilder().build();
        when(caseDataMock.getTitleAndClearingType()).thenReturn("TCTPartOthersRenouncing");
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData,
            caseDataMock);

        String expectedConfirmationBody = testUtils.getStringFromFile("expectedConfirmationBodyWithPA17Form.md");

        assertThat(stopConfirmation.getConfirmationBody(), is(expectedConfirmationBody));
    }

    @Test
    void shouldGenerateNotarialCopyConfirmationBody() throws Exception {
        CCDData ccdData = createCCDataBuilder().build();
        when(caseDataMock.getWillAccessNotarial()).thenReturn(YES);
        AfterSubmitCallbackResponse confirmation = confirmationResponseService.getNextStepsConfirmation(ccdData,
            caseDataMock);
        String expectedConfirmationBody = testUtils.getStringFromFile(
            "expectedConfirmationBodyWithNotarialCopy.md");
        assertEquals(expectedConfirmationBody, confirmation.getConfirmationBody());
    }

    private CCDData.CCDDataBuilder createCCDataBuilder() {
        return CCDData.builder()
            .solicitorReference(SOLICITOR_REFERENCE)
            .caseSubmissionDate(LocalDate.of(2018, 1, 1))
            .solicitor(createSolicitor())
            .deceased(createDeceased())
            .iht(createInheritanceTax(IHT_FORM))
            .fee(createFee())
            .executors(new ArrayList<>())
            .solsAdditionalInfo(ADDITIONAL_INFO)
            .solsWillType(WILL_TYPE_PROBATE)
            .solsCoversheetDocument(createSolsCoverSheet().getDocumentLink())
            .caseId(ID);
    }

    private CaveatData.CaveatDataBuilder createCaveatDataBuilder() {
        return CaveatData.builder()
            .solsSolicitorAppReference(SOLICITOR_REFERENCE)
            .applicationSubmittedDate(LocalDate.of(2018, 1, 1))
            .solsPaymentMethods(PAYMENT_METHOD)
            .solsPBANumber(DynamicList.builder()
                .value(DynamicListItem.builder().code(SOLS_FEE_PBA).label(SOLS_FEE_PBA).build()).build())
            .solsPBAPaymentReference(SOLS_PBA_PAY_REF);
    }

    private Fee createFee() {
        return Fee.builder()
            .extraCopiesOfGrant(EXTRA_UK)
            .outsideUKGrantCopies(EXTRA_OUTSIDE_UK)
            .paymentMethod(PAYMENT_METHOD)
            .amount(TOTAL_FEE)
            .applicationFee(APPLICATION_FEE)
            .solsPBANumber(SOLS_FEE_PBA)
            .solsPBAPaymentReference(SOLS_PBA_PAY_REF)
            .feeForUkCopies(FEE_UK)
            .feeForNonUkCopies(FEE_NON_UK)
            .build();
    }

    private InheritanceTax createInheritanceTax(String ihtForm) {
        return InheritanceTax.builder()
            .formName(ihtForm)
            .netValue(NET)
            .grossValue(GROSS)
            .build();
    }

    private Deceased createDeceased() {
        return Deceased.builder()
            .firstname(FORENAME)
            .lastname(SURNAME)
            .dateOfBirth(DOB)
            .dateOfDeath(DOD)
            .build();
    }

    private Solicitor createSolicitor() {
        SolsAddress solsAddress = SolsAddress.builder().addressLine1(SOLICITOR_FIRM_LINE1)
            .postCode(SOLICITOR_FIRM_POSTCODE)
            .build();
        return Solicitor.builder()
            .firmName(SOLICITOR_FIRM_NAME)
            .firmAddress(solsAddress)
            .fullname(SOLICITOR_NAME)
            .jobRole(SOLICITOR_JOB_TITLE)
            .build();
    }

    private Executor createDeadExecutor(String forename, String lastname) {
        return Executor.builder()
            .forename(forename)
            .lastname(lastname)
            .reasonNotApplying(REASON_DIED_BEFORE)
            .build();
    }


    private Executor createRenouncingExecutor(String forename, String lastname) {
        return Executor.builder()
            .forename(forename)
            .lastname(lastname)
            .reasonNotApplying(REASON_RENOUNCED)
            .build();
    }

    private Document createSolsCoverSheet() {
        return Document.builder().documentType(DocumentType.SOLICITOR_COVERSHEET)
            .documentLink(DocumentLink.builder().documentFilename("solicitorCoverSheet.pdf").build())
            .build();
    }

    private List<CollectionMember<AdditionalExecutorNotApplying>> createAdditionalExecutorNotApplying(String forenames,
                                                                                                      String lastname,
                                                                                                      String reason) {
        return Arrays.asList(new CollectionMember<>(null, AdditionalExecutorNotApplying.builder()
            .notApplyingExecutorName(forenames + " " + lastname)
            .notApplyingExecutorReason(reason)
            .build()));
    }

    private List<CollectionMember<AdditionalExecutor>> createAdditionalExecutor(String forenames,
                                                                                String lastname, boolean applying,
                                                                                String reason) {
        return Arrays.asList(new CollectionMember<>(null, AdditionalExecutor.builder()
            .additionalExecForenames(forenames)
            .additionalExecLastname(lastname)
            .additionalApplying(applying ? "Yes" : "No")
            .additionalExecReasonNotApplying(reason)
            .build()));
    }

}
