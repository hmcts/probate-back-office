package uk.gov.hmcts.probate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import uk.gov.hmcts.probate.model.ccd.*;
import uk.gov.hmcts.probate.model.ccd.raw.response.AfterSubmitCallbackResponse;
import uk.gov.hmcts.probate.util.TestUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration
@TestPropertySource(properties = {"markdown.templatesDirectory=templates/markdown/"})
public class ConfirmationResponseServiceFeatureTest {

    public static final String SOLICITOR_REFERENCE = "SOL_REF_X12345";

    @Autowired
    private ConfirmationResponseService confirmationResponseService;

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

    private CCDData createCCData() {
        Solicitor solicitor = Solicitor.builder()
                .firmName(SOLICITOR_FIRM_NAME)
                .firmPostcode(SOLICITOR_FIRM_POSTCODE)
                .fullname(SOLICITOR_NAME)
                .jobRole(SOLICITOR_JOB_TITLE)
                .build();

        Deceased deceased = Deceased.builder()
                .firstname(FORENAME)
                .lastname(SURNAME)
                .dateOfBirth(DOB)
                .dateOfDeath(DOD)
                .build();

        InheritanceTax inheritanceTax = InheritanceTax.builder()
                .formName(IHT_FORM)
                .netValue(NET)
                .grossValue(GROSS)
                .build();

        Fee fee = Fee.builder()
                .extraCopiesOfGrant(EXTRA_UK)
                .outsideUKGrantCopies(EXTRA_OUTSIDE_UK)
                .paymentMethod(PAYMENT_METHOD)
                .amount(TOTAL_FEE)
                .applicationFee(APPLICATION_FEE)
                .paymentReferenceNumber(PAYMENT_REFERENCE)
                .build();

        return CCDData.builder()
                .solicitorReference(SOLICITOR_REFERENCE)
                .caseSubmissionDate(LocalDate.of(2018, 1, 1))
                .solicitor(solicitor)
                .deceased(deceased)
                .iht(inheritanceTax)
                .fee(fee)
                .solsAdditionalInfo("ADDITIONAL INFO")
                .executors(new ArrayList<>())
                .build();
    }

    @Test
    public void shouldGenerateCorrectConfirmationBody() throws Exception {
        CCDData ccdData = createCCData();
        AfterSubmitCallbackResponse stopConfirmation = confirmationResponseService.getNextStepsConfirmation(ccdData);
        TestUtils testUtils = new TestUtils();
        String expectedConfirmationBody = testUtils.getStringFromFile("expectedConfirmationBody.md");
        assertThat(stopConfirmation.getConfirmationBody(), is(expectedConfirmationBody));
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
