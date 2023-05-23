package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.caseaccess.ChangeOrganisationRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.caseaccess.AssignCaseAccessClient;
import uk.gov.hmcts.probate.service.caseaccess.NoticeOfChangeClient;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;


import java.util.Map;

import static uk.gov.hmcts.probate.model.caseaccess.DecisionCCDRequest.decisionCCDRequest;

@Slf4j
@RequiredArgsConstructor
@Service
public class NoticeOfChangeService {

    private final AuthTokenGenerator authTokenGenerator;
    private final NoticeOfChangeClient noticeOfChangeClient;

    public AboutToStartOrSubmitCallbackResponse applyCCDDecision(CallbackRequest callbackRequest, String authorisation) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        Map<String, Object> caseData = caseDetails.getData();
        caseData.put("deceasedForenames","deceasedForenames123");
        System.out.println("applyCCDDecision-1111-->");
        caseDetails.getData().putAll(caseData);
        System.out.println("applyCCDDecision-2222-->");
        caseData = caseDetails.getData();
        System.out.println("applyCCDDecision-3333-->"+caseData.get("deceasedForenames"));
        return noticeOfChangeClient.applyDecision(
                authorisation,
                authTokenGenerator.generate(),
                decisionCCDRequest(caseDetails)
        );
    }

    public void nocRequestSubmitted(CallbackRequest callbackRequest, String authorisation) {
        System.out.println("nocRequestSubmitted-1111-->");
        CaseDetails caseDetails = callbackRequest.getCaseDetailsBefore();
        System.out.println("nocRequestSubmitted-2222-->"+caseDetails);
        Map<String, Object> caseData = caseDetails.getData();
        System.out.println("nocRequestSubmitted-333-->"+caseData);
        //ChangeOrganisationRequest changeOrganisationRequest = (ChangeOrganisationRequest)caseData.get("changeOrganisationRequestField");
        //System.out.println("nocRequestSubmitted-4444-->"+changeOrganisationRequest);
        //CaseData newCaseData = getCaseData(callbackRequest.getCaseDetails(), objectMapper);
        //ChangeOrganisationRequest changeOrganisationRequest = caseDetails.getChangeOrganisationRequestField();
    }
}