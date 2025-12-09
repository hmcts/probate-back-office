package uk.gov.hmcts.probate.transformer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData.ResponseCaseDataBuilder;

import java.time.LocalDate;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
@RequiredArgsConstructor
public class GrantIssueTooEarlyTransformer {

    public void validate(CaseData caseData, ResponseCaseDataBuilder responseCaseDataBuilder) {
        LocalDate dod = caseData.getDeceasedDateOfDeath();
        final LocalDate now = LocalDate.now();
        int minDays = 0;
        String caseType = caseData.getCaseType(); // adjust if field name differs
        if ("gop".equalsIgnoreCase(caseType) || "AdmonWill".equalsIgnoreCase(caseType)) {
            minDays = 8; // 1st + 8 days = 9th
        } else if ("intestacy".equalsIgnoreCase(caseType) || "AdColligendaBona".equalsIgnoreCase(caseType)) {
            minDays = 15; // 1st + 15 days = 16th
        }

        if (dod != null && minDays > 0 && !now.isAfter(dod.plusDays(minDays))) {
            responseCaseDataBuilder.issueEarlySwitch(YES);
        }
        else {
            responseCaseDataBuilder.issueEarlySwitch(NO);
        }
    }
}
