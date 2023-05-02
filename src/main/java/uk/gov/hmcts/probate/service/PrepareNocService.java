package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
@Service
public class PrepareNocService {

    public void addNocDate(CaseData caseData) {
        caseData.setNocPreparedDate(LocalDate.now());
    }
}
