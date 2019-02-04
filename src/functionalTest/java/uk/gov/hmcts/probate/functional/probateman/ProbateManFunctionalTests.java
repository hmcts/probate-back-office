package uk.gov.hmcts.probate.functional.probateman;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import org.apache.commons.lang3.RandomStringUtils;
import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SerenityRunner.class)
public class ProbateManFunctionalTests extends IntegrationTestBase {

    @Value("${user.auth.provider.oauth2.url}")
    private String idamUrl;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String PROBATEMAN_DB_PASS = "Probate123";

    private static final String FORENAME_REPLACE = "[FORENAME_REPLACE]";

    private static final String SURNAME_REPLACE = "[SURNAME_REPLACE]";

    private static final String ALIAS_REPLACE = "[ALIAS_REPLACE]";

    private ObjectMapper objectMapper;

    private String email;

    private Headers headers;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    private List<Map> legacySearchResultRows;

    private Map<String, Object> requestMap;

    @Before
    public void setUp() throws JsonProcessingException {
        Awaitility.reset();
        Awaitility.setDefaultPollDelay(100, MILLISECONDS);
        Awaitility.setDefaultPollInterval(1, SECONDS);
        Awaitility.setDefaultTimeout(10, SECONDS);
        legacySearchResultRows = Collections.emptyList();

        jdbcTemplate = new JdbcTemplate(dataSource);
        objectMapper = new ObjectMapper();
        String forename = RandomStringUtils.randomAlphanumeric(5);
        String surname = RandomStringUtils.randomAlphanumeric(5);
        email = forename + "." + surname + "@email.com";
        logger.info("Generate user name: {}", email);

        IdamData idamData = IdamData.builder()
            .email(email)
            .forename(forename)
            .surname(surname)
            .password(PROBATEMAN_DB_PASS)
            .levelOfAccess(1)
            .roles(Arrays.asList(
                Role.builder().code("caseworker-probate").build(),
                Role.builder().code("caseworker-probate-issuer").build()
            ))
            .userGroup(UserGroup.builder().code("caseworker").build())
            .build();

        SerenityRest.given()
            .relaxedHTTPSValidation()
            .headers(Headers.headers(new Header("Content-Type", ContentType.JSON.toString())))
            .baseUri(idamUrl)
            .body(objectMapper.writeValueAsString(idamData))
            .when()
            .post("/testing-support/accounts")
            .then()
            .statusCode(204);

        headers = utils.getHeaders(email, PROBATEMAN_DB_PASS);
    }

    @Test
    @Sql(scripts = "/scripts/grant_application_insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/scripts/grant_application_clean_up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldGetGrantApplicationFromProbateMan() {
        String actualJson = SerenityRest.given()
            .relaxedHTTPSValidation()
            .headers(headers)
            .when()
            .get("/probateManTypes/GRANT_APPLICATION/cases/999")
            .then()
            .assertThat()
            .statusCode(200)
            .extract().body().asString();
        JSONAssert.assertEquals(utils.getJsonFromFile("/probateman/grantApplicant.json"), actualJson, true);
    }

    @Test
    @Sql(scripts = "/scripts/caveat_insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/scripts/caveat_clean_up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldGetCaveatFromProbateMan() {
        String actualJson = SerenityRest.given()
            .relaxedHTTPSValidation()
            .headers(headers)
            .when()
            .get("/probateManTypes/CAVEAT/cases/999")
            .then()
            .assertThat()
            .statusCode(200)
            .extract().body().asString();
        JSONAssert.assertEquals(utils.getJsonFromFile("/probateman/caveat.json"), actualJson, true);
    }

    @Test
    @Sql(scripts = "/scripts/standing_search_insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/scripts/standing_search_clean_up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldGetStandingSearchFromProbateMan() {
        String actualJson = SerenityRest.given()
            .relaxedHTTPSValidation()
            .headers(headers)
            .when()
            .get("/probateManTypes/STANDING_SEARCH/cases/999")
            .then()
            .assertThat()
            .statusCode(200).extract().jsonPath().prettyPrint();
        JSONAssert.assertEquals(utils.getJsonFromFile("/probateman/standingSearch.json"), actualJson, true);
    }

    @Test
    @Sql(scripts = "/scripts/wills_insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/scripts/wills_clean_up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldGetWillLodgementFromProbateman() {
        String actualJson = SerenityRest.given()
            .relaxedHTTPSValidation()
            .headers(headers)
            .when()
            .get("/probateManTypes/WILL_LODGEMENT/cases/999")
            .then()
            .assertThat()
            .statusCode(200).extract().body().asString();
        JSONAssert.assertEquals(utils.getJsonFromFile("/probateman/willLodgement.json"), actualJson, true);
    }

    @Test
    public void shouldDoLegacySearch() throws Exception {
        String forename = RandomStringUtils.randomAlphanumeric(10) + "_FN";
        String surname = RandomStringUtils.randomAlphanumeric(10) + "_SN";
        String alias = RandomStringUtils.randomAlphanumeric(10) + "_ALIAS";

        generateSqlAndExecute(forename, surname, alias, "/scripts/legacy_search_caveat_insert.sql");

        final String legacySearchQuery = getRequestJson(forename, surname);
        
        await().until(() -> getLegacySearchRows(legacySearchQuery));
        assertThat(legacySearchResultRows, hasSize(1));
        Map<String, Object> legacySearchResultRow = ((Map<String, Object>) legacySearchResultRows.get(0).get("value"));

        String id = (String) legacySearchResultRow.get("id");
        assertThat(legacySearchResultRow.get("id"), notNullValue());
        assertThat(legacySearchResultRow.get("aliases"), equalTo(alias));
        assertThat(legacySearchResultRow.get("fullName"), equalTo(forename + " " + surname));
        assertThat(legacySearchResultRow.get("type"), equalTo("Legacy CAVEAT"));
        assertThat((String) legacySearchResultRow.get("legacyCaseViewUrl"), containsString("/print/probateManTypes/CAVEAT/cases/" + id));
        assertThat(legacySearchResultRow.get("dob"), equalTo("1900-01-01"));
        assertThat(legacySearchResultRow.get("dod"), equalTo("2018-01-01"));

        legacySearchResultRow.put("doImport", "YES");

        String importJson = objectMapper.writeValueAsString(ImmutableMap.of("case_details",
            ImmutableMap.of("case_data", requestMap.get("data"))));

        JsonPath jsonPath = SerenityRest.given()
            .relaxedHTTPSValidation()
            .headers(headers)
            .when().body(importJson)
            .post("/legacy/doImport")
            .then()
            .assertThat()
            .statusCode(200)
            .extract().response()
            .jsonPath();
        jsonPath.prettyPrint();

        Map<String, Object> dbResultsMap = jdbcTemplate.queryForMap("SELECT * FROM CAVEATS_FLAT WHERE ID=" + id);
        String ccdCaseNo = (String) dbResultsMap.get("ccd_case_no");
        assertThat(ccdCaseNo, not(isEmptyOrNullString()));
        assertThat(dbResultsMap.get("dnm_ind"), equalTo("Y"));

        generateSqlAndExecute(forename, surname, alias, "/scripts/legacy_search_caveat_clean_up.sql");
    }

    private String getRequestJson(String forename, String surname) {
        String legacySearchJson = utils.getJsonFromFile("/probateman/legacySearch.json");
        legacySearchJson = legacySearchJson.replace(FORENAME_REPLACE, forename);
        return legacySearchJson.replace(SURNAME_REPLACE, surname);
    }

    private void generateSqlAndExecute(String forename, String surname, String alias, String sqlFile) {
        String sql = utils.getStringFromFile(sqlFile);
        sql = sql.replace(FORENAME_REPLACE, forename);
        sql = sql.replace(SURNAME_REPLACE, surname);
        sql = sql.replace(ALIAS_REPLACE, alias);
        jdbcTemplate.execute(sql);
    }

    private boolean getLegacySearchRows(String legacySearchQuery) {
        JsonPath jsonPath = SerenityRest.given()
            .relaxedHTTPSValidation()
            .headers(headers)
            .when().body(legacySearchQuery)
            .post("/legacy/search")
            .then()
            .assertThat()
            .statusCode(200)
            .extract().response()
            .jsonPath();
        jsonPath.prettyPrint();
        requestMap = jsonPath.getMap("");
        legacySearchResultRows = jsonPath.getList("data.legacySearchResultRows");
        return legacySearchResultRows.size() > 0;
    }
}
