package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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

        CaseData caseData = caseDetails.getData();

        addToGrantDelay(caseData);
        addToAwaitingDocumentation(caseData);

        caseData.setGrantStoppedDate(null);
    }

    private void addToAwaitingDocumentation(CaseData caseData) {
        if (caseData.getGrantAwaitingDocumentationNotificationDate() != null
            && caseData.getGrantStoppedDate() != null) {
            
            LocalDate now = LocalDate.now();
            Period period = Period.between(caseData.getGrantStoppedDate(), now);

            caseData.setGrantAwaitingDocumentationNotificationDate(caseData.getGrantAwaitingDocumentationNotificationDate()
                .plusDays(period.getDays()));
        }
    }

    private void addToGrantDelay(CaseData caseData) {
        if ((StringUtils.isEmpty(caseData.getGrantDelayedNotificationSent())
            || caseData.getGrantDelayedNotificationSent().equals(Constants.NO))
            && caseData.getGrantStoppedDate() != null
            && caseData.getGrantDelayedNotificationDate() != null) {

            LocalDate now = LocalDate.now();
            Period period = Period.between(caseData.getGrantStoppedDate(), now);

            caseData.setGrantDelayedNotificationDate(caseData.getGrantDelayedNotificationDate()
                .plusDays(period.getDays()));
        }
    }
}
