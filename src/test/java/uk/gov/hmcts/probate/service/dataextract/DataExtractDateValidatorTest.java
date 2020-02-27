package uk.gov.hmcts.probate.service.dataextract;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.probate.exception.ClientException;

public class DataExtractDateValidatorTest {

    private DataExtractDateValidator dataExtractDateValidator;

    @Before
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

    @Test(expected = ClientException.class)
    public void shouldThrowExceptionForInvalidaDate() {
        dataExtractDateValidator.dateValidator("2000-14-31");
    }

    @Test(expected = ClientException.class)
    public void shouldThrowExceptionForInvalidFromDate() {
        dataExtractDateValidator.dateValidator("2000--31", "2001-12-31");
    }

    @Test(expected = ClientException.class)
    public void shouldThrowExceptionForInvalidToDate() {
        dataExtractDateValidator.dateValidator("2000-12-31", "2001");
    }

    @Test(expected = ClientException.class)
    public void shouldThrowExceptionForNullDate() {
        dataExtractDateValidator.dateValidator(null);
    }

    @Test(expected = ClientException.class)
    public void shouldThrowExceptionForNullFromToDates() {
        dataExtractDateValidator.dateValidator(null, null);
    }

    @Test(expected = ClientException.class)
    public void shouldThrowExceptionForEmptyFromToDates() {
        dataExtractDateValidator.dateValidator("", "");
    }
}