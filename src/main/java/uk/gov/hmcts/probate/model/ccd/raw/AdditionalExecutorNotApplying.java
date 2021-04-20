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

    @Override
    public AdditionalExecutorNotApplying clone() {
        // need to do this as super.clone() doesn't like the localdate
        return AdditionalExecutorNotApplying.builder()
            .notApplyingExecutorDispenseWithNotice(getNotApplyingExecutorDispenseWithNotice())
            .notApplyingExecutorDispenseWithNoticeLeaveGiven(
                    getNotApplyingExecutorDispenseWithNoticeLeaveGiven())
            .notApplyingExecutorDispenseWithNoticeLeaveGivenDate(
                    getNotApplyingExecutorDispenseWithNoticeLeaveGivenDate())
            .notApplyingExecutorName(getNotApplyingExecutorName())
            .notApplyingExecutorNameDifferenceComment(getNotApplyingExecutorNameDifferenceComment())
            .notApplyingExecutorNameOnWill(getNotApplyingExecutorNameOnWill())
            .notApplyingExecutorNotified(getNotApplyingExecutorNotified())
            .notApplyingExecutorReason(getNotApplyingExecutorReason())
            .build();
    }
}
