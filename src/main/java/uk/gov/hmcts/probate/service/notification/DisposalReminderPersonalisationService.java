package uk.gov.hmcts.probate.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class DisposalReminderPersonalisationService {

    private static final String PERSONALISATION_DATE_CREATED = "date_created";
    private static final String PERSONALISATION_CASE_ID = "case_ref";
    private static final String PERSONALISATION_SOLICITOR_NAME = "solicitor_name";
    private static final String PERSONALISATION_LINK_TO_CASE = "link_to_case";
    private static final String CASE_ID_STRING = "<CASE_ID>";
    private static final String CASE_TYPE_STRING = "<CASE_TYPE>";
    private static final String SOLICITOR_CASE_URL = "/cases/case-details/<CASE_ID>";
    private static final String PERSONAL_CASE_URL = "/get-case/<CASE_ID>?probateType=<CASE_TYPE>";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMMM yyyy");
    @Value("${disposal.personalNotificationLink}")
    private String urlPrefixToPersonalCase;
    @Value("${disposal.solsNotificationLink}")
    private String urlPrefixSolicitorCase;


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
        log.info("Get personal link for: {}", caseId);
        return StringUtils.replace(urlPrefixSolicitorCase + SOLICITOR_CASE_URL, CASE_ID_STRING, caseId);
    }

    private String getSolicitorName(Map<String, Object> data, ApplicationType applicationType) {
        log.info("Get solicitor name");

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

    private String getCaseType(Map<String, Object> data) {
        return getStringValue(data, "caseType");
    }

    private String getStringValue(Map<String, Object> data, String key) {
        return Optional.ofNullable(data.get(key))
                .map(Object::toString)
                .orElse(StringUtils.EMPTY);
    }
}
