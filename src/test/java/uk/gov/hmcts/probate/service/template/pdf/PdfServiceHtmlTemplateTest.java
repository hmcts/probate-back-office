package uk.gov.hmcts.probate.service.template.pdf;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.model.ccd.raw.BigDecimalNumberSerializer;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Map;

@RunWith(SpringRunner.class)
@JsonTest
public class PdfServiceHtmlTemplateTest {

    private static final LocalDate DOB = LocalDate.of(1990, 4, 4);
    private static final LocalDate DOD = LocalDate.of(2017, 4, 4);
    private static final Long ID = 1L;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final String FORENAME = "Andy";
    private static final String SURNAME = "Michael";
    private static final String SOLICITOR_APP_REFERENCE = "Reference";
    private static final String SOLICITOR_FIRM_NAME = "Legal Service Ltd";
    private static final String SOLICITOR_FIRM_ADD_LINE1 = "Sols Add Line1";
    private static final String SOLICITOR_FIRM_POSTCODE = "SW1E 6EA";
    private static final String IHT_FORM = "IHT207";
    private static final String SOLICITOR_NAME = "Peter Crouch";
    private static final String SOLICITOR_JOB_TITLE = "Lawyer";
    private static final String PAYMENT_METHOD = "Fee account";
    private static final String WILL_HAS_CODICILS = "Yes";
    private static final String NUMBER_OF_CODICILS = "1";
    private static final BigDecimal APPLICATION_FEE = BigDecimal.TEN;
    private static final BigDecimal FEE_FOR_UK_COPIES = BigDecimal.TEN;
    private static final BigDecimal FEE_FOR_NON_UK_COPIES = BigDecimal.TEN;
    private static final BigDecimal TOTAL_FEE = BigDecimal.TEN;
    private static final BigDecimal NET = new BigDecimal("100001");
    private static final BigDecimal GROSS = new BigDecimal("9000043");
    private static final Long EXTRA_UK = 1L;
    private static final Long EXTRA_OUTSIDE_UK = 2L;
    private static final String DEC_ADD_LINE1 = "DecLine1";
    private static final String DEC_ADD_PC = "DecPC";
    private static final SolsAddress DECEASED_ADDRESS = SolsAddress.builder().addressLine1(DEC_ADD_LINE1)
            .postCode(DEC_ADD_PC).build();
    private static final String EX_ADD_LINE1 = "ExLine1";
    private static final String EX_ADD_PC = "ExPC";
    private static final SolsAddress PRIMARY_ADDRESS = SolsAddress.builder().addressLine1(EX_ADD_LINE1)
            .postCode(EX_ADD_PC).build();
    private static final SolsAddress SOLICITOR_ADDRESS = SolsAddress.builder().addressLine1(SOLICITOR_FIRM_ADD_LINE1)
            .postCode(SOLICITOR_FIRM_POSTCODE).build();
    private static final String PRIMARY_APPLICANT_APPLYING = "Yes";
    private static final String PRIMARY_APPLICANT_HAS_ALIAS = "No";
    private static final String OTHER_EXEC_EXISTS = "No";
    private static final String WILL_EXISTS = "Yes";
    private static final String WILL_ACCESS_ORIGINAL = "Yes";
    private static final String PRIMARY_FORENAMES = "ExFN";
    private static final String PRIMARY_SURNAME = "ExSN";
    private static final String DECEASED_OTHER_NAMES = "No";
    private static final String DECEASED_DOM_UK = "Yes";

    private PebbleEngine pebble;
    private String jsonData;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        pebble = new PebbleEngine.Builder()
                .strictVariables(true)
                .loader(new StringLoader())
                .cacheActive(false)
                .build();
        CaseData caseData = CaseData.builder()
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
                .solsSolicitorAddress(SOLICITOR_ADDRESS)
                .ihtFormId(IHT_FORM)
                .solsSOTName(SOLICITOR_NAME)
                .solsSOTJobTitle(SOLICITOR_JOB_TITLE)
                .solsPaymentMethods(PAYMENT_METHOD)
                .applicationFee(APPLICATION_FEE)
                .feeForUkCopies(FEE_FOR_UK_COPIES)
                .feeForNonUkCopies(FEE_FOR_NON_UK_COPIES)
                .extraCopiesOfGrant(EXTRA_UK)
                .outsideUKGrantCopies(EXTRA_OUTSIDE_UK)
                .totalFee(TOTAL_FEE)
                .build();

        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        ObjectMapper otherObjectMapper = objectMapper.copy();
        SimpleModule module = new SimpleModule();
        module.addSerializer(BigDecimal.class, new BigDecimalNumberSerializer());
        otherObjectMapper.registerModule(module);

        jsonData = otherObjectMapper.writeValueAsString(callbackRequest);
    }

    @Test
    public void shouldGenerateCorrectHtml() throws Exception {
        Map<String, Object> valuesMap = objectMapper.readValue(jsonData, MapType.REFERENCE);
        String templateString = new String(Files.readAllBytes(Paths.get(getClass()
                .getResource("/templates/pdf/legalStatementProbate.html").toURI())));
        Writer writer = new StringWriter();
        PebbleTemplate pebbleTemplate = pebble.getTemplate(templateString);
        pebbleTemplate.evaluate(writer, valuesMap);
        String generatedHtml = writer.toString();
        System.out.println(generatedHtml);
    }

    private static class MapType extends TypeReference<Map<String, Object>> {
        private static final MapType REFERENCE = new MapType();
    }
}
