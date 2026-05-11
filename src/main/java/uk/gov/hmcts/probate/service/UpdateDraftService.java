package uk.gov.hmcts.probate.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;


@Component
public class UpdateDraftService {

    private static final String DECLARATION = "/declaration";
    /*
    private static final String EXECUTOR_CONTACT_DETAILS = "/executor-contact-details";
    private static final String EXECUTORS_INVITE = "/executors-invite";
    private static final String EXECUTORS_UPDATE_INVITE = "/executors-update-invite";
    */

    public void update(CallbackRequest callbackRequest) {
        final CaseData caseData = callbackRequest.getCaseDetails().getData();
        final String eventDescription = caseData.getEventDescription();
        if (StringUtils.isEmpty(eventDescription)) {
            return;
        }
        if (eventDescription.contains(DECLARATION)) {
            if (shouldResetAgreeFlag(caseData)) {
                caseData.resetExecutorsApplyingAgreedFlags();
            }
        }
    }

    private boolean shouldResetAgreeFlag(CaseData caseData) {
        return caseData.hasDataChanged() && caseData.inviteSent();
    }
}
