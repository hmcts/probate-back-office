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
            "^(cts|CTS)\\s?([a-zA-Z0-9]\\s?){18}$";
    private static final String REMOVE_SPACE_REGEX_PATTERN = "\\s+";
    private static final int UNIQUE_CODE_MIN_LENGTH = 21;
    private static final int UNIQUE_CODE_MAX_LENGTH = 25;
    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    public void validate(CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();
        if (caseData.getUniqueProbateCodeId() != null && (!caseData.getUniqueProbateCodeId()
                .matches(UNIQUE_CODE_REGEX_PATTERN) || !removeSpaces(caseData.getUniqueProbateCodeId())
        || caseData.getUniqueProbateCodeId().length() > UNIQUE_CODE_MAX_LENGTH)) {
            String userMessage = businessValidationMessageRetriever
                    .getMessage("uniqueProbateCode", null, Locale.UK);
            throw new BusinessValidationException(userMessage,
                    "Unique Probate code is invalid: " + caseDetails.getId());
        }
    }

    private boolean removeSpaces(String uniqueCode) {
        return UNIQUE_CODE_MIN_LENGTH == uniqueCode.replaceAll(REMOVE_SPACE_REGEX_PATTERN, "").length();
    }
}
