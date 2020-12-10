package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ExecutorsApplyingNotification;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ExecutorsApplyingNotificationServiceTest {

    private final ExecutorsApplyingNotificationService executorsApplyingNotificationService = new ExecutorsApplyingNotificationService();

    private CaseData caseDataPersonal;
    private CaseData caseDataSolicitor;

    private List<CollectionMember<ExecutorsApplyingNotification>> expectedResponse;
    private List<CollectionMember<AdditionalExecutorApplying>> additionalExecutorApplyingList;

    private static final SolsAddress ADDRESS = SolsAddress.builder()
            .addressLine1("123 street")
            .addressLine2("line 2")
            .postCode("AB1 2CD")
            .build();

    @Before
    public void setup() {
        expectedResponse = new ArrayList<>();
        additionalExecutorApplyingList = new ArrayList<>();

        caseDataPersonal = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .primaryApplicantForenames("Bob")
                .primaryApplicantSurname("Smith")
                .primaryApplicantEmailAddress("primary@probate-test.com")
                .primaryApplicantAddress(ADDRESS)
                .additionalExecutorsApplying(additionalExecutorApplyingList)
                .primaryApplicantIsApplying("Yes")
                .build();

        caseDataSolicitor = CaseData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .solsSOTName("Timmy Tom")
                .solsSolicitorEmail("solicitor@probate-test.com")
                .solsSolicitorAddress(ADDRESS)
                .primaryApplicantForenames("Bob")
                .primaryApplicantSurname("Smith")
                .primaryApplicantEmailAddress("primary@probate-test.com")
                .primaryApplicantAddress(ADDRESS)
                .additionalExecutorsApplying(additionalExecutorApplyingList)
                .primaryApplicantIsApplying("Yes")
                .build();
    }


    @Test
    public void testPaAndExecutorIsAddedToExecutorsApplyingNotificationList() {
        additionalExecutorApplyingList.add(buildExecApplying("Tommy Tank", "executor1@probate-test.com"));

        caseDataPersonal = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .primaryApplicantForenames("Bob")
                .primaryApplicantSurname("Smith")
                .primaryApplicantEmailAddress("primary@probate-test.com")
                .primaryApplicantAddress(ADDRESS)
                .additionalExecutorsApplying(additionalExecutorApplyingList)
                .primaryApplicantIsApplying("Yes")
                .build();

        CollectionMember<ExecutorsApplyingNotification> paApplying =
                buildExecNotification("Bob Smith", "primary@probate-test.com", "1");

        CollectionMember<ExecutorsApplyingNotification> execApplying =
                buildExecNotification("Tommy Tank", "executor1@probate-test.com", "2");

        expectedResponse.add(paApplying);
        expectedResponse.add(execApplying);

        assertEquals(expectedResponse, executorsApplyingNotificationService.createExecutorList(caseDataPersonal));
    }

    @Test
    public void testPaNotApplyingAddsExecutorsOnly() {
        caseDataPersonal = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .primaryApplicantForenames("Bob")
                .primaryApplicantSurname("Smith")
                .primaryApplicantEmailAddress("primary@probate-test.com")
                .primaryApplicantAddress(ADDRESS)
                .additionalExecutorsApplying(additionalExecutorApplyingList)
                .primaryApplicantIsApplying("No")
                .build();

        additionalExecutorApplyingList.add(buildExecApplying("Tommy Tank", "executor1@probate-test.com"));

        CollectionMember<ExecutorsApplyingNotification> execApplying =
                buildExecNotification("Tommy Tank", "executor1@probate-test.com", "1");
        expectedResponse.add(execApplying);

        assertEquals(expectedResponse, executorsApplyingNotificationService.createExecutorList(caseDataPersonal));
    }

    @Test
    public void testPaApplyingIsNull() {
        caseDataPersonal = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .primaryApplicantForenames("Bob")
                .primaryApplicantSurname("Smith")
                .primaryApplicantEmailAddress("primary@probate-test.com")
                .primaryApplicantAddress(ADDRESS)
                .primaryApplicantIsApplying(null)
                .additionalExecutorsApplying(null)
                .build();

        CollectionMember<ExecutorsApplyingNotification> execApplying =
                buildExecNotification("Bob Smith", "primary@probate-test.com", "1");
        expectedResponse.add(execApplying);

        assertEquals(expectedResponse, executorsApplyingNotificationService.createExecutorList(caseDataPersonal));
        assertEquals(expectedResponse.size(), 1);
    }

    @Test
    public void testPaApplyingIsNotApplying() {
        caseDataPersonal = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .primaryApplicantForenames("Bob")
                .primaryApplicantSurname("Smith")
                .primaryApplicantEmailAddress("primary@probate-test.com")
                .primaryApplicantAddress(ADDRESS)
                .primaryApplicantIsApplying("No")
                .additionalExecutorsApplying(null)
                .build();

        assertEquals(expectedResponse, executorsApplyingNotificationService.createExecutorList(caseDataPersonal));
        assertEquals(expectedResponse.size(), 0);
    }

    @Test
    public void testPaIsApplying() {
        caseDataPersonal = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .primaryApplicantForenames("Bob")
                .primaryApplicantSurname("Smith")
                .primaryApplicantEmailAddress("primary@probate-test.com")
                .primaryApplicantAddress(ADDRESS)
                .primaryApplicantIsApplying("Yes")
                .additionalExecutorsApplying(null)
                .build();

        CollectionMember<ExecutorsApplyingNotification> execApplying =
                buildExecNotification("Bob Smith", "primary@probate-test.com", "1");
        expectedResponse.add(execApplying);

        assertEquals(expectedResponse, executorsApplyingNotificationService.createExecutorList(caseDataPersonal));
        assertEquals(expectedResponse.size(), 1);
    }

    @Test
    public void testMultipleExecutorsAreAddedSuccessfully() {
        additionalExecutorApplyingList.add(buildExecApplying("Tommy Tank", "executor1@probate-test.com"));
        additionalExecutorApplyingList.add(buildExecApplying("Bobby Bank", "executor2@probate-test.com"));
        additionalExecutorApplyingList.add(buildExecApplying("Dobby Dank", "executor3@probate-test.com"));
        additionalExecutorApplyingList.add(buildExecApplying("Jack Johnson", "executor4@probate-test.com"));

        caseDataPersonal = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .primaryApplicantForenames("Bob")
                .primaryApplicantSurname("Smith")
                .primaryApplicantEmailAddress("primary@probate-test.com")
                .primaryApplicantAddress(ADDRESS)
                .additionalExecutorsApplying(additionalExecutorApplyingList)
                .primaryApplicantIsApplying("No")
                .build();

        CollectionMember<ExecutorsApplyingNotification> executorNotification1 =
                buildExecNotification("Tommy Tank", "executor1@probate-test.com", "1");

        CollectionMember<ExecutorsApplyingNotification> executorNotification2 =
                buildExecNotification("Bobby Bank", "executor2@probate-test.com", "2");

        CollectionMember<ExecutorsApplyingNotification> executorNotification3 =
                buildExecNotification("Dobby Dank", "executor3@probate-test.com", "3");

        CollectionMember<ExecutorsApplyingNotification> executorNotification4 =
                buildExecNotification("Jack Johnson", "executor4@probate-test.com", "4");

        expectedResponse.add(executorNotification1);
        expectedResponse.add(executorNotification2);
        expectedResponse.add(executorNotification3);
        expectedResponse.add(executorNotification4);

        assertEquals(expectedResponse, executorsApplyingNotificationService.createExecutorList(caseDataPersonal));

    }

    @Test
    public void testSingleExecutorIsAddedSuccessfully() {
        caseDataPersonal = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .primaryApplicantForenames("Bob")
                .primaryApplicantSurname("Smith")
                .primaryApplicantEmailAddress("primary@probate-test.com")
                .primaryApplicantAddress(ADDRESS)
                .additionalExecutorsApplying(additionalExecutorApplyingList)
                .primaryApplicantIsApplying("No")
                .build();
        additionalExecutorApplyingList.add(buildExecApplying("Tommy Tank", "executor1@probate-test.com"));

        CollectionMember<ExecutorsApplyingNotification> executorNotification1 =
                buildExecNotification("Tommy Tank", "executor1@probate-test.com", "1");

        expectedResponse.add(executorNotification1);
        assertEquals(expectedResponse, executorsApplyingNotificationService.createExecutorList(caseDataPersonal));
    }

    @Test
    public void testNoExecutorsReturnsSuccessfully() {
        caseDataPersonal = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .primaryApplicantForenames("Bob")
                .primaryApplicantSurname("Smith")
                .primaryApplicantEmailAddress("primary@probate-test.com")
                .primaryApplicantAddress(ADDRESS)
                .primaryApplicantIsApplying("No")
                .build();
        assertEquals(expectedResponse, executorsApplyingNotificationService.createExecutorList(caseDataPersonal));
    }

    @Test
    public void testEmptyExecutorsReturnsSuccessfully() {
        caseDataPersonal = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .primaryApplicantForenames("Bob")
                .primaryApplicantSurname("Smith")
                .primaryApplicantEmailAddress("primary@probate-test.com")
                .primaryApplicantAddress(ADDRESS)
                .primaryApplicantIsApplying("No")
                .additionalExecutorsApplying(additionalExecutorApplyingList)
                .build();
        assertEquals(expectedResponse, executorsApplyingNotificationService.createExecutorList(caseDataPersonal));
    }

    @Test
    public void testExecutorsApplyingNotificationsIsCleared() {
        CollectionMember<ExecutorsApplyingNotification> executorNotification1 =
                buildExecNotification("Tommy Tank", "executor1@probate-test.com", "1");

        CollectionMember<ExecutorsApplyingNotification> executorNotification2 =
                buildExecNotification("Bobby Bank", "executor2@probate-test.com", "2");

        CollectionMember<ExecutorsApplyingNotification> executorNotification3 =
                buildExecNotification("Dobby Dank", "executor3@probate-test.com", "3");

        CollectionMember<ExecutorsApplyingNotification> executorNotification4 =
                buildExecNotification("Jack Johnson", "executor4@probate-test.com", "4");

        expectedResponse.add(executorNotification1);
        expectedResponse.add(executorNotification2);
        expectedResponse.add(executorNotification3);
        expectedResponse.add(executorNotification4);

        caseDataPersonal = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .primaryApplicantForenames("Bob")
                .primaryApplicantSurname("Smith")
                .primaryApplicantEmailAddress("primary@probate-test.com")
                .primaryApplicantAddress(ADDRESS)
                .additionalExecutorsApplying(additionalExecutorApplyingList)
                .primaryApplicantIsApplying("No")
                .executorsApplyingNotifications(expectedResponse)
                .build();

        assertEquals(new ArrayList<>(), executorsApplyingNotificationService.createExecutorList(caseDataPersonal));
    }

    @Test
    public void testSolicitorIsAddedToExecutorsApplyingNotificationList() {
        additionalExecutorApplyingList.add(buildExecApplying("Timmy Tom", "executor1@probate-test.com"));

        CollectionMember<ExecutorsApplyingNotification> solApplying =
                buildExecNotification("Timmy Tom", "solicitor@probate-test.com", "1");

        expectedResponse.add(solApplying);

        assertEquals(expectedResponse, executorsApplyingNotificationService.createExecutorList(caseDataSolicitor));
    }

    private CollectionMember<AdditionalExecutorApplying> buildExecApplying(String name, String email) {
        return new CollectionMember<>(AdditionalExecutorApplying.builder()
                .applyingExecutorName(name)
                .applyingExecutorEmail(email)
                .applyingExecutorAddress(ADDRESS)
                .build());
    }

    private CollectionMember<ExecutorsApplyingNotification> buildExecNotification(String name, String email,
                                                                                  String item) {
        return new CollectionMember<>(item, ExecutorsApplyingNotification.builder()
                .name(name)
                .email(email)
                .address(ADDRESS)
                .build());
    }
}
