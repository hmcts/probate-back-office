package uk.gov.hmcts.probate.service.wa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.wa.search.enums.TaskTypes;

import java.util.List;

import static uk.gov.hmcts.probate.model.wa.search.enums.TaskTypes.EXAMINE_DIGITAL_CASE_ADCOLLIGENDA_BONA;
import static uk.gov.hmcts.probate.model.wa.search.enums.TaskTypes.EXAMINE_DIGITAL_CASE_ADMON_WILL;
import static uk.gov.hmcts.probate.model.wa.search.enums.TaskTypes.EXAMINE_DIGITAL_CASE_INTESTACY;
import static uk.gov.hmcts.probate.model.wa.search.enums.TaskTypes.EXAMINE_DIGITAL_CASE_PROBATE;

@RequiredArgsConstructor
@Component
public class AmendCaseDetailsForReadyToIssue implements CreateTaskProcessor {
    private final WaTaskService waTaskService;
    private final List<TaskTypes> taskToCLose = List.of(EXAMINE_DIGITAL_CASE_PROBATE,
            EXAMINE_DIGITAL_CASE_INTESTACY,
            EXAMINE_DIGITAL_CASE_ADMON_WILL,
            EXAMINE_DIGITAL_CASE_ADCOLLIGENDA_BONA);

    @Override
    public String getEventId() {
        return "boAmendCaseDetailsForReadyToIssue";
    }

    @Override
    public void process(String authToken, CallbackRequest callbackRequest, ResponseCaseData responseCaseData) {
        boolean caseTypeChanged = !callbackRequest.getCaseDetails().getData().getCaseType()
                .equals(callbackRequest.getCaseDetailsBefore().getData().getCaseType());

        boolean taskToClosePresent = !caseTypeChanged
                && waTaskService.isTaskPresent(authToken,
                callbackRequest.getCaseDetails().getId().toString(),
                "boAmendCaseDetailsForAwaitingDocumentation",
                taskToCLose);


        responseCaseData.setCreateTask(caseTypeChanged || taskToClosePresent
                ? Constants.YES : Constants.NO);
    }
}
