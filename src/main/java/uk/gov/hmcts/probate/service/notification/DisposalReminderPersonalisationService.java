package uk.gov.hmcts.probate.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

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
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMMMM yyyy");
    @Value("${disposal.personalNotificationLink}")
    private String linkToPersonalCase;
    @Value("${disposal.solsNotificationLink}")
    private String linkToSolicitorCase;


    public Map<String, String> getDisposalReminderPersonalisation(ReturnedCaseDetails caseDetails) {
        HashMap<String, String> personalisation = new HashMap<>();
        personalisation.put(PERSONALISATION_DATE_CREATED, DATE_FORMAT.format(caseDetails.getCreatedDate()));
        personalisation.put(PERSONALISATION_CASE_ID, caseDetails.getId().toString());
        personalisation.put(PERSONALISATION_SOLICITOR_NAME, caseDetails.getId().toString());
        personalisation.put(PERSONALISATION_LINK_TO_CASE,
                getHyperLink(caseDetails.getId().toString(),
                        caseDetails.getData().getApplicationType(),
                        caseDetails.getData().getCaseType()));

        return personalisation;
    }

    private String getHyperLink(String caseId, ApplicationType applicationType, String caseType) {
        return applicationType.equals(ApplicationType.PERSONAL)
                ? getPersonalCaseLink(caseId, caseType)
                : getSolicitorCaseLink(caseId);
    }

    private String getPersonalCaseLink(String caseId, String caseType) {
        return StringUtils.replace(
                StringUtils.replace(linkToPersonalCase, CASE_ID_STRING, caseId),
                CASE_TYPE_STRING, caseType
        );
    }

    private String getSolicitorCaseLink(String caseId) {
        return StringUtils.replace(linkToSolicitorCase, CASE_ID_STRING, caseId);
    }
}
