package uk.gov.hmcts.probate.transformer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.DocumentCaseType;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.time.Clock;
import java.time.LocalDate;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
@RequiredArgsConstructor
public class GrantIssueTooEarlyTransformer {
    private final Clock clock;

    public String defaultIssueTooEarlySwitch(CaseData caseData) {
        LocalDate dod = caseData.getDeceasedDateOfDeath();
        final LocalDate now = LocalDate.now(clock);
        int minDays = 0;
        DocumentCaseType docCaseType = DocumentCaseType.getCaseType(caseData.getCaseType());
        if (docCaseType == DocumentCaseType.GOP || docCaseType == DocumentCaseType.ADMON_WILL) {
            minDays = 7;
        } else if (docCaseType == DocumentCaseType.INTESTACY || docCaseType == DocumentCaseType.AD_COLLIGENDA_BONA) {
            minDays = 14;
        }
        if (dod != null && minDays > 0 && !now.isAfter(dod.plusDays(minDays))) {
            return YES;
        } else {
            return NO;
        }
    }
}
