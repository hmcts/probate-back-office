package uk.gov.hmcts.probate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/sol-transform")
public class SolicitorTransformerController {

    private final CallbackResponseTransformer callbackResponseTransformer;

    // Transform executors into applying and not applying lists
    @PostMapping(path = "/executors")
    public ResponseEntity<CallbackResponse> setExecutorLists(@RequestBody CallbackRequest request) {
        return ResponseEntity.ok(callbackResponseTransformer.setExecutorListsForSolicitor(request));
    }

}
