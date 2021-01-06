package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.probate.changerule.ApplicantSiblingsRule;
import uk.gov.hmcts.probate.changerule.DiedOrNotApplyingRule;
import uk.gov.hmcts.probate.changerule.EntitledMinorityRule;
import uk.gov.hmcts.probate.changerule.ExecutorsRule;
import uk.gov.hmcts.probate.changerule.ImmovableEstateRule;
import uk.gov.hmcts.probate.changerule.LifeInterestRule;
import uk.gov.hmcts.probate.changerule.MinorityInterestRule;
import uk.gov.hmcts.probate.changerule.NoOriginalWillRule;
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
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.AfterSubmitCallbackResponse;
import uk.gov.hmcts.probate.model.template.MarkdownTemplate;
import uk.gov.hmcts.probate.model.template.TemplateResponse;
import uk.gov.hmcts.probate.service.template.markdown.MarkdownSubstitutionService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConfirmationResponseServiceTest {

    private static final String CONFIRMATION_BODY = "someBody";
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
    private NoOriginalWillRule noOriginalWillRuleMock;
    @Mock
    private RenouncingRule renouncingRuleMock;
    @Mock
    private ResiduaryRule residuaryRuleMock;
    @Mock
    private SolsExecutorRule solsExecutorRuleMock;
    @Mock
    private SpouseOrCivilRule spouseOrCivilRuleMock;
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
    private MessageResourceService messageResourceServiceMock;
    private final List<Executor> executorsList = new ArrayList<>();
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

    private static final String GRANT_TYPE_PROBATE = "WillLeft";
    private static final String GRANT_TYPE_INTESTACY = "NoWill";
    private static final String GRANT_TYPE_ADMON = "WillLeftAnnexed";


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        underTest = new ConfirmationResponseService(messageResourceServiceMock, markdownSubstitutionServiceMock,
                applicantSiblingsRuleMock, diedOrNotApplyingRuleMock, entitledMinorityRuleMock,
                executorsRuleMock, immovableEstateRule, lifeInterestRuleMock, minorityInterestRuleMock, noOriginalWillRuleMock,
                renouncingRuleMock, residuaryRuleMock, solsExecutorRuleMock, spouseOrCivilRuleMock);
        ReflectionTestUtils.setField(underTest, "templatesDirectory", "templates/markdown/");

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(willBodyTemplateResponseMock.getTemplate()).thenReturn(CONFIRMATION_BODY);

        when(executorMock.isApplying()).thenReturn(true);
        when(renouncingExecutorMock.isApplying()).thenReturn(false);
        when(renouncingExecutorMock.getReasonNotApplying()).thenReturn("Renunciation");
        when(deadBeforeExecutorMock.isApplying()).thenReturn(false);
        when(deadBeforeExecutorMock.getReasonNotApplying()).thenReturn("DiedBefore");
        when(deadAfterExecutorMock.isApplying()).thenReturn(false);
        when(deadAfterExecutorMock.getReasonNotApplying()).thenReturn("DiedAfter");
    }

    @Test
    public void shouldStopWillConfirmationForApplicantSiblings() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(applicantSiblingsRuleMock.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
                .thenReturn(willBodyTemplateResponseMock);
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_INTESTACY);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    public void shouldNOTStopApplicantSiblingsConfirmation() {
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
    public void shouldStopWillConfirmationForImmovableEstateIntestacy() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(immovableEstateRule.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
                .thenReturn(willBodyTemplateResponseMock);
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_INTESTACY);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    public void shouldStopWillConfirmationForImmovableEstateAdmon() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(immovableEstateRule.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
                .thenReturn(willBodyTemplateResponseMock);
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_ADMON);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    public void shouldNOTStopImmovableEstateConfirmation() {
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
    public void shouldStopWillConfirmationForDiedOrNotApplying() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(diedOrNotApplyingRuleMock.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
                .thenReturn(willBodyTemplateResponseMock);
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_ADMON);


        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    public void shouldNOTStopDiedOrNotApplyingConfirmation() {
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
    public void shouldStopWillConfirmationForEntitledMinority() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(entitledMinorityRuleMock.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
                .thenReturn(willBodyTemplateResponseMock);
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_ADMON);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    public void shouldNOTStopEntitledMinorityConfirmation() {
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
    public void shouldStopWillConfirmationForExecutor() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(executorsRuleMock.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
            .thenReturn(willBodyTemplateResponseMock);
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_PROBATE);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    public void shouldNOTStopExecutorConfirmation() {
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
    public void shouldStopWillConfirmationForLifeInterest() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(lifeInterestRuleMock.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
                .thenReturn(willBodyTemplateResponseMock);
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_ADMON);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    public void shouldNOTStopLifeInterestConfirmation() {
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
    public void shouldStopWillConfirmationForMinorityInterest() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(minorityInterestRuleMock.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
                .thenReturn(willBodyTemplateResponseMock);
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_INTESTACY);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    public void shouldNOTStopMinorityInterestConfirmation() {
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
    public void shouldStopWillConfirmationForRenouncing() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(renouncingRuleMock.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
                .thenReturn(willBodyTemplateResponseMock);
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_INTESTACY);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    public void shouldNOTStopRenouncingConfirmation() {
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
    public void shouldStopWillConfirmationForResiduary() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(residuaryRuleMock.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
                .thenReturn(willBodyTemplateResponseMock);
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_ADMON);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    public void shouldNOTStopResiduaryConfirmation() {
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
    public void shouldStopWillConfirmationForSolsExecutor() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(solsExecutorRuleMock.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
                .thenReturn(willBodyTemplateResponseMock);
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_INTESTACY);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    public void shouldNOTStopSolsExecutorConfirmation() {
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
    public void shouldStopWillConfirmationForSpouseOrCivil() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(spouseOrCivilRuleMock.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
                .thenReturn(willBodyTemplateResponseMock);
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_INTESTACY);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    public void shouldNOTStopSpouseOrCivilConfirmation() {
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
    public void shouldStopWillConfirmationForWillNotOriginalProbate() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(noOriginalWillRuleMock.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
                .thenReturn(willBodyTemplateResponseMock);
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_PROBATE);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    public void shouldStopWillConfirmationForWillNotOriginalAdmon() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(noOriginalWillRuleMock.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
                .thenReturn(willBodyTemplateResponseMock);
        when(caseDataMock.getSolsWillType()).thenReturn(GRANT_TYPE_ADMON);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Test
    public void shouldNOTStopWillNotOriginalConfirmation() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(noOriginalWillRuleMock.isChangeNeeded(caseDataMock)).thenReturn(false);
        when(markdownSubstitutionServiceMock.generatePage(anyString(), any(MarkdownTemplate.class), anyMap()))
                .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getStopConfirmation(callbackRequestMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertNull(afterSubmitCallbackResponse.getConfirmationBody());
    }

    @Captor
    private ArgumentCaptor<Map<String, String>> nextStepsKeyValueMap;

    @Test
    public void shouldGetNextStepsConfirmation() {
        CCDData ccdDataMock = getCcdDataForConfirmation();

        when(markdownSubstitutionServiceMock.generatePage(any(String.class), any(MarkdownTemplate.class), nextStepsKeyValueMap.capture()))
                .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getNextStepsConfirmation(ccdDataMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
        Map<String, String> nextStepsValues = nextStepsKeyValueMap.getValue();
        assertEquals("31/12/2000", nextStepsValues.get("{{caseSubmissionDate}}"));
        assertConfirmationValues(nextStepsValues);
    }

    @Test
    public void shouldGetCaveatNextStepsConfirmation() {
        CaveatData caveatData = getCaveatDataForConfirmation();

        when(markdownSubstitutionServiceMock.generatePage(any(String.class), any(MarkdownTemplate.class), nextStepsKeyValueMap.capture()))
                .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getNextStepsConfirmation(caveatData);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
        Map<String, String> nextStepsValues = nextStepsKeyValueMap.getValue();
        assertEquals("31/12/2000", nextStepsValues.get("{{caseSubmissionDate}}"));
        assertConfirmationValuesCaveats(nextStepsValues);
    }

    @Test
    public void shouldGetNextStepsConfirmationWithNoSubmissionDate() {
        CCDData ccdDataMock = getCcdDataForConfirmation();
        when(ccdDataMock.getCaseSubmissionDate()).thenReturn(null);

        when(markdownSubstitutionServiceMock.generatePage(any(String.class), any(MarkdownTemplate.class), nextStepsKeyValueMap.capture()))
                .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getNextStepsConfirmation(ccdDataMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
        Map<String, String> nextStepsValues = nextStepsKeyValueMap.getValue();
        assertEquals("", nextStepsValues.get("{{caseSubmissionDate}}"));
        assertConfirmationValues(nextStepsValues);
    }

    @Test
    public void shouldGetCaveatNextStepsConfirmationWithNoSubmissionDate() {
        CaveatData caveatData = getCaveatDataForConfirmation();
        when(caveatData.getApplicationSubmittedDate()).thenReturn(null);

        when(markdownSubstitutionServiceMock.generatePage(any(String.class), any(MarkdownTemplate.class), nextStepsKeyValueMap.capture()))
                .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getNextStepsConfirmation(caveatData);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
        Map<String, String> nextStepsValues = nextStepsKeyValueMap.getValue();
        assertEquals("", nextStepsValues.get("{{caseSubmissionDate}}"));
        assertConfirmationValuesCaveats(nextStepsValues);
    }

    @Test
    public void shouldGetNextStepsConfirmationWithCopies() {
        CCDData ccdDataMock = getCcdDataForConfirmation();

        when(markdownSubstitutionServiceMock.generatePage(any(String.class), any(MarkdownTemplate.class), nextStepsKeyValueMap.capture()))
                .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getNextStepsConfirmation(ccdDataMock);

        assertEquals(null, afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
        Map<String, String> nextStepsValues = nextStepsKeyValueMap.getValue();
        assertEquals("0.50", nextStepsValues.get("{{feeForUkCopies}}"));
        assertEquals("1.50", nextStepsValues.get("{{feeForNonUkCopies}}"));
        assertConfirmationValues(nextStepsValues);
    }

    @Test
    public void shouldGetNextStepsConfirmationWithNoCopies() {
        CCDData ccdDataMock = getCcdDataForConfirmation();
        when(ccdDataMock.getFee().getExtraCopiesOfGrant()).thenReturn(null);
        when(ccdDataMock.getFee().getOutsideUKGrantCopies()).thenReturn(null);
        when(ccdDataMock.getFee().getFeeForUkCopies()).thenReturn(null);
        when(ccdDataMock.getFee().getFeeForNonUkCopies()).thenReturn(null);

        when(markdownSubstitutionServiceMock.generatePage(any(String.class), any(MarkdownTemplate.class), nextStepsKeyValueMap.capture()))
                .thenReturn(willBodyTemplateResponseMock);

        AfterSubmitCallbackResponse afterSubmitCallbackResponse = underTest.getNextStepsConfirmation(ccdDataMock);

        assertNull(afterSubmitCallbackResponse.getConfirmationHeader());
        assertEquals(CONFIRMATION_BODY, afterSubmitCallbackResponse.getConfirmationBody());
        Map<String, String> nextStepsValues = nextStepsKeyValueMap.getValue();
        assertEquals("", nextStepsValues.get("{{feeForUkCopies}}"));
        assertEquals("", nextStepsValues.get("{{feeForNonUkCopies}}"));
        assertConfirmationValues(nextStepsValues);
    }

    private void assertConfirmationValues(Map<String, String> nextStepsValues) {
        assertEquals("ref", nextStepsValues.get("{{solicitorReference}}"));
        assertEquals("Sol Firm Name", nextStepsValues.get("{{solsSolicitorFirmName}}"));
        assertEquals("Andy Test", nextStepsValues.get("{{solicitorName}}"));
        assertEquals("Lawyer", nextStepsValues.get("{{solicitorJobRole}}"));
        assertEquals("Firstname", nextStepsValues.get("{{deceasedFirstname}}"));
        assertEquals("Lastname", nextStepsValues.get("{{deceasedLastname}}"));
        assertEquals("31/12/2000", nextStepsValues.get("{{deceasedDateOfDeath}}"));
        assertEquals("IHT207", nextStepsValues.get("{{ihtForm}}"));
        assertEquals("Cheque", nextStepsValues.get("{{paymentMethod}}"));
        assertEquals("100.00", nextStepsValues.get("{{paymentAmount}}"));
        assertEquals("solsAdditionalInfo", nextStepsValues.get("{{additionalInfo}}"));
        assertEquals("*   a photocopy of the signed legal statement and declaration",
                nextStepsValues.get("{{legalPhotocopy}}"));
    }

    private void assertConfirmationValuesCaveats(Map<String, String> nextStepsValues) {
        assertEquals("ref", nextStepsValues.get("{{solicitorReference}}"));
        assertEquals("3.00", nextStepsValues.get("{{applicationFee}}"));
        assertEquals("Cheque (payable to 'HM Courts & Tribunals Service')", nextStepsValues.get("{{paymentReferenceNumber}}"));
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

        return caveatDataMock;
    }
}
