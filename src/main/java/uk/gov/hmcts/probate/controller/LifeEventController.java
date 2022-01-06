package uk.gov.hmcts.probate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.LifeEventCCDService;
import uk.gov.hmcts.probate.service.LifeEventCallbackResponseService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.LifeEventValidationRule;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/lifeevent")
public class LifeEventController {

    private final CallbackResponseTransformer callbackResponseTransformer;
    private final LifeEventCCDService lifeEventCCDService;
    private final LifeEventCallbackResponseService lifeEventCallBackResponseService;
    private final SecurityUtils securityUtils;
    private final LifeEventValidationRule lifeEventValidationRule;

    @PostMapping(path = "/update")
    public ResponseEntity<CallbackResponse> update(@RequestBody CallbackRequest request) {
        final CaseDetails caseDetails = request.getCaseDetails();
        lifeEventCCDService.verifyDeathRecord(caseDetails, securityUtils.getSecurityDTO());
        return ResponseEntity.ok(callbackResponseTransformer.updateTaskList(request));
    }

    @PostMapping(path = "/manualUpdateAboutToStart")
    public ResponseEntity<CallbackResponse> manualUpdateAboutToStart(@RequestBody CallbackRequest request) {
        return ResponseEntity.ok(lifeEventCallBackResponseService.getDeathRecordsByNamesAndDate(request));
    }
    
    @PostMapping(path = "/manualUpdate")
    public ResponseEntity<CallbackResponse> manualUpdate(@RequestBody CallbackRequest request) {
        lifeEventValidationRule.validate(request.getCaseDetails());
        return ResponseEntity.ok(callbackResponseTransformer.updateTaskList(request));
    }
    
    @PostMapping(path = "/selectFromMultipleRecordsAboutToStart")
    public ResponseEntity<CallbackResponse> countRecords(@RequestBody CallbackRequest request) {
        return ResponseEntity.ok(lifeEventCallBackResponseService.setNumberOfDeathRecords(request));
    } 

    @PostMapping(path = "/selectFromMultipleRecords")
    public ResponseEntity<CallbackResponse> selectFromMultipleRecords(@RequestBody CallbackRequest request) {
        lifeEventValidationRule.validate(request.getCaseDetails());
        return ResponseEntity.ok(callbackResponseTransformer.updateTaskList(request));
    }
}
