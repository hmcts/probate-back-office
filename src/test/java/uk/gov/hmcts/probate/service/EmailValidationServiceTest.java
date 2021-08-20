package uk.gov.hmcts.probate.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.model.Constants;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmailValidationServiceTest {

    private final EmailValidationService emailValidationService = new EmailValidationService();
    private static final Long CASE_NUMBER = 1L;

    @Test
    public void shouldAssertEmailsAsValid() {
        for (String email : Constants.VALID_EMAIL_ADDRESSES) {
            assertTrue(emailValidationService.validateEmailAddress(email, CASE_NUMBER));
        }
    }

    @Test
    public void shouldAssertEmailsAsFalse() {
        for (String email : Constants.INVALID_EMAIL_ADDRESSES) {
            assertFalse(emailValidationService.validateEmailAddress(email, CASE_NUMBER));
        }
    }

}
