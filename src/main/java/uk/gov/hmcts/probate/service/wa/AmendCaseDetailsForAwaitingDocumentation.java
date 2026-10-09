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
import static uk.gov.hmcts.probate.model.wa.TaskTypes.EXAMINE_DIGITAL_CASE_ADMON_WILL;
import static uk.gov.hmcts.probate.model.wa.TaskTypes.EXAMINE_DIGITAL_CASE_INTESTACY;
import static uk.gov.hmcts.probate.model.wa.TaskTypes.EXAMINE_DIGITAL_CASE_PROBATE;

@Slf4j
@Component
public class AmendCaseDetailsForAwaitingDocumentation extends AbstractAmendCaseDetails implements CreateTaskProcessor {

    public static final String BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION
            = "boAmendCaseDetailsForAwaitingDocumentation";
    private static final List<TaskTypes> TASKS_AWAITING_DOCUMENTATION_TO_CREATE = List.of(EXAMINE_DIGITAL_CASE_PROBATE,
            EXAMINE_DIGITAL_CASE_INTESTACY,
            EXAMINE_DIGITAL_CASE_ADMON_WILL,
            EXAMINE_DIGITAL_CASE_ADCOLLIGENDA_BONA);

    private final WaTaskService waTaskService;

    public AmendCaseDetailsForAwaitingDocumentation(WaTaskService waTaskService) {
        super(waTaskService);
        this.waTaskService = waTaskService;
    }

    @Override
    public String getEventId() {
        return BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION;
    }

    @Override
    public void process(String authToken, CallbackRequest callbackRequest, ResponseCaseData responseCaseData) {
        boolean caseTypeChanged = waTaskService.getCaseTypePredicate().test(callbackRequest, null);

        Result result = getResult(callbackRequest, responseCaseData);

        boolean isInitialCaseExaminationTaskRequired =
                !caseTypeChanged
                        && !result.caseHandedOffToLegacySite()
                        && !waTaskService.isTaskPresent(authToken,
                        callbackRequest.getCaseDetails().getId().toString(),
                        BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION,
                        TASKS_AWAITING_DOCUMENTATION_TO_CREATE);

        responseCaseData.setCreateTask(caseTypeChanged || result.newHandOffPresent()
                || isInitialCaseExaminationTaskRequired
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
