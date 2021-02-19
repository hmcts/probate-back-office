package uk.gov.hmcts.probate.service.docmosis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.service.DateFormatterService;
import uk.gov.hmcts.probate.service.ccd.CcdReferenceFormatterService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CaveatDocmosisService {
    private static final String DATE_INPUT_FORMAT = "ddMMyyyy";
    private static final String PERSONALISATION_CASE_REFERENCE = "caseReference";
    private static final String PERSONALISATION_GENERATED_DATE = "generatedDate";
    private static final String PERSONALISATION_REGISTRY = "registry";
    private static final String PERSONALISATION_PA8A_URL = "PA8AURL";
    private static final String PERSONALISATION_CAVEAT_EXPIRY_DATE = "caveatExpiryDate";
    private static final String PERSONALISATION_CAVEATOR_NAME = "caveatorName";
    private static final String PERSONALISATION_DECEASED_NAME = "deceasedName";
    private final RegistriesProperties registriesProperties;
    private final CcdReferenceFormatterService ccdReferenceFormatterService;
    private final DateFormatterService dateFormatterService;

    public Map<String, Object> caseDataAsPlaceholders(CaveatDetails caveatDetails) {

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> placeholders = mapper.convertValue(caveatDetails.getData(), Map.class);

        Registry registry = registriesProperties.getEnglish().get(
                caveatDetails.getData().getRegistryLocation().toLowerCase());
        Map<String, Object> registryPlaceholders = mapper.convertValue(registry, Map.class);

        DateFormat generatedDateFormat = new SimpleDateFormat(DATE_INPUT_FORMAT);

        placeholders.put(PERSONALISATION_CASE_REFERENCE,
            ccdReferenceFormatterService.getFormattedCaseReference(caveatDetails.getId().toString()));
        placeholders.put(PERSONALISATION_GENERATED_DATE, generatedDateFormat.format(new Date()));
        placeholders.put(PERSONALISATION_REGISTRY, registryPlaceholders);
        placeholders.put(PERSONALISATION_PA8A_URL, "www.citizensadvice.org.uk|https://www.citizensadvice.org.uk/");
        placeholders.put(PERSONALISATION_CAVEAT_EXPIRY_DATE,
            dateFormatterService.formatCaveatExpiryDate(caveatDetails.getData().getExpiryDate()));
        placeholders.put(PERSONALISATION_CAVEATOR_NAME, caveatDetails.getData().getCaveatorFullName());
        placeholders.put(PERSONALISATION_DECEASED_NAME, caveatDetails.getData().getDeceasedFullName());

        return placeholders;
    }


}
