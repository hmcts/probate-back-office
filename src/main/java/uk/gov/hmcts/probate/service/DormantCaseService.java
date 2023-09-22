package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.probate.validator.EmailAddressNotifyApplicantValidationRule;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.State.DORMANT_NOTIFICATION1;
import static uk.gov.hmcts.probate.model.State.DORMANT_NOTIFICATION2;

@Service
@RequiredArgsConstructor
@Slf4j
public class DormantCaseService {

    public static final String DORMANT_SUMMARY = "This case has been moved to "
            + "the dormant state due to no action or event on the case for 6 months";
    public static final String REACTIVATE_DORMANT_SUMMARY = "Case-reactivated due to new evidence received";
    public static final String WITHDRAW_DORMANT_SUMMARY = "Case withdrawn as no action after six months of case in Dormant";
    private final CaseQueryService caseQueryService;
    private final CcdClientApi ccdClientApi;
    private final SecurityUtils securityUtils;
    private final EventValidationService eventValidationService;
    private final EmailAddressNotifyApplicantValidationRule emailAddressNotifyApplicantValidationRule;
    private final NotificationService notificationService;
    @Value("${make_dormant.add_time_minutes}")
    private int makeDormantAddTimeMinutes;
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    public static final DateTimeFormatter LOCAL_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void makeCasesDormant(String dormancyStartDate, String endDate) {
        log.info("Make Dormant upto date: {}", endDate);
        List<ReturnedCaseDetails> cases = caseQueryService.findCaseToBeMadeDormant(dormancyStartDate, endDate);
        log.info("Found {} cases with dated document for Make Dormant", cases.size());
        for (ReturnedCaseDetails returnedCaseDetails : cases) {
            GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder()
                        .moveToDormantDateTime(LocalDateTime.now(ZoneOffset.UTC)
                        .plusMinutes(makeDormantAddTimeMinutes))
                        .build();
            log.info("Updating case to Dormant in CCD by scheduler for case id : {}",returnedCaseDetails.getId());
            try {
                ccdClientApi.updateCaseAsCaseworker(CcdCaseType.GRANT_OF_REPRESENTATION,
                            returnedCaseDetails.getId().toString(),
                            returnedCaseDetails.getLastModified(),
                            grantOfRepresentationData, EventId.MAKE_CASE_DORMANT,
                            securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO(), DORMANT_SUMMARY,
                        DORMANT_SUMMARY);
                log.info("Updated case to Dormant in CCD by scheduler for case id : {}", returnedCaseDetails.getId());
            } catch (Exception e) {
                log.error("Dormant case error: Case:{}, cannot be moved in Dormant state {}",
                        returnedCaseDetails.getId(),e.getMessage());
            }
        }
    }

    private CCDData dataForEmailAddress(CaseData data) {
        return CCDData.builder()
                .applicationType(data.getApplicationType().name())
                .primaryApplicantEmailAddress(data.getPrimaryApplicantEmailAddress())
                .solsSolicitorEmail(data.getSolsSolicitorEmail())
                .build();
    }

    public void reactivateDormantCases(String date) {
        try {
            log.info("Reactivate Dormant cases for date: {}", date);
            LocalDate reactivateDate = LocalDate.parse(date, LOCAL_DATE_FORMAT);
            log.info("reactivateDate {}",reactivateDate);
            List<ReturnedCaseDetails> cases = caseQueryService.findCaseToBeReactivatedFromDormant(date);
            log.info("Found {} cases with dated document for Reactivate Dormant", cases.size());
            for (ReturnedCaseDetails returnedCaseDetails : cases) {
                log.info("MoveToDormantDateTime before {} ", returnedCaseDetails.getData().getMoveToDormantDateTime());
                if (StringUtils.isNotBlank(returnedCaseDetails.getData().getMoveToDormantDateTime())) {
                    LocalDateTime moveToDormantDateTime = LocalDateTime.parse(returnedCaseDetails.getData()
                            .getMoveToDormantDateTime(), DATE_FORMAT);
                    log.info("plus 3 month {}",moveToDormantDateTime.plusMonths(0).toLocalDate());
                    if (returnedCaseDetails.getLastModified().isAfter(moveToDormantDateTime) &&
                            Constants.NO.equals(returnedCaseDetails.getData().getDormantNotificationSent())) {
                        GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder()
                                .evidenceHandled(false)
                                .build();
                        log.info("Updating case to Stopped from Dormant in CCD by scheduler for case id : {}",
                                returnedCaseDetails.getId());
                        updateCaseAsCaseworker(returnedCaseDetails.getId().toString(), grantOfRepresentationData,
                            returnedCaseDetails.getLastModified());
                    }
                    else if (reactivateDate.equals(moveToDormantDateTime.plusMonths(0).toLocalDate())) {
                        log.info("second Notification 3+");
                        GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder()
                                .dormantNotificationSent(true)
                                .build();
                        updateCaseDormantAsCaseworker(returnedCaseDetails.getId().toString(), grantOfRepresentationData,
                                returnedCaseDetails.getLastModified());
                        //sendSecondNotification(returnedCaseDetails);
                    }
                    else if (reactivateDate.equals(moveToDormantDateTime.plusMonths(6).toLocalDate())) {
                        GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder()
                                .evidenceHandled(false)
                                .build();
                        updateCaseWithdrawnAsCaseworker(returnedCaseDetails.getId().toString(), grantOfRepresentationData,
                                returnedCaseDetails.getLastModified());
                    }
                }
            }
            log.info("End of the reactivateDormantCases method");
        } catch (Exception e) {
            log.error("Reactivate Dormant method error {}", e.getMessage());
        }
    }

    private void sendSecondNotification(ReturnedCaseDetails caseDetails) throws NotificationClientException {
        log.info("Preparing to send email notification for case still in Dormant");
        CaseData caseData = caseDetails.getData();
        CallbackResponse response;
        List<Document> documents = new ArrayList<>();
        CCDData emailAddressData = dataForEmailAddress(caseData);
        response = eventValidationService.validateDormantEmail(emailAddressData,
                emailAddressNotifyApplicantValidationRule);
        if (response.getErrors().isEmpty()) {
            log.info("Initiate call to notify Solicitor for case id {} ",
                    caseDetails.getId());
            //Document dormantSentEmail = notificationService.sendDormantEmailNotification2(DORMANT_NOTIFICATION2, caseDetails);
           // documents.add(dormantSentEmail);
            log.info("Successful response from notify for case id {} ",
                    caseDetails.getId());
        } else {
            log.info("No email sent or document returned to case: {}", caseDetails.getId());
        }
    }

    private void updateCaseAsCaseworker(String caseId, GrantOfRepresentationData grantOfRepresentationData,
                                        LocalDateTime lastModifiedDate) {
        try {
            ccdClientApi.updateCaseAsCaseworker(CcdCaseType.GRANT_OF_REPRESENTATION, caseId,
                lastModifiedDate, grantOfRepresentationData, EventId.REACTIVATE_DORMANT_CASE,
                    securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO(),
                    REACTIVATE_DORMANT_SUMMARY, REACTIVATE_DORMANT_SUMMARY);
            log.info("Updated case to Stopped from Dormant in CCD by scheduler for case id : {}", caseId);
        } catch (Exception e) {
            log.error("Dormant case error: Case:{} ,cannot be reactivated from Dormant state {}", caseId,
                    e.getMessage());
        }
    }

    private void updateCaseDormantAsCaseworker(String caseId, GrantOfRepresentationData grantOfRepresentationData,
                                        LocalDateTime lastModifiedDate) {
        try {
            ccdClientApi.updateCaseAsCaseworker(CcdCaseType.GRANT_OF_REPRESENTATION, caseId,
                    lastModifiedDate, grantOfRepresentationData, EventId.STILL_DORMANT,
                    securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO(),
                    DORMANT_SUMMARY, DORMANT_SUMMARY);
            log.info("sent notification for Dormant in CCD by scheduler for case id : {}", caseId);
        } catch (Exception e) {
            log.error("Dormant case error: Case:{} ,cannot be reactivated from Dormant state {}", caseId,
                    e.getMessage());
        }
    }

    private void updateCaseWithdrawnAsCaseworker(String caseId, GrantOfRepresentationData grantOfRepresentationData,
                                        LocalDateTime lastModifiedDate) {
        try {
            ccdClientApi.updateCaseAsCaseworker(CcdCaseType.GRANT_OF_REPRESENTATION, caseId,
                    lastModifiedDate, grantOfRepresentationData, EventId.WITHDRAWN,
                    securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO(),
                    WITHDRAW_DORMANT_SUMMARY, WITHDRAW_DORMANT_SUMMARY);
            log.info("Updated case to withdraw from Dormant in CCD by scheduler for case id : {}", caseId);
        } catch (Exception e) {
            log.error("Dormant case error: Case:{} ,cannot be withdrawn from Dormant state {}", caseId,
                    e.getMessage());
        }
    }
}
