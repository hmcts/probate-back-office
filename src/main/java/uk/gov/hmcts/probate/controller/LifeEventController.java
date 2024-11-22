package uk.gov.hmcts.probate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.transformer.HandOffLegacyTransformer;
import uk.gov.hmcts.probate.service.LifeEventCCDService;
import uk.gov.hmcts.probate.service.LifeEventCallbackResponseService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.LifeEventValidationRule;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


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
    private final HandOffLegacyTransformer handOffLegacyTransformer;

    @PostMapping(path = "/update", consumes = MediaType.APPLICATION_JSON_VALUE,  produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> update(@RequestBody CallbackRequest request) {
        SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        boolean isCitizenUser = true;
        List<String> roles = securityUtils.getRoles(securityDTO.getAuthorisation());
        log.info("User roles from the token:{}", roles);
        if (roles.contains("caseworker-probate")) {
            securityDTO = securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO();
            isCitizenUser = false;
        }
        final CaseDetails caseDetails = request.getCaseDetails();
        lifeEventCCDService.verifyDeathRecord(caseDetails, securityDTO, isCitizenUser);
        return ResponseEntity.ok(callbackResponseTransformer.updateTaskList(request, null));
    }

    @PostMapping(path = "/manualUpdateAboutToStart", produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> manualUpdateAboutToStart(@RequestBody CallbackRequest request) {
        return ResponseEntity.ok(lifeEventCallBackResponseService.getDeathRecordsByNamesAndDate(request));
    }

    @PostMapping(path = "/manualUpdate", produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> manualUpdate(@RequestHeader(value = "Authorization") String authToken,
                                                         @RequestBody CallbackRequest request) {
        lifeEventValidationRule.validate(request.getCaseDetails());
        return ResponseEntity.ok(callbackResponseTransformer.updateTaskList(request, authToken));
    }

    @PostMapping(path = "/selectFromMultipleRecordsAboutToStart",produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> countRecords(@RequestBody CallbackRequest request) {
        return ResponseEntity.ok(lifeEventCallBackResponseService.setNumberOfDeathRecords(request));
    }

    @PostMapping(path = "/selectFromMultipleRecords", produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> selectFromMultipleRecords(
            @RequestHeader(value = "Authorization") String authToken,
            @RequestBody CallbackRequest request) {
        lifeEventValidationRule.validate(request.getCaseDetails());
        return ResponseEntity.ok(callbackResponseTransformer.updateTaskList(request, authToken));
    }

    @PostMapping(path = "/handOffToLegacySite", produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> handOffToLegacySite(
            @RequestHeader(value = "Authorization") String authToken,
            @RequestBody CallbackRequest request) {
        handOffLegacyTransformer.setHandOffToLegacySiteYes(request);
        return ResponseEntity.ok(callbackResponseTransformer.updateTaskList(request, authToken));
    }
}
