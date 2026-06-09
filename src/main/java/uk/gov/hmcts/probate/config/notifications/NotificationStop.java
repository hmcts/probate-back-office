package uk.gov.hmcts.probate.config.notifications;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import uk.gov.hmcts.probate.model.LanguagePreference;

import java.util.Map;

@Getter
@Setter
@Component
@Validated
@ConfigurationProperties("notifications.stop")
public class NotificationStop {

    @Valid
    private Map<LanguagePreference,StopReasonCode> reasons;

}
