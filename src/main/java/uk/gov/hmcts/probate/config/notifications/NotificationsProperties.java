package uk.gov.hmcts.probate.config.notifications;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Getter
@Setter
@Component
@Validated
@ConfigurationProperties(prefix = "notifications")
public class NotificationsProperties {

    @NotBlank
    private String govNotifyApiKey;

    @Valid
    private NotificationTemplates templates;
}
