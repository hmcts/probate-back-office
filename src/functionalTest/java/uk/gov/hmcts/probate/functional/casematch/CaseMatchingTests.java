package uk.gov.hmcts.probate.functional.casematch;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static uk.gov.hmcts.probate.functional.util.FunctionalTestUtils.TOKEN_PARM;

@Slf4j
@RunWith(SpringIntegrationSerenityRunner.class)
public class CaseMatchingTests extends CaseSearchTestBase {
    public static final String NAME = "Ned Stark";
    private static final String GRANT_OF_PROBATE_JSON = "casematch/applyForGrantPayload.json";
    private static final String EVENT_PARAMETER = "EVENT_PARM";
    private static final String APPLY_GRANT_EVENT = "applyForGrant";
    private static final String CREATE_CASE_EVENT = "createCase";
    private static final String SEARCH_GRANT_FLOW = "/case-matching/search-from-grant-flow";

    @Before
    public void setUp() {
        initialiseConfig();
    }


    @Test
    public void shouldReturnNoMatchingCaseWhenGOPSearchFlow() throws IOException {
        final Response response = search(SEARCH_GRANT_FLOW);
        final JsonPath jsonPath = JsonPath.from(response.getBody().prettyPrint());
        assertThat(jsonPath.get("data.caseMatches"), is(empty()));
    }


    public void createCase() throws IOException {
        //Create Case
        final String baseCaseJson = utils.getJsonFromFile(GRANT_OF_PROBATE_JSON);
        final String applyForGrantyCaseJson = utils.replaceAttribute(baseCaseJson, EVENT_PARAMETER, APPLY_GRANT_EVENT);
        final String applyForGrantCase = utils.createCaseAsCaseworker(applyForGrantyCaseJson, APPLY_GRANT_EVENT);
        final JsonPath jsonPathApply = JsonPath.from(applyForGrantCase);
        final String caseId = jsonPathApply.get("id").toString();
        log.info("createCase : caseId {} ", caseId);
        assertThat(caseId, is(notNullValue()));
        log.info("CaseMatchingTests : createCase : caseId {} ", caseId);
        //Update Case
        //Move PAAppCreated to createCase state
        final String updateToken = utils.startUpdateCaseAsCaseworker(caseId, CREATE_CASE_EVENT);
        String updateBaseCase = utils.replaceAttribute(baseCaseJson, TOKEN_PARM, updateToken);
        updateBaseCase = utils.replaceAttribute(updateBaseCase, EVENT_PARAMETER, CREATE_CASE_EVENT);
        utils.continueUpdateCaseAsCaseworker(updateBaseCase, caseId);
    }
}
