package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
@Component
public class EvidenceUploadService {

    public void updateLastEvidenceAddedDate(CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();
        if (caseData.getGrantStoppedDate() != null) {
            log.info("Case is stopped.");
            log.info("getDocumentUploadedAfterCaseStopped has value {}",caseData.getDocumentUploadedAfterCaseStopped());
            if (caseData.getDocumentUploadedAfterCaseStopped().equalsIgnoreCase("No")
                    || caseData.getDocumentUploadedAfterCaseStopped() == null) {
                log.info("Setting documentUploadedAfterCaseStopped to Yes");
                caseData.setDocumentUploadedAfterCaseStopped("Yes");
                this.setLastEvidenceAddedDate(caseDetails);
            } else {
                log.info("A document has already been uploaded since "
                        + "case was stopped so no need to update lastEvidenceAddedDate");
            }
        } else {
            log.info("Case is ongoing.");
            this.setLastEvidenceAddedDate(caseDetails);
        }
    }

    public CaseDetails setLastEvidenceAddedDate(CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();
        log.info("Updating updateLastEvidenceAddedDate for case {}", caseDetails.getId());
        caseData.setLastEvidenceAddedDate(LocalDate.now());
        return caseDetails;
    }
}
