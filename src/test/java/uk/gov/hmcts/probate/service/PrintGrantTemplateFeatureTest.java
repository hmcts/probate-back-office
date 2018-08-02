package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.controller.DocumentController;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.Executor;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.util.TestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"markdown.templatesDirectory=templates/markdown/"})
public class PrintGrantTemplateFeatureTest {

    private final TestUtils testUtils = new TestUtils();

    private static final String DIGITAL_GRANT_DRAFT = "digitalGrantDraft";

    private static final String REASON_FOR_NOT_APPLYING_POWER_RESERVED = "PowerReserved";
    private static final String REASON_FOR_NOT_APPLYING_DIED_BEFORE = "DiedBefore";

    private static final String SOLICITOR_REFERENCE = "sols app Ref";
    private static final String SOLICITOR_FIRM_NAME = "Solicitor firm name";
    private static final String SOLICITOR_FIRM_POSTCODE = "SW17 9PE";

    private static final LocalDate DOD = LocalDate.of(2017, 4, 4);
    private static final String DECEASED_FIRSTNAME = "Andy";
    private static final String DECEASED_LASTNAME = "Michael";
    private static final String DECEASED_HONOURS = "OBE";
    private static final String DECEASED_TITLE = "Lord";
    private static final String ALIAS_NAME = "John Doe";
    private static final String ALIAS_NAME_SCEOND = "John Smith";

    private static final String APPLICANT_FORENAME = "Fred";
    private static final String APPLICANT_SURNAME = "Bloggs";

    private static final BigDecimal NET = BigDecimal.valueOf(2000);
    private static final BigDecimal GROSS = BigDecimal.valueOf(4000);

    private static final String REGISTRY_LOCATION = "Oxford";

    private static final String CASE_ID = "ABC-123-DEF-456";

    private static final String WILL_MESSAGE = "Will Message";
    private static final String EXECUTOR_LIMITATION = "Executor limitation message should appear here";
    private static final String ADMIN_CLAUSE_LIMITATION = "Admin clause message should appear here";
    private static final String LIMITATION_TEXT = "Limitation should text appears here";

    @Autowired
    private DocumentController DocumentController;

    @MockBean
    private AppInsights appInsights;

    private CaseData.CaseDataBuilder caseDataBuilder;

    @Before
    public void setup() {

        caseDataBuilder = CaseData.builder()
                .solsSolicitorFirmName(SOLICITOR_FIRM_NAME)
                .solsSolicitorFirmPostcode(SOLICITOR_FIRM_POSTCODE)
                .solsSolicitorAppReference(SOLICITOR_REFERENCE)

                .deceasedForenames(DECEASED_FIRSTNAME)
                .deceasedSurname(DECEASED_LASTNAME)
                .deceasedAddress(createAddress())
                .deceasedDateOfDeath(DOD)
                .boDeceasedTitle(DECEASED_TITLE)
                .boDeceasedHonours(DECEASED_HONOURS)

                .ihtGrossValue(GROSS)
                .ihtNetValue(NET)

                .primaryApplicantForenames(APPLICANT_FORENAME)
                .primaryApplicantSurname(APPLICANT_SURNAME)
                .primaryApplicantAddress(createAddress())

                .registryLocation(REGISTRY_LOCATION);

    }

    @Ignore
    @Test
    public void shouldGenerateCorrectTemplateSingleExecutorSols() throws Exception {
        CaseData caseData = caseDataBuilder
                .applicationType(ApplicationType.SOLICITOR)
                .build();


        String expectedPrintTemplate = testUtils.getStringFromFile("PrintTemplateSingleExecutorSOLs.md");

    }

    @Ignore
    @Test
    public void shouldGenerateCorrectTemplateSingleExecutorPA() throws Exception {
        CaseData caseData = caseDataBuilder
                .applicationType(ApplicationType.PERSONAL)
                .build();

        String expectedPrintTemplate = testUtils.getStringFromFile("PrintTemplateSingleExecutorPA.md");


    }

    @Ignore
    @Test
    public void shouldGenerateCorrectTemplateMultipleExecutorsSOls() throws Exception {
        CaseData caseData = caseDataBuilder
                .applicationType(ApplicationType.SOLICITOR)
                .build();

        String expectedPrintTemplate = testUtils.getStringFromFile("PrintTemplateWithMultipleExecutorSOLs.md");

    }

    @Ignore
    @Test
    public void shouldGenerateCorrectTemplateWithGrantInfoSOls() throws Exception {
        CaseData caseData = caseDataBuilder
                .applicationType(ApplicationType.SOLICITOR)
                .boWillMessage(WILL_MESSAGE)
                .boExecutorLimitation(EXECUTOR_LIMITATION)
                .boAdminClauseLimitation(ADMIN_CLAUSE_LIMITATION)
                .boLimitationText(LIMITATION_TEXT)
                .build();


        String expectedPrintTemplate = testUtils.getStringFromFile("PrintTemplateWithGrantInfoSOLs.md");


    }

    @Ignore
    @Test
    public void shouldGenerateCorrectTemplateWithPowerReservedMultipleSOls() throws Exception {
        CaseData caseData = caseDataBuilder
                .applicationType(ApplicationType.SOLICITOR)
                .build();


        String expectedPrintTemplate = testUtils.getStringFromFile("PrintTemplateWithPowerReservedMultipleSOLs.md");


    }

    @Ignore
    @Test
    public void shouldGenerateCorrectTemplateWithPowerReservedSingleSOls() throws Exception {

        CaseData caseData = caseDataBuilder
                .applicationType(ApplicationType.SOLICITOR)
                .build();


        String expectedPrintTemplate = testUtils.getStringFromFile("PrintTemplateWithPowerReservedSingleSOLs.md");

    }

    private Executor createNotApplyingExecutors() {
        return Executor.builder()
                .forename("Sarah")
                .lastname("Bloggs")
                .reasonNotApplying(REASON_FOR_NOT_APPLYING_DIED_BEFORE)
                .forename("Jane")
                .lastname("Doe")
                .reasonNotApplying(REASON_FOR_NOT_APPLYING_POWER_RESERVED)
                .forename("Hannah")
                .lastname("Doe")
                .reasonNotApplying(REASON_FOR_NOT_APPLYING_POWER_RESERVED)
                .build();
    }

    private Executor createApplyingExecutors() {
        return Executor.builder()
                .forename("Sarah")
                .lastname("Bloggs")
                .address(createAddress())
                .forename("Jane")
                .lastname("Doe")
                .address(createAddress())
                .build();
    }

    private SolsAddress createAddress() {
        return SolsAddress.builder()
                .addressLine1("123 Street name")
                .addressLine2(null)
                .addressLine3(null)
                .postTown("PostTown")
                .county("Surrey")
                .postCode("SW18 9PE")
                .country("United Kingdom")
                .build();
    }
}
