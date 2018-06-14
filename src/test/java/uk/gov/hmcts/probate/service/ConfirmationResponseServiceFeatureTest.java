package uk.gov.hmcts.probate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.config.ApplicationConfiguration;
import uk.gov.hmcts.probate.config.EvidenceManagementRestTemplate;
import uk.gov.hmcts.probate.config.FeeServiceConfiguration;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.Deceased;
import uk.gov.hmcts.probate.model.ccd.Executor;
import uk.gov.hmcts.probate.model.ccd.Fee;
import uk.gov.hmcts.probate.model.ccd.InheritanceTax;
import uk.gov.hmcts.probate.model.ccd.Solicitor;
import uk.gov.hmcts.probate.model.ccd.raw.response.AfterSubmitCallbackResponse;
import uk.gov.hmcts.probate.util.TestUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@Ignore // I'm fixing these tests
@RunWith(SpringRunner.class)
@ContextConfiguration
@TestPropertySource(properties = {"markdown.templatesDirectory=templates/markdown/"})
public class ConfirmationResponseServiceFeatureTest {

    private final TestUtils testUtils = new TestUtils();
    private static final String REASON_FOR_NOT_APPLYING_RENUNCIATION = "Renunciation";
    private static final String REASON_FOR_NOT_APPLYING_DIED_BEFORE = "DiedBefore";
    private static final String SOLICITOR_REFERENCE = "SOL_REF_X12345";

    private static final LocalDate DOB = LocalDate.of(1990, 4, 4);
    private static final LocalDate DOD = LocalDate.of(2017, 4, 4);
    private static final String FORENAME = "Andy";
    private static final String SURNAME = "Michael";
    private static final String SOLICITOR_FIRM_NAME = "Legal Service Ltd";
    private static final String SOLICITOR_FIRM_POSTCODE = "SW1E 6EA";
    private static final String IHT_FORM = "IHT207";
    private static final String SOLICITOR_NAME = "Peter Crouch";
    private static final String SOLICITOR_JOB_TITLE = "Lawyer";
    private static final String PAYMENT_METHOD = "Cheque";
    private static final BigDecimal APPLICATION_FEE = BigDecimal.TEN;
    private static final BigDecimal TOTAL_FEE = BigDecimal.TEN;
    private static final BigDecimal FEE_UK = new BigDecimal(100);
    private static final BigDecimal FEE_NON_UK = new BigDecimal(200);
    private static final Float NET = 900f;
    private static final Float GROSS = 1000f;
    private static final Long EXTRA_UK = 1L;
    private static final Long EXTRA_OUTSIDE_UK = 2L;
    private static final String PAYMENT_REFERENCE = "XXXXX123456";
    private static final String ADDITIONAL_INFO = "ADDITIONAL INFO";

    @Autowired
    private ConfirmationResponseService confirmationResponseService;

    @Test
    public void shouldGenerateCorrectConfirmationBodyWithNoAdditionalOptions() throws Exception {
        CCDData ccdData = createCCDataBuilder().build();
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData);

        String expectedConfirmationBody = testUtils.getStringFromFile("expectedConfirmationBody.md");

        assertThat(stopConfirmation.getConfirmationBody(), is(expectedConfirmationBody));
    }

    @Test
    public void shouldGenerateCorrectConfirmationBodyWithRenouncingExecutor() throws Exception {
        Executor renouncingExecutor = createRenouncingExecutor("Tim", "Smith");
        CCDData ccdData = createCCDataBuilder().executors(Collections.singletonList(renouncingExecutor)).build();
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData);

        String expectedConfirmationBody = testUtils.getStringFromFile("expectedConfirmationBodyWithRenouncingExecutor.md");

        assertThat(stopConfirmation.getConfirmationBody(), is(expectedConfirmationBody));
    }

    @Test
    public void shouldGenerateCorrectConfirmationBodyWithMultipleRenouncingExecutors() throws Exception {
        Executor renouncingExecutor = createRenouncingExecutor("Tim", "Smith");
        Executor renouncingExecutor2 = createRenouncingExecutor("John", "Smith");
        CCDData ccdData = createCCDataBuilder().executors(Arrays.asList(renouncingExecutor, renouncingExecutor2)).build();
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData);

        String expectedConfirmationBody = testUtils.getStringFromFile("expectedConfirmationBodyWithMultipleRenouncingExecutors.md");

        assertThat(stopConfirmation.getConfirmationBody(), is(expectedConfirmationBody));
    }

    @Test
    public void shouldGenerateCorrectConfirmationBodyWithDeadExecutor() throws Exception {
        Executor deadExecutor = createDeadExecutor("Bob", "Martin");
        CCDData ccdData = createCCDataBuilder().executors(Collections.singletonList(deadExecutor)).build();
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData);

        String expectedConfirmationBody = testUtils.getStringFromFile("expectedConfirmationBodyWithDeadExecutor.md");

        assertThat(stopConfirmation.getConfirmationBody(), is(expectedConfirmationBody));
    }

    @Test
    public void shouldGenerateCorrectConfirmationBodyWithMultipleDeadExecutors() throws Exception {
        Executor deadExecutor = createDeadExecutor("Bob", "Martin");
        Executor deadExecutor2 = createDeadExecutor("John", "Martin");
        CCDData ccdData = createCCDataBuilder().executors(Arrays.asList(deadExecutor, deadExecutor2)).build();
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData);

        String expectedConfirmationBody = testUtils.getStringFromFile("expectedConfirmationBodyWithMultipleDeadExecutors.md");

        assertThat(stopConfirmation.getConfirmationBody(), is(expectedConfirmationBody));
    }

    @Test
    public void shouldGenerateCorrectConfirmationBodyWithIHT400421() throws Exception {
        CCDData ccdData = createCCDataBuilder().iht(createInheritanceTax("IHT400421")).build();
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData);

        String expectedConfirmationBody = testUtils.getStringFromFile("expectedConfirmationBodyWithIHT400421.md");

        assertThat(stopConfirmation.getConfirmationBody(), is(expectedConfirmationBody));
    }

    @Test
    public void shouldGenerateCorrectConfirmationBodyWithAllCombinationsForAdditionalOptions() throws Exception {
        Executor renouncingExecutor = createRenouncingExecutor("Tim", "Smith");
        Executor deadExecutor = createDeadExecutor("Bob", "Martin");
        CCDData ccdData = createCCDataBuilder()
                .executors(Arrays.asList(renouncingExecutor, deadExecutor))
                .iht(createInheritanceTax("IHT400421")).build();
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData);

        String expectedConfirmationBody = testUtils.getStringFromFile("expectedConfirmationBodyWithAllCombinations.md");

        assertThat(stopConfirmation.getConfirmationBody(), is(expectedConfirmationBody));
    }

    @Test
    public void shouldGenerateCorrectConfirmationBodyWithAllCombinationsForAdditionalOptionsAndMultiples() throws Exception {
        Executor renouncingExecutor = createRenouncingExecutor("Tim", "Smith");
        Executor renouncingExecutor2 = createRenouncingExecutor("John", "Smith");
        Executor deadExecutor = createDeadExecutor("Bob", "Martin");
        Executor deadExecutor2 = createDeadExecutor("John", "Martin");
        CCDData ccdData = createCCDataBuilder()
                .executors(Arrays.asList(renouncingExecutor, renouncingExecutor2, deadExecutor, deadExecutor2))
                .iht(createInheritanceTax("IHT400421")).build();
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData);

        String expectedConfirmationBody = testUtils.getStringFromFile("expectedConfirmationBodyWithAllCombinationsAndMultiples.md");

        assertThat(stopConfirmation.getConfirmationBody(), is(expectedConfirmationBody));
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
                .solsAdditionalInfo(ADDITIONAL_INFO);
    }

    private Fee createFee() {
        return Fee.builder()
                .extraCopiesOfGrant(EXTRA_UK)
                .outsideUKGrantCopies(EXTRA_OUTSIDE_UK)
                .paymentMethod(PAYMENT_METHOD)
                .amount(TOTAL_FEE)
                .applicationFee(APPLICATION_FEE)
                .paymentReferenceNumber(PAYMENT_REFERENCE)
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
        return Solicitor.builder()
                .firmName(SOLICITOR_FIRM_NAME)
                .firmPostcode(SOLICITOR_FIRM_POSTCODE)
                .fullname(SOLICITOR_NAME)
                .jobRole(SOLICITOR_JOB_TITLE)
                .build();
    }

    private Executor createDeadExecutor(String forename, String lastname) {
        return Executor.builder()
                .forename(forename)
                .lastname(lastname)
                .reasonNotApplying(REASON_FOR_NOT_APPLYING_DIED_BEFORE)
                .build();
    }


    private Executor createRenouncingExecutor(String forename, String lastname) {
        return Executor.builder()
                .forename(forename)
                .lastname(lastname)
                .reasonNotApplying(REASON_FOR_NOT_APPLYING_RENUNCIATION)
                .build();
    }

    @TestConfiguration
    @ComponentScan(basePackages = {"uk.gov.hmcts.probate.changerule",
            "uk.gov.hmcts.probate.validator",
            "uk.gov.hmcts.probate.service"}
    )
    @Import({ApplicationConfiguration.class})
    public static class Config {

        @MockBean
        HttpServletRequest httpServletRequest;

        @MockBean
        EvidenceManagementRestTemplate evidenceManagementRestTemplate;

        @MockBean
        FeeServiceConfiguration feeServiceConfiguration;

        @MockBean
        PDFServiceConfiguration pdfServiceConfiguration;

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
}
