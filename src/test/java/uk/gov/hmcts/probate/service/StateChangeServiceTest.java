package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.changerule.ApplicantSiblingsRule;
import uk.gov.hmcts.probate.changerule.DiedOrNotApplyingRule;
import uk.gov.hmcts.probate.changerule.EntitledMinorityRule;
import uk.gov.hmcts.probate.changerule.ExecutorsRule;
import uk.gov.hmcts.probate.changerule.ImmovableEstateRule;
import uk.gov.hmcts.probate.changerule.LifeInterestRule;
import uk.gov.hmcts.probate.changerule.MinorityInterestRule;
import uk.gov.hmcts.probate.changerule.NoNotorialWillCopyRule;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static uk.gov.hmcts.probate.model.Constants.REDEC_NOTIFICATION_SENT_STATE;

@ExtendWith(SpringExtension.class)
class StateChangeServiceTest {

    private static final String WILL_TYPE_PROBATE = "WillLeft";
    private static final String WILL_TYPE_INTESTACY = "NoWill";
    private static final String WILL_TYPE_ADMON = "WillLeftAnnexed";
    private static final String STATE_GRANT_TYPE_PROBATE = "SolProbateCreated";
    private static final String STATE_GRANT_TYPE_INTESTACY = "SolIntestacyCreated";
    private static final String STATE_GRANT_TYPE_ADMON = "SolAdmonCreated";
    private static final String STATE_GRANT_TYPE_CREATED = "SolAppCreatedDeceasedDtls";

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
    private NoNotorialWillCopyRule noNotorialWillCopyRule;
    @Mock
    private CaseData caseDataMock;
    private List<CollectionMember<ExecutorsApplyingNotification>> execList;
    private CollectionMember<ExecutorsApplyingNotification> execResponseReceived;
    private CollectionMember<ExecutorsApplyingNotification> execResponseNotReceived;
    private CollectionMember<ExecutorsApplyingNotification> execResponseNotificationNo;

    @BeforeEach
    public void setup() {
        openMocks(this);

        underTest = new StateChangeService(applicantSiblingsRule, diedOrNotApplyingRule,
            entitledMinorityRule, executorsStateRule, immovableEstateRule, lifeInterestRule, minorityInterestRule,
            renouncingRule, residuaryRule, solsExecutorRule, spouseOrCivilRule, updateApplicationRule,
            noNotorialWillCopyRule);

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
    void shouldChangeStateForAnyRuleValid() {
        when(executorsStateRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForProbateUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    void shouldChangeStateForAnyNoNotrialRuleValid() {
        when(noNotorialWillCopyRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForProbateUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    void shouldChangeStateForAnyNoNotrialRuleValidAdmon() {
        when(noNotorialWillCopyRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForAdmonUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    void shouldChangeStateForApplicantSiblingsRuleValid() {
        when(applicantSiblingsRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForIntestacyUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    void shouldNOTChangeStateForApplicantSiblingsRule() {
        when(applicantSiblingsRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForIntestacyUpdate(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    void shouldChangeStateForDiedOrNotApplyingRuleValid() {
        when(diedOrNotApplyingRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForAdmonUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    void shouldNOTChangeStateForDiedOrNotApplyingRule() {
        when(diedOrNotApplyingRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForAdmonUpdate(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    void shouldChangeStateForEntitledMinorityRuleValid() {
        when(entitledMinorityRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForAdmonUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    void shouldNOTChangeStateForEntitledMinority() {
        when(entitledMinorityRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForAdmonUpdate(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    void shouldChangeStateForExecutorsRuleValid() {
        when(executorsStateRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForProbateUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    void shouldNOTChangeStateForExecutors() {
        when(executorsStateRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForProbateUpdate(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    void shouldChangeStateForImmovableEstateRuleIntestacyValid() {
        when(immovableEstateRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForIntestacyUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    void shouldNOTChangeStateForImmovableEstateRuleIntestacy() {
        when(immovableEstateRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForIntestacyUpdate(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    void shouldChangeStateForImmovableEstateRuleAdmonValid() {
        when(immovableEstateRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForAdmonUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    void shouldNOTChangeStateForImmovableEstateRuleAdmon() {
        when(immovableEstateRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForAdmonUpdate(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    void shouldChangeStateForLifeInterestRuleValid() {
        when(lifeInterestRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForAdmonUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    void shouldNOTChangeStateForLifeInterestRule() {
        when(lifeInterestRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForAdmonUpdate(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    void shouldChangeStateForMinorityInterestRuleValid() {
        when(minorityInterestRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForIntestacyUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    void shouldNOTChangeStateForMinorityInterestRule() {
        when(minorityInterestRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForIntestacyUpdate(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    void shouldChangeStateForRenouncingRuleValid() {
        when(renouncingRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForIntestacyUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    void shouldNOTChangeStateForRenouncingRule() {
        when(renouncingRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForIntestacyUpdate(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    void shouldChangeStateForResiduaryRuleValid() {
        when(residuaryRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForAdmonUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    void shouldNOTChangeStateForResiduaryRule() {
        when(residuaryRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForAdmonUpdate(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    void shouldChangeStateForSolsExecutorRuleIntestacyValid() {
        when(solsExecutorRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForIntestacyUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    void shouldChangeStateForSolsExecutorRuleAdmonValid() {
        when(solsExecutorRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForAdmonUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    void shouldNOTChangeStateForSolsExecutorRule() {
        when(solsExecutorRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForAdmonUpdate(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    void shouldChangeStateForSpouseOrCivilRuleValid() {
        when(spouseOrCivilRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForIntestacyUpdate(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals("Stopped", newState.get());
    }

    @Test
    void shouldNOTChangeStateForSpouseOrCivilRule() {
        when(spouseOrCivilRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForIntestacyUpdate(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    void shouldNOTChangeStateForAllRulesInvalid() {
        when(executorsStateRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForProbateUpdate(caseDataMock);

        assertFalse(newState.isPresent());
    }

    @Test
    void shouldChangeStateForCaseReview() {
        when(updateApplicationRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForCaseReview(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals(STATE_GRANT_TYPE_CREATED, newState.get());
    }

    @Test
    void shouldChangeStateForCaseReviewOnSelectedLegalStatementChangeAsDeceasedDetails() {
        when(updateApplicationRule.isChangeNeeded(caseDataMock)).thenReturn(true);
        DynamicListItem item = DynamicListItem.builder().code("SolAppCreatedDeceasedDtls").label("label1").build();
        DynamicListItem value = DynamicListItem.builder().code("SolAppCreatedDeceasedDtls").label("label1").build();

        DynamicList list = DynamicList.builder().listItems(Arrays.asList(item)).value(value).build();
        when(caseDataMock.getSolsAmendLegalStatmentSelect()).thenReturn(list);

        Optional<String> newState = underTest.getChangedStateForCaseReview(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals(STATE_GRANT_TYPE_CREATED, newState.get());
    }

    @Test
    void shouldChangeStateForCaseReviewOnSelectedLegalStatementChangeAsProbate() {
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
    void shouldChangeStateForCaseReviewOnSelectedLegalStatementChangeAsIntestacy() {
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
    void shouldChangeStateForCaseReviewOnSelectedLegalStatementChangeAsAdmon() {
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
    void shouldChangeStateForGrantTypeProbate() {
        when(caseDataMock.getSolsWillType()).thenReturn(WILL_TYPE_PROBATE);

        Optional<String> newState = underTest.getChangedStateForGrantType(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals(STATE_GRANT_TYPE_PROBATE, newState.get());
    }

    @Test
    void shouldChangeStateForGrantTypeIntestacy() {
        when(caseDataMock.getSolsWillType()).thenReturn(WILL_TYPE_INTESTACY);

        Optional<String> newState = underTest.getChangedStateForGrantType(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals(STATE_GRANT_TYPE_INTESTACY, newState.get());
    }

    @Test
    void shouldChangeStateForGrantTypeAdmon() {
        when(caseDataMock.getSolsWillType()).thenReturn(WILL_TYPE_ADMON);

        Optional<String> newState = underTest.getChangedStateForGrantType(caseDataMock);

        assertTrue(newState.isPresent());
        assertEquals(STATE_GRANT_TYPE_ADMON, newState.get());
    }

    @Test
    void shouldNOTChangeStateForReview() {
        when(updateApplicationRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForCaseReview(caseDataMock);

        assertFalse(newState.isPresent());
    }

    @Test
    void getRedeclarationCompleteNoStateChange() {
        execList.add(execResponseReceived);
        CaseData caseData = CaseData.builder().executorsApplyingNotifications(execList).build();

        Optional<String> state = underTest.getRedeclarationComplete(caseData);
        assertEquals(Optional.empty(), state);
    }

    @Test
    void getRedeclarationCompleteWithStateChange() {
        execList.add(execResponseReceived);
        execList.add(execResponseNotReceived);
        CaseData caseData = CaseData.builder().executorsApplyingNotifications(execList).build();

        Optional<String> state = underTest.getRedeclarationComplete(caseData);
        assertEquals(Optional.of(REDEC_NOTIFICATION_SENT_STATE), state);
    }

    @Test
    void getRedeclarationCompleteWithStateChangeNotificationNo() {
        execList.add(execResponseReceived);
        execList.add(execResponseNotificationNo);
        CaseData caseData = CaseData.builder().executorsApplyingNotifications(execList).build();

        Optional<String> state = underTest.getRedeclarationComplete(caseData);
        assertEquals(Optional.empty(), state);
    }
}
