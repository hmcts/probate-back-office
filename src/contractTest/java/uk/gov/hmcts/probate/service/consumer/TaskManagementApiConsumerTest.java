package uk.gov.hmcts.probate.service.consumer;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.annotations.PactFolder;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.probate.model.wa.GetTasksCompletableResponse;
import uk.gov.hmcts.probate.model.wa.SearchEventAndCase;
import uk.gov.hmcts.probate.model.wa.TaskData;
import uk.gov.hmcts.probate.service.wa.WaApi;

import java.util.Map;

import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ExtendWith(PactConsumerTestExt.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@PactFolder("pacts")
@SpringBootTest
@TestPropertySource(locations = {"/application.properties"})
@PactTestFor(providerName = "wa_task_management_api_search_completable", port = "8991")
public class TaskManagementApiConsumerTest {

    @Autowired
    protected WaApi waApi;

    public static final String CONTENT_TYPE = "Content-Type";
    private static final String WA_URL = "/task";
    private static final String WA_SEARCH_FOR_COMPLETABLE = WA_URL + "/search-for-completable";
    private static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";
    private static final String AUTH_TOKEN = "Bearer someAuthorizationToken";
    private static final String SERVICE_AUTH_TOKEN = "Bearer someServiceAuthorizationToken";

    @Pact(provider = "wa_task_management_api_search_completable", consumer = "probate_backOffice")
    public RequestResponsePact executeSearchForCompletable200(PactDslWithProvider builder) {
        return builder
                .given("appropriate tasks are returned by search for completable")
                .uponReceiving("Provider receives a POST /task/search-for-completable request from a WA API")
                .path(WA_SEARCH_FOR_COMPLETABLE)
                .method(HttpMethod.POST.toString())
                .headers(getTaskManagementServiceResponseHeaders())
                .matchHeader(AUTHORIZATION, AUTH_TOKEN)
                .matchHeader(SERVICE_AUTHORIZATION, SERVICE_AUTH_TOKEN)
                .body(creteSearchEventCaseRequest())
                .willRespondWith()
                .status(HttpStatus.OK.value())
                .body(createResponseForGetTask())
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "executeSearchForCompletable200", pactVersion = PactSpecVersion.V3)
    void testSearchForCompletable200Test() {
        ResponseEntity<GetTasksCompletableResponse<TaskData>> responseEntity
                = waApi.searchWithCriteriaForAutomaticCompletion(
                AUTH_TOKEN,
                SERVICE_AUTH_TOKEN,
                new SearchEventAndCase("1617708245335311",
                        "requestRespondentEvidence",
                        "IA",
                        "Asylum")
        );
        Assertions.assertNotNull(responseEntity);
    }

    private Map<String, String> getTaskManagementServiceResponseHeaders() {

        return ImmutableMap.<String, String>builder()
                .put(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .put(SERVICE_AUTHORIZATION, SERVICE_AUTH_TOKEN)
                .put(AUTHORIZATION, AUTH_TOKEN)
                .build();
    }


    private PactDslJsonBody creteSearchEventCaseRequest() {
        return new PactDslJsonBody()
                .stringType("case_id", "1617708245335311")
                .stringValue("event_id", "requestRespondentEvidence")
                .stringValue("case_jurisdiction", "IA")
                .stringValue("case_type", "Asylum");
    }

    private DslPart createResponseForGetTask() {
        return newJsonBody(
                o -> o
                        .booleanType("task_required_for_event", false)
                        .minArrayLike("tasks", 1, 1,
                                task -> task
                                        .stringType("id", "4d4b6fgh-c91f-433f-92ac-e456ae34f72a")
                                        .stringType("name", "Review the appeal")
                                        .stringType("assignee", "10bac6bf-80a7-4c81-b2db-516aba826be6")
                                        .stringType("type", "ReviewTheAppeal")
                                        .stringType("task_state", "assigned")
                                        .stringType("task_system", "SELF")
                                        .stringType("security_classification", "PUBLIC")
                                        .stringType("task_title", "Review the appeal")
                                        .datetime("due_date", "yyyy-MM-dd'T'HH:mm:ssZ")
                                        .datetime("created_date", "yyyy-MM-dd'T'HH:mm:ssZ")
                                        .stringType("location_name", "Taylor House")
                                        .stringType("location", "765324")
                                        .stringType("execution_type", "Case Management Task")
                                        .stringType("jurisdiction", "IA")
                                        .stringType("region", "1")
                                        .stringType("case_type_id", "Asylum")
                                        .stringType("case_id", "1617708245335311")
                                        .stringType("case_category", "refusalOfHumanRights")
                                        .stringType("case_name", "Bob Smith")
                                        .booleanType("auto_assigned", true)
                                        .booleanType("warnings", false)
                                        .stringType("work_type_id", "hearing_work")
                                        .stringType("work_type_label", "Hearing work")
                                        .stringType("role_category", "LEGAL_OPERATIONS")
                                        .stringType("description", "a description")
                                        .stringType("next_hearing_id", "nextHearingId")
                                        .datetime("next_hearing_date", "yyyy-MM-dd'T'HH:mm:ssZ")
                        )).build();
    }

}