package uk.gov.hmcts.probate.service.notification;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
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
import uk.gov.hmcts.probate.util.TestUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.DocumentType.GRANT_RAISED;
import static uk.gov.hmcts.probate.model.DocumentType.OTHER;

public class SmeeAndFordPersonalisationServiceTest {

    @InjectMocks
    private SmeeAndFordPersonalisationService smeeAndFordPersonalisationService;
    
    private ReturnedCaseDetails returnedCaseDetailsPersonal;
    private ReturnedCaseDetails returnedCaseDetailsSolicitor;

    private static final DateTimeFormatter DATA_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter CONTENT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Long ID = 1L;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final BigDecimal GROSS = BigDecimal.valueOf(1000000);
    private static final BigDecimal NET = BigDecimal.valueOf(900000);

    private TestUtils testUtils = new TestUtils();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        returnedCaseDetailsPersonal = buildAll(PERSONAL);
        returnedCaseDetailsSolicitor = buildAll(SOLICITOR);
        //when(responseEntity.getBody()).thenReturn(fee);
    }

    private ReturnedCaseDetails buildAll(ApplicationType applicationType) {
        List<CollectionMember<ProbateAliasName>> deceasedAliases = new ArrayList();
        deceasedAliases.add(new CollectionMember<ProbateAliasName>(ProbateAliasName.builder()
            .forenames("AliasForename1")
            .lastName("AliasLastName1")
            .build()));
        SolsAddress deceasedAddress = buildAddress("Dec");
        List<CollectionMember<AdditionalExecutorApplying>> additionalExecsApplying = new ArrayList();
        SolsAddress execAddress = buildAddress("Exec1");
        additionalExecsApplying.add(
            new CollectionMember<AdditionalExecutorApplying>(AdditionalExecutorApplying.builder()
            .applyingExecutorFirstName("Exec1FN")
            .applyingExecutorLastName("Exec1LN")
            .applyingExecutorOtherNames("Exec1ON")
            .applyingExecutorAddress(execAddress)
            .applyingExecutorEmail("exec1@probate-test.com")
            .applyingExecutorPhoneNumber("0711111111")
            .build()));
        SolsAddress primaryAddress = buildAddress("Prim");
        ReturnedCaseDetails returnedCaseDetails = new ReturnedCaseDetails(CaseData.builder()
            .registryLocation("Registry Address")
            .grantIssuedDate("2021-12-31")
            .deceasedForenames("Jack")
            .deceasedSurname("Michelson")
            .boDeceasedHonours("OBE")
            .deceasedAliasNameList(deceasedAliases)
            .caseType("gop")
            .applicationType(applicationType)
            .deceasedDateOfDeath(LocalDate.of(2020, 12, 31))
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
            .scannedDocuments(buildScannedDocs())
            .registryLocation("Cardiff")
            .willHasCodicils(Constants.YES)
            .probateDocumentsGenerated(buildGeneratedDocs())
            .build(), LAST_MODIFIED, ID);
        
        return returnedCaseDetails;
    }

    private List<CollectionMember<Document>> buildGeneratedDocs() {
        List<CollectionMember<Document>> docs = new ArrayList<>();
        docs.add(new CollectionMember<Document>(Document.builder()
            .documentType(OTHER)
            .documentFileName("OtherFileName")
            .build()));
        docs.add(new CollectionMember<Document>(Document.builder()
            .documentType(GRANT_RAISED)
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
        
        return  docs;
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
        List<ReturnedCaseDetails> cases = new ArrayList<ReturnedCaseDetails>();
        cases.add(returnedCaseDetailsPersonal);
        cases.add(returnedCaseDetailsSolicitor);
        Map<String, String> personalisation = smeeAndFordPersonalisationService.getSmeeAndFordPersonalisation(cases);

        assertThat(personalisation.get("smeeAndFordName"), is(LocalDateTime.now().format(DATA_DATE) + "sf"));
        String smeeAndFordRespnse = testUtils.getStringFromFile("smeeAndFordExpectedData.txt");

        assertThat(personalisation.get("caseData"), is(smeeAndFordRespnse));
    }
}