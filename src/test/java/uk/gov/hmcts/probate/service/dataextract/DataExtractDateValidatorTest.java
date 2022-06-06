package uk.gov.hmcts.probate.service.dataextract;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.exception.ClientException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class DataExtractDateValidatorTest {

    private DataExtractDateValidator dataExtractDateValidator;

    @BeforeEach
    public void setup() {
        dataExtractDateValidator = new DataExtractDateValidator();
    }

    @Test
    void shouldValidateDate() {
        dataExtractDateValidator.dateValidator("2000-12-31");
    }

    @Test
    void shouldValidateEmptyFromDate() {
        dataExtractDateValidator.dateValidator("", "2000-12-31");
    }

    @Test
    void shouldValidatenullFromDate() {
        dataExtractDateValidator.dateValidator(null, "2000-12-31");
    }

    @Test
    void shouldValidateDateFromTo() {
        dataExtractDateValidator.dateValidator("2000-12-31", "2001-12-31");
    }

    @Test
    void shouldValidateDateFromToSame() {
        dataExtractDateValidator.dateValidator("2000-12-31", "2000-12-31");
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
}
