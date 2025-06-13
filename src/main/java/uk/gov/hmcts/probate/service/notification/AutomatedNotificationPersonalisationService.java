package uk.gov.hmcts.probate.service.notification;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.LanguagePreference;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.StopReason;
import uk.gov.hmcts.probate.service.DateFormatterService;
import uk.gov.hmcts.probate.service.template.pdf.LocalDateToWelshStringConverter;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Slf4j
@RequiredArgsConstructor
@Service
public class AutomatedNotificationPersonalisationService {

    private static final String PERSONALISATION_DATE_CREATED = "date_created";
    private static final String PERSONALISATION_CASE_ID = "case_ref";
    private static final String PERSONALISATION_CCD_REFERENCE = "ccd_reference";
    private static final String PERSONALISATION_SOLICITOR_NAME = "solicitor_name";
    private static final String PERSONALISATION_LINK_TO_CASE = "link_to_case";
    private static final String PERSONALISATION_APPLICANT_NAME = "applicant_name";
    private static final String PERSONALISATION_DECEASED_NAME = "deceased_name";
    private static final String PERSONALISATION_RESPOND_DATE = "respond_date";
    private static final String PERSONALISATION_CASE_STOP_DETAILS_DEC = "boStopDetailsDeclarationParagraph";
    private static final String PERSONALISATION_CASE_STOP_REASONS = "stop-reasons";
    private static final String PERSONALISATION_CASE_STOP_REASONS_WELSH = "stop-reasons-welsh";
    private static final String PERSONALISATION_DECEASED_DOD = "deceased_dod";
    private static final String PERSONALISATION_WELSH_DECEASED_DATE_OF_DEATH = "welsh_deceased_date_of_death";
    private static final String PERSONALISATION_DISPLAY_SINGLE_STOP_REASON = "display-single-stop-reason";
    private static final String PERSONALISATION_DISPLAY_MULTIPLE_STOP_REASONS = "display-multiple-stop-reasons";
    private static final String CASE_ID_STRING = "<CASE_ID>";
    private static final String CASE_TYPE_STRING = "<CASE_TYPE>";
    private static final String SOLICITOR_CASE_URL = "/cases/case-details/<CASE_ID>";
    private static final String PERSONAL_CASE_URL = "/get-case/<CASE_ID>?probateType=<CASE_TYPE>";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMMM yyyy");
    private static final DateTimeFormatter DOD_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final LocalDateToWelshStringConverter localDateToWelshStringConverter;
    private final StopReasonService stopReasonService;
    private final DateFormatterService dateFormatterService;
    @Value("${disposal.personalNotificationLink}")
    private String urlPrefixToPersonalCase;
    @Value("${disposal.solsNotificationLink}")
    private String urlPrefixSolicitorCase;

    private final ObjectMapper objectMapper;

    public Map<String, String> getDisposalReminderPersonalisation(CaseDetails caseDetails,
                                                                  ApplicationType applicationType) {
        log.info("getDisposalReminderPersonalisation");
        HashMap<String, String> personalisation = new HashMap<>();
        Map<String, Object> data = caseDetails.getData();
        personalisation.put(PERSONALISATION_DATE_CREATED, DATE_FORMAT.format(caseDetails.getCreatedDate()));
        personalisation.put(PERSONALISATION_CASE_ID, caseDetails.getId().toString());
        personalisation.put(PERSONALISATION_SOLICITOR_NAME, getSolicitorName(data, applicationType));
        personalisation.put(PERSONALISATION_LINK_TO_CASE, getHyperLink(caseDetails.getId().toString(),
                applicationType, getCaseType(data)));
        return personalisation;
    }

    public Map<String, String> getPersonalisation(uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails,
                                                  ApplicationType applicationType) {
        log.info("AutomatedNotificationPersonalisationService getPersonalisation");
        Map<String, Object> caseData = caseDetails.getData();
        HashMap<String, String> personalisation = new HashMap<>();
        personalisation.put(PERSONALISATION_CCD_REFERENCE, caseDetails.getId().toString());
        personalisation.put(PERSONALISATION_RESPOND_DATE, DATE_FORMAT.format(LocalDate.now().plusDays(14)));
        personalisation.put(PERSONALISATION_APPLICANT_NAME, getPrimaryApplicantName(caseData));
        personalisation.put(PERSONALISATION_DECEASED_NAME, getDeceasedFullName(caseData));
        personalisation.put(PERSONALISATION_SOLICITOR_NAME, getSolicitorName(caseData, applicationType));
        List<CollectionMember<StopReason>> stopReasonList = getStopReasonList(caseDetails.getData());
        personalisation.put(PERSONALISATION_CASE_STOP_REASONS, getStopReason(stopReasonList, false));
        personalisation.put(PERSONALISATION_CASE_STOP_REASONS_WELSH, getStopReason(stopReasonList, true));
        personalisation.put(PERSONALISATION_DISPLAY_SINGLE_STOP_REASON, stopReasonList.size() == 1 ? YES : NO);
        personalisation.put(PERSONALISATION_DISPLAY_MULTIPLE_STOP_REASONS, stopReasonList.size() > 1 ? YES : NO);
        LocalDate dateOfDeath = getDateValue(caseData, "deceasedDateOfDeath");
        personalisation.put(PERSONALISATION_DECEASED_DOD, dateFormatterService.formatDate(dateOfDeath));
        personalisation.put(PERSONALISATION_WELSH_DECEASED_DATE_OF_DEATH,
                localDateToWelshStringConverter.convert(dateOfDeath));
        personalisation.put(PERSONALISATION_CASE_ID, caseDetails.getId().toString());
        personalisation.put(PERSONALISATION_CASE_STOP_DETAILS_DEC,
                getStringValue(caseData, PERSONALISATION_CASE_STOP_DETAILS_DEC));
        personalisation.put(PERSONALISATION_LINK_TO_CASE, getHyperLink(caseDetails.getId().toString(),
                applicationType, getCaseType(caseData)));
        return personalisation;
    }

    private String getStopReason(List<CollectionMember<StopReason>> stopReasonList, boolean isWelsh) {
        if (stopReasonList.isEmpty()) {
            return StringUtils.EMPTY;
        }
        StringBuilder stopReasons = new StringBuilder();
        LanguagePreference languagePreference = isWelsh ? LanguagePreference.WELSH : LanguagePreference.ENGLISH;

        stopReasonList.stream()
                .filter(sr -> isValidStopReason(sr, false))
                .forEach(sr -> stopReasons.append(
                        stopReasonService.getStopReasonDescription(languagePreference,
                                sr.getValue().getCaseStopReason())).append("\n"));

        // Filter for "DocumentsRequired" reasons
        List<CollectionMember<StopReason>> docRequiredReasons = stopReasonList.stream()
                .filter(sr -> isValidStopReason(sr, true))
                .toList();

        if (!docRequiredReasons.isEmpty()) {
            stopReasons.append(stopReasonService.getStopReasonDescription(languagePreference,
                    "DocumentsRequired")).append("\n");
            docRequiredReasons.forEach(sr ->
                    stopReasons.append("&nbsp;&nbsp;&nbsp;&nbsp;").append(stopReasonService
                            .getStopReasonDescription(languagePreference,sr.getValue()
                                    .getCaseStopSubReasonDocRequired())).append("\n"));
        }

        return stopReasons.toString();
    }

    private boolean isValidStopReason(CollectionMember<StopReason> stopReason, boolean isDocumentsRequired) {
        StopReason value = stopReason.getValue();
        return value != null && value.getCaseStopReason() != null
                && (isDocumentsRequired == value.getCaseStopReason().equals("DocumentsRequired"));
    }

    private List<CollectionMember<StopReason>> getStopReasonList(Map<String, Object> data) {
        Object raw = data.get("boCaseStopReasonList");
        if (raw == null) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.convertValue(raw, new TypeReference<List<CollectionMember<StopReason>>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse probateNotificationsGenerated. Reason: {}", e.getMessage());
            throw e;
        }
    }

    private String getHyperLink(String caseId, ApplicationType applicationType, String caseType) {
        return applicationType.equals(ApplicationType.PERSONAL)
                ? getPersonalCaseLink(caseId, caseType)
                : getSolicitorCaseLink(caseId);
    }

    private String getPersonalCaseLink(String caseId, String caseType) {
        String caseTypeLink = Optional.ofNullable(caseType).filter("intestacy"::equalsIgnoreCase)
                .map(intestacy -> "INTESTACY")
                .orElse("PA");
        log.info("Get personal link for: {} caseTypeLink: {}", caseId, caseTypeLink);
        return StringUtils.replace(
                StringUtils.replace(urlPrefixToPersonalCase + PERSONAL_CASE_URL, CASE_ID_STRING, caseId),
                CASE_TYPE_STRING, caseTypeLink
        );
    }

    private String getSolicitorCaseLink(String caseId) {
        return StringUtils.replace(urlPrefixSolicitorCase + SOLICITOR_CASE_URL, CASE_ID_STRING, caseId);
    }

    private String getSolicitorName(Map<String, Object> data, ApplicationType applicationType) {
        if (data != null && applicationType.equals(ApplicationType.SOLICITOR)) {
            String solsSOTName = getStringValue(data, "solsSOTName");
            String solsSOTForenames = getStringValue(data, "solsSOTForenames");
            String solsSOTSurname = getStringValue(data, "solsSOTSurname");

            if (StringUtils.isNotEmpty(solsSOTName)) {
                return solsSOTName;
            } else if (StringUtils.isNotEmpty(solsSOTForenames) && StringUtils.isNotEmpty(solsSOTSurname)) {
                return String.join(" ", solsSOTForenames, solsSOTSurname);
            }
        }
        return StringUtils.EMPTY;
    }

    private String getPrimaryApplicantName(Map<String, Object> data) {
        String primaryApplicantForenames = getStringValue(data, "primaryApplicantForenames");
        String primaryApplicantSurname = getStringValue(data, "primaryApplicantSurname");
        return String.join(" ", primaryApplicantForenames, primaryApplicantSurname);
    }

    private String getDeceasedFullName(Map<String, Object> data) {
        String deceasedForenames = getStringValue(data, "deceasedForenames");
        String deceasedSurname = getStringValue(data, "deceasedSurname");
        return String.join(" ", deceasedForenames, deceasedSurname);
    }

    private String getCaseType(Map<String, Object> data) {
        return getStringValue(data, "caseType");
    }

    private String getStringValue(Map<String, Object> data, String key) {
        return Optional.ofNullable(data.get(key))
                .map(Object::toString)
                .orElse(StringUtils.EMPTY);
    }

    private LocalDate getDateValue(Map<String, Object> data, String key) {
        return Optional.ofNullable(data.get(key))
                .map(Object::toString)
                .map(this::normaliseDate)
                .map(date -> LocalDate.parse(date, DOD_DATE_FORMAT))
                .orElse(null);
    }

    private String normaliseDate(String s) {
        if (s.startsWith("[") && s.endsWith("]")) {
            String inner = s.substring(1, s.length() - 1);
            String[] parts = inner.split(",", -1);
            if (parts.length == 3) {
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].strip();
                }
                return String.join("-", parts);
            }
        }
        return s;
    }
}
