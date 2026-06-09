package uk.gov.hmcts.probate.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
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

import static uk.gov.hmcts.probate.model.Constants.DOC_TYPE_WILL;
import static uk.gov.hmcts.probate.model.Constants.DOC_SUBTYPE_WILL;
import static uk.gov.hmcts.probate.model.Constants.DOC_TYPE_OTHER;

@Slf4j
@RequiredArgsConstructor
@Service
public class GrantOfRepresentationPersonalisationService {
    private static final DateTimeFormatter EXELA_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter EXELA_CONTENT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String PERSONALISATION_APPLICANT_NAME = "applicant_name";
    private static final String PERSONALISATION_DECEASED_NAME = "deceased_name";
    private static final String PERSONALISATION_SOLICITOR_NAME = "solicitor_name";
    private static final String PERSONALISATION_SOLICITOR_SOT_FORENAMES = "solicitor_sot_forenames";
    private static final String PERSONALISATION_SOLICITOR_SOT_SURNAME = "solicitor_sot_surname";
    private static final String PERSONALISATION_SOLICITOR_REFERENCE = "solicitor_reference";
    private static final String PERSONALISATION_REGISTRY_NAME = "registry_name";
    private static final String PERSONALISATION_REGISTRY_PHONE = "registry_phone";
    private static final String PERSONALISATION_CASE_STOP_DETAILS = "case-stop-details";
    private static final String PERSONALISATION_CASE_STOP_DETAILS_DEC = "boStopDetailsDeclarationParagraph";
    private static final String PERSONALISATION_CAVEAT_CASE_ID = "caveat_case_id";
    private static final String PERSONALISATION_DECEASED_DOD = "deceased_dod";
    private static final String PERSONALISATION_CCD_REFERENCE = "ccd_reference";
    private static final String PERSONALISATION_EXELA_NAME = "exelaName";
    private static final String PERSONALISATION_CASE_DATA = "caseData";
    private static final String PERSONALISATION_ADDRESSEE = "addressee";
    private static final String PERSONALISATION_WELSH_DECEASED_DATE_OF_DEATH = "welsh_deceased_date_of_death";
    private static final String PERSONALISATION_NOC_SUBMITTED_DATE = "noc_date";
    private static final String PERSONALISATION_OLD_SOLICITOR_NAME = "old_solicitor_name";
    private static final String PERSONALISATION_DRAFT_NAME = "draftName";
    private static final String PERSONALISATION_CASE_TYPE = "caseType";
    private static final String SUBJECT = "Draft cases with payment success extract from :fromDate to :toDate";
    private final LocalDateToWelshStringConverter localDateToWelshStringConverter;

    public Map<String, Object> getPersonalisation(CaseDetails caseDetails, Registry registry) {

        return getPersonalisationFromCaseData(caseDetails.getData(), caseDetails.getId(), registry);
    }

    public Map<String, Object> getPersonalisation(ReturnedCaseDetails caseDetails, Registry registry) {

        return getPersonalisationFromCaseData(caseDetails.getData(), caseDetails.getId(), registry);
    }

    public Map<String, String> getExelaPersonalisation(List<ReturnedCaseDetails> cases) {
        HashMap<String, String> personalisation = new HashMap<>();

        StringBuilder data = getExelaBuiltData(cases);

        personalisation.put(PERSONALISATION_EXELA_NAME, LocalDateTime.now().format(EXELA_DATE) + "will");
        personalisation.put(PERSONALISATION_CASE_DATA, data.toString());

        return personalisation;
    }

    public Map<String, String> getStopResponseReceivedPersonalisation(Long id, String addresseeName) {
        HashMap<String, String> personalisation = new HashMap<>();
        personalisation.put(PERSONALISATION_CCD_REFERENCE, id.toString());
        personalisation.put(PERSONALISATION_APPLICANT_NAME, addresseeName);
        return personalisation;

    }

    public Map<String, Object> getSealedAndCertifiedPersonalisation(Long caseId, String deceasedName) {
        HashMap<String, Object> personalisation = new HashMap<>();
        personalisation.put(PERSONALISATION_CCD_REFERENCE, caseId.toString());
        personalisation.put(PERSONALISATION_DECEASED_NAME, deceasedName);

        return personalisation;
    }

    public Map<String, Object> getNocPersonalisation(Long caseId, String solicitorName, String deceasedName) {
        HashMap<String, Object> personalisation = new HashMap<>();

        personalisation.put(PERSONALISATION_OLD_SOLICITOR_NAME, solicitorName);
        personalisation.put(PERSONALISATION_CCD_REFERENCE, caseId.toString());
        personalisation.put(PERSONALISATION_NOC_SUBMITTED_DATE, EXELA_CONTENT_DATE.format(LocalDateTime.now()));
        personalisation.put(PERSONALISATION_DECEASED_NAME, deceasedName);

        return personalisation;
    }

    public Map<String, Object> getDraftCaseWithPaymentPersonalisation(
            List<uk.gov.hmcts.reform.ccd.client.model.CaseDetails> cases,
            String fromDate, String toDate, CcdCaseType ccdCaseType) {
        HashMap<String, Object> personalisation = new HashMap<>();

        StringBuilder data = getDraftCasesBuiltData(cases);

        personalisation.put(PERSONALISATION_DRAFT_NAME, getSubject(fromDate, toDate));
        personalisation.put(PERSONALISATION_CASE_TYPE, ccdCaseType.getName().equals(CcdCaseType.CAVEAT.getName())
                ? "Caveat" : "Grant of Representation");
        personalisation.put(PERSONALISATION_CASE_DATA, data.toString());

        return personalisation;
    }

    private String getSubject(String fromDate, String toDate) {
        return SUBJECT.replace(":fromDate", fromDate)
                .replace(":toDate", toDate);
    }

    public Map<String, Object> addSingleAddressee(Map<String, Object> currentMap, String addressee) {
        currentMap.put(PERSONALISATION_ADDRESSEE, addressee);
        return currentMap;
    }

    private Map<String, Object> getPersonalisationFromCaseData(CaseData caseData, Long caseId, Registry registry) {
        HashMap<String, Object> personalisation = new HashMap<>();
        personalisation.put(PERSONALISATION_APPLICANT_NAME, caseData.getPrimaryApplicantFullName());
        personalisation.put(PERSONALISATION_DECEASED_NAME, caseData.getDeceasedFullName());
        personalisation.put(PERSONALISATION_SOLICITOR_NAME, caseData.getSolsSOTName());
        personalisation.put(PERSONALISATION_SOLICITOR_SOT_FORENAMES, caseData.getSolsSOTForenames());
        personalisation.put(PERSONALISATION_SOLICITOR_SOT_SURNAME, caseData.getSolsSOTSurname());
        personalisation.put(PERSONALISATION_SOLICITOR_REFERENCE, caseData.getSolsSolicitorAppReference());
        personalisation.put(PERSONALISATION_REGISTRY_NAME, registry.getName());
        personalisation.put(PERSONALISATION_REGISTRY_PHONE, registry.getPhone());
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS, caseData.getBoStopDetails());
        personalisation.put(PERSONALISATION_CAVEAT_CASE_ID, caseData.getBoCaseStopCaveatId());
        personalisation.put(PERSONALISATION_DECEASED_DOD, caseData.getDeceasedDateOfDeathFormatted());
        personalisation.put(PERSONALISATION_CCD_REFERENCE, caseId.toString());
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS_DEC, caseData.getBoStopDetailsDeclarationParagraph());
        personalisation.put(PERSONALISATION_WELSH_DECEASED_DATE_OF_DEATH,
            localDateToWelshStringConverter.convert(caseData.getDeceasedDateOfDeath()));

        return personalisation;
    }

    private StringBuilder getExelaBuiltData(List<ReturnedCaseDetails> cases) {
        StringBuilder data = new StringBuilder();

        for (ReturnedCaseDetails currentCase : cases) {
            try {
                data.append(getWillReferenceNumber(currentCase.getData()));
                data.append(", ");
                data.append(parseDelimiters(currentCase.getData().getDeceasedForenames()));
                data.append(", ");
                data.append(parseDelimiters(currentCase.getData().getDeceasedSurname()));
                data.append(", ");
                data.append(EXELA_CONTENT_DATE.format(currentCase.getData().getDeceasedDateOfBirth()));
                data.append(", ");
                data.append(EXELA_CONTENT_DATE.format(LocalDate.parse(currentCase.getData().getGrantIssuedDate())));
                data.append(", ");
                data.append(currentCase.getId().toString());
                data.append(", ");
                data.append(currentCase.getData().getRegistryLocation());
                data.append("\n");
            } catch (Exception e) {
                data.append(currentCase.getId().toString());
                data.append(", ");
                data.append(e.toString());
                data.append("\n");
            }
        }
        return data;
    }

    private StringBuilder getDraftCasesBuiltData(List<uk.gov.hmcts.reform.ccd.client.model.CaseDetails> cases) {
        StringBuilder data = new StringBuilder();

        for (uk.gov.hmcts.reform.ccd.client.model.CaseDetails currentCase : cases) {
            getCaseData(data, currentCase.getId(),
                    (String)currentCase.getData().getOrDefault("deceasedForenames",""),
                    (String)currentCase.getData().getOrDefault("deceasedSurname",""));
        }
        return data;
    }

    private void getCaseData(StringBuilder data, Long id, String deceasedForenames, String deceasedSurname) {
        try {
            data.append(id.toString());
            data.append(", ");
            data.append(parseDelimiters(deceasedForenames));
            data.append(", ");
            data.append(parseDelimiters(deceasedSurname));
            data.append("\n");
        } catch (Exception e) {
            data.append(id.toString());
            data.append(", ");
            data.append(e.toString());
            data.append("\n");
        }
    }

    private String parseDelimiters(String value) {
        return value.replaceAll(",", " ");
    }

    private String getWillReferenceNumber(CaseData data) {
        for (CollectionMember<ScannedDocument> document : data.getScannedDocuments()) {
            if ((DOC_TYPE_OTHER.equalsIgnoreCase(document.getValue().getType())
                        && DOC_SUBTYPE_WILL.equalsIgnoreCase(document.getValue().getSubtype()))
                        || DOC_TYPE_WILL.equalsIgnoreCase(document.getValue().getType())) {
                return document.getValue().getControlNumber();
            }
        }
        return "";
    }
}
