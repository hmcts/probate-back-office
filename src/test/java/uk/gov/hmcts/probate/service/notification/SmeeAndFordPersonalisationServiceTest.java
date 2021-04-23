package uk.gov.hmcts.probate.service.notification;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.ProbateAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.service.FileSystemResourceService;
import uk.gov.hmcts.probate.util.TestUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.OTHER;

public class SmeeAndFordPersonalisationServiceTest {

    @InjectMocks
    private SmeeAndFordPersonalisationService smeeAndFordPersonalisationService;

    @Mock
    private FileSystemResourceService fileSystemResourceService;

    private ReturnedCaseDetails returnedCaseDetailsPersonal;
    private ReturnedCaseDetails returnedCaseDetailsSolicitor;

    private static final Long ID = 1234567812345678L;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final BigDecimal GROSS = BigDecimal.valueOf(1000000);
    private static final BigDecimal NET = BigDecimal.valueOf(900000);

    private TestUtils testUtils = new TestUtils();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(fileSystemResourceService.getFileFromResourceAsString("templates/dataExtracts/SmeeAndFordHeaderRow.csv"))
            .thenReturn("col1,col2,col3,col4,col5,col6,col7,col8,col9,col10,"
                + "col11,col12,col13,col14,col15,col16,col17,col18,col19");
    }

    private CaseData.CaseDataBuilder getCaseDataBuilder(ApplicationType applicationType, boolean hasScanned,
                                                        boolean hasGrant, boolean hasCodicils,
                                                        boolean hasDeceasedAlias, boolean hasDOD) {
        List<CollectionMember<ProbateAliasName>> deceasedAliases = new ArrayList();
        if (hasDeceasedAlias) {
            deceasedAliases.add(new CollectionMember<ProbateAliasName>(buildAlias("Dec", "1")));
            deceasedAliases.add(new CollectionMember<ProbateAliasName>(buildAlias("Dec", "2")));
        }
        SolsAddress deceasedAddress = buildAddress("Dec");
        List<CollectionMember<AdditionalExecutorApplying>> additionalExecsApplying = new ArrayList();
        additionalExecsApplying
            .add(new CollectionMember<AdditionalExecutorApplying>(buildApplyingExec("Applying", "1", true)));
        additionalExecsApplying
            .add(new CollectionMember<AdditionalExecutorApplying>(buildApplyingExec("Applying", "2", false)));
        SolsAddress primaryAddress = buildAddress("Prim");
        CaseData.CaseDataBuilder caseDataBuilder = CaseData.builder()
            .registryLocation("Registry Address")
            .grantIssuedDate("2021-12-31")
            .deceasedForenames("Jack")
            .deceasedSurname("Michelson")
            .boDeceasedHonours("OBE")
            .deceasedAliasNameList(hasDeceasedAlias ? deceasedAliases : null)
            .caseType("gop")
            .applicationType(applicationType)
            .deceasedDateOfDeath(hasDOD ? LocalDate.of(2020, 12, 31) : null)
            .deceasedAddress(deceasedAddress)
            .additionalExecutorsApplying(additionalExecsApplying)
            .primaryApplicantForenames("PrimaryFN")
            .primaryApplicantSurname("PrimarySN1 PrimarySN2")
            .primaryApplicantAlias("PrimaryAlias")
            .primaryApplicantAddress(primaryAddress)
            .ihtGrossValue(GROSS)
            .ihtNetValue(NET)
            .deceasedDateOfBirth(LocalDate.of(2000, 12, 1))
            .solsSolicitorFirmName(applicationType == SOLICITOR ? "SolFirmName" : "")
            .solsSolicitorAddress(applicationType == SOLICITOR ? buildAddress("Sol") : null)
            .solsSolicitorAppReference(applicationType == SOLICITOR ? "SolAppRef" : null)
            .scannedDocuments(hasScanned ? buildScannedDocs() : null)
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

    private List<CollectionMember<Document>> buildGeneratedDocs() {
        List<CollectionMember<Document>> docs = new ArrayList<>();
        docs.add(new CollectionMember<Document>(Document.builder()
            .documentType(OTHER)
            .documentFileName("OtherFileName")
            .build()));
        docs.add(new CollectionMember<Document>(Document.builder()
            .documentType(DIGITAL_GRANT)
            .documentFileName("GrantFileName")
            .build()));

        return docs;
    }

    private List<CollectionMember<ScannedDocument>> buildScannedDocs() {
        List<CollectionMember<ScannedDocument>> docs = new ArrayList<>();
        docs.add(new CollectionMember<>(ScannedDocument.builder()
            .type(OTHER.name())
            .subtype(Constants.DOC_SUBTYPE_WILL)
            .fileName("ScannedWillFileName")
            .build()));
        docs.add(new CollectionMember<>(ScannedDocument.builder()
            .type(DocumentType.EDGE_CASE.name())
            .fileName("ScannedEdgeFileName")
            .build()));
        docs.add(new CollectionMember<>(ScannedDocument.builder()
            .type(OTHER.name())
            .subtype("somthingElse")
            .fileName("ScannedOtherFileName")
            .build()));

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
    public void shouldMapAllAttributes() throws IOException {
        returnedCaseDetailsPersonal = new ReturnedCaseDetails(getCaseDataBuilder(PERSONAL, true, true, true, true,
            true).build(), LAST_MODIFIED, ID);
        returnedCaseDetailsSolicitor = new ReturnedCaseDetails(getCaseDataBuilder(SOLICITOR, true, true, false, false, 
            true).build(), LAST_MODIFIED, ID);

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
    public void shouldMapForNoScannedOrNoGrantAttributes() throws IOException {
        returnedCaseDetailsPersonal = new ReturnedCaseDetails(getCaseDataBuilder(PERSONAL, false, true, false, true, 
            true).build(), LAST_MODIFIED, ID);
        returnedCaseDetailsSolicitor = new ReturnedCaseDetails(getCaseDataBuilder(SOLICITOR, true, false, true, false, 
            true).build(), LAST_MODIFIED, ID);

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
    public void shouldMapAllAttributesWithNullDODCausingException() throws IOException {
        returnedCaseDetailsPersonal = new ReturnedCaseDetails(getCaseDataBuilder(PERSONAL, true, true, true, true,
            false).build(), LAST_MODIFIED, ID);
        returnedCaseDetailsSolicitor = new ReturnedCaseDetails(getCaseDataBuilder(SOLICITOR, true, true, false, false,
            false).build(), LAST_MODIFIED, ID);

        List<ReturnedCaseDetails> cases = new ArrayList<ReturnedCaseDetails>();
        cases.add(returnedCaseDetailsPersonal);
        cases.add(returnedCaseDetailsSolicitor);
        Map<String, String> personalisation = smeeAndFordPersonalisationService.getSmeeAndFordPersonalisation(cases,
            "fromDate", "toDate");

        assertThat(personalisation.get("smeeAndFordName"), is("Smee And Ford Data extract from fromDate to toDate"));
        String smeeAndFordRespnse = testUtils.getStringFromFile("smeeAndFordExpectedDataNullDOD.txt");

        assertThat(personalisation.get("caseData"), is(smeeAndFordRespnse));
    }
}