package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData.CaseDataBuilder;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.util.TestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class NextStepsControllerTest {

    private static final LocalDate DOB = LocalDate.of(1990, 4, 4);
    private static final LocalDate DOD = LocalDate.of(2017, 4, 4);
    private static final Long ID = 1L;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final String FORNAME = "Andy";
    private static final String SURANME = "Michael";
    private static final String SOLICITOR_APP_REFERENCE = "Reff";
    private static final String SOLICITOR_FIRM_NAME = "Legal Service Ltd";
    private static final String SOLICITOR_FIRM_POSTCODE = "SW1E 6EA";
    private static final String IHT_FORM = "IHT207";
    private static final String SOLICITOR_NAME = "Peter Crouch";
    private static final String SOLICITOR_JOB_TITLE = "Lawyer";
    private static final String PAYMENT_METHOD = "Cheque";
    private static final float PAYMENT_AMOUNT = 150;
    private static final String WILL_HAS_CODICLIS = "Yes";
    private static final String NUMBER_OF_CODICLIS = "1";
    private static final BigDecimal APPLICATION_FEE = BigDecimal.TEN;
    private static final BigDecimal FEE_FOR_UK_COPIES = BigDecimal.TEN;
    private static final BigDecimal FEE_FOR_NON_UK_COPIES = BigDecimal.TEN;
    private static final BigDecimal TOTAL_FEE = BigDecimal.TEN;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtils utils;

    private CaseDataBuilder caseDataBuilder;

    @Before
    public void setup() {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());

        caseDataBuilder = CaseData.builder()
            .deceasedDateOfBirth(DOB)
            .deceasedDateOfDeath(DOD)
            .deceasedForenames(FORNAME)
            .deceasedSurname(SURANME)
            .solsSolicitorAppReference(SOLICITOR_APP_REFERENCE)
            .willHasCodicils(WILL_HAS_CODICLIS)
            .willNumberOfCodicils(NUMBER_OF_CODICLIS)
            .solsSolicitorFirmName(SOLICITOR_FIRM_NAME)
            .solsSolicitorFirmPostcode(SOLICITOR_FIRM_POSTCODE)
            .solsIHTFormId(IHT_FORM)
            .solsSOTName(SOLICITOR_NAME)
            .solsSOTJobTitle(SOLICITOR_JOB_TITLE)
            .solsPaymentMethods(PAYMENT_METHOD)
            .applicationFee(APPLICATION_FEE)
            .feeForUkCopies(FEE_FOR_UK_COPIES)
            .feeForNonUkCopies(FEE_FOR_NON_UK_COPIES)
            .totalFee(TOTAL_FEE);
    }

    @Test
    public void shouldCallNextStepstWithNoErrors() throws Exception {
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        MvcResult mvcResult = mockMvc.perform(post("/nextsteps").content(json).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andReturn();

        System.out.print(mvcResult.getResponse().getContentAsString());

    }

    @Test
    public void shouldCallNextStepstWithDodIsNullError() throws Exception {
        caseDataBuilder.deceasedDateOfDeath(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post("/nextsteps").content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
            .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.deceasedDateOfDeath"))
            .andExpect(jsonPath("$.fieldErrors[0].code").value("NotNull"))
            .andExpect(jsonPath("$.fieldErrors[0].message").value("Date of death cannot be empty"));
    }

    @Test
    public void shouldCallNextStepstWithDobIsNullError() throws Exception {
        caseDataBuilder.deceasedDateOfBirth(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post("/nextsteps").content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
            .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.deceasedDateOfBirth"))
            .andExpect(jsonPath("$.fieldErrors[0].code").value("NotNull"))
            .andExpect(jsonPath("$.fieldErrors[0].message").value("Date of birth cannot be empty"));
    }

    @Test
    public void shouldCallNextStepstWithFornameIsNullError() throws Exception {
        caseDataBuilder.deceasedForenames(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post("/nextsteps").content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
            .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.deceasedForenames"))
            .andExpect(jsonPath("$.fieldErrors[0].code").value("NotBlank"))
            .andExpect(jsonPath("$.fieldErrors[0].message").value("Deceased forename cannot be empty"));
    }

    @Test
    public void shouldCallNextStepstWithSurnameIsNullError() throws Exception {
        caseDataBuilder.deceasedSurname(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post("/nextsteps").content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
            .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.deceasedSurname"))
            .andExpect(jsonPath("$.fieldErrors[0].code").value("NotBlank"))
            .andExpect(jsonPath("$.fieldErrors[0].message").value("Deceased surname cannot be empty"));
    }

    @Test
    public void shouldCallNextStepstWithSolicitorFirmIsNullError() throws Exception {
        caseDataBuilder.solsSolicitorFirmName(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post("/nextsteps").content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
            .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.solsSolicitorFirmName"))
            .andExpect(jsonPath("$.fieldErrors[0].code").value("NotBlank"))
            .andExpect(jsonPath("$.fieldErrors[0].message").value("Solicitor firm name cannot be empty"));
    }

    @Test
    public void shouldCallNextStepstWithSolsSolicitorFirmPostcodeIsNullError() throws Exception {
        caseDataBuilder.solsSolicitorFirmPostcode(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post("/nextsteps").content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
            .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.solsSolicitorFirmPostcode"))
            .andExpect(jsonPath("$.fieldErrors[0].code").value("NotBlank"))
            .andExpect(jsonPath("$.fieldErrors[0].message").value("Solicitor firm postcode cannot be empty"));
    }

    @Test
    public void shouldCallNextStepstWithSolicitorIHTFormIsNullError() throws Exception {
        caseDataBuilder.solsIHTFormId(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post("/nextsteps").content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
            .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.solsIHTFormId"))
            .andExpect(jsonPath("$.fieldErrors[0].code").value("NotBlank"))
            .andExpect(jsonPath("$.fieldErrors[0].message").value("Solicitor IHT Form cannot be empty"));
    }

    @Test
    public void shouldCallNextStepstWithSolsSOTNameIsNullError() throws Exception {
        caseDataBuilder.solsSOTName(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post("/nextsteps").content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
            .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.solsSOTName"))
            .andExpect(jsonPath("$.fieldErrors[0].code").value("NotBlank"))
            .andExpect(jsonPath("$.fieldErrors[0].message").value("Solicitor SOT name cannot be empty"));
    }

    @Test
    public void shouldCallNextStepstWithSolsSOTJobTitleIsNullError() throws Exception {
        caseDataBuilder.solsSOTJobTitle(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post("/nextsteps").content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
            .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.solsSOTJobTitle"))
            .andExpect(jsonPath("$.fieldErrors[0].code").value("NotBlank"))
            .andExpect(jsonPath("$.fieldErrors[0].message").value("Solicitor SOT job title cannot be empty"));
    }

    @Test
    public void shouldCallNextStepstWithPaymentMethodIsNullError() throws Exception {
        caseDataBuilder.solsPaymentMethods(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post("/nextsteps").content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
            .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.solsPaymentMethods"))
            .andExpect(jsonPath("$.fieldErrors[0].code").value("NotNull"))
            .andExpect(jsonPath("$.fieldErrors[0].message")
                .value("Payment method cannot be empty. It must be one of fee account or cheque"));
    }

    @Test
    public void shouldCallNextStepstWithApplicationFeeIsNullError() throws Exception {
        caseDataBuilder.applicationFee(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post("/nextsteps").content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
            .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.applicationFee"))
            .andExpect(jsonPath("$.fieldErrors[0].code").value("NotNull"))
            .andExpect(jsonPath("$.fieldErrors[0].message")
                .value("Application Fee cannot be null"));
    }

    @Test
    public void shouldCallNextStepstWithApplicationFeeIsNegativeError() throws Exception {
        caseDataBuilder.applicationFee(BigDecimal.valueOf(-1));
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post("/nextsteps").content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
            .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.applicationFee"))
            .andExpect(jsonPath("$.fieldErrors[0].code").value("DecimalMin"))
            .andExpect(jsonPath("$.fieldErrors[0].message")
                .value("Application fee amount cannot be negative"));
    }

    @Test
    public void shouldCallNextStepstWithFeeForUkCopiesIsNullError() throws Exception {
        caseDataBuilder.feeForUkCopies(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post("/nextsteps").content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
            .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.feeForUkCopies"))
            .andExpect(jsonPath("$.fieldErrors[0].code").value("NotNull"))
            .andExpect(jsonPath("$.fieldErrors[0].message")
                .value("Fee for UK Copies cannot be null"));
    }

    @Test
    public void shouldCallNextStepstWithFeeForUkCopiesIsNegativeError() throws Exception {
        caseDataBuilder.feeForUkCopies(BigDecimal.valueOf(-1));
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post("/nextsteps").content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
            .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.feeForUkCopies"))
            .andExpect(jsonPath("$.fieldErrors[0].code").value("DecimalMin"))
            .andExpect(jsonPath("$.fieldErrors[0].message")
                .value("Fee for UK copies cannot be negative"));
    }

    @Test
    public void shouldCallNextStepstWithFeeForNonUkCopiesIsNullError() throws Exception {
        caseDataBuilder.feeForNonUkCopies(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post("/nextsteps").content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
            .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.feeForNonUkCopies"))
            .andExpect(jsonPath("$.fieldErrors[0].code").value("NotNull"))
            .andExpect(jsonPath("$.fieldErrors[0].message")
                .value("Fee for non UK Copies cannot be null"));
    }

    @Test
    public void shouldCallNextStepstWithFeeForNonUkCopiesIsNegativeError() throws Exception {
        caseDataBuilder.feeForNonUkCopies(BigDecimal.valueOf(-1));
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post("/nextsteps").content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
            .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.feeForNonUkCopies"))
            .andExpect(jsonPath("$.fieldErrors[0].code").value("DecimalMin"))
            .andExpect(jsonPath("$.fieldErrors[0].message")
                .value("Fee for non UK copies cannot be negative"));
    }

    @Test
    public void shouldCallNextStepstWithTotalFeeIsNullError() throws Exception {
        caseDataBuilder.totalFee(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post("/nextsteps").content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
            .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.totalFee"))
            .andExpect(jsonPath("$.fieldErrors[0].code").value("NotNull"))
            .andExpect(jsonPath("$.fieldErrors[0].message")
                .value("Fee payment amount cannot be null"));
    }

    @Test
    public void shouldCallNextStepstWithTotalFeeIsNegativeError() throws Exception {
        caseDataBuilder.totalFee(BigDecimal.valueOf(-1));
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post("/nextsteps").content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
            .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.totalFee"))
            .andExpect(jsonPath("$.fieldErrors[0].code").value("DecimalMin"))
            .andExpect(jsonPath("$.fieldErrors[0].message")
                .value("Total fee amount cannot be negative"));
    }
}
