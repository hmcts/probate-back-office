package uk.gov.hmcts.probate.functional.lifeeventservice;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import static junit.framework.TestCase.assertEquals;

@RunWith(SpringIntegrationSerenityRunner.class)
public class LifeEventServiceTest extends IntegrationTestBase {

    @Test
    public void toDo() {
        final String jsonFromFile = utils.getJsonFromFile("caseprogress/01-appCreated.json");
        
        Response response = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(jsonFromFile)
            .when().post("/lifeevent/update");

        assertEquals(200, response.getStatusCode());
    }
}


