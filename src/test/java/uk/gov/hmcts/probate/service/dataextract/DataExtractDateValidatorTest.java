package uk.gov.hmcts.probate.service.dataextract;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.exception.ClientException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataExtractDateValidatorTest {

    private DataExtractDateValidator dataExtractDateValidator;

    @BeforeEach
    public void setup() {
        dataExtractDateValidator = new DataExtractDateValidator();
    }

    @Test
    public void shouldValidateDate() {
        dataExtractDateValidator.dateValidator("2000-12-31");
    }

    @Test
    public void shouldValidateEmptyFromDate() {
        dataExtractDateValidator.dateValidator("", "2000-12-31");
    }

    @Test
    public void shouldValidatenullFromDate() {
        dataExtractDateValidator.dateValidator(null, "2000-12-31");
    }

    @Test
    public void shouldValidateDateFromTo() {
        dataExtractDateValidator.dateValidator("2000-12-31", "2001-12-31");
    }

    @Test
    public void shouldValidateDateFromToSame() {
        dataExtractDateValidator.dateValidator("2000-12-31", "2000-12-31");
    }

    @Test
    public void shouldThrowExceptionForInvalidaDate() {
        assertThrows(ClientException.class, () -> {
            dataExtractDateValidator.dateValidator("2000-14-31");
        });
    }

    @Test
    public void shouldThrowExceptionForInvalidFromDate() {
        assertThrows(ClientException.class, () -> {
            dataExtractDateValidator.dateValidator("2000--31", "2001-12-31");
        });
    }

    @Test
    public void shouldThrowExceptionForInvalidToDate() {
        assertThrows(ClientException.class, () -> {
            dataExtractDateValidator.dateValidator("2000-12-31", "2001");
        });
    }

    @Test
    public void shouldThrowExceptionForNullDate() {
        assertThrows(ClientException.class, () -> {
            dataExtractDateValidator.dateValidator(null);
        });
    }

    @Test
    public void shouldThrowExceptionForNullFromToDates() {
        assertThrows(ClientException.class, () -> {
            dataExtractDateValidator.dateValidator(null, null);
        });
    }

    @Test
    public void shouldThrowExceptionForEmptyFromToDates() {
        assertThrows(ClientException.class, () -> {
            dataExtractDateValidator.dateValidator("", "");
        });
    }

    @Test
    public void shouldThrowExceptionForFromDateNotBeforeToDate() {
        assertThrows(ClientException.class, () -> {
            dataExtractDateValidator.dateValidator("2001-12-31", "2001-01-31");
        });
    }
}
