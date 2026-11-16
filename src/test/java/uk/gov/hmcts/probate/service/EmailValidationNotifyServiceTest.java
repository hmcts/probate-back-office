package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailValidationNotifyServiceTest {

    private final EmailValidationNotifyService emailValidationNotifyService = new EmailValidationNotifyService();

    @Test
    void shouldReturnFalseIfEmailTooLong() {
        String email = "a".repeat(315) + "@a.com"; // 315 + 6 = 321 characters
        assertFalse(emailValidationNotifyService.validateEmailAddress(email));
    }

    @Test
    void shouldReturnFalseIfPartOfHostnameTooLong() {
        String HOSTNAME_PART = "a".repeat(65);
        String email =  "a@" + HOSTNAME_PART + ".com";
        assertFalse(emailValidationNotifyService.validateEmailAddress(email));
    }

    @Test
    void shouldReturnFalseIfHostnameTooLong() {
        String HOSTNAME_PART = "b".repeat(49) + ".";
        //  51 + (4*50) + 3 = 254 characters
        String email = "a@b" + HOSTNAME_PART.repeat(5) + "com";
        assertFalse(emailValidationNotifyService.validateEmailAddress(email));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            " ",
            "plainaddress",
            "@example.com",
            "email@example",
            "email@example.c",
            "email@example..com",
            "email@-example.com",
            "email@example-.com",
            "email@exam_ple.com",
            "email@123.123.123.123",
            "email@example@example.com",
            "email.example.com",
            "email.@example.com",
            "email..email@example.com",
            ".email@example.com",
            "email@example.com (Joe Smith)",
            "Joe Smith <email@example.com>",
            "#@%^%#$@#$@#.com",
            "あいうえお@example.com"
    })
    void shouldReturnInvalidEmailAddress(String email) {
        assertFalse(emailValidationNotifyService.validateEmailAddress(email));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "email@example.com",
            "email@subdomain.example.com",
            "email@example-one.com",
            "email@example.name",
            "email@example.museum",
            "email@example.co",
            "email@example.co.jp",
            "firstname.lastname@example.com",
            "firstname+lastname@example.com",
            "firstname-lastname@example.com",
            "1234567890@example.com",
            "_______@example.com",
    })
    void shouldReturnValidEmailAddress(String email) {
        assertTrue(emailValidationNotifyService.validateEmailAddress(email));
    }

    @Test
    void shouldFormatEmailAddress() {
        String email = " \u200BJohn.Doe+Test@EXAMPLE.COM\u00A0";
        assertEquals("john.doe+test@example.com",
                emailValidationNotifyService.formatEmailAddress(email));
    }

    @Test
    void shouldReturnNullWhenFormattingNull() {
        final String expected = "NULL";
        final String actual = emailValidationNotifyService.formatEmailAddress(null);
        assertEquals(expected, actual, "if given null input, should return \"NULL\"");
    }
}
