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
import uk.gov.hmcts.probate.service.FeatureToggleService;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.CHANNEL_CHOICE_DIGITAL;
import static uk.gov.hmcts.probate.model.Constants.CHANNEL_CHOICE_PAPERFORM;
import static uk.gov.hmcts.probate.model.State.CAVEAT_RAISED;
import static uk.gov.hmcts.probate.model.State.CAVEAT_RAISED_SOLS;

@ExtendWith(SpringExtension.class)
class TemplateServiceTest {

    @Mock
    NotificationTemplates notificationTemplates;

    @Mock
    FeatureToggleService featureToggleService;

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
        underTest = new TemplateService(notificationTemplates, featureToggleService);
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

    @Test
    void returnsPostGrantIssuedWhenPersonalAndEnglish() {
        final String expected = "personal-pgi-english";
        when(applicationTypeTemplatesMap.get(ApplicationType.PERSONAL))
                .thenReturn(emailTemplates);
        when(emailTemplates.getPostGrantIssuedNotification())
                .thenReturn(expected);

        final String actual = underTest.getPostGrantIssueTemplateId(
                LanguagePreference.ENGLISH,
                ApplicationType.PERSONAL);

        assertThat(actual, sameInstance(expected));
    }
  
    @Test
    void returnsRegistrarEscWhenPersonalAndEnglish() {
        final String expected = "personal-registrar-esc-english";
        when(applicationTypeTemplatesMap.get(ApplicationType.PERSONAL))
                .thenReturn(emailTemplates);
        when(emailTemplates.getRegistrarEscalationNotification())
                .thenReturn(expected);

        final String actual = underTest.getRegistrarEscalationNotification(
                ApplicationType.PERSONAL,
                LanguagePreference.ENGLISH);

        assertThat(actual, sameInstance(expected));
    }

    @Test
    void returnsRegistrarEscFailedWhenPersonalAndEnglish() {
        final String expected = "personal-registrar-esc-failed-english";
        when(applicationTypeTemplatesMap.get(ApplicationType.PERSONAL))
                .thenReturn(emailTemplates);
        when(emailTemplates.getRegistrarEscalationNotificationFailed())
                .thenReturn(expected);

        final String actual = underTest.getRegistrarEscalationNotificationFailed(
                ApplicationType.PERSONAL,
                LanguagePreference.ENGLISH);

        assertThat(actual, sameInstance(expected));
    }

    @Test
    void returnsFirstRedecReminderPersonalWelsh() {

        when(applicationTypeTemplatesMapWelsh.get(ApplicationType.PERSONAL)).thenReturn(emailTemplates);
        when(emailTemplates.getFirstRedecReminder()).thenReturn("pa-first-redec-reminder-welsh");

        String result = underTest.getRedecReminderTemplateId(ApplicationType.PERSONAL, LanguagePreference.WELSH,
                true);

        assertEquals("pa-first-redec-reminder-welsh", result);
    }

    @Test
    void returnsFirstRedecReminderPersonalEnglish() {

        when(applicationTypeTemplatesMap.get(ApplicationType.PERSONAL)).thenReturn(emailTemplates);
        when(emailTemplates.getFirstRedecReminder()).thenReturn("pa-first-redec-reminder");

        String result = underTest.getRedecReminderTemplateId(ApplicationType.PERSONAL, LanguagePreference.ENGLISH,
                true);

        assertEquals("pa-first-redec-reminder", result);
    }

    @Test
    void returnsSecondRedecReminderPersonalWelsh() {

        when(applicationTypeTemplatesMapWelsh.get(ApplicationType.PERSONAL)).thenReturn(emailTemplates);
        when(emailTemplates.getSecondRedecReminder()).thenReturn("pa-second-redec-reminder-welsh");

        String result = underTest.getRedecReminderTemplateId(ApplicationType.PERSONAL, LanguagePreference.WELSH,
                false);

        assertEquals("pa-second-redec-reminder-welsh", result);
    }

    @Test
    void returnsSecondRedecReminderPersonalEnglish() {

        when(applicationTypeTemplatesMap.get(ApplicationType.PERSONAL)).thenReturn(emailTemplates);
        when(emailTemplates.getSecondRedecReminder()).thenReturn("pa-second-redec-reminder");

        String result = underTest.getRedecReminderTemplateId(ApplicationType.PERSONAL, LanguagePreference.ENGLISH,
                false);

        assertEquals("pa-second-redec-reminder", result);
    }

    @Test
    void returnCaveatRaisedTemplateWhenFeatureToggleEnabled() {
        when(featureToggleService.isNewFee2026Enabled()).thenReturn(true);
        when(applicationTypeTemplatesMap.get(ApplicationType.PERSONAL)).thenReturn(emailTemplates);
        when(emailTemplates.getCaveatRaised()).thenReturn("pa-caveat-raised");

        String result = underTest.getTemplateId(CAVEAT_RAISED, ApplicationType.PERSONAL,"",LanguagePreference.ENGLISH);

        assertEquals("pa-caveat-raised", result);
    }

    @Test
    void returnCaveatRaisedTemplateWhenFeatureToggleDisEnabled() {
        when(featureToggleService.isNewFee2026Enabled()).thenReturn(false);
        when(applicationTypeTemplatesMap.get(ApplicationType.PERSONAL)).thenReturn(emailTemplates);
        when(emailTemplates.getCaveatRaisedOld()).thenReturn("pa-caveat-raised-old");

        String result = underTest.getTemplateId(CAVEAT_RAISED, ApplicationType.PERSONAL,"",LanguagePreference.ENGLISH);

        assertEquals("pa-caveat-raised-old", result);
    }

    @Test
    void returnSolsCaveatRaisedTemplateWhenFeatureToggleEnabled() {
        when(featureToggleService.isNewFee2026Enabled()).thenReturn(true);
        when(applicationTypeTemplatesMap.get(ApplicationType.SOLICITOR)).thenReturn(emailTemplates);
        when(emailTemplates.getCaveatRaisedSols()).thenReturn("caveat-raised-sols");

        String result = underTest.getTemplateId(CAVEAT_RAISED_SOLS, ApplicationType.SOLICITOR,"",
                LanguagePreference.ENGLISH);

        assertEquals("caveat-raised-sols", result);
    }

    @Test
    void returnSolsCaveatRaisedTemplateWhenFeatureToggleDisEnabled() {
        when(featureToggleService.isNewFee2026Enabled()).thenReturn(false);
        when(applicationTypeTemplatesMap.get(ApplicationType.SOLICITOR)).thenReturn(emailTemplates);
        when(emailTemplates.getCaveatRaisedSolsOld()).thenReturn("caveat-raised-sols-old");

        String result = underTest.getTemplateId(CAVEAT_RAISED_SOLS, ApplicationType.SOLICITOR,"",
                LanguagePreference.ENGLISH);

        assertEquals("caveat-raised-sols-old", result);
    }
}
