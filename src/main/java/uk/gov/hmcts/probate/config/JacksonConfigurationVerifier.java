package uk.gov.hmcts.probate.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class JacksonConfigurationVerifier {

    public JacksonConfigurationVerifier(ObjectMapper objectMapper) {
        if (objectMapper.getFactory().isEnabled(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT)) {
            throw new IllegalStateException("Jackson ObjectMapper is configured with AUTO_CLOSE_JSON_CONTENT enabled. "
                    + "This can cause issues with streaming JSON responses and must be disabled.");
        }
    }
}