package uk.gov.hmcts.probate.service.ccd;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class CcdReferenceFormatterService {

    public String getFormattedCaseReference(String caseId) {
        if (isAlreadyFormattedWithHyphens(caseId)) {
            return "#" + caseId;
        } else {
            return "#" + caseId.substring(0, 4) + "-"
                + caseId.substring(4, 8) + "-"
                + caseId.substring(8, 12) + "-"
                + caseId.substring(12, 16);
        }
    }
    public static boolean isAlreadyFormattedWithHyphens(String input) {
        // Define a regular expression to match the specified format
        String regex = "\\d{4}-\\d{4}-\\d{4}-\\d{4}";
        return input.matches(regex);
    }
}
