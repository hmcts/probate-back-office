package uk.gov.hmcts.probate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration
@TestPropertySource(properties = {"markdown.templatesDirectory=templates/markdown/"})
public class ConfirmationResponseServiceFeatureTest {

    private final TestUtils testUtils = new TestUtils();
    private static final String REASON_FOR_NOT_APPLYING_RENUNCIATION = "Renunciation";
    private static final String REASON_FOR_NOT_APPLYING_DIED_BEFORE = "DiedBefore";
    public static final String SOLICITOR_REFERENCE = "SOL_REF_X12345";

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
    private static final Float NET = 900f;
    private static final Float GROSS = 1000f;
    private static final Long EXTRA_UK = 1L;
    private static final Long EXTRA_OUTSIDE_UK = 2L;
    private static final String PAYMENT_REFERENCE = "XXXXX123456";
    public static final String ADDITIONAL_INFO = "ADDITIONAL INFO";

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
        Executor renouncingExecutor = Executor.builder()
                .forename("Tim")
                .lastname("Smith")
                .reasonNotApplying(REASON_FOR_NOT_APPLYING_RENUNCIATION)
                .build();
        CCDData ccdData = createCCDataBuilder().executors(Lists.newArrayList(renouncingExecutor)).build();
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData);

        String expectedConfirmationBody = testUtils.getStringFromFile("expectedConfirmationBodyWithRenouncingExecutor.md");

        assertThat(stopConfirmation.getConfirmationBody(), is(expectedConfirmationBody));
    }

    @Test
    public void shouldGenerateCorrectConfirmationBodyWithDeadExecutor() throws Exception {
        Executor deadExecutor = Executor.builder()
                .forename("Bob")
                .lastname("Martin")
                .reasonNotApplying(REASON_FOR_NOT_APPLYING_DIED_BEFORE)
                .build();
        CCDData ccdData = createCCDataBuilder().executors(Lists.newArrayList(deadExecutor)).build();
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData);

        String expectedConfirmationBody = testUtils.getStringFromFile("expectedConfirmationBodyWithDeadExecutor.md");

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
        Executor renouncingExecutor = Executor.builder()
                .forename("Tim")
                .lastname("Smith")
                .reasonNotApplying(REASON_FOR_NOT_APPLYING_RENUNCIATION)
                .build();
        Executor deadExecutor = Executor.builder()
                .forename("Bob")
                .lastname("Martin")
                .reasonNotApplying(REASON_FOR_NOT_APPLYING_DIED_BEFORE)
                .build();
        CCDData ccdData = createCCDataBuilder()
                .executors(Lists.newArrayList(renouncingExecutor, deadExecutor))
                .iht(createInheritanceTax("IHT400421")).build();
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData);

        String expectedConfirmationBody = testUtils.getStringFromFile("expectedConfirmationBodyWithAllCombinations.md");

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
                .executors(Lists.newArrayList())
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

    @Configuration
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
