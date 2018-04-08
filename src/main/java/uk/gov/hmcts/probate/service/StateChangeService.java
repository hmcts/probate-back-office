package uk.gov.hmcts.probate.service;

import lombok.Data;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.changerule.CheckYourAnswersRule;
import uk.gov.hmcts.probate.changerule.DomicilityRule;
import uk.gov.hmcts.probate.changerule.ExecutorsRule;
import uk.gov.hmcts.probate.changerule.NoOriginalWillRule;
import uk.gov.hmcts.probate.changerule.NoWillRule;
import uk.gov.hmcts.probate.changerule.StatementOfTruthRule;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.Optional;

@Data
@Component
public class StateChangeService {
    private static final String STATE_STOPPED = "Stopped";

    private final NoWillRule noWillRule;
    private final NoOriginalWillRule noOriginalWillRule;
    private final CheckYourAnswersRule checkYourAnswersRule;
    private final StatementOfTruthRule statementOfTruthStateChangeRule;
    private final DomicilityRule domicilityRule;
    private final ExecutorsRule executorsRule;

    public Optional<String> getChangedStateForWillDetails(CaseData caseData) {
        if (noWillRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }
        if (noOriginalWillRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }
        return Optional.empty();
    }

    public Optional<String> getChangedStateForCheckYourAnswers(CaseData caseData) {
        if (checkYourAnswersRule.isChangeNeeded(caseData)) {
            return Optional.of(caseData.getSolsCYAStateTransition());
        }
        return Optional.empty();
    }

    public Optional<String> getChangedStateForStatementOfTruth(CaseData caseData) {
        if (statementOfTruthStateChangeRule.isChangeNeeded(caseData)) {
            return Optional.of(caseData.getSolsSOTStateTransition());
        }
        return Optional.empty();
    }

    public Optional<String> getChangedStateForDomicility(CaseData caseData) {
        if (domicilityRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }
        return Optional.empty();
    }

    public Optional<String> getChangedStateForExecutors(CaseData caseData) {
        if (executorsRule.isChangeNeeded(caseData)) {
            return Optional.of(STATE_STOPPED);
        }
        return Optional.empty();
    }
}
