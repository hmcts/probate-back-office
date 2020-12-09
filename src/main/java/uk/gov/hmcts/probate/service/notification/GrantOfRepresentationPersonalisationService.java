package uk.gov.hmcts.probate.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.service.template.pdf.LocalDateToWelshStringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.gov.hmcts.probate.model.Constants.DOC_SUBTYPE_WILL;

@Slf4j
@RequiredArgsConstructor
@Service
public class GrantOfRepresentationPersonalisationService {
    private final LocalDateToWelshStringConverter localDateToWelshStringConverter;

    private static final DateTimeFormatter EXCELA_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter EXCELA_CONTENT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String PERSONALISATION_APPLICANT_NAME = "applicant_name";
    private static final String PERSONALISATION_DECEASED_NAME = "deceased_name";
    private static final String PERSONALISATION_SOLICITOR_NAME = "solicitor_name";
    private static final String PERSONALISATION_SOLICITOR_REFERENCE = "solicitor_reference";
    private static final String PERSONALISATION_REGISTRY_NAME = "registry_name";
    private static final String PERSONALISATION_REGISTRY_PHONE = "registry_phone";
    private static final String PERSONALISATION_CASE_STOP_DETAILS = "case-stop-details";
    private static final String PERSONALISATION_CASE_STOP_DETAILS_DEC = "boStopDetailsDeclarationParagraph";
    private static final String PERSONALISATION_CAVEAT_CASE_ID = "caveat_case_id";
    private static final String PERSONALISATION_DECEASED_DOD = "deceased_dod";
    private static final String PERSONALISATION_CCD_REFERENCE = "ccd_reference";
    private static final String PERSONALISATION_EXCELA_NAME = "excelaName";
    private static final String PERSONALISATION_CASE_DATA = "caseData";
    private static final String PERSONALISATION_ADDRESSEE = "addressee";
    private static final String PERSONALISATION_WELSH_DECEASED_DATE_OF_DEATH = "welsh_deceased_date_of_death";

    public Map<String, Object> getPersonalisation(CaseDetails caseDetails, Registry registry) {
        
        return getPersonalisationFromCaseData(caseDetails.getData(), caseDetails.getId(), registry);
    }

    public Map<String, Object> getPersonalisation(ReturnedCaseDetails caseDetails, Registry registry) {
 
        return getPersonalisationFromCaseData(caseDetails.getData(), caseDetails.getId(), registry);
    }

    public Map<String, String> getExcelaPersonalisation(List<ReturnedCaseDetails> cases) {
        HashMap<String, String> personalisation = new HashMap<>();

        StringBuilder data = getBuiltData(cases);

        personalisation.put(PERSONALISATION_EXCELA_NAME, LocalDateTime.now().format(EXCELA_DATE) + "will");
        personalisation.put(PERSONALISATION_CASE_DATA, data.toString());

        return personalisation;
    }

    public Map<String, Object> addSingleAddressee(Map<String, Object> currentMap,String addressee) {
        currentMap.put(PERSONALISATION_ADDRESSEE, addressee);
        return currentMap;
    }

    private Map<String, Object> getPersonalisationFromCaseData(CaseData caseData, Long caseId, Registry registry) {
        HashMap<String, Object> personalisation = new HashMap<>();
        personalisation.put(PERSONALISATION_APPLICANT_NAME, caseData.getPrimaryApplicantFullName());
        personalisation.put(PERSONALISATION_DECEASED_NAME, caseData.getDeceasedFullName());
        personalisation.put(PERSONALISATION_SOLICITOR_NAME, caseData.getSolsSOTName());
        personalisation.put(PERSONALISATION_SOLICITOR_REFERENCE, caseData.getSolsSolicitorAppReference());
        personalisation.put(PERSONALISATION_REGISTRY_NAME, registry.getName());
        personalisation.put(PERSONALISATION_REGISTRY_PHONE, registry.getPhone());
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS, caseData.getBoStopDetails());
        personalisation.put(PERSONALISATION_CAVEAT_CASE_ID, caseData.getBoCaseStopCaveatId());
        personalisation.put(PERSONALISATION_DECEASED_DOD, caseData.getDeceasedDateOfDeathFormatted());
        personalisation.put(PERSONALISATION_CCD_REFERENCE, caseId.toString());
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS_DEC, caseData.getBoStopDetailsDeclarationParagraph());
        personalisation.put(PERSONALISATION_WELSH_DECEASED_DATE_OF_DEATH, localDateToWelshStringConverter.convert(caseData.getDeceasedDateOfDeath()));

        return personalisation;
    }

    private StringBuilder getBuiltData(List<ReturnedCaseDetails> cases) {
        StringBuilder data = new StringBuilder();

        for (ReturnedCaseDetails currentCase : cases) {
            data.append(getWillReferenceNumber(currentCase.getData()));
            data.append(", ");
            data.append(currentCase.getData().getDeceasedForenames());
            data.append(" ");
            data.append(", ");
            data.append(currentCase.getData().getDeceasedSurname());
            data.append(", ");
            data.append(EXCELA_CONTENT_DATE.format(currentCase.getData().getDeceasedDateOfBirth()));
            data.append(", ");
            data.append(EXCELA_CONTENT_DATE.format(LocalDate.parse(currentCase.getData().getGrantIssuedDate())));
            data.append(", ");
            data.append(currentCase.getId().toString());
            data.append(", ");
            data.append(currentCase.getData().getRegistryLocation());
            data.append("\n");
        }
        return data;
    }

    private String getWillReferenceNumber(CaseData data) {
        for (CollectionMember<ScannedDocument> document : data.getScannedDocuments()) {
            if (document.getValue().getSubtype() != null && document.getValue().getSubtype().equalsIgnoreCase(DOC_SUBTYPE_WILL)) {
                return document.getValue().getControlNumber();
            }
        }
        return "";
    }
}
