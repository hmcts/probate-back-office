package uk.gov.hmcts.probate.casecreator.util;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import org.apache.commons.codec.binary.Base32;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;

import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RelaxedServiceAuthTokenGenerator {

    private final String secret;
    private final String microService;
    private final TimeBasedOneTimePasswordGenerator totpGenerator;
    private final String idamS2sAuthUrl;

    public RelaxedServiceAuthTokenGenerator(
        final String secret,
        final String microService,
        final String idamS2sAuthUrl
    ) {
        this.secret = secret;
        this.microService = microService;
        this.totpGenerator = new TimeBasedOneTimePasswordGenerator();
        this.idamS2sAuthUrl = idamS2sAuthUrl;
    }

    public String generate() {
        final String oneTimePassword = generateTotp();

        Map<String, String> signInDetails = new HashMap<>();
        signInDetails.put("microservice", this.microService);
        signInDetails.put("oneTimePassword", oneTimePassword);

        return RestAssured.given()
            .baseUri(idamS2sAuthUrl)
            .relaxedHTTPSValidation()
            .headers(Headers.headers(new Header("Content-Type", ContentType.JSON.toString())))
            .body(signInDetails)
            .when().post("/lease")
            .then().extract().body().asString();
    }

    private String generateTotp() {
        byte[] key = new Base32().decode(secret.toUpperCase(Locale.ROOT));

        try {
            return totpGenerator.generateOneTimePasswordString(
                    new SecretKeySpec(key, "HmacSHA1"),
                    Instant.now(),
                    Locale.ROOT
            );
        } catch (InvalidKeyException e) {
            throw new IllegalStateException("Failed to generate one time password", e);
        }
    }
}
