package uk.gov.hmcts.probate.service.ccd;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class CcdReferenceFormatterService {

    public String getFormattedCaseReference(String caseId) {
        return "#" + caseId.substring(0, 4) + "-"
                + caseId.substring(4, 8) + "-"
                + caseId.substring(8, 12) + "-"
                + caseId.substring(12, 16);
    }
}
