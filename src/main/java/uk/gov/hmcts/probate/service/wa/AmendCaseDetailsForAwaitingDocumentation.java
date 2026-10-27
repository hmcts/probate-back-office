package uk.gov.hmcts.probate.service.wa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Slf4j
@Component
public class AmendCaseDetailsForAwaitingDocumentation extends AbstractAmendCaseDetails implements CreateTaskProcessor {

    public static final String BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION
            = "boAmendCaseDetailsForAwaitingDocumentation";

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

        Result result = getResult(callbackRequest,
                responseCaseData);

        boolean isInitialCaseExaminationTaskRequired =
                !caseTypeChanged
                        && !result.caseHandedOffToLegacySite()
                        && areAllHandOffsRemoved(callbackRequest);

        responseCaseData.setCreateTask(caseTypeChanged || result.newHandOffPresent()
                || isInitialCaseExaminationTaskRequired
                ? YES : NO);

        log.info("case id {}: caseTypeChanged {},  newHandOffPresent {}"
                        + " new handoffs {}, isInitialCaseExaminationTaskRequired {}",
                callbackRequest.getCaseDetails().getId(),
                caseTypeChanged,
                result.newHandOffPresent(),
                responseCaseData.getWaHandoffReasonList(),
                isInitialCaseExaminationTaskRequired);
    }
}
