package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.probate.businessrule.IhtEstate207BusinessRule;
import uk.gov.hmcts.probate.businessrule.NoDocumentsRequiredBusinessRule;
import uk.gov.hmcts.probate.changerule.ApplicantSiblingsRule;
import uk.gov.hmcts.probate.changerule.DiedOrNotApplyingRule;
import uk.gov.hmcts.probate.changerule.EntitledMinorityRule;
import uk.gov.hmcts.probate.changerule.ExecutorsRule;
import uk.gov.hmcts.probate.changerule.ImmovableEstateRule;
import uk.gov.hmcts.probate.changerule.LifeInterestRule;
import uk.gov.hmcts.probate.changerule.MinorityInterestRule;
import uk.gov.hmcts.probate.changerule.RenouncingRule;
import uk.gov.hmcts.probate.changerule.ResiduaryRule;
import uk.gov.hmcts.probate.changerule.SolsExecutorRule;
import uk.gov.hmcts.probate.changerule.SpouseOrCivilRule;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.Deceased;
import uk.gov.hmcts.probate.model.ccd.Executor;
import uk.gov.hmcts.probate.model.ccd.Fee;
import uk.gov.hmcts.probate.model.ccd.InheritanceTax;
import uk.gov.hmcts.probate.model.ccd.ProbateAddress;
import uk.gov.hmcts.probate.model.ccd.Solicitor;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.AfterSubmitCallbackResponse;
import uk.gov.hmcts.probate.model.template.MarkdownTemplate;
import uk.gov.hmcts.probate.model.template.TemplateResponse;
import uk.gov.hmcts.probate.service.template.markdown.MarkdownDecoratorService;
import uk.gov.hmcts.probate.service.template.markdown.MarkdownSubstitutionService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT207_VALUE;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT400421_VALUE;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT400_VALUE;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.NOT_APPLICABLE_VALUE;

class ConfirmationResponseServiceTest {

    private static final String CONFIRMATION_BODY = "someBody";
    private static final String GRANT_TYPE_PROBATE = "WillLeft";
    private static final String GRANT_TYPE_INTESTACY = "NoWill";
    private static final String GRANT_TYPE_ADMON = "WillLeftAnnexed";
    private final List<Executor> executorsList = new ArrayList<>();
    private ConfirmationResponseService underTest;
    @Mock
    private ApplicantSiblingsRule applicantSiblingsRuleMock;
    @Mock
    private DiedOrNotApplyingRule diedOrNotApplyingRuleMock;
    @Mock
    private EntitledMinorityRule entitledMinorityRuleMock;
    @Mock
    private ExecutorsRule executorsRuleMock;
    @Mock
    private ImmovableEstateRule immovableEstateRule;
    @Mock
    private LifeInterestRule lifeInterestRuleMock;
    @Mock
    private MinorityInterestRule minorityInterestRuleMock;
    @Mock
    private RenouncingRule renouncingRuleMock;
    @Mock
    private ResiduaryRule residuaryRuleMock;
    @Mock
    private SolsExecutorRule solsExecutorRuleMock;
    @Mock
    private SpouseOrCivilRule spouseOrCivilRuleMock;
    @Mock
    private IhtEstate207BusinessRule ihtEstate207BusinessRuleMock;
    @Mock
    private NoDocumentsRequiredBusinessRule noDocumentsRequiredBusinessRule;
    @Mock
    private CallbackRequest callbackRequestMock;
    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private CaseData caseDataMock;
    @Mock
    private TemplateResponse willBodyTemplateResponseMock;
    @Mock
    private MarkdownSubstitutionService markdownSubstitutionServiceMock;
    @Mock
    private MarkdownDecoratorService markdownDecoratorService;
    @Mock
    private MessageResourceService messageResourceServiceMock;
    @Mock
    private Executor executorMock;
    @Mock
    private Executor renouncingExecutorMock;
    @Mock
    private Executor deadBeforeExecutorMock;
    @Mock
    private Executor deadAfterExecutorMock;
    @Mock
    private SolsAddress solsAddressMock;
    @Mock
    private ProbateAddress probateAddressMock;
    @Captor
    private ArgumentCaptor<Map<String, String>> nextStepsKeyValueMap;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        underTest = new ConfirmationResponseService(messageResourceServiceMock, markdownSubstitutionServiceMock,
            markdownDecoratorService,
            applicantSiblingsRuleMock, diedOrNotApplyingRuleMock, entitledMinorityRuleMock,
            executorsRuleMock, immovableEstateRule, lifeInterestRuleMock, minorityInterestRuleMock,
            renouncingRuleMock, residuaryRuleMock, solsExecutorRuleMock, spouseOrCivilRuleMock,
            ihtEstate207BusinessRuleMock, noDocumentsRequiredBusinessRule);
        ReflectionTestUtils.setField(underTest, "templatesDirectory", "templates/markdown/");

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(willBodyTemplateResponseMock.getTemplate()).thenReturn(CONFIRMATION_BODY);

        when(executorMock.isApplying()).thenReturn(true);
        when(messageResourceServiceMock.getMessage(anyString())).thenReturn("someMessage1:someMessage2");
        when(renouncingExecutorMock.isApplying()).thenReturn(false);
        when(renouncingExecutorMock.getReasonNotApplying()).thenReturn("Renunciation");
        when(deadBeforeExecutorMock.isApplying()).thenReturn(false);
        when(deadBeforeExecutorMock.getReasonNotApplying()).thenReturn("DiedBefore");
        when(deadAfterExecutorMock.isApplying()).thenReturn(false);
        when(deadAfterExecutorMock.getReasonNotApplying()).thenReturn("DiedAfter");
    }

    @Test
    void shouldStopWillConfirmationForApplicantSiblings() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(applicantSiblingsRuleMock.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(applicantSiblingsRuleMock.getConfirmationBodyMessageKey()).thenReturn("someMessage1:someMessage2");
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_INTESTACY);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    void shouldNOTStopApplicantSiblingsConfirmation() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(applicantSiblingsRuleMock.isChangeNeeded(caseDataMock)).thenReturn(false);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertNull(afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    void shouldStopWillConfirmationForImmovableEstateIntestacy() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(immovableEstateRule.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(immovableEstateRule.getConfirmationBodyMessageKey()).thenReturn("someMessage1:someMessage2");
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_INTESTACY);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    void shouldStopWillConfirmationForImmovableEstateAdmon() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(immovableEstateRule.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(immovableEstateRule.getConfirmationBodyMessageKey()).thenReturn("someMessage1:someMessage2");
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_ADMON);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    void shouldNOTStopImmovableEstateConfirmation() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(immovableEstateRule.isChangeNeeded(caseDataMock)).thenReturn(false);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertNull(afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    void shouldStopWillConfirmationForDiedOrNotApplying() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(diedOrNotApplyingRuleMock.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(diedOrNotApplyingRuleMock.getConfirmationBodyMessageKey()).thenReturn("someMessage1:someMessage2");
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_ADMON);


        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    void shouldNOTStopDiedOrNotApplyingConfirmation() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(diedOrNotApplyingRuleMock.isChangeNeeded(caseDataMock)).thenReturn(false);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertNull(afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    void shouldStopWillConfirmationForEntitledMinority() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(entitledMinorityRuleMock.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(entitledMinorityRuleMock.getConfirmationBodyMessageKey()).thenReturn("someMessage1:someMessage2");
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_ADMON);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    void shouldNOTStopEntitledMinorityConfirmation() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(entitledMinorityRuleMock.isChangeNeeded(caseDataMock)).thenReturn(false);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertNull(afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    void shouldStopWillConfirmationForExecutor() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(executorsRuleMock.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(executorsRuleMock.getConfirmationBodyMessageKey()).thenReturn("someMessage1:someMessage2");
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_PROBATE);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    void shouldNOTStopExecutorConfirmation() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(executorsRuleMock.isChangeNeeded(caseDataMock)).thenReturn(false);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertNull(afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    void shouldStopWillConfirmationForLifeInterest() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(lifeInterestRuleMock.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(lifeInterestRuleMock.getConfirmationBodyMessageKey()).thenReturn("someMessage1:someMessage2");
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_ADMON);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    void shouldNOTStopLifeInterestConfirmation() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(lifeInterestRuleMock.isChangeNeeded(caseDataMock)).thenReturn(false);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertNull(afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    void shouldStopWillConfirmationForMinorityInterest() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(minorityInterestRuleMock.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(minorityInterestRuleMock.getConfirmationBodyMessageKey()).thenReturn("someMessage1:someMessage2");
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_INTESTACY);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    void shouldNOTStopMinorityInterestConfirmation() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(minorityInterestRuleMock.isChangeNeeded(caseDataMock)).thenReturn(false);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertNull(afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    void shouldStopWillConfirmationForRenouncing() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(renouncingRuleMock.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(renouncingRuleMock.getConfirmationBodyMessageKey()).thenReturn("someMessage1:someMessage2");
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_INTESTACY);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    void shouldNOTStopRenouncingConfirmation() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(renouncingRuleMock.isChangeNeeded(caseDataMock)).thenReturn(false);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertNull(afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    void shouldStopWillConfirmationForResiduary() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(residuaryRuleMock.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(residuaryRuleMock.getConfirmationBodyMessageKey()).thenReturn("someMessage1:someMessage2");
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_ADMON);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    void shouldNOTStopResiduaryConfirmation() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(residuaryRuleMock.isChangeNeeded(caseDataMock)).thenReturn(false);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertNull(afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    void shouldStopWillConfirmationForSolsExecutor() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(solsExecutorRuleMock.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(solsExecutorRuleMock.getConfirmationBodyMessageKey()).thenReturn("someMessage1:someMessage2");
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_INTESTACY);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    void shouldNOTStopSolsExecutorConfirmation() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(solsExecutorRuleMock.isChangeNeeded(caseDataMock)).thenReturn(false);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertNull(afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    void shouldStopWillConfirmationForSpouseOrCivil() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(spouseOrCivilRuleMock.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(spouseOrCivilRuleMock.getConfirmationBodyMessageKey()).thenReturn("someMessage1:someMessage2");
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_INTESTACY);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    void shouldNOTStopSpouseOrCivilConfirmation() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(spouseOrCivilRuleMock.isChangeNeeded(caseDataMock)).thenReturn(false);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertNull(afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    void shouldNotStopWillConfirmationForWillNotOriginalProbate() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_PROBATE);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertNull(afterSubmitCallbackResponse.getConfirmationBody(), CONFIRMATION_BODY);
    }

    @Test
    void shouldNotStopWillConfirmationForWillNotOriginalAdmon() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_ADMON);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertNull(afterSubmitCallbackResponse.getConfirmationBody(), CONFIRMATION_BODY);
    }

    @Test
    void shouldNOTStopWillNotOriginalConfirmation() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertNull(afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    void shouldGetExceptedEstateYes207Confirmation() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getIhtFormEstateValuesCompleted()).thenReturn(YES);
        when(caseDataMock.getIhtFormEstate()).thenReturn(IHT207_VALUE);
        when(ihtEstate207BusinessRuleMock.isApplicable(any(CaseData.class))).thenReturn(true);
        CCDData ccdDataMock = getCcdDataForConfirmation();
        when(ccdDataMock.getIht().getFormName()).thenReturn(null);
        when(ccdDataMock.getIht().getIhtFormEstateValuesCompleted()).thenReturn(YES);
        when(ccdDataMock.getIht().getIhtFormEstate()).thenReturn(IHT207_VALUE);

        when(markdownSubstitutionServiceMock
            .generatePage(any(String.class), any(MarkdownTemplate.class), nextStepsKeyValueMap.capture()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getNextStepsConfirmation(ccdDataMock,
            caseDataMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
        Map<String, String> nextStepsValues = nextStepsKeyValueMap.getValue();
        assertEquals("31/12/2000", nextStepsValues.get("{{caseSubmissionDate}}"));
        assertConfirmationValues(nextStepsValues);
        assertEquals("", nextStepsValues.get("{{ihtForm}}"));
        assertEquals("\n*   the inheritance tax form IHT 207", nextStepsValues.get("{{ihtText}}"));
        assertFeeConfirmationValues(nextStepsValues);
        assertLegalStatement(nextStepsValues);
    }

    @Test
    void shouldGetExceptedEstateYes400421Confirmation() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getIhtFormEstateValuesCompleted()).thenReturn(YES);
        when(caseDataMock.getIhtFormEstate()).thenReturn(IHT400421_VALUE);
        CCDData ccdDataMock = getCcdDataForConfirmation();
        when(ccdDataMock.getIht().getFormName()).thenReturn(null);
        when(ccdDataMock.getIht().getIhtFormEstateValuesCompleted()).thenReturn(YES);
        when(ccdDataMock.getIht().getIhtFormEstate()).thenReturn(IHT400421_VALUE);

        when(markdownSubstitutionServiceMock
            .generatePage(any(String.class), any(MarkdownTemplate.class), nextStepsKeyValueMap.capture()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getNextStepsConfirmation(ccdDataMock,
            caseDataMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
        Map<String, String> nextStepsValues = nextStepsKeyValueMap.getValue();
        assertEquals("31/12/2000", nextStepsValues.get("{{caseSubmissionDate}}"));
        assertConfirmationValues(nextStepsValues);
        assertEquals("", nextStepsValues.get("{{ihtForm}}"));
        assertEquals("", nextStepsValues.get("{{ihtText}}"));
        assertFeeConfirmationValues(nextStepsValues);
        assertLegalStatement(nextStepsValues);
    }

    @Test
    void shouldGetExceptedEstateYes400Confirmation() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getIhtFormEstateValuesCompleted()).thenReturn(YES);
        when(caseDataMock.getIhtFormEstate()).thenReturn(IHT400_VALUE);
        CCDData ccdDataMock = getCcdDataForConfirmation();
        when(ccdDataMock.getIht().getFormName()).thenReturn(null);
        when(ccdDataMock.getIht().getIhtFormEstateValuesCompleted()).thenReturn(YES);
        when(ccdDataMock.getIht().getIhtFormEstate()).thenReturn(IHT400_VALUE);

        when(markdownSubstitutionServiceMock
                .generatePage(any(String.class), any(MarkdownTemplate.class), nextStepsKeyValueMap.capture()))
                .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getNextStepsConfirmation(ccdDataMock,
                caseDataMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
        Map<String, String> nextStepsValues = nextStepsKeyValueMap.getValue();
        assertEquals("31/12/2000", nextStepsValues.get("{{caseSubmissionDate}}"));
        assertConfirmationValues(nextStepsValues);
        assertEquals("", nextStepsValues.get("{{ihtForm}}"));
        assertEquals("", nextStepsValues.get("{{ihtText}}"));
        assertFeeConfirmationValues(nextStepsValues);
        assertLegalStatement(nextStepsValues);
    }

    @Test
    void shouldGetExceptedEstateYesNAConfirmation() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getIhtFormId()).thenReturn(NOT_APPLICABLE_VALUE);
        CCDData ccdDataMock = getCcdDataForConfirmation();
        when(ccdDataMock.getIht().getFormName()).thenReturn(null);
        when(ccdDataMock.getIht().getIhtFormEstate()).thenReturn(NOT_APPLICABLE_VALUE);

        when(markdownSubstitutionServiceMock
                .generatePage(any(String.class), any(MarkdownTemplate.class), nextStepsKeyValueMap.capture()))
                .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getNextStepsConfirmation(ccdDataMock,
                caseDataMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
        Map<String, String> nextStepsValues = nextStepsKeyValueMap.getValue();
        assertEquals("31/12/2000", nextStepsValues.get("{{caseSubmissionDate}}"));
        assertConfirmationValues(nextStepsValues);
        assertEquals("", nextStepsValues.get("{{ihtForm}}"));
        assertEquals("", nextStepsValues.get("{{ihtText}}"));
        assertFeeConfirmationValues(nextStepsValues);
        assertLegalStatement(nextStepsValues);
    }

    @Test
    void shouldGetExceptedEstateNoConfirmation() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getIhtFormEstateValuesCompleted()).thenReturn(NO);
        CCDData ccdDataMock = getCcdDataForConfirmation();
        when(ccdDataMock.getIht().getFormName()).thenReturn(null);
        when(ccdDataMock.getIht().getIhtFormEstateValuesCompleted()).thenReturn(NO);

        when(markdownSubstitutionServiceMock
            .generatePage(any(String.class), any(MarkdownTemplate.class), nextStepsKeyValueMap.capture()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getNextStepsConfirmation(ccdDataMock,
            caseDataMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
        Map<String, String> nextStepsValues = nextStepsKeyValueMap.getValue();
        assertEquals("31/12/2000", nextStepsValues.get("{{caseSubmissionDate}}"));
        assertConfirmationValues(nextStepsValues);
        assertEquals("", nextStepsValues.get("{{ihtForm}}"));
        assertEquals("", nextStepsValues.get("{{ihtText}}"));
        assertFeeConfirmationValues(nextStepsValues);
        assertLegalStatement(nextStepsValues);
    }

    @Test
    void shouldGetNextStepsConfirmation() {
        CCDData ccdDataMock = getCcdDataForConfirmation();

        when(markdownSubstitutionServiceMock
            .generatePage(any(String.class), any(MarkdownTemplate.class), nextStepsKeyValueMap.capture()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getNextStepsConfirmation(ccdDataMock,
            caseDataMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
        Map<String, String> nextStepsValues = nextStepsKeyValueMap.getValue();
        assertEquals("31/12/2000", nextStepsValues.get("{{caseSubmissionDate}}"));
        assertConfirmationValues(nextStepsValues);
        assertIHT207(nextStepsValues);
        assertFeeConfirmationValues(nextStepsValues);
        assertLegalStatement(nextStepsValues);
    }

    @Test
    void shouldGetNextStepsConfirmationAdmonWill() {
        CCDData ccdDataMock = getCcdDataForConfirmation();
        when(ccdDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_ADMON);

        when(markdownSubstitutionServiceMock
            .generatePage(any(String.class), any(MarkdownTemplate.class), nextStepsKeyValueMap.capture()))
            .thenReturn(willBodyTemplateResponseMock);

        when(markdownDecoratorService.getAdmonWillRenunciationFormLabel(any(CaseData.class), any(Boolean.class)))
            .thenReturn("PA15 and PA17 form text");

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getNextStepsConfirmation(ccdDataMock,
            caseDataMock);

        Map<String, String> nextStepsValues = nextStepsKeyValueMap.getValue();
        assertEquals("PA15 and PA17 form text",
            nextStepsValues.get("{{admonWillRenunciation}}"));
    }

    @Test
    void shouldGetNextStepsConfirmationForPA14Form() {
        CCDData ccdDataMock = getCcdDataForConfirmation();

        when(markdownSubstitutionServiceMock
            .generatePage(any(String.class), any(MarkdownTemplate.class), nextStepsKeyValueMap.capture()))
            .thenReturn(willBodyTemplateResponseMock);

        when(markdownDecoratorService.getPA14FormLabel(any(CaseData.class),
                any(Boolean.class))).thenReturn("PA14Form text");

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getNextStepsConfirmation(ccdDataMock,
            caseDataMock);

        Map<String, String> nextStepsValues = nextStepsKeyValueMap.getValue();
        assertEquals("PA14Form text",
            nextStepsValues.get("{{pa14form}}"));

    }

    @Test
    void shouldGetNextStepsConfirmationForPA15Form() {
        CCDData ccdDataMock = getCcdDataForConfirmation();

        when(markdownSubstitutionServiceMock
            .generatePage(any(String.class), any(MarkdownTemplate.class), nextStepsKeyValueMap.capture()))
            .thenReturn(willBodyTemplateResponseMock);

        when(markdownDecoratorService.getPA15FormLabel(any(CaseData.class),
                any(Boolean.class))).thenReturn("PA15Form text");

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getNextStepsConfirmation(ccdDataMock,
            caseDataMock);

        Map<String, String> nextStepsValues = nextStepsKeyValueMap.getValue();
        assertEquals("PA15Form text",
            nextStepsValues.get("{{pa15form}}"));

    }

    @Test
    void shouldGetNextStepsConfirmationForPA16Form() {
        CCDData ccdDataMock = getCcdDataForConfirmation();

        when(markdownSubstitutionServiceMock
            .generatePage(any(String.class), any(MarkdownTemplate.class), nextStepsKeyValueMap.capture()))
            .thenReturn(willBodyTemplateResponseMock);

        when(markdownDecoratorService.getPA16FormLabel(any(CaseData.class),
                any(Boolean.class))).thenReturn("PA16Form text");

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getNextStepsConfirmation(ccdDataMock,
            caseDataMock);

        Map<String, String> nextStepsValues = nextStepsKeyValueMap.getValue();
        assertEquals("PA16Form text",
            nextStepsValues.get("{{pa16form}}"));

    }

    @Test
    void shouldGetNextStepsConfirmationForPA17Form() {
        CCDData ccdDataMock = getCcdDataForConfirmation();

        when(markdownSubstitutionServiceMock
            .generatePage(any(String.class), any(MarkdownTemplate.class), nextStepsKeyValueMap.capture()))
            .thenReturn(willBodyTemplateResponseMock);

        when(markdownDecoratorService.getPA17FormLabel(any(CaseData.class),
                any(Boolean.class))).thenReturn("PA17Form text");

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getNextStepsConfirmation(ccdDataMock,
            caseDataMock);

        Map<String, String> nextStepsValues = nextStepsKeyValueMap.getValue();
        assertEquals("PA17Form text",
            nextStepsValues.get("{{pa17form}}"));

    }

    @Test
    void shouldGetNextStepsConfirmationForTcResolutionLodgedWithApp() {
        CCDData ccdDataMock = getCcdDataForConfirmation();

        when(markdownSubstitutionServiceMock
                .generatePage(any(String.class), any(MarkdownTemplate.class), nextStepsKeyValueMap.capture()))
                .thenReturn(willBodyTemplateResponseMock);

        when(markdownDecoratorService.getTcResolutionFormLabel(any(CaseData.class), any(Boolean.class)))
            .thenReturn("a certified copy of the resolution");

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getNextStepsConfirmation(ccdDataMock,
            caseDataMock);

        Map<String, String> nextStepsValues = nextStepsKeyValueMap.getValue();
        assertEquals("a certified copy of the resolution",
                nextStepsValues.get("{{tcResolutionLodgedWithApp}}"));

    }

    @Test
    void shouldGetNextStepsConfirmationLegalstatementUploaded() {
        CCDData ccdDataMock = getCcdDataForConfirmation();
        when(ccdDataMock.isHasUploadedLegalStatement()).thenReturn(true);

        when(markdownSubstitutionServiceMock
            .generatePage(any(String.class), any(MarkdownTemplate.class), nextStepsKeyValueMap.capture()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getNextStepsConfirmation(ccdDataMock,
            caseDataMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
        Map<String, String> nextStepsValues = nextStepsKeyValueMap.getValue();
        assertEquals("31/12/2000", nextStepsValues.get("{{caseSubmissionDate}}"));
        assertConfirmationValues(nextStepsValues);
        assertIHT207(nextStepsValues);
        assertEquals("",
            nextStepsValues.get("{{legalPhotocopy}}"));
        assertFeeConfirmationValues(nextStepsValues);
    }

    @Test
    void shouldGetCaveatNextStepsConfirmation() {
        CaveatData caveatData = getCaveatDataForConfirmation();

        when(markdownSubstitutionServiceMock
            .generatePage(any(String.class), any(MarkdownTemplate.class), nextStepsKeyValueMap.capture()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getNextStepsConfirmation(caveatData,
                123456L);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
        Map<String, String> nextStepsValues = nextStepsKeyValueMap.getValue();
        assertEquals("31/12/2000", nextStepsValues.get("{{caseSubmissionDate}}"));
        assertConfirmationValuesCaveats(nextStepsValues);
    }

    @Test
    void shouldGetNextStepsConfirmationWithNoSubmissionDate() {
        CCDData ccdDataMock = getCcdDataForConfirmation();
        when(ccdDataMock.getCaseSubmissionDate()).thenReturn(null);

        when(markdownSubstitutionServiceMock
            .generatePage(any(String.class), any(MarkdownTemplate.class), nextStepsKeyValueMap.capture()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getNextStepsConfirmation(ccdDataMock,
            caseDataMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
        Map<String, String> nextStepsValues = nextStepsKeyValueMap.getValue();
        assertEquals("", nextStepsValues.get("{{caseSubmissionDate}}"));
        assertConfirmationValues(nextStepsValues);
        assertIHT207(nextStepsValues);
        assertLegalStatement(nextStepsValues);
        assertFeeConfirmationValues(nextStepsValues);
    }

    @Test
    void shouldGetCaveatNextStepsConfirmationWithNoSubmissionDate() {
        CaveatData caveatData = getCaveatDataForConfirmation();
        when(caveatData.getApplicationSubmittedDate()).thenReturn(null);

        when(markdownSubstitutionServiceMock
            .generatePage(any(String.class), any(MarkdownTemplate.class), nextStepsKeyValueMap.capture()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getNextStepsConfirmation(caveatData,
                123456L);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
        Map<String, String> nextStepsValues = nextStepsKeyValueMap.getValue();
        assertEquals("", nextStepsValues.get("{{caseSubmissionDate}}"));
        assertConfirmationValuesCaveats(nextStepsValues);
    }

    @Test
    void shouldGetNextStepsConfirmationWithCopies() {
        CCDData ccdDataMock = getCcdDataForConfirmation();

        when(markdownSubstitutionServiceMock
            .generatePage(any(String.class), any(MarkdownTemplate.class), nextStepsKeyValueMap.capture()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getNextStepsConfirmation(ccdDataMock,
            caseDataMock);

        assertEquals(null, afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
        Map<String, String> nextStepsValues = nextStepsKeyValueMap.getValue();
        assertEquals("0.50", nextStepsValues.get("{{feeForUkCopies}}"));
        assertEquals("1.50", nextStepsValues.get("{{feeForNonUkCopies}}"));
        assertConfirmationValues(nextStepsValues);
        assertIHT207(nextStepsValues);
        assertLegalStatement(nextStepsValues);
        assertFeeConfirmationValues(nextStepsValues);
    }

    @Test
    void shouldGetNextStepsConfirmationWithNoCopies() {
        CCDData ccdDataMock = getCcdDataForConfirmation();
        when(ccdDataMock.getFee().getExtraCopiesOfGrant()).thenReturn(null);
        when(ccdDataMock.getFee().getOutsideUKGrantCopies()).thenReturn(null);
        when(ccdDataMock.getFee().getFeeForUkCopies()).thenReturn(null);
        when(ccdDataMock.getFee().getFeeForNonUkCopies()).thenReturn(null);

        when(markdownSubstitutionServiceMock
            .generatePage(any(String.class), any(MarkdownTemplate.class), nextStepsKeyValueMap.capture()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getNextStepsConfirmation(ccdDataMock,
            caseDataMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
        Map<String, String> nextStepsValues = nextStepsKeyValueMap.getValue();
        assertEquals("", nextStepsValues.get("{{feeForUkCopies}}"));
        assertEquals("", nextStepsValues.get("{{feeForNonUkCopies}}"));
        assertConfirmationValues(nextStepsValues);
        assertIHT207(nextStepsValues);
        assertLegalStatement(nextStepsValues);
        assertFeeConfirmationValues(nextStepsValues);
    }

    @Test
    void shouldGetNextStepsConfirmationWithNoPayment() {
        CCDData ccdDataMock = getCcdDataForConfirmation();
        when(ccdDataMock.getFee().getPaymentMethod()).thenReturn(null);

        when(ccdDataMock.getFee().getExtraCopiesOfGrant()).thenReturn(null);
        when(ccdDataMock.getFee().getOutsideUKGrantCopies()).thenReturn(null);
        when(ccdDataMock.getFee().getFeeForUkCopies()).thenReturn(null);
        when(ccdDataMock.getFee().getFeeForNonUkCopies()).thenReturn(null);
        when(ccdDataMock.getFee().getAmount()).thenReturn(BigDecimal.valueOf(0));

        when(markdownSubstitutionServiceMock
            .generatePage(any(String.class), any(MarkdownTemplate.class), nextStepsKeyValueMap.capture()))
            .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getNextStepsConfirmation(ccdDataMock,
            caseDataMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
        Map<String, String> nextStepsValues = nextStepsKeyValueMap.getValue();
        assertEquals("", nextStepsValues.get("{{feeForUkCopies}}"));
        assertEquals("", nextStepsValues.get("{{feeForNonUkCopies}}"));
        assertConfirmationValues(nextStepsValues);
        assertIHT207(nextStepsValues);
        //assertEquals("No payment needed", nextStepsValues.get("{{paymentMethod}}"));
        assertEquals("0.00", nextStepsValues.get("{{paymentAmount}}"));
    }

    @Test
    void shouldGetNextStepsConfirmationWithDiedBeforeOrDiedAfterThenShouldNotShowDeathCertificate() {
        CCDData ccdDataMock = getCcdDataForConfirmation();
        when(deadBeforeExecutorMock.isApplying()).thenReturn(false);
        when(deadBeforeExecutorMock.getReasonNotApplying()).thenReturn("DiedBefore");
        when(deadBeforeExecutorMock.getForename()).thenReturn("deadBeforeExecutorFirstname");
        when(deadBeforeExecutorMock.getLastname()).thenReturn("deadBeforeExecutorLastname");

        when(deadAfterExecutorMock.isApplying()).thenReturn(false);
        when(deadAfterExecutorMock.getReasonNotApplying()).thenReturn("DiedAfter");
        when(deadAfterExecutorMock.getForename()).thenReturn("deadAfterExecutorFirstname");
        when(deadAfterExecutorMock.getLastname()).thenReturn("deadAfterExecutorLastname");

        when(markdownSubstitutionServiceMock
                .generatePage(any(String.class), any(MarkdownTemplate.class), nextStepsKeyValueMap.capture()))
                .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getNextStepsConfirmation(ccdDataMock,
                caseDataMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
        Map<String, String> nextStepsValues = nextStepsKeyValueMap.getValue();
        assertConfirmationValues(nextStepsValues);
        assertNull(nextStepsValues.get("{{deadExecutors}}"));
        assertIHT207(nextStepsValues);
    }

    private void assertConfirmationValues(Map<String, String> nextStepsValues) {
        assertEquals("ref", nextStepsValues.get("{{solicitorReference}}"));
        assertEquals("Sol Firm Name", nextStepsValues.get("{{solsSolicitorFirmName}}"));
        assertEquals("Andy Test", nextStepsValues.get("{{solicitorName}}"));
        assertEquals("Lawyer", nextStepsValues.get("{{solicitorJobRole}}"));
        assertEquals("Firstname", nextStepsValues.get("{{deceasedFirstname}}"));
        assertEquals("Lastname", nextStepsValues.get("{{deceasedLastname}}"));
        assertEquals("31/12/2000", nextStepsValues.get("{{deceasedDateOfDeath}}"));
        assertEquals("solsAdditionalInfo", nextStepsValues.get("{{additionalInfo}}"));
    }

    private void assertLegalStatement(Map<String, String> nextStepsValues) {
        assertEquals("*   a photocopy of the signed legal statement and declaration",
            nextStepsValues.get("{{legalPhotocopy}}"));
    }

    private void assertIHT207(Map<String, String> nextStepsValues) {
        assertEquals("IHT207", nextStepsValues.get("{{ihtForm}}"));
    }

    private void assertFeeConfirmationValues(Map<String, String> nextStepsValues) {
        assertEquals("100.00", nextStepsValues.get("{{paymentAmount}}"));
    }

    private void assertConfirmationValuesCaveats(Map<String, String> nextStepsValues) {
        assertEquals("ref", nextStepsValues.get("{{solicitorReference}}"));
        assertEquals("123456", nextStepsValues.get("{{caseReference}}"));
    }

    private CCDData getCcdDataForConfirmation() {
        Solicitor solicitor = mock(Solicitor.class);
        Deceased deceased = mock(Deceased.class);
        InheritanceTax inheritanceTax = mock(InheritanceTax.class);
        Fee fee = mock(Fee.class);
        CCDData ccdDataMock = mock(CCDData.class);
        LocalDate date = LocalDate.parse("2000-12-31");

        when(ccdDataMock.getDeceased()).thenReturn(deceased);
        when(ccdDataMock.getFee()).thenReturn(fee);
        when(ccdDataMock.getIht()).thenReturn(inheritanceTax);
        when(ccdDataMock.getSolicitor()).thenReturn(solicitor);
        when(ccdDataMock.getSolicitorReference()).thenReturn("ref");
        when(ccdDataMock.getCaseSubmissionDate()).thenReturn(date);
        when(solicitor.getFirmName()).thenReturn("Sol Firm Name");
        when(solicitor.getFullname()).thenReturn("Andy Test");
        when(solicitor.getJobRole()).thenReturn("Lawyer");
        when(deceased.getFirstname()).thenReturn("Firstname");
        when(deceased.getLastname()).thenReturn("Lastname");
        when(deceased.getDateOfBirth()).thenReturn(date);
        when(deceased.getDateOfDeath()).thenReturn(date);
        when(inheritanceTax.getFormName()).thenReturn("IHT207");
        when(fee.getPaymentMethod()).thenReturn("Cheque");
        when(fee.getAmount()).thenReturn(BigDecimal.valueOf(10000));
        when(fee.getApplicationFee()).thenReturn(BigDecimal.valueOf(5000));
        when(ccdDataMock.getFee().getExtraCopiesOfGrant()).thenReturn(1L);
        when(ccdDataMock.getFee().getOutsideUKGrantCopies()).thenReturn(3L);
        when(ccdDataMock.getFee().getFeeForUkCopies()).thenReturn(BigDecimal.valueOf(50));
        when(ccdDataMock.getFee().getFeeForNonUkCopies()).thenReturn(BigDecimal.valueOf(150));
        when(ccdDataMock.getFee().getSolsPBANumber()).thenReturn("SelectePBA");
        when(ccdDataMock.getSolsAdditionalInfo()).thenReturn("solsAdditionalInfo");
        when(ccdDataMock.getSolsWillType()).thenReturn("NoWill");
        executorsList.add(executorMock);
        executorsList.add(renouncingExecutorMock);
        executorsList.add(deadBeforeExecutorMock);
        executorsList.add(deadAfterExecutorMock);
        when(ccdDataMock.getExecutors()).thenReturn(executorsList);
        when(ccdDataMock.getSolicitor().getFirmAddress()).thenReturn(solsAddressMock);

        return ccdDataMock;
    }

    private CaveatData getCaveatDataForConfirmation() {
        CaveatData caveatDataMock = mock(CaveatData.class);
        LocalDate date = LocalDate.parse("2000-12-31");


        when(caveatDataMock.getSolsSolicitorAppReference()).thenReturn("ref");
        when(caveatDataMock.getApplicationSubmittedDate()).thenReturn(date);
        when(caveatDataMock.getSolsSolicitorFirmName()).thenReturn("Sol Firm Name");
        when(caveatDataMock.getCaveatorEmailAddress()).thenReturn("caveator@probate-test.com");
        when(caveatDataMock.getSolsSolicitorPhoneNumber()).thenReturn("07070707077");
        when(caveatDataMock.getCaveatorAddress()).thenReturn(probateAddressMock);

        when(caveatDataMock.getCaveatorFullName()).thenReturn("Applicant_fn Applicant_ln");
        when(caveatDataMock.getDeceasedFullName()).thenReturn("Deceased Fullname");
        when(caveatDataMock.getDeceasedDateOfDeath()).thenReturn(date);
        when(caveatDataMock.getDeceasedDateOfBirth()).thenReturn(date);
        when(caveatDataMock.getDeceasedAddress()).thenReturn(probateAddressMock);
        when(caveatDataMock.getDeceasedAnyOtherNames()).thenReturn("No");
        when(caveatDataMock.getSolsPaymentMethods()).thenReturn("fee account");
        when(caveatDataMock.getSolsPBANumber()).thenReturn(DynamicList.builder()
            .value(DynamicListItem.builder().code("SelectePBA").label("SelectePBA").build())
            .build());
        when(caveatDataMock.getSolsPBAPaymentReference()).thenReturn("Sol Pay Ref");

        return caveatDataMock;
    }
}
