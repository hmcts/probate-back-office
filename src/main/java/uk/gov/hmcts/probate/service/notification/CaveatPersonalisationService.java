package uk.gov.hmcts.probate.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.AddressFormatterService;
import uk.gov.hmcts.probate.service.CaveatQueryService;
import uk.gov.hmcts.probate.service.DateFormatterService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class CaveatPersonalisationService {

    private final DateFormatterService dateFormatterService;
    private final AddressFormatterService addressFormatterService;
    private final CaveatQueryService caveatQueryService;

    private static final String PERSONALISATION_APPLICANT_NAME = "applicant_name";
    private static final String PERSONALISATION_DECEASED_NAME = "deceased_name";
    private static final String PERSONALISATION_REGISTRY_NAME = "registry_name";
    private static final String PERSONALISATION_REGISTRY_PHONE = "registry_phone";
    private static final String PERSONALISATION_CCD_REFERENCE = "ccd_reference";
    private static final String PERSONALISATION_SOLICITOR_REFERENCE = "solicitor_reference";
    private static final String PERSONALISATION_CAVEAT_EXPIRY_DATE = "caveat_expiry_date";
    private static final String PERSONALISATION_MESSAGE_CONTENT = "message_content";
    private static final String PERSONALISATION_DATE_CAVEAT_ENTERED = "date_caveat_entered";
    private static final String PERSONALISATION_CAVEATOR_NAME = "caveator_name";
    private static final String PERSONALISATION_CAVEATOR_ADDRESS = "caveator_address";


    public Map<String, Object> getCaveatStopPersonalisation(Map<String, Object> personalisation, CaseData caseData) {

        CaveatData caveatData = caveatQueryService.findCaveatById(CaseType.CAVEAT, caseData.getBoCaseStopCaveatId());

        if (caveatData != null) {
            personalisation.put(PERSONALISATION_DATE_CAVEAT_ENTERED,
                    dateFormatterService.formatDate(caveatData.getApplicationSubmittedDate()));
            personalisation.put(PERSONALISATION_CAVEATOR_NAME, caveatData.getCaveatorFullName());
            personalisation.put(PERSONALISATION_CAVEATOR_ADDRESS,
                    addressFormatterService.formatAddress(caveatData.getCaveatorAddress()));
            personalisation.put(PERSONALISATION_CAVEAT_EXPIRY_DATE,
                    dateFormatterService.formatCaveatExpiryDate(caveatData.getExpiryDate()));
        }
        if (caseData.getApplicationType().equals(ApplicationType.SOLICITOR)) {
            personalisation.replace(PERSONALISATION_APPLICANT_NAME, caseData.getSolsSOTName());
        }
        return personalisation;
    }

    public Map<String, String> getCaveatPersonalisation(CaveatDetails caveatDetails, Registry registry) {
        CaveatData caveatData = caveatDetails.getData();

        HashMap<String, String> personalisation = new HashMap<>();

        personalisation.put(PERSONALISATION_APPLICANT_NAME, caveatData.getCaveatorFullName());
        personalisation.put(PERSONALISATION_DECEASED_NAME, caveatData.getDeceasedFullName());
        personalisation.put(PERSONALISATION_CCD_REFERENCE, caveatDetails.getId().toString());
        personalisation.put(PERSONALISATION_MESSAGE_CONTENT, caveatData.getMessageContent());
        personalisation.put(PERSONALISATION_REGISTRY_NAME, registry.getName());
        personalisation.put(PERSONALISATION_REGISTRY_PHONE, registry.getPhone());
        personalisation.put(PERSONALISATION_CAVEAT_EXPIRY_DATE,
                dateFormatterService.formatCaveatExpiryDate(caveatData.getExpiryDate()));

        return personalisation;
    }

    public Map<String, String> getSolsCaveatPersonalisation(CaveatDetails caveatDetails, Registry registry) {
        CaveatData caveatData = caveatDetails.getData();

        HashMap<String, String> personalisation = new HashMap<>();

        personalisation.put(PERSONALISATION_APPLICANT_NAME, "Sir/Madam");
        personalisation.put(PERSONALISATION_DECEASED_NAME, caveatData.getDeceasedFullName());
        personalisation.put(PERSONALISATION_SOLICITOR_REFERENCE, caveatData.getSolsSolicitorAppReference());
        personalisation.put(PERSONALISATION_CCD_REFERENCE, caveatDetails.getId().toString());
        personalisation.put(PERSONALISATION_MESSAGE_CONTENT, caveatData.getMessageContent());
        personalisation.put(PERSONALISATION_REGISTRY_NAME, registry.getName());
        personalisation.put(PERSONALISATION_REGISTRY_PHONE, registry.getPhone());
        personalisation.put(PERSONALISATION_CAVEAT_EXPIRY_DATE,
                dateFormatterService.formatCaveatExpiryDate(caveatData.getExpiryDate()));

        return personalisation;
    }


}
