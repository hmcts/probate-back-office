package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.transformer.BOCaseTransformer;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;

import javax.servlet.http.HttpServletRequest;

@Data
@Controller
@RequestMapping("/case")
public class StateController {

    private static final Logger log = LoggerFactory.getLogger(StateController.class);

    private final ObjectMapper objectMapper;
    private final CallbackResponseTransformer callbackResponseTransformer;
    private final BOCaseTransformer boCaseTransformer;

    @PostMapping(path = "/copyState", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> copyState(@RequestBody CallbackRequest callbackRequest, HttpServletRequest request) {

        logRequest(request.getRequestURI(), callbackRequest);

        callbackRequest = boCaseTransformer.transformExecutors(callbackRequest);
        CallbackResponse response = callbackResponseTransformer.addCcdState(callbackRequest);

        return ResponseEntity.ok(response);
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
