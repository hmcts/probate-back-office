package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
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
import uk.gov.hmcts.probate.service.wa.WorkAllocationToggleService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.utils.TaskUtils;

import java.util.Base64;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.probate.model.Constants.CLIENT_CONTEXT_HEADER_PARAMETER;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/waTaskContoller")
public class WaTaskContoller {

    private final TaskUtils taskUtils;
    private final ObjectMapper objectMapper;
    private final CallbackResponseTransformer callbackResponseTransformer;
    private final WorkAllocationToggleService workAllocationToggleService;
    public static final String CASE_ID_ERROR = "Case Id: {} ERROR: {}";

    @PostMapping(path = "/case-type/updateClientContext",
            consumes = APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> updateClientContext(
            @RequestBody CallbackRequest callbackRequest,
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
                        log.info("Updated client context {}", new String(Base64.getDecoder().decode(value)));
                        responseBuilder.header(CLIENT_CONTEXT_HEADER_PARAMETER, value);
                    });
            CallbackResponse response = callbackResponseTransformer.transform(callbackRequest, Optional.empty());
            //CallbackResponse response = CallbackResponse.builder().build();
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
