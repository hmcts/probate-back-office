package uk.gov.hmcts.probate.service.wa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.wa.TaskTypes;

import java.util.Set;

import static uk.gov.hmcts.probate.model.Constants.YES;

@Slf4j
@Component
public class AmendCaseDetailsForReadyToIssue extends AbstractAmendCaseDetails implements CreateTaskProcessor {

    private static final String BO_AMEND_CASE_DETAILS_FOR_READY_TO_ISSUE = "boAmendCaseDetailsForReadyToIssue";
    private final WaTaskService waTaskService;

    public AmendCaseDetailsForReadyToIssue(WaTaskService waTaskService) {
        super(waTaskService);
        this.waTaskService = waTaskService;
    }

    @Override
    public String getEventId() {
        return BO_AMEND_CASE_DETAILS_FOR_READY_TO_ISSUE;
    }

    @Override
    public void process(final String authToken,
                        final CallbackRequest callbackRequest,
                        final ResponseCaseData responseCaseData) {
        boolean caseTypeChanged = waTaskService.getCaseTypePredicate().test(callbackRequest, null);

        Result result = getResult(
                callbackRequest,
                responseCaseData);

        boolean isInitialCaseExaminationTaskRequired =
                !caseTypeChanged
                && !result.caseHandedOffToLegacySite()
                && areAllHandOffsRemoved(callbackRequest);

        responseCaseData.setCreateTask(caseTypeChanged
                || result.newHandOffPresent() || isInitialCaseExaminationTaskRequired
                ? YES : Constants.NO);

        log.info("case id {}: caseTypeChanged {},  newHandOffPresent {}"
                        + " new handoffs {}, isInitialCaseExaminationTaskRequired {}",
                callbackRequest.getCaseDetails().getId(),
                caseTypeChanged,
                result.newHandOffPresent(),
                responseCaseData.getWaHandoffReasonList(),
                isInitialCaseExaminationTaskRequired);
    }
}
