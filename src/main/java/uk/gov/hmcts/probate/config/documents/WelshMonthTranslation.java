package uk.gov.hmcts.probate.config.documents;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@Validated
@ConfigurationProperties("welsh")
public class WelshMonthTranslation {
    @Valid
    private Map<Integer, String> months;
}
