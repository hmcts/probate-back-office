package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
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
import uk.gov.hmcts.probate.changerule.UpdateApplicationRule;
import uk.gov.hmcts.probate.model.ExecutorsApplyingNotification;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static uk.gov.hmcts.probate.model.Constants.REDEC_NOTIFICATION_SENT_STATE;

@RunWith(SpringRunner.class)
public class StateChangeServiceTest {

    @InjectMocks
    private StateChangeService underTest;

    @Mock
    private ApplicantSiblingsRule applicantSiblingsRule;
    @Mock
    private DiedOrNotApplyingRule diedOrNotApplyingRule;
    @Mock
    private EntitledMinorityRule entitledMinorityRule;
    @Mock
    private ExecutorsRule executorsStateRule;
    @Mock
    private ImmovableEstateRule immovableEstateRule;
    @Mock
    private LifeInterestRule lifeInterestRule;
    @Mock
    private MinorityInterestRule minorityInterestRule;
    @Mock
    private NoOriginalWillRule noOriginalWillRule;
    @Mock
    private RenouncingRule renouncingRule;
    @Mock
    private ResiduaryRule residuaryRule;
    @Mock
    private SolsExecutorRule solsExecutorRule;
    @Mock
    private SpouseOrCivilRule spouseOrCivilRule;
    @Mock
    private UpdateApplicationRule updateApplicationRule;
    @Mock
    private CallbackResponseTransformer callbackResponseTransformer;

    @Mock
    private CaseData caseDataMock;

    private static final String WILL_TYPE_PROBATE = "WillLeft";
    private static final String WILL_TYPE_INTESTACY = "NoWill";
    private static final String WILL_TYPE_ADMON = "WillLeftAnnexed";
    private static final String STATE_GRANT_TYPE_PROBATE = "SolProbateCreated";
    private static final String STATE_GRANT_TYPE_INTESTACY = "SolIntestacyCreated";
    private static final String STATE_GRANT_TYPE_ADMON = "SolAdmonCreated";
    private static final String STATE_GRANT_TYPE_CREATED = "SolAppCreated";
    private static final Long ID = 1L;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;
    private List<CollectionMember<ExecutorsApplyingNotification>> execList;
    private CollectionMember<ExecutorsApplyingNotification> execResponseReceived;
    private CollectionMember<ExecutorsApplyingNotification> execResponseNotReceived;
    private CollectionMember<ExecutorsApplyingNotification> execResponseNotificationNo;

    @Before
    public void setup() {
        initMocks(this);

        underTest = new StateChangeService(applicantSiblingsRule, diedOrNotApplyingRule,
                entitledMinorityRule, executorsStateRule, immovableEstateRule, lifeInterestRule, minorityInterestRule, noOriginalWillRule,
                renouncingRule, residuaryRule, solsExecutorRule,spouseOrCivilRule, updateApplicationRule, callbackResponseTransformer);

        execList = new ArrayList<>();
        execResponseReceived = new CollectionMember<>(
                ExecutorsApplyingNotification.builder()
                        .notification("Yes")
                        .email("executor1@probate-test.com")
                        .responseReceived("Yes")
                        .build());

        execResponseNotReceived = new CollectionMember<>(
                ExecutorsApplyingNotification.builder()
                        .notification("Yes")
                        .email("executor2@probate-test.com")
                        .responseReceived("No")
                        .build());

        execResponseNotificationNo = new CollectionMember<>(
                ExecutorsApplyingNotification.builder()
                        .notification("No")
                        .email("executor3@probate-test.com")
                        .responseReceived("No")
                        .build());
    }

    @Test
    public void shouldChangeStateForAnyRuleValid() {
        when(executorsStateRule.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(noOriginalWillRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForProbateUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    public void shouldChangeStateForApplicantSiblingsRuleValid() {
        when(applicantSiblingsRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForIntestacyUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    public void shouldNOTChangeStateForApplicantSiblingsRule() {
        when(applicantSiblingsRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForIntestacyUpdate(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    public void shouldChangeStateForDiedOrNotApplyingRuleValid() {
        when(diedOrNotApplyingRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForAdmonUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    public void shouldNOTChangeStateForDiedOrNotApplyingRule() {
        when(diedOrNotApplyingRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForAdmonUpdate(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    public void shouldChangeStateForEntitledMinorityRuleValid() {
        when(entitledMinorityRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForAdmonUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    public void shouldNOTChangeStateForEntitledMinority() {
        when(entitledMinorityRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForAdmonUpdate(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    public void shouldChangeStateForExecutorsRuleValid() {
        when(executorsStateRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForProbateUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    public void shouldNOTChangeStateForExecutors() {
        when(executorsStateRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForProbateUpdate(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    public void shouldChangeStateForImmovableEstateRuleIntestacyValid() {
        when(immovableEstateRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForIntestacyUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    public void shouldNOTChangeStateForImmovableEstateRuleIntestacy() {
        when(immovableEstateRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForIntestacyUpdate(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
        public void shouldChangeStateForImmovableEstateRuleAdmonValid() {
        when(immovableEstateRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForAdmonUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    public void shouldNOTChangeStateForImmovableEstateRuleAdmon() {
        when(immovableEstateRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForAdmonUpdate(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    public void shouldChangeStateForLifeInterestRuleValid() {
        when(lifeInterestRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForAdmonUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    public void shouldNOTChangeStateForLifeInterestRule() {
        when(lifeInterestRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForAdmonUpdate(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    public void shouldChangeStateForMinorityInterestRuleValid() {
        when(minorityInterestRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForIntestacyUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    public void shouldNOTChangeStateForMinorityInterestRule() {
        when(minorityInterestRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForIntestacyUpdate(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    public void shouldChangeStateForOriginalWillRuleValid() {
        when(noOriginalWillRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForAdmonUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    public void shouldNOTChangeStateForOriginalWillRule() {
        when(noOriginalWillRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForAdmonUpdate(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    public void shouldChangeStateForRenouncingRuleValid() {
        when(renouncingRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForIntestacyUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    public void shouldNOTChangeStateForRenouncingRule() {
        when(renouncingRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForIntestacyUpdate(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    public void shouldChangeStateForResiduaryRuleValid() {
        when(residuaryRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForAdmonUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    public void shouldNOTChangeStateForResiduaryRule() {
        when(residuaryRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForAdmonUpdate(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    public void shouldChangeStateForSolsExecutorRuleIntestacyValid() {
        when(solsExecutorRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForIntestacyUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    public void shouldChangeStateForSolsExecutorRuleAdmonValid() {
        when(solsExecutorRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForAdmonUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    public void shouldNOTChangeStateForSolsExecutorRule() {
        when(solsExecutorRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForAdmonUpdate(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    public void shouldChangeStateForSpouseOrCivilRuleValid() {
        when(spouseOrCivilRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForIntestacyUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    public void shouldNOTChangeStateForSpouseOrCivilRule() {
        when(spouseOrCivilRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForIntestacyUpdate(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    public void shouldNOTChangeStateForAllRulesInvalid() {
        when(noOriginalWillRule.isChangeNeeded(caseDataMock)).thenReturn(false);
        when(executorsStateRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForProbateUpdate(caseDataMock);

        assertFalse(newState.isPresent());
    }

    @Test
    public void shouldChangeStateForCaseReview() {
        when(updateApplicationRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForCaseReview(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals(STATE_GRANT_TYPE_CREATED, newState.get());
    }

    @Test
    public void shouldChangeStateForCaseReviewOnSelectedLegalStatementChangeAsDeceasedDetails() {
        when(updateApplicationRule.isChangeNeeded(caseDataMock)).thenReturn(true);
        DynamicListItem item = DynamicListItem.builder().code("SolAppCreated").label("label1").build();
        DynamicListItem value = DynamicListItem.builder().code("SolAppCreated").label("label1").build();

        DynamicList list = DynamicList.builder().listItems(Arrays.asList(item)).value(value).build();
        when(caseDataMock.getSolsAmendLegalStatmentSelect()).thenReturn(list);

        Optional<String> newState = underTest.getChangedStateForCaseReview(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals(STATE_GRANT_TYPE_CREATED, newState.get());
    }

    @Test
    public void shouldChangeStateForCaseReviewOnSelectedLegalStatementChangeAsProbate() {
        when(updateApplicationRule.isChangeNeeded(caseDataMock)).thenReturn(true);
        DynamicListItem item = DynamicListItem.builder().code("WillLeft").label("label1").build();
        DynamicListItem value = DynamicListItem.builder().code("WillLeft").label("label1").build();

        DynamicList list = DynamicList.builder().listItems(Arrays.asList(item)).value(value).build();
        when(caseDataMock.getSolsAmendLegalStatmentSelect()).thenReturn(list);

        Optional<String> newState = underTest.getChangedStateForCaseReview(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals(STATE_GRANT_TYPE_PROBATE, newState.get());
    }

    @Test
    public void shouldChangeStateForCaseReviewOnSelectedLegalStatementChangeAsIntestacy() {
        when(updateApplicationRule.isChangeNeeded(caseDataMock)).thenReturn(true);
        DynamicListItem item = DynamicListItem.builder().code("NoWill").label("label1").build();
        DynamicListItem value = DynamicListItem.builder().code("NoWill").label("label1").build();

        DynamicList list = DynamicList.builder().listItems(Arrays.asList(item)).value(value).build();
        when(caseDataMock.getSolsAmendLegalStatmentSelect()).thenReturn(list);

        Optional<String> newState = underTest.getChangedStateForCaseReview(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals(STATE_GRANT_TYPE_INTESTACY, newState.get());
    }

    @Test
    public void shouldChangeStateForCaseReviewOnSelectedLegalStatementChangeAsAdmon() {
        when(updateApplicationRule.isChangeNeeded(caseDataMock)).thenReturn(true);
        DynamicListItem item = DynamicListItem.builder().code("WillLeftAnnexed").label("label1").build();
        DynamicListItem value = DynamicListItem.builder().code("WillLeftAnnexed").label("label1").build();

        DynamicList list = DynamicList.builder().listItems(Arrays.asList(item)).value(value).build();
        when(caseDataMock.getSolsAmendLegalStatmentSelect()).thenReturn(list);

        Optional<String> newState = underTest.getChangedStateForCaseReview(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals(STATE_GRANT_TYPE_ADMON, newState.get());
    }

    @Test
    public void shouldChangeStateForGrantTypeProbate() {
        when(caseDataMock.getSolsWillType()).thenReturn(WILL_TYPE_PROBATE);

        Optional<String> newState = underTest.getChangedStateForGrantType(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals(STATE_GRANT_TYPE_PROBATE, newState.get());
    }

    @Test
    public void shouldChangeStateForGrantTypeIntestacy() {
        when(caseDataMock.getSolsWillType()).thenReturn(WILL_TYPE_INTESTACY);

        Optional<String> newState = underTest.getChangedStateForGrantType(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals(STATE_GRANT_TYPE_INTESTACY, newState.get());
    }

    @Test
    public void shouldChangeStateForGrantTypeAdmon() {
        when(caseDataMock.getSolsWillType()).thenReturn(WILL_TYPE_ADMON);

        Optional<String> newState = underTest.getChangedStateForGrantType(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals(STATE_GRANT_TYPE_ADMON, newState.get());
    }

    @Test
    public void shouldNOTChangeStateForReview() {
        when(updateApplicationRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForCaseReview(caseDataMock);

        assertFalse(newState.isPresent());
    }

    @Test
    public void getRedeclarationCompleteNoStateChange() {
        execList.add(execResponseReceived);
        CaseData caseData = CaseData.builder().executorsApplyingNotifications(execList).build();

        Optional<String> state = underTest.getRedeclarationComplete(caseData);
        assertEquals(Optional.empty(), state);
    }

    @Test
    public void getRedeclarationCompleteWithStateChange() {
        execList.add(execResponseReceived);
        execList.add(execResponseNotReceived);
        CaseData caseData = CaseData.builder().executorsApplyingNotifications(execList).build();

        Optional<String> state = underTest.getRedeclarationComplete(caseData);
        assertEquals(Optional.of(REDEC_NOTIFICATION_SENT_STATE), state);
    }

    @Test
    public void getRedeclarationCompleteWithStateChangeNotificationNo() {
        execList.add(execResponseReceived);
        execList.add(execResponseNotificationNo);
        CaseData caseData = CaseData.builder().executorsApplyingNotifications(execList).build();

        Optional<String> state = underTest.getRedeclarationComplete(caseData);
        assertEquals(Optional.empty(), state);
    }

}
