package uk.gov.hmcts.probate.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.gov.hmcts.probate.model.BusinessValidationError;
import uk.gov.hmcts.probate.model.BusinessValidationResponse;
import uk.gov.hmcts.probate.model.BusinessValidationStatus;
import uk.gov.hmcts.probate.model.CCDData;
import uk.gov.hmcts.probate.service.BusinessValidationService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BusinessValidationControllerTest {

    private BusinessValidationController businessValidationController;
    @Mock
    private BusinessValidationService businessValidationServiceMock;
    @Mock
    private CCDData ccdDataMock;
    @Mock
    private BindingResult bindingResultMock;
    @Mock
    private BusinessValidationError businessValidationErrorMock;
    @Mock
    private FieldError fieldErrorMock;

    @Before
    public void setUp() {
        businessValidationController = new BusinessValidationController(businessValidationServiceMock);
    }

    @Test
    public void shouldValidateWithNoErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        when(businessValidationServiceMock.validateForm(ccdDataMock)).thenReturn(Collections.emptyList());

        BusinessValidationResponse response = businessValidationController.validate(ccdDataMock, bindingResultMock);

        assertThat(response.getErrors().isEmpty(), is(true));
        assertThat(response.getStatus(), is(BusinessValidationStatus.SUCCESS));
    }

    @Test
    public void shouldValidateWithFieldErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(true);
        when(bindingResultMock.getFieldErrors()).thenReturn(Arrays.asList(fieldErrorMock));

        BusinessValidationResponse response = businessValidationController.validate(ccdDataMock, bindingResultMock);

        assertThat(response.getErrors().isEmpty(), is(false));
        assertThat(response.getStatus(), is(BusinessValidationStatus.FAILURE));
    }

    @Test
    public void shouldValidateWithBusinessErrors() {
        when(bindingResultMock.hasErrors()).thenReturn(false);
        List<BusinessValidationError> businessErrors = Arrays.asList(businessValidationErrorMock);
        when(businessValidationServiceMock.validateForm(ccdDataMock)).thenReturn(businessErrors);

        BusinessValidationResponse response = businessValidationController.validate(ccdDataMock, bindingResultMock);

        assertThat(response.getErrors().isEmpty(), is(false));
        assertThat(response.getStatus(), is(BusinessValidationStatus.FAILURE));
    }
}
