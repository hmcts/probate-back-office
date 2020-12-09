package uk.gov.hmcts.probate.functional.util;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.pdfbox.cos.COSDocument;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.ResourceUtils;
import uk.gov.hmcts.probate.functional.SolCCDServiceAuthTokenGenerator;
import uk.gov.hmcts.probate.functional.TestContextConfiguration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@ContextConfiguration(classes = TestContextConfiguration.class)
@Component
@Slf4j
public class FunctionalTestUtils {
    public static final String TOKEN_PARM = "TOKEN_PARM";

    @Autowired
    protected SolCCDServiceAuthTokenGenerator serviceAuthTokenGenerator;

    @Value("${user.id.url}")
    private String userId;

    private String serviceToken;

    @Value("${probate.caseworker.email}")
    private String caseworkerEmail;

    @Value("${probate.caseworker.password}")
    private String caseworkerPassword;

    @Value("${evidence.management.url}")
    private String dmStoreUrl;

    @Value("${probate.scheduler.username}")
    private String schedulerEmail;

    @Value("${probate.scheduler.password}")
    private String schedulerPassword;

    @Value("${core_case_data.api.url}")
    private String coreCaseDataApiUrl;

    @Value("${user.auth.provider.oauth2.url}")
    private String authProviderUrl;

    @PostConstruct
    public void init() {
        serviceToken = serviceAuthTokenGenerator.generateServiceToken();

        if (userId == null || userId.isEmpty()) {
            serviceAuthTokenGenerator.createNewUser();
            userId = serviceAuthTokenGenerator.getUserId();
        }
    }

    public String getJsonFromFile(String fileName) {
        try {
            File file = ResourceUtils.getFile(this.getClass().getResource("/json/" + fileName));
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getStringFromFile(String fileName) {
        try {
            File file = ResourceUtils.getFile(this.getClass().getResource(fileName));
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Headers getHeaders() {
        return getHeaders(serviceToken);
    }

    public Headers getHeaders(String serviceToken) {
        return Headers.headers(
            new Header("ServiceAuthorization", serviceToken),
            new Header("Content-Type", ContentType.JSON.toString()));
    }

    public Headers getHeadersWithUserId() {
        return getHeadersWithUserId(serviceToken, userId);
    }

    private Headers getHeadersWithUserId(String serviceToken, String userId) {
        return Headers.headers(
            new Header("ServiceAuthorization", serviceToken),
            new Header("Content-Type", ContentType.JSON.toString()),
            new Header("Authorization", serviceAuthTokenGenerator.generateAuthorisation(caseworkerEmail, caseworkerPassword)),
            new Header("user-id", userId));
    }

    public String downloadPdfAndParseToString(String documentUrl) {
        Response document = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(getHeadersWithUserId())
            .when().get(documentUrl.replace("http://dm-store:8080", dmStoreUrl)).andReturn();

        return parsePDFToString(document.getBody().asInputStream());
    }

    public String downloadPdfAndParseToStringForScheduler(String documentUrl) {
        String userId = getSchedulerCaseworkerUserId();
        Response document = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(getHeadersWithUserId(serviceToken, userId))
            .when().get(documentUrl.replace("http://dm-store:8080", dmStoreUrl)).andReturn();

        return parsePDFToString(document.getBody().asInputStream());
    }

    public String parsePDFToString(InputStream inputStream) {

        PDFParser parser;
        PDDocument pdDoc = null;
        COSDocument cosDoc = null;
        PDFTextStripper pdfStripper;
        String parsedText = "";

        try {
            parser = new PDFParser(inputStream);
            parser.parse();
            cosDoc = parser.getDocument();
            pdfStripper = new PDFTextStripper();
            pdDoc = new PDDocument(cosDoc);
            parsedText = pdfStripper.getText(pdDoc);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (cosDoc != null) {
                    cosDoc.close();
                }
                if (pdDoc != null) {
                    pdDoc.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return parsedText;
    }

    public Headers getHeaders(String userName, String password, Integer id) {
        String authorizationToken = serviceAuthTokenGenerator.generateClientToken(userName, password);
        String serviceToken = serviceAuthTokenGenerator.generateServiceToken();

        return Headers.headers(
            new Header("ServiceAuthorization", serviceToken),
            new Header("Content-Type", ContentType.JSON.toString()),
            new Header("Authorization", "Bearer " + authorizationToken),
            new Header("user-id", id.toString()));
    }

    public String getCaseworkerUserId() {
        return getUserId(caseworkerEmail, caseworkerPassword);
    }

    public String getSchedulerCaseworkerUserId() {
        return getUserId(schedulerEmail, schedulerPassword);
    }

    public String getUserId(String email, String password) {
        String caseworkerToken = serviceAuthTokenGenerator.generateClientToken(email, password);
        Headers headers = Headers.headers(
            new Header("Authorization", "Bearer " + caseworkerToken));

        String userInfoUrl = authProviderUrl + "/details";
        Response userResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(headers)
            .when().get(userInfoUrl).andReturn();

        JsonPath jsonPath = JsonPath.from(userResponse.getBody().asString());
        return jsonPath.get("id");
    }

    public Headers getHeadersWithCaseworkerUser() {
        String authorizationToken = serviceAuthTokenGenerator.generateClientToken(caseworkerEmail, caseworkerPassword);
        return Headers.headers(
            new Header("ServiceAuthorization", serviceToken),
            new Header("Content-Type", ContentType.JSON.toString()),
            new Header("Authorization", "Bearer " + authorizationToken));
    }

    public Headers getHeadersWithSchedulerCaseworkerUser() {
        String authorizationToken = serviceAuthTokenGenerator.generateClientToken(schedulerEmail, schedulerPassword);
        String id = getUserId(schedulerEmail, schedulerPassword);
        return Headers.headers(
            new Header("ServiceAuthorization", serviceToken),
            new Header("Content-Type", ContentType.JSON.toString()),
            new Header("Authorization", "Bearer " + authorizationToken),
            new Header("user-id", id));
    }

    public String createCaseAsCaseworker(String caseJson, String eventId) {
        String user = getCaseworkerUserId();
        String ccdStartAsCaseworkerUrl = coreCaseDataApiUrl + "/caseworkers/" + user + "/jurisdictions/PROBATE/case-types/GrantOfRepresentation/event-triggers/" + eventId + "/token";
        Response startResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(getHeadersWithCaseworkerUser())
            .when().get(ccdStartAsCaseworkerUrl).andReturn();
        String token = startResponse.getBody().jsonPath().get("token");
        String caseCreateJson = caseJson.replaceAll(TOKEN_PARM, token);
        String submitForCaseworkerUrl = coreCaseDataApiUrl + "/caseworkers/" + user + "/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases";
        Response submitResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(getHeadersWithCaseworkerUser())
            .body(caseCreateJson)
            .when().post(submitForCaseworkerUrl).andReturn();
        return submitResponse.getBody().asString();
    }

    public String findCaseAsCaseworker(String caseId) {
        String user = getCaseworkerUserId();
        String ccdFindCaseUrl = coreCaseDataApiUrl + "/caseworkers/" + user + "/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/" + caseId;
        Response startResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(getHeadersWithCaseworkerUser())
            .when().get(ccdFindCaseUrl).andReturn();
        return startResponse.getBody().asString();
    }

    public String updateCaseAsCaseworker(String caseJson, String eventId, String caseId) {
        String updateToken = startUpdateCaseAsCaseworker(caseId, eventId);
        String markAsReadyForExaminationUpdateJson = replaceAttribute(caseJson, TOKEN_PARM, updateToken);
        return continueUpdateCaseAsCaseworker(markAsReadyForExaminationUpdateJson, caseId);
    }

    public String startUpdateCaseAsCaseworker(String caseId, String eventId) {
        String user = getCaseworkerUserId();
        String ccdStartAsCaseworkerUrl = coreCaseDataApiUrl + "/caseworkers/" + user + "/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/" + caseId + "/event-triggers/" + eventId + "/token";
        Response startResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(getHeadersWithCaseworkerUser())
            .when().get(ccdStartAsCaseworkerUrl).andReturn();
        return startResponse.getBody().jsonPath().get("token");
    }

    public String continueUpdateCaseAsCaseworker(String caseJson, String caseId) {
        String user = getCaseworkerUserId();
        String submitForCaseworkerUrl = coreCaseDataApiUrl + "/caseworkers/" + user + "/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/" + caseId + "/events";
        Response submitResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(getHeadersWithCaseworkerUser())
            .body(caseJson)
            .when().post(submitForCaseworkerUrl).andReturn();
        return submitResponse.getBody().asString();
    }

    public String replaceAttribute(String json, String key, String value) {
        return json.replaceAll(key, value);
    }

    public String addAttribute(String json, String attributeKey, String attributeValue) {
        return json.replaceAll("\"applicationID\": \"603\",", "\"applicationID\": \"603\",\"" + attributeKey + "\": \"" + attributeValue + "\",");
    }


}
