package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
@Service
public class CaseEscalatedService {

    public void caseEscalated(CaseDetails caseDetails) {
        caseDetails.getData().setEscalatedDate(LocalDate.now());
    }
}
