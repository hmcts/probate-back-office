package uk.gov.hmcts.probate.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.service.FeatureToggleService;
import uk.gov.service.notify.NotificationClient;

@Slf4j
@Service
public class NotificationClientProvider {
    private final NotificationClient primaryNotificationClient;
    private final NotificationClient secondaryNotificationClient;
    private final FeatureToggleService featureToggleService;

    public NotificationClientProvider(
            @Qualifier("primaryNotificationClient") NotificationClient primaryNotificationClient,
            @Qualifier("secondaryNotificationClient") NotificationClient secondaryNotificationClient,
            FeatureToggleService featureToggleService) {

        this.primaryNotificationClient = primaryNotificationClient;
        this.secondaryNotificationClient = secondaryNotificationClient;
        this.featureToggleService = featureToggleService;
    }

    public NotificationClient getClient() {
        boolean usePrimary = featureToggleService.usePrimaryNotifyKey();
        log.debug("Using GOV.UK Notify client slot: {}", usePrimary ? "primary" : "secondary");
        return usePrimary ? primaryNotificationClient : secondaryNotificationClient;
    }
}