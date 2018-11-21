package uk.gov.hmcts.probate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import uk.gov.hmcts.probate.config.notifications.NotificationsProperties;
import uk.gov.service.notify.NotificationClient;

@Configuration
@EnableAsync
public class DuplicateNotificationsConfiguration {
    @Bean
    public NotificationClient notificationClient(NotificationsProperties notificationsProperties) {
        return new NotificationClient(notificationsProperties.getGovNotifyApiKey());
    }
}
