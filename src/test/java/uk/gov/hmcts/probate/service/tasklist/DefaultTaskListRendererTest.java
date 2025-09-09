package uk.gov.hmcts.probate.service.tasklist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.businessrule.NoDocumentsRequiredBusinessRule;
import uk.gov.hmcts.probate.model.caseprogress.TaskListState;
import uk.gov.hmcts.probate.model.caseprogress.TaskState;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.EstateItem;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.APPLICATION_FEE;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.APPLICATION_GROUNDS;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.DECEASED_ADDRESS;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.DECEASED_DOM_UK;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.DECEASED_OTHER_NAMES;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.DOB;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.DOD;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.EXTRA_OUTSIDE_UK;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.EXTRA_UK;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.FEE_FOR_NON_UK_COPIES;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.FEE_FOR_UK_COPIES;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.GROSS;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.ID;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.IHT_FORM;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.LAST_MODIFIED;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.NET;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.OTHER_EXEC_EXISTS;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.PRIMARY_ADDRESS;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.PRIMARY_APPLICANT_APPLYING;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.PRIMARY_APPLICANT_HAS_ALIAS;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.PRIMARY_FORENAMES;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.PRIMARY_SURNAME;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.SOLICITOR_APP_REFERENCE;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.SOLICITOR_FIRM_LINE1;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.SOLICITOR_FIRM_NAME;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.SOLICITOR_FIRM_POSTCODE;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.TOTAL_FEE;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.WILL_ACCESS_ORIGINAL;
import static uk.gov.hmcts.probate.controller.CaseDataTestBuilder.WILL_EXISTS;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

class DefaultTaskListRendererTest {

    @InjectMocks
    private DefaultTaskListRenderer renderer;

    @Mock
    private TaskStateRenderer taskStateRendererMock;
    @Mock
    private NoDocumentsRequiredBusinessRule noDocumentsRequiredBusinessRule;
    @Mock
    private CaseData mockCaseData;
    @Mock
    DocumentLink mockDocumentLink;

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

    private static final String SERVICE_REQUEST_REFERENCE = "Service Request Ref";

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

    private final String expectedHtml = "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">\n"
        + "<h2 class=\"govuk-heading-l\">1. Enter application details</h2>\n"
        + "<h2 class=\"govuk-heading-l\">Rhoi manylion y cais</h2>\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
        + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">"
        + "These steps are to be completed by the Probate practitioner.</font></p>"
        + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">Dylai'r camau hyn gael eu cwblhau gan yr "
        + "ymarferydd profiant.</font></p>"
        + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
        + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Add "
        + "Probate practitioner details</p>"
        + "</div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
        + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
        + TaskState.CODE_BRANCH
        + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
        + "</div></div>\n"
        + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
        + "\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Add "
        + "deceased details</p>"
        + "</div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
        + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
        + TaskState.CODE_BRANCH
        + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
        + "</div></div>\n"
        + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Add "
        + "application details</p>"
        + "</div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
        + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
        + TaskState.CODE_BRANCH
        + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
        + "</div></div>\n"
        + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<br/>\n"
        + "<h2 class=\"govuk-heading-l\">2. Sign legal statement and submit application</h2>\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font "
        + "color=\"#505a5f\">These steps are to be completed by the Probate practitioner.</font></p>"
        + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">Dylai'r camau hyn gael eu cwblhau gan "
        + "yr ymarferydd profiant.</font></p></div><div "
        + "class=\"govuk-grid-column-one-third\">&nbsp;"
        + "</div></div>\n"
        + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
        + "Review and sign legal statement and submit application</p></div><div class=\"govuk-grid-column-one-third\">"
        + "<p><img align=\"right\" width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/"
        + "hmcts/probate-back-office/"
        + TaskState.CODE_BRANCH
        + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
        + "</div></div>\n<div class=\"govuk-grid-row\">"
        + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
        + "<font color=\"#505a5f\">"
        + "The legal statement is generated. You can review, change any details, then sign and "
        + "submit your application.</font></p>"
        + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">Cynhyrchwyd y datganiad cyfreithiol.  "
        + "Gallwch adolygu, newid unrhyw fanylion, llofnodi a chyflwyno eich cais.</font></p>"
        + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
        + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
        + "<p class=\"govuk-body-s\">Send documents<br/>Anfon dogfennau<br/><details class=\"govuk-details\" "
        + "data-module=\"govuk-details\">\n"
        + "  <summary class=\"govuk-details__summary\">\n"
        + "    <span class=\"govuk-details__summary-text\">\n"
        + "      View the documents needed by HM Courts and Tribunal Service\n"
        + "    </span>\n"
        + "  </summary>\n"
        + "  <div class=\"govuk-details__text\">\n"
        + "    You now need to send us<br/><ul><li>the printed coversheet (accessed in the cover sheet tab)"
        + " or your reference number 1 written on a"
        + " sheet of paper</li><li>a photocopy of the signed legal statement and declaration</li>"
        + "<li>the original will and any codicils</li><li>the inheritance tax form IHT207</li></ul>\n"
        + "  </div>\n"
        + "</details></p></div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" "
        + "height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
        + TaskState.CODE_BRANCH
        + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
        + "</div></div>\n"
        + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
        + "\n"
        + "<br/>\n"
        + "<h2 class=\"govuk-heading-l\">3. Review application</h2>\n"
        + "<h2 class=\"govuk-heading-l\">Adolygu'r cais</h2>\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
        + "<font color=\"#505a5f\">These steps are completed by HM Courts and Tribunals Service staff. "
        + "It can take a few weeks before the review starts.</font></p>"
        + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">Dylai'r camau hyn gael eu cwblhau gan staff "
        + "Gwasanaeth Llysoedd a Thribiwnlysoedd EF. Gall gymryd ychydig wythnosau cyn i'r adolygiad ddechrau."
        + "</font></p>"
        + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
        + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
        + "\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
        + "<p class=\"govuk-body-s\">Authenticate documents</p></div><div class=\"govuk-grid-column-one-third\">"
        + "<p><img align=\"right\" width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com"
        + "/hmcts/probate-back-office/"
        + TaskState.CODE_BRANCH
        + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
        + "</div></div>\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
        + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">"
        + "We will authenticate your documents and match them with your application.</font></p>"
        + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">"
        + "Byddwn yn dilysu eich dogfennau ac yn eu paru â'ch cais.</font></p>"
        + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
        + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
        + "\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
        + "<p class=\"govuk-body-s\">Examine application</p></div><div class=\"govuk-grid-column-one-third\">"
        + "<p><img align=\"right\" width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/"
        + "probate-back-office/"
        + TaskState.CODE_BRANCH
        + "/src/main/resources/statusImages/in-progress.png\" alt=\"IN PROGRESS\" title=\"IN PROGRESS\" /></p>\n"
        + "</div></div>\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
        + "<font color=\"#505a5f\">We review your application for incomplete information or problems and validate it "
        + "against other cases or caveats. After the review we prepare the grant.</font></p>"
        + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">"
        + "Byddwn yn adolygu eich cais am wybodaeth anghyflawn neu broblemau ac yn ei ddilysu "
        + "yn erbyn achosion eraill neu gafeatau. Ar ôl yr adolygiad, byddwn yn paratoi'r grant.</font></p>"
        + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
        + "<font color=\"#505a5f\">Your application will update through any of these case states as it is reviewed by "
        + "our team:</font></p>"
        + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">Bydd eich cais yn cael ei ddiweddaru ac yn symud "
        + "drwy'r camau hyn fel y bydd yn cael ei adolygu gan ein tîm:</font></p>"
        + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
        + "<ul class=\"govuk-list govuk-list--bullet\">\n"
        + "<li>Examining</li>\n"
        + "<li>Archwilio</li>\n"
        + "<li>Case Matching</li>\n"
        + "<li>Paru Achos</li>\n"
        + "<li>Case selected for Quality Assurance</li>\n"
        + "<li>Achos wedi’i ddethol ar gyfer Sicrhau Ansawdd</li>\n"
        + "<li>Ready to issue</li>\n"
        + "<li>Barod i’w gychwyn</li>\n"
        + "</ul><hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
        + "\n"
        + "<h2 class=\"govuk-heading-l\">4. Grant of representation</h2>\n"
        + "<h2 class=\"govuk-heading-l\">Grant cynrychiolaeth</h2>\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
        + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">This step is completed by HM Courts and Tribunals "
        + "Service staff.</font></p>"
        + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">Dylai'r cam hwn gael ei gwblhau gan staff Gwasanaeth "
        + "Llysoedd a Thribiwnlysoedd EF.</font></p>"
        + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
        + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
        + "\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
        + "Issue grant of representation</p></div><div class=\"govuk-grid-column-one-third\"></div></div>\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font "
        + "color=\"#505a5f\">The grant will be delivered in the post a few days after issuing.</font></p>"
        + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">Bydd y grant yn cael ei anfon yn y post ychydig "
        + "ddyddiau ar ôl ei gyhoeddi.</font></p>"
        + "</div><div class=\"govuk-grid-column-one-third\">"
        + "&nbsp;</div></div>\n"
        + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
        + "\n</div>\n</div>\n";
    private final String expectedHtml1 = "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">\n"
        + "<h2 class=\"govuk-heading-l\">1. Enter application details</h2>\n"
        + "<h2 class=\"govuk-heading-l\">1. Rhoi manylion y cais</h2>\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
        + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">"
        + "These steps are to be completed by the Probate practitioner.</font></p>"
        + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">Dylai'r camau hyn gael eu cwblhau gan yr "
        + "ymarferydd profiant.</font></p>"
        + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
        + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Add "
        + "Probate practitioner details</p>"
        + "</div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
        + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
        + TaskState.CODE_BRANCH
        + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
        + "</div></div>\n"
        + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
        + "\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Add "
        + "deceased details</p>"
        + "</div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
        + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
        + TaskState.CODE_BRANCH
        + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
        + "</div></div>\n"
        + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">Add "
        + "application details</p>"
        + "</div><div class=\"govuk-grid-column-one-third\"><p><img align=\"right\" width=\"114px\" height=\"31px\" "
        + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
        + TaskState.CODE_BRANCH
        + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
        + "</div></div>\n"
        + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n<br/>\n"
        + "<h2 class=\"govuk-heading-l\">2. Sign legal statement and submit application</h2>\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font "
        + "color=\"#505a5f\">These steps are to be completed by the Probate practitioner.</font></p>"
        + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">Dylai'r camau hyn gael eu cwblhau gan yr "
        + "ymarferydd profiant.</font></p></div><div "
        + "class=\"govuk-grid-column-one-third\">&nbsp;"
        + "</div></div>\n"
        + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
        + "Review and sign legal statement and submit application</p></div><div class=\"govuk-grid-column-one-third\">"
        + "<p><img align=\"right\" width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/"
        + "hmcts/probate-back-office/"
        + TaskState.CODE_BRANCH
        + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
        + "</div></div>\n<div class=\"govuk-grid-row\">"
        + "<div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
        + "<font color=\"#505a5f\">"
        + "The legal statement is generated. You can review, change any details, then sign and "
        + "submit your application.</font></p></div>"
        + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">Cynhyrchwyd y datganiad cyfreithiol.  "
        + "Gallwch adolygu, newid unrhyw fanylion, llofnodi a chyflwyno eich cais.</font></p>"
        + "<br/>\n"
        + "<h2 class=\"govuk-heading-l\">3. Review application</h2>\n"
        + "<h2 class=\"govuk-heading-l\">3. Adolygu'r cais</h2>\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
        + "<font color=\"#505a5f\">These steps are completed by HM Courts and Tribunals Service staff. "
        + "It can take a few weeks before the review starts.</font></p>"
        + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">Dylai'r camau hyn gael eu cwblhau gan staff "
        + "Gwasanaeth Llysoedd a Thribiwnlysoedd EF. Gall gymryd ychydig wythnosau cyn i'r adolygiad ddechrau."
        + "</font></p>"
        + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
        + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
        + "\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
        + "<p class=\"govuk-body-s\">Authenticate documents</p></div><div class=\"govuk-grid-column-one-third\">"
        + "<p><img align=\"right\" width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com"
        + "/hmcts/probate-back-office/"
        + TaskState.CODE_BRANCH
        + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
        + "</div></div>\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
        + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">"
        + "We will authenticate your documents and match them with your application.</font></p>"
        + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">"
        + "Byddwn yn dilysu eich dogfennau ac yn eu paru â'ch cais.</font></p>"
        + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
        + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
        + "\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
        + "<p class=\"govuk-body-s\">Examine application</p></div><div class=\"govuk-grid-column-one-third\">"
        + "<p><img align=\"right\" width=\"114px\" height=\"31px\" src=\"https://raw.githubusercontent.com/hmcts/"
        + "probate-back-office/"
        + TaskState.CODE_BRANCH
        + "/src/main/resources/statusImages/in-progress.png\" alt=\"IN PROGRESS\" title=\"IN PROGRESS\" /></p>\n"
        + "</div></div>\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
        + "<font color=\"#505a5f\">We review your application for incomplete information or problems and validate it "
        + "against other cases or caveats. After the review we prepare the grant.</font></p>"
        + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">"
        + "Byddwn yn adolygu eich cais am wybodaeth anghyflawn neu broblemau ac yn ei ddilysu "
        + "yn erbyn achosion eraill neu gafeatau. Ar ôl yr adolygiad, byddwn yn paratoi'r grant.</font></p>"
        + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
        + "<font color=\"#505a5f\">Your application will update through any of these case states as it is reviewed by "
        + "our team:</font></p>"
        + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">Bydd eich cais yn cael ei ddiweddaru ac yn symud "
        + "drwy'r camau hyn fel y bydd yn cael ei adolygu gan ein tîm:</font></p>"
        + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
        + "<ul class=\"govuk-list govuk-list--bullet\">\n"
        + "<li>Examining</li>\n"
        + "<li>Archwilio</li>\n"
        + "<li>Case Matching</li>\n"
        + "<li>Paru Achos</li>\n"
        + "<li>Case selected for Quality Assurance</li>\n"
        + "<li>Achos wedi’i ddethol ar gyfer Sicrhau Ansawdd</li>\n"
        + "<li>Ready to issue</li>\n"
        + "<li>Barod i’w gychwyn</li>\n"
        + "</ul><hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
        + "\n"
        + "<h2 class=\"govuk-heading-l\">4. Grant of representation</h2>\n"
        + "<h2 class=\"govuk-heading-l\">4. Grant cynrychiolaeth</h2>\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
        + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">This step is completed by HM Courts and Tribunals "
        + "Service staff.</font></p>"
        + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">Dylai'r cam hwn gael ei gwblhau gan staff Gwasanaeth "
        + "Llysoedd a Thribiwnlysoedd EF.</font></p>"
        + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
        + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
        + "\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
        + "Issue grant of representation</p></div><div class=\"govuk-grid-column-one-third\"></div></div>\n"
        + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\"><font "
        + "color=\"#505a5f\">The grant will be delivered in the post a few days after issuing.</font></p>"
        + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">Bydd y grant yn cael ei anfon yn y post ychydig "
        + "ddyddiau ar ôl ei gyhoeddi.</font></p>"
        + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
        + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
        + "\n</div>\n</div>\n";

    @BeforeEach
    public void setup() {
        openMocks(this);
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
                .solsSolicitorIsApplying(YES)
                .solsSolicitorNotApplyingReason(SOLS_NOT_APPLYING_REASON)
                .solsSOTJobTitle(SOLICITOR_JOB_TITLE)
                .applicationFee(APPLICATION_FEE)
                .feeForUkCopies(FEE_FOR_UK_COPIES)
                .feeForNonUkCopies(FEE_FOR_NON_UK_COPIES)
                .extraCopiesOfGrant(EXTRA_UK)
                .outsideUKGrantCopies(EXTRA_OUTSIDE_UK)
                .totalFee(TOTAL_FEE)
                .scannedDocuments(SCANNED_DOCUMENTS_LIST);
    }

    @Test
    void shouldRenderCaseProgressHtmlCorrectlyForNoDocs() {
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        caseDetails.setState("BOExaminingReissue");
        when(mockCaseData.getSolsLegalStatementUpload()).thenReturn(mockDocumentLink);
        when(noDocumentsRequiredBusinessRule.isApplicable(any())).thenReturn(true);
        when(taskStateRendererMock.renderByReplace(TaskListState.TL_STATE_EXAMINE_APPLICATION,
            "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">\n"
                + "<h2 class=\"govuk-heading-l\">1. Enter application details</h2>\n"
                + "<h2 class=\"govuk-heading-l\">Rhoi manylion y cais</h2>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p "
                + "class=\"govuk-body-s\"><font color=\"#505a5f\">These steps are to be completed by the Probate "
                + "practitioner.</font></p>"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">Dylai'r camau hyn gael eu cwblhau gan yr "
                + "ymarferydd profiant.</font></p>"
                + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
                + "\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p "
                + "class=\"govuk-body-s\"><addSolicitorLink/></p>"
                + "<p class=\"govuk-body-s\"><addSolicitorLinkWelsh/></p></div><div "
                + "class=\"govuk-grid-column-one-third\"><status-addSolicitor/><status-addSolicitorWelsh/>"
                + "</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
                + "\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p "
                + "class=\"govuk-body-s\"><addDeceasedLink/></p>"
                + "<p class=\"govuk-body-s\"><addDeceasedLinkWelsh/></p></div><div "
                + "class=\"govuk-grid-column-one-third\"><status-addDeceasedDetails/>"
                + "<status-addDeceasedDetailsWelsh/></div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
                + "\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p "
                + "class=\"govuk-body-s\"><addAppLink/></p><p class=\"govuk-body-s\"><addAppLinkWelsh/></p></div><div "
                + "class=\"govuk-grid-column-one-third\"><status-addApplicationDetails/>"
                    + "<status-addApplicationDetailsWelsh/></div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
                + "\n"
                + "<br/>\n"
                + "<h2 class=\"govuk-heading-l\">2. Sign legal statement and submit application</h2>\n"
                + "<h2 class=\"govuk-heading-l\">Llofnodi'r datganiad cyfreithiol a chyflwyno'r cais</h2>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p "
                + "class=\"govuk-body-s\"><font color=\"#505a5f\">These steps are to be completed by the Probate "
                + "practitioner.</font></p>"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">Dylai'r camau hyn gael eu cwblhau gan yr "
                + "ymarferydd profiant.</font></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
                + "\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><rvwLink/></p>"
                + "</div><div "
                + "class=\"govuk-grid-column-one-third\"><status-reviewAndSubmit/></div></div>\n"
                + "<reviewAndSubmitDate/><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p"
                + " class=\"govuk-body-s\"><font color=\"#505a5f\">The legal statement is generated. You can review, "
                + "change any details, then sign and submit your application.</font></p>"
                + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><rvwLinkWelsh/></p></div><div "
                + "class=\"govuk-grid-column-one-third\"><status-reviewAndSubmitWelsh/></div></div>\n"
                + "<reviewAndSubmitDateWelsh/>"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">Cynhyrchwyd y datganiad cyfreithiol.  "
                + "Gallwch adolygu, newid unrhyw fanylion, llofnodi a chyflwyno eich cais.</font></p>"
                + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
                + "\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p class=\"govuk-body-s\">"
                + "<paymentTabLink/></p><p class=\"govuk-body-s\"><paymentTabLinkWelsh/></p>"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\"><paymentHintText/></font></p>"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\"><paymentHintTextWelsh/></font></p>"
                + "</div><div class=\"govuk-grid-column-one-third\"><status-paymentMade/>"
                    + "<status-paymentMadeWelsh/></div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
                + "\n"
                + "<br/>\n"
                + "<h2 class=\"govuk-heading-l\">3. Review application</h2>\n"
                + "<h2 class=\"govuk-heading-l\">Adolygu'r cais</h2>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">These steps are completed by HM Courts and "
                + "Tribunals Service staff. It can take a few weeks before the review starts.</font></p>"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">Dylai'r camau hyn gael eu cwblhau gan staff "
                + "Gwasanaeth Llysoedd a Thribiwnlysoedd EF. Gall gymryd ychydig wythnosau cyn i'r adolygiad ddechrau."
                + "</font></p>"
                + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
                + "\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><authDocsLink/></p>"
                + "</div><div class=\"govuk-grid-column-one-third\"><status-authDocuments/></div></div>\n"
                + "<authenticatedDate/><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">We will authenticate your documents and "
                + "match them with your application.</font></p>"
                + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><authDocsLinkWelsh/></p>"
                + "</div><div class=\"govuk-grid-column-one-third\"><status-authDocumentsWelsh/></div></div>\n"
                + "<authenticatedDateWelsh/><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "Byddwn yn dilysu eich dogfennau ac yn eu paru â'ch cais.</font></p>"
                + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
                + "\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><examAppLink/></p>"
                + "</div><div class=\"govuk-grid-column-one-third\"><status-examineApp/></div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">We review your application for incomplete "
                + "information or problems and validate it against other cases or caveats. After the review we "
                + "prepare the grant.</font></p>"
                + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">Your application will update through "
                + "any of these case states as it is reviewed by our team:</font></p>"
                + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<ul class=\"govuk-list govuk-list--bullet\">\n"
                + "<li>Examining</li>\n"
                + "<li>Case Matching</li>\n"
                + "<li>Case selected for Quality Assurance</li>\n"
                + "<li>Ready to issue</li>\n"
                + "</ul>"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><examAppLinkWelsh/></p>"
                + "</div><div class=\"govuk-grid-column-one-third\"><status-examineAppWelsh/></div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">"
                + "Byddwn yn adolygu eich cais am wybodaeth anghyflawn neu broblemau ac yn ei ddilysu "
                + "yn erbyn achosion eraill neu gafeatau. Ar ôl yr adolygiad, byddwn yn paratoi'r grant.</font></p>"
                + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">Bydd eich cais yn cael ei ddiweddaru "
                + "ac yn symud drwy'r camau hyn fel y bydd yn cael ei adolygu gan ein tîm:</font></p>"
                + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<ul class=\"govuk-list govuk-list--bullet\">\n"
                + "<li>Archwilio</li>\n"
                + "<li>Paru Achos</li>\n"
                + "<li>Achos wedi’i ddethol ar gyfer Sicrhau Ansawdd</li>\n"
                + "<li>Barod i’w gychwyn</li>\n"
                + "</ul><hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
                + "\n"
                + "<h2 class=\"govuk-heading-l\">4. Grant of representation</h2>\n"
                + "<h2 class=\"govuk-heading-l\">Grant cynrychiolaeth</h2>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p "
                + "class=\"govuk-body-s\"><font color=\"#505a5f\">This step is completed by HM Courts and Tribunals "
                + "Service staff.</font></p>"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">Dylai'r cam hwn gael ei gwblhau gan staff "
                + "Gwasanaeth Llysoedd a Thribiwnlysoedd EF.</font></p>"
                + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
                + "\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><issueGrantLink/></p>"
                + "</div><div class=\"govuk-grid-column-one-third\"><status-issueGrant/></div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">The grant will be delivered in the post a few "
                + "days after issuing.</font></p>"
                + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><issueGrantLinkWelsh/></p>"
                + "</div><div class=\"govuk-grid-column-one-third\"><status-issueGrantWelsh/></div></div>\n"
                + "<div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p class=\"govuk-body-s\"><font color=\"#505a5f\">Bydd y grant yn cael ei anfon yn y post "
                + "ychydig ddyddiau ar ôl ei gyhoeddi.</font></p>"
                + "</div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n"
                + "\n"
                + "</div>\n"
                + "</div>\n",
                ID, "WillLeft", null,
                null, null, caseDetails)).thenReturn(expectedHtml1);
        String result = renderer.renderHtml(caseDetails);
        assertEquals(expectedHtml1, result);
    }
}
