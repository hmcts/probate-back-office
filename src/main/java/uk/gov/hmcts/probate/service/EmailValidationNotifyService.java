package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.IDN;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailValidationNotifyService extends EmailValidationService {

    /**
     * This class ports gov.uk/notify email validation
     * https://github.com/alphagov/notifications-utils/blob/main/notifications_utils/recipient_validation/email_address.py
     * Currently, we have no API to call to check if Notify would accept an email or not until notifications are being
     * sent. Not an ideal solution, however until a validation API is provided we'll need to copy functionality here.
     * This is created separately to EmailValidationService to maintain backwards compatibility with previously
     * submitted email addresses.
     */

    private static final int MAX_EMAIL_LENGTH = 320;
    private static final int MAX_HOSTNAME_LENGTH = 253;
    private static final int MAX_HOSTNAME_PART_LENGTH = 63;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@([^.@][^@\\s]+)$",
            Pattern.UNICODE_CHARACTER_CLASS
    );

    private static final Pattern HOSTNAME_PART_PATTERN = Pattern.compile(
            // Not sure if this properly follows notify, sonarscan will not allow that expression.
            "^([a-z0-9]+)(-?-[a-z0-9]+){0,62}$",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern TLD_PART_PATTERN = Pattern.compile(
            "^([a-z]{2,63}|xn--([a-z0-9]+-){0,58}[a-z0-9]?)$",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern OBSCURE_WHITESPACE = Pattern.compile(
            "[\\u200B\\u200C\\u200D\\u2060\\uFEFF]"
    );

    private static final Pattern SURROUNDING_WHITESPACE = Pattern.compile(
            "^([\\p{javaWhitespace}\\p{Z}\\x{85}]+)|([\\p{javaWhitespace}\\p{Z}\\x{85}]+)$"
    );

    @Override
    public boolean validateEmailAddress(String emailAddress) {
        String formattedEmail = formatEmailAddress(emailAddress);

        if ("NULL".equals(formattedEmail) || !isValid(formattedEmail)) {
            log.info("Invalid email address: {}", getHashedEmail(emailAddress));
            return false;
        }

        return true;
    }

    private boolean isValid(String emailAddress) {
        if (emailAddress.codePointCount(0, emailAddress.length()) > MAX_EMAIL_LENGTH) {
            log.info("email address is greater than " + MAX_EMAIL_LENGTH);
            return false;
        }

        if (emailAddress.contains("..")) {
            log.info("email address contains two or more consecutive \".\"");
            return false;
        }

        if (emailAddress.startsWith(".")) {
            log.info("email address contains a leading \".\"");
            return false;
        }

        Matcher matcher = EMAIL_PATTERN.matcher(emailAddress);
        if (!matcher.matches()) {
            log.info("email address does not match regex pattern");
            return false;
        }

        String localpart = emailAddress.split("@", -1)[0];
        if (localpart.endsWith(".")) {
            log.info("local-part of email address contains a trailing \".\"");
            return false;
        }

        String hostname;
        try {
            hostname = IDN.toASCII(matcher.group(1));
        } catch (IllegalArgumentException e) {
            log.info("email address cannot be converted to ASCII");
            return false;
        }

        String[] parts = hostname.split("\\.", -1);
        if (hostname.length() > MAX_HOSTNAME_LENGTH || parts.length < 2) {
            // Emails can have more than
            log.info("email address hostname is either too long, or contains too many parts");
            return false;
        }

        for (String part : parts) {
            if (part.length() > MAX_HOSTNAME_PART_LENGTH
                    || !HOSTNAME_PART_PATTERN.matcher(part).matches()) {
                log.info("part of email address hostname is either too long, or fails regex validation");
                return false;
            }
        }

        return TLD_PART_PATTERN.matcher(parts[parts.length - 1]).matches();
    }

    public String formatEmailAddress(String emailAddress) {
        if (emailAddress == null) {
            return "NULL";
        }

        return stripAndRemoveObscureWhitespace(emailAddress)
                .toLowerCase(Locale.ROOT);
    }

    private String stripAndRemoveObscureWhitespace(final String emailAddress) {
        String cleanedObscure = OBSCURE_WHITESPACE.matcher(emailAddress).replaceAll("");
        return SURROUNDING_WHITESPACE.matcher(cleanedObscure).replaceAll("");
    }
}
