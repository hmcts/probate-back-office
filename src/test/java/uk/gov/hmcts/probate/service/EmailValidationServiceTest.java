package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static uk.gov.hmcts.probate.util.EmailAddressUtils.INVALID_EMAIL_ADDRESSES;
import static uk.gov.hmcts.probate.util.EmailAddressUtils.VALID_EMAIL_ADDRESSES;

@ExtendWith(SpringExtension.class)
@SpringBootTest
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

}
