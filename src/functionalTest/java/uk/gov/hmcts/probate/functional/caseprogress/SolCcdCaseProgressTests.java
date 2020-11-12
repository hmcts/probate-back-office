package uk.gov.hmcts.probate.functional.caseprogress;

import io.restassured.RestAssured;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

@RunWith(SpringIntegrationSerenityRunner.class)
public class SolCcdCaseProgressTests extends IntegrationTestBase  {

    private void validatePostSuccess(String jsonFileName, String URL) {
        RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile(jsonFileName))
                .when().post(URL)
                .then().assertThat().statusCode(200);
    }
}
