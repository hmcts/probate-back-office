package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData.CaseDataBuilder;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
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
public class BusinessValidationControllerTest {

    private static final LocalDate DOB = LocalDate.of(1990, 4, 4);
    private static final LocalDate DOD = LocalDate.of(2017, 4, 4);
    private static final Long ID = 1L;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final String FORENAME = "Andy";
    private static final String SURNAME = "Michael";
    private static final String SOLICITOR_APP_REFERENCE = "Reference";
    private static final String SOLICITOR_FIRM_NAME = "Legal Service Ltd";
    private static final String SOLICITOR_FIRM_POSTCODE = "SW1E 6EA";
    private static final String IHT_FORM = "IHT207";
    private static final String SOLICITOR_NAME = "Peter Crouch";
    private static final String SOLICITOR_JOB_TITLE = "Lawyer";
    private static final String PAYMENT_METHOD = "Cheque";
    private static final String WILL_HAS_CODICILS = "Yes";
    private static final String NUMBER_OF_CODICILS = "1";
    private static final BigDecimal APPLICATION_FEE = BigDecimal.TEN;
    private static final BigDecimal FEE_FOR_UK_COPIES = BigDecimal.TEN;
    private static final BigDecimal FEE_FOR_NON_UK_COPIES = BigDecimal.TEN;
    private static final BigDecimal TOTAL_FEE = BigDecimal.TEN;
    private static final BigDecimal NET = new BigDecimal("77777777");
    private static final BigDecimal GROSS = new BigDecimal("999999999");
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
    private static final String ANSWER_NO = "No";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String CASE_VALIDATE_URL = "/case/validate";
    private static final String CASE_VALIDATE_CASE_DETAILS_URL = "/case/validateCaseDetails";
    private static final String CASE_TRANSFORM_URL = "/case/transformCase";
    private static final String CASE_CHCEKLIST_URL = "/case/validateCheckListDetails";

    @Autowired
    private MockMvc mockMvc;

    private CaseDataBuilder caseDataBuilder;

    private final TestUtils testUtils = new TestUtils();

    @MockBean
    private AppInsights appInsights;

    @MockBean
    private PDFManagementService pdfManagementService;

    @Before
    public void setup() {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());

        caseDataBuilder = CaseData.builder()
                .deceasedDateOfBirth(DOB)
                .deceasedDateOfDeath(DOD)
                .deceasedForenames(FORENAME)
                .deceasedSurname(SURNAME)
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
                .willHasCodicils(WILL_HAS_CODICILS)
                .willNumberOfCodicils(NUMBER_OF_CODICILS)
                .solsSolicitorFirmName(SOLICITOR_FIRM_NAME)
                .solsSolicitorFirmPostcode(SOLICITOR_FIRM_POSTCODE)
                .ihtFormId(IHT_FORM)
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
        validateDodIsNullError(CASE_VALIDATE_URL);
    }

    @Test
    public void shouldValidateDobIsNullError() throws Exception {
        validateDobIsNullError(CASE_VALIDATE_URL);
    }

    @Test
    public void shouldValidateWithForenameIsNullError() throws Exception {
        validateForenameIsNullError(CASE_VALIDATE_URL);
    }

    @Test
    public void shouldValidateWithSurnameIsNullError() throws Exception {
        validateSurnameIsNullError(CASE_VALIDATE_URL);
    }

    @Test
    public void shouldValidateWithSolicitorIHTFormIsNullError() throws Exception {
        caseDataBuilder.ihtFormId(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(CASE_VALIDATE_URL).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.ihtFormId"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotBlank"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("Solicitor IHT Form cannot be empty"));
    }

    @Test
    public void shouldValidateWithDodIsNullErrorForCaseDetails() throws Exception {
        validateDodIsNullError(CASE_VALIDATE_CASE_DETAILS_URL);
    }

    @Test
    public void shouldValidateDobIsNullErrorForCaseDetails() throws Exception {
        validateDobIsNullError(CASE_VALIDATE_CASE_DETAILS_URL);
    }

    @Test
    public void shouldValidateWithDeceasedForenameIsNullErrorForCaseDetails() throws Exception {
        validateForenameIsNullError(CASE_VALIDATE_CASE_DETAILS_URL);
    }

    @Test
    public void shouldValidateWithDeceasedSurnameIsNullErrorForCaseDetails() throws Exception {
        validateSurnameIsNullError(CASE_VALIDATE_CASE_DETAILS_URL);
    }

    private void validateDodIsNullError(String url) throws Exception {
        caseDataBuilder.deceasedDateOfDeath(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(url).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.deceasedDateOfDeath"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotNull"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("Date of death cannot be empty"));
    }

    private void validateDobIsNullError(String url) throws Exception {
        caseDataBuilder.deceasedDateOfBirth(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(url).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.deceasedDateOfBirth"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotNull"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("Date of birth cannot be empty"));
    }

    private void validateForenameIsNullError(String url) throws Exception {
        caseDataBuilder.deceasedForenames(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(url).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.deceasedForenames"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotBlank"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("Deceased forename cannot be empty"));
    }

    private void validateSurnameIsNullError(String url) throws Exception {
        caseDataBuilder.deceasedSurname(null);
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String json = OBJECT_MAPPER.writeValueAsString(callbackRequest);
        mockMvc.perform(post(url).content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fieldErrors[0].param").value("callbackRequest"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("caseDetails.data.deceasedSurname"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("NotBlank"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("Deceased surname cannot be empty"));
    }

    @Test
    public void shouldReturnAliasNameTransformed() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadAliasNames.json");

        mockMvc.perform(post(CASE_TRANSFORM_URL).content(solicitorPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnAdditionalExecutorsTransformed() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorAdditionalExecutors.json");

        mockMvc.perform(post(CASE_TRANSFORM_URL).content(solicitorPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnCheckListValidateSuccessful() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorAdditionalExecutors.json");

        mockMvc.perform(post(CASE_CHCEKLIST_URL).content(solicitorPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnCheckListValidateUnSuccessful() throws Exception {
        String solicitorPayload = testUtils.getStringFromFile("solicitorPayloadAliasNames.json");

        mockMvc.perform(post(CASE_CHCEKLIST_URL).content(solicitorPayload).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0]").value("Please ensure all checks have been completed"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

    }
}
