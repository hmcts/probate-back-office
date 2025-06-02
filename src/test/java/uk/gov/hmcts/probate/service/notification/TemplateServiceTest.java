package uk.gov.hmcts.probate.service.notification;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.config.notifications.EmailTemplates;
import uk.gov.hmcts.probate.config.notifications.NotificationTemplates;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.LanguagePreference;

import static uk.gov.hmcts.probate.model.Constants.CHANNEL_CHOICE_PAPERFORM;
import static uk.gov.hmcts.probate.model.Constants.CHANNEL_CHOICE_DIGITAL;


import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class TemplateServiceTest {

    @Mock
    NotificationTemplates notificationTemplates;

    @Mock
    Map<LanguagePreference, Map<ApplicationType, EmailTemplates>> emailTemplatesMap;

    @Mock
    Map<ApplicationType, EmailTemplates> applicationTypeTemplatesMap;

    @Mock
    Map<ApplicationType, EmailTemplates> applicationTypeTemplatesMapWelsh;

    @Mock
    EmailTemplates emailTemplates;


    @Mock
    TemplateService underTest;

    AutoCloseable closeableMocks;

    private static final String NO = "No";
    private static final String YES = "Yes";

    @BeforeEach
    void setUp() {
        closeableMocks = MockitoAnnotations.openMocks(this);
        underTest = new TemplateService(notificationTemplates);
        when(notificationTemplates.getEmail()).thenReturn(emailTemplatesMap);
        when(emailTemplatesMap.get(LanguagePreference.ENGLISH)).thenReturn(applicationTypeTemplatesMap);
        when(emailTemplatesMap.get(LanguagePreference.WELSH)).thenReturn(applicationTypeTemplatesMapWelsh);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeableMocks.close();
    }

    @Test
    void returnsHseReminderForHubWhenNotSolicitorAndNotPostalRequest() {

        when(applicationTypeTemplatesMap.get(ApplicationType.PERSONAL)).thenReturn(emailTemplates);
        when(emailTemplates.getHseReminderForHub()).thenReturn("pa-hse-reminder-for-hub");

        String result = underTest.getHseReminderTemplateId(ApplicationType.PERSONAL, LanguagePreference.ENGLISH,
                CHANNEL_CHOICE_DIGITAL, NO);

        assertEquals("pa-hse-reminder-for-hub", result);
    }

    @Test
    void returnsHseReminderWhenSolicitor() {

        when(applicationTypeTemplatesMap.get(ApplicationType.SOLICITOR)).thenReturn(emailTemplates);
        when(emailTemplates.getHseReminder()).thenReturn("pp-hse-reminder");

        String result = underTest.getHseReminderTemplateId(ApplicationType.SOLICITOR, LanguagePreference.ENGLISH,
                CHANNEL_CHOICE_DIGITAL, NO);

        assertEquals("pp-hse-reminder", result);
    }

    @Test
    void returnsHseReminderWhenPostalRequest() {

        when(applicationTypeTemplatesMap.get(ApplicationType.PERSONAL)).thenReturn(emailTemplates);
        when(emailTemplates.getHseReminder()).thenReturn("pa-hse-reminder");

        String result = underTest.getHseReminderTemplateId(ApplicationType.PERSONAL, LanguagePreference.ENGLISH,
                CHANNEL_CHOICE_PAPERFORM, YES);

        assertEquals("pa-hse-reminder", result);
    }

    @Test
    void returnsHseReminderForHubWhenNotSolicitorAndNotPostalRequestWelsh() {

        when(applicationTypeTemplatesMapWelsh.get(ApplicationType.PERSONAL)).thenReturn(emailTemplates);
        when(emailTemplates.getHseReminderForHub()).thenReturn("pa-hse-reminder-for-hub-welsh");

        String result = underTest.getHseReminderTemplateId(ApplicationType.PERSONAL, LanguagePreference.WELSH,
                CHANNEL_CHOICE_DIGITAL, NO);

        assertEquals("pa-hse-reminder-for-hub-welsh", result);
    }

    @Test
    void returnsHseReminderWhenSolicitorWelsh() {

        when(applicationTypeTemplatesMapWelsh.get(ApplicationType.SOLICITOR)).thenReturn(emailTemplates);
        when(emailTemplates.getHseReminder()).thenReturn("pp-hse-reminder-welsh");

        String result = underTest.getHseReminderTemplateId(ApplicationType.SOLICITOR, LanguagePreference.WELSH,
                CHANNEL_CHOICE_DIGITAL, NO);

        assertEquals("pp-hse-reminder-welsh", result);
    }

    @Test
    void returnsHseReminderWhenPostalRequestWelsh() {

        when(applicationTypeTemplatesMapWelsh.get(ApplicationType.PERSONAL)).thenReturn(emailTemplates);
        when(emailTemplates.getHseReminder()).thenReturn("pa-hse-reminder-welsh");

        String result = underTest.getHseReminderTemplateId(ApplicationType.PERSONAL, LanguagePreference.WELSH,
                CHANNEL_CHOICE_PAPERFORM, YES);

        assertEquals("pa-hse-reminder-welsh", result);
    }
}