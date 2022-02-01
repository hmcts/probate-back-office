package uk.gov.hmcts.probate.service.exceptionrecord.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EeDateOfDeathCheckerTest {

    private static final String PRE_EE_DECEASED_DATE_OF_DEATH = "01012021";
    private static final String POST_EE_DECEASED_DATE_OF_DEATH = "01012022";

    @Autowired
    EeDateOfDeathChecker eeDateOfDeathChecker;

    @Test
    public void shouldReturnFalse() {
        assertFalse(eeDateOfDeathChecker.isOnOrAfterSwitchDate(PRE_EE_DECEASED_DATE_OF_DEATH));
    }

    @Test
    public void shouldReturnTrue() {
        assertTrue(eeDateOfDeathChecker.isOnOrAfterSwitchDate(POST_EE_DECEASED_DATE_OF_DEATH));
    }
}
