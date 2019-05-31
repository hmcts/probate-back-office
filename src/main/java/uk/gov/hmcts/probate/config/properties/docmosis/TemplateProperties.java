package uk.gov.hmcts.probate.config.properties.docmosis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Component
@Validated
@ConfigurationProperties(prefix = "docmosis")
public class TemplateProperties {

    private Map<String, Template> templates = new HashMap<>();
}
