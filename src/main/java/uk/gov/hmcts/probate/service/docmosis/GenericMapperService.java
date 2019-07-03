package uk.gov.hmcts.probate.service.docmosis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.FileSystemResourceService;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenericMapperService {

    private ObjectMapper mapper;
    private final RegistriesProperties registriesProperties;
    private final FileSystemResourceService fileSystemResourceService;

    private static final String PERSONALISATION_REGISTRY = "registry";
    private static final String GRANT_OF_REPRESENTATION_CASE_ID = "gorCaseReference";
    private static final String DECEASED_DATE_OF_DEATH = "deceasedDateOfDeath";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Map<String, Object> addCaseData(CaseData caseData) {
        mapper = new ObjectMapper();
        Map<String, Object> placeholders = mapper.convertValue(caseData, Map.class);
        placeholders.replace(DECEASED_DATE_OF_DEATH, DATE_FORMAT.format(caseData.getDeceasedDateOfDeath()));
        return placeholders;
    }

    public Map<String, Object> addCaseDataWithRegistryProperties(CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();
        Registry registry = registriesProperties.getRegistries().get(
                caseData.getRegistryLocation().toLowerCase());
        Map<String, Object> placeholders = addCaseData(caseData);
        Map<String, Object> registryPlaceholders = mapper.convertValue(registry, Map.class);

        placeholders.put(PERSONALISATION_REGISTRY, registryPlaceholders);
        placeholders.put(GRANT_OF_REPRESENTATION_CASE_ID, caseDetails.getId().toString());
        return placeholders;
    }

    public Map<String, Object> addCaseDataWithImages(Map<String, Object> images, CaseDetails caseDetails) {
        Map<String, Object> placeholders = addCaseDataWithRegistryProperties(caseDetails);
        Map<String, Object> mappedImages = mappedBase64Images(images);
        placeholders.putAll(mappedImages);
        return placeholders;
    }

    private Map<String, Object> mappedBase64Images(Map<String, Object> files) {
        Map<String, Object> mappedImages = new HashMap<>();
        for (Map.Entry entry : files.entrySet()) {
            mappedImages.put(entry.getKey().toString(),
                    "image:base64:" + fileSystemResourceService.getFileFromResourceAsString(entry.getValue().toString()));

        }
        return mappedImages;
    }
}
