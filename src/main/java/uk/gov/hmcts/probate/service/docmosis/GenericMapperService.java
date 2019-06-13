package uk.gov.hmcts.probate.service.docmosis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenericMapperService {

    private ObjectMapper mapper;
    private final RegistriesProperties registriesProperties;

    private static final String PERSONALISATION_REGISTRY = "registry";

    public Map<String, Object> addCaseData(CaseData caseData) {
        mapper = new ObjectMapper();
        return mapper.convertValue(caseData, Map.class);
    }

    public Map<String, Object> addCaseDataWithRegistryProperties(CaseData caseData) {
        Registry registry = registriesProperties.getRegistries().get(
                caseData.getRegistryLocation().toLowerCase());
        Map<String, Object> placeholders = addCaseData(caseData);
        Map<String, Object> registryPlaceholders = mapper.convertValue(registry, Map.class);

        placeholders.put(PERSONALISATION_REGISTRY, registryPlaceholders);
        return placeholders;
    }

    public Map<String, Object> caseDataWithImages(Map<String, Object> images, CaseDetails caseDetails) {
        Map<String, Object> placeholders = addCaseDataWithRegistryProperties(caseDetails.getData());
        Map<String, Object> mappedImages = mappedBase64Images(images);
        placeholders.putAll(mappedImages);
        return placeholders;
    }

    private Map<String, Object> mappedBase64Images(Map<String, Object> files) {
        BufferedReader br = null;
        Map<String, Object> mappedImages = new HashMap<>();
        for (Map.Entry entry : files.entrySet()) {
            File file = new File(entry.getValue().toString());
            try {
                br = new BufferedReader(new FileReader(file));
            } catch (FileNotFoundException e) {
                log.error("Could not find file {}. {}", entry.getValue(), e.getMessage());
            }
            try {
                mappedImages.put(entry.getKey().toString(), "image:base64:" + br.readLine());
            } catch (IOException e) {
                log.error("Could not read file. {}", e.getMessage());
            }
        }
        return mappedImages;
    }
}
