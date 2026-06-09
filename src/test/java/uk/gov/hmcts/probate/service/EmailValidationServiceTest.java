package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.hmcts.probate.util.EmailAddressUtils.INVALID_EMAIL_ADDRESSES;
import static uk.gov.hmcts.probate.util.EmailAddressUtils.VALID_EMAIL_ADDRESSES;

class EmailValidationServiceTest {

    private final EmailValidationService emailValidationService = new EmailValidationService();

    @Test
    void shouldAssertEmailsAsValid() {
        for (String email : VALID_EMAIL_ADDRESSES) {
            assertTrue(emailValidationService.validateEmailAddress(email));
        }
    }

    @Test
    void shouldAssertEmailsAsFalse() {
        for (String email : INVALID_EMAIL_ADDRESSES) {
            assertFalse(emailValidationService.validateEmailAddress(email));
        }
    }

    @Test
    void shouldReturnNullIfHashingNull() {
        final String expected = "NULL";
        final String actual = emailValidationService.getHashedEmail(null);
        assertEquals(expected, actual, "if given null input, should return \"NULL\"");
    }

    @Test
    void shouldReturnHashIfHashingInput() {
        final String input = "some_input";

        final String expected = "SHA-256[0dc1e7e935329cc6e86c871f63df89fbbfdf0c3d264030e5da42316dcc332427]";
        final String actual = emailValidationService.getHashedEmail(input);
        assertEquals(expected, actual, "if given some_input, should return the sha256 of it");
    }

}
