package uk.gov.hmcts.probate.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.functional.model.ClientAuthorizationResponse;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;

import java.io.IOException;

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
        String clientToken = generateOpenIdToken();

        String withoutSignature = clientToken.substring(0, clientToken.lastIndexOf('.') + 1);
        Claims claims = Jwts.parser().build().parseSignedClaims(withoutSignature).getPayload();

        return claims.get("id", String.class);
    }

    public void createNewUser() {
        given().headers("Content-type", "application/json")
            .relaxedHTTPSValidation()
            .body(
                "{ \"email\":\"test@TEST.COM\", \"forename\":\"test@TEST.COM\",\"surname\":\"test@TEST.COM\","
                    + "\"password\":\"123\",\"continue-url\":\"test\"}")
            .post(idamUrl + "/testing-support/accounts");
    }

    public String generateOpenIdToken() {
        String token = "";

        String jsonResponse = post(idamUrl + "/o/token?"
                + "client_secret=" + clientSecret
                + "&client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&grant_type=password&scope=openid profile roles")
                .body().asString();

        ObjectMapper mapper = new ObjectMapper();

        try {
            token = mapper.readValue(jsonResponse, ClientAuthorizationResponse.class).accessToken;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return token;
    }

    public String generateOpenIdToken(String userName, String password) {
        JsonPath jp = RestAssured.given().relaxedHTTPSValidation().post(idamUrl + "/o/token?"
            + "client_secret=" + probateClientSecret
            + "&client_id=" + probateClientId
            + "&redirect_uri=" + redirectUri
            + "&username=" + userName
            + "&password=" + password
            + "&grant_type=password&scope=openid profile roles")
            .body().jsonPath();
        String token = jp.get("access_token");

        return token;
    }

}