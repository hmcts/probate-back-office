package uk.gov.hmcts.probate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DeathRecord;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.LifeEventService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.LifeEventValidationRule;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/lifeevent")
public class LifeEventController {

    private final CallbackResponseTransformer callbackResponseTransformer;
    private final LifeEventService lifeEventService;
    private final SecurityUtils securityUtils;
    private final LifeEventValidationRule lifeEventValidationRule;

    @PostMapping(path = "/update")
    public ResponseEntity<CallbackResponse> update(@RequestBody CallbackRequest request) {
        final CaseDetails caseDetails = request.getCaseDetails();
        lifeEventService.verifyDeathRecord(caseDetails, securityUtils.getSecurityDTO());
        return ResponseEntity.ok(callbackResponseTransformer.updateTaskList(request));
    }

    @PostMapping(path = "/manualUpdateAboutToStart")
    public ResponseEntity<CallbackResponse> manualUpdateAboutToStart(@RequestBody CallbackRequest request) {
        final CaseDetails caseDetails = request.getCaseDetails();
        final List<uk.gov.hmcts.probate.model.ccd.raw.CollectionMember<DeathRecord>> deathRecords
            = lifeEventService.getDeathRecordsByNamesAndDate(caseDetails);
        final CallbackResponse response = callbackResponseTransformer.updateTaskList(request);
        response.getData().setDeathRecords(deathRecords);
        response.getData().setNumberOfDeathRecords(deathRecords.size());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping(path = "/manualUpdate")
    public ResponseEntity<CallbackResponse> manualUpdate(@RequestBody CallbackRequest request) {
        lifeEventValidationRule.validate(request.getCaseDetails());
        return ResponseEntity.ok(callbackResponseTransformer.updateTaskList(request));
    }

    @PostMapping(path = "/updateWithSystemNumber")
    public ResponseEntity<CallbackResponse> updateWithSystemNumber(@RequestBody CallbackRequest request) {
        final Integer systemNumber = request.getCaseDetails().getData().getDeathRecordSystemNumber();
        final DeathRecord deathRecord = lifeEventService.getDeathRecordById(systemNumber);
        final CallbackResponse response = callbackResponseTransformer.updateTaskList(request);
        response.getData().setDeathRecord(deathRecord);
        response.getData().setDeathRecords(List.of(new CollectionMember<>(null, deathRecord)));
        return ResponseEntity.ok(response);
    }
    
    @PostMapping(path = "/selectFromMultipleRecordsAboutToStart")
    public ResponseEntity<CallbackResponse> countRecords(@RequestBody CallbackRequest request) {
        final List<CollectionMember<DeathRecord>> deathRecords = request.getCaseDetails().getData().getDeathRecords();
        final CallbackResponse response = callbackResponseTransformer.updateTaskList(request);
        response.getData().setNumberOfDeathRecords(deathRecords == null ? null : deathRecords.size());
        return ResponseEntity.ok(response);
    } 

    @PostMapping(path = "/selectFromMultipleRecords")
    public ResponseEntity<CallbackResponse> selectFromMultipleRecords(@RequestBody CallbackRequest request) {
        lifeEventValidationRule.validate(request.getCaseDetails());
        return ResponseEntity.ok(callbackResponseTransformer.updateTaskList(request));
    }
}
