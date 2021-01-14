package uk.gov.hmcts.probate.functional.casematch;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static uk.gov.hmcts.probate.functional.util.FunctionalTestUtils.TOKEN_PARM;

@Slf4j
@RunWith(SpringIntegrationSerenityRunner.class)
public class CaseMatchingTests extends IntegrationTestBase {

    private static final String GRANT_OF_PROBATE_JSON = "casematch/applyForGrantPayoad.json";
    private static final String GRANT_OF_PROBATE_MATCH_CASE_JSON = "casematch/grantOfProbateMatchCase.json";
    private static final String STANDING_SEARCH_MATCH_CASE_JSON = "casematch/standingSearchMatchCase.json";
    private static final String WILL_LODGEMENT_MATCH_CASE_JSON = "casematch/willLodgementMatchCase.json";
    private static final String CAVEAT_MATCH_CASE_JSON = "casematch/caveatFlowSearchMatchCase.json";

    private static final String EVENT_PARM = "EVENT_PARM";
    private static final String CASE_CREATE_EVENT = "applyForGrant";
    private static final String CASE_UPDATE_EVENT = "createCase";

    private static final String SEARCH_GRANT_FLOW = "/case-matching/search-from-grant-flow";
    private static final String SEARCH_FROM_CAVEAT_FLOW = "/case-matching/search-from-caveat-flow";
    private static final String SEARCH_FROM_STANDING_SEARCH_FLOW = "/case-matching/search-from-standing-search-flow";
    private static final String SEARCH_FROM_WILL_LODGEMENT_FLOW = "/case-matching/search-from-will-lodgement-flow";

    public static final String NAME = "Ned Stark";
    public static final String DATE_OF_BIRTH = "1900-01-01";
    public static final String DATE_OF_DEATH = "2020-01-01";


    @BeforeAll
    void setup() {
        //Create Case
        String baseCaseJson = utils.getJsonFromFile(GRANT_OF_PROBATE_JSON);
        String applyForGrantyCaseJson = utils.replaceAttribute(baseCaseJson, EVENT_PARM, CASE_CREATE_EVENT);
        String applyForGrantCase = utils.createCaseAsCaseworker(applyForGrantyCaseJson, CASE_CREATE_EVENT);
        JsonPath jsonPathApply = JsonPath.from(applyForGrantCase);
        String caseId = jsonPathApply.get("id").toString();
        //Update Case
        //Move PAAppCreated to createCase state
        String updateToken = utils.startUpdateCaseAsCaseworker(caseId, CASE_UPDATE_EVENT);
        String updateBaseCase = utils.replaceAttribute(baseCaseJson, TOKEN_PARM, updateToken);
        updateBaseCase = utils.replaceAttribute(updateBaseCase, EVENT_PARM, CASE_UPDATE_EVENT);
        String updateResponse = utils.continueUpdateCaseAsCaseworker(updateBaseCase, caseId);
    }

    @Test
    public void shouldReturnMatchingCaseWhenGOPSearchFlow() {
        Response response = search(GRANT_OF_PROBATE_MATCH_CASE_JSON, SEARCH_GRANT_FLOW);
        response.prettyPrint();
        response.then().assertThat().statusCode(200);
        JsonPath jsonPath = JsonPath.from(response.getBody().prettyPrint());
        assertThat(jsonPath.get("data.caseMatches[0]"), notNullValue());
        assertThat(jsonPath.get("data.caseMatches[0].value.fullName"), is(equalTo(NAME)));
        assertThat(jsonPath.get("data.caseMatches[0].value.dob"), is(equalTo(DATE_OF_BIRTH)));
        assertThat(jsonPath.get("data.caseMatches[0].value.dod"), is(equalTo(DATE_OF_DEATH)));
    }

    @Test
    public void shouldReturnNoMatchingCaseWhenGOPSearchFlow() {
        Response response = search(SEARCH_GRANT_FLOW);
        JsonPath jsonPath = JsonPath.from(response.getBody().prettyPrint());
        assertThat(jsonPath.get("data.caseMatches"), is(empty()));
    }

    @Test
    public void shouldReturnMatchingCaseWhenCaveatSearchFlow() {
        Response response = search(CAVEAT_MATCH_CASE_JSON, SEARCH_FROM_CAVEAT_FLOW);
        response.prettyPrint();
        response.then().assertThat().statusCode(200);
        JsonPath jsonPath = JsonPath.from(response.getBody().prettyPrint());
        assertThat(jsonPath.get("data.caseMatches[0]"), notNullValue());
        assertThat(jsonPath.get("data.caseMatches[0]"), notNullValue());
        assertThat(jsonPath.get("data.caseMatches[0].value.fullName"), is(equalTo(NAME)));
        assertThat(jsonPath.get("data.caseMatches[0].value.dob"), is(equalTo(DATE_OF_BIRTH)));
        assertThat(jsonPath.get("data.caseMatches[0].value.dod"), is(equalTo(DATE_OF_DEATH)));
    }

    @Test
    public void shouldReturnNoMatchingCaseWhenCaveatSearchFlow() {

        Response response = search(SEARCH_FROM_CAVEAT_FLOW);
        JsonPath jsonPath = JsonPath.from(response.getBody().prettyPrint());
        assertThat(jsonPath.get("data.caseMatches"), is(empty()));
    }

    @Test
    public void shouldReturnMatchingCaseWhenStandingSearchFlow() {
        Response response = search(STANDING_SEARCH_MATCH_CASE_JSON, SEARCH_FROM_STANDING_SEARCH_FLOW);
        response.prettyPrint();
        response.then().assertThat().statusCode(200);
        JsonPath jsonPath = JsonPath.from(response.getBody().prettyPrint());
        assertThat(jsonPath.get("data.caseMatches[0]"), notNullValue());
        assertThat(jsonPath.get("data.caseMatches[0]"), notNullValue());
        assertThat(jsonPath.get("data.caseMatches[0].value.fullName"), is(equalTo(NAME)));
        assertThat(jsonPath.get("data.caseMatches[0].value.dob"), is(equalTo(DATE_OF_BIRTH)));
        assertThat(jsonPath.get("data.caseMatches[0].value.dod"), is(equalTo(DATE_OF_DEATH)));
    }

    @Test
    public void shouldReturnNoMatchingCaseWhenStandingSearchFlow() {
        Response response = search(SEARCH_FROM_STANDING_SEARCH_FLOW);
        JsonPath jsonPath = JsonPath.from(response.getBody().prettyPrint());
        assertThat(jsonPath.get("data.caseMatches"), is(empty()));
    }

    @Test
    public void shouldReturnMatchingCaseWhenWillLodgementSearchFlow() {
        Response response = search(WILL_LODGEMENT_MATCH_CASE_JSON, SEARCH_FROM_WILL_LODGEMENT_FLOW);
        response.prettyPrint();
        response.then().assertThat().statusCode(200);
        JsonPath jsonPath = JsonPath.from(response.getBody().prettyPrint());
        assertThat(jsonPath.get("data.caseMatches[0]"), notNullValue());
        assertThat(jsonPath.get("data.caseMatches[0]"), notNullValue());
        assertThat(jsonPath.get("data.caseMatches[0].value.fullName"), is(equalTo(NAME)));
        assertThat(jsonPath.get("data.caseMatches[0].value.dob"), is(equalTo(DATE_OF_BIRTH)));
        assertThat(jsonPath.get("data.caseMatches[0].value.dod"), is(equalTo(DATE_OF_DEATH)));
    }

    @Test
    public void shouldReturnNoMatchingCaseWhenWillLodgementSearchFlow() {
        Response response = search(SEARCH_FROM_WILL_LODGEMENT_FLOW);
        JsonPath jsonPath = JsonPath.from(response.getBody().prettyPrint());
        assertThat(jsonPath.get("data.caseMatches"), is(empty()));
    }

    private Response search(String path) {
        Response response = RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithCaseworkerUser())
                .body(updateDodInJson())
                .when().post(path)
                .andReturn();
        response.prettyPrint();
        response.then().assertThat().statusCode(200);
        return response;
    }

    private Response search(String jsonFileName, String path) {
        return RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithCaseworkerUser())
                .body(getJsonFromFile(jsonFileName))
                .when().post(path)
                .andReturn();
    }

    private String updateDodInJson() {
        String json = getJsonFromFile(GRANT_OF_PROBATE_MATCH_CASE_JSON);
        json = json.replaceAll("2020-01-01", "2021-01-01");
        return json;
    }
}