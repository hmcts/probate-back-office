package uk.gov.hmcts.probate.service.taskList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import uk.gov.hmcts.probate.controller.CaseDataTestBuilder;
import uk.gov.hmcts.probate.model.ccd.raw.*;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.tasklist.DefaultTaskListRenderer;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.*;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

public class DefaultTaskListRendererTest {

    private final DefaultTaskListRenderer renderer = new DefaultTaskListRenderer();
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

    private final String expectedHtml = "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">\n" +
            "<h2 class=\"govuk-heading-l\">1. Enter application details</h2>\n" +
            "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">These steps are to be completed by the legal professional.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
            "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
            "\n" +
            "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Add solicitor details</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"92px\" height=\"25px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/DTSPB-778-basic-case-progress-tab/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n" +
            "</div></div>\n" +
            "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
            "\n" +
            "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Add deceased details</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"92px\" height=\"25px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/DTSPB-778-basic-case-progress-tab/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n" +
            "</div></div>\n" +
            "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
            "\n" +
            "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Add application details</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"92px\" height=\"25px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/DTSPB-778-basic-case-progress-tab/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n" +
            "</div></div>\n" +
            "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
            "\n" +
            "<br/>\n" +
            "<h2 class=\"govuk-heading-l\">2. Sign legal statement and submit application</h2>\n" +
            "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">These steps are to be completed by the legal professional.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
            "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
            "\n" +
            "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Review and sign legal statement and submit application</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"92px\" height=\"25px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/DTSPB-778-basic-case-progress-tab/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n" +
            "</div></div>\n" +
            "<reviewAndSubmitDate/><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">The legal statement is generated. You can review, change any details, then sign and submit your application.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
            "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
            "\n" +
            "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Send documents<br/><details class=\"govuk-details\" data-module=\"govuk-details\">\n" +
            "  <summary class=\"govuk-details__summary\">\n" +
            "    <span class=\"govuk-details__summary-text\">\n" +
            "      View the documents needed by HM Courts and Tribunal Service\n" +
            "    </span>\n" +
            "  </summary>\n" +
            "  <div class=\"govuk-details__text\">\n" +
            "    You now need to send us<br/><ul><li>your reference number 1 written on a piece of paper</li><li>the stamped (receipted) IHT 421 with this application</li><li>a photocopy of the signed legal statement and declaration</li></ul>\n" +
            "  </div>\n" +
            "</details></p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"92px\" height=\"25px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/DTSPB-778-basic-case-progress-tab/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n" +
            "</div></div>\n" +
            "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
            "\n" +
            "<br/>\n" +
            "<h2 class=\"govuk-heading-l\">3. Review application</h2>\n" +
            "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">These steps are completed by HM Courts and Tribunals Service staff. It can take a few weeks before the review starts.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
            "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
            "\n" +
            "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Authenticate documents</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"92px\" height=\"25px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/DTSPB-778-basic-case-progress-tab/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n" +
            "</div></div>\n" +
            "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">We will authenticate your documents and match them with your application.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
            "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
            "\n" +
            "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Examine application</p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"92px\" height=\"25px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/DTSPB-778-basic-case-progress-tab/src/main/resources/statusImages/in-progress.png\" alt=\"IN PROGRESS\" title=\"IN PROGRESS\" /></p>\n" +
            "</div></div>\n" +
            "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">We review your application for incomplete information or problems and validate it against other cases or caveats. After the review we prepare the grant.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
            "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">Your application will update through any of these case states as it is reviewed by our team:</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
            "<ul class=\"govuk-list govuk-list--bullet\">\n" +
            "<li>Examining</li>\n" +
            "<li>Case Matching</li>\n" +
            "<li>Case selected for Quality Assurance</li>\n" +
            "<li>Ready to issue</li>\n" +
            "</ul><hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
            "\n" +
            "<h2 class=\"govuk-heading-l\">4. Grant of representation</h2>\n" +
            "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">This step is completed by HM Courts and Tribunals Service staff.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
            "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
            "\n" +
            "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Issue grant of representation</p></div><div class=\"govuk-grid-column-one-third\"></div></div>\n" +
            "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font color=\"#505a5f\">The grant will be delivered in the post a few days after issuing.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n" +
            "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n" +
            "\n" +
            "</div>\n" +
            "</div>\n";

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
    public void shouldRenderDefaultCaseProgressHtmlCorrectly() {
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        caseDetails.setState("BOExamining");
        String result = renderer.renderHtml(caseDetails);
        assertEquals(expectedHtml, result);
    }

}
