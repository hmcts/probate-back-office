package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

import static uk.gov.hmcts.probate.model.Constants.EMAIL_REGEX;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailValidationService {

    public boolean validateEmailAddress(String emailAddress) {
        return isEmailValid(emailAddress);
    }

    private boolean isEmailValid(String emailAddress) {
        if (emailAddress == null) {
            log.info("email address is null");
            return false;
        }

        if (!Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE).matcher(emailAddress).matches()) {
            log.info("Notification not sent due to invalid email address: {}",
                getHashedEmail(emailAddress));
            return false;
        }
        return true;
    }

    public String getHashedEmail(final String emailAddress) {
        if (emailAddress == null) {
            return "NULL";
        }
        final String digest = DigestUtils.sha256Hex(emailAddress);
        final StringBuilder builder = new StringBuilder();

        builder.append("SHA-256[");
        builder.append(digest);
        builder.append("]");
        return builder.toString();
    }
}
