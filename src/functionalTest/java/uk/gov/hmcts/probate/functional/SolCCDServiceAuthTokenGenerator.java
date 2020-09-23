package uk.gov.hmcts.probate.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.functional.model.ClientAuthorizationCodeResponse;
import uk.gov.hmcts.probate.functional.model.ClientAuthorizationResponse;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;

import java.io.IOException;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.post;

@Slf4j
@Component
public class SolCCDServiceAuthTokenGenerator {

    @Value("${idam.oauth2.client.id}")
    private String clientId;

    @Value("${idam.oauth2.client.secret}")
    private String clientSecret;

    @Value("${idam.oauth2.client.probate.id}")
    private String probateClientId;

    @Value("${idam.oauth2.client.probate.secret}")
    private String probateClientSecret;

    @Value("${idam.oauth2.redirect_uri}")
    private String redirectUri;

    @Value("${service.name}")
    private String serviceName;

    @Value("${service.auth.provider.base.url}")
    private String baseServiceAuthUrl;

    @Value("${user.auth.provider.oauth2.url}")
    private String idamUrl;

    @Autowired
    private ServiceAuthTokenGenerator tokenGenerator;

    public String generateServiceToken() {
        return tokenGenerator.generate();
    }

    public String getUserId() {
        String clientToken = generateClientToken();

        String withoutSignature = clientToken.substring(0, clientToken.lastIndexOf('.') + 1);
        Claims claims = Jwts.parser().parseClaimsJwt(withoutSignature).getBody();

        return claims.get("id", String.class);
    }

    public String generateClientToken() {
        String code = generateClientCode();
        String token = "";

        String jsonResponse = post(idamUrl + "/oauth2/token?code=" + code +
            "&client_secret=" + clientSecret +
            "&client_id=" + clientId +
            "&redirect_uri=" + redirectUri +
            "&grant_type=authorization_code")
            .body().asString();

        ObjectMapper mapper = new ObjectMapper();

        try {
            token = mapper.readValue(jsonResponse, ClientAuthorizationResponse.class).accessToken;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return token;
    }

    private String generateClientCode() {
        String code = "";
        String jsonResponse = given()
            .relaxedHTTPSValidation()
            .header("Authorization", "Basic dGVzdEBURVNULkNPTToxMjM=")
            .post(idamUrl + "/oauth2/authorize?response_type=code" +
                "&client_id=" + clientId +
                "&redirect_uri=" + redirectUri)
            .asString();

        ObjectMapper mapper = new ObjectMapper();

        try {
            code = mapper.readValue(jsonResponse, ClientAuthorizationCodeResponse.class).code;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return code;
    }

    public void createNewUser() {
        given().headers("Content-type", "application/json")
            .relaxedHTTPSValidation()
            .body("{ \"email\":\"test@TEST.COM\", \"forename\":\"test@TEST.COM\",\"surname\":\"test@TEST.COM\",\"password\":\"123\",\"continue-url\":\"test\"}")
            .post(idamUrl + "/testing-support/accounts");
    }

    public String generateClientToken(String userName, String password) {
        String code = generateClientCode(userName, password);
        JsonPath jp = RestAssured.given().relaxedHTTPSValidation().post(idamUrl + "/oauth2/token?" +
            "code=" + code +
            "&client_secret=" + probateClientSecret +
            "&client_id=" + probateClientId +
            "&redirect_uri=" + redirectUri +
            "&grant_type=authorization_code")
            .body().jsonPath();
        String token = jp.get("access_token");

        return token;
    }

    public String generateAuthorisation(String userName, String password) {
        return generateClientToken(userName, password);
    }


    private String generateClientCode(String userName, String password) {
        final String encoded = Base64.getEncoder().encodeToString((userName + ":" + password).getBytes());
        ResponseBody authorization = given().relaxedHTTPSValidation().baseUri(idamUrl)
            .header("Authorization", "Basic " + encoded)
            .post("/oauth2/authorize?response_type=code&client_id=" + probateClientId + "&redirect_uri=" + redirectUri)
            .body();
        
        log.info("authorization:"+authorization.prettyPrint());
        return authorization.jsonPath().get("code");
    }

}