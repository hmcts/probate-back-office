package uk.gov.hmcts.probate.caseCreator.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.parsing.Parser;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit.spring.integration.SpringIntegrationMethodRule;
import net.serenitybdd.rest.SerenityRest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestCaseCreatorConfig.class)
public class TestCaseCreator {

    {
        System.setProperty("socksProxyHost", "localhost");
        System.setProperty("socksProxyPort", "9090");
    }

    private static final String EVENT_NAME_GOR_PA = "applyForGrant";

    private static final String EVENT_NAME_CAVEAT_PA = "applyForCaveat";

    private static final String GOR = "GrantOfRepresentation";

    private static final String CAVEAT = "Caveat";

    private String clientToken;

    private String userId;

    @Value("${user.auth.provider.oauth2.url}")
    private String idamUserBaseUrl;

    @Value("${ccd.data.store.api.url}")
    private String solCcdServiceUrl;

    @Value("${idam.secret}")
    private String idamSecret;

    private String idamUsername;

    @Value("${idam.pa.username}")
    private String idamPaUsername;

    @Value("${idam.sol.username}")
    private String idamSolUsername;

    @Value("${idam.bo.username}")
    private String idamBoUsername;

    @Value("${idam.userpassword}")
    private String idamPassword;

    @Value("${idam.oauth2.redirect_uri}")
    private String redirectUri;

    @Autowired
    private RelaxedServiceAuthTokenGenerator relaxedServiceAuthTokenGenerator;

    @Rule
    public SpringIntegrationMethodRule springIntegration;

    private ObjectMapper objectMapper = new ObjectMapper();

    public TestCaseCreator() {
        this.springIntegration = new SpringIntegrationMethodRule();

    }

    @Before
    public void setUp() {
        RestAssured.baseURI = solCcdServiceUrl;
        RestAssured.defaultParser = Parser.JSON;
    }

    @Test
    public void createPaCase() throws Exception {
        idamUsername = idamPaUsername;
        createCase("create.pa.ccd.json", "citizens", EVENT_NAME_GOR_PA, GOR);
    }

    @Test
    public void createPaCaseCaveats() throws Exception {
        idamUsername = idamPaUsername;
        createCase("create.caveat.pa.ccd.json", "citizens", EVENT_NAME_CAVEAT_PA, CAVEAT);
    }

    @Test
    public void createSolsCase() throws Exception {
        idamUsername = idamSolUsername;
        createCase("create.sols.ccd.json", "caseworkers", "solicitorCreateApplication", GOR);
    }

    @Ignore
    @Test
    public void createBOSolsCase() throws Exception {
        idamUsername = idamBoUsername;
        createCase("create.bo.sols.ccd.json", "caseworkers", "boPrintCase", GOR);
    }

    private void createCase(String fileName, String role, String eventName, String caseType) throws Exception {
        Headers headersWithUserId = getHeadersWithUserId();
        userId = getUserId(clientToken);
        String token = generateEventToken(role, eventName, headersWithUserId, caseType);
        String rep = getJsonFromFile(fileName).replace("\"event_token\": \"sampletoken\"", "\"event_token\":\"" + token + "\"");


        SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(headersWithUserId)
                .baseUri(solCcdServiceUrl)
                .body(rep)
                .when().post("/" + role + "/" + userId + "/jurisdictions/PROBATE/case-types/"+ caseType +"/cases").
                        then()
                .statusCode(201);
    }


    public Headers getHeadersWithUserId() throws Exception {
        return getHeadersWithUserId(generateServiceToken());
    }


    public String generateServiceToken() {
        String serviceToken = relaxedServiceAuthTokenGenerator.generate();
        log.info("Service Token: {}", serviceToken);
        return serviceToken;
    }

    public Headers getHeadersWithUserId(String serviceToken) throws Exception {
        return Headers.headers(
                new Header("ServiceAuthorization", serviceToken),
                new Header("Content-Type", ContentType.JSON.toString()),
                new Header("Authorization", generateUserTokenWithNoRoles()));
    }

    private String generateEventToken(String role, String eventName, Headers headersWithUserId, String caseType) {
        log.info("User Id: {}", userId);
        RestAssured.baseURI = solCcdServiceUrl;
        return SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(headersWithUserId)
                .when().get("/" + role + "/" + userId + "/jurisdictions/PROBATE/case-types/"+ caseType +"/event-triggers/" + eventName + "/token")
                .then().assertThat().statusCode(200).extract().path("token");
    }


    public String generateUserTokenWithNoRoles() throws Exception {
        clientToken = generateClientToken();
        log.info("Client Token : {}", clientToken);
        return clientToken;
    }

    private String generateClientToken() throws Exception {
        String code = generateClientCode();
        log.info("Client Code: {}", code);
        return "Bearer " + RestAssured.given().relaxedHTTPSValidation().post(idamUserBaseUrl + "/oauth2/token?code=" + code +
                "&client_secret=" + idamSecret +
                "&client_id=probate" +
                "&redirect_uri=" + redirectUri +
                "&grant_type=authorization_code")
                .body().path("access_token");
    }

    private String generateClientCode() throws Exception {
        final String encoded = Base64.getEncoder().encodeToString((idamUsername + ":" + idamPassword).getBytes());
        JsonNode jsonNode = objectMapper.readValue(RestAssured.given().relaxedHTTPSValidation().baseUri(idamUserBaseUrl)
                .header("Authorization", "Basic " + encoded)
                .post("/oauth2/authorize?response_type=code&client_id=probate&redirect_uri=" + redirectUri)
                .body().print(), JsonNode.class);
        return jsonNode.get("code").asText();
    }

    private String getJsonFromFile(String fileName) {
        try {
            File file = ResourceUtils.getFile(this.getClass().getResource("/json/" + fileName));
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUserId(String userToken) {
        return "" + RestAssured.given()
                .header("Authorization", userToken)
                .get(idamUserBaseUrl + "/details")
                .body()
                .path("id");
    }
}
