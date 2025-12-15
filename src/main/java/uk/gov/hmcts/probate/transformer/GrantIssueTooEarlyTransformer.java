package uk.gov.hmcts.probate.transformer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData.ResponseCaseDataBuilder;

import java.time.Clock;
import java.time.LocalDate;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
@RequiredArgsConstructor
public class GrantIssueTooEarlyTransformer {
    private final Clock clock;

    public void defaultIssueTooEarlySwitch(CaseData caseData, ResponseCaseDataBuilder responseCaseDataBuilder) {
        LocalDate dod = caseData.getDeceasedDateOfDeath();
        final LocalDate now = LocalDate.now(clock);
        int minDays = 0;
        String caseType = caseData.getCaseType();
        if ("gop".equalsIgnoreCase(caseType) || "AdmonWill".equalsIgnoreCase(caseType)) {
            minDays = 7;
        } else if ("intestacy".equalsIgnoreCase(caseType) || "AdColligendaBona".equalsIgnoreCase(caseType)) {
            minDays = 14;
        }

        if (dod != null && minDays > 0 && !now.isAfter(dod.plusDays(minDays))) {
            responseCaseDataBuilder.issueEarlySwitch(YES);
        } else {
            responseCaseDataBuilder.issueEarlySwitch(NO);
        }
    }
}
