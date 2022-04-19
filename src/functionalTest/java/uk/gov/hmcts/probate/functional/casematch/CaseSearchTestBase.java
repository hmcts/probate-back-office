package uk.gov.hmcts.probate.functional.casematch;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import java.io.IOException;

public abstract class CaseSearchTestBase extends IntegrationTestBase {
    protected static final String ERROR_MSG = "You may only select one legacy record for import at a time.";
    protected static final String GRANT_OF_PROBATE_MATCH_CASE_JSON = "casematch/grantOfProbateMatchCase.json";
    protected static final String CAVEAT_MATCH_CASE_JSON = "casematch/caveatFlowSearchMatchCase.json";
    protected static final String STANDING_SEARCH_MATCH_CASE_JSON = "casematch/standingSearchMatchCase.json";
    protected static final String WILL_LODGEMENT_MATCH_CASE_JSON = "casematch/willLodgementMatchCase.json";

    protected Response search(String path) throws IOException {
        final Response response = RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithCaseworkerUser())
            .body(modifyDODInJson())
            .when().post(path)
            .andReturn();
        response.then().assertThat().statusCode(200);
        return response;
    }

    protected Response search(String jsonFileName, String path) throws IOException {
        return RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithCaseworkerUser())
            .body(getJsonFromFile(jsonFileName))
            .when().post(path)
            .andReturn();
    }

    private String modifyDODInJson() throws IOException {
        String json = getJsonFromFile(GRANT_OF_PROBATE_MATCH_CASE_JSON);
        json = json.replaceAll("2020-01-01", "2021-01-01");
        return json;
    }


}
