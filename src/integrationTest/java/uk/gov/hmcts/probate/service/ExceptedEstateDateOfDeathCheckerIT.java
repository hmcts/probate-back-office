package uk.gov.hmcts.probate.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ExceptedEstateDateOfDeathCheckerIT {

    private static final String PRE_EE_DECEASED_DATE_OF_DEATH = "01012021";
    private static final String POST_EE_DECEASED_DATE_OF_DEATH = "01012022";
    private static final  DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyy");

    @Autowired
    ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;

    @Test
    void shouldReturnFalse() {
        assertFalse(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate(PRE_EE_DECEASED_DATE_OF_DEATH));
    }

    @Test
    void shouldReturnFalseWhenNull() {
        assertFalse(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((String) null));
    }

    @Test
    void shouldReturnTrue() {
        assertTrue(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate(POST_EE_DECEASED_DATE_OF_DEATH));
    }

    @Test
    public void localDateShouldReturnFalse() {
        LocalDate preDate = LocalDate.parse(PRE_EE_DECEASED_DATE_OF_DEATH, DATE_TIME_FORMATTER);
        assertFalse(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate(preDate));

    }

    @Test
    public void localDateShouldReturnFalseWhenNull() {
        assertFalse(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) null));
    }

    @Test
    public void localDateShouldReturnTrue() {
        LocalDate postDate = LocalDate.parse(POST_EE_DECEASED_DATE_OF_DEATH, DATE_TIME_FORMATTER);
        assertTrue(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate(postDate));

    }
}
