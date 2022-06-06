package uk.gov.hmcts.probate.service.dataextract;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.exception.ClientException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DataExtractDateValidatorTest {

    private DataExtractDateValidator dataExtractDateValidator;

    @BeforeEach
    public void setup() {
        dataExtractDateValidator = new DataExtractDateValidator();
    }

    @Test
    void shouldValidateDate() {
        assertNoThrow("2000-12-31");
    }

    @Test
    void shouldValidateEmptyFromDate() {
        assertNoThrow("", "2000-12-31");
    }

    @Test
    void shouldValidatenullFromDate() {
        assertNoThrow(null, "2000-12-31");
    }

    @Test
    void shouldValidateDateFromTo() {
        assertNoThrow("2000-12-31", "2001-12-31");
    }

    @Test
    void shouldValidateDateFromToSame() {
        assertNoThrow("2000-12-31", "2000-12-31");
    }

    @Test
    void shouldThrowExceptionForInvalidaDate() {
        assertThrows(ClientException.class, () -> {
            dataExtractDateValidator.dateValidator("2000-14-31");
        });
    }

    @Test
    void shouldThrowExceptionForInvalidFromDate() {
        assertThrows(ClientException.class, () -> {
            dataExtractDateValidator.dateValidator("2000--31", "2001-12-31");
        });
    }

    @Test
    void shouldThrowExceptionForInvalidToDate() {
        assertThrows(ClientException.class, () -> {
            dataExtractDateValidator.dateValidator("2000-12-31", "2001");
        });
    }

    @Test
    void shouldThrowExceptionForNullDate() {
        assertThrows(ClientException.class, () -> {
            dataExtractDateValidator.dateValidator(null);
        });
    }

    @Test
    void shouldThrowExceptionForNullFromToDates() {
        assertThrows(ClientException.class, () -> {
            dataExtractDateValidator.dateValidator(null, null);
        });
    }

    @Test
    void shouldThrowExceptionForEmptyFromToDates() {
        assertThrows(ClientException.class, () -> {
            dataExtractDateValidator.dateValidator("", "");
        });
    }

    @Test
    void shouldThrowExceptionForFromDateNotBeforeToDate() {
        assertThrows(ClientException.class, () -> {
            dataExtractDateValidator.dateValidator("2001-12-31", "2001-01-31");
        });
    }

    private void assertNoThrow(String date) {
        assertDoesNotThrow(() -> {
            dataExtractDateValidator.dateValidator(date);
        });
    }

    private void assertNoThrow(String date1, String date2) {
        assertDoesNotThrow(() -> {
            dataExtractDateValidator.dateValidator(date1, date2);
        });
    }
}
