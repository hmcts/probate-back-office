package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.State.CASE_STOPPED;
import static uk.gov.hmcts.probate.model.State.DOCUMENTS_RECEIVED;
import static uk.gov.hmcts.probate.model.State.GRANT_ISSUED;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @MockBean
    private AppInsights appInsights;

    @SpyBean
    private NotificationClient notificationClient;

    private CaseData personalCaseDataOxford;
    private CaseData solicitorCaseDataOxford;
    private CaseData personalCaseDataBirmingham;
    private CaseData solicitorCaseDataBirmingham;
    private CaseData personalCaseDataManchester;
    private CaseData solicitorCaseDataManchester;

    @Before
    public void setUp() throws NotificationClientException {
        doReturn(null)
                .when(notificationClient).sendEmail(anyString(), anyString(), any(), anyString(), anyString());

        personalCaseDataOxford = CaseData.builder()
                .applicationType(PERSONAL)
                .registryLocation("Oxford")
                .primaryApplicantEmailAddress("personal@test.com")
                .build();

        solicitorCaseDataOxford = CaseData.builder()
                .applicationType(SOLICITOR)
                .registryLocation("Oxford")
                .solsSolicitorEmail("solicitor@test.com")
                .solsSolicitorAppReference("1234-5678-9012")
                .build();

        personalCaseDataBirmingham = CaseData.builder()
                .applicationType(PERSONAL)
                .registryLocation("Birmingham")
                .primaryApplicantEmailAddress("personal@test.com")
                .build();

        solicitorCaseDataBirmingham = CaseData.builder()
                .applicationType(SOLICITOR)
                .registryLocation("Birmingham")
                .solsSolicitorEmail("solicitor@test.com")
                .solsSolicitorAppReference("1234-5678-9012")
                .build();

        personalCaseDataManchester = CaseData.builder()
                .applicationType(PERSONAL)
                .registryLocation("Manchester")
                .primaryApplicantEmailAddress("personal@test.com")
                .build();

        solicitorCaseDataManchester = CaseData.builder()
                .applicationType(SOLICITOR)
                .registryLocation("Manchester")
                .solsSolicitorEmail("solicitor@test.com")
                .solsSolicitorAppReference("1234-5678-9012")
                .build();
    }

    @Test
    public void sendDocumentsReceivedEmailToPersonalApplicantFromBirmingham()
            throws NotificationClientException, BadRequestException {

        notificationService.sendEmail(DOCUMENTS_RECEIVED, personalCaseDataBirmingham);

        verify(notificationClient).sendEmail(
                eq("pa-document-received"),
                eq("personal@test.com"),
                any(),
                anyString());
    }

    @Test
    public void sendDocumentsReceivedEmailToSolicitorFromBirmingham()
            throws NotificationClientException, BadRequestException {

        notificationService.sendEmail(DOCUMENTS_RECEIVED, solicitorCaseDataBirmingham);

        verify(notificationClient).sendEmail(
                eq("sol-document-received"),
                eq("solicitor@test.com"),
                any(),
                eq("1234-5678-9012"));
    }

    @Test
    public void sendGrantIssuedEmailToPersonalApplicantFromBirmingham()
            throws NotificationClientException, BadRequestException {

        notificationService.sendEmail(GRANT_ISSUED, personalCaseDataBirmingham);

        verify(notificationClient).sendEmail(
                eq("pa-grant-issued"),
                eq("personal@test.com"),
                any(),
                anyString());
    }

    @Test
    public void sendGrantIssuedEmailToSolicitorFromBirmingham()
            throws NotificationClientException, BadRequestException {

        notificationService.sendEmail(GRANT_ISSUED, solicitorCaseDataBirmingham);

        verify(notificationClient).sendEmail(
                eq("sol-grant-issued"),
                eq("solicitor@test.com"),
                any(),
                eq("1234-5678-9012"));
    }

    @Test
    public void sendCaseStoppedEmailToPersonalApplicantFromBirmingham()
            throws NotificationClientException, BadRequestException {

        notificationService.sendEmail(CASE_STOPPED, personalCaseDataBirmingham);

        verify(notificationClient).sendEmail(
                eq("pa-case-stopped"),
                eq("personal@test.com"),
                any(),
                anyString(),
                eq("birmingham-emailReplyToId"));
    }

    @Test
    public void sendCaseStoppedEmailToSolicitorFromBirmingham()
        throws NotificationClientException, BadRequestException {

        notificationService.sendEmail(CASE_STOPPED, solicitorCaseDataBirmingham);

        verify(notificationClient).sendEmail(
                eq("sol-case-stopped"),
                eq("solicitor@test.com"),
                any(),
                eq("1234-5678-9012"),
                eq("birmingham-emailReplyToId"));
    }

    @Test
    public void sendCaseStoppedEmailToPersonalApplicantFromOxford()
            throws NotificationClientException, BadRequestException {

        notificationService.sendEmail(CASE_STOPPED, personalCaseDataOxford);

        verify(notificationClient).sendEmail(
                eq("pa-case-stopped"),
                eq("personal@test.com"),
                any(),
                anyString(),
                eq("oxford-emailReplyToId"));
    }

    @Test
    public void sendCaseStoppedEmailToSolicitorFromOxford()
            throws NotificationClientException, BadRequestException {

        notificationService.sendEmail(CASE_STOPPED, solicitorCaseDataOxford);

        verify(notificationClient).sendEmail(
                eq("sol-case-stopped"),
                eq("solicitor@test.com"),
                any(),
                eq("1234-5678-9012"),
                eq("oxford-emailReplyToId"));
    }

    @Test
    public void sendCaseStoppedEmailToPersonalApplicantFromManchester()
            throws NotificationClientException, BadRequestException {

        notificationService.sendEmail(CASE_STOPPED, personalCaseDataManchester);

        verify(notificationClient).sendEmail(
                eq("pa-case-stopped"),
                eq("personal@test.com"),
                any(),
                anyString(),
                eq("manchester-emailReplyToId"));
    }

    @Test
    public void sendCaseStoppedEmailToSolicitorFromManchester()
            throws NotificationClientException, BadRequestException {

        notificationService.sendEmail(CASE_STOPPED, solicitorCaseDataManchester);

        verify(notificationClient).sendEmail(
                eq("sol-case-stopped"),
                eq("solicitor@test.com"),
                any(),
                eq("1234-5678-9012"),
                eq("manchester-emailReplyToId"));
    }
}
