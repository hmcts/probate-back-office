package uk.gov.hmcts.probate.service.tasklist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.businessrule.AdmonWillRenunicationRule;
import uk.gov.hmcts.probate.businessrule.AuthenticatedTranslationBusinessRule;
import uk.gov.hmcts.probate.businessrule.DispenseNoticeSupportDocsRule;
import uk.gov.hmcts.probate.businessrule.IhtEstate207BusinessRule;
import uk.gov.hmcts.probate.businessrule.NoDocumentsRequiredBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA14FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA15FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA16FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA17FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.TCResolutionLodgedWithApplicationRule;
import uk.gov.hmcts.probate.model.caseprogress.TaskListState;
import uk.gov.hmcts.probate.model.caseprogress.TaskState;
import uk.gov.hmcts.probate.model.caseprogress.UrlConstants;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData.CaseDataBuilder;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.FileSystemResourceService;
import uk.gov.hmcts.probate.service.SendDocumentsRenderer;
import uk.gov.hmcts.probate.service.solicitorexecutor.NotApplyingExecutorsMapper;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ExecutorNotApplyingReason;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_ADMON;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_INTESTACY;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_PROBATE;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT207_VALUE;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT400421_VALUE;

class TaskStateRendererTest {
    @InjectMocks
    private TaskStateRenderer taskStateRenderer;
    @Mock
    private PA14FormBusinessRule pa14FormBusinessRule;
    @Mock
    private PA15FormBusinessRule pa15FormBusinessRule;
    @Mock
    private PA16FormBusinessRule pa16FormBusinessRule;
    @Mock
    private PA17FormBusinessRule pa17FormBusinessRule;
    @Mock
    private IhtEstate207BusinessRule ihtEstate207BusinessRule;
    @Mock
    private DispenseNoticeSupportDocsRule dispenseNoticeSupportDocsRule;
    @Mock
    private AuthenticatedTranslationBusinessRule authenticatedTranslationBusinessRule;
    @Mock
    private AdmonWillRenunicationRule admonWillRenunicationRule;
    @Mock
    private NotApplyingExecutorsMapper notApplyingExecutorsMapper;
    @Mock
    private SendDocumentsRenderer sendDocumentsRenderer;
    @Mock
    private TCResolutionLodgedWithApplicationRule tcResolutionLodgedWithApplicationRule;
    @Mock
    private NoDocumentsRequiredBusinessRule noDocumentsRequiredBusinessRule;


    private CaseDetails caseDetails;
    public static final Long ID = 1L;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final String IHT_FORM_207 = "IHT207";
    private static final String IHT_FORM_205 = "IHT205";
    private static final String IHT_FORM_400 = "IHT400";
    private static final String PRIMARY_APPLICANT_FIRST_NAME = "fName";
    private static final String PRIMARY_APPLICANT_SURNAME = "sName";
    private static final SolsAddress PRIMARY_APPLICANT_ADDRESS = mock(SolsAddress.class);
    private static final String PRIMARY_APPLICANT_NAME_ON_WILL = "willName";
    private static final String CHILD_ADOPTED = "ChildAdopted";
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

    @BeforeEach
    public void setup() {
        openMocks(this);
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
    void shouldRenderCorrectHtmlForState_CaseCreatedSolDtls() {
        final String expectedHtml = "<div><a href=\""
                + UrlConstants.SOLICITOR_DETAILS_URL_TEMPLATE.replaceFirst("<CASE_ID>", "9999")
                    .replaceFirst("<CASE_TYPE>", "GrantOfRepresentation")
                + "\" class=\"govuk-link\">Add Probate practitioner details</a></div>\n"
                + "<div>Add deceased details</div>\n"
                + "<div>Add application details</div>\n"
                + "<div>Review and sign legal statement and submit application</div>\n"
                + "<div>Make payment</div>\n"
                + "<div></div>\n"
                + "<div></div>\n"
                + "<div></div>\n"
                + "<div>Authenticate documents</div>\n"
                + "<div>Examine application</div>\n"
                + "<div>Issue grant of representation</div>\n"
                + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/"
                + "not-started.png\" alt=\"NOT STARTED\" title=\"NOT STARTED\" /></p>\n"
                + "</p>\n"
                + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/"
                + "not-started-welsh.png\" alt=\"HEB DDECHRAU\" title=\"HEB DDECHRAU\" /></p>\n"
                + "</p>\n"
                + "<p></p>\n"
                + "<p></p>\n"
                + "<p></p>\n"
                + "<p></p>\n"
                + "<p></p>\n"
                + "<p></p>\n"
                + "<p><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p><strong>Submitted on 01 Nov 2020</strong></p></div><div class=\"govuk-grid-column-one-third\">"
                + "&nbsp;</div></div>\n</p>\n<p></p>\n<p></p>\n<p></p>\n<p></p>\n"
                + "<p><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p><strong>Authenticated on "
                + "10 Oct 2020</strong></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "</p>\n<p></p>\n<p></p>\n<p></p>\n<p></p>\n<p></p>\n<p></p>\n";

        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_ADD_SOLICITOR_DETAILS,
                testHtml, (long) 9999, "WillLeft", "No",
                LocalDate.of(2020,10,10),
                LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectHtmlForState_CaseCreatedDeceasedDtls() {

        final String expectedHtml = "<div>Add Probate practitioner details</div>\n"
            + "<div><a href=\""
            + UrlConstants.DECEASED_DETAILS_URL_TEMPLATE.replaceFirst("<CASE_ID>", "9999")
                .replaceFirst("<CASE_TYPE>", "GrantOfRepresentation")
            + "\" class=\"govuk-link\">Add deceased details</a></div>\n"
            + "<div>Add application details</div>\n"
            + "<div>Review and sign legal statement and submit application</div>\n"
            + "<div>Make payment</div>\n"
            + "<div></div>\n"
            + "<div></div>\n"
            + "<div></div>\n"
            + "<div>Authenticate documents</div>\n"
            + "<div>Examine application</div>\n"
            + "<div>Issue grant of representation</div>\n"
            + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
            + "</p>\n"
                + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed-welsh.png\" alt=\"CWBLHAWYD\" title=\"CWBLHAWYD\" />"
                + "</p>\n</p>\n"
            + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/not-started.png\" alt=\"NOT STARTED\" title=\"NOT STARTED\" /></p>\n"
            + "</p>\n"
                + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/not-started-welsh.png\" alt=\"HEB DDECHRAU\" "
                + "title=\"HEB DDECHRAU\" /></p>\n</p>\n"
            + "<p></p>\n"
            + "<p></p>\n"
                + "<p></p>\n"
                + "<p></p>\n"
            + "<p><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
            + "<p><strong>Submitted on 01 Nov 2020</strong></p></div><div class=\"govuk-grid-column-one-third\">"
            + "&nbsp;</div></div>\n</p>\n<p></p>\n<p></p>\n"
                + "<p></p>\n"
                + "<p></p>\n"
            + "<p><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
            + "<p><strong>Authenticated on "
            + "10 Oct 2020</strong></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
            + "</p>\n<p></p>\n<p></p>\n<p></p>\n"
                + "<p></p>\n<p></p>\n<p></p>\n";

        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_ADD_DECEASED_DETAILS,
                testHtml, (long) 9999, "WillLeft", "No",
                LocalDate.of(2020,10,10),
                LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectHtmlForState_DeceasedDtlsWithNoHmrcCode() {

        final String expectedHtml = "<div>Add Probate practitioner details</div>\n"
                + "<div><a href=\""
                + UrlConstants.DECEASED_DETAILS_URL_TEMPLATE.replaceFirst("<CASE_ID>", "9999")
                    .replaceFirst("<CASE_TYPE>", "GrantOfRepresentation")
                + "\" class=\"govuk-link\">Add deceased details</a></div>\n"
                + "<div>Add application details</div>\n"
                + "<div>Review and sign legal statement and submit application</div>\n"
                + "<div>Make payment</div>\n"
                + "<div></div>\n"
                + "<div></div>\n"
                + "<div></div>\n"
                + "<div>Authenticate documents</div>\n"
                + "<div>Examine application</div>\n"
                + "<div>Issue grant of representation</div>\n"
                + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
                + "</p>\n"
                + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed-welsh.png\" alt=\"CWBLHAWYD\" title=\"CWBLHAWYD\" />"
                + "</p>\n</p>\n"
                + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/in-progress.png\" alt=\"IN PROGRESS\" title=\"IN PROGRESS\" />"
                + "</p>\n</p>\n"
                + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/in-progress-welsh.png\" alt=\"YN MYND RHAGDDO\" "
                + "title=\"YN MYND RHAGDDO\" /></p>\n</p>\n"
                + "<p></p>\n"
                + "<p></p>\n"
                + "<p></p>\n"
                + "<p></p>\n"
                + "<p><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p><strong>Submitted on 01 Nov 2020</strong></p></div><div class=\"govuk-grid-column-one-third\">"
                + "&nbsp;</div></div>\n</p>\n<p></p>\n<p></p>\n<p></p>\n<p></p>\n"
                + "<p><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
                + "<p><strong>Authenticated on "
                + "10 Oct 2020</strong></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
                + "</p>\n<p></p>\n<p></p>\n<p></p>\n<p></p>\n<p></p>\n<p></p>\n";

        CaseDataBuilder caseDataBuilder = CaseData.builder()
                .escalatedDate(LocalDate.of(2020, 1, 1))
                .hmrcLetterId(NO);
        caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_ADD_DECEASED_DETAILS,
                testHtml, (long) 9999, "WillLeft", "No",
                LocalDate.of(2020,10,10),
                LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectHtmlForState_AddAppDtls_Gop_UpdateCase() {

        final String expectedHtml = "<div>Add Probate practitioner details</div>\n"
            + "<div>Add deceased details</div>\n"
            + "<div><a href=\""
            + UrlConstants.ADD_APPLICATION_DETAILS_URL_TEMPLATE_GOP.replaceFirst("<CASE_ID>", "9999")
                .replaceFirst("<CASE_TYPE>", "GrantOfRepresentation")
            + "\" class=\"govuk-link\">Add application details</a></div>\n"
            + "<div>Review and sign legal statement and submit application</div>\n"
            + "<div>Make payment</div>\n"
            + "<div></div>\n"
            + "<div></div>\n"
            + "<div></div>\n"
            + "<div>Authenticate documents</div>\n"
            + "<div>Examine application</div>\n"
            + "<div>Issue grant of representation</div>\n"
            + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
            + "</p>\n"
                + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed-welsh.png\" alt=\"CWBLHAWYD\" title=\"CWBLHAWYD\" />"
                + "</p>\n</p>\n"
            + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
            + "</p>\n"
                + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed-welsh.png\" alt=\"CWBLHAWYD\" title=\"CWBLHAWYD\" />"
                + "</p>\n</p>\n"
            + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/in-progress.png\" alt=\"IN PROGRESS\" title=\"IN PROGRESS\" /></p>\n"
            + "</p>\n"
                + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/in-progress-welsh.png\" alt=\"YN MYND RHAGDDO\" "
                + "title=\"YN MYND RHAGDDO\" /></p>\n</p>\n"
            + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/in-progress.png\" alt=\"IN PROGRESS\" title=\"IN PROGRESS\" /></p>\n"
            + "</p>\n"
                + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/in-progress-welsh.png\" alt=\"YN MYND RHAGDDO\" "
                + "title=\"YN MYND RHAGDDO\" /></p>\n</p>\n"
            + "<p><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
            + "<p><strong>Submitted on 01 Nov 2020</strong></p></div>"
            + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
            + "</p>\n"
            + "<p></p>\n"
            + "<p></p>\n"
                + "<p></p>\n"
                + "<p></p>\n"
            + "<p><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\">"
            + "<p><strong>Authenticated on 10 Oct 2020</strong></p></div>"
            + "<div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
            + "</p>\n"
            + "<p></p>\n"
            + "<p></p>\n"
            + "<p></p>\n"
                + "<p></p>\n"
                + "<p></p>\n"
                + "<p></p>\n";

        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_ADD_APPLICATION_DETAILS,
                testHtml, (long) 9999, "WillLeft", "Yes",
                LocalDate.of(2020,10,10),
                LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectHtmlForState_AddAppDtls_Gop_NotUpdatingCase() {

        final String expectedHtml = "<div>Add Probate practitioner details</div>\n"
            + "<div>Add deceased details</div>\n"
            + "<div><a href=\""
            + UrlConstants.ADD_APPLICATION_DETAILS_URL_TEMPLATE_GOP.replaceFirst("<CASE_ID>", "9999")
                .replaceFirst("<CASE_TYPE>", "GrantOfRepresentation")
            + "\" class=\"govuk-link\">Add application details</a></div>\n"
            + "<div>Review and sign legal statement and submit application</div>\n"
            + "<div>Make payment</div>\n"
            + "<div></div>\n"
            + "<div></div>\n"
            + "<div></div>\n"
            + "<div>Authenticate documents</div>\n"
            + "<div>Examine application</div>\n"
            + "<div>Issue grant of representation</div>\n"
            + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
            + "</p>\n"
                + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed-welsh.png\" alt=\"CWBLHAWYD\" title=\"CWBLHAWYD\" />"
                + "</p>\n</p>\n"
            + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
            + "</p>\n"
                + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed-welsh.png\" alt=\"CWBLHAWYD\" title=\"CWBLHAWYD\" />"
                + "</p>\n</p>\n"
            + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/not-started.png\" alt=\"NOT STARTED\" title=\"NOT STARTED\" /></p>\n"
            + "</p>\n"
                + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/not-started-welsh.png\" alt=\"HEB DDECHRAU\" "
                + "title=\"HEB DDECHRAU\" /></p>\n</p>\n"
            + "<p></p>\n"
                + "<p></p>\n"
            + "<p><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p><strong>"
            + "Submitted on 01 Nov 2020"
            + "</strong></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
            + "</p>\n"
            + "<p></p>\n"
            + "<p></p>\n"
                + "<p></p>\n"
                + "<p></p>\n"
            + "<p><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p><strong>Authenticated "
            + "on 10 Oct 2020"
            + "</strong></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
            + "</p>\n<p></p>\n<p></p>\n<p></p>\n<p></p>\n<p></p>\n<p></p>\n";

        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_ADD_APPLICATION_DETAILS,
                testHtml, (long) 9999, "WillLeft", "No",
                LocalDate.of(2020,10,10),
                LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectHtmlForState_AddAppDtls_Intestacy() {

        final String expectedHtml = "<div>Add Probate practitioner details</div>\n"
            + "<div>Add deceased details</div>\n"
            + "<div><a href=\""
            + UrlConstants.ADD_APPLICATION_DETAILS_URL_TEMPLATE_INTESTACY
                .replaceFirst("<CASE_ID>", "9999")
                .replaceFirst("<CASE_TYPE>", "GrantOfRepresentation")
            + "\" class=\"govuk-link\">Add application details</a></div>\n"
            + "<div>Review and sign legal statement and submit application</div>\n"
            + "<div>Make payment</div>\n"
            + "<div></div>\n"
            + "<div></div>\n"
            + "<div></div>\n"
            + "<div>Authenticate documents</div>\n"
            + "<div>Examine application</div>\n"
            + "<div>Issue grant of representation</div>\n"
            + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
            + "</p>\n"
                + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed-welsh.png\" alt=\"CWBLHAWYD\" title=\"CWBLHAWYD\" />"
                + "</p>\n</p>\n"
            + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
            + "</p>\n"
                + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed-welsh.png\" alt=\"CWBLHAWYD\" title=\"CWBLHAWYD\" />"
                + "</p>\n</p>\n"
            + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/not-started.png\" alt=\"NOT STARTED\" title=\"NOT STARTED\" /></p>\n"
            + "</p>\n"
                + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/not-started-welsh.png\" alt=\"HEB DDECHRAU\" "
                + "title=\"HEB DDECHRAU\" /></p>\n</p>\n"
            + "<p></p>\n"
                + "<p></p>\n"
            + "<p><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p><strong>Submitted on "
            + "01 Nov 2020"
            + "</strong></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
            + "</p>\n"
            + "<p></p>\n"
            + "<p></p>\n"
                + "<p></p>\n"
                + "<p></p>\n"
            + "<p><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p><strong>"
            + "Authenticated on 10 Oct 2020"
            + "</strong></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
            + "</p>\n<p></p>\n<p></p>\n<p></p>\n<p></p>\n<p></p>\n<p></p>\n";

        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_ADD_APPLICATION_DETAILS,
                testHtml, (long) 9999, "NoWill", null,
                LocalDate.of(2020,10,10),
                LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectHtmlForState_AddAppDtls_AdmonWill() {

        final String expectedHtml = "<div>Add Probate practitioner details</div>\n"
            + "<div>Add deceased details</div>\n"
            + "<div><a href=\""
            + UrlConstants.ADD_APPLICATION_DETAILS_URL_TEMPLATE_ADMON_WILL
                .replaceFirst("<CASE_ID>", "9999")
                .replaceFirst("<CASE_TYPE>", "GrantOfRepresentation")
            + "\" class=\"govuk-link\">Add application details</a></div>\n"
            + "<div>Review and sign legal statement and submit application</div>\n"
            + "<div>Make payment</div>\n"
            + "<div></div>\n"
            + "<div></div>\n"
            + "<div></div>\n"
            + "<div>Authenticate documents</div>\n"
            + "<div>Examine application</div>\n"
            + "<div>Issue grant of representation</div>\n"
            + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
            + "</p>\n"
                + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed-welsh.png\" alt=\"CWBLHAWYD\" "
                + "title=\"CWBLHAWYD\" /></p>\n</p>\n"
            + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/completed.png\" alt=\"COMPLETED\" title=\"COMPLETED\" /></p>\n"
            + "</p>\n"
                + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/completed-welsh.png\" alt=\"CWBLHAWYD\" title=\"CWBLHAWYD\" />"
                + "</p>\n</p>\n"
            + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
            + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
            + TaskState.CODE_BRANCH
            + "/src/main/resources/statusImages/not-started.png\" alt=\"NOT STARTED\" title=\"NOT STARTED\" /></p>\n"
            + "</p>\n"
                + "<p><p align=\"right\"><img width=\"114px\" height=\"31px\" "
                + "src=\"https://raw.githubusercontent.com/hmcts/probate-back-office/"
                + TaskState.CODE_BRANCH
                + "/src/main/resources/statusImages/not-started-welsh.png\" alt=\"HEB DDECHRAU\" "
                + "title=\"HEB DDECHRAU\" /></p>\n</p>\n"
            + "<p></p>\n"
                + "<p></p>\n"
            + "<p><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p><strong>Submitted on "
            + "01 Nov 2020"
            + "</strong></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
            + "</p>\n"
            + "<p></p>\n"
            + "<p></p>\n"
                + "<p></p>\n"
                + "<p></p>\n"
            + "<p><div class=\"govuk-grid-row\"><div class=\"govuk-grid-column-two-thirds\"><p><strong>Authenticated "
            + "on 10 Oct 2020"
            + "</strong></p></div><div class=\"govuk-grid-column-one-third\">&nbsp;</div></div>\n"
            + "</p>\n"
            + "<p></p>\n"
            + "<p></p>\n"
            + "<p></p>\n"
                + "<p></p>\n"
                + "<p></p>\n"
                + "<p></p>\n";

        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_ADD_APPLICATION_DETAILS,
                testHtml, (long) 9999, "WillLeftAnnexed", "No",
                LocalDate.of(2020,10,10),
                LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectDocumentsForState_SendDocuments_GopWithPA17Form() {
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
            .titleAndClearingType("TCTPartAllRenouncing")
            .build();
        String expectedHtml = fileSystemResourceService
            .getFileFromResourceAsString("caseprogress/gop/solicitorCaseProgressSendDocumentsWithPA17Form");
        expectedHtml = expectedHtml.replaceAll("<BRANCH/>", TaskState.CODE_BRANCH);
        when(pa17FormBusinessRule.isApplicable(caseData)).thenReturn(true);
        when(sendDocumentsRenderer.getPA17FormText()).thenReturn("<a href=\"https://www.gov"
            + ".uk/government/publications/form-pa17-give-up-probate-executor-rights-for-legal-professionals\" "
            + "target=\"_blank\">Give up probate executor rights for probate practitioners paper form (PA17)</a>");
        when(sendDocumentsRenderer.getPA17FormTextWelsh()).thenReturn("<a href=\"https://www.gov"
                + ".uk/government/publications/form-pa17-give-up-probate-executor-rights-for-legal-professionals\" "
                + "target=\"_blank\">Ffurflen bapur rhoi’r gorau i hawliau ysgutor profiant ar gyfer ymarferwyr "
                + "profiant (PA17)</a>");
        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);

        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
            testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
            LocalDate.of(2020,10,10),
            LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectDocumentsForState_SendDocuments_GopWithPA14Form() {
        when(additionalExecutorNotApplyingRenounced1.getNotApplyingExecutorName()).thenReturn("Executor One");
        when(additionalExecutorNotApplyingRenounced1.getNotApplyingExecutorReason()).thenReturn("MentallyIncapable");

        when(additionalExecutorNotApplyingRenounced2.getNotApplyingExecutorName()).thenReturn("Executor Two");
        when(additionalExecutorNotApplyingRenounced2.getNotApplyingExecutorReason()).thenReturn("MentallyIncapable");

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
            .titleAndClearingType("TCTPartAllRenouncing")
            .build();

        String expectedHtml = fileSystemResourceService
            .getFileFromResourceAsString("caseprogress/gop/solicitorCaseProgressSendDocumentsWithPA14Form");
        expectedHtml = expectedHtml.replaceAll("<BRANCH/>", TaskState.CODE_BRANCH);
        when(pa15FormBusinessRule.isApplicable(caseData)).thenReturn(true);
        List<AdditionalExecutorNotApplying> all = new ArrayList<>();
        AdditionalExecutorNotApplying single =
            AdditionalExecutorNotApplying.builder().notApplyingExecutorName("Tim Smith")
                .notApplyingExecutorReason("MentallyIncapable")
                .build();
        all.add(single);
        when(notApplyingExecutorsMapper.getAllExecutorsNotApplying(caseData, "Renunciation")).thenReturn(all);
        when(sendDocumentsRenderer.getPA15NotApplyingExecutorText("Tim Smith")).thenReturn("<a href=\"https://www.gov"
            + ".uk/government/publications/form-pa14-medical-certificate-probate\" target=\"_blank\">Medical "
            + "certificate completed by a health professional</a> (PA14) for Tim Smith");
        when(sendDocumentsRenderer.getPA15NotApplyingExecutorTextWelsh("Tim Smith")).thenReturn("<a href=\"https://www.gov"
                + ".uk/government/publications/form-pa14-medical-certificate-probate\" target=\"_blank\">Tystysgrif "
                + "feddygol wedi'i chwblhau gan weithiwr iechyd proffesiynol</a> (PA14) ar gyfer Tim Smith");

        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);
        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
            testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
            LocalDate.of(2020,10,10),
            LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectDocumentsForState_SendDocuments_GopWithPA15Form() {
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
            .titleAndClearingType("TCTPartAllRenouncing")
            .build();

        String expectedHtml = fileSystemResourceService
            .getFileFromResourceAsString("caseprogress/gop/solicitorCaseProgressSendDocumentsWithPA15Form");
        expectedHtml = expectedHtml.replaceAll("<BRANCH/>", TaskState.CODE_BRANCH);
        when(pa15FormBusinessRule.isApplicable(caseData)).thenReturn(true);
        List<AdditionalExecutorNotApplying> all = new ArrayList<>();
        AdditionalExecutorNotApplying single =
            AdditionalExecutorNotApplying.builder().notApplyingExecutorName("Tim Smith")
                .notApplyingExecutorReason("Renunciation")
                .build();
        all.add(single);
        when(notApplyingExecutorsMapper.getAllExecutorsNotApplying(caseData, "Renunciation")).thenReturn(all);
        when(sendDocumentsRenderer.getPA15NotApplyingExecutorText("Tim Smith")).thenReturn("<a href=\"https://www.gov"
            + ".uk/government/publications/form-pa15-give-up-probate-executor-rights\" target=\"_blank\">Give up "
            + "probate administrator rights paper form</a> (PA15) for Tim Smith");
        when(sendDocumentsRenderer.getPA15NotApplyingExecutorTextWelsh("Tim Smith")).thenReturn("<a href=\"https://www.gov"
                + ".uk/government/publications/form-pa15-give-up-probate-executor-rights\" target=\"_blank\">Ffurflen "
                + "bapur rhoi’r gorau i hawliau gweinyddwr profiant</a> (PA15) ar gyfer Tim Smith");

        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);
        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
            testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
            LocalDate.of(2020,10,10),
            LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectDocumentsForState_SendDocuments_GopWillHasCodicils() {
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

        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
            testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
            LocalDate.of(2020,10,10),
            LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectDocumentsForState_SendDocuments_GopIht217() {
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

        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
            testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
            LocalDate.of(2020,10,10),
            LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectDocumentsForState_SendDocuments_GopIht400() {
        final CaseData caseData = CaseData.builder()
                .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
                .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantIsApplying(NO)
                .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
                .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
                .solsAdditionalExecutorList(null)
                .solsWillType(GRANT_TYPE_PROBATE)
                .solsFeeAccountNumber("1")
                .ihtFormId(IHT_FORM_400)
                .iht217(YES)
                .build();

        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);

        String expectedHtml = fileSystemResourceService
                .getFileFromResourceAsString(
                        "caseprogress/gop/solicitorCaseProgressSendDocumentsIHT400");
        expectedHtml = expectedHtml.replaceAll("<BRANCH/>", TaskState.CODE_BRANCH);

        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
                testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
                LocalDate.of(2020,10,10),
                LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectDocumentsForState_SendDocuments_WithIntestacy() {
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

        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
                testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
                LocalDate.of(2020,10,10),
                LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectDocumentsForState_SendDocuments_WithIntestacyNoDoc() {
        final CaseData caseData = CaseData.builder()
            .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
            .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
            .primaryApplicantIsApplying(NO)
            .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
            .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
            .solsAdditionalExecutorList(null)
            .solsWillType(GRANT_TYPE_INTESTACY)
            .ihtFormId(IHT_FORM_207)
            .evidenceHandled(NO)
            .build();

        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);

        String expectedHtml = fileSystemResourceService
            .getFileFromResourceAsString(
                "caseprogress/intestacy/solicitorCaseProgressSendDocumentsIntestacyNoDocs");
        expectedHtml = expectedHtml.replaceAll("<BRANCH/>", TaskState.CODE_BRANCH);

        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
            testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
            LocalDate.of(2020,10,10),
            LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectDocumentsForState_SendDocuments_WithIntestacyAndPA16Form() {
        final CaseData caseData = CaseData.builder()
            .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
            .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
            .primaryApplicantIsApplying(NO)
            .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
            .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
            .solsAdditionalExecutorList(null)
            .solsWillType(GRANT_TYPE_INTESTACY)
            .ihtFormId(IHT_FORM_207)
            .solsApplicantRelationshipToDeceased(CHILD_ADOPTED)
            .solsApplicantSiblings(NO)
            .solsSpouseOrCivilRenouncing(YES)
            .build();
        String expectedHtml = fileSystemResourceService
            .getFileFromResourceAsString(
                "caseprogress/intestacy/solicitorCaseProgressSendDocumentsWithPA16Form");
        expectedHtml = expectedHtml.replaceAll("<BRANCH/>", TaskState.CODE_BRANCH);

        when(pa16FormBusinessRule.isApplicable(caseData)).thenReturn(true);
        when(sendDocumentsRenderer.getPA16FormText()).thenReturn("<a href=\"https://www.gov"
            + ".uk/government/publications/form-pa16-give-up-probate-administrator-rights\" target=\"_blank\">Give up"
            + " probate administrator rights paper form (PA16)</a>");
        when(sendDocumentsRenderer.getPA16FormTextWelsh()).thenReturn("<a href=\"https://www.gov"
                + ".uk/government/publications/form-pa16-give-up-probate-administrator-rights\" target=\"_blank\">"
                + "Ffurflen bapur rhoi’r gorau i hawliau gweinyddwr profiant (PA16)</a>");
        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);

        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
            testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
            LocalDate.of(2020,10,10),
            LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectDocumentsForState_SendDocuments_WithIntestacyAndNoPA16Form() {
        final CaseData caseData = CaseData.builder()
            .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
            .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
            .primaryApplicantIsApplying(NO)
            .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
            .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
            .solsAdditionalExecutorList(null)
            .solsWillType(GRANT_TYPE_INTESTACY)
            .ihtFormId(IHT_FORM_207)
            .solsApplicantRelationshipToDeceased(CHILD_ADOPTED)
            .solsApplicantSiblings(YES)
            .solsSpouseOrCivilRenouncing(YES)
            .build();

        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);

        String expectedHtml = fileSystemResourceService
            .getFileFromResourceAsString(
                "caseprogress/intestacy/solicitorCaseProgressSendDocumentsNoPA16Form");
        expectedHtml = expectedHtml.replaceAll("<BRANCH/>", TaskState.CODE_BRANCH);

        when(pa16FormBusinessRule.isApplicable(caseData)).thenReturn(false);
        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
            testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
            LocalDate.of(2020,10,10),
            LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectDocumentsForState_SendDocuments_WithIhtEstate207() {
        final CaseData caseData = CaseData.builder()
            .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
            .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
            .primaryApplicantIsApplying(NO)
            .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
            .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
            .solsAdditionalExecutorList(null)
            .solsWillType(GRANT_TYPE_PROBATE)
            .ihtFormId(null)
            .ihtFormEstateValuesCompleted(YES)
            .ihtFormEstate(IHT207_VALUE)
            .build();

        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);

        String expectedHtml = fileSystemResourceService
            .getFileFromResourceAsString(
                "caseprogress/gop/solicitorCaseProgressSendDocumentsIHTEstate207");
        expectedHtml = expectedHtml.replaceAll("<BRANCH/>", TaskState.CODE_BRANCH);

        when(ihtEstate207BusinessRule.isApplicable(any(CaseData.class))).thenReturn(true);
        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
            testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
            LocalDate.of(2020,10,10),
            LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectDocumentsForState_AuthenticateDocuments() {
        final CaseData caseData = CaseData.builder()
                .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
                .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantIsApplying(NO)
                .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
                .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
                .solsAdditionalExecutorList(null)
                .solsWillType(GRANT_TYPE_PROBATE)
                .ihtFormId(null)
                .ihtFormEstateValuesCompleted(YES)
                .ihtFormEstate(IHT400421_VALUE)
                .attachDocuments(YES)
                .build();

        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);

        String expectedHtml = fileSystemResourceService
                .getFileFromResourceAsString(
                        "caseprogress/gop/solicitorCaseProgressAuthenticateDocumentsSend");
        expectedHtml = expectedHtml.replaceAll("<BRANCH/>", TaskState.CODE_BRANCH);

        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_AUTHENTICATE_DOCUMENTS,
                testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
                LocalDate.of(2022,10,10),
                LocalDate.of(2022,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectDocumentsForState_SendDocuments() {
        final CaseData caseData = CaseData.builder()
                .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
                .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantIsApplying(NO)
                .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
                .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
                .solsAdditionalExecutorList(null)
                .solsWillType(GRANT_TYPE_PROBATE)
                .ihtFormId(null)
                .ihtFormEstateValuesCompleted(YES)
                .ihtFormEstate(IHT400421_VALUE)
                .attachDocuments(YES)
                .build();

        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);

        String expectedHtml = fileSystemResourceService
                .getFileFromResourceAsString(
                        "caseprogress/gop/solicitorCaseProgressSendDocumentsState");
        expectedHtml = expectedHtml.replaceAll("<BRANCH/>", TaskState.CODE_BRANCH);

        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
                testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
                LocalDate.of(2022,10,10),
                LocalDate.of(2022,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectDocumentsForState_SendDocuments_WithIhtEstate400421() {
        final CaseData caseData = CaseData.builder()
            .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
            .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
            .primaryApplicantIsApplying(NO)
            .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
            .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
            .solsAdditionalExecutorList(null)
            .solsWillType(GRANT_TYPE_PROBATE)
            .ihtFormId(null)
            .ihtFormEstateValuesCompleted(YES)
            .ihtFormEstate(IHT400421_VALUE)
            .build();

        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);

        String expectedHtml = fileSystemResourceService
            .getFileFromResourceAsString(
                "caseprogress/gop/solicitorCaseProgressSendDocumentsIHTEstate400421");
        expectedHtml = expectedHtml.replaceAll("<BRANCH/>", TaskState.CODE_BRANCH);

        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
            testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
            LocalDate.of(2020,10,10),
            LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectDocumentsForState_SendDocuments_WithIhtEstateCompletedNo() {
        final CaseData caseData = CaseData.builder()
            .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
            .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
            .primaryApplicantIsApplying(NO)
            .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
            .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
            .solsAdditionalExecutorList(null)
            .solsWillType(GRANT_TYPE_PROBATE)
            .ihtFormId(null)
            .ihtFormEstateValuesCompleted(NO)
            .build();

        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);

        String expectedHtml = fileSystemResourceService
            .getFileFromResourceAsString(
                "caseprogress/gop/solicitorCaseProgressSendDocumentsIHTEstateCompletedNo");
        expectedHtml = expectedHtml.replaceAll("<BRANCH/>", TaskState.CODE_BRANCH);

        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
            testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
            LocalDate.of(2020,10,10),
            LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectDocumentsForState_SendDocuments_IntestacyIht217() {
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

        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
            testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
            LocalDate.of(2020,10,10),
            LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectDocumentsForState_SendDocuments_WithAdmonWill() {
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

        final CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);

        String expectedHtml = fileSystemResourceService
            .getFileFromResourceAsString(
                "caseprogress/admonwill/solicitorCaseProgressSendDocuments");
        expectedHtml = expectedHtml.replaceAll("<BRANCH/>", TaskState.CODE_BRANCH);
        when(admonWillRenunicationRule.isApplicable(caseData)).thenReturn(true);
        when(sendDocumentsRenderer.getAdmonWillRenunciationText()).thenReturn("if applicable, send us the appropriate"
            + " renunciation form <a href=\"https://www.gov.uk/government/publications/form-pa15-give-up-probate-"
            + "executor-rights\" target=\"_blank\" rel=\"noopener noreferrer\" class=\"govuk-link\">PA15</a> /"
            + " <a href=\"https://www.gov.uk/government/publications/form-pa17-give-up-probate-executor-rights-for"
            + "-probate-practitioners\" target=\"_blank\" rel=\"noopener noreferrer\" class=\"govuk-link\">PA17</a>"
            + " for executors who have renounced their right to apply");

        when(sendDocumentsRenderer.getAdmonWillRenunciationTextWelsh()).thenReturn("os yw’n berthnasol, anfonwch "
                + "y ffurflen ymwrthod briodol atom <a href=\"https://www.gov.uk/government/publications/form-pa15-give-up-probate-"
                + "executor-rights\" target=\"_blank\" rel=\"noopener noreferrer\" class=\"govuk-link\">PA15</a> /"
                + " <a href=\"https://www.gov.uk/government/publications/form-pa17-give-up-probate-executor-rights-for"
                + "-probate-practitioners\" target=\"_blank\" rel=\"noopener noreferrer\" class=\"govuk-link\">PA17</a>"
                + " ar gyfer ysgutorion sydd wedi rhoi’r gorau i’w hawl i wneud cais");

        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
            testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
            LocalDate.of(2020,10,10),
            LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectDocumentsForState_SendDocuments_AdmonWillWillHasCodicils() {
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

        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
            testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
            LocalDate.of(2020,10,10),
            LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectDocumentsForState_SendDocuments_AdmonWillIht217() {
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
                "caseprogress/admonwill/solicitorCaseProgressSendDocumentsIHT217");
        expectedHtml = expectedHtml.replaceAll("<BRANCH/>", TaskState.CODE_BRANCH);

        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
            testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
            LocalDate.of(2020,10,10),
            LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectDocumentForState_SendDocuments_GopTcResolutionLodgedWithApplication() {
        final CaseData caseData = CaseData.builder()
            .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
            .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
            .primaryApplicantIsApplying(YES)
            .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
            .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
            .solsAdditionalExecutorList(null)
            .ihtFormId(IHT_FORM_207)
            .solsWillType(GRANT_TYPE_PROBATE)
            .solsFeeAccountNumber("1")
            .titleAndClearingType("TCTTrustCorpResWithApp")
            .build();

        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);

        String expectedHtml = fileSystemResourceService
            .getFileFromResourceAsString(
                "caseprogress/gop/solicitorCaseProgressSendDocumentsCopyOfResolution");
        expectedHtml = expectedHtml.replaceAll("<BRANCH/>", TaskState.CODE_BRANCH);

        when(tcResolutionLodgedWithApplicationRule.isApplicable(any(CaseData.class))).thenReturn(true);

        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
            testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
            LocalDate.of(2020,10,10),
            LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectDocumentsForState_SendDocuments_NoEnglishWill() {

        final CaseData caseData = CaseData.builder()
            .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
            .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
            .primaryApplicantIsApplying(YES)
            .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
            .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
            .solsAdditionalExecutorList(null)
            .ihtFormId(IHT_FORM_207)
            .solsWillType(GRANT_TYPE_PROBATE)
            .solsFeeAccountNumber("1")
            .titleAndClearingType("TCTTrustCorpResWithApp")
            .englishWill(NO)
            .build();

        String expectedHtml = fileSystemResourceService
            .getFileFromResourceAsString(
                "caseprogress/gop/solicitorCaseProgressSendDocumentsWithAuthenticatedTranslationOfWill");
        expectedHtml = expectedHtml.replaceAll("<BRANCH/>", TaskState.CODE_BRANCH);
        when(pa17FormBusinessRule.isApplicable(caseData)).thenReturn(false);
        when(authenticatedTranslationBusinessRule.isApplicable(caseData)).thenReturn(true);
        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);
        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
            testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
            LocalDate.of(2020,10,10),
            LocalDate.of(2020,11, 1), caseDetails);

        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectDocumentsForState_SendDocuments_DispenseNotice() {

        final CaseData caseData = CaseData.builder()
            .primaryApplicantForenames(PRIMARY_APPLICANT_FIRST_NAME)
            .primaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
            .primaryApplicantIsApplying(NO)
            .primaryApplicantAddress(PRIMARY_APPLICANT_ADDRESS)
            .primaryApplicantAlias(PRIMARY_APPLICANT_NAME_ON_WILL)
            .solsAdditionalExecutorList(null)
            .solsWillType(GRANT_TYPE_PROBATE)
            .solsFeeAccountNumber("1")
            .titleAndClearingType("TCTTrustCorpResWithApp")
            .englishWill(NO)
            .dispenseWithNotice(YES)
            .dispenseWithNoticeSupportingDocs("document1, document2")
            .build();

        String expectedHtml = fileSystemResourceService
            .getFileFromResourceAsString(
                "caseprogress/gop/solicitorCaseProgressSendDocumentsWithDispenseNoticeSupportDocs");
        expectedHtml = expectedHtml.replaceAll("<BRANCH/>", TaskState.CODE_BRANCH);
        when(dispenseNoticeSupportDocsRule.isApplicable(caseData)).thenReturn(true);
        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);
        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_SEND_DOCUMENTS,
            testHtml, (long) 9999, caseDetails.getData().getSolsWillType(), "No",
            LocalDate.of(2020,10,10),
            LocalDate.of(2020,11, 1), caseDetails);
        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderEmptySendDocsDetailsWhenNoDocumentsRequired() {
        String expectedHtml = fileSystemResourceService
                .getFileFromResourceAsString(
                        "caseprogress/intestacy/SendDocsDetailsWhenNoDocumentsRequired");
        when(noDocumentsRequiredBusinessRule.isApplicable(any())).thenReturn(true);
        String result = taskStateRenderer.renderSendDocsDetails(TaskState.IN_PROGRESS, "", mock(CaseDetails.class));
        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectPaymentStateNotStarted() {
        String expectedHtml = fileSystemResourceService
                .getFileFromResourceAsString(
                        "caseprogress/gop/PaymentNotStarted");
        expectedHtml = expectedHtml.replaceAll("<BRANCH/>", TaskState.CODE_BRANCH);
        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_MAKE_PAYMENT,
                testHtml, (long) 9999, "WillLeft", "No",
                LocalDate.of(2020,10,10),
                LocalDate.of(2020,11, 1), caseDetails);
        assertEquals(expectedHtml, result);
    }

    @Test
    void shouldRenderCorrectPaymentStateInProgress() {
        String expectedHtml = fileSystemResourceService
                .getFileFromResourceAsString(
                        "caseprogress/gop/PaymentInProgress");
        expectedHtml = expectedHtml.replaceAll("<BRANCH/>", TaskState.CODE_BRANCH);
        String result = taskStateRenderer.renderByReplace(TaskListState.TL_STATE_PAYMENT_ATTEMPTED,
                testHtml, (long) 9999, "WillLeft", "No",
                LocalDate.of(2020,10,10),
                LocalDate.of(2020,11, 1), caseDetails);
        assertEquals(expectedHtml, result);
    }

}
