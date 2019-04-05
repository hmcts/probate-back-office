package uk.gov.hmcts.probate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import uk.gov.hmcts.probate.config.notifications.NotificationsProperties;
import uk.gov.service.notify.NotificationClient;

@Configuration
@EnableAsync
@EnableScheduling
public class NotificationsConfiguration {
    @Bean
    public NotificationClient notificationClient(NotificationsProperties notificationsProperties) {
        return new NotificationClient(notificationsProperties.getGovNotifyApiKey());
    }
}
