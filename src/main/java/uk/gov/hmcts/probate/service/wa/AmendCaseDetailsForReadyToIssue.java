package uk.gov.hmcts.probate.service.wa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.wa.TaskTypes;

import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.wa.TaskTypes.EXAMINE_DIGITAL_CASE_ADCOLLIGENDA_BONA;
import static uk.gov.hmcts.probate.model.wa.TaskTypes.EXAMINE_DIGITAL_CASE_ADCOLLIGENDA_BONA_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.model.wa.TaskTypes.EXAMINE_DIGITAL_CASE_ADMON_WILL;
import static uk.gov.hmcts.probate.model.wa.TaskTypes.EXAMINE_DIGITAL_CASE_ADMON_WILL_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.model.wa.TaskTypes.EXAMINE_DIGITAL_CASE_INTESTACY;
import static uk.gov.hmcts.probate.model.wa.TaskTypes.EXAMINE_DIGITAL_CASE_INTESTACY_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.model.wa.TaskTypes.EXAMINE_DIGITAL_CASE_PROBATE;
import static uk.gov.hmcts.probate.model.wa.TaskTypes.EXAMINE_DIGITAL_CASE_PROBATE_READY_TO_ISSUE;

@Slf4j
@Component
public class AmendCaseDetailsForReadyToIssue extends AbstractAmendCaseDetails implements CreateTaskProcessor {

    private static final String BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION
            = "boAmendCaseDetailsForAwaitingDocumentation";
    private static final String BO_AMEND_CASE_DETAILS_FOR_READY_TO_ISSUE = "boAmendCaseDetailsForReadyToIssue";
    private static final List<TaskTypes> TASKS_AWAITING_DOCUMENTATION_TO_CLOSE = List.of(EXAMINE_DIGITAL_CASE_PROBATE,
            EXAMINE_DIGITAL_CASE_INTESTACY,
            EXAMINE_DIGITAL_CASE_ADMON_WILL,
            EXAMINE_DIGITAL_CASE_ADCOLLIGENDA_BONA);
    private static final List<TaskTypes> TASKS_READY_TO_ISSUE_TO_CREATE
            = List.of(EXAMINE_DIGITAL_CASE_PROBATE_READY_TO_ISSUE,
            EXAMINE_DIGITAL_CASE_INTESTACY_READY_TO_ISSUE,
            EXAMINE_DIGITAL_CASE_ADMON_WILL_READY_TO_ISSUE,
            EXAMINE_DIGITAL_CASE_ADCOLLIGENDA_BONA_READY_TO_ISSUE);

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
    public void process(String authToken, CallbackRequest callbackRequest, ResponseCaseData responseCaseData) {
        boolean caseTypeChanged = waTaskService.getCaseTypePredicate().test(callbackRequest, null);

        boolean taskToClosePresent = !caseTypeChanged
                && waTaskService.isTaskPresent(authToken,
                callbackRequest.getCaseDetails().getId().toString(),
                BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION,
                TASKS_AWAITING_DOCUMENTATION_TO_CLOSE);

        Result result = getResult(callbackRequest, responseCaseData);

        boolean isInitialCaseExaminationTaskRequired =
                !caseTypeChanged
                && !result.caseHandedOffToLegacySite()
                && !waTaskService.isTaskPresent(authToken,
                        callbackRequest.getCaseDetails().getId().toString(),
                        BO_AMEND_CASE_DETAILS_FOR_READY_TO_ISSUE,
                        TASKS_READY_TO_ISSUE_TO_CREATE);

        responseCaseData.setCreateTask(caseTypeChanged || taskToClosePresent
                || result.newHandOffPresent() || isInitialCaseExaminationTaskRequired
                ? YES : Constants.NO);

        log.info("case id {}: caseTypeChanged {}, taskToClosePresent {}, newHandOffPresent {}"
                        + " new handoffs {}, isInitialCaseExaminationTaskRequired {}",
                callbackRequest.getCaseDetails().getId(),
                caseTypeChanged,
                taskToClosePresent,
                result.newHandOffPresent(),
                responseCaseData.getWaHandoffReasonList(),
                isInitialCaseExaminationTaskRequired);
    }
}
