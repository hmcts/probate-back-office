package uk.gov.hmcts.probate.config.properties.thirdParties;

import lombok.*;
import org.springframework.boot.context.properties.*;
import org.springframework.stereotype.*;
import org.springframework.validation.annotation.*;

import java.util.*;

@Getter
@Setter
@Component
@Validated
@ConfigurationProperties
public class ThirdPartiesProperties {

    private Map<String, ThirdParty> thirdParty = new HashMap<>();
}
