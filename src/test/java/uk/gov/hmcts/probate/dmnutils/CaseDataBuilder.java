package uk.gov.hmcts.probate.dmnutils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.util.ResourceUtils.getFile;

public class CaseDataBuilder {

    Map<String,Object> caseData;

    private static final MapTypeReference MAP_TYPE = new MapTypeReference();

    private CaseDataBuilder(Map<String,Object> caseData) {
        this.caseData = caseData;
    }

    public static CaseDataBuilder defaultCase() {
        Map<String,Object> caseData = new HashMap<>();
        caseData.put("caseNameHmctsInternal", "Joe Blogs");
        caseData.put("isUrgent", "No");
        caseData.put("dueDate", LocalDate.now());
        return new CaseDataBuilder(caseData);
    }

    public static CaseDataBuilder customCase(final String resourcePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        Map<String,Object> caseData = objectMapper.readValue(getFile(resourcePath), MAP_TYPE);
        return new CaseDataBuilder(caseData);
    }

    public Map<String,Object> build() {
        return caseData;
    }

    public CaseDataBuilder isUrgent() {
        caseData.put("isUrgent", "Yes");
        return this;
    }

    private static class MapTypeReference extends TypeReference<Map<String, Object>> {
    }
}
