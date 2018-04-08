package uk.gov.hmcts.probate.service.pdf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.controller.exception.PDFMissingPayloadException;
import uk.gov.hmcts.probate.model.pdf.PDFServiceTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@ConfigurationProperties(prefix = "template")
public class PDFPayloadValidator {

    private final Map<String, String> properties = new HashMap<String, String>();

    public boolean validatePayload(String pdfGenerationData, PDFServiceTemplate pdfServiceTemplate) {
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(pdfGenerationData);
            Map dataMap = getDataMap(jsonNode);

            List<String> missing = getMissingPayloadKeys(dataMap, pdfServiceTemplate);
            if (!missing.isEmpty()) {
                throw new PDFMissingPayloadException(missing, pdfServiceTemplate);
            }

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    private List<String> getMissingPayloadKeys(Map<String, Object> payloadMap, PDFServiceTemplate pdfServiceTemplate) {
        return payloadMap.entrySet().stream()
            .filter(entry -> ((ValueNode) entry.getValue()).asText().isEmpty())
            .map(entry -> getFieldName(entry.getKey(), pdfServiceTemplate.getHtmlFileName()))
            .collect(Collectors.toList());
    }

    private String getFieldName(String key, String templateName) {
        return key.replaceFirst(templateName + ".", "");
    }

    private Map<String, Object> getDataMap(JsonNode submitData) {
        return properties
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, prop -> submitData.at(prop.getValue())));
    }

}
