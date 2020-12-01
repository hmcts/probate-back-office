package uk.hmcts.reform.probate.backoffice;

import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.provider.junit.loader.PactFolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.fluent.Executor;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public abstract class AbstractBackOfficePact {

  public static final String CASEWORKER_USERNAME = "caseworkerUsername";
  public static final String CASEWOKER_PASSWORD = "casewokerPassword";
  public static final String CASE_DATA_CONTENT = "caseDataContent";


  @Autowired
  protected CoreCaseDataApi coreCaseDataApi;

  @Value("${ccd.jurisdictionid}")
  protected String jurisdictionId;

  @Value("${ccd.casetype}")
  protected String caseType;

  @Value("${ccd.eventid.create}")
  protected String createEventId;

  @Value("${idam.caseworker.username}")
  protected String caseworkerUsername;

  @Value("${idam.caseworker.password}")
  protected String caseworkerPwd;

  @Autowired
  private ObjectMapper objectMapper;

  CaseDataContent caseDataContent;

  protected static final String USER_ID = "123456";
  protected static final Long CASE_ID = 1593694526480034L;
  protected static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";

  protected static final String SOME_AUTHORIZATION_TOKEN = "Bearer UserAuthToken";
  protected static final String SOME_SERVICE_AUTHORIZATION_TOKEN = "ServiceToken";

  protected static final String DIVORCE_CASE_SUBMISSION_EVENT_SUMMARY = "Divorce case submission event";
  protected static final String DIVORCE_CASE_SUBMISSION_EVENT_DESCRIPTION = "Submitting Divorce Case";

  @BeforeEach
  public void setUp() throws Exception {

  }

  @AfterEach
  void teardown() {
    Executor.closeIdleConnections();
  }

  private File getFile(String fileName) throws FileNotFoundException {
    return org.springframework.util.ResourceUtils.getFile(this.getClass().getResource("/json/" + fileName));
  }

  protected CaseDetails getCaseDetails(String fileName) throws JSONException, IOException {
    File file = getFile(fileName);
    return  objectMapper.readValue(file, CaseDetails.class);
  }

  protected Map<String, Object> getCaseDetailsAsMap(String fileName) throws JSONException, IOException {
    File file = getFile(fileName);
    CaseDetails caseDetails =   objectMapper.readValue(file, CaseDetails.class);
    Map<String, Object> map = objectMapper.convertValue(caseDetails, Map.class);
    return map;
  }

  protected Map<String, Object> getCaseDataContentAsMap(CaseDataContent caseDataContent) throws JSONException {
    Map<String, Object> caseDataContentMap = objectMapper.convertValue(caseDataContent, Map.class);
    Map<String, Object> map = new HashMap<>();
    map.put(CASEWORKER_USERNAME, caseworkerUsername);
    map.put(CASEWOKER_PASSWORD, caseworkerPwd);
    map.put(CASE_DATA_CONTENT, caseDataContentMap);
    return map;
  }

}

