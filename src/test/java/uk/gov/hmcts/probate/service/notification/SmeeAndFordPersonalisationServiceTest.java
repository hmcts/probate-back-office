package uk.gov.hmcts.probate.service.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.ProbateAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.service.FeatureToggleService;
import uk.gov.hmcts.probate.service.FileSystemResourceService;
import uk.gov.hmcts.probate.util.TestUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.OTHER;

class SmeeAndFordPersonalisationServiceTest {

    @InjectMocks
    private SmeeAndFordPersonalisationService smeeAndFordPersonalisationService;

    @Mock
    private FileSystemResourceService fileSystemResourceService;

    @Mock
    private FeatureToggleService featureToggleService;

    private ReturnedCaseDetails returnedCaseDetailsPersonal;
    private ReturnedCaseDetails returnedCaseDetailsSolicitor;
    private ReturnedCaseDetails returnedCaseDetailsTypeWill;

    private static final Long ID = 1234567812345678L;
    private static final LocalDateTime LAST_MODIFIED = LocalDateTime.now(ZoneOffset.UTC).minusYears(2);

    private static final BigDecimal GROSS = BigDecimal.valueOf(1000000);
    private static final BigDecimal NET = BigDecimal.valueOf(900000);

    private static final BigDecimal GROSS_WITH_DECIMAL = BigDecimal.valueOf(1000000.34);
    private static final BigDecimal NET_WITH_DECIMAL = BigDecimal.valueOf(900000.56);
    private TestUtils testUtils = new TestUtils();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(fileSystemResourceService.getFileFromResourceAsString("templates/dataExtracts/SmeeAndFordHeaderRow.csv"))
            .thenReturn("Registry name|Date of Issue|Case reference number|Full name of deceased|All names which the "
                + "deceased was otherwise known as|Type of Grant|Date of death|Deceased Address|Deceased Town|"
                + "Deceased County|Deceased Postcode|Deceased Country|Applying executor 1 Name|Applying executor 1 "
                + "Address|Applying executor 1 Town|Applying executor 1 County|Applying executor 1 Postcode|Applying "
                + "executor 1 Country|Applying executor 2 Name|Applying executor 2 Address|Applying executor 2 Town|"
                + "Applying executor 2 County|Applying executor 2 Postcode|Applying executor 2 Country|Applying "
                + "executor 3 Name|Applying executor 3 Address|Applying executor 3 Town|Applying executor 3 County|"
                + "Applying executor 3 Postcode|Applying executor 3 Country|Primary applicant Name|Primary applicant "
                + "Address|Primary applicant Town|Primary applicant County|Primary applicant Postcode|Primary "
                + "applicant Country|Gross|Net|Solicitor Name|Solicitor Reference|Solicitor Address|Solicitor Town|"
                + "Solicitor County|Solicitor Postcode|Solicitor Country|date of birth|Field which alerts us to "
                + "Codicil being present|pdf file name field for Wills|pdf for Digital Grant");
        when(featureToggleService.isPoundValueFeatureToggleOn()).thenReturn(true);
    }

    private CaseData.CaseDataBuilder getCaseDataBuilder(ApplicationType applicationType,
                                                        int numExecs, String hasScanned,
                                                        boolean hasGrant, boolean hasCodicils,
                                                        boolean hasDeceasedAlias, boolean hasSolsDeceasedAlias,
                                                        boolean hasDOD, boolean isWelsh) {
        List<CollectionMember<ProbateAliasName>> deceasedAliases = null;
        if (hasDeceasedAlias) {
            deceasedAliases = new ArrayList();
            deceasedAliases.add(new CollectionMember<ProbateAliasName>(buildAlias("Dec", "1")));
            deceasedAliases.add(new CollectionMember<ProbateAliasName>(buildAlias("Dec", "2")));
        }
        List<CollectionMember<AliasName>> solsDeceasedAliases = null;
        if (hasSolsDeceasedAlias) {
            solsDeceasedAliases = new ArrayList();
            solsDeceasedAliases.add(new CollectionMember<AliasName>(buildSolsAlias("1")));
            solsDeceasedAliases.add(new CollectionMember<AliasName>(buildSolsAlias("2")));
        }

        SolsAddress deceasedAddress = buildAddress("Dec");
        List<CollectionMember<AdditionalExecutorApplying>> additionalExecsApplying = new ArrayList();
        if (numExecs == -1) {
            additionalExecsApplying = null;
        } else {
            for (int i = 0; i < numExecs; i++) {
                additionalExecsApplying
                    .add(new CollectionMember<AdditionalExecutorApplying>(buildApplyingExec("Applying", "" + i, true)));
            }
        }
        CaseData.CaseDataBuilder caseDataBuilder = CaseData.builder()
            .registryLocation("Registry Address")
            .grantIssuedDate("2021-12-31")
            .deceasedForenames("Jack")
            .deceasedSurname("Michelson")
            .boDeceasedHonours("OBE")
            .deceasedAliasNameList(hasDeceasedAlias ? deceasedAliases : null)
            .solsDeceasedAliasNamesList(hasSolsDeceasedAlias ? solsDeceasedAliases : null)
            .caseType("gop")
            .applicationType(applicationType)
            .deceasedDateOfDeath(hasDOD ? LocalDate.of(2020, 12, 31) : null)
            .deceasedAddress(deceasedAddress)
            .additionalExecutorsApplying(additionalExecsApplying)
            .primaryApplicantForenames("PrimaryFN")
            .primaryApplicantSurname("PrimarySN1 PrimarySN2")
            .primaryApplicantAlias("PrimaryAlias")
            .primaryApplicantAddress(applicationType == SOLICITOR ? null : buildAddress("Prim"))
            .ihtGrossValue(GROSS)
            .ihtNetValue(NET)
            .deceasedDateOfBirth(LocalDate.of(2000, 12, 1))
            .solsSolicitorFirmName(applicationType == SOLICITOR ? "SolFirmName" : "")
            .solsSolicitorAddress(applicationType == SOLICITOR ? buildAddress("Sol") : null)
            .solsSolicitorAppReference(applicationType == SOLICITOR ? "SolAppRef" : null)
            .scannedDocuments(buildScannedDocs(hasScanned))
            .registryLocation("Cardiff")
            .willHasCodicils(hasCodicils ? YES : NO)
            .probateDocumentsGenerated(hasGrant ? buildGeneratedDocs() : new ArrayList<CollectionMember<Document>>());

        return caseDataBuilder;
    }

    private AdditionalExecutorApplying buildApplyingExec(String prefix, String num, boolean withAddress) {
        SolsAddress execAddress = buildAddress(prefix + "Exec" + num);

        return AdditionalExecutorApplying.builder()
            .applyingExecutorFirstName(prefix + "Exec" + num + "FN")
            .applyingExecutorLastName(prefix + "Exec" + num + "LN")
            .applyingExecutorOtherNames(prefix + "Exec" + num + "ON")
            .applyingExecutorAddress(withAddress ? execAddress : null)
            .applyingExecutorEmail("exec1@probate-test.com")
            .applyingExecutorPhoneNumber("0711111111")
            .build();
    }

    private ProbateAliasName buildAlias(String prefix, String suffix) {
        return ProbateAliasName.builder()
            .forenames(prefix + "AliasForename" + suffix)
            .lastName(prefix + "AliasLastName" + suffix)
            .build();
    }

    private AliasName buildSolsAlias(String suffix) {
        return AliasName.builder()
            .solsAliasname("SolsAliasForename" + suffix + " SolsAliasLastname" + suffix)
            .build();
    }

    private List<CollectionMember<Document>> buildGeneratedDocs() {
        List<CollectionMember<Document>> docs = new ArrayList<>();
        docs.add(new CollectionMember<Document>(Document.builder()
            .documentType(OTHER)
            .documentFileName("OtherFileName")
            .build()));
        docs.add(new CollectionMember<Document>(Document.builder()
            .documentType(DIGITAL_GRANT)
            .documentFileName("GrantFileName.pdf")
            .build()));

        return docs;
    }

    private List<CollectionMember<ScannedDocument>> buildScannedDocs(String withTypeWill) {
        List<CollectionMember<ScannedDocument>> docs = new ArrayList<>();
        if (withTypeWill.equals("YesWithoutTypeWill")) {
            docs.add(new CollectionMember<>(ScannedDocument.builder()
                    .type(OTHER.name())
                    .subtype(Constants.DOC_SUBTYPE_WILL)
                    .fileName("ScannedSubtypeWillFileName.pdf")
                    .build()));
        } else if (withTypeWill.equals("YesWithTypeWill")) {
            docs.add(new CollectionMember<>(ScannedDocument.builder()
                    .type(Constants.DOC_TYPE_WILL)
                    .subtype(Constants.DOC_SUBTYPE_ORIGINAL_WILL)
                    .fileName("ScannedOriginalWillFileName.pdf")
                    .build()));
        }
        docs.add(new CollectionMember<>(ScannedDocument.builder()
            .type(DocumentType.EDGE_CASE.name())
            .fileName("ScannedEdgeFileName")
            .build()));
        docs.add(new CollectionMember<>(ScannedDocument.builder()
            .type(OTHER.name())
            .subtype("somthingElse")
            .fileName("ScannedOtherFileName")
            .build()));
        if (withTypeWill.equals("No")) {
            docs = null;
        }
        return docs;
    }

    private SolsAddress buildAddress(String pre) {

        return SolsAddress.builder()
            .addressLine1(pre + "Add1")
            .addressLine2(pre + "Add2")
            .addressLine3(pre + "Add3")
            .postTown(pre + "PostTown")
            .postCode(pre + "Postcode")
            .county(pre + "County")
            .country(pre + "Country")
            .build();
    }

    @Test
    void shouldMapAllAttributes() throws IOException {
        returnedCaseDetailsPersonal = new ReturnedCaseDetails(getCaseDataBuilder(PERSONAL, 2,
                "YesWithoutTypeWill", true, true, true,
            false, true, false).build(), LAST_MODIFIED, ID);
        returnedCaseDetailsSolicitor = new ReturnedCaseDetails(getCaseDataBuilder(SOLICITOR, 2,
                "YesWithoutTypeWill", true, false,
            false, true, true, true).build(), LAST_MODIFIED, ID);

        List<ReturnedCaseDetails> cases = new ArrayList<ReturnedCaseDetails>();
        cases.add(returnedCaseDetailsPersonal);
        cases.add(returnedCaseDetailsSolicitor);
        Map<String, String> personalisation = smeeAndFordPersonalisationService.getSmeeAndFordPersonalisation(cases,
            "fromDate", "toDate");

        assertThat(personalisation.get("smeeAndFordName"), is("Smee And Ford Data extract from fromDate to toDate"));
        String smeeAndFordRespnse = testUtils.getStringFromFile("smeeAndFordExpectedData.txt");

        assertThat(personalisation.get("caseData"), is(smeeAndFordRespnse));
    }

    @Test
    void shouldMapAllAttributesWithDelimetersInContents() throws IOException {
        returnedCaseDetailsPersonal = new ReturnedCaseDetails(getCaseDataBuilder(PERSONAL, 2,
                "YesWithoutTypeWill", true, true, true,
            false, true, true)
                .primaryApplicantSurname("PrimarySN1 |PrimarySN2").build(), LAST_MODIFIED, ID);
        returnedCaseDetailsSolicitor = new ReturnedCaseDetails(getCaseDataBuilder(SOLICITOR, 2,
                "YesWithoutTypeWill", true, false,
            false, false, true, false).build(), LAST_MODIFIED, ID);

        List<ReturnedCaseDetails> cases = new ArrayList<ReturnedCaseDetails>();
        cases.add(returnedCaseDetailsPersonal);
        cases.add(returnedCaseDetailsSolicitor);
        Map<String, String> personalisation = smeeAndFordPersonalisationService.getSmeeAndFordPersonalisation(cases,
            "fromDate", "toDate");

        assertThat(personalisation.get("smeeAndFordName"), is("Smee And Ford Data extract from fromDate to toDate"));
        String smeeAndFordRespnse = testUtils.getStringFromFile("smeeAndFordExpectedDataWithDelimeters.txt");

        assertThat(personalisation.get("caseData"), is(smeeAndFordRespnse));
    }

    @Test
    void shouldMapAllAttributesWithoutAdditionalExecs() throws IOException {
        returnedCaseDetailsPersonal = new ReturnedCaseDetails(getCaseDataBuilder(PERSONAL, -1,
                "YesWithoutTypeWill", true, true, true,
            false, true, false).build(), LAST_MODIFIED, ID);
        returnedCaseDetailsSolicitor = new ReturnedCaseDetails(getCaseDataBuilder(SOLICITOR, 0,
                "YesWithoutTypeWill", true, false,
            false, false, true, true).build(), LAST_MODIFIED, ID);

        List<ReturnedCaseDetails> cases = new ArrayList<ReturnedCaseDetails>();
        cases.add(returnedCaseDetailsPersonal);
        cases.add(returnedCaseDetailsSolicitor);
        Map<String, String> personalisation = smeeAndFordPersonalisationService.getSmeeAndFordPersonalisation(cases,
            "fromDate", "toDate");

        assertThat(personalisation.get("smeeAndFordName"), is("Smee And Ford Data extract from fromDate to toDate"));
        String smeeAndFordRespnse = testUtils.getStringFromFile("smeeAndFordExpectedDataNoExecs.txt");

        assertThat(personalisation.get("caseData"), is(smeeAndFordRespnse));
    }

    @Test
    void shouldMapAllAttributesWithExtraAdditionalExecs() throws IOException {
        returnedCaseDetailsPersonal = new ReturnedCaseDetails(getCaseDataBuilder(PERSONAL, 3,
                "YesWithoutTypeWill", true, true, true,
            false, true, true).build(), LAST_MODIFIED, ID);
        returnedCaseDetailsSolicitor = new ReturnedCaseDetails(getCaseDataBuilder(SOLICITOR, 4,
                "YesWithoutTypeWill", true, false,
            false, false, true, false).build(), LAST_MODIFIED, ID);

        List<ReturnedCaseDetails> cases = new ArrayList<ReturnedCaseDetails>();
        cases.add(returnedCaseDetailsPersonal);
        cases.add(returnedCaseDetailsSolicitor);
        Map<String, String> personalisation = smeeAndFordPersonalisationService.getSmeeAndFordPersonalisation(cases,
            "fromDate", "toDate");

        assertThat(personalisation.get("smeeAndFordName"), is("Smee And Ford Data extract from fromDate to toDate"));
        String smeeAndFordRespnse = testUtils.getStringFromFile("smeeAndFordExpectedDataExtraExecs.txt");

        assertThat(personalisation.get("caseData"), is(smeeAndFordRespnse));
    }

    @Test
    void shouldMapForNoScannedOrNoGrantAttributes() throws IOException {
        returnedCaseDetailsPersonal = new ReturnedCaseDetails(getCaseDataBuilder(PERSONAL, 2,
                "No", true, false,
            true, false, true, false).build(), LAST_MODIFIED, ID);
        returnedCaseDetailsSolicitor = new ReturnedCaseDetails(getCaseDataBuilder(SOLICITOR, 2,
                "YesWithoutTypeWill", false, true,
            false, true, true, true).build(), LAST_MODIFIED, ID);

        List<ReturnedCaseDetails> cases = new ArrayList<ReturnedCaseDetails>();
        cases.add(returnedCaseDetailsPersonal);
        cases.add(returnedCaseDetailsSolicitor);
        Map<String, String> personalisation = smeeAndFordPersonalisationService.getSmeeAndFordPersonalisation(cases,
            "fromDate", "toDate");

        assertThat(personalisation.get("smeeAndFordName"), is("Smee And Ford Data extract from fromDate to toDate"));
        String smeeAndFordRespnse = testUtils.getStringFromFile("smeeAndFordExpectedDataNoDocs.txt");

        assertThat(personalisation.get("caseData"), is(smeeAndFordRespnse));
    }

    @Test
    void shouldMapAllAttributesWithNullDODCausingException() throws IOException {
        returnedCaseDetailsPersonal = new ReturnedCaseDetails(getCaseDataBuilder(PERSONAL, 2,
                "YesWithoutTypeWill", true, true, true,
            false, false, true).build(), LAST_MODIFIED, ID);
        returnedCaseDetailsSolicitor = new ReturnedCaseDetails(getCaseDataBuilder(SOLICITOR, 2,
                "YesWithoutTypeWill", true, false,
            false, true, false, false).build(), LAST_MODIFIED, ID);

        List<ReturnedCaseDetails> cases = new ArrayList<ReturnedCaseDetails>();
        cases.add(returnedCaseDetailsPersonal);
        cases.add(returnedCaseDetailsSolicitor);
        Map<String, String> personalisation = smeeAndFordPersonalisationService.getSmeeAndFordPersonalisation(cases,
            "fromDate", "toDate");

        assertThat(personalisation.get("smeeAndFordName"), is("Smee And Ford Data extract from fromDate to toDate"));
        String smeeAndFordRespnse = testUtils.getStringFromFile("smeeAndFordExpectedDataNullDOD.txt");

        assertThat(personalisation.get("caseData"), is(smeeAndFordRespnse));
    }

    @Test
    void shouldGetSmeeAndFordByteArray() {
        returnedCaseDetailsPersonal = new ReturnedCaseDetails(getCaseDataBuilder(PERSONAL, 2,
                "YesWithoutTypeWill", true, true, true,
                false, false, true).build(), LAST_MODIFIED, ID);
        returnedCaseDetailsSolicitor = new ReturnedCaseDetails(getCaseDataBuilder(SOLICITOR, 2,
                "YesWithoutTypeWill", true, false,
                false, true, false, false).build(), LAST_MODIFIED, ID);

        List<ReturnedCaseDetails> cases = new ArrayList<ReturnedCaseDetails>();
        cases.add(returnedCaseDetailsPersonal);
        cases.add(returnedCaseDetailsSolicitor);
        byte[] actual = smeeAndFordPersonalisationService.getSmeeAndFordByteArray(cases);

        assertThat(1497, is(actual.length));
    }

    @Test
    void shouldGetWillDocuments() throws IOException {
        returnedCaseDetailsTypeWill = new ReturnedCaseDetails(getCaseDataBuilder(SOLICITOR, 2,
                "YesWithTypeWill", true, false,
                false, true, true, true).build(), LAST_MODIFIED, ID);

        List<ReturnedCaseDetails> cases = new ArrayList<ReturnedCaseDetails>();
        cases.add(returnedCaseDetailsTypeWill);
        Map<String, String> personalisation = smeeAndFordPersonalisationService.getSmeeAndFordPersonalisation(cases,
                "fromDate", "toDate");

        assertThat(personalisation.get("caseData"), containsString("ScannedOriginalWillFileName.pdf"));
    }

    @Test
    void shouldNotGetOtherNonWillCase() {
        List<CollectionMember<ScannedDocument>> docs = new ArrayList<>();
        docs.add(new CollectionMember<>(ScannedDocument.builder()
                .type(OTHER.name())
                .subtype("somethingElse")
                .fileName("ScannedOtherFileName")
                .build()));
        docs.add(new CollectionMember<>(ScannedDocument.builder()
                .type(Constants.DOC_TYPE_WILL)
                .subtype(Constants.DOC_SUBTYPE_COPY_WILL)
                .fileName("ScannedCopyWillFileName.pdf")
                .build()));
        CaseData caseData = CaseData.builder()
                .scannedDocuments(docs)
                .registryLocation("Registry Address")
                .grantIssuedDate("2021-12-31")
                .deceasedForenames("Jack")
                .deceasedSurname("Michelson")
                .boDeceasedHonours("OBE")
                .deceasedAliasNameList(null)
                .solsDeceasedAliasNamesList(null)
                .caseType("gop")
                .applicationType(PERSONAL)
                .deceasedDateOfDeath(LocalDate.of(2020, 12, 31))
                .deceasedAddress(buildAddress("Dec"))
                .additionalExecutorsApplying(null)
                .primaryApplicantForenames("PrimaryFN")
                .primaryApplicantSurname("PrimarySN1 PrimarySN2")
                .primaryApplicantAlias("PrimaryAlias")
                .primaryApplicantAddress(buildAddress("Prim"))
                .ihtGrossValue(GROSS)
                .ihtNetValue(NET)
                .deceasedDateOfBirth(LocalDate.of(2000, 12, 1))
                .solsSolicitorFirmName("")
                .solsSolicitorAddress(null)
                .solsSolicitorAppReference(null)
                .registryLocation("Cardiff")
                .willHasCodicils(NO)
                .probateDocumentsGenerated(new ArrayList<CollectionMember<Document>>())
                .build();

        ReturnedCaseDetails returnedCaseDetailsTypeOtherNoWill = new ReturnedCaseDetails(caseData, LAST_MODIFIED, ID);

        List<ReturnedCaseDetails> cases = new ArrayList<ReturnedCaseDetails>();
        cases.add(returnedCaseDetailsTypeOtherNoWill);
        Map<String, String> personalisation = smeeAndFordPersonalisationService.getSmeeAndFordPersonalisation(cases,
                "fromDate", "toDate");

        assertThat(personalisation.get("caseData"), not(containsString("ScannedOtherFileName")));
        assertThat(personalisation.get("caseData"), not(containsString("ScannedCopyWillFileName")));
    }
}
