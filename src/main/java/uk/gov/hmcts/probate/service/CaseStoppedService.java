package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.time.LocalDate;
import java.time.Period;

@Slf4j
@RequiredArgsConstructor
@Service
public class CaseStoppedService {

    public void caseStopped(CaseDetails caseDetails) {
        caseDetails.getData().setGrantStoppedDate(LocalDate.now());
    }

    public void caseResolved(CaseDetails caseDetails) {
        log.info("Case resolved: {} ", caseDetails.getId());

        addToGrantDelay(caseDetails);
        addToAwaitingDocumentation(caseDetails);

        caseDetails.getData().setGrantStoppedDate(null);
    }

    private void addToAwaitingDocumentation(CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();
        if (caseData.getGrantAwaitingDocumentationNotificationDate() != null
            && caseData.getGrantStoppedDate() != null) {

            LocalDate now = LocalDate.now();
            Period period = Period.between(caseData.getGrantStoppedDate(), now);

            LocalDate notificationDate =
                caseData.getGrantAwaitingDocumentationNotificationDate().plusDays(period.getDays());
            log.info("From case-stopped/resolved, setting grantAwaitingDocumentationNotificationDate {} for case {}",
                notificationDate.toString(), caseDetails.getId());
            caseData.setGrantAwaitingDocumentationNotificationDate(notificationDate);
        }
    }

    private void addToGrantDelay(CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();
        if ((StringUtils.isEmpty(caseData.getGrantDelayedNotificationSent())
            || caseData.getGrantDelayedNotificationSent().equals(Constants.NO))
            && caseData.getGrantStoppedDate() != null
            && caseData.getGrantDelayedNotificationDate() != null) {

            LocalDate now = LocalDate.now();
            Period period = Period.between(caseData.getGrantStoppedDate(), now);

            LocalDate notificationDate = caseData.getGrantDelayedNotificationDate().plusDays(period.getDays());
            log.info("From case-stopped/resolved, setting grantDelayedNotificationDate {} for case {}",
                notificationDate.toString(), caseDetails.getId());
            caseData.setGrantDelayedNotificationDate(notificationDate);
        }
    }
}
