package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;

import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
import static uk.gov.hmcts.probate.model.Constants.EMAIL_REGEX;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailValidationService {

    public boolean validateEmailAddress(CCDData ccdData) {
        String emailAddress = ccdData.getApplicationType().equalsIgnoreCase(String.valueOf(PERSONAL))
            ? ccdData.getPrimaryApplicantEmailAddress() : ccdData.getSolsSolicitorEmail();
        String caseId = ccdData.getCaseId() != null ? ccdData.getCaseId().toString() : "<No case ID>";
        return isEmailValid(emailAddress, caseId);
    }

    public boolean validateEmailAddress(CaveatData caveatData) {
        String emailAddress = caveatData.getCaveatorEmailAddress();
        return isEmailValid(emailAddress, caveatData.getRecordId());
    }

    public boolean validateEmailAddress(String executorEmailAddress, Long caseId) {
        return isEmailValid(executorEmailAddress, caseId.toString());
    }

    public boolean validateEmailAddress(String emailAddress, String bulkScan) {
        return isEmailValid(emailAddress, bulkScan);
    }

    public boolean isEmailNotValidErrorResponse(List<FieldErrorResponse> errors) {
        FieldErrorResponse invalidEmail = errors.stream()
            .filter(error -> error.getCode().equals("notifyApplicantInvalidEmail")
                || error.getCode().equals("emailInvalidCaveats"))
            .findFirst()
            .orElse(null);
        return invalidEmail != null;
    }

    private boolean isEmailValid(String emailAddress, String caseID) {
        if (emailAddress == null) {
            log.info("email address is null for case: {}", caseID);
            return false;
        }

        if (!Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE).matcher(emailAddress).matches()) {
            log.info("Notification not sent due to invalid email address: {}, for case: {}",
                getEmailEncodedBase64(emailAddress), caseID);
            return false;
        }
        return true;
    }

    private String getEmailEncodedBase64(String emailAddress) {
        return new String(Base64.getEncoder().encode(emailAddress.getBytes()));
    }

}
