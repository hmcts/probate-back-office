package uk.gov.hmcts.probate.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static uk.gov.hmcts.probate.util.EmailAddressUtils.INVALID_EMAIL_ADDRESSES;
import static uk.gov.hmcts.probate.util.EmailAddressUtils.VALID_EMAIL_ADDRESSES;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmailValidationServiceTest {

    private final EmailValidationService emailValidationService = new EmailValidationService();

    @Test
    public void shouldAssertEmailsAsValid() {
        for (String email : VALID_EMAIL_ADDRESSES) {
            assertTrue(emailValidationService.validateEmailAddress(email));
        }
    }

    @Test
    public void shouldAssertEmailsAsFalse() {
        for (String email : INVALID_EMAIL_ADDRESSES) {
            assertFalse(emailValidationService.validateEmailAddress(email));
        }
    }

}