package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.changerule.DomicilityRule;
import uk.gov.hmcts.probate.changerule.ExecutorsRule;
import uk.gov.hmcts.probate.changerule.MinorityRule;
import uk.gov.hmcts.probate.changerule.NoOriginalWillRule;
import uk.gov.hmcts.probate.changerule.UpdateApplicationRule;

import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StateChangeService {
    private static final String STATE_STOPPED = "Stopped";
    private static final String STATE_GRANT_TYPE_PROBATE = "SolProbateCreated";
    private static final String STATE_GRANT_TYPE_INTESTACY = "SolIntestacyCreated";
    private static final String STATE_GRANT_TYPE_ADMON = "SolAdmonCreated";

    private static final String GRANT_TYPE_PROBATE = "WillLeft";
    private static final String GRANT_TYPE_INTESTACY = "NoWill";


    private final NoOriginalWillRule noOriginalWillRule;
    private final DomicilityRule domicilityRule;
    private final ExecutorsRule executorsRule;
    private final UpdateApplicationRule updateApplicationRule;
    private final MinorityRule minorityRule;

    public Optional<String> getChangedStateForCaseUpdate(CaseData caseData) {
        if (noOriginalWillRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }
        if (domicilityRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }
        if (executorsRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }
        return Optional.empty();
    }

    public Optional<String> getChangedStateForProbateUpdate(CaseData caseData) {
        if (noOriginalWillRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }
        if (domicilityRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }
        if (executorsRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }
        return Optional.empty();
    }

    public Optional<String> getChangedStateForIntestacyUpdate(CaseData caseData) {
        if (domicilityRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }

        if (minorityRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }
        return Optional.empty();
    }

    public Optional<String> getChangedStateForAdmonUpdate(CaseData caseData) {
        if (noOriginalWillRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }
        if (domicilityRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }
        return Optional.empty();
    }

    public Optional<String> getChangedStateForCaseReview(CaseData caseData) {
        if (updateApplicationRule.isChangeNeeded(caseData)) {
            if (caseData.getSolsWillType().equals(GRANT_TYPE_PROBATE)) {
                return Optional.of(STATE_GRANT_TYPE_PROBATE);
            } else if (caseData.getSolsWillType().equals(GRANT_TYPE_INTESTACY)) {
                return Optional.of(STATE_GRANT_TYPE_INTESTACY);
            }
            return Optional.of(STATE_GRANT_TYPE_ADMON);
        }
        return Optional.empty();
    }

    public Optional<String> getChangedStateForGrantType(CaseData caseData) {
        if (caseData.getSolsWillType().equals(GRANT_TYPE_PROBATE)) {
            return Optional.of(STATE_GRANT_TYPE_PROBATE);
        } else if (caseData.getSolsWillType().equals(GRANT_TYPE_INTESTACY)) {
            return Optional.of(STATE_GRANT_TYPE_INTESTACY);
        }
        return Optional.of(STATE_GRANT_TYPE_ADMON);
    }
}
