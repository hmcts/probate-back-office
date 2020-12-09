package uk.gov.hmcts.probate.service.consumer.ccd;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.probate.service.consumer.util.ObjectMapperTestUtil;
import uk.gov.hmcts.probate.service.consumer.util.ResourceLoader;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.Event;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCcdConsumerTest {

    @Autowired
    private ObjectMapper objectMapper;

    public static final String CASEWORKER_USERNAME = "caseworkerUsername";
    public static final String CASEWORKER_PASSWORD = "caseworkerPassword";
    public static final String CASE_DATA_CONTENT = "caseDataContent";
    public static final String JURISDICTION = "jurisdictionId";
    public static final String EVENT_ID = "eventId";
    public static final String CASE_TYPE = "caseType";

    public static final String CREATE_APPLICATION_EVENT = "createApplication";
    public static final String APPLY_FOR_GRANT = "applyForGrant";
    public static final String PAYMENT_SUCCESS_APP = "paymentSuccessApp";

    @Value("${idam.caseworker.username}")
    protected String caseworkerUsername;

    @Value("${idam.caseworker.password}")
    protected String caseworkerPwd;

    @Autowired
    protected CoreCaseDataApi coreCaseDataApi;

    @Value("${ccd.jurisdictionid}")
    protected String jurisdictionId;

    @Value("${ccd.casetype}")
    protected String caseType;

    @Value("${ccd.eventid.create}")
    protected String createEventId;

    protected static final String USER_ID = "123456";
    protected static final Long CASE_ID = 1593694526480034L;
    protected static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";
    protected static final String SOME_AUTHORIZATION_TOKEN = "Bearer UserAuthToken";
    protected static final String SOME_SERVICE_AUTHORIZATION_TOKEN = "ServiceToken";
    protected static final String VALID_PAYLOAD_PATH = "json/backoffice-case.json";

    protected Map<String, Object> getCaseDetailsAsMap(String fileName) throws JSONException, IOException {
        File file = getFile(fileName);
        CaseDetails caseDetails = objectMapper.readValue(file, CaseDetails.class);
        Map<String, Object> map = objectMapper.convertValue(caseDetails, Map.class);
        return map;
    }

    protected Map<String, Object> setUpStateMapForProviderWithCaseData(String eventId) throws Exception {
        Map<String, Object> caseDataContentMap = objectMapper.convertValue(setUpCaseDataContent("dsl-backoffice-case.json", eventId), Map.class);
        Map<String, Object> map = setUpStateMapForProvider(eventId);
        map.put(CASE_DATA_CONTENT, caseDataContentMap);
        return map;
    }

    protected Map<String, Object> setUpStateMapForProvider(String eventId) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put(CASEWORKER_USERNAME, caseworkerUsername);
        map.put(CASEWORKER_PASSWORD, caseworkerPwd);
        map.put(JURISDICTION, jurisdictionId);
        map.put(EVENT_ID, eventId);
        map.put(CASE_TYPE, caseType);
        return map;
    }


    protected CaseDataContent setUpCaseDataContent(String fileName, String eventId) throws Exception {
        Map caseDetailsMap = getCaseDetailsAsMap(fileName);
        return CaseDataContent.builder()
            .eventToken("someEventToken")
            .event(
                Event.builder()
                    .id(eventId)
                    .summary("PROBATE")
                    .description("probate")
                    .build()
            ).data(caseDetailsMap.get("case_data"))
            .build();
    }

    protected CaseDataContent getCaseDataContent(String eventId, String validPayloadPath) throws Exception {

        final String caseData = ResourceLoader.loadJson(validPayloadPath);
        final CaseDataContent caseDataContent = CaseDataContent.builder()
            .eventToken(SOME_AUTHORIZATION_TOKEN)
            .event(
                Event.builder()
                    .id(eventId)
                    .summary("probateSummary")
                    .description("probate")
                    .build()
            ).data(ObjectMapperTestUtil.convertStringToObject(caseData, Map.class))
            .build();

        return caseDataContent;
    }

    private File getFile(String fileName) throws FileNotFoundException {
        return org.springframework.util.ResourceUtils.getFile(this.getClass().getResource("/json/" + fileName));
    }


}

