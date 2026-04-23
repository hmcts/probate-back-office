package uk.gov.hmcts.probate.service.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.service.FeatureToggleService;
import uk.gov.service.notify.NotificationClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class NotificationClientProviderTest {

    @Mock
    private NotificationClient primaryNotificationClient;

    @Mock
    private NotificationClient secondaryNotificationClient;

    @Mock
    private FeatureToggleService featureToggleService;

    private NotificationClientProvider provider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        provider = new NotificationClientProvider(
                primaryNotificationClient,
                secondaryNotificationClient,
                featureToggleService
        );
    }

    @Test
    void shouldReturnPrimaryClientWhenToggleIsTrue() {
        when(featureToggleService.usePrimaryNotifyKey()).thenReturn(true);

        NotificationClient result = provider.getClient();

        assertThat(result).isSameAs(primaryNotificationClient);
        verify(featureToggleService).usePrimaryNotifyKey();
    }

    @Test
    void shouldReturnSecondaryClientWhenToggleIsFalse() {
        when(featureToggleService.usePrimaryNotifyKey()).thenReturn(false);

        NotificationClient result = provider.getClient();

        assertThat(result).isSameAs(secondaryNotificationClient);
        verify(featureToggleService).usePrimaryNotifyKey();
    }
}