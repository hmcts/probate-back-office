package uk.gov.hmcts.probate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.CaseMatchingService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RequestMapping(value = "/case-matching", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_VALUE)
@RestController
public class CaseMatchingController {

    private final CallbackResponseTransformer callbackResponseTransformer;
    private final CaseMatchingService caseMatchingService;

    @PostMapping(path = "/search")
    public ResponseEntity<CallbackResponse> search(@RequestBody CallbackRequest callbackRequest) {
        List<CaseMatch> caseMatches = caseMatchingService.findMatches(callbackRequest.getCaseDetails());

        return ResponseEntity.ok(callbackResponseTransformer.addMatches(callbackRequest, caseMatches));
    }
}
