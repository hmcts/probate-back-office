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
import uk.gov.hmcts.probate.service.user.UserInfoService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.CaseDataTransformer;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;

import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/tasklist")
public class TaskListController {

    private final CallbackResponseTransformer callbackResponseTransformer;
    private final CaseDataTransformer caseDataTransformer;
    private final UserInfoService userInfoService;

    @PostMapping(path = "/update", produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> update(@RequestBody CallbackRequest request) {
        Optional<UserInfo> caseworkerInfo = userInfoService.getCaseworkerInfo();
        return ResponseEntity.ok(callbackResponseTransformer.updateTaskList(request, caseworkerInfo));
    }

    @PostMapping(path = "/updateCasePrinted", produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> updateCasePrinted(@RequestBody CallbackRequest request) {
        caseDataTransformer.transformCaseDataForEvidenceHandled(request);
        caseDataTransformer.transformIhtFormCaseDataByDeceasedDOD(request);
        caseDataTransformer.setApplicationSubmittedDateForPA(request.getCaseDetails());
        Optional<UserInfo> caseworkerInfo = userInfoService.getCaseworkerInfo();
        return ResponseEntity.ok(callbackResponseTransformer.updateTaskList(request, caseworkerInfo));
    }
}
