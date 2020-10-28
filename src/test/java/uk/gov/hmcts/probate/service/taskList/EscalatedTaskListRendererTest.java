package uk.gov.hmcts.probate.service.taskList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import uk.gov.hmcts.probate.controller.CaseDataTestBuilder;
import uk.gov.hmcts.probate.model.ccd.raw.*;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.tasklist.EscalatedTaskListRenderer;
import uk.gov.hmcts.probate.service.tasklist.StoppedTaskListRenderer;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.*;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

public class EscalatedTaskListRendererTest {

    private final EscalatedTaskListRenderer renderer = new EscalatedTaskListRenderer();
    private CaseData.CaseDataBuilder caseDataBuilder;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String FORENAME = "Andy";
    private static final String SURNAME = "Michael";
    private static final String WILL_TYPE_PROBATE = "WillLeft";
    private static final String WILL_HAS_CODICILS = "Yes";
    private static final String NUMBER_OF_CODICILS = "1";
    private static final String SOLICITOR_FORENAMES = "Peter";
    private static final String SOLICITOR_SURNAME = "Crouch";
    private static final String SOLICITOR_JOB_TITLE = "Lawyer";
    private static final String SOLS_NOT_APPLYING_REASON = "Power reserved";

    private static final List<CollectionMember<EstateItem>> UK_ESTATE = Arrays.asList(
            new CollectionMember<>(null,
                    EstateItem.builder()
                            .item("Item")
                            .value("999.99")
                            .build()));

    private static final DocumentLink SCANNED_DOCUMENT_URL = DocumentLink.builder()
            .documentBinaryUrl("http://somedoc")
            .documentFilename("somedoc.pdf")
            .documentUrl("http://somedoc/location")
            .build();

    private static final LocalDateTime scannedDate = LocalDateTime.parse("2018-01-01T12:34:56.123");
    private static final List<CollectionMember<ScannedDocument>> SCANNED_DOCUMENTS_LIST = Arrays.asList(
            new CollectionMember("id",
                    ScannedDocument.builder()
                            .fileName("scanneddocument.pdf")
                            .controlNumber("1234")
                            .scannedDate(scannedDate)
                            .type("other")
                            .subtype("will")
                            .url(SCANNED_DOCUMENT_URL)
                            .build()));

    private final String expectedHtml = "<div class='width-50'>## Case progress<div class=\"govuk-inset-text\">CASE_ESCALATED</div>\n" +
            "\n" +
            "## What happens next\n" +
            "<p class=\"govuk-body-s\">The case was escalated on Unknown.</p>\n" +
            "<p class=\"govuk-body-s\">The case will be reviewed by the Registrar and you will be notified by email if we need any information from you to progress the case.</p>\n" +
            "<p class=\"govuk-body-s\">Only contact the CTSC staff if your case has been escalated for 6 weeks or more and you have not received any communication since then.</p>\n</div>";
    @Before
    public void setup() {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        SolsAddress solsAddress = SolsAddress.builder()
                .addressLine1(SOLICITOR_FIRM_LINE1)
                .postCode(SOLICITOR_FIRM_POSTCODE)
                .build();

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
                .solsWillType(WILL_TYPE_PROBATE)
                .willExists(WILL_EXISTS)
                .willAccessOriginal(WILL_ACCESS_ORIGINAL)
                .ihtNetValue(NET)
                .ihtGrossValue(GROSS)
                .solsSolicitorAppReference(SOLICITOR_APP_REFERENCE)
                .willHasCodicils(WILL_HAS_CODICILS)
                .willNumberOfCodicils(NUMBER_OF_CODICILS)
                .solsSolicitorFirmName(SOLICITOR_FIRM_NAME)
                .solsSolicitorAddress(solsAddress)
                .ukEstate(UK_ESTATE)
                .applicationGrounds(APPLICATION_GROUNDS)
                .willDispose(YES)
                .englishWill(NO)
                .appointExec(YES)
                .ihtFormId(IHT_FORM)
                .solsSOTForenames(SOLICITOR_FORENAMES)
                .solsSOTSurname(SOLICITOR_SURNAME)
                .solsSolicitorIsExec(YES)
                .solsSolicitorIsMainApplicant(YES)
                .solsSolicitorIsApplying(YES)
                .solsSolicitorNotApplyingReason(SOLS_NOT_APPLYING_REASON)
                .solsSOTJobTitle(SOLICITOR_JOB_TITLE)
                .solsPaymentMethods(PAYMENT_METHOD)
                .applicationFee(APPLICATION_FEE)
                .feeForUkCopies(FEE_FOR_UK_COPIES)
                .feeForNonUkCopies(FEE_FOR_NON_UK_COPIES)
                .extraCopiesOfGrant(EXTRA_UK)
                .outsideUKGrantCopies(EXTRA_OUTSIDE_UK)
                .totalFee(TOTAL_FEE)
                .scannedDocuments(SCANNED_DOCUMENTS_LIST);
    }

    @Test
    public void shouldRenderEscalatedCaseProgressHtmlCorrectly() {
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        caseDetails.setState("BORegistrarEscalation");
        String result = renderer.renderHtml(caseDetails);
        assertTrue(result.equals(expectedHtml));
    }
}
