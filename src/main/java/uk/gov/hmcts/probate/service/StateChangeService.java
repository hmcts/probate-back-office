package uk.gov.hmcts.probate.service;

import lombok.Data;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.changerule.DomicilityRule;
import uk.gov.hmcts.probate.changerule.ExecutorsRule;
import uk.gov.hmcts.probate.changerule.NoOriginalWillRule;
import uk.gov.hmcts.probate.changerule.NoWillRule;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.Optional;

@Data
@Component
public class StateChangeService {
    private static final String STATE_STOPPED = "Stopped";

    private final NoWillRule noWillRule;
    private final NoOriginalWillRule noOriginalWillRule;
    private final DomicilityRule domicilityRule;
    private final ExecutorsRule executorsRule;

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
}
