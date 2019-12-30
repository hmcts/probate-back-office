package uk.gov.hmcts.probate.service.dataextract;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.config.DataExtractConfiguration;
import uk.gov.hmcts.probate.exception.DataExtractUnauthorisedException;

import static org.mockito.Mockito.when;

public class DataExtractScheduleValidatorTest {

    private DataExtractScheduleValidator dataExtractScheduleValidator;

    @Mock
    private DataExtractConfiguration dataExtractConfiguration;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        dataExtractScheduleValidator = new DataExtractScheduleValidator(dataExtractConfiguration);

        when(dataExtractConfiguration.getExela()).thenReturn("AAAAAAAAAA");
        when(dataExtractConfiguration.getIron()).thenReturn("BBBBBBBBBB");
        when(dataExtractConfiguration.getHmrc()).thenReturn("CCCCCCCCCC");
    }

    @Test
    public void shouldValidateHmrc() {
        dataExtractScheduleValidator.validateHmrc("CCCCCCCCCC");
    }

    @Test(expected = DataExtractUnauthorisedException.class)
    public void shouldThrowUnauthorisedExceptionForValidateHmrc() {
        dataExtractScheduleValidator.validateHmrc("CCCCCCCCCX");
    }

    @Test
    public void shouldValidateIronMoutain() {
        dataExtractScheduleValidator.validateIronMountain("BBBBBBBBBB");
    }

    @Test(expected = DataExtractUnauthorisedException.class)
    public void shouldThrowUnauthorisedExceptionForValidateIronMoutain() {
        dataExtractScheduleValidator.validateIronMountain("BBBBBBBBBX");
    }

    @Test
    public void shouldValidateExela() {
        dataExtractScheduleValidator.validateExela("AAAAAAAAAA");
    }

    @Test(expected = DataExtractUnauthorisedException.class)
    public void shouldThrowUnauthorisedExceptionForValidateExela() {
        dataExtractScheduleValidator.validateExela("AAAAAAAAAX");
    }

}