package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.changerule.*;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StateChangeService {
    private static final String STATE_STOPPED = "Stopped";
    private static final String STATE_UPDATE_APPLICATION = "SolAppCreated";
    private static final String STATE_GRANT_TYPE_PROBATE = "SolProbateCreated";
    private static final String STATE_GRANT_TYPE_INTESTACY = "SolIntestacyCreated";
    private static final String STATE_GRANT_TYPE_ADMON = "SolAdmonCreated";


    private final NoWillRule noWillRule;
    private final NoOriginalWillRule noOriginalWillRule;
    private final DomicilityRule domicilityRule;
    private final ExecutorsRule executorsRule;
    private final UpdateApplicationRule updateApplicationRule;
    private final GrantTypeRule grantTypeRule;

    public Optional<String> getChangedStateForCaseUpdate(CaseData caseData) {
        if (noWillRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }
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

    public Optional<String> getChangedStateForCaseReview(CaseData caseData) {
        if (updateApplicationRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_UPDATE_APPLICATION);
        }
        return Optional.empty();
    }

    public Optional<String> getChangedStateForGrantType(CaseData caseData) {
        if (!grantTypeRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_GRANT_TYPE_PROBATE);
        } else if(grantTypeRule.isIntestacy(caseData)) {
            return Optional.of(STATE_GRANT_TYPE_INTESTACY);
        }
        return Optional.of(STATE_GRANT_TYPE_ADMON);
    }
}
