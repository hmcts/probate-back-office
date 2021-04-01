package uk.gov.hmcts.probate.service.tasklist;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import uk.gov.hmcts.probate.model.caseprogress.UrlConstants;
import uk.gov.hmcts.probate.model.caseprogress.TaskListState;
import uk.gov.hmcts.probate.model.caseprogress.TaskState;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData.CaseDataBuilder;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.FileSystemResourceService;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ExecutorNotApplyingReason;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_ADMON;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_INTESTACY;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_PROBATE;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

public class TaskStateRendererTest {

    private CaseDetails caseDetails;
    public static final Long ID = 1L;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final String IHT_FORM_207 = "IHT207";
    private static final String IHT_FORM_205 = "IHT205";
    private static final String PRIMARY_APPLICANT_FIRST_NAME = "fName";
    private static final String PRIMARY_APPLICANT_SURNAME = "sName";
    private static final SolsAddress PRIMARY_APPLICANT_ADDRESS = mock(SolsAddress.class);
    private static final String PRIMARY_APPLICANT_NAME_ON_WILL = "willName";
    private final FileSystemResourceService fileSystemResourceService = new FileSystemResourceService();
    final String testHtml = fileSystemResourceService
        .getFileFromResourceAsString("caseprogress/testCaseProgressHTML");

    @Mock
    private AdditionalExecutorNotApplying additionalExecutorNotApplyingRenounced1;
    @Mock
    private AdditionalExecutorNotApplying additionalExecutorNotApplyingRenounced2;
    @Mock
    private AdditionalExecutorNotApplying additionalExecutorNotApplyingDied;

    @Mock
    private CollectionMember<AdditionalExecutorNotApplying> additionalExecutorsNotApplyingRenounced1;
    @Mock
    private CollectionMember<AdditionalExecutorNotApplying> additionalExecutorsNotApplyingRenounced2;
    @Mock
    private CollectionMember<AdditionalExecutorNotApplying> additionalExecutorsNotApplyingDied;

    private List<CollectionMember<AdditionalExecutorNotApplying>> additionalExecutorsNotApplyingList;

    @Before
    public void setup() {
        initMocks(this);
        CaseDataBuilder caseDataBuilder = CaseData.builder()
            .escalatedDate(LocalDate.of(2020, 1, 1));
        caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);

        when(additionalExecutorsNotApplyingRenounced1.getValue()).thenReturn(additionalExecutorNotApplyingRenounced1);
        when(additionalExecutorsNotApplyingRenounced2.getValue()).thenReturn(additionalExecutorNotApplyingRenounced2);
        when(additionalExecutorsNotApplyingDied.getValue()).thenReturn(additionalExecutorNotApplyingDied);

        additionalExecutorsNotApplyingList = new ArrayList<>();
        additionalExecutorsNotApplyingList.add(additionalExecutorsNotApplyingRenounced1);
        additionalExecutorsNotApplyingList.add(additionalExecutorsNotApplyingRenounced2);
        additionalExecutorsNotApplyingList.add(additionalExecutorsNotApplyingDied);
    }

    @Test
    public void shouldRenderCorrectHtmlForState_CaseCreated() {

        final String expectedHtml = "<div>Add solicitor details</div>\n"
            + "<div><a href=\""
            + UrlConstants.DECEASED_DETAILS_URL_TEMPLATE.replaceFirst("<CASE_ID>", "9999")
            + "\" class=\"govuk-link\">Add deceased details</a></div>\n"
            + "<div>Add application details</div>\n"
            + "<div>Review and sign legal statement and submit application</div>\n"
            + "<div>/div>\n"
            + "<div>Authenticate documents</div>\n"
            + "<div>Examine application</div>\n"
            + "<div>Issue grant of representation<</div>\n"
            + "<p><p><img align=\"right\" width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
            + "</p>\n"
            + "<p><p><img align=\"right\" width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/not-started.png\" alt=\"NOT STARTED\" title=\"NOT STARTED\" /></p>\n"
            + "</p>\n"
            + "<p></p>\n"
            + "<p></p>\n"
            + "<p><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
            + "<p><strong>Submitted on 01 Nov 2020</strong></p></div><div class=\"govuk-grid-column-one-third\">"
            + "&nbsp;</div></div>\n</p>\n<p></p>\n"
            + "<p><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
            + "<p><strong>Authenticated on "
            + "10 Oct 2020</strong></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
            + "</p>\n<p></p>\n<p></p>\n<p></p>\n";

        String result = TaskStateRenderer.renderByReplace(TaskListState.TL_STATE_ADD_DECEASED_DETAILS,
                testHtml, (long) 9999, "WillLeft", "No",
                LocalDate.of(2020,10,10),
                LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    public void shouldRenderCorrectHtmlForState_AddAppDtls_Gop_UpdateCase() {

        final String expectedHtml = "<div>Add solicitor details</div>\n"
            + "<div>Add deceased details</div>\n"
            + "<div><a href=\""
            + UrlConstants.ADD_APPLICATION_DETAILS_URL_TEMPLATE_GOP.replaceFirst("<CASE_ID>", "9999")
            + "\" class=\"govuk-link\">Add application details</a></div>\n"
            + "<div>Review and sign legal statement and submit application</div>\n"
            + "<div>/div>\n"
            + "<div>Authenticate documents</div>\n"
            + "<div>Examine application</div>\n"
            + "<div>Issue grant of representation<</div>\n"
            + "<p><p><img align=\"right\" width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
            + "</p>\n"
            + "<p><p><img align=\"right\" width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
            + "</p>\n"
            + "<p><p><img align=\"right\" width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/in-progress.png\" alt=\"IN PROGRESS\" title=\"IN PROGRESS\" /></p>\n"
            + "</p>\n"
            + "<p><p><img align=\"right\" width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/in-progress.png\" alt=\"IN PROGRESS\" title=\"IN PROGRESS\" /></p>\n"
            + "</p>\n"
            + "<p><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
            + "<p><strong>Submitted on 01 Nov 2020</strong></p></div>"
            + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
            + "</p>\n"
            + "<p></p>\n"
            + "<p><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
            + "<p><strong>Authenticated on 10 Oct 2020</strong></p></div>"
            + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
            + "</p>\n"
            + "<p></p>\n"
            + "<p></p>\n"
            + "<p></p>\n";

        String result = TaskStateRenderer.renderByReplace(TaskListState.TL_STATE_ADD_APPLICATION_DETAILS,
                testHtml, (long) 9999, "WillLeft", "Yes",
                LocalDate.of(2020,10,10),
                LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    public void shouldRenderCorrectHtmlForState_AddAppDtls_Gop_NotUpdatingCase() {

        final String expectedHtml = "<div>Add solicitor details</div>\n"
            + "<div>Add deceased details</div>\n"
            + "<div><a href=\""
            + UrlConstants.ADD_APPLICATION_DETAILS_URL_TEMPLATE_GOP.replaceFirst("<CASE_ID>", "9999")
            + "\" class=\"govuk-link\">Add application details</a></div>\n"
            + "<div>Review and sign legal statement and submit application</div>\n"
            + "<div>/div>\n"
            + "<div>Authenticate documents</div>\n"
            + "<div>Examine application</div>\n"
            + "<div>Issue grant of representation<</div>\n"
            + "<p><p><img align=\"right\" width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
            + "</p>\n"
            + "<p><p><img align=\"right\" width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
            + "</p>\n"
            + "<p><p><img align=\"right\" width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/not-started.png\" alt=\"NOT STARTED\" title=\"NOT STARTED\" /></p>\n"
            + "</p>\n"
            + "<p></p>\n"
            + "<p><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p><strong>"
            + "Submitted on 01 Nov 2020"
            + "</strong></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
            + "</p>\n"
            + "<p></p>\n"
            + "<p><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p><strong>Authenticated "
            + "on 10 Oct 2020"
            + "</strong></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
            + "</p>\n<p></p>\n<p></p>\n<p></p>\n";

        String result = TaskStateRenderer.renderByReplace(TaskListState.TL_STATE_ADD_APPLICATION_DETAILS,
                testHtml, (long) 9999, "WillLeft", "No",
                LocalDate.of(2020,10,10),
                LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    public void shouldRenderCorrectHtmlForState_AddAppDtls_Intestacy() {

        final String expectedHtml = "<div>Add solicitor details</div>\n"
            + "<div>Add deceased details</div>\n"
            + "<div><a href=\""
            + UrlConstants.ADD_APPLICATION_DETAILS_URL_TEMPLATE_INTESTACY
                .replaceFirst("<CASE_ID>", "9999")
            + "\" class=\"govuk-link\">Add application details</a></div>\n"
            + "<div>Review and sign legal statement and submit application</div>\n"
            + "<div>/div>\n"
            + "<div>Authenticate documents</div>\n"
            + "<div>Examine application</div>\n"
            + "<div>Issue grant of representation<</div>\n"
            + "<p><p><img align=\"right\" width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
            + "</p>\n"
            + "<p><p><img align=\"right\" width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
            + "</p>\n"
            + "<p><p><img align=\"right\" width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/not-started.png\" alt=\"NOT STARTED\" title=\"NOT STARTED\" /></p>\n"
            + "</p>\n"
            + "<p></p>\n"
            + "<p><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p><strong>Submitted on "
            + "01 Nov 2020"
            + "</strong></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
            + "</p>\n"
            + "<p></p>\n"
            + "<p><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p><strong>"
            + "Authenticated on 10 Oct 2020"
            + "</strong></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
            + "</p>\n<p></p>\n<p></p>\n<p></p>\n";

        String result = TaskStateRenderer.renderByReplace(TaskListState.TL_STATE_ADD_APPLICATION_DETAILS,
                testHtml, (long) 9999, "NoWill", null,
                LocalDate.of(2020,10,10),
                LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    public void shouldRenderCorrectHtmlForState_AddAppDtls_AdmonWill() {

        final String expectedHtml = "<div>Add solicitor details</div>\n"
            + "<div>Add deceased details</div>\n"
            + "<div><a href=\""
            + UrlConstants.ADD_APPLICATION_DETAILS_URL_TEMPLATE_ADMON_WILL
                .replaceFirst("<CASE_ID>", "9999")
            + "\" class=\"govuk-link\">Add application details</a></div>\n"
            + "<div>Review and sign legal statement and submit application</div>\n"
            + "<div>/div>\n"
            + "<div>Authenticate documents</div>\n"
            + "<div>Examine application</div>\n"
            + "<div>Issue grant of representation<</div>\n"
            + "<p><p><img align=\"right\" width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
            + "</p>\n"
            + "<p><p><img align=\"right\" width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
            + "</p>\n"
            + "<p><p><img align=\"right\" width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/not-started.png\" alt=\"NOT STARTED\" title=\"NOT STARTED\" /></p>\n"
            + "</p>\n"
            + "<p></p>\n"
            + "<p><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p><strong>Submitted on "
            + "01 Nov 2020"
            + "</strong></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
            + "</p>\n"
            + "<p></p>\n"
            + "<p><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p><strong>Authenticated "
            + "on 10 Oct 2020"
            + "</strong></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
            + "</p>\n"
            + "<p></p>\n"
            + "<p></p>\n"
            + "<p></p>\n";

        String result = TaskStateRenderer.renderByReplace(TaskListState.TL_STATE_ADD_APPLICATION_DETAILS,
                testHtml, (long) 9999, "WillLeftAnnexed", "No",
                LocalDate.of(2020,10,10),
                LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    public void shouldRenderCorrectDocumentsForState_SendDocuments_GopWithRenouncingExecs() {
        when(additionalExecutorNotApplyingRenounced1.getNotApplyingExecutorName()).thenReturn("Executor One");
        when(additionalExecutorNotApplyingRenounced1.getNotApplyingExecutorReason()).thenReturn("Renunciation");

        when(additionalExecutorNotApplyingRenounced2.getNotApplyingExecutorName()).thenReturn("Executor Two");
        when(additionalExecutorNotApplyingRenounced2.getNotApplyingExecutorReason()).thenReturn("Renunciation");

        when(additionalExecutorNotApplyingDied.getNotApplyingExecutorName()).thenReturn("Executor Three");
        when(additionalExecutorNotApplyingDied.getNotApplyingExecutorReason()).thenReturn(
            ExecutorNotApplyingReason.DIED_BEFORE.toString());

        final CaseData caseData = CaseData.builder()
            .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
            .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
            .primaryApplicantIsApplying(NO)
            .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
            .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
            .solsAdditionalExecutorList(null)
            .ihtFormId(IHT_FORM_207)
            .additionalExecutorsNotApplying(additionalExecutorsNotApplyingList)
            .solsWillType(GRANT_TYPE_PROBATE)
            .solsFeeAccountNumber("1")
            .build();

        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);

        String expectedHtml = fileSystemResourceService
            .getFileFromResourceAsString("caseprogress/gop/solicitorCaseProgressSendDocuments");
        expectedHtml = expectedHtml.replaceAll("<BRANCH/>", TaskState.CODE_BRANCH);

        String result = TaskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
            testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
            LocalDate.of(2020,10,10),
            LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    public void shouldRenderCorrectDocumentsForState_SendDocuments_GopWillHasCodicils() {
        final CaseData caseData = CaseData.builder()
            .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
            .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
            .primaryApplicantIsApplying(NO)
            .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
            .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
            .solsAdditionalExecutorList(null)
            .solsWillType(GRANT_TYPE_PROBATE)
            .solsFeeAccountNumber("1")
            .ihtFormId(IHT_FORM_207)
            .willHasCodicils(YES)
            .build();

        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);

        String expectedHtml = fileSystemResourceService
            .getFileFromResourceAsString(
                "caseprogress/gop/solicitorCaseProgressSendDocumentsWillHasCodicils");
        expectedHtml = expectedHtml.replaceAll("<BRANCH/>", TaskState.CODE_BRANCH);

        String result = TaskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
            testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
            LocalDate.of(2020,10,10),
            LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    public void shouldRenderCorrectDocumentsForState_SendDocuments_GopIht217() {
        final CaseData caseData = CaseData.builder()
            .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
            .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
            .primaryApplicantIsApplying(NO)
            .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
            .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
            .solsAdditionalExecutorList(null)
            .solsWillType(GRANT_TYPE_PROBATE)
            .solsFeeAccountNumber("1")
            .ihtFormId(IHT_FORM_205)
            .iht217(YES)
            .build();

        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);

        String expectedHtml = fileSystemResourceService
            .getFileFromResourceAsString(
                "caseprogress/gop/solicitorCaseProgressSendDocumentsIHT217");
        expectedHtml = expectedHtml.replaceAll("<BRANCH/>", TaskState.CODE_BRANCH);

        String result = TaskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
            testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
            LocalDate.of(2020,10,10),
            LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    public void shouldRenderCorrectDocumentsForState_SendDocuments_WithIntestacy() {
        final CaseData caseData = CaseData.builder()
            .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
            .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
            .primaryApplicantIsApplying(NO)
            .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
            .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
            .solsAdditionalExecutorList(null)
            .solsWillType(GRANT_TYPE_INTESTACY)
            .ihtFormId(IHT_FORM_207)
            .build();

        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);

        String expectedHtml = fileSystemResourceService
            .getFileFromResourceAsString(
                "caseprogress/intestacy/solicitorCaseProgressSendDocuments");
        expectedHtml = expectedHtml.replaceAll("<BRANCH/>", TaskState.CODE_BRANCH);

        String result = TaskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
            testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
            LocalDate.of(2020,10,10),
            LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    public void shouldRenderCorrectDocumentsForState_SendDocuments_IntestacyIht217() {
        final CaseData caseData = CaseData.builder()
            .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
            .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
            .primaryApplicantIsApplying(NO)
            .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
            .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
            .solsAdditionalExecutorList(null)
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsFeeAccountNumber("1")
            .ihtFormId(IHT_FORM_205)
            .iht217(YES)
            .build();

        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);

        String expectedHtml = fileSystemResourceService
            .getFileFromResourceAsString(
                "caseprogress/intestacy/solicitorCaseProgressSendDocumentsIHT217");
        expectedHtml = expectedHtml.replaceAll("<BRANCH/>", TaskState.CODE_BRANCH);

        String result = TaskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
            testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
            LocalDate.of(2020,10,10),
            LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    public void shouldRenderCorrectDocumentsForState_SendDocuments_WithAdmonWill() {
        final CaseData caseData = CaseData.builder()
            .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
            .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
            .primaryApplicantIsApplying(NO)
            .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
            .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
            .solsAdditionalExecutorList(null)
            .solsWillType(GRANT_TYPE_ADMON)
            .solsFeeAccountNumber("1")
            .ihtFormId(IHT_FORM_207)
            .build();

        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);

        String expectedHtml = fileSystemResourceService
            .getFileFromResourceAsString(
                "caseprogress/admonwill/solicitorCaseProgressSendDocuments");
        expectedHtml = expectedHtml.replaceAll("<BRANCH/>", TaskState.CODE_BRANCH);

        String result = TaskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
            testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
            LocalDate.of(2020,10,10),
            LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    public void shouldRenderCorrectDocumentsForState_SendDocuments_AdmonWillWillHasCodicils() {
        final CaseData caseData = CaseData.builder()
            .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
            .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
            .primaryApplicantIsApplying(NO)
            .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
            .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
            .solsAdditionalExecutorList(null)
            .solsWillType(GRANT_TYPE_ADMON)
            .solsFeeAccountNumber("1")
            .willHasCodicils(YES)
            .ihtFormId(IHT_FORM_207)
            .build();

        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);

        String expectedHtml = fileSystemResourceService
            .getFileFromResourceAsString(
                "caseprogress/admonwill/solicitorCaseProgressSendDocumentsWillHasCodicils");
        expectedHtml = expectedHtml.replaceAll("<BRANCH/>", TaskState.CODE_BRANCH);

        String result = TaskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
            testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
            LocalDate.of(2020,10,10),
            LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    public void shouldRenderCorrectDocumentsForState_SendDocuments_AdmonWillIht217() {
        final CaseData caseData = CaseData.builder()
            .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
            .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
            .primaryApplicantIsApplying(NO)
            .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
            .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
            .solsAdditionalExecutorList(null)
            .solsWillType(GRANT_TYPE_PROBATE)
            .solsFeeAccountNumber("1")
            .ihtFormId(IHT_FORM_205)
            .iht217(YES)
            .build();

        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);

        String expectedHtml = fileSystemResourceService
            .getFileFromResourceAsString(
                "caseprogress/admonWill/solicitorCaseProgressSendDocumentsIHT217");
        expectedHtml = expectedHtml.replaceAll("<BRANCH/>", TaskState.CODE_BRANCH);

        String result = TaskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
            testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
            LocalDate.of(2020,10,10),
            LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }
}
