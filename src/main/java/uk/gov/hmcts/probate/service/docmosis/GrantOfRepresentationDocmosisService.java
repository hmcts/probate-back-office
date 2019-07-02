package uk.gov.hmcts.probate.service.docmosis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.AddressFormatterService;
import uk.gov.hmcts.probate.service.CaveatQueryService;
import uk.gov.hmcts.probate.service.ccd.CcdReferenceFormatterService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GrantOfRepresentationDocmosisService {
    private static final String DATE_INPUT_FORMAT = "ddMMyyyy";
    private final RegistriesProperties registriesProperties;
    private final CcdReferenceFormatterService ccdReferenceFormatterService;
    private final CaveatQueryService caveatQueryService;
    private final AddressFormatterService addressFormatterService;

    private static final String PERSONALISATION_DATE_CAVEAT_ENTERED = "dateCaveatEntered";
    private static final String PERSONALISATION_CAVEATOR_NAME = "caveatorName";
    private static final String PERSONALISATION_CAVEAT_REFERENCE = "caveatReference";
    private static final String PERSONALISATION_CAVEATOR_ADDRESS = "caveatorAddress";
    private static final String PERSONALISATION_CASE_REFERENCE = "caseReference";
    private static final String PERSONALISATION_GENERATED_DATE = "generatedDate";
    private static final String PERSONALISATION_REGISTRY = "registry";
    private static final String PERSONALISATION_PA8AURL = "PA8AURL";
    private static final String PERSONALISATION_PA8BURL = "PA8BURL";

    public Map<String, Object> caseDataAsPlaceholders(CaseDetails caseDetails) {

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> placeholders = mapper.convertValue(caseDetails.getData(), Map.class);

        Registry registry = registriesProperties.getRegistries().get(
                caseDetails.getData().getRegistryLocation().toLowerCase());
        Map<String, Object> registryPlaceholders = mapper.convertValue(registry, Map.class);

        DateFormat generatedDateFormat = new SimpleDateFormat(DATE_INPUT_FORMAT);

        CaveatData caveatData = caveatQueryService.findCaveatById(CaseType.CAVEAT, caseDetails.getData().getBoCaseStopCaveatId());

        placeholders.put(PERSONALISATION_CASE_REFERENCE,
                ccdReferenceFormatterService.getFormattedCaseReference(caseDetails.getId().toString()));
        placeholders.put(PERSONALISATION_GENERATED_DATE, generatedDateFormat.format(new Date()));
        placeholders.put(PERSONALISATION_REGISTRY, registryPlaceholders);
        placeholders.put(PERSONALISATION_PA8AURL, "https://www.gov.uk/inherits-someone-dies-without-will|https://www.gov.uk/inherits-someone-dies-without-will");
        placeholders.put(PERSONALISATION_PA8BURL, "https://www.citizensadvice.org.uk|https://www.citizensadvice.org.uk/");
        placeholders.put(PERSONALISATION_CAVEATOR_NAME, caveatData.getCaveatorFullName());
        placeholders.put(PERSONALISATION_CAVEATOR_ADDRESS, addressFormatterService.formatAddress(caveatData.getCaveatorAddress()));
        placeholders.put(PERSONALISATION_CAVEAT_REFERENCE,
                ccdReferenceFormatterService.getFormattedCaseReference(caseDetails.getData().getBoCaseStopCaveatId()));
        return placeholders;
    }


}
