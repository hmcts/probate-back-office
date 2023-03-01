package uk.gov.hmcts.probate.service.consumer;


import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.annotations.PactFolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Executor;
import org.json.JSONException;
import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody;

@ExtendWith(PactConsumerTestExt.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@PactTestFor(providerName = "acc_manageCaseAssignment", port = "8893")
@PactFolder("pacts")
@SpringBootTest({
    "aca.api.url : localhost:8893"
})
public class AssignCaseServiceConsumerTest {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_TOKEN = "Bearer some-access-token";
    private static final String SERVICE_AUTHORIZATION_HEADER = "ServiceAuthorization";
    private static final long CASE_ID = 1583841721773828L;
    private static final String USER_ID = "userId";
    private static final String SERVICE_AUTH_TOKEN = "someServiceAuthToken";

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AssignCaseAccessClient assignCaseAccessClient;


    @BeforeEach
    public void setUpEachTest() throws InterruptedException {
        Thread.sleep(2000);
    }

    @After
    void teardown() {
        Executor.closeIdleConnections();
    }

    @Pact(provider = "acc_manageCaseAssignment", consumer = "probate_backOffice")
    public RequestResponsePact generatePactFragmentForAssign(PactDslWithProvider builder)
        throws JSONException, IOException {
        // @formatter:off
        return builder
            .given("Assign a user to a case")
            .uponReceiving("A request for that case to be assigned")
            .method("POST")
            .headers(SERVICE_AUTHORIZATION_HEADER, SERVICE_AUTH_TOKEN, AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
            .body(createJsonObject(buildAssignCaseAccessRequest()))
            .query("use_user_token=true")
            .path("/case-assignments")
            .willRespondWith()
            .body(buildAssignCasesResponseDsl())
            .status(HttpStatus.SC_CREATED)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "generatePactFragmentForAssign")
    public void verifyAssignAccessToCase() {

        assignCaseAccessClient.assignCaseAccess(AUTHORIZATION_TOKEN, SERVICE_AUTH_TOKEN, Boolean.TRUE,
            buildAssignCaseAccessRequest());

    }

    private DslPart buildAssignCasesResponseDsl() {
        return newJsonBody((o) -> {
            o.stringType("status_message",
                "Roles Role1,Role2 from the organisation policies successfully assigned to the assignee.");
        }).build();
    }

    private String createJsonObject(Object obj) throws IOException {
        return objectMapper.writeValueAsString(obj);
    }

    private AssignCaseAccessRequest buildAssignCaseAccessRequest() {
        return AssignCaseAccessRequest
            .builder()
            .caseId(Long.toString(CASE_ID))
            .assigneeId("0a5874a4-3f38-4bbd-ba4c")
            .caseTypeId("PROBATE")
            .build();
    }
}