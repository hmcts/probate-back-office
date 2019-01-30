//package uk.gov.hmcts.probate.functional.probateman;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.restassured.http.ContentType;
//import io.restassured.http.Header;
//import io.restassured.http.Headers;
//import net.serenitybdd.junit.runners.SerenityRunner;
//import net.serenitybdd.rest.SerenityRest;
//import org.apache.commons.lang3.RandomStringUtils;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.skyscreamer.jsonassert.JSONAssert;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.test.context.jdbc.Sql;
//import uk.gov.hmcts.probate.functional.IntegrationTestBase;
//
//import java.util.Arrays;
//
//@RunWith(SerenityRunner.class)
//public class ProbateManFunctionalTests extends IntegrationTestBase {
//
//    @Value("${user.auth.provider.oauth2.url}")
//    private String idamUrl;
//
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    private static final String PROBATEMAN_DB_PASS = "Probate123";
//
//    private ObjectMapper objectMapper;
//
//    private String email;
//
//    private Headers headers;
//
//    @Before
//    public void setUp() throws JsonProcessingException {
//        objectMapper = new ObjectMapper();
//        String forename = RandomStringUtils.randomAlphanumeric(5);
//        String surname = RandomStringUtils.randomAlphanumeric(5);
//        email = forename + "." + surname + "@email.com";
//        logger.info("Generate user name: {}", email);
//
//        IdamData idamData = IdamData.builder()
//            .email(email)
//            .forename(forename)
//            .surname(surname)
//            .password(PROBATEMAN_DB_PASS)
//            .roles(Arrays.asList(Role.builder().code("caseworker-probate").build()))
//            .build();
//
//        SerenityRest.given()
//            .relaxedHTTPSValidation()
//            .headers(Headers.headers(new Header("Content-Type", ContentType.JSON.toString())))
//            .baseUri(idamUrl)
//            .body(objectMapper.writeValueAsString(idamData))
//            .when()
//            .post("/testing-support/accounts")
//            .then()
//            .statusCode(204);
//
//        headers = utils.getHeaders(email, PROBATEMAN_DB_PASS);
//    }
//
//    @Test
//    @Sql(scripts = "/scripts/grant_application_insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    @Sql(scripts = "/scripts/grant_application_clean_up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//    public void shouldCreateCcdCaseForGrantApplication() {
//        String actualJson = SerenityRest.given()
//            .relaxedHTTPSValidation()
//            .headers(headers)
//            .when()
//            .get("/probateManTypes/GRANT_APPLICATION/cases/999")
//            .then()
//            .assertThat()
//            .statusCode(200)
//            .extract().body().asString();
//        JSONAssert.assertEquals(utils.getJsonFromFile("/probateman/grantApplicant.json"), actualJson, true);
//    }
//
//    @Test
//    @Sql(scripts = "/scripts/caveat_insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    @Sql(scripts = "/scripts/caveat_clean_up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//    public void shouldCreateCcdCaseForCaveat() {
//        String actualJson = SerenityRest.given()
//            .relaxedHTTPSValidation()
//            .headers(headers)
//            .when()
//            .get("/probateManTypes/CAVEAT/cases/999")
//            .then()
//            .assertThat()
//            .statusCode(200)
//            .extract().body().asString();
//        JSONAssert.assertEquals(utils.getJsonFromFile("/probateman/caveat.json"), actualJson, true);
//    }
//
//    @Test
//    @Sql(scripts = "/scripts/standing_search_insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    @Sql(scripts = "/scripts/standing_search_clean_up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//    public void shouldCreateCcdCaseForStandingSearch() {
//        String actualJson = SerenityRest.given()
//            .relaxedHTTPSValidation()
//            .headers(headers)
//            .when()
//            .get("/probateManTypes/STANDING_SEARCH/cases/999")
//            .then()
//            .assertThat()
//            .statusCode(200).extract().jsonPath().prettyPrint();
//        JSONAssert.assertEquals(utils.getJsonFromFile("/probateman/standingSearch.json"), actualJson, true);
//    }
//
//    @Test
//    @Sql(scripts = "/scripts/wills_insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    @Sql(scripts = "/scripts/wills_clean_up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//    public void shouldCreateCcdCaseForWillLodgement() {
//        String actualJson = SerenityRest.given()
//            .relaxedHTTPSValidation()
//            .headers(headers)
//            .when()
//            .get("/probateManTypes/WILL_LODGEMENT/cases/999")
//            .then()
//            .assertThat()
//            .statusCode(200).extract().body().asString();
//        JSONAssert.assertEquals(utils.getJsonFromFile("/probateman/willLodgement.json"), actualJson, true);
//    }
//}
