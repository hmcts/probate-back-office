package uk.gov.hmcts.probate.functional.util;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import net.serenitybdd.rest.SerenityRest;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

public class RelaxedServiceAuthTokenGenerator {

    private final String secret;
    private final String microService;
    private final GoogleAuthenticator googleAuthenticator;
    private String idamS2sAuthUrl;

    public RelaxedServiceAuthTokenGenerator(
            final String secret,
            final String microService,
            final String idamS2sAuthUrl
    ) {
        this.secret = secret;
        this.microService = microService;
        this.googleAuthenticator = new GoogleAuthenticator();
        this.idamS2sAuthUrl = idamS2sAuthUrl;
    }

    public String generate() {
        final String oneTimePassword = format("%06d", googleAuthenticator.getTotpPassword(secret));

        Map<String, String> signInDetails = new HashMap<>();
        signInDetails.put("microservice", this.microService);
        signInDetails.put("oneTimePassword", oneTimePassword);

        return SerenityRest.given()
                .baseUri(idamS2sAuthUrl)
                .relaxedHTTPSValidation()
                .headers(Headers.headers(new Header("Content-Type", ContentType.JSON.toString())))
                .body(signInDetails)
                .when().post("/lease")
                .then().extract().body().asString();
    }
}
