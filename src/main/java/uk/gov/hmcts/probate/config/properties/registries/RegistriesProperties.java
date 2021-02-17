package uk.gov.hmcts.probate.config.properties.registries;

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
@ConfigurationProperties("registry")
public class RegistriesProperties {

    private Map<String, Registry> english = new HashMap();
    private Map<String, Registry> welsh = new HashMap(); //call registry properties with key being english or welsh
}