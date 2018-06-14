package uk.gov.hmcts.probate.config.notifications;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import uk.gov.hmcts.probate.model.ApplicationType;

import javax.validation.Valid;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Component
@Validated
@ConfigurationProperties("notifications.templates")
public class NotificationTemplates {

    @Valid
    private Map<ApplicationType, EmailTemplates> email;
}
