package uk.gov.hmcts.probate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import uk.gov.hmcts.probate.config.notifications.NotificationsProperties;
import uk.gov.service.notify.NotificationClient;

@Configuration
@EnableScheduling
@EnableAsync
public class NotificationsConfiguration {
    @Bean
    public NotificationClient primaryNotificationClient(NotificationsProperties properties) {
        return new NotificationClient(properties.getGovNotifyApiKeyPrimary());
    }

    @Bean
    public NotificationClient secondaryNotificationClient(NotificationsProperties properties) {
        return new NotificationClient(properties.getGovNotifyApiKeySecondary());
    }
}
