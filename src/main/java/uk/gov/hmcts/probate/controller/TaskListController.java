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
import uk.gov.hmcts.probate.transformer.CaseDataTransformer;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/tasklist")
public class TaskListController {

    private final CallbackResponseTransformer callbackResponseTransformer;
    private final CaseDataTransformer caseDataTransformer;

    @PostMapping(path = "/update")
    public ResponseEntity<CallbackResponse> update(@RequestBody CallbackRequest request) {
        return ResponseEntity.ok(callbackResponseTransformer.updateTaskList(request));
    }

    @PostMapping(path = "/updateCasePrinted")
    public ResponseEntity<CallbackResponse> updateCasePrinted(@RequestBody CallbackRequest request) {
        caseDataTransformer.transformCaseDataForEvidenceHandled(request);
        return ResponseEntity.ok(callbackResponseTransformer.updateTaskList(request));
    }
}
