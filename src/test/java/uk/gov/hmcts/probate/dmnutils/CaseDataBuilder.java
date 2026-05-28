package uk.gov.hmcts.probate.dmnutils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
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

    public static CaseDataBuilder defaultWaCase() {
        Map<String,Object> caseData = new HashMap<>();
        caseData.put("caseNameHmctsInternal", "Joe Blogs");
        caseData.put("isUrgent", "No");
        caseData.put("dueDate", LocalDate.now());
        String refusalOfEuLabel = "Refusal of a human rights claim";
        caseData.put("caseCategory", Map.of(
                "value", Map.of("code", "refusalOfHumanRights", "label", "Refusal of a human rights claim"),
                "list_items", List.of(Map.of("code", "refusalOfHumanRights", "label", refusalOfEuLabel))
        ));
        caseData.put("caseName", "someCaseName");
        caseData.put("region", "someRegion");
        caseData.put("roleCategory", "someRoleCategory");
        caseData.put("location", Map.of(
                "region", "some other region",
                "baseLocation", "some other location"
        ));
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
