package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.BusinessValidationError;
import uk.gov.hmcts.probate.model.CCDData;
import uk.gov.hmcts.probate.validator.ValidationRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(MockitoJUnitRunner.class)
public class BusinessValidationServiceTest {
    BusinessValidationService businessValidationService;

    @Mock
    private ValidationRule validationRule1Mock;

    @Mock
    private ValidationRule validationRule2Mock;

    @Mock
    private CCDData ccdDataMock;

    @Mock
    private BusinessValidationError businessValidationError1Mock;

    @Mock
    private BusinessValidationError businessValidationError2Mock;

    @Before
    public void setup() {
        List<ValidationRule> validationRules = Arrays.asList(validationRule1Mock, validationRule2Mock);
        businessValidationService = new BusinessValidationService(validationRules);
    }

    @Test
    public void shouldValidateFormWithNoErrors() {
        Mockito.when(validationRule1Mock.validate(ccdDataMock)).thenReturn(new ArrayList<>());
        Mockito.when(validationRule2Mock.validate(ccdDataMock)).thenReturn(new ArrayList<>());

        List<BusinessValidationError> errors = businessValidationService.validateForm(ccdDataMock);

        assertThat(errors.isEmpty(), is(true));
    }

    @Test
    public void shouldValidateFormWithSingleError() {
        Mockito.when(validationRule1Mock.validate(ccdDataMock)).thenReturn(Arrays.asList(businessValidationError1Mock));
        Mockito.when(validationRule2Mock.validate(ccdDataMock)).thenReturn(new ArrayList<>());

        List<BusinessValidationError> errors = businessValidationService.validateForm(ccdDataMock);

        assertThat(errors.isEmpty(), is(false));
        assertThat(errors.get(0), is(businessValidationError1Mock));
    }

    @Test
    public void shouldValidateFormWithMultipleErrors() {
        Mockito.when(validationRule1Mock.validate(ccdDataMock)).thenReturn(Arrays.asList(businessValidationError1Mock));
        Mockito.when(validationRule2Mock.validate(ccdDataMock)).thenReturn(Arrays.asList(businessValidationError2Mock));

        List<BusinessValidationError> errors = businessValidationService.validateForm(ccdDataMock);

        assertThat(errors.isEmpty(), is(false));
        assertThat(errors.get(0), is(businessValidationError1Mock));
        assertThat(errors.get(1), is(businessValidationError2Mock));
    }
}
