package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.service.notify.NotificationClientException;

import java.util.List;

import static uk.gov.hmcts.probate.model.State.CASE_STOPPED_REQUEST_INFORMATION;

@Slf4j
@Service
@RequiredArgsConstructor
public class InformationRequestCorrespondenceService {

    private final NotificationService notificationService;
    private Document previous = null;

    public List<Document> emailInformationRequest(CaseDetails caseDetails) {
        try {
            final Document notification = notificationService.sendEmail(CASE_STOPPED_REQUEST_INFORMATION, caseDetails);
            log.info("Successful response for request for information email for case id {} ", caseDetails.getId());
            final List<Document> rVal;
            if (previous != null) {
                rVal = List.of(notification, previous);
                previous.setRaced(true);
            } else {
                rVal = List.of(notification);
            }
            previous = notification;
            return rVal;
        } catch (NotificationClientException e) {
            log.error(e.getMessage());
            // this feels wrong - do we not want to alert the caller that the email sending has failed?
            return List.of();
        }
    }
}
