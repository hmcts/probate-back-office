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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.probate.config.SecurityConfiguration;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData.CaseDataBuilder;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {SecurityConfiguration.class, ControllerConfiguration.class})
public class BusinessValidationControllerTest {

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
    private static final Float NET = 900f;
    private static final Float GROSS = 1000f;
    private static final Long EXTRA_UK = 1L;
    private static final Long EXTRA_OUTSIDE_UK = 2L;
    private static final String DEC_ADD_LINE1 = "DecLine1";
    private static final String DEC_ADD_PC = "DecPC";
    private static final SolsAddress DECEASED_ADDRESS = SolsAddress.builder().addressLine1(DEC_ADD_LINE1).postCode(DEC_ADD_PC).build();
    private static final String EX_ADD_LINE1 = "ExLine1";
    private static final String EX_ADD_PC = "ExPC";
    private static final SolsAddress PRIMARY_ADDRESS = SolsAddress.builder().addressLine1(EX_ADD_LINE1).postCode(EX_ADD_PC).build();
    private static final String PRIMARY_APPLICANT_APPLYING = "Yes";
    private static final String PRIMARY_APPLICANT_HAS_ALIAS = "No";
    private static final String OTHER_EXEC_EXISTS = "No";
    private static final String WILL_EXISTS = "Yes";
    private static final String WILL_ACCESS_ORIGINAL = "Yes";
    private static final String PRIMARY_FORENAMES = "ExFN";
    private static final String PRIMARY_SURNAME = "ExSN";
    private static final String DECEASED_OTHER_NAMES = "No";
    private static final String DECEASED_DOM_UK = "Yes";


    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final String CASE_VALIDATE_URL = "/case/validate";

    @Autowired
    private MockMvc mockMvc;

    private CaseDataBuilder caseDataBuilder;

    @Before
    public void setup() {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());

        caseDataBuilder = CaseData.builder()
                .deceasedDateOfBirth(DOB)
                .deceasedDateOfDeath(DOD)
                .deceasedForenames(FORNAME)
                .deceasedSurname(SURANME)
                .deceasedAddress(DECEASED_ADDRESS)
                .deceasedAnyOtherNames(DECEASED_OTHER_NAMES)
                .deceasedDomicileInEngWales(DECEASED_DOM_UK)
                .primaryApplicantForenames(PRIMARY_FORENAMES)
                .primaryApplicantSurname(PRIMARY_SURNAME)
                .primaryApplicantAddress(PRIMARY_ADDRESS)
                .primaryApplicantIsApplying(PRIMARY_APPLICANT_APPLYING)
                .primaryApplicantHasAlias(PRIMARY_APPLICANT_HAS_ALIAS)
                .otherExecutorExists(OTHER_EXEC_EXISTS)
                .willExists(WILL_EXISTS)
                .willAccessOriginal(WILL_ACCESS_ORIGINAL)
                .ihtNetValue(NET)
                .ihtGrossValue(GROSS)
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
                .extraCopiesOfGrant(EXTRA_UK)
                .outsideUKGrantCopies(EXTRA_OUTSIDE_UK)
                .totalFee(TOTAL_FEE);
    }

    @Test
    public void shouldValidateWithDodIsNullError() throws Exception {
        caseDataBuilder.deceasedDateOfDeath(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(CASE_VALIDATE_URL).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.deceasedDateOfDeath"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotNull"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("Date of death cannot be empty"));
    }

    @Test
    public void shouldValidateDobIsNullError() throws Exception {
        caseDataBuilder.deceasedDateOfBirth(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(CASE_VALIDATE_URL).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.deceasedDateOfBirth"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotNull"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("Date of birth cannot be empty"));
    }

    @Test
    public void shouldValidateWithFornameIsNullError() throws Exception {
        caseDataBuilder.deceasedForenames(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(CASE_VALIDATE_URL).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.deceasedForenames"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotBlank"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("Deceased forename cannot be empty"));
    }

    @Test
    public void shouldValidateWithSurnameIsNullError() throws Exception {
        caseDataBuilder.deceasedSurname(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(CASE_VALIDATE_URL).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.deceasedSurname"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotBlank"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("Deceased surname cannot be empty"));
    }

    @Test
    public void shouldValidateWithSolicitorIHTFormIsNullError() throws Exception {
        caseDataBuilder.solsIHTFormId(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(CASE_VALIDATE_URL).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.solsIHTFormId"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotBlank"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("Solicitor IHT Form cannot be empty"));
    }
}
