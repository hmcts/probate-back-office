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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
                new SearchEventAndCase("1789150518978844",
                        "boAmendCaseDetailsForAwaitingDocumentation",
                        "PROBATE",
                        "grantofrepresentation")
        );
        assertNotNull(responseEntity);
        assertThat(responseEntity.getStatusCodeValue())
                .isEqualTo(HttpStatus.OK.value());
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
                .stringType("case_id", "1789150518978844")
                .stringValue("event_id", "boAmendCaseDetailsForAwaitingDocumentation")
                .stringValue("case_jurisdiction", "PROBATE")
                .stringValue("case_type", "grantofrepresentation");
    }

    private DslPart createResponseForGetTask() {
        return newJsonBody(
                o -> o
                        .booleanType("task_required_for_event", false)
                        .minArrayLike("tasks", 1, 1,
                                task -> task
                                        .stringType("id", "c6719957-ae0c-11f1-8492-b65ccab2630f")
                                        .stringType("name", "Examine Digital Case - Probate")
                                        .stringType("assignee", "10bac6bf-80a7-4c81-b2db-516aba826be6")
                                        .stringType("type", "ExamineDigitalCaseProbate")
                                        .stringType("task_state", "assigned")
                                        .stringType("task_system", "SELF")
                                        .stringType("security_classification", "PUBLIC")
                                        .stringType("task_title", "Review the appeal")
                                        .datetime("due_date", "yyyy-MM-dd'T'HH:mm:ssZ")
                                        .datetime("created_date", "yyyy-MM-dd'T'HH:mm:ssZ")
                                        .stringType("location_name", "London")
                                        .stringType("location", "London")
                                        .stringType("execution_type", "Case Management Task")
                                        .stringType("jurisdiction", "IA")
                                        .stringType("region", "1")
                                        .stringType("case_type_id", "grantofrepresentation")
                                        .stringType("case_id", "1789150518978844")
                                        .stringType("case_category", "Probate")
                                        .stringType("case_name", "Bob Smith")
                                        .booleanType("auto_assigned", true)
                                        .booleanType("warnings", false)
                                        .stringType("work_type_id", "applications")
                                        .stringType("work_type_label", "Applications")
                                        .stringType("role_category", "CTSC")
                                        .stringType("description", "a description")
                                        .stringType("next_hearing_id", "nextHearingId")
                                        .datetime("next_hearing_date", "yyyy-MM-dd'T'HH:mm:ssZ")
                        )).build();
    }

}