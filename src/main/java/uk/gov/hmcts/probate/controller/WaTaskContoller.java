package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.wa.WaTaskService;
import uk.gov.hmcts.probate.service.wa.WorkAllocationToggleService;
import uk.gov.hmcts.probate.utils.TaskUtils;

import java.util.Base64;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.probate.model.Constants.CLIENT_CONTEXT_HEADER_PARAMETER;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.ccd.EventId.AUTO_SELECT_FOR_QA_CREATE_TASK;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/waTaskContoller")
public class WaTaskContoller {

    private final TaskUtils taskUtils;
    private final ObjectMapper objectMapper;
    private final WorkAllocationToggleService workAllocationToggleService;
    private final SecurityUtils securityUtils;
    private final WaTaskService waTaskService;
    public static final String CASE_ID_ERROR = "Case Id: {} ERROR: {}";
    private static final String BO_CASE_STOPPED = "BOCaseStopped";
    private static final String BO_CASE_WORKER_ESCALATION = "BOCaseWorkerEscalation";
    public static final String AUTO_SELECT_FOR_QA_CREATE_TASK_SUMMARY_DESCRIPTION = "Auto Select For QA Create Task";

    @PostMapping(path = "/case-type/updateClientContext",
            consumes = APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> updateClientContext(
            @Valid @RequestBody CallbackRequest callbackRequest,
            @RequestHeader(value = CLIENT_CONTEXT_HEADER_PARAMETER,
                    required = false) String clientContext,
            BindingResult bindingResult,
            HttpServletRequest request) {
        if (workAllocationToggleService.isProbateWAEnabled()) {
            logRequest(request.getRequestURI(), callbackRequest);

            if (bindingResult.hasErrors()) {
                log.error(CASE_ID_ERROR, callbackRequest.getCaseDetails().getId(), bindingResult);
                throw new BadRequestException("Invalid payload", bindingResult);
            }

            ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok();
            Optional<String> encodedClientContext = taskUtils.setTaskCompletion(
                    clientContext,
                    callbackRequest,
                    paramCallbackRequest ->
                            !paramCallbackRequest.getCaseDetails().getData().getCaseType()
                                    .equals(paramCallbackRequest.getCaseDetailsBefore().getData().getCaseType())

            );

            encodedClientContext
                    .ifPresent(value -> {
                        log.debug("Updated case id's {} client context {}",
                                callbackRequest.getCaseDetails().getId(),
                                new String(Base64.getDecoder().decode(value)));
                        responseBuilder.header(CLIENT_CONTEXT_HEADER_PARAMETER, value);
                    });
            return responseBuilder.body(CallbackResponse.builder().build());
        }
        return ResponseEntity.ok(CallbackResponse.builder().build());
    }

    @PostMapping(path = "/evidence-handled/updateClientContext",
            consumes = APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> updateClientContextEvidenceHandled(
            @Valid @RequestBody CallbackRequest callbackRequest,
            @RequestHeader(value = CLIENT_CONTEXT_HEADER_PARAMETER,
                    required = false) String clientContext,
            BindingResult bindingResult,
            HttpServletRequest request) {
        if (workAllocationToggleService.isProbateWAEnabled()) {
            logRequest(request.getRequestURI(), callbackRequest);

            if (bindingResult.hasErrors()) {
                log.error(CASE_ID_ERROR, callbackRequest.getCaseDetails().getId(), bindingResult);
                throw new BadRequestException("Invalid payload", bindingResult);
            }

            ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok();
            Optional<String> encodedClientContext = taskUtils.setTaskCompletion(
                    clientContext,
                    callbackRequest,
                    paramCallbackRequest -> {
                        log.debug("paramCallbackRequest.getCaseDetails().getState() {}", paramCallbackRequest
                                .getCaseDetails().getState());
                        if (BO_CASE_STOPPED.equals(paramCallbackRequest.getCaseDetails().getState())) {
                            String evidenceHandled = paramCallbackRequest.getCaseDetails().getData()
                                    .getEvidenceHandled();
                            return YES.equals(evidenceHandled);

                        } else if (BO_CASE_WORKER_ESCALATION.equals(paramCallbackRequest.getCaseDetails().getState())) {
                            String evidenceHandled = paramCallbackRequest.getCaseDetails().getData()
                                    .getEvidenceHandled();
                            return NO.equals(evidenceHandled);
                        } else {
                            return true;
                        }
                    }
            );
            encodedClientContext
                    .ifPresent(value -> {
                        log.debug("Updated case id's {} client context {}",
                                callbackRequest.getCaseDetails().getId(),
                                new String(Base64.getDecoder().decode(value)));
                        responseBuilder.header(CLIENT_CONTEXT_HEADER_PARAMETER, value);
                    });
            return responseBuilder.body(CallbackResponse.builder().build());
        }
        return ResponseEntity.ok(CallbackResponse.builder().build());
    }

    @PostMapping(path = "/select-for-qa/setup-wa-task",
            consumes = APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> selectForQASetupWATask(
            @Valid @RequestBody CallbackRequest callbackRequest,
            @RequestHeader(value = CLIENT_CONTEXT_HEADER_PARAMETER,
                    required = false) String clientContext,
            BindingResult bindingResult,
            HttpServletRequest request) {
        if (workAllocationToggleService.isProbateWAEnabled()) {
            logRequest(request.getRequestURI(), callbackRequest);

            if (bindingResult.hasErrors()) {
                log.error(CASE_ID_ERROR, callbackRequest.getCaseDetails().getId(), bindingResult);
                throw new BadRequestException("Invalid payload", bindingResult);
            }

            ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok();
            Optional<String> encodedClientContext = taskUtils.setTaskCompletion(
                    clientContext,
                    callbackRequest,
                    paramCallbackRequest -> {
                        String evidenceHandled = paramCallbackRequest.getCaseDetails().getData().getEvidenceHandled();
                        return NO.equals(evidenceHandled);
                    }
            );
            encodedClientContext
                    .ifPresent(value -> {
                        log.debug("Updated case id's {} client context {}",
                                callbackRequest.getCaseDetails().getId(),
                                new String(Base64.getDecoder().decode(value)));
                        responseBuilder.header(CLIENT_CONTEXT_HEADER_PARAMETER, value);
                    });

            log.info("Case Type: {}, Case ID: {}",
                    callbackRequest.getCaseDetails().getData().getCaseType(),
                    callbackRequest.getCaseDetails().getId().toString());

            waTaskService.createAndSubmitTaskForSystemUser(callbackRequest, AUTO_SELECT_FOR_QA_CREATE_TASK,
                    AUTO_SELECT_FOR_QA_CREATE_TASK_SUMMARY_DESCRIPTION,
                    AUTO_SELECT_FOR_QA_CREATE_TASK_SUMMARY_DESCRIPTION);

            return responseBuilder.body(CallbackResponse.builder().build());
        }
        return ResponseEntity.ok(CallbackResponse.builder().build());
    }


    private void logRequest(String uri, CallbackRequest callbackRequest) {
        try {
            log.info("POST: {} Case Id: {} ", uri, callbackRequest.getCaseDetails().getId().toString());
            log.debug("POST: {} {}", uri, objectMapper.writeValueAsString(callbackRequest));
        } catch (JsonProcessingException e) {
            log.error("POST: {}", uri, e);
        }
    }
}
