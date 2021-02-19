package uk.gov.hmcts.probate.config.properties.registries;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class RegistryCountry {

    private Map<String, Registry> english = new HashMap<>();
    private Map<String, Registry> welsh = new HashMap<>();
}
