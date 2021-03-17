package uk.gov.hmcts.probate.functional.search;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import static java.time.LocalDate.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringIntegrationSerenityRunner.class)
public class StandingSearchTests extends IntegrationTestBase {

    private static final int MONTHS_TO_ADD = 6;
    private static final String DEFAULT_APPLICATION_TYPE = "Personal";
    private static final String DEFAULT_REGISTRY_LOCATION = "Leeds";
    public static final String STANDING_SEARCH_CREATE = "/standing-search/create";

    @Test
    public void standingSearchCreatedShouldReturnDataPayloadOkResponseCode(){
        Response response = RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile("/search/standingSearchPayload.json"))
                .when().post(STANDING_SEARCH_CREATE)
                .andReturn();

        response.then().assertThat().statusCode(200);

        JsonPath jsonPath = JsonPath.from(response.prettyPrint());
        assertThat(ofPattern("yyyy-MM-dd").format(now().plusMonths(MONTHS_TO_ADD)), is(equalTo(jsonPath.get("data.expiryDate"))));
        assertThat(jsonPath.get("data.errors"), is(nullValue()));
        assertThat(jsonPath.get("data.registryLocation"),equalTo("Manchester"));
        assertThat(jsonPath.get("data.applicationType"),equalTo("Solicitor"));
    }

    @Test
    public void standingSearchCreatedShouldReturnDefaultValues() {
        //ARRANGE
        String jsonAsString = utils.getJsonFromFile("/search/standingSearchPayload.json");
        jsonAsString = jsonAsString.replaceFirst("\"registryLocation\": \"Manchester\",", "");
        jsonAsString = jsonAsString.replaceFirst("\"applicationType\": \"Solicitor\",", "");
        //ACT
        Response response = RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(jsonAsString)
                .when().post(STANDING_SEARCH_CREATE)
                .andReturn();
        //ASSERT
        response.then().assertThat().statusCode(200);
        JsonPath jsonPath = JsonPath.from(response.prettyPrint());
        assertThat(jsonPath.get("data.registryLocation"), equalTo(DEFAULT_REGISTRY_LOCATION));
        assertThat(jsonPath.get("data.applicationType"), equalTo(DEFAULT_APPLICATION_TYPE));
    }
}
