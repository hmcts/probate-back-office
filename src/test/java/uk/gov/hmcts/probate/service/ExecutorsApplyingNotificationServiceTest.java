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
                .primaryApplicantEmailAddress("PA@test.com")
                .primaryApplicantAddress(ADDRESS)
                .additionalExecutorsApplying(additionalExecutorApplyingList)
                .primaryApplicantIsApplying("Yes")
                .build();

        caseDataSolicitor = CaseData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .solsSOTName("Timmy Tom")
                .solsSolicitorEmail("timmy@sol.com")
                .solsSolicitorAddress(ADDRESS)
                .primaryApplicantForenames("Bob")
                .primaryApplicantSurname("Smith")
                .primaryApplicantEmailAddress("PA@test.com")
                .primaryApplicantAddress(ADDRESS)
                .additionalExecutorsApplying(additionalExecutorApplyingList)
                .primaryApplicantIsApplying("Yes")
                .build();
    }


    @Test
    public void testPaAndExecutorIsAddedToExecutorsApplyingNotificationList() {
        additionalExecutorApplyingList.add(buildExecApplying("Tommy Tank", "tommy@test.com"));

        caseDataPersonal = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .primaryApplicantForenames("Bob")
                .primaryApplicantSurname("Smith")
                .primaryApplicantEmailAddress("PA@test.com")
                .primaryApplicantAddress(ADDRESS)
                .additionalExecutorsApplying(additionalExecutorApplyingList)
                .primaryApplicantIsApplying("Yes")
                .build();

        CollectionMember<ExecutorsApplyingNotification> paApplying =
                buildExecNotification("Bob Smith", "PA@test.com", "1");

        CollectionMember<ExecutorsApplyingNotification> execApplying =
                buildExecNotification("Tommy Tank", "tommy@test.com", "2");

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
                .primaryApplicantEmailAddress("PA@test.com")
                .primaryApplicantAddress(ADDRESS)
                .additionalExecutorsApplying(additionalExecutorApplyingList)
                .primaryApplicantIsApplying("No")
                .build();

        additionalExecutorApplyingList.add(buildExecApplying("Tommy Tank", "tommy@test.com"));

        CollectionMember<ExecutorsApplyingNotification> execApplying =
                buildExecNotification("Tommy Tank", "tommy@test.com", "1");
        expectedResponse.add(execApplying);

        assertEquals(expectedResponse, executorsApplyingNotificationService.createExecutorList(caseDataPersonal));
    }

    @Test
    public void testMultipleExecutorsAreAddedSuccessfully() {
        additionalExecutorApplyingList.add(buildExecApplying("Tommy Tank", "tommy@test.com"));
        additionalExecutorApplyingList.add(buildExecApplying("Bobby Bank", "bobby@test.com"));
        additionalExecutorApplyingList.add(buildExecApplying("Dobby Dank", "dobby@test.com"));
        additionalExecutorApplyingList.add(buildExecApplying("Jack Johnson", "jack@test.com"));

        caseDataPersonal = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .primaryApplicantForenames("Bob")
                .primaryApplicantSurname("Smith")
                .primaryApplicantEmailAddress("PA@test.com")
                .primaryApplicantAddress(ADDRESS)
                .additionalExecutorsApplying(additionalExecutorApplyingList)
                .primaryApplicantIsApplying("No")
                .build();

        CollectionMember<ExecutorsApplyingNotification> executorNotification1 =
                buildExecNotification("Tommy Tank", "tommy@test.com", "1");

        CollectionMember<ExecutorsApplyingNotification> executorNotification2 =
                buildExecNotification("Bobby Bank", "bobby@test.com", "2");

        CollectionMember<ExecutorsApplyingNotification> executorNotification3 =
                buildExecNotification("Dobby Dank", "dobby@test.com", "3");

        CollectionMember<ExecutorsApplyingNotification> executorNotification4 =
                buildExecNotification("Jack Johnson", "jack@test.com", "4");

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
                .primaryApplicantEmailAddress("PA@test.com")
                .primaryApplicantAddress(ADDRESS)
                .additionalExecutorsApplying(additionalExecutorApplyingList)
                .primaryApplicantIsApplying("No")
                .build();
        additionalExecutorApplyingList.add(buildExecApplying("Tommy Tank", "tommy@test.com"));

        CollectionMember<ExecutorsApplyingNotification> executorNotification1 =
                buildExecNotification("Tommy Tank", "tommy@test.com", "1");

        expectedResponse.add(executorNotification1);
        assertEquals(expectedResponse, executorsApplyingNotificationService.createExecutorList(caseDataPersonal));
    }

    @Test
    public void testNoExecutorsReturnsSuccessfully() {
        caseDataPersonal = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .primaryApplicantForenames("Bob")
                .primaryApplicantSurname("Smith")
                .primaryApplicantEmailAddress("PA@test.com")
                .primaryApplicantAddress(ADDRESS)
                .additionalExecutorsApplying(additionalExecutorApplyingList)
                .primaryApplicantIsApplying("No")
                .build();
        assertEquals(expectedResponse, executorsApplyingNotificationService.createExecutorList(caseDataPersonal));
    }

    @Test
    public void testExecutorsApplyingNotificationsIsCleared() {
        CollectionMember<ExecutorsApplyingNotification> executorNotification1 =
                buildExecNotification("Tommy Tank", "tommy@test.com", "1");

        CollectionMember<ExecutorsApplyingNotification> executorNotification2 =
                buildExecNotification("Bobby Bank", "bobby@test.com", "2");

        CollectionMember<ExecutorsApplyingNotification> executorNotification3 =
                buildExecNotification("Dobby Dank", "dobby@test.com", "3");

        CollectionMember<ExecutorsApplyingNotification> executorNotification4 =
                buildExecNotification("Jack Johnson", "jack@test.com", "4");

        expectedResponse.add(executorNotification1);
        expectedResponse.add(executorNotification2);
        expectedResponse.add(executorNotification3);
        expectedResponse.add(executorNotification4);

        caseDataPersonal = CaseData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .primaryApplicantForenames("Bob")
                .primaryApplicantSurname("Smith")
                .primaryApplicantEmailAddress("PA@test.com")
                .primaryApplicantAddress(ADDRESS)
                .additionalExecutorsApplying(additionalExecutorApplyingList)
                .primaryApplicantIsApplying("No")
                .executorsApplyingNotifications(expectedResponse)
                .build();

        assertEquals(new ArrayList<>(), executorsApplyingNotificationService.createExecutorList(caseDataPersonal));
    }

    @Test
    public void testSolicitorIsAddedToExecutorsApplyingNotificationList() {
        additionalExecutorApplyingList.add(buildExecApplying("Timmy Tom", "timmy@sol.com"));

        CollectionMember<ExecutorsApplyingNotification> solApplying =
                buildExecNotification("Timmy Tom", "timmy@sol.com", "1");

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
