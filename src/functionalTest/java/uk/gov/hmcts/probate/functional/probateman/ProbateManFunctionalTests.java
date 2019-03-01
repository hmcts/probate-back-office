package uk.gov.hmcts.probate.functional.probateman;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.Headers;
import net.serenitybdd.junit.runners.SerenityParameterizedRunner;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@RunWith(SerenityParameterizedRunner.class)
public class ProbateManFunctionalTests extends IntegrationTestBase {

    @Value("${user.auth.provider.oauth2.url}")
    private String idamUrl;

    private static final String FORENAME_REPLACE = "[FORENAME_REPLACE]";

    private static final String SURNAME_REPLACE = "[SURNAME_REPLACE]";

    private static final String ALIAS_REPLACE = "[ALIAS_REPLACE]";

    private final String caseType;

    private final String caseTypeFilename;

    private final String legacyType;

    private final String jsonFileName;

    private ObjectMapper objectMapper;

    @Value("${probate.caseworker.password}")
    private String password;

    @Value("${probate.caseworker.id}")
    private Integer id;

    @Value("${probate.caseworker.email}")
    private String email;

    private Headers headers;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    private List<Object> legacySearchResultRows;

    private Map<String, Object> requestMap;

    private String deceasedForename;

    private String deceasedSurname;

    private String deceasedAlias;

    public ProbateManFunctionalTests(String caseType, String caseTypeFilename, String legacyType, String jsonFileName) {
        this.caseType = caseType;
        this.caseTypeFilename = caseTypeFilename;
        this.legacyType = legacyType;
        this.jsonFileName = jsonFileName;
    }

//    @TestData
//    public static Collection<Object[]> testData() {
//        return Arrays.asList(new Object[][] {
//            {"CAVEAT", "caveat", "CAVEAT", "expectedCaveat"},
//            {"GRANT_APPLICATION", "grant_application", "LEGACY APPLICATION", "expectedGrantApplicant"},
//            {"WILL_LODGEMENT", "wills", "WILL", "expectedWillLodgement"},
//            {"STANDING_SEARCH", "standing_search", "STANDING SEARCH", "expectedStandingSearch"}
//        });
//    }
//
//    @Before
//    public void setUp() {
//        Awaitility.reset();
//        Awaitility.setDefaultPollDelay(100, MILLISECONDS);
//        Awaitility.setDefaultPollInterval(1, SECONDS);
//        Awaitility.setDefaultTimeout(10, SECONDS);
//        legacySearchResultRows = Collections.emptyList();
//
//        jdbcTemplate = new JdbcTemplate(dataSource);
//        objectMapper = new ObjectMapper();
//
//        headers = utils.getHeaders(email, password, id);
//
//        deceasedForename = RandomStringUtils.randomAlphanumeric(10) + "_FN";
//        deceasedSurname = RandomStringUtils.randomAlphanumeric(10) + "_SN";
//        deceasedAlias = RandomStringUtils.randomAlphanumeric(10) + "_ALIAS" + " " + RandomStringUtils.randomAlphanumeric(10);
//
//        System.out.println("DECEASED FORENAME: " + deceasedForename);
//        System.out.println("DECEASED SURNAME: " + deceasedSurname);
//
//        generateSqlAndExecute(deceasedForename, deceasedSurname, deceasedAlias, "/scripts/legacy_search_" + caseTypeFilename + "_insert.sql");
//    }
//
//    @After
//    public void cleanUp(){
//        generateSqlAndExecute(deceasedForename, deceasedSurname, deceasedAlias, "/scripts/legacy_search_" + caseTypeFilename + "_clean_up.sql");
//    }
//
//    @Test
//    public void shouldViewProbateManCase() {
//        Map<String, Object> dbResultsMap = retrieveRecordFromDb(deceasedForename, deceasedSurname, deceasedAlias, "/scripts/legacy_search_" + caseTypeFilename + "_query.sql");
//        Long id = (Long) dbResultsMap.get("id");
//
//        String expectedJSON = addVariablesToScript(deceasedForename, deceasedSurname, deceasedAlias, "/json/probateman/" + jsonFileName + ".json");
//
//        String actualJson = SerenityRest.given()
//            .relaxedHTTPSValidation()
//            .headers(headers)
//            .when()
//            .get("/probateManTypes/" + caseType + "/cases/" + id.toString())
//            .then()
//            .assertThat()
//            .statusCode(200).extract().body().asString();
//        JSONAssert.assertEquals(expectedJSON, actualJson, JSONCompareMode.LENIENT);
//    }
//
//    @Test
//    public void shouldDoLegacySearch() throws Exception {
//        final String legacySearchQuery = getRequestJson(deceasedForename, deceasedSurname);
//
//        await().atMost(10, SECONDS).until(() -> !getLegacySearchRows(legacySearchQuery).isEmpty());
//        assertThat(legacySearchResultRows, hasSize(1));
//        Map<String, Object> legacySearchResultRow = (Map<String, Object>) ((Map<String, Object>) legacySearchResultRows.get(0)).get("value");
//
//        String id = (String) legacySearchResultRow.get("id");
//        assertThat(legacySearchResultRow.get("id"), notNullValue());
//        assertThat(legacySearchResultRow.get("aliases"), equalTo(deceasedAlias));
//        assertThat(legacySearchResultRow.get("fullName"), equalTo(deceasedForename + " " + deceasedSurname));
////        assertThat(legacySearchResultRow.get("type"), equalTo("Legacy " + legacyType));
//        assertThat((String) legacySearchResultRow.get("legacyCaseViewUrl"), containsString("/print/probateManTypes/" + caseType + "/cases/" + id));
//        assertThat(legacySearchResultRow.get("dob"), equalTo("1900-01-01"));
//        assertThat(legacySearchResultRow.get("dod"), equalTo("2018-01-01"));
//
//        legacySearchResultRow.put("doImport", "YES");
//
//        String importJson = objectMapper.writeValueAsString(ImmutableMap.of("case_details",
//            ImmutableMap.of("case_data", requestMap.get("data"))));
//
//        JsonPath jsonPath = SerenityRest.given()
//            .relaxedHTTPSValidation()
//            .headers(headers)
//            .when().body(importJson)
//            .post("/legacy/doImport")
//            .then()
//            .assertThat()
//            .statusCode(200)
//            .extract().response()
//            .jsonPath();
//        jsonPath.prettyPrint();
//
//        checkDbRecordIsUpdated(deceasedForename, deceasedSurname, deceasedAlias, "/scripts/legacy_search_" + caseTypeFilename + "_query.sql");
//    }
//
//    private void checkDbRecordIsUpdated(String forename, String surname, String alias, String sqlFile) {
//        Map<String, Object> dbResultsMap = retrieveRecordFromDb(forename, surname, alias, sqlFile);
//        String ccdCaseNo = (String) dbResultsMap.get("ccd_case_no");
//        assertThat(ccdCaseNo, not(isEmptyOrNullString()));
//        assertThat(dbResultsMap.get("dnm_ind"), equalTo("Y"));
//    }
//
//    private Map<String, Object> retrieveRecordFromDb(String forename, String surname, String alias, String sqlFile) {
//        String sql = addVariablesToScript(forename, surname, alias, sqlFile);
//        return jdbcTemplate.queryForMap(sql);
//    }
//
//    private String getRequestJson(String forename, String surname) {
//        String legacySearchJson = utils.getJsonFromFile("/probateman/legacySearch.json");
//        legacySearchJson = legacySearchJson.replace(FORENAME_REPLACE, forename);
//        return legacySearchJson.replace(SURNAME_REPLACE, surname);
//    }
//
//    private void generateSqlAndExecute(String forename, String surname, String alias, String sqlFile) {
//        String sql = addVariablesToScript(forename, surname, alias, sqlFile);
//        jdbcTemplate.execute(sql);
//    }
//
//    private String addVariablesToScript(String forename, String surname, String alias, String sqlFile) {
//        String sql = utils.getStringFromFile(sqlFile);
//        sql = sql.replace(FORENAME_REPLACE, forename);
//        sql = sql.replace(SURNAME_REPLACE, surname);
//        sql = sql.replace(ALIAS_REPLACE, alias);
//        return sql;
//    }
//
//    private List<Object> getLegacySearchRows(String legacySearchQuery) {
//        JsonPath jsonPath = SerenityRest.given()
//            .relaxedHTTPSValidation()
//            .headers(headers)
//            .when().body(legacySearchQuery)
//            .post("/legacy/search")
//            .then()
//            .assertThat()
//            .statusCode(200)
//            .extract().response()
//            .jsonPath();
//        jsonPath.prettyPrint();
//        requestMap = jsonPath.getMap("");
//        legacySearchResultRows = jsonPath.getList("data.legacySearchResultRows");
//        return legacySearchResultRows;
//    }
}
