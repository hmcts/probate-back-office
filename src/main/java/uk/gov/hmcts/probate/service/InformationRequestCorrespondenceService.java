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

    public List<Document> emailInformationRequest(CaseDetails caseDetails) throws NotificationClientException {
        final Document notification = notificationService.sendEmail(CASE_STOPPED_REQUEST_INFORMATION, caseDetails);
        log.info("Successful response for request for information email for case id {} ", caseDetails.getId());
        return List.of(notification);
    }
}
