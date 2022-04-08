package uk.gov.hmcts.probate.functional.casematch;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@Slf4j
@RunWith(SpringIntegrationSerenityRunner.class)
public class LegacyTests extends CaseSearchTestBase {
    private static final String PROBATE_LEGACY_SEARCH_JSON = "casematch/grantOfProbateLegacy.json";
    private static final String CAVEAT_LEGACY_SEARCH_JSON = "casematch/caveatLegacySearch.json";
    private static final String WILL_LODGEMENT_LEGACY_SEARCH_JSON = "casematch/willLodgementLegacySearch.json";
    private static final String STANDING_SEARCH_LEGACY_SEARCH_JSON = "casematch/standingSearchLegacySearch.json";
    private static final String IMPORT_LEGACY_GRANT_FLOW = "/case-matching/import-legacy-from-grant-flow";
    private static final String IMPORT_LEGACY_CAVEAT_FLOW = "/case-matching/import-legacy-from-caveat-flow";
    private static final String IMPORT_LEGACY_STANDING_SEARCH =
        "/case-matching/import-legacy-from-standing-search-flow";
    private static final String IMPORT_LEGACY_WILL_LODGEMENT_SEARCH =
        "/case-matching/import-legacy-from-will-lodgement-flow";

    @Test
    public void shouldReturnErrorWhenMoreThanOneCaseMatchFoundInLegacyGrantFlowImport() throws IOException {
        final Response response = search(PROBATE_LEGACY_SEARCH_JSON, IMPORT_LEGACY_GRANT_FLOW);
        response.then().assertThat().statusCode(200);
        final JsonPath jsonPath = JsonPath.from(response.getBody().prettyPrint());
        assertThat(jsonPath.get("errors[0]"), is(equalTo(ERROR_MSG)));
    }

    @Test
    public void shouldReturnErrorWheNoCaseMatchInLegacyCaveatFlowImport() throws IOException {
        final Response response = search(CAVEAT_LEGACY_SEARCH_JSON, IMPORT_LEGACY_CAVEAT_FLOW);
        response.then().assertThat().statusCode(200);
        final JsonPath jsonPath = JsonPath.from(response.getBody().prettyPrint());
        assertThat(jsonPath.get("errors[0]"), is(equalTo(ERROR_MSG)));
    }

    @Test
    public void shouldReturnErrorWhenMoreThanOneCaseMatchFoundInLegacyWillLodgementImport() throws IOException {
        final Response response = search(WILL_LODGEMENT_LEGACY_SEARCH_JSON, IMPORT_LEGACY_WILL_LODGEMENT_SEARCH);
        response.then().assertThat().statusCode(200);
        final JsonPath jsonPath = JsonPath.from(response.getBody().prettyPrint());
        assertThat(jsonPath.get("errors[0]"), is(equalTo(ERROR_MSG)));
    }

    @Test
    public void shouldReturnErrorWhenMoreThanOneCaseMatchFoundInLegacyStandingSearchImport() throws IOException {
        final Response response = search(STANDING_SEARCH_LEGACY_SEARCH_JSON, IMPORT_LEGACY_STANDING_SEARCH);
        response.then().assertThat().statusCode(200);
        final JsonPath jsonPath = JsonPath.from(response.getBody().prettyPrint());
        assertThat(jsonPath.get("errors[0]"), is(equalTo(ERROR_MSG)));
    }

    @Test
    public void shouldReturnSucessWhenNoCaseMatchInLegacyGrantFlow() throws IOException {
        final Response response = search(CAVEAT_MATCH_CASE_JSON, IMPORT_LEGACY_GRANT_FLOW);
        response.then().assertThat().statusCode(200);
    }

    @Test
    public void shouldReturnSucessWhenNoCaseMatchInLegacyCaveatFlowImport() throws IOException {
        final Response response = search(GRANT_OF_PROBATE_MATCH_CASE_JSON, IMPORT_LEGACY_CAVEAT_FLOW);
        response.then().assertThat().statusCode(200);
    }

    @Test
    public void shouldReturnSucessWhenNoCaseMatchInLegacyWillLodgementImport() throws IOException {
        final Response response = search(WILL_LODGEMENT_MATCH_CASE_JSON, IMPORT_LEGACY_WILL_LODGEMENT_SEARCH);
        response.then().assertThat().statusCode(200);
    }

    @Test
    public void shouldReturSucessWhenNoCaseMatchInLegacyStandingSearchImport() throws IOException {
        final Response response = search(STANDING_SEARCH_MATCH_CASE_JSON, IMPORT_LEGACY_STANDING_SEARCH);
        response.then().assertThat().statusCode(200);
    }

}
