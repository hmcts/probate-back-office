package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AdditionalExecutorNotApplying {

    private final String notApplyingExecutorName;
    private final String notApplyingExecutorNameOnWill;
    private final String notApplyingExecutorNameDifferenceComment;
    private final String notApplyingExecutorReason;
    private final String notApplyingExecutorNotified;
    private final String notApplyingExecutorDispenseWithNotice;
    private final String notApplyingExecutorDispenseWithNoticeLeaveGiven;
    private final LocalDate notApplyingExecutorDispenseWithNoticeLeaveGivenDate;

}
