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
import uk.gov.hmcts.probate.service.tasklist.StoppedTaskListRenderer;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.*;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

public class StoppedTaskListRendererTest {
    private final StoppedTaskListRenderer renderer = new StoppedTaskListRenderer("");
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

    private static final String expectedHtml = "<div class=\"width-50\">\n\n<h2 class=\"govuk-heading-l\">Case progress</h2>\n\n<div class=\"govuk-inset-text govuk-!-font-weight-bold govuk-!-font-size-48\">Case stopped</div>\n" +
            "\n" +
            "<h2 class=\"govuk-heading-l\">What happens next</h2>\n\n" +
            "<p class=\"govuk-body-s\">The case was stopped on Unknown for one of two reasons:</p>\n" +
            "<ul class=\"govuk-list govuk-list--bullet\">\n" +
            "<li>an internal review is needed</li>\n" +
            "<li>further information from the applicant or solicitor is needed</li>\n" +
            "</ul>\n" +
            "\n" +
            "<p class=\"govuk-body-s\">You will be notified by email if we need any information from you to progress the case.</p>\n" +
            "<p class=\"govuk-body-s\">Only contact the CTSC staff if your case has been stopped for 4 weeks or more and you have not received any communication since then.</p>\n\n" +
            "<h2 class=\"govuk-heading-l\">Get help with your application</h2>\n\n" +
            "<h3 class=\"govuk-heading-m\">Telephone</h3>\n\n" +
            "<p class=\"govuk-body-s\">You will need the case reference or the deceased's full name when you call.</p><br/><p class=\"govuk-body-s\">Telephone: 0300 303 0648</p><p class=\"govuk-body-s\">Monday to Thursday, 8:00am to 5pm</p><p class=\"govuk-body-s\">Friday, 8am to 4:30pm</p><br/><p class=\"govuk-body-s\">Welsh language: 0300 303 0654</p><p class=\"govuk-body-s\">Monday to Friday, 8:00am to 5pm</p><br/>\n\n" +
            "<a href=\"https://www.gov.uk/call-charges\" target=\"_blank\" rel=\"noopener noreferrer\" class=\"govuk-link\">Find out about call charges</a>\n\n" +
            "<h3 class=\"govuk-heading-m\">Email</h3>\n\n" +
            "<a href=\"mailto:contactprobate@justice.gov.uk\" target=\"_blank\" rel=\"noopener noreferrer\" class=\"govuk-link\">contactprobate@justice.gov.uk</a><p class=\"govuk-body-s\">We aim to respond within 10 working days</p>\n\n" +
            "</div>";

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
    public void shouldRenderStoppedCaseProgressHtmlCorrectly() {
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        caseDetails.setState("BOCaseStopped");
        String result = renderer.renderHtml(caseDetails);
        assertTrue(result.equals(expectedHtml));
    }
}