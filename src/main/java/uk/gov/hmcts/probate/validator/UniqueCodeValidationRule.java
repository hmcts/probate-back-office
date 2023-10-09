package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.Locale;


@Component
@RequiredArgsConstructor
public class UniqueCodeValidationRule {

    private static final String UNIQUE_CODE_REGEX_PATTERN =
            "^(cts|CTS) [a-zA-Z0-9]{10} [a-zA-Z0-9]{4} [a-zA-Z0-9]{4}$|^(cts|CTS)[a-zA-Z0-9]{18}$";
    private static final String REMOVE_SPACE_REGEX_PATTERN = "\\s+";
    private static final int uniqueCodeLength = 21;
    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    public void validate(CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();
        if (caseData.getUniqueProbateCodeId() != null) {
            if (!caseData.getUniqueProbateCodeId().matches(UNIQUE_CODE_REGEX_PATTERN)
                    || !removeSpaces(caseData.getUniqueProbateCodeId())) {
                String userMessage = businessValidationMessageRetriever
                        .getMessage("uniqueProbateCode", null, Locale.UK);
                throw new BusinessValidationException(userMessage,
                        "Unique Probate code is invalid: " + caseDetails.getId());
            }
        }
    }

    private boolean removeSpaces(String uniqueCode) {
        return uniqueCodeLength == uniqueCode.replaceAll(REMOVE_SPACE_REGEX_PATTERN, "").length();
    }
}
