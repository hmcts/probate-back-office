package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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

    private CaseData solicitorCaseData;

    private CaseData personalCaseData;

    @Before
    public void setUp() throws NotificationClientException {
        doReturn(null)
                .when(notificationClient).sendEmail(anyString(), anyString(), any(), anyString());

        solicitorCaseData = CaseData.builder()
                .applicationType(SOLICITOR)
                .registryLocation("Birmingham")
                .solsSolicitorEmail("solicitor@test.com")
                .solsSolicitorAppReference("1234-5678-9012")
                .build();

        personalCaseData = CaseData.builder()
                .applicationType(PERSONAL)
                .registryLocation("Birmingham")
                .primaryApplicantEmailAddress("personal@test.com")
                .build();
    }

    @Test
    public void sendDocumentsReceivedEmailToPersonalApplicant()
            throws NotificationClientException, BadRequestException {

        notificationService.sendEmail(DOCUMENTS_RECEIVED, personalCaseData);

        verify(notificationClient).sendEmail(
                eq("pa-document-received"),
                eq("personal@test.com"),
                any(),
                anyString());
    }

    @Test
    public void sendDocumentsReceivedEmailToSolicitor()
            throws NotificationClientException, BadRequestException {

        notificationService.sendEmail(DOCUMENTS_RECEIVED, solicitorCaseData);

        verify(notificationClient).sendEmail(
                eq("sol-document-received"),
                eq("solicitor@test.com"),
                any(),
                eq("1234-5678-9012"));
    }

    @Test
    public void sendGrantIssuedEmailToPersonalApplicant()
            throws NotificationClientException, BadRequestException {

        notificationService.sendEmail(GRANT_ISSUED, personalCaseData);

        verify(notificationClient).sendEmail(
                eq("pa-grant-issued"),
                eq("personal@test.com"),
                any(),
                anyString());
    }

    @Test
    public void sendGrantIssuedEmailToSolicitor()
            throws NotificationClientException, BadRequestException {

        notificationService.sendEmail(GRANT_ISSUED, solicitorCaseData);

        verify(notificationClient).sendEmail(
                eq("sol-grant-issued"),
                eq("solicitor@test.com"),
                any(),
                eq("1234-5678-9012"));
    }
}
