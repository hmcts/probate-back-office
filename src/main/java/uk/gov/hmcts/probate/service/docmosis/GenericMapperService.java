package uk.gov.hmcts.probate.service.docmosis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
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

    private static final String PERSONALISATION_REGISTRY = "registry";
    private static final String GRANT_OF_REPRESENTATION_CASE_ID = "gorCaseReference";
    private static final String DECEASED_DATE_OF_DEATH = "deceasedDateOfDeath";
    private static final String DECEASED_DATE_OF_BIRTH = "deceasedDateOfBirth";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String NAME = "name";
    private static final String ADDRESS_LINE_1 = "addressLine1";
    private static final String ADDRESS_LINE_2 = "addressLine2";
    private static final String ADDRESS_LINE_3 = "addressLine3";
    private static final String COUNTRY = "country";
    private static final String COUNTY = "county";
    private static final String POSTCODE = "postCode";
    private static final String POST_TOWN = "postTown";
    private final RegistriesProperties registriesProperties;
    private final FileSystemResourceService fileSystemResourceService;
    private final ObjectMapper objectMapper;

    public Map<String, Object> addCaseData(CaseData caseData) {
        Map<String, Object> placeholders = objectMapper.convertValue(caseData, Map.class);
        placeholders.replace(DECEASED_DATE_OF_DEATH, DATE_FORMAT.format(caseData.getDeceasedDateOfDeath()));
        placeholders.replace(DECEASED_DATE_OF_BIRTH, DATE_FORMAT.format(caseData.getDeceasedDateOfBirth()));
        return placeholders;
    }

    public Map<String, Object> addCaseDataWithRegistryProperties(CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();
        Registry registry = registriesProperties.getRegistries().get(
            caseData.getRegistryLocation().toLowerCase());
        Map<String, Object> placeholders = addCaseData(caseData);
        Map<String, Object> registryPlaceholders = objectMapper.convertValue(registry, Map.class);

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

    public Map<String, Object> appendExecutorDetails(Map<String, Object> currentMap, String name, SolsAddress address) {
        currentMap.put(NAME, name);
        currentMap.put(ADDRESS_LINE_1, address.getAddressLine1());
        currentMap.put(ADDRESS_LINE_2, address.getAddressLine2());
        currentMap.put(ADDRESS_LINE_3, address.getAddressLine3());
        currentMap.put(POST_TOWN, address.getPostTown());
        currentMap.put(POSTCODE, address.getPostCode());
        currentMap.put(COUNTRY, address.getCountry());
        currentMap.put(COUNTY, address.getCounty());

        return currentMap;
    }

    public Map<String, Object> mappedBase64Images(Map<String, Object> files) {
        Map<String, Object> mappedImages = new HashMap<>();
        for (Map.Entry entry : files.entrySet()) {
            mappedImages.put(entry.getKey().toString(),
                "image:base64:" + fileSystemResourceService.getFileFromResourceAsString(entry.getValue().toString()));

        }
        return mappedImages;
    }
}
