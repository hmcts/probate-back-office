package uk.gov.hmcts.probate.config.properties.registries;

import lombok.Data;

@Getter
@Setter
@Component
@Validated
@ConfigurationProperties("registry")
public class RegistryCountry {

    private Map<String, Registry> english = new HashMap();
    private Map<String, Registry> welsh = new HashMap();
}
