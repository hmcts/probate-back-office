package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.caseaccess.AssignCaseAccessClient;
import uk.gov.hmcts.probate.service.caseaccess.NoticeOfChangeClient;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;


import static uk.gov.hmcts.probate.model.caseaccess.DecisionCCDRequest.decisionCCDRequest;

@Slf4j
@RequiredArgsConstructor
@Service
public class NoticeOfChangeService {

    private final AuthTokenGenerator authTokenGenerator;
    private final NoticeOfChangeClient noticeOfChangeClient;

    public AboutToStartOrSubmitCallbackResponse applyCCDDecision(CallbackRequest callbackRequest, String authorisation) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        return noticeOfChangeClient.applyDecision(
                authorisation,
                authTokenGenerator.generate(),
                decisionCCDRequest(caseDetails)
        );
    }
}