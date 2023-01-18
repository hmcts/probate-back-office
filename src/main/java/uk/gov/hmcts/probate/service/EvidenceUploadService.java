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
        log.info("Updating updateLastEvidenceAddedDate for case {}", caseDetails.getId());
        caseData.setLastEvidenceAddedDate(LocalDate.now());
    }

    public void setDocumentUploadedAfterCaseStopped(CaseDetails caseDetails, String choice) {
        CaseData caseData = caseDetails.getData();
        log.info("Setting documentUploadedAfterCaseStopped to {}", choice);
        caseData.setDocumentUploadedAfterCaseStopped(choice);
    }
}
