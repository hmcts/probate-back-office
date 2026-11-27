package uk.gov.hmcts.probate.service.consumer;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.annotations.PactFolder;
import org.apache.http.client.fluent.Executor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.hmcts.probate.model.wa.TaskSearchResponse;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.wa.WaApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(PactConsumerTestExt.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@PactTestFor(providerName = "wa_task_management_api", port = "8991")
@PactFolder("pacts")
@SpringBootTest({"auth.provider.client.user: http://localhost:8891"})
@TestPropertySource(locations = {"/application.properties"})
public class WaApiConsumerTest {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_TOKEN = "Bearer someAuthorizationToken";
    private static final String SERVICE_AUTHORIZATION_HEADER = "ServiceAuthorization";
    private static final String SERVICE_AUTH_TOKEN = "someServiceAuthToken";
    private static final String CASE_ID = "1593694526480034";

    @Autowired
    private WaApi waApi;

    @MockitoBean
    private SecurityUtils securityUtils;

    @MockitoBean
    private AuthTokenGenerator authTokenGenerator;

    @BeforeEach
    public void prepareTest() throws Exception {
        Thread.sleep(2000);
    }

    @AfterEach
    void teardown() {
        Executor.closeIdleConnections();
    }

    @Pact(provider = "wa_task_management_api", consumer = "probate_backOffice")
    public RequestResponsePact generatePactFragmentSearchTasksByCaseId(PactDslWithProvider builder) {
        return builder
            .given("tasks exist for a probate case")
            .uponReceiving("a request to retrieve tasks by case id")
            .path("/task/searchByCaseId")
            .method("GET")
            .headers(
                AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN,
                SERVICE_AUTHORIZATION_HEADER, SERVICE_AUTH_TOKEN
            )
            .query("case_id=" + CASE_ID)
            .willRespondWith()
            .status(200)
            .body(buildTaskSearchResponseDsl())
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "generatePactFragmentSearchTasksByCaseId")
    public void verifySearchTasksByCaseId() {
        ResponseEntity<TaskSearchResponse> response = waApi.searchTasksByCaseId(
            AUTHORIZATION_TOKEN,
            SERVICE_AUTH_TOKEN,
            CASE_ID
        );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().tasks());
        assertEquals(1, response.getBody().tasks().size());
        assertEquals("taskId123", response.getBody().tasks().get(0).getId());
        assertEquals("reviewTheAppeal", response.getBody().tasks().get(0).getType());
        assertEquals("unassigned", response.getBody().tasks().get(0).getTaskState());
    }

    private DslPart buildTaskSearchResponseDsl() {
        return newJsonBody(o ->
            o.minArrayLike("tasks", 1, task -> {
                task.stringType("id", "taskId123");
                task.stringType("name", "Review The Appeal");
                task.stringType("type", "reviewTheAppeal");
                task.stringType("task_state", "unassigned");
                task.stringType("task_system", "SELF");
                task.stringType("security_classification", "PUBLIC");
                task.stringType("task_title", "Review The Appeal");
                task.stringType("location", "765324");
                task.stringType("location_name", "Taylor House");
                task.stringType("execution_type", "Case Management Task");
                task.stringType("jurisdiction", "PROBATE");
                task.stringType("region", "1");
                task.stringType("case_type_id", "GrantOfRepresentation");
                task.stringType("case_id", CASE_ID);
                task.stringType("case_category", "Grant Of Probate");
                task.stringType("case_name", "Test Deceased");
                task.booleanType("auto_assigned", false);
                task.booleanType("warnings", false);
            })
        ).build();
    }
}
