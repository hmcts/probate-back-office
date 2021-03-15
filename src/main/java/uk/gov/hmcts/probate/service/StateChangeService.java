package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
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
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;

import java.util.Optional;

import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_INTESTACY;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_PROBATE;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.REDEC_NOTIFICATION_SENT_STATE;
import static uk.gov.hmcts.probate.model.Constants.STATE_GRANT_TYPE_ADMON;
import static uk.gov.hmcts.probate.model.Constants.STATE_GRANT_TYPE_CREATED_DECEASED_DTLS;
import static uk.gov.hmcts.probate.model.Constants.STATE_GRANT_TYPE_CREATED_SOLICITOR_DTLS;
import static uk.gov.hmcts.probate.model.Constants.STATE_GRANT_TYPE_INTESTACY;
import static uk.gov.hmcts.probate.model.Constants.STATE_GRANT_TYPE_PROBATE;
import static uk.gov.hmcts.probate.model.Constants.STATE_STOPPED;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
@RequiredArgsConstructor
public class StateChangeService {

    private final ApplicantSiblingsRule applicantSiblingsRule;
    private final DiedOrNotApplyingRule diedOrNotApplyingRule;
    private final EntitledMinorityRule entitledMinorityRule;
    private final ExecutorsRule executorsRule;
    private final ImmovableEstateRule immovableEstateRule;
    private final LifeInterestRule lifeInterestRule;
    private final MinorityInterestRule minorityInterestRule;
    private final NoOriginalWillRule noOriginalWillRule;
    private final RenouncingRule renouncingRule;
    private final ResiduaryRule residuaryRule;
    private final SolsExecutorRule solsExecutorRule;
    private final SpouseOrCivilRule spouseOrCivilRule;
    private final UpdateApplicationRule updateApplicationRule;
    private final CallbackResponseTransformer callbackResponseTransformer;


    public Optional<String> getChangedStateForProbateUpdate(CaseData caseData) {
        if (noOriginalWillRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }

        if (executorsRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }
        return Optional.empty();
    }

    public Optional<String> getChangedStateForIntestacyUpdate(CaseData caseData) {
        if (minorityInterestRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }

        if (immovableEstateRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }

        if (applicantSiblingsRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }

        if (renouncingRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }

        if (solsExecutorRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }

        if (spouseOrCivilRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }
        return Optional.empty();
    }

    public Optional<String> getChangedStateForAdmonUpdate(CaseData caseData) {
        if (immovableEstateRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }

        if (noOriginalWillRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }

        if (diedOrNotApplyingRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }

        if (entitledMinorityRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }

        if (lifeInterestRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }

        if (residuaryRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }

        if (solsExecutorRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }

        return Optional.empty();
    }

    public Optional<String> getChangedStateForCaseReview(CaseData caseData) {
        if (updateApplicationRule.isChangeNeeded(caseData)) {
            if (hasSelectedEventToReturnTo(caseData)) {
                String chosenStateOrWillType = caseData.getSolsAmendLegalStatmentSelect().getValue().getCode();
                if (STATE_GRANT_TYPE_CREATED_SOLICITOR_DTLS.equals(chosenStateOrWillType)
                    || STATE_GRANT_TYPE_CREATED_DECEASED_DTLS.equals(chosenStateOrWillType)) {
                    return Optional.of(chosenStateOrWillType);
                } else {
                    return getChangedStateForChosen(chosenStateOrWillType);
                }
            }
            return Optional.of(STATE_GRANT_TYPE_CREATED_DECEASED_DTLS);
        }
        return Optional.empty();
    }

    private boolean hasSelectedEventToReturnTo(CaseData caseData) {
        return caseData.getSolsAmendLegalStatmentSelect() != null
            && caseData.getSolsAmendLegalStatmentSelect().getValue() != null
            && caseData.getSolsAmendLegalStatmentSelect().getValue().getCode() != null;
    }

    public Optional<String> getChangedStateForGrantType(CaseData caseData) {
        return getChangedStateForChosen(caseData.getSolsWillType());
    }

    public Optional<String> getRedeclarationComplete(CaseData caseData) {
        Optional<String> state = Optional.empty();
        for (CollectionMember<ExecutorsApplyingNotification> executorsApplyingNotification :
                caseData.getExecutorsApplyingNotifications()) {
            if (YES.equals(executorsApplyingNotification.getValue().getNotification())) {
                if (NO.equals(executorsApplyingNotification.getValue().getResponseReceived())) {
                    return (Optional.of(REDEC_NOTIFICATION_SENT_STATE));
                }
            }
        }
        return state;
    }

    private Optional<String> getChangedStateForChosen(String stateChosen) {
        if (stateChosen.equals(GRANT_TYPE_PROBATE)) {
            return Optional.of(STATE_GRANT_TYPE_PROBATE);
        } else if (stateChosen.equals(GRANT_TYPE_INTESTACY)) {
            return Optional.of(STATE_GRANT_TYPE_INTESTACY);
        }
        return Optional.of(STATE_GRANT_TYPE_ADMON);
    }
}
