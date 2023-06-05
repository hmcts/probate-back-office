package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.caseaccess.ChangeOrganisationRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.noc.FindUsersByOrganisationResponse;
import uk.gov.hmcts.probate.model.noc.ProfessionalUser;
import uk.gov.hmcts.probate.service.caseaccess.AssignCaseAccessClient;
import uk.gov.hmcts.probate.service.caseaccess.NoticeOfChangeClient;
import uk.gov.hmcts.probate.service.organisations.OrganisationsRetrievalService;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;


import java.util.List;
import java.util.Map;
import java.util.Objects;

import static uk.gov.hmcts.probate.model.caseaccess.DecisionCCDRequest.decisionCCDRequest;

@Slf4j
@RequiredArgsConstructor
@Service
public class NoticeOfChangeService {

    private final AuthTokenGenerator authTokenGenerator;
    private final NoticeOfChangeClient noticeOfChangeClient;
    private final IdamApi idamApi;
    private final OrganisationsRetrievalService organisationsRetrievalService;

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

    public void showProfessionUsers(String authorisation){
        ResponseEntity<Map<String, Object>> userResponse = idamApi.getUserDetails(authorisation);
        Map<String, Object> result = Objects.requireNonNull(userResponse.getBody());
        String userId = result.get("id").toString().toLowerCase();
        result.forEach((key, value) -> System.out.println("key="+key+":"+result.get(key)));
        FindUsersByOrganisationResponse usersResponse =organisationsRetrievalService.getOrganisationUsers("XXXXX",authorisation);
        System.out.println(usersResponse);
        List<ProfessionalUser> users = Objects.requireNonNull(usersResponse.getUsers());
        //Map<String, Object> orgUsersResult = Objects.requireNonNull(userResponse.getBody());
        users.forEach((user) -> System.out.println("getEmail="+user.getEmail()+":"+user.getFirstName()+" "+ user.getLastName()));
    }
    public void nocRequestSubmitted(CallbackRequest callbackRequest, String authorisation) {
        System.out.println("nocRequestSubmitted-1111-->");
        CaseDetails caseDetails = callbackRequest.getCaseDetailsBefore();
        CaseDetails newCaseDetails = callbackRequest.getCaseDetails();
        //System.out.println("nocRequestSubmitted-2222-->"+caseDetails);
        Map<String, Object> caseData = caseDetails.getData();
        /*System.out.println("nocRequestSubmitted-333-->\n"+caseData);
        System.out.println("nocRequestSubmitted-333----------------------------------------------->\n");
        System.out.println("nocRequestSubmitted-444-->\n"+newCaseDetails.getData());
        System.out.println("nocRequestSubmitted-444----------------------------------------------->\n");*/
        ResponseEntity<Map<String, Object>> userResponse = idamApi.getUserDetails(authorisation);
        Map<String, Object> result = Objects.requireNonNull(userResponse.getBody());
        String userId = result.get("id").toString().toLowerCase();
        result.forEach((key, value) -> System.out.println("key="+key+":"+result.get(key)));
        FindUsersByOrganisationResponse usersResponse =organisationsRetrievalService.getOrganisationUsers("XXXXX",authorisation);
        //Map<String, Object> orgUsersResult = Objects.requireNonNull(userResponse.getBody());
        //usersResponse.forEach((key, value) -> System.out.println("key="+key+":"+result.get(key)));

        //ChangeOrganisationRequest changeOrganisationRequest = (ChangeOrganisationRequest)caseData.get("changeOrganisationRequestField");
        //System.out.println("nocRequestSubmitted-4444-->"+changeOrganisationRequest);
        //CaseData newCaseData = getCaseData(callbackRequest.getCaseDetails(), objectMapper);
        //ChangeOrganisationRequest changeOrganisationRequest = caseDetails.getChangeOrganisationRequestField();
    }
}